package completion.FromContentToRecord;

import com.hankcs.hanlp.seg.common.Term;
import completion.Tools.JdbcUtils;
import completion.Tools.SoftParameters;

import java.util.List;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/10/13 21:39
 **/
/*
如何评价一个myterm是所求结果的置信度？一个term对应一个评分，可用回归模型。
训练集：根据相似度为每个term打分。
如何定义相似度？编辑距离、语义距离、自定义距离（比如是层级关系？）
一个term表示为一个特征向量，包括文件名排序（根据搜索引擎，在前边的网页更相关），网页类型，文件总行数，文件有效行数，该文件一共有多少个单位类型的term，多少个专家名称的term
以及该term本身的信息，包括lineIndex，lineLen，sumLength，word，word的长度，是否包含地名等。假设离它最近的专家名称是E，还有它与E的距离(字符个数)，
与E之间的所有词的平均词向量。还有word是否包含地名，是否包含关键词（大学，学院等）。
由于word是一个机构名，也就是一个短语，如何通过词向量表示短语向量？这也是比较新的研究，值得说。最长前缀匹配，限制短语的关键词个数的最大值。
写代码时有两个选择，一是用学术的方法(KCNN等)，二是直接用词向量的均值，或者补零等特殊值处理（补零要考虑零值的语义）

10.14更新
与E的距离好像并不能作为一个特征，如果结果中有地名、“大学”等标志词，更符合结果。
先找到所有的专家名称的term


10.23更新(613维)
fileSort，pageType，fileLineCount，fileLegalLineCount，expertTypeTermCount，institutionTypeTermCount，ThisTerm.lineIndex，
ThisTerm.lineLen，ThisTerm.sumLength，ThisTerm.word.length，ThisTerm.word.containDiming，ThisTerm.word.containGuanjianci，distanceToE,
ThisTerm.word.vector，average(list(word.vector))

10.26更新
一个单ITerm和最近的专家ETerm可能很远，甚至没有找到ETerm，该如何处理？
如果距离超过了10个term或者ETerm不存在（大多数情况都如此），就将ITerm所在的这句话作为上下文。
好像要记录标点符号的位置。
如果这句话所在行比较短（从网页中得到的content，结构比较散乱），直接将这一行作为上下文；
如果比较长，就根据ITerm上下的标点符号截断，这句话作为上下文。
如果截断的话也很长，就将ITerm的上下5个term作为上下文。

将上下文处理为平均词向量。
比较复杂，不再序列化term信息，直接序列化向量。

10.31更新
HanLp对空格好像很敏感，考虑把空格替换为逗号。
考虑更换为深度学习的HanLP。
如何更好的抽取一个ITerm的上下文？？？

11.1 更新
实际上，一个专家的确可能有多个单位，或者曾经在一些单位任职过。如果抽取出这些单位，也不能算是完全错误。
那么，生成训练集，标注一个termFeature的分数时，不必只有一个正确答案（即表格中的那个单位），可以建立一个正确答案集合，不同的答案正确度不同。
在计算分数时，取相似度最高分即可。但是目前因为只有一个答案（除非后续人工添加答案集合），所以暂时只考虑一个。

11.5 更新
无法将向量写入excel，因为在获取workbook时，需要先读取excel中已有的数据，造成OOM in heap space
还是将向量写入mysql
 */
public class TermFeature {
    private final int fileSort;
    private final int pageType;
    private final int fileLineCount;
    private final int fileLegalLineCount;
    private final int sumLegalLineLength;
    private final int expertTypeTermCount;
    private final int institutionTypeTermCount;
    private MyTerm institutionMyTerm;
    private List<Term> contextTermList;

    public TermFeature(int fileSort, int pageType, int fileLineCount, int fileLegalLineCount, int sumLegalLineLength,
                       int expertTypeTermCount, int institutionTypeTermCount,
                       MyTerm institutionMyTerm, List<Term> contextTermList) {
        this.fileSort = fileSort;
        this.pageType = pageType;
        this.fileLineCount = fileLineCount;
        this.fileLegalLineCount = fileLegalLineCount;
        this.sumLegalLineLength = sumLegalLineLength;
        this.expertTypeTermCount = expertTypeTermCount;
        this.institutionTypeTermCount = institutionTypeTermCount;
        this.institutionMyTerm = institutionMyTerm;
        this.contextTermList = contextTermList;
    }

    //获取特征向量，保存为一个int[]
    public float[] getFeatureVector(){
        /*
        fileSort,pageType,fileLineCount,fileLegalLineCount,sumLegalLineLength，expertTypeTermCount,institutionTypeTermCount,
        lineIndex，lineLen，sumLength，word，len(word)，是否包含地名,与E的距离，该组织名称的词向量。与E之间的所有词的平均词向量，
        一共16个部分，后两个部分是词向量，
         */
        float[] feature = new float[SoftParameters.TermVectorLength];
        fillFileAttr(feature);
        fillTermAttr(feature);
        fillVectorAttr(feature);
        return feature;
    }

    //填充content的信息
    private void fillFileAttr(float[] feature){
        feature[SoftParameters.featureFileSortIndex] = fileSort;
        feature[SoftParameters.featurePageTypeIndex] = pageType;
        feature[SoftParameters.featureFileLineCountIndex] = fileLineCount;
        feature[SoftParameters.featureFileLegalLineCountIndex] = fileLegalLineCount;
        feature[SoftParameters.featureFileSumLegalLineLengthIndex] = sumLegalLineLength;
        feature[SoftParameters.featureExpertTypeTermCountIndex] = expertTypeTermCount;
        feature[SoftParameters.featureInstitutionTypeTermCountIndex] = institutionTypeTermCount;
    }

    //填充Term的信息
    private void fillTermAttr(float[] feature){
        feature[SoftParameters.featureTermLineIndexIndex] = institutionMyTerm.getLineIndex();
        feature[SoftParameters.featureTermLineLengthIndex] = institutionMyTerm.getLineLen();
        feature[SoftParameters.featureTermSumLengthIndex] = institutionMyTerm.getSumLength();

        Term term = institutionMyTerm.getTerm();
        feature[SoftParameters.featureTermWordLengthIndex] = term.word.length();
        feature[SoftParameters.featureTermContainPlaceIndex] = 0;     //暂时先置零
        feature[SoftParameters.featureTermContainKeywordIndex] = 0;     //暂时先置零
        feature[SoftParameters.featureDisToExpertIndex] = institutionMyTerm.getClosestDisToExpert();
    }

    //填充词向量和上下文向量
    private void fillVectorAttr(float[] feature){
        //填充word向量
        float[] wordVector = JdbcUtils.getWordVector(institutionMyTerm.getTerm().word);
        int wordBeginIndex = SoftParameters.WordVectorBeginIndex;
        for(int i=wordBeginIndex; i<wordBeginIndex+SoftParameters.WordVectorLength; i++)
            feature[i] = wordVector[i-wordBeginIndex];

        //填充上下文向量
        int size = contextTermList.size();
        float[] tempVector = new float[SoftParameters.WordVectorLength];
        for(int i=0; i<size; i++){
            float[] nowWordVector = JdbcUtils.getWordVector(contextTermList.get(i).word);
            for(int j=0; j<SoftParameters.WordVectorLength; j++){
                tempVector[j] += nowWordVector[j];
            }
        }
        for(int j=0; j<SoftParameters.WordVectorLength; j++){
            tempVector[j] = tempVector[j] / size;
        }

        int contextBeginIndex = SoftParameters.ContextVectorBeginIndex;
        for(int i=contextBeginIndex; i<contextBeginIndex + SoftParameters.WordVectorLength; i++)
            feature[i] = tempVector[i-contextBeginIndex];
    }
}
