package completion.FromKeywordToHTML;

import completion.Tools.ExcelTools;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/5/24 21:15
 **/
public class GetKeywords {
    public static void main(String[] args) throws IOException {
        //保存完整三元组的excel
        String comFileName = "D:/Java/Workspace/GraDesign1/src/main/resources/Keywords/Triple-Com.xlsx";
        Workbook comWb = ExcelTools.readExcel(comFileName);
        //Sheet comSheet = comWb.createSheet("Data");
        Sheet comSheet = ExcelTools.getSheetAt(comWb, 0);
        int comLine = 1;

        //保存单位缺失的三元组的excel
        String misFileName = "D:/Java/Workspace/GraDesign1/src/main/resources/Keywords/Triple-Mis.xlsx";
        Workbook misWb = ExcelTools.readExcel(misFileName);
        //Sheet misSheet = misWb.createSheet("Data");
        Sheet misSheet = ExcelTools.getSheetAt(misWb, 0);
        int misLine = 1;

        String srcFileName = "D:/Java/Workspace/GraDesign1/src/main/resources/Keywords/专家-初始完整版.xlsx";
        Workbook srcWb = ExcelTools.readExcel(srcFileName);
        Sheet srcSheet = ExcelTools.getSheetAt(srcWb, 0);
        int srcBeginLine = 2;
        int srcEndLine = 61268;
        for(int i=srcBeginLine; i<srcEndLine; i++){
            if(i % 200 == 0) System.out.println("i = " + i);
            String kg_id = srcSheet.getRow(i).getCell(0).getStringCellValue();
            String expertName = srcSheet.getRow(i).getCell(1).getStringCellValue();
            String institutionName = srcSheet.getRow(i).getCell(7).getStringCellValue();
            //单位缺失的三元组
            Row row;
            if(institutionName.equals("#")){
                row = misSheet.createRow(misLine);
                row.createCell(0).setCellValue(misLine++);
                row.createCell(1).setCellValue(kg_id);
                row.createCell(2).setCellValue(expertName);
            }
            //完整的三元组
            else{
                row = comSheet.createRow(comLine);
                row.createCell(0).setCellValue(comLine++);
                row.createCell(1).setCellValue(kg_id);
                row.createCell(2).setCellValue(expertName+institutionName);
            }
            row.createCell(3).setCellValue("0");
        }

        assert srcWb != null;
        srcWb.close();

        FileOutputStream comOut = new FileOutputStream(comFileName);
        assert comWb != null;
        comWb.write(comOut);
        comOut.close();
        comWb.close();

        FileOutputStream misOut = new FileOutputStream(misFileName);
        assert misWb != null;
        misWb.write(misOut);
        misOut.close();
        misWb.close();

    }
}
