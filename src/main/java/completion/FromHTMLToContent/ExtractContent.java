package completion.FromHTMLToContent;

import completion.Tools.FileTools;
import completion.Tools.ParamSetting;
import completion.Tools.SoftParameters;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/6/2 21:22
 **/
public class ExtractContent {
    /**
     * 读取网页全部内容
     */

    /**
     *
     * @param s
     * @return 获得网页标题
     */
    public String getTitle(String s) {
        String regex;
        String title = "";
        List<String> list = new ArrayList<String>();
        regex = "<title>.*?</title>";
        Pattern pa = Pattern.compile(regex, Pattern.CANON_EQ);
        Matcher ma = pa.matcher(s);
        while (ma.find()) {
            list.add(ma.group());
        }
        for (int i = 0; i < list.size(); i++) {
            title = title + list.get(i);
        }
        title = title.replaceAll("<.*?>", "");
        return title;
    }


    /**
     *
     * @param s
     * @return 获得链接
     */
    public List<String> getLink(String s) {
        String regex;
        List<String> list = new ArrayList<String>();
        regex = "<a[^>]*href=(\"([^\"]*)\"|\'([^\']*)\'|([^\\s>]*))[^>]*>(.*?)</a>";
        Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
        Matcher ma = pa.matcher(s);
        while (ma.find()) {
            list.add(ma.group());
        }
        return list;
    }

    public List<String> getNews(String s) {
        String regex = "<a.*?</a>";
        Pattern pa = Pattern.compile(regex, Pattern.DOTALL);
        Matcher ma = pa.matcher(s);
        List<String> list = new ArrayList<String>();
        while (ma.find()) {
            list.add(ma.group());
        }
        return list;
    }

    /**
     *
     * @param content html文本
     * @return 删除标签
     */
    public String deleteTag(String content) {
        // <p>段落替换为换行
        content = content.replaceAll("<p .*?>", "\n");
        // <br><br/>替换为换行
        content = content.replaceAll("<br\\s*/?>", "\n");
        // 去掉其它的<>之间的东西
        //content = content.replaceAll("\\<.*?>", "");
        // 还原HTML
        // content = HTMLDecoder.decode(content);
        return content;
    }



//    /**
//     * 去除html标签
//     * @param html 请求获得的html文本
//     * @return 纯文本
//     */
//    public static String deleteLabel(String html){
//        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
//        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
//        String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
//        String regEx_anno = "<!--[\\s\\S]*?-->"; //html注释
//        html = html.replaceAll(regEx_script, "");
//        html = html.replaceAll(regEx_style, "");
//        html = html.replaceAll(regEx_html, "");
//        html = html.replace(regEx_anno, "");
//        html = html.replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1").replaceAll("^((\r\n)|\n)", "");//去除空白行
//        html = html.replaceAll("    +| +|　+", ""); //去除空白
//        return html.trim();
//    }

    /**
     * 去除html标签
     * @param html 请求获得的html文本
     *             剔除script、style、注释
     * @return 纯文本
     */
    public String deleteLabel(String html){
        String regEx_anno = "<!--[\\s\\S]*?-->"; //html注释
        String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
        String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
        String regEx_img = "<p><em>.*?</em></p>";   // 去图片注释
        html = html.replace(regEx_anno, "\n");
        html = html.replaceAll(regEx_script, "\n");
        html = html.replaceAll(regEx_style, "\n");
        html = html.replaceAll(regEx_img, "\n");

        //String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
        //html = html.replaceAll(regEx_html, "\n");
        //html = html.replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "\n").replaceAll("^((\r\n)|\n)", "\n");//去除空白行
        //html = html.replaceAll("\\s*|\t", "");
        //html = html.replaceAll("    +| +|　+|\\s*|\t", ""); //去除空白与制表符
        //html = html.replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "\n");//去除空白行
        return html.trim();
    }

    //剔除网页中的无效标签，这些标签不应占用位置
    public String deleteUnuse(String html){
        String[] unuseTag = {"<strong>","</strong>","<b>","</b>", "&nbsp;", "&nbsp", "\t"};
        for (String s : unuseTag) {
            html = html.replaceAll(s, " ");
        }
        return html;
    }

    //剔除网页中的<>标签，替换为相同长度的空格
    public String deleteAngleBracket1(String html){
        StringBuilder htmlBuilder = new StringBuilder(html);
        int length = htmlBuilder.length();
        int beginIndex = 0;
        //记录左括号与右括号的出现位置
        var leftIndex = new ArrayList<Integer>();

        while(beginIndex < length){
            char nowChar = htmlBuilder.charAt(beginIndex);
            if(nowChar == '<'){
                leftIndex.add(beginIndex);
            }
            else if(nowChar == '>'){
                int leftSize = leftIndex.size();
                if(leftSize > 0){
                    int deleteBegin = leftIndex.get(leftSize-1);
                    for(int i=deleteBegin; i<=beginIndex; i++)
                        htmlBuilder.replace(i, i+1, " ");
                    leftIndex.remove(leftSize-1);
                }
            }
            beginIndex++;
        }
        return htmlBuilder.toString();
    }

    //剔除网页中的<>标签，替换为相同长度的空格
    public String deleteAngleBracket(String html){
        StringBuilder htmlBuilder = new StringBuilder(html);
        int length = htmlBuilder.length();
        int beginIndex = 0;
        //记录左括号与右括号的出现次数
        int leftBracket = 0, rightBracket = 0;
        //记录左括号与右括号的出现位置
        var leftIndex = new ArrayList<Integer>();
        var rightIndex = new ArrayList<Integer>();
        while(beginIndex < length){
            char nowChar = htmlBuilder.charAt(beginIndex);
            if(nowChar == '<'){
                leftBracket++;
                leftIndex.add(beginIndex);
            }
            else if(nowChar == '>'){
                rightBracket++;
                rightIndex.add(beginIndex);
            }
            //右括号数目比左括号还多，说明网页有问题，跳过这个右括号
            if(rightBracket > leftBracket){
                System.out.println("\ncount of '>' is more than count of '<',  skip this '>'.");
                System.out.println("now index is:" + beginIndex);
                rightBracket--;
                rightIndex.remove(rightIndex.size()-1);
                //continue;
            }
            else if(rightBracket == leftBracket && rightBracket > 0){
                //替换掉这一对<>
                int deleteBegin = leftIndex.get(0);
                int deleteEnd = rightIndex.get(0);
                for(int i=deleteBegin; i<=deleteEnd; i++)
                    htmlBuilder.replace(i, i+1, " ");
                //修改长度，修改index
                leftBracket = 0;
                rightBracket = 0;
                leftIndex.clear();
                rightIndex.clear();
            }
            beginIndex++;
        }

        return htmlBuilder.toString();
    }

    private String handleSpace(String html){
        String[] lineArr = html.split("\n");
        StringBuilder htmlSb = new StringBuilder();
        for(int i=0; i<lineArr.length; i++){
            int nowLen = lineArr[i].length();
            for(int j=0; j<nowLen-1; j++){
                if(lineArr[i].charAt(j) != ' '){
                    htmlSb.append(lineArr[i].charAt(j));
                }
                else {
                    if(lineArr[i].charAt(j+1) != ' ')
                        htmlSb.append(' ');
                }
            }
            if(nowLen>0 && lineArr[i].charAt(nowLen-1) != ' ')
                htmlSb.append(lineArr[i].charAt(nowLen-1));
            htmlSb.append("\n");
        }
        return htmlSb.toString();
    }


    //在此处运行，减少Test.main的工作量
    public void run(int beginIndex, int endIndex){
        ParamSetting prop = ParamSetting.getParams();
        //存储百度到的网页的url文件的目录，每行关键词会创建一个文件，一行是一个url，存在空行，即无效url
        String contentPath = prop.getContentPath();
        File contentDir = new File(contentPath);
        File[] contentDirs = contentDir.listFiles();//注意:这里只能用listFiles()，不能使用list()
        int count = 0;
        for(int i=beginIndex; i<endIndex; i++){
            if(contentDirs[i].isDirectory()){
                File[] contentFiles = contentDirs[i].listFiles();
                for(int j=0; j<contentFiles.length; j++){
                    File contentFile = contentFiles[j];
                    String finalContent;
                    if(contentFile.length() < SoftParameters.MaxLegalFileLen){
                        String charset = FileTools.findCharsetTypeOfFile(contentFile);
                        String htmlStr = FileTools.readFileToStrWithNewLine(contentFile,charset);
                        String[] lineList = htmlStr.split("\n");
                        //System.out.println("lineList.length = " + lineList.length);
                        //在此处提取文本内容，也是本程序的核心，顺序不可更改，处处都是细节。
                        /*******************************************************************/
                        //删除script，注释，CSS等内容
                        String content = deleteLabel(htmlStr);
                        //剔除网页中的无效标签，这些标签不应占用位置，如<p>，<strong>等
                        content = deleteUnuse(content);
                        //剩余的标签替换为相同长度的空格
                        content = deleteAngleBracket1(content);
                        //下一步应该剔除空格，合并多余的空格。
                        finalContent = handleSpace(content);
                        /*******************************************************************/
                    }
                    else{
                        finalContent = "";
                    }

                    FileTools.clearFile(contentFile);
                    FileTools.writeStrToFile(finalContent, contentFile);
                    count++;
                    if(count % 50 == 0){
                        System.out.println("the number of writing content:" + count);
                    }
                }
            }
        }
    }

    //在此处进行测试
    public void runTest(){
        String[] srcFilenameList = {"D:\\Java\\Workspace\\GraDesign1\\src\\main\\resources\\test\\source_0.txt",
                "D:\\Java\\Workspace\\GraDesign1\\src\\main\\resources\\test\\source_1.txt",
                "D:\\Java\\Workspace\\GraDesign1\\src\\main\\resources\\test\\source_2.txt",
                "D:\\Java\\Workspace\\GraDesign1\\src\\main\\resources\\test\\source_32.txt"};
        String[] toFilenameList = {"D:\\Java\\Workspace\\GraDesign1\\src\\main\\resources\\test\\0.txt",
                "D:\\Java\\Workspace\\GraDesign1\\src\\main\\resources\\test\\1.txt",
                "D:\\Java\\Workspace\\GraDesign1\\src\\main\\resources\\test\\2.txt",
                "D:\\Java\\Workspace\\GraDesign1\\src\\main\\resources\\test\\32.txt"};
        for(int i=0; i< srcFilenameList.length; i++){
            File fromFile = new File(srcFilenameList[i]);
            String finalContent;
            if(fromFile.length() < 100000){
                String charset = FileTools.findCharsetTypeOfFile(fromFile);
                String htmlStr = FileTools.readFileToStrWithNewLine(fromFile,charset);
                String[] lineList = htmlStr.split("\n");
                System.out.println("lineList.length = " + lineList.length);
                //在此处提取文本内容
                //删除script，注释，CSS等内容
                String content = deleteLabel(htmlStr);
                //剔除网页中的无效标签，这些标签不应占用位置，如<p>，<strong>等
                content = deleteUnuse(content);
                //剩余的标签替换为相同长度的空格
                content = deleteAngleBracket1(content);
                //下一步应该剔除空格，合并多余的空格。
                finalContent = handleSpace(content);
            }
            else{
                finalContent = "";
            }

            //System.out.println("content.substring(0,200) = " + content.substring(0,200));
            //将清理后的content写入文件
            File toFile = new File(toFilenameList[i]);
            FileTools.clearFile(toFile);
            FileTools.writeStrToFile(finalContent, toFile);
        }
    }
}
