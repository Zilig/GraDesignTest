package completion.Tools;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/7/15 19:37
 **/
public class JdbcUtils {
    //通过上面的工具就可以获取到properties文件中的键值从而可以加载驱动 获取链接 从而 可以增删改查
    private static Connection conn = null;
    private static boolean hasLoadConn = false;
    private static Connection vectorConn = null;
    private static boolean hasLoadVectorConn = false;

    private static ParamSetting paramSetting;
    static {
        paramSetting = ParamSetting.getParams();
    }

    public static Connection getConn(){
        if(!hasLoadConn){
            ParamSetting prop = ParamSetting.getParams();
            //存储百度到的网页的url文件的目录，每行关键词会创建一个文件，一行是一个url，存在空行，即无效url
            String driver = prop.getDriverName();
            String url = prop.getDriverUrl();
            String username = prop.getUsername();
            String password = prop.getPassword();

            try {
                Class.forName(driver);
                conn = DriverManager.getConnection(url,username,password);
                hasLoadConn = true;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
                closeConn();
            }
        }
        return conn;
    }

    public static Connection getVectorConn(){
        if(!hasLoadVectorConn){
            ParamSetting prop = ParamSetting.getParams();
            //存储百度到的网页的url文件的目录，每行关键词会创建一个文件，一行是一个url，存在空行，即无效url
            String driver = prop.getDriverName();
            String url = prop.getDriverVectorUrl();
            String username = prop.getUsername();
            String password = prop.getPassword();

            try {
                Class.forName(driver);
                vectorConn = DriverManager.getConnection(url,username,password);
                hasLoadVectorConn = true;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
                closeVectorConn();
            }
        }
        return vectorConn;
    }

    public static void closeConn(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeVectorConn(){
        try {
            vectorConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param word (word 汉字)
     * @return 汉字转拼音 其它字符不变
     */
    private static String getPinyin(String word){
        HanyuPinyinOutputFormat formart = new HanyuPinyinOutputFormat();
        formart.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        formart.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        formart.setVCharType(HanyuPinyinVCharType.WITH_V);
        char[] arrays = word.trim().toCharArray();
        StringBuilder result = new StringBuilder();
        try {
            for (int i=0;i<arrays.length;i++) {
                char ti = arrays[i];
                if(Character.toString(ti).matches("[\\u4e00-\\u9fa5]")){ //匹配是否是中文
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(ti,formart);
                    result.append(temp[0]);
                }else{
                    result.append(ti);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    private static String getSgnsTableName(String word){
        String tableName = SoftParameters.sngsDatabaseNamePrefix;

        char firstChar = word.charAt(0);
        if(Character.toString(firstChar).matches("[\\u4e00-\\u9fa5]")){
            //是中文
            String pinyin = getPinyin(String.valueOf(firstChar));
            char tableTag = pinyin.charAt(0);
            if(SoftParameters.yunmuSet.contains(tableTag)){
                tableName += "yun";
            }
            else if(SoftParameters.shengmuSet.contains(tableTag)){
                tableName += tableTag;
            }
        }
        else{
            tableName += "other";
        }
        return tableName;
    }

    //从数据库中查找出该word对应的vector
    private static String readVectorFromDB(String word, String tableName){
        try {
            Connection conn = getVectorConn();
            String sql = "select vector from " + tableName + " where word = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, word);
            ResultSet rs = stmt.executeQuery();
            String result = null;
            while(rs.next()){      //这里必须循环遍历
                result = rs.getString("vector");//返回一条记录
            }
            return result;
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            System.out.println("readVectorFromDB, 该word没有获取到对应的vector, word=" + word);
            return null;
        }
    }

    //根据一个中文单词获取词向量
    public static float[] getWordVector(String word){
        float[] resultArr = new float[SoftParameters.sngsDatabaseVectorSize];
        Arrays.fill(resultArr, 0);
        try{
            String tableName = getSgnsTableName(word);
            String vector = readVectorFromDB(word, tableName);
            if(Objects.isNull(vector)){
                return resultArr;
            }
            String[] vectorArr = vector.split(" ");
            for(int i=0; i<SoftParameters.sngsDatabaseVectorSize; i++){
                resultArr[i] = Float.parseFloat(vectorArr[i]);
            }
            return resultArr;
        } catch (Exception e){
            System.out.println("getWordVector, 该word没有获取到对应的vector, word=" + word);
            return resultArr;
        }
    }

    //获取数据集当前的最大id(最后一个id)
    public static int getLastId(){
        try {
            String trainTableName = paramSetting.getTrainDataTableName();
            Connection conn = getConn();
            String sql = "select id from " + trainTableName + " order by id desc limit 0, 1";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            int result = -1;
            while(rs.next()){      //这里必须循环遍历
                result = rs.getInt("id");//返回最后一条记录的id
            }
            return result;
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            System.out.println("getLastId 失败, 没有找到最后一个id");
            e.printStackTrace();
            return -1;
        }
    }

    //获取数据集中数据的行数
    public static int getRowCount(){
        try {
            Connection conn = getConn();
            String sql = "select ifnull(max(id),0)-ifnull(min(id),0)+1 as ro from gra_design.train_data";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            int result = -1;
            while(rs.next()){      //这里必须循环遍历
                result = rs.getInt("id");//返回最后一条记录的id
            }
            return result;
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            System.out.println("getRowCount 失败, 无法获取数据集行数");
            e.printStackTrace();
            return -1;
        }
    }

    //vector在表中为text属性，所以以ArrayList<String>保存查询结果
    public static boolean getDataAndLabel(ArrayList<String> vectorList, ArrayList<Float> labelList, int expectCount){
        try {
            Connection conn = getConn();
            String sql = "select 标签分数, vector from gra_design.train_data limit " + expectCount;
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){      //这里必须循环遍历
                vectorList.add(rs.getString("vector"));
                labelList.add(rs.getFloat("标签分数"));
            }
            return true;
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            System.out.println("getDataAndLabel 失败, 无法获取数据集");
            e.printStackTrace();
            return false;
        }
    }
}
