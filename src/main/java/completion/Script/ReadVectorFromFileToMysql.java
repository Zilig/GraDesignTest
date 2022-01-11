package completion.Script;

import completion.Tools.JdbcUtils;
import completion.Tools.ParamSetting;
import completion.Tools.SoftParameters;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashSet;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/10/16 19:35
 **/

//将sgns.sougou.bigram向量，从文件读取到表sgns.sougou.bigram中
//因为一个表36万行太大，普通检索要18s，因此分表，包括一类是声母，一类是韵母a o e i u v，一类是其他，。
public class ReadVectorFromFileToMysql {
    private static class WordVectorPair{
        String word;
        String vector;

        public WordVectorPair(String word, String vector){
            this.word = word;
            this.vector = vector;
        }
    }

    private static ParamSetting paramSetting = ParamSetting.getParams();
    public static void main(String[] args) throws InterruptedException {
        String sougouBigramFileName = "D:\\BaiduNetdiskDownload\\搜狗新闻语料库\\sgns.sogou.bigram";
        runSngsSougouBigram2(sougouBigramFileName, '!');
//        char[] tagArr = {'l', 's', 'y', 'z'}; //l，s， y， z, 非 太大，还没跑
//        for(char tag : tagArr){
//            runSngsSougouBigram2(sougouBigramFileName, tag);
//            Thread.sleep(2000);
//        }

    }

    private static void runSngsSougouBigram(String fileName, char tableTag){
        //确定表名
        String tableName = "sgns_sougou_bigram_";
        if(SoftParameters.yunmuSet.contains(tableTag)){
            tableName += "yun";
        }
        else if(SoftParameters.shengmuSet.contains(tableTag)){
            tableName += tableTag;
        }
        else tableName += "other";
        System.out.println("tableName = " + tableName);

        //确定汉字转拼音的模式
        HanyuPinyinOutputFormat formart = new HanyuPinyinOutputFormat();
        formart.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        formart.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        formart.setVCharType(HanyuPinyinVCharType.WITH_V);

        //开始读取文件
        File file = new File(fileName);
        HashSet<WordVectorPair> pairSet = new HashSet<>();
        HashSet<String> wordSet = new HashSet<>();
        //分行读取
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
            bf.readLine();  //第一行不是数据，跳过
            String lineStr = "";
            int count = 0;
            while ((lineStr = bf.readLine()) != null) {
                int blankIndex = lineStr.indexOf(" ");
                String word = lineStr.substring(0, blankIndex);
                if(word.contains("〇") || word.contains("０")) continue;
//                if(word.contains("〇")){
//                    System.out.println("a word contains 〇: " + word);
//                    word = word.replace("〇","零");
//                }

                String vector = lineStr.substring(blankIndex+1);
                char firstChar = word.charAt(0);
                if(Character.toString(firstChar).matches("[\\u4e00-\\u9fa5]")){ //是否为中文
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(word.charAt(0),formart);
                    if(temp.length == 0) continue;
                    char firstDigit = temp[0].charAt(0);
                    if(firstDigit == tableTag){
//                        if(pairSet.size() % 500 == 0) {
//                            System.out.println("pairSet = " + pairSet.size());
//                        }
                        if(wordSet.add(word)){
                            WordVectorPair wordVectorPair = new WordVectorPair(word, vector);
                            pairSet.add(wordVectorPair);
                        }
                    }
                }
                else{
                    //如果要读取的是非汉字
                    if(tableTag == '!'){
                        if(wordSet.add(word)){  //如果word重复了，就不能再插入。
                            WordVectorPair wordVectorPair = new WordVectorPair(word, vector);
                            pairSet.add(wordVectorPair);
                        }
                    }
                }

            }
            System.out.println("count = " + pairSet.size());
            if(pairSet.size() > 0) writeVectorToDB(pairSet, tableName);
            pairSet.clear();

            bf.close();
        } //如果文件读取时出现错误，报错，读取下一个文件
        catch(Exception exception){
            exception.printStackTrace();
        }
    }

    //有重复的word，该方法废弃。
    @Deprecated
    private static void runSngsSougouBigram2(String fileName, char tableTag){
        //确定表名
        String tableName = "sgns_sougou_bigram_";
        if(SoftParameters.yunmuSet.contains(tableTag)){
            tableName += "yun";
        }
        else if(SoftParameters.shengmuSet.contains(tableTag)){
            tableName += tableTag;
        }
        else tableName += "other";
        System.out.println("tableName = " + tableName);

        //确定汉字转拼音的模式
        HanyuPinyinOutputFormat formart = new HanyuPinyinOutputFormat();
        formart.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        formart.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        formart.setVCharType(HanyuPinyinVCharType.WITH_V);

        //开始读取文件
        File file = new File(fileName);
        HashSet<WordVectorPair> pairSet = new HashSet<>();
        //分行读取
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
            bf.readLine();  //第一行不是数据，跳过
            String lineStr = "";
            int count = 0;
            while ((lineStr = bf.readLine()) != null) {
                int blankIndex = lineStr.indexOf(" ");
                String word = lineStr.substring(0, blankIndex);
                String vector = lineStr.substring(blankIndex+1);
                char firstChar = word.charAt(0);
                if(Character.toString(firstChar).matches("[\\u4e00-\\u9fa5]")){ //是否为中文
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(word.charAt(0),formart);
                    if(temp.length == 0) continue;
                    char firstDigit = temp[0].charAt(0);
                    if(firstDigit == tableTag){
                        count++;
                        WordVectorPair wordVectorPair = new WordVectorPair(word, vector);
                        pairSet.add(wordVectorPair);
                        if(pairSet.size() % 300 == 0){
                            System.out.println("count = " + count);
                            writeVectorToDB(pairSet, tableName);
                            pairSet.clear();
                        }
                    }
                }
                else{
                    //如果要读取的是非汉字
                    if(tableTag == '!'){
                        count++;
                        WordVectorPair wordVectorPair = new WordVectorPair(word, vector);
                        pairSet.add(wordVectorPair);
                        if(pairSet.size() % 1 == 0){
                            System.out.println("count = " + count);
                            writeVectorToDB(pairSet, tableName);
                            pairSet.clear();
                        }
                    }
                }
            }
            bf.close();
        } //如果文件读取时出现错误，报错，读取下一个文件
        catch(Exception exception){
           exception.printStackTrace();
        }
    }

    //将词向量写入数据库，一次写入多行。暂定1000行。
    private static void writeVectorToDB(HashSet<WordVectorPair> pairSet, String tableName){
        StringBuilder sqlBuilder = new StringBuilder("insert into `" + tableName +"` (word, vector) values");
        for(WordVectorPair pair : pairSet){
            String word = pair.word;
            String vector = pair.vector;
            sqlBuilder.append(" ('").append(word).append("', '").append(vector).append("'),");
        }

        sqlBuilder.deleteCharAt(sqlBuilder.length()-1);
        sqlBuilder.append(";");
        //System.out.println(sqlBuilder.length() + "\n");

        Connection conn;
        Statement stmt;
        try {
            conn = JdbcUtils.getVectorConn();
            stmt = conn.createStatement();
            stmt.executeUpdate(sqlBuilder.toString());
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }
}
