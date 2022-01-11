package completion.Tools;

import java.util.HashSet;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/7/16 12:01
 **/
public class SoftParameters {
    //参数配置器
    public static ParamSetting paramSetting = ParamSetting.getParams();

    /*********************************************** 与分词有关的参数 ************************************************/

    //可作为分隔符的标点符号的词性标注简称。如引号“一般不能划分一个句子，而句号，问号，逗号可以。
    public static final HashSet<String> separatorSet = new HashSet<>(16){
        {
            add("wd");  //逗号，全角：， 半角：,
            add("wf");  //分号，全角：； 半角： ;
            add("wj");  //句号，全角：。
            add("wm");  //冒号，全角：： 半角： :
            add("wp");  //破折号，全角：—— －－   ——－   半角：—  —-
            add("ws");  //省略号，全角：…… …
            add("wt");  //叹号，全角：！
            add("ww");  //问号，全角：？
        }
    };

    /*********************************************** 与content抽取有关的参数 ************************************************/

    public static final int MaxLegalFileLen = 100000;   //HTML文件的最长合法长度
    public static final int MinLegalFileLen = 200;      //HTML文件的最短合法长度

    public static final int MinLegalContentLineLen = 2;    //content文件的一行的最短合法长度
    //网页爬取的文件的合法编码
    public static final String[] LegalFileCharset = {"UTF-8", "utf-8", "UTF-16", "utf-16", "GBK", "gbk", "GB2312", "gb2312"};

    public static final int ResultSize = 50;    //每个关键词需要的结果数量，默认50
    public static final int MinLengthOfValidStr = 5;    //有效字符串的最短长度，默认5

    /*********************************************** 与中文词向量库有关的参数 ************************************************/

    public static String sngsDatabaseNamePrefix = "sgns_sougou_bigram_";   //存储sgns向量的表的前缀
    public static int sngsDatabaseVectorSize = 300; //sngs中文词向量的维度
    //汉语拼音的韵母集合
    public static final HashSet<Character> yunmuSet = new HashSet<>(){
        private static final long serialVersionUID = 8521845356054730344L;
        {
            add('a');
            add('o');
            add('e');
            add('i');
            add('u');
            add('v');
        }
    };

    //汉语拼音的声母集合
    public static final HashSet<Character> shengmuSet = new HashSet<>(){
        private static final long serialVersionUID = -5377706741731719982L;
        {
            add('b');
            add('c');
            add('d');
            add('f');
            add('g');
            add('h');
            add('j');
            add('k');
            add('l');
            add('m');
            add('n');
            add('p');
            add('q');
            add('r');
            add('s');
            add('t');
            add('w');
            add('x');
            add('y');
            add('z');
        }
    };

    /*********************************************** 与特征向量有关的参数 ************************************************/
    public static final int WordVectorLength = 300;
    public static final int TermVectorLength = 614;

    public static final int featureFileSortIndex = 0;
    public static final int featurePageTypeIndex = 1;
    public static final int featureFileLineCountIndex = 2;
    public static final int featureFileLegalLineCountIndex = 3;
    public static final int featureFileSumLegalLineLengthIndex = 4;
    public static final int featureExpertTypeTermCountIndex = 5;        //该content中有几个专家名称。
    public static final int featureInstitutionTypeTermCountIndex = 6;   //该content中有几个单位类型的term。
    public static final int featureTermLineIndexIndex = 7;       //该term所在行的位置。
    public static final int featureTermLineLengthIndex = 8; //该term所在行的长度。
    public static final int featureTermSumLengthIndex = 9;  //到该term为止，该行的长度。
    public static final int featureTermWordLengthIndex = 10;  //word长度
    public static final int featureTermContainPlaceIndex = 11;  //是否包含地名
    public static final int featureTermContainKeywordIndex = 12;  //是否包含关键词。
    public static final int featureDisToExpertIndex = 13;  //与最近的专家名称的距离（字符个数）。
    public static final int WordVectorBeginIndex = 14;
    public static final int ContextVectorBeginIndex = 314;

    //在训练集的excel中，一行一共620行。第0行：id； 1:实体编码; 2:专家名称; 3:单位名称; 4：分数； 5：预测分数； 6-619：featureVector
    public static final int trainDataExcelColumnLength = 620;

    /*
fileSort，pageType，fileLineCount，fileLegalLineCount，expertTypeTermCount，institutionTypeTermCount，ThisTerm.lineIndex，
ThisTerm.lineLen，ThisTerm.sumLength，ThisTerm.word.length，ThisTerm.word.containDiming，ThisTerm.word.containGuanjianci，distanceToE,
 */
}
