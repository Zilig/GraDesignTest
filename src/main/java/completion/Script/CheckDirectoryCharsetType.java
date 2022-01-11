package completion.Script;

import completion.Tools.FileTools;
import completion.Tools.ParamSetting;

import java.io.File;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/9/3 10:11
 **/
public class CheckDirectoryCharsetType {
    //查找文件夹下的哪些网页编码不是utf-8
    private static void findCharsetTypeOnDirectory(){
        //共5个参数需要配置
        ParamSetting prop = ParamSetting.getParams();
        //存储下载的搜索结果文件的目录的路径，每行关键词会创建一个文件夹，文件夹下是该关键词对应的多个html网页
        String outputDirPath = prop.getOutputDirPath();
        //遍历所有关键词的文件夹
        File[] htmlDirectories = new File(outputDirPath).listFiles();
        assert htmlDirectories != null;
        for(File directory : htmlDirectories){
            if(directory.isDirectory()){
                System.out.println("\ndirectory name = " + directory.getName());
                File[] htmlFiles = directory.listFiles();
                assert htmlFiles != null;
                for(File html : htmlFiles){
                    //String htmlStr = FileTools.readFileToStr(html);
                    String charsetType = FileTools.findCharsetTypeOfFile(html);
                    if(!(charsetType.equals("utf") || charsetType.equals("utf-8") || charsetType.equals("UTF-8"))){
                        System.out.print("html name = " + html.getName());
                        System.out.println("  " + charsetType);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        findCharsetTypeOnDirectory();
    }
}
