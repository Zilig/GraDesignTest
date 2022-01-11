package completion.FromKeywordToHTML;

import completion.Tools.FileTools;
import completion.Tools.HTMLTools;
import completion.Tools.ParamSetting;
import completion.Tools.SoftParameters;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/5/18 11:14
 **/
public class GetHTMLs {
    public static void main(String[] args) throws FileNotFoundException {
        //共5个参数需要配置
        ParamSetting prop = ParamSetting.getParams();
        //存储百度到的网页的url文件的目录，每行关键词会创建一个文件，一行是一个url，存在空行，即无效url
        String urlDirPath = prop.getUrlDirPath();
        //存储下载的搜索结果文件的目录的路径，每行关键词会创建一个文件夹，文件夹下是该关键词对应的多个html网页
        String outputDirPath = prop.getOutputDirPath();
        //保存要下载的网页的数目，无效网页保存为空文件。
        int resultSize = SoftParameters.ResultSize;

        File urlDirectory = new File(urlDirPath);
        if(urlDirectory.isDirectory()){
            File[] urlTxts=urlDirectory.listFiles();//注意:这里只能用listFiles()，不能使用list()
            //assert urlTxts != null;
            //for(File urlTxt : urlTxts){
            //要读取文件的开始范围与结束范围
            int begIndex = 1022, endIndex = 1023;
            for(int txtIndex=begIndex; txtIndex<endIndex; txtIndex++){
                //File[] urlTxts=urlDirectory.listFiles();//注意:这里只能用listFiles()，不能使用list()
                //读取urlTxt中的url
                assert urlTxts != null;
                File urlTxt = urlTxts[txtIndex];
                String filename = urlTxt.getName();
                System.out.println("filename = " + filename);
                //从保存url的文件中读取出所有的url
                List<String> urlsList = FileTools.readFileToList(urlTxt, "utf-8");
                //如果url数目不足50，输出一下。
                if(urlsList.size() != resultSize){
                    System.out.println("The count of this urlTxt is less than 50:" + filename);
                    //continue;
                }
                //获取50个url对应的网页，并写入txt
                String directoryPath = outputDirPath + "/" + filename.substring(0, filename.lastIndexOf("."));
                //首先新建一个文件夹
                File keyWordsDirectory = new File(directoryPath);
                if(!keyWordsDirectory.exists()){
                    keyWordsDirectory.mkdirs();
                }
                //在文件夹下，写入这些文件
                //for(int i=10; i<urlsList.size(); i++){
                for(int i=0; i<urlsList.size(); i++){
                    String nowURL = urlsList.get(i);
                    System.out.println(i + ", nowURL=" + nowURL);
                    try {
                        Thread.sleep(500);     //让线程等待0.5秒钟
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //获取网页内容和网页编码
                    Map.Entry<String, String> htmlEntry = HTMLTools.getHTML(nowURL);
                    String strHTML = htmlEntry.getKey();
                    String contentType = htmlEntry.getValue();
                    //System.out.println("html.length = "+strHTML.length() + ", html.charset = " + contentType);
                    //全路径文件名
                    String htmlFilename = directoryPath + "/" + i + ".txt";
                    //将网页内容写入指定路径的txt
                    try{
                        File htmlFile = new File(htmlFilename);
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(htmlFile), contentType));
                        writer.write(strHTML);
                        writer.close();
                        //Files.write(Paths.get(htmlFilename), strHTML.getBytes(StandardCharsets.UTF_8));
                    }
                    catch(Exception exception){
                        System.out.println("failed to write this html. keywords=" + filename + ", index=" + i);
                        System.out.println("exception = " + exception);
                    }
                }
            }
        }
    }

}
