package completion.Script;

import completion.Tools.ParamSetting;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/9/3 10:09
 **/
public class MiningKeywordInfor {
    //统计一个关键词的所有网页，有多少是可用的。
    @Deprecated
    private static double[] findKeywordTimes(String htmlFolder){
        double[] results = {-1, -1, -1, -1};
        File htmlDirectory = new File(htmlFolder);
        if(!htmlDirectory.exists()){
            System.out.println("This folder don't exist:" + htmlDirectory);
            return results;
        }
        for(double i : results) i+=1;
        File[] htmlFiles = htmlDirectory.listFiles();
        assert htmlFiles != null;
        if(htmlFiles.length == 0) return results;

        for (File htmlFile : htmlFiles) {
            //读取html的内容
            if(htmlFile.isFile()) {
                results[0] += 1;
                StringBuilder htmlStrBuilder = new StringBuilder();
                String htmlStr = null;
                try{
                    BufferedReader br = new BufferedReader(new FileReader(htmlFile));//构造一个BufferedReader类来读取文件
                    while((htmlStr = br.readLine())!=null){//使用readLine方法，一次读一行
                        htmlStrBuilder.append(htmlStr).append(System.lineSeparator());
                    }
                    br.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
                htmlStr = htmlStrBuilder.toString();
                //获取该html的头尾关键词
                String[] filename = htmlFile.getName().split(" ");
                String headName = filename[1];
                String tailName = filename[3];
                int useful = 0;
                if(htmlStr.contains(headName)) {
                    results[1] += 1;
                    useful++;
                }
                if(htmlStr.contains(tailName)) {
                    results[2] += 1;
                    useful++;
                }
                if(useful == 2) results[3] += 1;
            }
        }
        return results;
    }

    //找到一个关键词在文件中出现在第几行，返回所有的出现行位置
    private static ArrayList<Integer> findWordLocationLineList(String keyword, File file){
        ArrayList<Integer> results = new ArrayList<>();
        //分行读取文件
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String lineStr = "";
            int index = 1;
            while ((lineStr = bf.readLine()) != null) {
                if(lineStr.contains(keyword)){
                    results.add(index);
                }
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    private static String statisticKeywordAppear(String[] keywordArr, File keywordDic){
        StringBuilder result = new StringBuilder();
        String dicName = keywordDic.getName();
        if(keywordDic.isDirectory()){
            File[] txtFile = keywordDic.listFiles();
            for(int i=0; i< txtFile.length; i++){
                StringBuilder nowTxtResSb = new StringBuilder();
                nowTxtResSb.append(dicName).append("\\").append(txtFile[i].getName()).append("\n");
                for(String word : keywordArr){
                    ArrayList<Integer> nowLines = findWordLocationLineList(word, txtFile[i]);
                    nowTxtResSb.append(word).append(" : ");
                    nowTxtResSb.append(Arrays.toString(nowLines.toArray())).append("\n");
                }

                result.append(nowTxtResSb.toString()).append("\n");
            }
        }
        return result.toString();
    }

    public static void main(String[] args) {

        String[][] keywordsArr = {
                {"山东省", "枣庄", "综合开发", "公司"},
                {"山东省", "枣庄", "委员会", "建设委员会"},
                {"新华社", "新华社天津", "天津分社", "天津"},
                {"新华社", "新华社天津", "天津分社", "天津"},
                {"上海", "邮电设计", "研究院", "有限公司"}
        };
        ParamSetting prop = ParamSetting.getParams();
        //contents_mis的存储路径
        String contents_misPath = prop.getContentPathMis();
        //String htmlPath = prop.getOutputDirPathMis();
        File contentDirectory = new File(contents_misPath);
        if(contentDirectory.isDirectory()){
            File[] content = contentDirectory.listFiles();//注意:这里只能用listFiles()，不能使用list()
            int begIndex = 0, endIndex = 5;
            for(int index=begIndex; index<endIndex; index++){
                assert content != null;
                File keywordDic = content[index];
                String nowInformation = statisticKeywordAppear(keywordsArr[index], keywordDic);
                System.out.println(nowInformation);
            }
        }


/*
        File file = new File("D:\\Java\\Workspace\\GraDesign1\\src\\main\\resources\\Contents_mis\\0000000000000001_1 赵义湘\\13.txt");
        String keyword = "枣庄";
        ArrayList<Integer> lineList = findWordLocationLineList(keyword, file);
        System.out.println(Arrays.toString(lineList.toArray()));
*/

    }
}
