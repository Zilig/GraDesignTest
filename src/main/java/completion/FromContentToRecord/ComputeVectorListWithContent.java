package completion.FromContentToRecord;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import completion.Tools.FileTools;
import completion.Tools.NumberTools;
import completion.Tools.SoftParameters;
import completion.Tools.SuperParameters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/10/28 19:59
 **/

/*
fileSort，pageType，fileLineCount，fileLegalLineCount，expertTypeTermCount，institutionTypeTermCount，ThisTerm.lineIndex，
ThisTerm.lineLen，ThisTerm.sumLength，ThisTerm.word.length，ThisTerm.word.containDiming，ThisTerm.word.containGuanjianci，distanceToE,
ThisTerm.word.vector，average(list(word.vector))
 */

public class ComputeVectorListWithContent {
    private final File file;
    private int fileSort = 0;
    private int pageType = 0;
    private final String expertName;
    private final String institutionName;

    private int fileLineCount = 0;
    private int fileLegalLineCount = 0;
    private int sumLegalLineLength = 0;
    private int expertTypeTermCount = 0;
    private int institutionTypeTermCount = 0;

    private List<MyTerm> institutionTermList;
    private List<List<Term>> institutionTermContextList;
    private List<Float> institutionScoreList;

    private final Segment segment;

    public ComputeVectorListWithContent(File file, String expertName, String institutionName){
        this.file = file;
        this.fileSort = Integer.parseInt(file.getName().split("\\.")[0]);
        this.expertName = expertName;
        this.institutionName = institutionName;

        //分词器，可以命名实体识别，也可以人名识别，并携带了term的在文本中的位置信息。
        segment = HanLP.newSegment("perceptron")
                .enableNameRecognize(true)
                .enableAllNamedEntityRecognize(true)
                .enableOffset(true)
                .enableOrganizationRecognize(true);

        institutionTermList = new ArrayList<>(64);
        institutionTermContextList = new ArrayList<>(64);
        institutionScoreList = new ArrayList<>(64);
    }

    //核心计算函数。通过该函数抽取term和term的上下文，term的标签分值。
    private void compute(){
        List<String> lineList = FileTools.readFileToList(file, "utf-8");
        fileLineCount = lineList.size();
        for (int lineIndex=0; lineIndex<fileLineCount; lineIndex++)
        {
            String line = lineList.get(lineIndex);
            int lineLen = line.length();
            if(lineLen < SoftParameters.MinLegalContentLineLen) continue;
            fileLegalLineCount++;
            sumLegalLineLength += lineLen;

            List<Term> nowLineTerms = segment.seg(line);    //这个分词器保留标点了吗？非常有必要保留，标点可能含有很多模式的信息
            int nowTermDigitBeginIndex = 0;     //term在该行中的起始位置
            for(int i=0; i<nowLineTerms.size(); i++){
                Term nowTerm = nowLineTerms.get(i);
                Nature nowNature = nowTerm.nature;
                String nowWord = nowTerm.word;
                //机构相关，地名，机构团体名
                if(nowNature.startsWith("ni") || nowNature.startsWith("ns") || nowNature.startsWith("nt")){
                    /**
                     * 找到这一行中离这个ITerm最近的专家名称。
                     * 为什么还要在文本中搜索，而不是在termList中搜索？因为content不规整，可能有专家名称存在，却没有被识别为人名。
                     * 10.31更新：Term自带offset字段。
                     * hasCheckExpert:为了避免重复计算content中expertName的数量而设置的标志
                     */

                    int nowTermDigitEndIndex = nowTermDigitBeginIndex + nowWord.length() - 1;
                    int[] closestExpertIndexes = findClosestExpertName(line, nowTermDigitBeginIndex, nowTermDigitEndIndex);

                    //根据计算到的信息，收集ITerm的上下文term，保存在list中
                    List<Term> contextTermList = new ArrayList<>(8);
                    //这一行没有expert
                    if(closestExpertIndexes[0] == 0){
                        //左边3个term，如果碰到分隔符，直接终止。
                        for(int j=i-1; j>=0 && j>=i-SuperParameters.ContextLeftTermCountWithoutExpert; j--){
                            Term tempTerm = nowLineTerms.get(i);
                            //如果碰到了是分隔符的标点符号，就终止
                            if(tempTerm.nature.startsWith("w") && SoftParameters.separatorSet.contains(tempTerm.word)){
                                break;
                            }
                            contextTermList.add(tempTerm);
                        }
                        //右边3个term，如果碰到分隔符，直接终止。
                        for(int j=i+1; j<=i+SuperParameters.ContextRightTermCountWithoutExpert && j<=nowLineTerms.size(); j++){
                            Term tempTerm = nowLineTerms.get(i);
                            //如果碰到了是分隔符的标点符号，就终止
                            if(tempTerm.nature.startsWith("w") && SoftParameters.separatorSet.contains(tempTerm.word)){
                                break;
                            }
                            contextTermList.add(tempTerm);
                        }
                    }
                    else if(closestExpertIndexes[0] == -1){
                        //在左边有expert
                        int tempTermIndex = i - 1;
                        int addCount = 0;
                        while (addCount <= SuperParameters.ContextLeftTermCountWithExpert && nowLineTerms.get(tempTermIndex).offset > closestExpertIndexes[2] ){
                            contextTermList.add(nowLineTerms.get(tempTermIndex));
                            tempTermIndex--;
                            addCount++;
                        }
                    }
                    else{
                        //在右边有expert
                        int tempTermIndex = i + 1;
                        int addCount = 0;
                        Term tempTerm = nowLineTerms.get(tempTermIndex);
                        while (addCount <= SuperParameters.ContextRightTermCountWithExpert && tempTerm.offset + tempTerm.word.length() < closestExpertIndexes[1]){
                            contextTermList.add(tempTerm);
                            tempTermIndex++;
                            tempTerm = nowLineTerms.get(tempTermIndex);
                            addCount++;
                        }
                    }

                    MyTerm myTerm = new MyTerm(lineIndex, lineLen, nowTermDigitBeginIndex, 2, nowTerm, closestExpertIndexes[3]);
                    institutionTermList.add(myTerm);
                    institutionTermContextList.add(contextTermList);
                    institutionScoreList.add(computeLabelScore(nowWord));

                    if(i != 0){
                        expertTypeTermCount += closestExpertIndexes[4];
                    }
                    institutionTypeTermCount++;
                }

                nowTermDigitBeginIndex += nowWord.length();
            }
        }
    }

    //找到一个word在line出现的所有位置
    private List<Integer> findWordIndexInLine(String line, String word){
        List<Integer> resultList = new ArrayList<>();
        int pos = line.indexOf(word);
        while(pos>-1) {
            resultList.add(pos);
            pos = line.indexOf(word,pos+1);
        }
        return resultList;
    }

    //找到一行中离ITerm最近的专家名称在ITerm的前边还是后边，以及开始位置和结束位置，字符距离。并修改expertName的数值
    private int[] findClosestExpertName(String line, int termBegin, int termEnd){
        //result[0]表示在前边还是后边，-1表示前边，1表示后边，0表示不存在。result[3]表示最近的专家名称与ITerm相隔多少字符
        int[] result = new int[5];
        List<Integer> frontExpertIndexList = findWordIndexInLine(line.substring(0, termBegin), expertName);
        List<Integer> backendExpertIndexList = findWordIndexInLine(line.substring(termEnd), expertName);
        result[4] = frontExpertIndexList.size() + backendExpertIndexList.size();

        //这一行的左边，离ITerm最近的专家名称的末尾位置
        int leftIndex = -1;
        if(frontExpertIndexList.size() > 0){
            leftIndex = frontExpertIndexList.get(frontExpertIndexList.size()-1) + expertName.length() - 1;
        }

        //这一行的右边，离ITerm最近的专家名称的开头位置
        int rightIndex = -1;
        if(backendExpertIndexList.size() > 0){
            rightIndex = backendExpertIndexList.get(0);
        }

        //这一行的左边，离ITerm最近的专家名称与ITerm间相隔几个字符
        int leftDis = Integer.MAX_VALUE;
        if(leftIndex != -1){
            leftDis = termBegin - leftIndex + 1;
        }

        //这一行的右边，离ITerm最近的专家名称与ITerm间相隔几个字符
        int rightDis = Integer.MAX_VALUE;
        if(rightIndex != -1){
            rightDis = rightIndex;
        }

        int minDis = Math.min(leftDis, rightDis);
        //不存在专家名称
        if(minDis == Integer.MAX_VALUE){
            result[0] = 0;
        }
        else{
            //左边的专家名称更近
            if(leftDis == minDis){
                result[0] = -1;
                result[1] = leftIndex - expertName.length() + 1;
                result[2] = leftIndex;
                result[3] = leftDis;
            }
            else{
                result[0] = 1;
                result[1] = termEnd + rightIndex + 1;
                result[2] = result[1] + expertName.length() - 1;
                result[3] = rightDis;
            }
        }

        return result;
    }


    /**评分计算函数，计算一个word的标签分值，根据自定义的相似度。
     * 1. 是否完全相同
     * 2. 是否有标志词（大学、学院、研究中心、省、市、乡、等），有的话加0.5分。可能是干扰项。
     * 3. 基于同义词词库进行距离调整
     * 4. 还是要建立层级体系？
     * 5. 太麻烦，暂时根据编辑距离，归一化到0-1之间。
     * @param word : 一个term的word
     * @return  这个word与正确答案的接近程度。（0-1）
     */
    private float computeLabelScore(String word){
        if(word.equals(institutionName)){
            return 1.0f;
        }

        int editDis = NumberTools.computeEditDistance(word, institutionName);
        int maxLen = Math.max(word.length(), institutionName.length());
        return 1 - (float)editDis / (float)maxLen;
    }

    public FileTermInfor getFileTermInfo(){
        compute();
        return new FileTermInfor(file.getName(), fileSort, fileLineCount, fileLegalLineCount, sumLegalLineLength,
                expertTypeTermCount, institutionTypeTermCount, institutionTermList, institutionScoreList, institutionTermContextList);
    }
}
