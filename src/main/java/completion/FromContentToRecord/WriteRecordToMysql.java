package completion.FromContentToRecord;

import com.hankcs.hanlp.seg.common.Term;
import completion.Tools.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/11/5 18:07
 **/
public class WriteRecordToMysql {
    private final ParamSetting paramSetting;
    int beginIndex = 0;
    int endIndex = 0;

    List<Integer> idList;
    List<String> entityCodeList;
    List<String> expertNameList;
    List<String> institutionNameList;
    List<Float> scoreList;
    List<Float> predictedScoreList;
    List<float[]> featureVectorList;

    private int beginId = -1;
    private int nowId = -1;
    private String nowEntityCode;
    private String nowExpertName;
    private String nowInstitutionName;


    public WriteRecordToMysql(){
        paramSetting = SoftParameters.paramSetting;
        int initialCapacity = 4096;

        idList = new ArrayList<>(initialCapacity);
        entityCodeList = new ArrayList<>(initialCapacity);
        expertNameList = new ArrayList<>(initialCapacity);
        institutionNameList = new ArrayList<>(initialCapacity);
        scoreList = new ArrayList<>(initialCapacity);
        predictedScoreList = new ArrayList<>(initialCapacity);
        featureVectorList = new ArrayList<>(initialCapacity);

        //找到现在的开始id，因为与excel有关。
        int lastId = JdbcUtils.getLastId();
        beginId = lastId + 1;
        nowId = beginId;
    }

    /**
     * beginIndex与endIndex指的是entity的index。即读取多少个serialEntity
     */
    public void run(int beginIndex, int endIndex) {
        this.beginIndex = beginIndex;
        this.endIndex = endIndex;

        if(nowId == -1){
            System.out.println("lastId有误，拒绝写入");
            return;
        }

        extractAllRecord();

        System.out.println("提取完毕，准备写入");
        if(!checkRecordSizeLegal()){
            System.out.println("record 检验失败，拒绝写入！");
            return;
        }

        writeRecordToTable();
    }

    /**
     * 提取出所有的record，一个record就是excel中的一行。
     */
    private void extractAllRecord(){
        String serialEntityTermPath = paramSetting.getSerialEntityTermPath();
        File entityTermDic = new File(serialEntityTermPath);
        File[] entityTermFiles = entityTermDic.listFiles();

        for(int i=beginIndex; i<endIndex; i++){
            assert entityTermFiles != null;
            File datFile = entityTermFiles[i];
            EntityTermInfor entityTermInfor = (EntityTermInfor) FileTools.readObjFromDat(datFile);

            assert entityTermInfor != null;
            nowEntityCode = entityTermInfor.getEntityCode();
            nowExpertName = entityTermInfor.getExpertName();
            nowInstitutionName = entityTermInfor.getInstitutionName();

            ArrayList<FileTermInfor> fileTermInforList = entityTermInfor.getFileTermInforList();
            for(FileTermInfor termInfor : fileTermInforList){
                extractRecordFromFileTermInfor(termInfor);
            }
        }
    }

    /**
     * 提取出一个FileTermInfor中的所有record。
     */
    private void extractRecordFromFileTermInfor(FileTermInfor fileTermInfor){
        List<MyTerm> institutionTermList = fileTermInfor.getInstitutionTermList();
        List<List<Term>> institutionTermContextList = fileTermInfor.getInstitutionTermContextList();
        List<Float> nowScoreList = fileTermInfor.getInstitutionScoreList();

        int size = institutionTermList.size();
        for(int i=0; i<size; i++){
            idList.add(nowId);
            entityCodeList.add(nowEntityCode);
            expertNameList.add(nowExpertName);
            institutionNameList.add(nowInstitutionName);

            scoreList.add(nowScoreList.get(i));
            predictedScoreList.add(-1f);    //训练集，填充为-1，测试集才有意义。
            TermFeature feature = new TermFeature(fileTermInfor.getSort(), fileTermInfor.getWebType(), fileTermInfor.getFileLineCount(),
                    fileTermInfor.getFileLegalLineCount(), fileTermInfor.getSumLegalLineLength(), fileTermInfor.getExpertTypeTermCount(),
                    fileTermInfor.getInstitutionTypeTermCount(), institutionTermList.get(i), institutionTermContextList.get(i));
            featureVectorList.add(feature.getFeatureVector());

            nowId++;
        }
    }


    /**
     * 检查提取的record是否合法。目前就是检查所有的list长度是否相同
     * @return
     */
    private boolean checkRecordSizeLegal(){
        int nowSize = nowId - beginId;

        int idSize = idList.size();
        int entityCodeSize = entityCodeList.size();
        int expertNameSize = expertNameList.size();
        int institutionNameSize = institutionNameList.size();
        int scoreSize = scoreList.size();
        int predictedScoreSize = predictedScoreList.size();
        int vectorSize = featureVectorList.size();

        if(!(nowSize == idSize && nowSize == entityCodeSize && nowSize == expertNameSize && nowSize == institutionNameSize
                && nowSize == scoreSize && nowSize == predictedScoreSize && nowSize == vectorSize)){
            return false;
        }

        return true;
    }


    /**
     * 把提取到的所有record写到mysql表中
     */
    private void writeRecordToTable(){
        String trainDataTableName = paramSetting.getTrainDataTableName();
        int count = nowId - beginId;
        for(int i=0; i<count; i+=200){
            StringBuilder sqlBuilder = new StringBuilder("insert into `" + trainDataTableName +"` " +
                    "(id, entity_code, 专家名称, 单位名称, 标签分数, 预测分数, vector) values");
            for(int j=i; j<i+200 && j<count; j++){
                String vectorString = floatArrToString(featureVectorList.get(j));
                //一定注意，要不要带单引号，很容易出错
                sqlBuilder.append(" (")
                        .append(idList.get(j)).append(", '").append(entityCodeList.get(j)).append("', '")
                        .append(expertNameList.get(j)).append("', '").append(institutionNameList.get(j)).append("', ")
                        .append(scoreList.get(j)).append(", ").append(predictedScoreList.get(j)).append(", '")
                        .append(vectorString).append("'),");
            }
            sqlBuilder.deleteCharAt(sqlBuilder.length()-1);
            sqlBuilder.append(";");
            Connection conn;
            Statement stmt;
            try {
                conn = JdbcUtils.getConn();
                stmt = conn.createStatement();
                stmt.executeUpdate(sqlBuilder.toString());
            } catch (Exception e) {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
                System.out.println("failed, i = " + i);
                return;
            }
        }


    }

    //将存储特征向量的float[] 转换为一个String，方便存入数据库。因为不可能直接存储一个600多维的向量
    private String floatArrToString(float[] vectorArr){
        StringBuilder sb = new StringBuilder();
        int length = vectorArr.length;
        for(int i=0; i<length-1; i++){
            sb.append(vectorArr[i]).append(',');
        }
        sb.append(vectorArr[length-1]);
        return sb.toString();
    }


    /**
     * 将excel中的score数据读出来
     */
    public List<Float> readScoreListFromExcel(){
        String trainDataExcelPath = paramSetting.getTrainDataExcelPath();
        Workbook trainDataWb = ExcelTools.readExcel(trainDataExcelPath);
        assert trainDataWb != null;
        int[] dataLienRange = ExcelTools.findBeginEndLine(trainDataWb);
        int beginLineIndex = dataLienRange[0], endLineIndex = dataLienRange[1];

        Sheet trainDataSheet = ExcelTools.getSheet(trainDataWb, "Sheet1");
        List<Float> scoreList = new ArrayList<>(endLineIndex - beginLineIndex + 1);
        for(int i=beginLineIndex; i<endLineIndex; i++){
            scoreList.add((float)trainDataSheet.getRow(i).getCell(1).getNumericCellValue());
        }
        return scoreList;
    }
}
