package completion.FromContentToRecord;

import completion.Tools.FileTools;
import completion.Tools.ParamSetting;
import completion.Tools.SoftParameters;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/10/25 21:38
 **/
public class SerialEntityTermInfor {
    private final ParamSetting paramSetting;
    private int type = 0;   //entity类型，训练集=1，测试集=2

    public SerialEntityTermInfor(){
        paramSetting = SoftParameters.paramSetting;
    }

    public void run(int type, int beginIndex, int endIndex){
        this.type = type;
        //存储content的目录的文件夹，每个子目录依然是一个文件夹，对应一个entity，保存50content。
        String contentDirPath = paramSetting.getContentPath();
        String serialEntityTermPath = paramSetting.getSerialEntityTermPath();

        File dicDir = new File(contentDirPath);
        File[] contentDirs = dicDir.listFiles();//注意:这里只能用listFiles()，不能使用list()
        for(int i=beginIndex; i<endIndex; i++){
            assert contentDirs != null;
            File entityDic = contentDirs[i];
            EntityTermInfor entityTermInfor = getEntityTermInfo(entityDic);
            System.out.println(entityTermInfor.toString());

            String datFileName = serialEntityTermPath + "/" + entityDic.getName() + ".dat";
            FileTools.serializeObjToDat(datFileName, entityTermInfor);
        }
    }

    private EntityTermInfor getEntityTermInfo(File entityDic){
        String entityDicName = entityDic.getName();
        String[] inforArr = entityDicName.split(" ");
        String entityCode = inforArr[0];
        String expertName = inforArr[1];
        String institutionName = inforArr[2];
        EntityTermInfor entityTermInfor = new EntityTermInfor(-1, type, entityCode, expertName, institutionName);

        ArrayList<FileTermInfor> fileTermInforList = new ArrayList<>();
        File[] contentFiles = entityDic.listFiles();
        for(int j = 0; j< Objects.requireNonNull(contentFiles).length; j++){
            File contentFile = contentFiles[j];
            ComputeVectorListWithContent computeFileTermInfor = new ComputeVectorListWithContent(
                    contentFile, expertName, institutionName);
            FileTermInfor nowFileTerm = computeFileTermInfor.getFileTermInfo();
            fileTermInforList.add(nowFileTerm);
        }

        entityTermInfor.setFileTermInforList(fileTermInforList);
        return entityTermInfor;
    }
}

