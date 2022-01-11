package completion.FromHTMLToContent;

import completion.Tools.FileTools;
import completion.Tools.JdbcUtils;
import completion.Tools.ParamSetting;
import completion.Tools.SoftParameters;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/7/16 16:24
 **/


public class ExtractHtmlInformation {
    private static class EntityInforInFile{
        String firstDicName;    //第一级目录
        String secondDicName;   //第二级目录
        String filename;        //文件名
        String charset;         //文件编码{"utf-8", "utf-16", "gbk", "gb2312", "unknown"};
        int length;             //文件长度
        int lenType;            //长度类型，太长为1，太短为-1，其他为0。1和-1均为无效文件，剔除
        int headTime;           //头实体出现次数
        int tailTime;           //尾实体出现次数
        boolean headAndTail;    //头尾实体是否都存在

        EntityInforInFile(String dic1, String dic2, String name, int headT, int tailT){
            firstDicName = dic1;
            secondDicName = dic2;
            filename = name;
            headTime = headT;
            tailTime = tailT;
            if(headT > 0 && tailT > 0) headAndTail = true;
        }

        EntityInforInFile(String dic1, String dic2, String name, String charset, int length, int lenType, int headT, int tailT){
            firstDicName = dic1;
            secondDicName = dic2;
            filename = name;
            this.charset = charset;
            this.length = length;
            this.lenType = lenType;
            headTime = headT;
            tailTime = tailT;
            if(headT > 0 && tailT > 0) headAndTail = true;
        }

        @Override
        public String toString(){
            return firstDicName + "/" + secondDicName + "/" + filename + ": (" + headTime + ", " + tailTime + ", " + headAndTail + ")";
        }

        public void printInfor(){
            System.out.print(firstDicName + "/" + secondDicName + "/" + filename + ":");
            System.out.print("charset=" + charset + ", length=" + length + ", lenType=" + lenType + ",  ");
            System.out.println(" (" + headTime + ", " + tailTime + ", " + headAndTail + ")");
        }
    }

    private ArrayList<EntityInforInFile> findEntityInfor(String dicPath, int beginIndex, int endIndex){
        ArrayList<EntityInforInFile> results = new ArrayList<>();

        File dicDir = new File(dicPath);
        File[] fileDirs = dicDir.listFiles();//注意:这里只能用listFiles()，不能使用list()
        for(int i=beginIndex; i<endIndex; i++) {
            if (fileDirs[i].isDirectory()) {
                String dicName = fileDirs[i].getName(); //二级文件目录名称，包含了头尾实体名称
                String[] repInfor = dicName.split(" ");
                String headName = repInfor[1];
                String tailName = repInfor[2];

                File[] files = fileDirs[i].listFiles();

                for (int j = 0; j < files.length; j++) {
                    File file = files[j];
                    String fileName = file.getName();
                    int fileLen = (int) file.length();
                    int fileLenType = 0;
                    String charset = "unknown";
                    int headTime = 0, tailTime = 0;
                    if(fileLen > SoftParameters.MaxLegalFileLen) fileLenType = 1;
                    else if(fileLen < SoftParameters.MinLegalFileLen) fileLenType = -1;
                    else{
                        charset = FileTools.findCharsetTypeOfFile(file);
                        String fileStr = FileTools.readFileToStrWithNewLine(file, charset);
                        headTime = entityTimeInFile(fileStr, headName);
                        tailTime = entityTimeInFile(fileStr, tailName);
                    }

                    EntityInforInFile infor = new EntityInforInFile(dicPath,dicName, fileName, charset, fileLen, fileLenType, headTime, tailTime);
                    results.add(infor);
                }
            }
        }
        return results;
    }


    //此处不必使用KMP算法实现，因为出现的次数很少，而且长度也小。KMP的改进不大
    private int entityTimeInFile(String fileStr, String entityName){
        int count = 0;
        int index = 0;
        while ((index = fileStr.indexOf(entityName, index)) != -1) {
            index = index + entityName.length();
            count++;
        }
        return count;
    }

    //将文件的统计信息写入数据库
    private void writeInforToDB(ArrayList<EntityInforInFile> inforList, String sql){
        Connection conn;
        PreparedStatement stmt;
        ParamSetting prop = ParamSetting.getParams();
        String driver = prop.getDriverName();

        try {
            Class.forName(driver);
            conn = JdbcUtils.getConn();
            for(int i=0; i<inforList.size(); i++){
                EntityInforInFile infor = inforList.get(i);
                stmt = (PreparedStatement) conn.prepareStatement(sql);
                stmt.setString(1, infor.firstDicName);
                stmt.setString(2, infor.secondDicName);
                stmt.setString(3, infor.filename);
                stmt.setString(4, infor.charset);
                stmt.setInt(5,infor.length);
                stmt.setInt(6,infor.lenType);
                stmt.setInt(7,infor.headTime);
                stmt.setInt(8,infor.tailTime);
                stmt.setBoolean(9, infor.headAndTail);
                stmt.executeUpdate();
                if(i % 50 == 0){
                    System.out.println("the number of writing file information:" + i);
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
    }


    public void runHTML(int begin, int end){
        ParamSetting paramSetting = ParamSetting.getParams();
        String dicPath = paramSetting.getOutputDirPath();
        ArrayList<EntityInforInFile> results = findEntityInfor(dicPath, begin, end);
        String sql = "insert into htmlInfor values (?,?,?,?,?,?,?,?,?)";
        writeInforToDB(results, sql);
    }
}
