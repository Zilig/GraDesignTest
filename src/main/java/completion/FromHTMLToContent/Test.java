package completion.FromHTMLToContent;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/6/2 21:28
 **/
public class Test {
    public static void main(String[] args) {
        //将html处理为content
        ExtractContent extractor = new ExtractContent();
        int beginIndex = 1050, endIndex = 1100;
        //extractor.runTest();
        extractor.run(beginIndex, endIndex);


        //提取html文件的统计信息
//        ExtractHtmlInformation extractor = new ExtractHtmlInformation();
//        int beginIndex = 102, endIndex = 1000;
//        extractor.runHTML(beginIndex, endIndex);

        //统计前1000个html和content的数据，然后放松对tail名称的限制，不再要求全部相同。
//        ExtractContentInformation extractor = new ExtractContentInformation();
//        int beginIndex = 100, endIndex = 1000;
//        extractor.runContent(beginIndex, endIndex);

        //将每个html的统计数据合并为每个entity的统计数据
//        StatisticHtmlInfor statistics = new StatisticHtmlInfor();
//        int beginIndex = 201, endIndex = 49678;
//        statistics.runStatisticHtml(beginIndex, endIndex);

        //将每个content的统计数据合并为每个entity的统计数据
//        StatisticContentInfor statistics = new StatisticContentInfor();
//        int beginIndex = 201, endIndex = 49678;
//        statistics.runStatisticContent(beginIndex,endIndex);

        //将html_mis处理为content
//        ExtractContent_mis extractor = new ExtractContent_mis();
//        int beginIndex = 0, endIndex = 100;
//        extractor.run(beginIndex, endIndex);


    }

}

