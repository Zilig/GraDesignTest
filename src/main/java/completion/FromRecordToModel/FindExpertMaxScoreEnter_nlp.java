package completion.FromRecordToModel;

import completion.FromContentToRecord.EntityTermInfor;
import completion.FromContentToRecord.FileTermInfor;
import completion.FromContentToRecord.MyTerm;
import completion.Tools.FileTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/10/8 20:57
 **/
public class FindExpertMaxScoreEnter_nlp {
    public void testEntityTerm(){

    }

    //计算一个MyTerm是所求结果的置信度，返回float（0,1）
    public float computeTermScore(MyTerm myTerm){
        return 0.0f;
    }


    //找出该txtFile中与正确结果最接近的5个单位
    public static ArrayList<String> findHighScoreEnterInTxt(ArrayList<MyTerm> termInforList, String realEnterName){
        ArrayList<String> resultList = new ArrayList<>();
        for(MyTerm myTerm : termInforList){
            String enterName = myTerm.getTerm().word;
        }
        return resultList;
    }


    //计算一个datFile中所有MyTerm的置信度，返回最高的几个
    public static List<String> run(File datFile, String realEnterName){
        String datFileName = datFile.getName();
        String expertName = datFileName.split(" ")[1].split("\\.")[0];

        EntityTermInfor entityTermInfor = (EntityTermInfor) FileTools.readObjFromDat(datFile);
        System.out.println(entityTermInfor.toString());
        ArrayList<FileTermInfor> fileTermInforList = entityTermInfor.getFileTermInforList();
        for(FileTermInfor termInfor : fileTermInforList){

        }
        return null;
    }
}
