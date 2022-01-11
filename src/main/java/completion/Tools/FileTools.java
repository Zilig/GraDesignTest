package completion.Tools;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/5/19 19:47
 **/
public class FileTools {
    //***********************************读类型的函数******************************************************************

    //读取一个txt，将内容返回为一个String，包括换行符“\n”。charsetType可能为unknown
    public static String readFileToStrWithNewLine(File file, String charsetType){
        String filename = file.getName();
        StringBuilder newBuffer = new StringBuilder();
        //分行读取url
        try {
            if(charsetType.equals("unknown")) charsetType = "utf-8";
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file),charsetType));
            String lineStr = "";
            while ((lineStr = bf.readLine()) != null) {
                newBuffer.append(lineStr);
                newBuffer.append("\n");
            }
            bf.close();
        } //如果文件读取时出现错误，报错，读取下一个文件
        catch(Exception exception){
            System.out.println("failed to read this file:" + filename);
            System.out.println("exception = " + exception);
            return "";
        }
        return newBuffer.toString();
    }

    //读取一个txt，将内容返回为一个String，包括换行符“\n”。charsetType可能为unknown
    public static String readFileToStrWithNewLine(File file){
        String filename = file.getName();
        StringBuilder newBuffer = new StringBuilder();
        //分行读取url
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String lineStr = "";
            while ((lineStr = bf.readLine()) != null) {
                newBuffer.append(lineStr);
                newBuffer.append("\n");
            }
            bf.close();
        } //如果文件读取时出现错误，报错，读取下一个文件
        catch(Exception exception){
            System.out.println("failed to read this file:" + filename);
            System.out.println("exception = " + exception);
            return "";
        }
        return newBuffer.toString();
    }

    //读取一个txt，将内容返回为一个String，不包括换行符“\n”。charsetType可能为unknown
    public static String readFileToStr(File file, String charsetType){
        String filename = file.getName();
        StringBuilder newBuffer = new StringBuilder();
        //分行读取url
        try {
            if(charsetType.equals("unknown")) charsetType = "utf-8";
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file),charsetType));
            String lineStr = "";
            while ((lineStr = bf.readLine()) != null) {
                newBuffer.append(lineStr);
            }
            bf.close();
        } //如果文件读取时出现错误，报错，读取下一个文件
        catch(Exception exception){
            System.out.println("failed to read this file:" + filename);
            System.out.println("exception = " + exception);
            return "";
        }
        return newBuffer.toString();
    }

    //读取一个txt，将内容返回为一个List<String>，每一个String是文本的一行。charsetType可能为unknown
    public static List<String> readFileToList(File file, String charsetType){
        String filename = file.getName();
        List<String> lineList = new ArrayList<>();
        //分行读取url
        try {
            if(charsetType.equals("unknown")) charsetType = "utf-8";
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file),charsetType));
            String lineStr = "";
            while ((lineStr = bf.readLine()) != null) {
                lineList.add(lineStr);
            }
            bf.close();
        } //如果文件读取时出现错误，报错，读取下一个文件
        catch(Exception exception){
            System.out.println("failed to read this file:" + filename);
            System.out.println("exception = " + exception);
        }
        return lineList;
    }

    //从dat文件中读取obj
    public static Object readObjFromDat(File datFile){
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(datFile));
            Object obj = ois.readObject();
            ois.close();
            return obj;
        } catch(IOException | ClassNotFoundException ioAndClassNotFountException){
            System.out.println("failed to read a obj from file");
            ioAndClassNotFountException.printStackTrace();
            return null;
        }
    }

    public static Object readObjFromDat(String datFileName){
        File datFile = new File(datFileName);
        return readObjFromDat(datFile);
    }

    //***********************************查类型的函数******************************************************************

    //判断一个文件的编码类型，可能为未知类型unknown
    public static String findCharsetTypeOfFile(File file){
        StringBuilder oldBuffer = new StringBuilder();
        //分行读取文件
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String lineStr = "";
            while ((lineStr = bf.readLine()) != null) {
                oldBuffer.append(lineStr);
            }
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileStr = oldBuffer.toString();
        return findCharsetTypeOfFileStr(fileStr);
    }


    //判断一个文件字符串的编码类型,包括unknown
    public static String findCharsetTypeOfFileStr(String strHTML){
        String[] normalTypes = SoftParameters.LegalFileCharset;
        int charIndex = strHTML.indexOf("charset");
        if(charIndex == -1){
            //System.out.println("   the charset type is unknown, please be careful of the subsequent processing");
            return "unknown";
        }

        int endIndex = strHTML.indexOf(">", charIndex);
        String filed = strHTML.substring(charIndex, endIndex);
        for(int i=0; i<normalTypes.length; i+=2){
            if(filed.contains(normalTypes[i]) || filed.contains(normalTypes[i+1])){
                return normalTypes[i+1];
            }
        }
        return "unknown";
    }


    //***********************************写类型的函数******************************************************************
    //将一个文件清空
    public static boolean clearFile(File file) {
        String fileName = file.getName();
        try {
            if(!file.exists()) {
                System.out.println("can't clear this file, error fileName:" + fileName);
                return false;
            }
            FileWriter fileWriter =new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    //将一个String写入一个txt，返回是否写入成功
    public static boolean writeStrToFile(String str, String filename){
        try{
            File file = new File(filename);
            return writeStrToFile(str,file);
        }
        catch(Exception exception){
            System.out.println("failed to write this file:" + filename);
            System.out.println("exception = " + exception);
            return false;
        }
    }

    //将一个String写入一个txt，返回是否写入成功
    public static boolean writeStrToFile(String str, File file){
        try{
            BufferedWriter bw = new BufferedWriter((new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)));
            bw.write(str);
            bw.close();
            return true;
        }
        catch(Exception exception){
            System.out.println("failed to write this file:" + file.getName());
            System.out.println("exception = " + exception);
            return false;
        }
    }

    //将一个类obj序列化到datFile中
    public static void serializeObjToDat(File datFile, Object obj) {
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(datFile));
            oos.writeObject(obj);
            oos.close();
        } catch (IOException ioException){
            System.out.println("failed to write a obj to file");
            ioException.printStackTrace();
        }
    }

    public static void serializeObjToDat(String datFileName, Object obj) {
        File datFile = new File(datFileName);
        serializeObjToDat(datFile, obj);
    }

}
