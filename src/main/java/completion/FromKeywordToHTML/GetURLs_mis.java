package completion.FromKeywordToHTML;

import completion.Tools.ExcelTools;
import completion.Tools.NumberTools;
import completion.Tools.ParamSetting;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/8/23 15:48
 **/
public class GetURLs_mis {
    public static void main(String[] args) throws IOException {
        //读取待百度的关键词列表
        ParamSetting prop = ParamSetting.getParams();
        //1.存储不完整关键词的excel文件的路径
        String misKeywordsFilePath = prop.getMisKeywordsFilePath();
        String urlDirPathMis = prop.getUrlDirPathMis();

        Workbook wb = ExcelTools.readExcel(misKeywordsFilePath);
        int beginLine = (int)ExcelTools.getSheetAt(wb,1).getRow(1).getCell(0).getNumericCellValue();
        int endLine = (int)ExcelTools.getSheetAt(wb,1).getRow(1).getCell(1).getNumericCellValue();
        Sheet sheet = ExcelTools.getSheetAt(wb, 0);

        List<Integer> indexList = new ArrayList<>();    //保存索引值
        List<String> keywordsList = new ArrayList<>();  //保存关键词
        for(int i=beginLine; i<endLine; i++){
            int index = (int)sheet.getRow(i).getCell(0).getNumericCellValue();
            indexList.add(index);
            String keyword = sheet.getRow(i).getCell(2).getStringCellValue();
            keywordsList.add(keyword);
        }
        assert wb != null;
        wb.close();

        //设置webdriver，进入搜索页面
        System.setProperty("webdriver.gecko.firefox", "C:\\Program Files\\Mozilla Firefox\\firefox.exe");
        //System.setProperty("webdriver.firefox.marionette", "C:\\Program Files\\Mozilla Firefox\\firefox.exe");
        //指定Firefox浏览器的路径
        String Url = "https://www.baidu.com";   //百度的地址
        WebDriver driver =new FirefoxDriver();        //new一个FirefoxDriver()
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);    //设置隐式等待5秒钟
        driver.get(Url);    //打开百度首页
        //driver.manage().window().maximize();    //把浏览器窗口最大化
        driver.findElement(By.className("s_ipt")).sendKeys("李彦宏");
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        driver.findElement(By.id("su")).click();
        try {
            Thread.sleep(3000);     //让线程等待3秒钟
        } catch (InterruptedException e ) {
            e.printStackTrace();
        }
        //开始读取列表，进行百度
        for(int i=97; i<100; i++){
        //for(int i=0; i<indexList.size(); i++)
            int index = indexList.get(i);
            //String keyword = keywordsList.get(i);
            String keyword = keywordsList.get(i) + "  智慧城市";
            System.out.println("\nnow keyword = " + keyword);
            //输入关键词进行搜索
            try{
                Thread.sleep(1000);
                driver.findElement(By.className("s_ipt")).clear();
                Thread.sleep(1000);
                driver.findElement(By.className("s_ipt")).sendKeys(keyword);
                Thread.sleep(1000);
                driver.findElement(By.id("su")).click();
                Thread.sleep(1000);
            } catch(Exception exception){
                exception.printStackTrace();
            }

            //一个关键词获取50个url，要读取5个页面。
            List<String> urlsList = new ArrayList<>();
            for(int page=0; page<5; page++){
                for(int line=1; line<=10; line++){
                    String nowLineURL = null;//*[@id="content_left"]
                    int nowID = page*10+line;
                    String nowXpath = "//*[@id=\"content_left\"]//*[@id=\"" + nowID + "\"]"; //此处选择到了右侧的百度热搜，而不是搜索结果。

                    String firTagName = "";
                    try{
                        WebElement recordEle = driver.findElement(By.xpath(nowXpath));
                        List<WebElement> childEleList = recordEle.findElements(By.xpath("./*"));
                        firTagName = childEleList.get(0).getTagName();
                    }catch (NoSuchElementException noSuchElementException){
                        System.out.println("Unable to locate element:" + nowXpath);
                    }

                    //有两种不同的组织模式
                    if(firTagName.equals("div"))    nowXpath = "//*[@id=\"" + nowID + "\"]/div/h3/a";
                    else if(firTagName.equals("h3")) nowXpath = "//*[@id=\"" + nowID + "\"]/h3/a";
                    else{
                        System.out.println("unknown web model, please check");
                        urlsList.add("");
                        continue;
                    }

                    try{
                        WebElement hrefEle = driver.findElement(By.xpath(nowXpath));
                        nowLineURL = hrefEle.getAttribute("href");
                    }
                    catch(StaleElementReferenceException | NoSuchElementException staleExcAndNoSuch){
                        System.out.println("failed to get url of page = " + page + ", line = " + line);
                        System.out.println("Unable to locate element:" + nowXpath);
                        nowLineURL = "";
                    }
                    urlsList.add(nowLineURL);
                }

                //点击“下一页”，等待几秒钟
                try{
                    //可能只有上一页，只有下一页，或者两个皆有
                    int size = driver.findElement(By.xpath("//*[@id=\"page\"]")).findElements(By.className("n")).size();
                    if(page == 4) break;
                    else if(page == 0){
                        driver.findElement(By.className("n")).click();
                    }
                    else{
                        if(size == 2) driver.findElements(By.className("n")).get(1).click();
                        else if(size == 1) break; //driver.findElement(By.className("n")).click();说明这是最后一页
                        else{
                            System.out.println("failed to get next page, save and quit! now page = " + page);
                            break;
                        }
                    }

                }catch(NoSuchElementException noSuchElementException){
                    //这种情况是结果小于40个，即少于5页。
                    try{
                        driver.findElement(By.xpath("/html/body/div[1]/div[3]/div[2]/div/a[9]")).click();
                    } catch(NoSuchElementException noSuchElementException1) {
                        System.out.println("failed to get next page, save and quit! now page = " + page);
                        System.out.println("noSuchElementException1 = " + noSuchElementException1);
                        break;
                    }
                }catch (Exception exception){
                    System.out.println("failed to get next page, save and quit! now page = " + page);
                    System.out.println("exception = " + exception);
                    break;
                }
                try {
                    Thread.sleep(300);     //让线程等待3秒钟
                } catch (InterruptedException ignored) { }
            }
            System.out.println("urlsList.size() = " + urlsList.size());

            /* 将50个url写入数据 */
            String binStr = NumberTools.intToBinStr(index);
            String textName = urlDirPathMis + "\\" +binStr+"_"+index +" "+keyword+".txt";
            System.out.println("textName = " + textName);
            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(textName)), "UTF-8"));
                for (String url : urlsList) {
                    bw.write(url);
                    bw.newLine();
                }
                bw.close();
            } catch (Exception e) {
                System.err.println("write errors :" + e);
            }

        }

        driver.quit();  //退出driver

    }
}
/*
///html/body/div[1]/div[4]/div[1]/div[3]/div[1]/div/h3/a
///html/body/div[1]/div[4]/div[1]/div[3]/div[2]/h3/a
///html/body/div[1]/div[4]/div[1]/div[3]/div[3]/div/h3/a
///html/body/div[1]/div[3]/div[1]/div[3]/div[2]/div
////*[@id="11"]//*[@id="11"]//*[@id="31"]/html/body/div[1]/div[3]/div[1]/div[3]/div[2]/h3/a
///html/body/div[1]/div[3]/div[1]/div[3]/div[2]/h3/a
///html/body/div[1]/div[3]/div[1]/div[3]/div[3]/h3/a//*[@id="1"]//*[@id="1"]
 */
