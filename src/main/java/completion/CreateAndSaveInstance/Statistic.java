package completion.CreateAndSaveInstance;

import completion.Tools.ParamSetting;
import completion.Tools.SoftParameters;

import java.io.File;
import java.util.Objects;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2022/3/22 21:27
 * @Description TODO
 **/
public class Statistic {

    private static final ParamSetting paramSetting = SoftParameters.paramSetting;

    public static void main(String[] args) {
        ParamSetting paramSetting = SoftParameters.paramSetting;
        //存储content的目录的文件夹，每个子目录依然是一个文件夹，对应一个entity，保存50content。
        String contentDirPath = paramSetting.getContentPath();
        File dicDir = new File(contentDirPath);
        File[] contentDirs = dicDir.listFiles();//注意:这里只能用listFiles()，不能使用list()

        int all_count = 0, organiseCount = 0, positiveCount = 0, negativeCount = 0;
        int file_count = 0;
        //int beginIndex = 0, endIndex = Objects.requireNonNull(contentDirs).length;
        int beginIndex = 0, endIndex = 500;
        for(int i=beginIndex; i<endIndex; i++){
            assert contentDirs != null;
            File entityDic = contentDirs[i];
            String entityDicName = entityDic.getName();
            String[] inforArr = entityDicName.split(" ");
            String entityCode = inforArr[0];
            String expertName = inforArr[1];
            String institutionName = inforArr[2];
            File[] contentFiles = entityDic.listFiles();
            for(int j = 0; j< Objects.requireNonNull(contentFiles).length; j++){
                file_count++;
                File contentFile = contentFiles[j];
                FileInfor fileInfor = new FileInfor(contentFile, entityCode, contentFile.getName(), expertName, institutionName, j);
                int[] nowCount = fileInfor.statistic();
                all_count += nowCount[0];
                organiseCount += nowCount[1];
                positiveCount += nowCount[2];
                negativeCount += nowCount[3];

                if(file_count % 1000 == 0){
                    System.out.println("*******************************************");
                    System.out.println("file_count=" + file_count + ", all_count=" + all_count + ", organiseCount=" + organiseCount
                            + "positiveCount=" + positiveCount + "negativeCount=" + negativeCount);
                    System.out.println("*******************************************");
                }
                //List<Record> recordList = fileInfor.getRecords();
                //System.out.println("文件夹：" + entityDicName + ", 文件名：" + j + ", 记录个数：" + recordList.size());
                //allRecordList.addAll(recordList);
            }
            System.out.println(entityCode + ", all_count=" + all_count + ", organiseCount=" + organiseCount
                                    + ", positiveCount=" + positiveCount + ", negativeCount=" + negativeCount);
        }

        System.out.println("*******************************************");
        System.out.println("file_count=" + file_count + ", all_count=" + all_count + ", organiseCount=" + organiseCount
                + "positiveCount=" + positiveCount + "negativeCount=" + negativeCount);
        System.out.println("*******************************************");
    }



}
