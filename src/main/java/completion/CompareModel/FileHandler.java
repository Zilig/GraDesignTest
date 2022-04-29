package completion.CompareModel;

import completion.Tools.FileTools;
import completion.Tools.NumberTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2022/4/25 20:33
 * @Description TODO
 **/
public class FileHandler {
    private final File file;
    private String fileName;
    private final String entityCode;
    private final String headName;
    private final String tailName;

    public FileHandler(File file, String entityCode, String headName, String tailName) {
        this.file = file;
        this.fileName = file.getName();
        this.entityCode = entityCode;
        this.headName = headName;
        this.tailName = tailName;
    }

    //在一个句子中找到两个实体的起始位置
    private int[] findPos(String sentence){
        List<Integer> headIndexList = NumberTools.findWordIndexInLine(sentence, headName);
        List<Integer> tailIndexList = NumberTools.findWordIndexInLine(sentence, tailName);
        int headListCount = headIndexList.size();
        int tailListCount = tailIndexList.size();
        if(headListCount == 0 || tailListCount == 0) return null;
        return new int[]{headIndexList.get(0), tailIndexList.get(0)};
    }

    private String printInformation(String sentence){
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(entityCode).append("],");
        sb.append("[").append(fileName).append("],");
        sb.append("[").append(headName).append("],");
        sb.append("[").append(tailName).append("],");
        if(sentence.length() <= 50){
            sb.append("[").append(sentence).append("],");
        }
        else{
            sb.append("[").append(sentence.substring(0,50)).append("],");
        }
        return sb.toString();
    }

    private void appendWrite(String str, String filename){
        try{
            File file = new File(filename);
            BufferedWriter bw = new BufferedWriter((new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8)));
            bw.write(str);
            bw.close();
        }
        catch(Exception exception){
            System.out.println("failed to write this file:" + filename);
            System.out.println("exception = " + exception);
        }
    }

    public int handle(){
        int recordCount = 0;
        StringBuilder resultSb = new StringBuilder();
        List<String> lineList = FileTools.readFileToList(file, "utf-8");
        int fileLineCount = lineList.size();
        for (int lineIndex=0; lineIndex<fileLineCount; lineIndex++){
            List<String> sentenceList = FileTools.splitLineToSentence(lineList.get(lineIndex));
            int senCount = sentenceList.size();
            for(int j=0; j<senCount; j++){
                Object posObj = findPos(sentenceList.get(j));
                if(Objects.nonNull(posObj)){
                    recordCount++;
                    String record = printInformation(sentenceList.get(j));
                    //System.out.println(record);
                    //System.out.println(entityCode + ", " + fileName);
                    resultSb.append(record).append("\n");
                }
            }
        }

        String resultFilePath = "D:\\Java\\Workspace\\GraduateDesign\\FrontHalf\\src\\main\\resources\\CompareModel\\record.txt";
        appendWrite(resultSb.toString(), resultFilePath);
        return recordCount;
    }
}
