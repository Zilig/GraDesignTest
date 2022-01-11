package completion.FromKeywordToHTML;

import com.hankcs.hanlp.HanLP;

import java.io.IOException;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/5/18 18:24
 **/
public class Test {
    public static void main(String[] args) throws IOException {
//        String url = "http://www.baidu.com/link?url=qiYqdntZ949OUAmbAgZzzj43mUmIThb2qWHEevJTFl9mg1NMdJ_IoAAQ2Z8M4hVo";
//        String strHTML = HTMLTools.getHTML(url);
//
        // 设置webdriver，进入搜索页面
//        String outputDirPath = "./src/main/resources/HTMLs";
//        String filename = "0000001110001001_905 汪科 中国城市规划设计研究院                  .txt";
//        String directoryPath = outputDirPath + "/" + filename.substring(0, filename.lastIndexOf("."));
//        int i = 100;
//        String htmlFilename = directoryPath + "/" + i + ".txt";
//        System.out.println("htmlFilename = " + htmlFilename);
//        try{
//            File htmlFile = new File(htmlFilename);
//            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(htmlFile), "utf-8"));
//            writer.write("test");
//            writer.close();
//            //Files.write(Paths.get(htmlFilename), strHTML.getBytes(StandardCharsets.UTF_8));
//        }
//        catch(Exception exception){
//            System.out.println("failed to write this html. keywords=" + filename + ", index=" + i);
//            System.out.println("exception = " + exception);
//        }
        //TxtTools.findCharsetTypeOnDirectory();
//        HTMLTools.findIllegalURLFilename();
//        System.out.println("\n**************************\n");
//        HTMLTools.findIllegalHTMLFilename();
        HanLP.Config.enableDebug();         // 首次运行会自动建立模型缓存，为了避免你等得无聊，开启调试模式说点什么:-)
        System.out.println(HanLP.segment("王国维和服务员一块吃了饭"));

    }

}
