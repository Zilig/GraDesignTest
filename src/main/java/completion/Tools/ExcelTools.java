package completion.Tools;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/5/19 19:30
 **/
public class ExcelTools {
    public static Workbook readExcel(String filePath){
        Workbook wb = null;
        if(filePath==null){
            return null;
        }
        String extString = filePath.substring(filePath.lastIndexOf("."));
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            if(".xls".equals(extString)){
                return wb = new HSSFWorkbook(is);
            }else if(".xlsx".equals(extString)){
                return wb = new XSSFWorkbook(is);
            }else{
                return wb = null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }

    //根据sheet的索引返回sheet
    public static Sheet getSheetAt(Workbook wb, int index){
        Sheet sheet = null;
        if(wb != null){
            sheet = wb.getSheetAt(index);
        }
        return sheet;
    }

    //根据sheet的名称返回sheet
    public static Sheet getSheet(Workbook wb, String s){
        Sheet sheet = null;
        if(wb != null){
            sheet = wb.getSheet(s);
        }
        return sheet;
    }

    //如果sheet1保存的是数据，sheet2保存的是开始行与结束行，可调用此方法在sheet2中记录开始行与结束行
    public static boolean writeRangeOfSheet(Workbook wb, int beginIndex, int endIndex){
        Sheet trainRangeSheet = getSheet(wb, "Sheet2");
        Row rangeRow =  trainRangeSheet.createRow(1);
        rangeRow.createCell(0).setCellValue(beginIndex);
        rangeRow.createCell(1).setCellValue(endIndex);    //按照java的习惯，结尾不在循环中
        return true;
    }

    //如果sheet1保存的是数据，sheet2保存的是开始行与结束行，可调用此方法返回开始行与结束行
    public static int[] findBeginEndLine(String excelFilePath){
        Workbook trainDataWb = ExcelTools.readExcel(excelFilePath);
        assert trainDataWb != null;
        return findBeginEndLine(trainDataWb);
    }

    //如果sheet1保存的是数据，sheet2保存的是开始行与结束行，可调用此方法返回开始行与结束行
    public static int[] findBeginEndLine(Workbook wb){
        Sheet sheet = wb.getSheetAt(1);
        int begin = (int)sheet.getRow(1).getCell(0).getNumericCellValue();
        int end = (int)sheet.getRow(1).getCell(1).getNumericCellValue();
        return new int[]{begin, end};
    }

    //如果sheet1保存的是数据，sheet2保存的是开始行与结束行，可调用此方法找到word所在的行数
    public static int findWordLine(Workbook wb, String word, int rowIndex){
        int[] beginAndEnd = findBeginEndLine(wb);
        int result = findWordLine(getSheetAt(wb, 0), word, rowIndex, beginAndEnd[0], beginAndEnd[1]);
        return result;
    }

    //找到该word在第几行
    public static int findWordLine(Sheet sheet, String word, int rowIndex, int beginLine, int endLine){
        for(int i=beginLine; i<endLine; i++){
            String cellValue = sheet.getRow(i).getCell(rowIndex).getStringCellValue();
            if(word.equals(cellValue)) return i;
        }
        return -1;
    }

    //在表格中修改一个cell的内容，已经使用数据库，无需再使用该函数
    @Deprecated
    public synchronized static void updateReadTag(Sheet sheet, int rowIndex, int colIndex){
        Cell cell = sheet.getRow(rowIndex).getCell(colIndex);
        cell.setCellValue(1);
    }


}
