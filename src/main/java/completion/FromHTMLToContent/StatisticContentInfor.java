package completion.FromHTMLToContent;

import completion.Tools.ExcelTools;
import completion.Tools.JdbcUtils;
import completion.Tools.ParamSetting;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/7/22 16:02
 **/
public class StatisticContentInfor {
    private static class ContentEntityInfor{
        String rootPath;
        String entityPath;
        int totalFileNumber;
        int lengthLegalFileNumber;
        float lengthLegalRate;
        int totalHeadFileNumber;
        float headLegalRateInAll;
        float headLegalRateInLength;
        int totalTailFileNumber;
        float tailLegalRateInAll;
        float tailLegalRateInLength;
        int totalLegalFileNumber;
        float legalRateInAll;
        float legalRateInLength;

        ContentEntityInfor(String rootPath, String entityPath, int totalFileNumber, int lengthLegalFileNumber,
                        int totalHeadFileNumber, int totalTailFileNumber, int totalLegalFileNumber){
            this.rootPath = rootPath;
            this.entityPath = entityPath;
            this.totalFileNumber = totalFileNumber;
            this.lengthLegalFileNumber = lengthLegalFileNumber;
            this.lengthLegalRate = (float)lengthLegalFileNumber / (float)totalFileNumber;

            this.totalHeadFileNumber = totalHeadFileNumber;
            this.headLegalRateInAll = (float)totalHeadFileNumber / (float)totalFileNumber;
            this.headLegalRateInLength = (float)totalHeadFileNumber / (float)lengthLegalFileNumber;

            this.totalTailFileNumber = totalTailFileNumber;
            this.tailLegalRateInAll = (float)totalTailFileNumber / (float)totalFileNumber;
            this.tailLegalRateInLength = (float)totalTailFileNumber / (float)lengthLegalFileNumber;

            this.totalLegalFileNumber = totalLegalFileNumber;
            this.legalRateInAll = (float)totalLegalFileNumber / (float)totalFileNumber;
            this.legalRateInLength = (float)totalLegalFileNumber / (float)lengthLegalFileNumber;
        }
    }

    private ArrayList<ContentEntityInfor> fileToEntityInfor(int beginIndex, int endIndex) {
        ArrayList<ContentEntityInfor> resultList = new ArrayList<>();

        String readExcelName = "D:/Java/Workspace/GraDesign1/src/main/resources/Keywords/contentinfor_202107211618.xlsx";
        Workbook readWB = ExcelTools.readExcel(readExcelName);
        Sheet readSheet = ExcelTools.getSheetAt(readWB, 0);

        ArrayList<Integer> lengthTypeList = new ArrayList<>();
        ArrayList<Integer> headTimeList = new ArrayList<>();
        ArrayList<Integer> tailTimeList = new ArrayList<>();
        ArrayList<Integer> headAndTailList = new ArrayList<>();
        for (int i = beginIndex; i < endIndex; i++) {
            //if(i % 200 == 0) System.out.println("i = " + i);
            String nowEntityPath = readSheet.getRow(i).getCell(1).getStringCellValue();
            String nextEntityPath = readSheet.getRow(i + 1).getCell(1).getStringCellValue();

            String rootPath = readSheet.getRow(i).getCell(0).getStringCellValue();
            String entityPath = readSheet.getRow(i).getCell(1).getStringCellValue();
            int lengthType = (int)readSheet.getRow(i).getCell(4).getNumericCellValue();
            int headTime = (int)readSheet.getRow(i).getCell(5).getNumericCellValue();
            int tailTime = (int)readSheet.getRow(i).getCell(6).getNumericCellValue();
            int headAndTail = (int)readSheet.getRow(i).getCell(7).getNumericCellValue();

            lengthTypeList.add(lengthType);
            headTimeList.add(headTime);
            tailTimeList.add(tailTime);
            headAndTailList.add(headAndTail);

            if(!nextEntityPath.equals(nowEntityPath)){
                ContentEntityInfor infor = inforListToEntityInfor(rootPath,entityPath,lengthTypeList,headTimeList,tailTimeList,headAndTailList);
                resultList.add(infor);

                lengthTypeList.clear();
                headTimeList.clear();
                tailTimeList.clear();
                headAndTailList.clear();
            }
        }

        return resultList;
    }

    private ContentEntityInfor inforListToEntityInfor(String rootPath, String entityPath, ArrayList<Integer> lengthTypeList,
                                                                      ArrayList<Integer> headTimeList, ArrayList<Integer> tailTimeList,
                                                                      ArrayList<Integer> headAndTailList){
        int totalFileNumber = lengthTypeList.size();

        int lengthLegalFileNumber = 0;
        for (Integer integer : lengthTypeList) {
            if (integer.equals(0)) lengthLegalFileNumber++;
        }

        int totalHeadFileNumber = 0;
        for (Integer integer : headTimeList) {
            if (integer > 0) totalHeadFileNumber++;
        }

        int totalTailFileNumber = 0;
        for (Integer value : tailTimeList) {
            if (value > 0) totalTailFileNumber++;
        }

        int totalLegalFileNumber = 0;
        for (Integer integer : headAndTailList) {
            if (integer.equals(1))  totalLegalFileNumber++;
        }

        ContentEntityInfor entityInfor = new ContentEntityInfor(rootPath,entityPath,totalFileNumber,lengthLegalFileNumber,
                totalHeadFileNumber,totalTailFileNumber,totalLegalFileNumber);
        return entityInfor;
    }

    //将文件的统计信息写入数据库
    private void writeInforToDB(ArrayList<ContentEntityInfor> inforList, String sql){
        Connection conn;
        PreparedStatement stmt;
        ParamSetting prop = ParamSetting.getParams();
        String driver = prop.getDriverName();

        try {
            Class.forName(driver);
            conn = JdbcUtils.getConn();
            for(int i=0; i<inforList.size(); i++){
                ContentEntityInfor infor = inforList.get(i);
                stmt = (PreparedStatement) conn.prepareStatement(sql);
                stmt.setString(1, infor.rootPath);
                stmt.setString(2, infor.entityPath);

                stmt.setInt(3, infor.totalFileNumber);
                stmt.setInt(4, infor.lengthLegalFileNumber);
                stmt.setFloat(5,infor.lengthLegalRate);

                stmt.setInt(6,infor.totalHeadFileNumber);
                stmt.setFloat(7,infor.headLegalRateInAll);
                stmt.setFloat(8,infor.headLegalRateInLength);

                stmt.setInt(9, infor.totalTailFileNumber);
                stmt.setFloat(10,infor.tailLegalRateInAll);
                stmt.setFloat(11,infor.tailLegalRateInLength);

                stmt.setInt(12, infor.totalLegalFileNumber);
                stmt.setFloat(13,infor.legalRateInAll);
                stmt.setFloat(14,infor.legalRateInLength);
                stmt.executeUpdate();
                if(i % 50 == 0){
                    System.out.println("the number of writing content entity information:" + i);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }

    public void runStatisticContent(int begin, int end){
        ArrayList<ContentEntityInfor> results = fileToEntityInfor(begin, end);
        String sql = "insert into ContentEntityInfor values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        writeInforToDB(results, sql);
    }
}
