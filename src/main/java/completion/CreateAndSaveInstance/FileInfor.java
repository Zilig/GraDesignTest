package completion.CreateAndSaveInstance;

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
 * @date 2022/3/22 21:34
 * @Description TODO
 **/
public class FileInfor {
    private final File file;
    private final String entityCode;
    private final String fileName;
    private final String expertName;
    private final String institutionName;
    private final int sort;       //网页的排名[1,50]

    private final int fileLineCount;  //文件总行数
    private int fileLegalLineCount; //文件有效行数
    private int fileDigitCount; //文件有效字符数
    private int expertTypeTermCount;    //该文件中有多个少专家term（头实体）
    private int institutionTypeTermCount;   //该文件中有多个少单位term
    private final int dispersion = 0; //文档行离散度，目前设置为0

    private final List<Record> recordList = new ArrayList<>(64);    //文档中所有标注实体的记录
    private final List<List<Term>> allTermList = new ArrayList<>(128);  //文档的分词结果

    private final Segment segment = HanLP.newSegment("perceptron")
            .enableNameRecognize(true)
            .enableAllNamedEntityRecognize(true)
            .enableOffset(true)
            .enableOrganizationRecognize(true);


    public FileInfor(File file, String entityCode, String fileName, String expertName, String institutionName, int sort){
        this.file = file;
        this.entityCode = entityCode;
        this.fileName = fileName;
        this.expertName = expertName;
        this.institutionName = institutionName;
        this.sort = sort;

        //先遍历一次文档，得到一些全局参数，并保留所有的分词结果，避免后续多次分词占用资源。
        List<String> lineList = FileTools.readFileToList(this.file, "utf-8");
        this.fileLineCount = lineList.size();

        for (int lineIndex=0; lineIndex<fileLineCount; lineIndex++) {
            String line = lineList.get(lineIndex);
            int lineLen = line.length();

            fileDigitCount += lineLen;
            if (lineLen >= SoftParameters.MinLegalContentLineLen) fileLegalLineCount++;;

            List<Term> nowLineTerms = segment.seg(line);    //这个分词器保留标点了吗？非常有必要保留，标点可能含有很多模式的信息
            allTermList.add(nowLineTerms);
        }
    }

    //核心计算函数。
    private void compute(){
        List<String> lineList = FileTools.readFileToList(file, "utf-8");
        int nowDigitCount = 0;  //从文件开头到现在的字符个数
        for (int lineIndex=0; lineIndex<fileLineCount; lineIndex++)
        {
            String line = lineList.get(lineIndex);
            int lineLen = line.length();
            if(lineLen < SoftParameters.MinLegalContentLineLen){
                nowDigitCount += lineLen;  //从文件开头到现在的字符个数
                continue;
            }

            expertTypeTermCount += NumberTools.findWordIndexInLine(line, expertName).size();

            List<Term> nowLineTerms = allTermList.get(lineIndex);    //这个分词器保留标点了吗？非常有必要保留，标点可能含有很多模式的信息
            int nowTermDigitBeginIndex = 0;     //term在该行中的起始位置
            for(int i=0; i<nowLineTerms.size(); i++){
                Term nowTerm = nowLineTerms.get(i);
                Nature nowNature = nowTerm.nature;
                String nowWord = nowTerm.word;

                //机构相关，地名，机构团体名
                if(nowNature.startsWith("ni") || nowNature.startsWith("ns") || nowNature.startsWith("nt")){
                    institutionTypeTermCount++;
                    /**
                     * 找到这一行中离这个ITerm最近的专家名称。
                     * 为什么还要在文本中搜索，而不是在termList中搜索？因为content不规整，可能有专家名称存在，却没有被识别为人名。
                     * 10.31更新：Term自带offset字段。
                     * hasCheckExpert:为了避免重复计算content中expertName的数量而设置的标志
                     */
                    //上三行
                    List<String> aboveContextList = new ArrayList<>();
                    for(int j=SuperParameters.ContextAboveLineCount; j>=1; j--){
                        aboveContextList.add(lineIndex < j ? "" : lineList.get(lineIndex-j));
                    }
                    //下三行
                    List<String> belowContextList = new ArrayList<>();
                    for(int j=1; j<=SuperParameters.ContextBelowLineCount; j++){
                        belowContextList.add(lineIndex + j >= fileLineCount ? "" : lineList.get(lineIndex+j));
                    }

                    //将实体信息和上下文传入，构造一个实例
                    Instance instance = new Instance(nowWord, nowDigitCount, line,
                            lineIndex, nowTermDigitBeginIndex, aboveContextList, belowContextList, allTermList);
                    //生成一个半成品record
                    Record record = instance.createSemiRecord(expertName, institutionName);
                    record.setEntityName(nowWord);
                    recordList.add(record);
                }
                nowTermDigitBeginIndex += nowWord.length(); //从本行开始到现在的字符个数
                nowDigitCount += nowWord.length();  //从文件开头到现在的字符个数
            }
        }
    }

    public List<Record> getRecords(){
        compute();

        //设置所有的全局特征和指示信息
        for(Record record : recordList){
            //设置一些指示信息
            record.setDictCode(entityCode);
            record.setFileName(fileName);
            record.setExpertName(expertName);
            record.setInstitutionName(institutionName);

            //设置全局特征
            record.setSort(sort);
            record.setFileLineCount(fileLineCount);
            record.setFileLegalLineCount(fileLegalLineCount);
            record.setFileDigitCount(fileDigitCount);
            record.setExpertTypeTermCount(expertTypeTermCount);
            record.setInstitutionTypeTermCount(institutionTypeTermCount);
            record.setDispersion(dispersion);
        }

        return recordList;
    }

    public int[] statistic(){
        int[] result = new int[4];
        int allWordCount = 0;
        int nameEntityCount = 0;
        int positiveCount = 0;
        int negativeCount = 0;
        for(List<Term> lineWord : allTermList){
            for(Term term : lineWord){
                allWordCount++;
                String entityName = term.word;
                Nature nowNature = term.nature;

                if(nowNature.startsWith("n")){
                    nameEntityCount++;
                    if(nowNature.startsWith("ni") || nowNature.startsWith("ns") || nowNature.startsWith("nt")){
                        if(entityName.equals(institutionName)){
                            positiveCount++;
                            continue;
                        }
                        int editDis = NumberTools.computeEditDistance(entityName, institutionName);
                        int maxLen = Math.max(entityName.length(), institutionName.length());
                        float label =  1 - (float)editDis / (float)maxLen;
                        if (label >= 0.75) positiveCount++;
                        else negativeCount++;
                    }
                }
            }
        }

        result[0] = allWordCount;
        result[1] = nameEntityCount;
        result[2] = positiveCount;
        result[3] = negativeCount;
        return result;
    }
}
