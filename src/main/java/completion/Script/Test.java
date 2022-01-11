package completion.Script;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.List;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/10/17 15:27
 **/

//做一些小测试
public class Test {
    public static void main(String[] args) {
        Segment segment = HanLP.newSegment("perceptron")
                .enableNameRecognize(true)
                .enableAllNamedEntityRecognize(true)
                .enableOffset(true)
                .enableOrganizationRecognize(true);
        String word = " 自2010年8月起，在北京航空航天大学中法工程师学院担任青年骨干教师从事科研和教学工作，主持和参加自然科学基金项目、国家“863”主题项目、国际国家科技合作项目等多项科研课题，发表论文10余篇。2014年里尔中央理工大学和2015年里昂中央理工大学访问学者。人工智能学会智能交互专委会委员。";
        List<Term> termList = segment.seg(word);
        for(Term term : termList){
            System.out.println(term.word + ", " + term.nature.toString() + "," + term.offset);
        }
        //System.out.println(getPinyin("中国红123。")); //--zhongguohong123
    }

    /**
     * @param china (字符串 汉字)
     * @return 汉字转拼音 其它字符不变
     */
    public static String getPinyin(String china){
        HanyuPinyinOutputFormat formart = new HanyuPinyinOutputFormat();
        formart.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        formart.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        formart.setVCharType(HanyuPinyinVCharType.WITH_V);
        char[] arrays = china.trim().toCharArray();
        String result = "";
        try {
            for (int i=0;i<arrays.length;i++) {
                char ti = arrays[i];
                if(Character.toString(ti).matches("[\\u4e00-\\u9fa5]")){ //匹配是否是中文
                    String[] temp = PinyinHelper.toHanyuPinyinStringArray(ti,formart);
                    result += temp[0];
                    System.out.println(temp[0].charAt(0));
                }else{
                    result += ti;
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            e.printStackTrace();
        }

        return result;
    }
}
