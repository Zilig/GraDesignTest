package completion.Tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/6/2 21:36
 **/
public class ParamSetting {

    /**
     * 1.mysql驱动
     */
    private String driverName;

    /**
     * 2.驱动地址
     */
    private String driverUrl;

    /**
     * 3.用户名
     */
    private String username;

    /**
     * 4.密码
     */
    private String password;


    /**
     * 5.存储完整三元组的文件的路径，在该文件所在文件夹会创建success.txt用于存储下载成功的关键词组
     */
    private String comKeywordsFilePath;

    /**
     * 6.存储不完整三元组的文件的路径，在该文件所在文件夹会创建success.txt用于存储下载成功的关键词组
     */
    private String misKeywordsFilePath;

    /**
     * 7.存储下载的搜索结果文件的目录的路径，每行关键词会创建一个文件夹，文件夹下是该关键词对应的多个html网页。完整的关键词
     */
    private String outputDirPath;

    /**
     * 8.存储下载的搜索结果文件的目录的路径，每行关键词会创建一个文件夹，文件夹下是该关键词对应的多个html网页。缺失尾实体的关键词
     */
    private String outputDirPathMis;

    /**
     * 9.存储百度到的网页的url文件的目录，每行关键词会创建一个文件，一行是一个url，存在空行，即无效url。完整的关键词
     */
    private String urlDirPath;

    /**
     * 10.存储百度到的网页的url文件的目录，每行关键词会创建一个文件，一行是一个url，存在空行，即无效url。缺失尾实体的关键词
     */
    private String urlDirPathMis;

    /**
     * 11.存储从html中提取到的正文的目录，与outputDirPath结构完全相同，可以认为是其content副本。完整的关键词
     */
    private String contentPath;

    /**
     * 12.存储从html中提取到的正文的目录，与outputDirPath结构完全相同，可以认为是其content副本。缺失尾实体的关键词
     */
    private String contentPathMis;

    /**
     * 13.存储从content中得到的EntityTermInfor对象的序列化的dat文件
     */
    private String serialEntityTermPath;

    /**
     * 14.存储从content_mis中得到的EntityTermInfor对象的序列化的dat文件
     */
    private String serialEntityTermPathMis;

    /**
     * 15.存储sgns.sougou.bigram向量的表名
     */
    private String sgnsSougouBigramTableName;

    /**
     * 16.存储词向量的数据库的连接地址
     */
    private String driverVectorUrl;

    /**
     * 17.存储序列化对象的根目录
     */
    private String serialRootPath;

    /**
     * 18.存储训练集的excel路径
     */
    private String trainDataExcelPath;

    /**
     * 19.存储测试集的excel路径
     */
    private String testDataExcelPath;

    /**
     * 20.存储训练集的表名
     */
    private String trainDataTableName;

    /**
     * 21.存储测试集的表名
     */
    private String testDataTableName;

    private static ParamSetting paramSetting = new ParamSetting();

    private static boolean hasLoad = false;

    public static ParamSetting getParams() {

        if (hasLoad) {
            return paramSetting;
        }

        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream(new File(".\\FrontHalf\\settings.properties")));

            //1.
            String driverName = prop.getProperty("driverName");
            if(driverName != null){
                paramSetting.setDriverName(driverName);
            }
            //2.
            String driverUrl = prop.getProperty("driverUrl");
            if(driverUrl != null){
                paramSetting.setDriverUrl(driverUrl);
            }
            //3.
            String username = prop.getProperty("username");
            if(username != null){
                paramSetting.setUsername(username);
            }
            //4.
            String password = prop.getProperty("password");
            if(password != null){
                paramSetting.setPassword(password);
            }

            //5.存储完整三元组的文件
            String comKeywordsFilePathSet = prop.getProperty("comKeywordsFilePath");
            if (comKeywordsFilePathSet != null) {
                paramSetting.setComKeywordsFilePath(comKeywordsFilePathSet);
            }
            //6.存储不完整三元组的文件
            String misKeywordsFilePathSet = prop.getProperty("misKeywordsFilePath");
            if (misKeywordsFilePathSet != null) {
                paramSetting.setMisKeywordsFilePath(misKeywordsFilePathSet);
            }

            //7.存储下载的搜索结果文件的目录的路径，每行关键词会创建一个文件夹，文件夹下是该关键词对应的多个html网页。完整的关键词
            String outputDirPathSet = prop.getProperty("outputDirPath");
            if (outputDirPathSet != null) {
                paramSetting.setOutputDirPath(outputDirPathSet);
            }
            //8.存储下载的搜索结果文件的目录的路径，每行关键词会创建一个文件夹，文件夹下是该关键词对应的多个html网页。缺失尾实体的关键词
            String outputDirPathMisSet = prop.getProperty("outputDirPathMis");
            if(outputDirPathMisSet != null){
                paramSetting.setOutputDirPathMis(outputDirPathMisSet);
            }
            //9.存储百度到的网页的url文件的目录，每行关键词会创建一个文件，一行是一个url，存在空行，即无效url。完整的关键词
            String urlDirPathSet = prop.getProperty("urlDirPath");
            if(urlDirPathSet != null){
                paramSetting.setUrlDirPath(urlDirPathSet);
            }
            //10.存储百度到的网页的url文件的目录，每行关键词会创建一个文件，一行是一个url，存在空行，即无效url。缺失尾实体的关键词
            String urlDirPathSetMis = prop.getProperty("urlDirPathMis");
            if(urlDirPathSetMis != null){
                paramSetting.setUrlDirPathMis(urlDirPathSetMis);
            }
            //11.存储从html中提取到的正文的目录，与outputDirPath结构完全相同，可以认为是其content副本。完整的关键词
            String contentPathSet = prop.getProperty("contentPath");
            if(contentPathSet != null){
                paramSetting.setContentPath(contentPathSet);
            }
            //12.存储从html中提取到的正文的目录，与outputDirPath结构完全相同，可以认为是其content副本。缺失尾实体的关键词
            String contentPathSetMis = prop.getProperty("contentPathMis");
            if(contentPathSetMis != null){
                paramSetting.setContentPathMis(contentPathSetMis);
            }

            //13.存储从content中得到的EntityTermInfor对象的序列化的dat文件
            String serialEntityTermPath = prop.getProperty("serialEntityTermPath");
            if(serialEntityTermPath != null){
                paramSetting.setSerialEntityTermPath(serialEntityTermPath);
            }

            //14.存储从content_mis中得到的EntityTermInfor对象的序列化的dat文件
            String serialEntityTermPathMis = prop.getProperty("serialEntityTermPathMis");
            if(serialEntityTermPathMis != null){
                paramSetting.setSerialEntityTermPathMis(serialEntityTermPathMis);
            }

            //15.存储sgns.sougou.bigram向量的表名
            String sgnsSougouBigramTableName = prop.getProperty("sgns.sougou.bigram");
            if(sgnsSougouBigramTableName != null){
                paramSetting.setSgnsSougouBigramTableName(sgnsSougouBigramTableName);
            }

            //16.存储词向量的数据库的连接地址
            String driverVectorUrl = prop.getProperty("driverVectorUrl");
            if(driverVectorUrl != null){
                paramSetting.setDriverVectorUrl(driverVectorUrl);
            }

            //17.序列化文件的根目录
            String serialRootPath = prop.getProperty("serialRootPath");
            if(serialRootPath != null){
                paramSetting.setSerialRootPath(serialRootPath);
            }

            //18.训练集的Excel文件
            String trainDataExcelPath = prop.getProperty("trainDataExcelPath");
            if(trainDataExcelPath != null){
                paramSetting.setTrainDataExcelPath(trainDataExcelPath);
            }

            //19.测试集的Excel文件
            String testDataExcelPath = prop.getProperty("testDataExcelPath");
            if(testDataExcelPath != null){
                paramSetting.setTestDataExcelPath(testDataExcelPath);
            }

            //20.训练集的表名
            String trainDataTableName = prop.getProperty("trainDataTableName");
            if(trainDataTableName != null){
                paramSetting.setTrainDataTableName(trainDataTableName);
            }

            //21.训练集的表名
            String testDataTableName = prop.getProperty("testDataTableName");
            if(testDataTableName != null){
                paramSetting.setTestDataTableName(testDataTableName);
            }

            hasLoad = true;

        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("没有找到配置文件，加载默认设置。");
        } catch (IOException e) {

        }
        return paramSetting;
    }


    //mysql连接信息
    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverUrl() {
        return driverUrl;
    }

    public void setDriverUrl(String driverUrl) {
        this.driverUrl = driverUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    //存放关键词的文件路径信息
    public String getComKeywordsFilePath() {
        return comKeywordsFilePath;
    }

    public void setComKeywordsFilePath(String comKeywordsFilePath) {
        this.comKeywordsFilePath = comKeywordsFilePath;
    }

    public String getMisKeywordsFilePath() {
        return misKeywordsFilePath;
    }

    public void setMisKeywordsFilePath(String misKeywordsFilePath) {
        this.misKeywordsFilePath = misKeywordsFilePath;
    }


    //HTML路径信息
    public String getOutputDirPath() {
        return outputDirPath;
    }

    public void setOutputDirPath(String outputDirPath) { this.outputDirPath = outputDirPath; }

    public String getOutputDirPathMis() {return outputDirPathMis; }

    public void setOutputDirPathMis(String outputDirPathMis) { this.outputDirPathMis = outputDirPathMis; }
    //url路径信息
    public String getUrlDirPath() {return urlDirPath;}

    public void setUrlDirPath(String urlDirPath) {this.urlDirPath = urlDirPath;}

    public String getUrlDirPathMis() {return urlDirPathMis; }

    public void setUrlDirPathMis(String urlDirPathMis) {this.urlDirPathMis = urlDirPathMis;}
    //content路径
    public String getContentPath() {return contentPath;}

    public void setContentPath(String contentPath) {this.contentPath = contentPath;}

    public String getContentPathMis() {return contentPathMis; }

    public void setContentPathMis(String contentPathMis) {this.contentPathMis = contentPathMis;}

    //序列化文件
    public String getSerialEntityTermPath() { return serialEntityTermPath; }

    public void setSerialEntityTermPath(String serialEntityTermPath) { this.serialEntityTermPath = serialEntityTermPath; }

    public String getSerialEntityTermPathMis() { return serialEntityTermPathMis; }

    public void setSerialEntityTermPathMis(String serialEntityTermPathMis) { this.serialEntityTermPathMis = serialEntityTermPathMis; }

    public String getSerialRootPath() {
        return serialRootPath;
    }

    public void setSerialRootPath(String serialRootPath) {
        this.serialRootPath = serialRootPath;
    }

    public String getTrainDataExcelPath() {
        return trainDataExcelPath;
    }

    public void setTrainDataExcelPath(String trainDataExcelPath) {
        this.trainDataExcelPath = trainDataExcelPath;
    }

    public String getTestDataExcelPath() {
        return testDataExcelPath;
    }

    public void setTestDataExcelPath(String testDataExcelPath) {
        this.testDataExcelPath = testDataExcelPath;
    }


    //中文词向量与特征向量
    public String getDriverVectorUrl() {
        return driverVectorUrl;
    }

    public void setDriverVectorUrl(String driverVectorUrl) {
        this.driverVectorUrl = driverVectorUrl;
    }

    public String getSgnsSougouBigramTableName() {
        return sgnsSougouBigramTableName;
    }

    public void setSgnsSougouBigramTableName(String sgnsSougouBigramTableName) {
        this.sgnsSougouBigramTableName = sgnsSougouBigramTableName;
    }

    public String getTrainDataTableName() {
        return trainDataTableName;
    }

    public void setTrainDataTableName(String trainDataTableName) {
        this.trainDataTableName = trainDataTableName;
    }

    public String getTestDataTableName() {
        return testDataTableName;
    }

    public void setTestDataTableName(String testDataTableName) {
        this.testDataTableName = testDataTableName;
    }

    @Override
    public String toString() {
        return "PropertiesSettings{" +
                "comKeywordsFilePath='" + comKeywordsFilePath + '\'' +
                "misKeywordsFilePath='" + misKeywordsFilePath + '\'' +
                ", outputDirPath='" + outputDirPath + '\'' +
                '}';
    }
}
