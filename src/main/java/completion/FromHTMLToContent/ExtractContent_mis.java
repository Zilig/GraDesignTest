package completion.FromHTMLToContent;

import completion.Tools.FileTools;
import completion.Tools.ParamSetting;
import completion.Tools.SoftParameters;

import java.io.File;
import java.util.ArrayList;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/8/28 17:00
 **/
public class ExtractContent_mis {
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
        String contentPathMis = prop.getContentPathMis();
        File contentDirMis = new File(contentPathMis);
        File[] contentMisDirs = contentDirMis.listFiles();//注意:这里只能用listFiles()，不能使用list()
        int count = 0;
        for(int i=beginIndex; i<endIndex; i++){
            if(contentMisDirs[i].isDirectory()){
                File[] contentMisFiles = contentMisDirs[i].listFiles();
                for(int j=0; j<contentMisFiles.length; j++){
                    File contentFile = contentMisFiles[j];
                    String finalContent;
                    if(contentFile.length() < SoftParameters.MaxLegalFileLen){
                        String charset = FileTools.findCharsetTypeOfFile(contentFile);
                        String htmlStr = FileTools.readFileToStrWithNewLine(contentFile,charset);
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
}
