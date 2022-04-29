package completion.CompareModel;

import completion.Tools.ParamSetting;
import completion.Tools.SoftParameters;

import java.io.File;
import java.util.Objects;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2022/4/25 20:16
 * @Description TODO
 **/
public class Main {
    private static final ParamSetting paramSetting = SoftParameters.paramSetting;

    public static void main(String[] args) {
        ParamSetting paramSetting = SoftParameters.paramSetting;
        //存储content的目录的文件夹，每个子目录依然是一个文件夹，对应一个entity，保存50content。
        String contentDirPath = paramSetting.getContentPath();
        File dicDir = new File(contentDirPath);
        File[] contentDirs = dicDir.listFiles();//注意:这里只能用listFiles()，不能使用list()

        int fileCount = 0;
        int positiveCount = 0;
        //int beginIndex = 0, endIndex = Objects.requireNonNull(contentDirs).length;
        int beginIndex = 0, endIndex = 1000;
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
                File contentFile = contentFiles[j];
                FileHandler handler = new FileHandler(contentFile, entityCode, expertName, institutionName);
                positiveCount += handler.handle();
                fileCount++;

                if(fileCount % 1000 == 0){
                    System.out.println("*******************************************");
                    System.out.println("fileCount=" + fileCount + ", positiveCount=" + positiveCount );
                    System.out.println("*******************************************");
                }
            }
            System.out.println(entityCode + ", fileCount=" + fileCount + ", positiveCount=" + positiveCount );
        }
        System.out.println("fileCount=" + fileCount + ", positiveCount=" + positiveCount );
//        RecordHandler recordHandler = new RecordHandler();
//        recordHandler.run();
    }
}
