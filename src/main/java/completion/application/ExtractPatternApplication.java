package completion.application;

import completion.FromContentToRecord.ReadSerialEntityTermInfo;
import completion.FromContentToRecord.SerialEntityTermInfor;
import completion.FromContentToRecord.WriteRecordToMysql;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/10/26 21:43
 **/
public class ExtractPatternApplication {
    public static void main(String[] args) {
        //runSerialEntityTermInfo();
        //runReadSerialEntityTermInfo();
        runWriteRecordToMysql();
    }

    private static void runSerialEntityTermInfo(){
        SerialEntityTermInfor serialEntityTermInfo = new SerialEntityTermInfor();
        int type = 1;
        int beginIndex = 625;
        int endIndex = 625;
        serialEntityTermInfo.run(type, beginIndex, endIndex);    //1表示训练集
    }

    private static void runReadSerialEntityTermInfo(){
        ReadSerialEntityTermInfo readSerialEntityTermInfo = new ReadSerialEntityTermInfo();
        int beginIndex = 0;
        int endIndex = 2;
        readSerialEntityTermInfo.run(beginIndex, endIndex);
    }


    private static void runWriteRecordToMysql(){
        int beginIndex = 200;
        int endIndex = 250;
        for(int i=beginIndex; i<endIndex; i++){
            WriteRecordToMysql writer = new WriteRecordToMysql();
            writer.run(i, i+1);
            System.out.println("i = " + i);
        }
    }

}
