package completion.CompareModel;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import completion.Tools.FileTools;
import completion.Tools.NumberTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2022/4/26 15:10
 * @Description TODO
 **/
public class RecordHandler {
    private final Segment segment = HanLP.newSegment("perceptron")
            .enableNameRecognize(true)
            .enableAllNamedEntityRecognize(true)
            .enableOffset(true)
            .enableOrganizationRecognize(true);

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




    private String handleLine(String line){
        String[] attrLine = line.split("],");
        String dictCode = attrLine[0].substring(1);
        String fileName = attrLine[1].substring(1);
        String headName = attrLine[2].substring(1);
        String tailName = attrLine[3].substring(1);
        String sentence = attrLine[4].substring(1);

        //确定包含头实体，再进行处理
        List<Integer> headIndexList = NumberTools.findWordIndexInLine(sentence, headName);
        if(headIndexList.size() != 0){
            List<Term> termList = segment.seg(sentence);
            int[] posArr = findPos(headName, tailName, termList);
            if(posArr[0] != -1 && posArr[1] != -1){
                System.out.println(dictCode + ", " + fileName);

                StringBuilder sb = new StringBuilder();
                sb.append("[").append(dictCode).append("],");
                sb.append("[").append(fileName).append("],");
                sb.append("[").append(headName).append("],");
                sb.append("[").append(posArr[0]).append("],");
                sb.append("[").append(tailName).append("],");
                sb.append("[").append(posArr[1]).append("],");
                if(sentence.length() <= 50){
                    sb.append("[").append(sentence).append("],");
                }
                else{
                    sb.append("[").append(sentence.substring(0,50)).append("],");
                }
                return sb.toString();
            }
        }

        return "";
    }

    private int[] findPos(String headName, String tailName, List<Term> termList){
        int[] posArr = {-1, -1};
        boolean[] flagArr = {false, false};

        int bias = 0;
        int termCount = termList.size();
        for(int i=0; i<termCount; i++){
            if(flagArr[0] && flagArr[1]) break;
            Term nowTerm = termList.get(i);
            String nowWord = nowTerm.word;
            if(!flagArr[0] && nowWord.equals(headName)){
                posArr[0] = bias;
                flagArr[0] = true;
            }
            if(!flagArr[1] && NumberTools.computeSimilarity(nowWord, tailName) >= 0.75){
                posArr[1] = bias;
                flagArr[1] = true;
            }
            bias += nowWord.length();
        }

        return posArr;
    }

    public void run(){
        StringBuilder resultSb = new StringBuilder();
        String filename = "D:\\Java\\Workspace\\GraduateDesign\\FrontHalf\\src\\main\\resources\\CompareModel\\record0.txt";
        List<String> lineList = FileTools.readFileToList(new File(filename), "utf-8");
        int lineCount = lineList.size();
        for(int i=0; i<lineCount; i++){
            String recordLine = handleLine(lineList.get(i));
            if(recordLine.length() != 0){
                resultSb.append(recordLine).append("\n");
            }
        }

        String resultFilePath = "D:\\Java\\Workspace\\GraduateDesign\\FrontHalf\\src\\main\\resources\\CompareModel\\record1.txt";
        appendWrite(resultSb.toString(), resultFilePath);
    }

}
