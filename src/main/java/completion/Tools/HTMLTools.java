package completion.Tools;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/5/19 20:46
 **/
public class HTMLTools {
    //找出URLs文件夹下所有的不合法文件名
    public static void findIllegalURLFilename(){
        ParamSetting prop = ParamSetting.getParams();
        //存储百度到的网页的url文件的目录，每行关键词会创建一个文件，一行是一个url，存在空行，即无效url
        String urlDirPath = prop.getUrlDirPath();
        //存储下载的搜索结果文件的目录的路径，每行关键词会创建一个文件夹，文件夹下是该关键词对应的多个html网页
        File urlDirectory = new File(urlDirPath);
        if(urlDirectory.isDirectory()){
            File[] urlTxts=urlDirectory.listFiles();//注意:这里只能用listFiles()，不能使用list()
            for(File txt : urlTxts){
                String filename = txt.getName();
                filename = filename.substring(0, filename.lastIndexOf("."));
                if(filename.contains("#") || filename.charAt(filename.length()-1) == ' '){
                    System.out.println("filename = " + filename);
                }
            }
        }
    }

    //找出HTMLs文件夹下所有的不合法文件名
    public static void findIllegalHTMLFilename(){
        ParamSetting prop = ParamSetting.getParams();
        //存储百度到的网页的url文件的目录，每行关键词会创建一个文件，一行是一个url，存在空行，即无效url
        String htmlDirPath = prop.getOutputDirPath();
        //存储下载的搜索结果文件的目录的路径，每行关键词会创建一个文件夹，文件夹下是该关键词对应的多个html网页
        File urlDirectory = new File(htmlDirPath);
        if(urlDirectory.isDirectory()){
            File[] htmlTxts=urlDirectory.listFiles();//注意:这里只能用listFiles()，不能使用list()
            for(File txt : htmlTxts){
                String filename = txt.getName();
                //filename = filename.substring(0, filename.lastIndexOf("."));
                String[] values = filename.split(" ");
                if(values.length != 3){
                    System.out.println(filename + ", error length");
                }
                else{
                    String expert = values[1];
                    String institution = values[2];
                    if(expert.length() < 2 || expert.length() > 5){
                        System.out.println(filename  + ", error expert name");
                    }
                    if(institution.length() < 3){
                        System.out.println(filename + ", error institution name");
                    }
                    String[] illegalDigits = {"（", "）", "。","，","、","；","？","！","#",".",",","\\",";","?","!"};
                    //String[] illegalDigits = {"。","，","、","；","？","！","#",".",",","\\",";","?","!"};
                    for (String illegalDigit : illegalDigits) {
                        if(expert.contains(illegalDigit)){
                            System.out.println(filename + ", error expert name");
                        }
                        if(institution.contains(illegalDigit)){
                            System.out.println(filename + ", error institution name");
                        }
                    }

                }

            }
        }
    }

    //根据url读取网页，返回为一个String
    @Deprecated
    public static String getHTML2(String url){
        if(url.length() < 5) return "";
        //1.生成httpclient，相当于该打开一个浏览器
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        //2.创建get请求，相当于在浏览器地址栏输入 网址
        HttpGet request = new HttpGet(url);
        String html = "";
        try {
            //3.执行get请求，相当于在输入地址栏后敲回车键
            response = httpClient.execute(request);

            //4.判断响应状态为200，进行处理
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //5.获取响应内容
                HttpEntity httpEntity = response.getEntity();
                //html = EntityUtils.toString(httpEntity, "utf-8");
                //html = EntityUtils.toString(httpEntity, "gbk");
                html = EntityUtils.toString(httpEntity, "gb2312");
            } else {
                //如果返回状态不是200，比如404（页面不存在）等，根据情况做处理，这里略
                System.out.println("\n返回状态不是200");
                System.out.println("url=" + url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //6.关闭
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
        }
        return html;
    }

    //根据url读取网页，返回为一个String，根据网页编码不同，返回不同编码的String
    public static Map.Entry<String, String> getHTML(String url){
        String html = "";
        String charsetType = "utf-8";
        //空URL，直接返回默认值
        if(url.length() < 5) return Map.entry(html, charsetType);
        //1.生成httpclient，相当于该打开一个浏览器
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(1500).setConnectTimeout(1500).build();
        CloseableHttpResponse response = null;
        //2.创建get请求，相当于在浏览器地址栏输入 网址
        HttpGet request = new HttpGet(url);
        request.setConfig(requestConfig);
        try {
            //3.执行get请求，相当于在输入地址栏后敲回车键
            response = httpClient.execute(request);
            //4.判断响应状态为200，进行处理
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //5.获取响应内容
                HttpEntity httpEntity = response.getEntity();
                //非常重要，使httpEntity可以重复使用！
                httpEntity = new BufferedHttpEntity(httpEntity);
                //接收网页内容，用以判断网页编码类型
                String iniHtml = EntityUtils.toString(httpEntity, "utf-8");
                //根据网页内容判断编码
                charsetType = FileTools.findCharsetTypeOfFileStr(iniHtml);
                if(charsetType.equals("unknown")) charsetType = "utf-8";
                //根据编码生成真正可用的String网页
                html = EntityUtils.toString(httpEntity, charsetType);
            } else {
                //如果返回状态不是200，比如404（页面不存在）等，根据情况做处理，这里略
                System.out.println("\n返回状态不是200，statusCode=" + response.getStatusLine().getStatusCode());
                System.out.println("url=" + url);
            }
        } catch (IOException e) {
            System.out.println(e.toString());
            //e.printStackTrace();
        } finally {
            //6.关闭
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
        }
        return Map.entry(html, charsetType);
    }
}
