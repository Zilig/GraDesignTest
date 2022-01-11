package completion.Tools;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.util.HashMap;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/10/8 20:20
 **/
public class EntityTools {
    //key为专家名称，value为专家单位
    public HashMap<String, String> expertNameEnterpriseMap;
    private static EntityTools entityTools = null;
    private EntityTools(){
        expertNameEnterpriseMap = new HashMap<>();

        //1.存储所有专家的excel文件的路径
        String allExpertFilePath = "./FrontHalf/src/main/resources/Keywords/专家-初始完整版.xlsx";
        Workbook wb = ExcelTools.readExcel(allExpertFilePath);
        //int beginLine = (int)ExcelTools.getSheetAt(wb,1).getRow(1).getCell(0).getNumericCellValue();
        //int endLine = (int)ExcelTools.getSheetAt(wb,1).getRow(1).getCell(1).getNumericCellValue();
        int beginLine = 2;
        int endLine = 1500;
        Sheet sheet = ExcelTools.getSheetAt(wb, 0);

        for(int i=beginLine; i<endLine; i++){
            String expertName = sheet.getRow(i).getCell(1).getStringCellValue();
            String enterpriseName = sheet.getRow(i).getCell(7).getStringCellValue();
            expertNameEnterpriseMap.put(expertName,enterpriseName);
        }
        assert wb != null;
        try{
            wb.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static EntityTools getSingleInstance(){
        if(entityTools == null){
            entityTools = new EntityTools();
        }
        return entityTools;
    }
}
