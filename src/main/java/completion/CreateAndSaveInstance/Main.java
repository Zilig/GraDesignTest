package completion.CreateAndSaveInstance;

import completion.Tools.JdbcUtils;
import completion.Tools.ParamSetting;
import completion.Tools.SoftParameters;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2022/3/22 21:27
 * @Description TODO
 **/
public class Main {

    private static final ParamSetting paramSetting = SoftParameters.paramSetting;
    private static int GlobalId = 947;

    public static void main(String[] args) {
        List<Record> allRecordList = new ArrayList<>(512);
        ParamSetting paramSetting = SoftParameters.paramSetting;
        //存储content的目录的文件夹，每个子目录依然是一个文件夹，对应一个entity，保存50content。
        String contentDirPath = paramSetting.getContentPath();
        File dicDir = new File(contentDirPath);
        File[] contentDirs = dicDir.listFiles();//注意:这里只能用listFiles()，不能使用list()

        //int beginIndex = 0, endIndex = Objects.requireNonNull(contentDirs).length;
        int beginIndex = 0, endIndex = 41;
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
                FileInfor fileInfor = new FileInfor(contentFile, entityCode, contentFile.getName(), expertName, institutionName, j);
                List<Record> recordList = fileInfor.getRecords();
                //System.out.println("文件夹：" + entityDicName + ", 文件名：" + j + ", 记录个数：" + recordList.size());
                allRecordList.addAll(recordList);
            }
            //每计算出一个实例的所有record，就写入一次数据库
            System.out.println("文件夹：" + entityDicName + ", 记录个数：" + allRecordList.size());
            int result = writeRecordToTable(allRecordList);
            allRecordList.clear();
            if(result == -1){
                System.out.println("i = " + i);
                return;
            }

        }
    }


    /**
     * 把提取到的所有record写到mysql表中
     */
    private static int writeRecordToTable(List<Record> allRecordList) {
        String instanceTableName = paramSetting.getInstanceTable();
        int count = allRecordList.size();
        for (int i = 0; i < count; i += 100) {
            StringBuilder sqlBuilder = new StringBuilder("insert into `" + instanceTableName + "` " +
                    "(id, DictCode, FileName, ExpertName, InstitutionName, EntityName, Score, " +
                    "EntityLen, EntityLineLen, DistanceFromLine, DistanceFromBegin, DistanceFromHead, ContainKeyType," +
                    "Sort, FileLineCount, FileLegalLineCount, FileDigitCount, ExpertTypeTermCount, InstitutionTypeTermCount, Dispersion," +
                    "AboveContext, InnerContext, BelowContext) values");
            for (int j = i; j < i + 100 && j < count; j++) {
                Record record = allRecordList.get(j);
                //一定注意，要不要带单引号，很容易出错
                sqlBuilder.append(" (")
                        .append(GlobalId).append(", '").append(record.getDictCode()).append("', '")
                        .append(record.getFileName()).append("', '").append(record.getExpertName()).append("', '")
                        .append(record.getInstitutionName()).append("', '").append(record.getEntityName()).append("', ")
                        .append(record.getScore()).append(", ").append(record.getEntityLen()).append(", ")
                        .append(record.getEntityLineLen()).append(", ").append(record.getDistanceFromLine()).append(", ")
                        .append(record.getDistanceFromBegin()).append(", ").append(record.getDistanceFromHead()).append(", ")
                        .append(record.getContainKeyType()).append(", ").append(record.getSort()).append(", ")
                        .append(record.getFileLineCount()).append(", ").append(record.getFileLegalLineCount()).append(", ")
                        .append(record.getFileDigitCount()).append(", ").append(record.getExpertTypeTermCount()).append(", ")
                        .append(record.getInstitutionTypeTermCount()).append(", ").append(record.getDispersion()).append(", '")
                        .append(record.getAboveContext()).append("', '").append(record.getInnerContext()).append("', '")
                        .append(record.getBelowContext()).append("'),");
                GlobalId++;
            }
            sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
            sqlBuilder.append(";");
            System.out.println("sql语句的长度：" + sqlBuilder.length());
            Connection conn;
            Statement stmt;
            try {
                conn = JdbcUtils.getConn();
                stmt = conn.createStatement();
                stmt.executeUpdate(sqlBuilder.toString());
            } catch (Exception e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
                System.out.println(sqlBuilder.toString());
                System.out.println("failed, i = " + i);
                return -1;
            }
        }
        return 0;
    }
}
