package completion.FromContentToRecord;

import completion.Tools.FileTools;
import completion.Tools.ParamSetting;
import completion.Tools.SoftParameters;

import java.io.File;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/10/26 22:27
 **/
public class ReadSerialEntityTermInfo {
    private final ParamSetting paramSetting = SoftParameters.paramSetting;

    public void run(int beginIndex, int endIndex){
        readSerialEntityTerm(beginIndex, endIndex);
    }

    private void readSerialEntityTerm(int beginIndex, int endIndex){
        String serialEntityTermPath = paramSetting.getSerialEntityTermPath();
        File entityTermDic = new File(serialEntityTermPath);
        File[] entityTermFiles = entityTermDic.listFiles();
        for(int i=beginIndex; i<endIndex; i++){
            assert entityTermFiles != null;
            File datFile = entityTermFiles[i];
            EntityTermInfor entityTermInfor = (EntityTermInfor) FileTools.readObjFromDat(datFile);
            assert entityTermInfor != null;
            entityTermInfor.printDetail();
        }
    }
}
