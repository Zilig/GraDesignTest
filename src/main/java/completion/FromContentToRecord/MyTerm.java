package completion.FromContentToRecord;

import com.hankcs.hanlp.seg.common.Term;

import java.io.Serializable;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/9/14 17:50
 **/

public class MyTerm implements Serializable {
    private static final long serialVersionUID = 8640102013241096224L;
    private int lineIndex;   //在文本中的第几行
    private int lineLen;    //该行的长度
    private int sumLength;  //从文本开头至此的文本总长度
    private final int type;       //1表示为专家名称，2表示为单位名称，未来可能细分单位名称（2021.10.25）。
    private Term term;
    private int closestDisToExpert; //在一行中，与该term最近的专家名称的距离（字符个数）

    private float score;    //该term的评分

    public MyTerm(int lineIndex, int lineLen, int sumLength, int type, Term term, int disToExpert){
        this.lineIndex = lineIndex;
        this.lineLen = lineLen;
        this.sumLength = sumLength;
        this.type = type;
        this.term = term;
        this.closestDisToExpert = disToExpert;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public void setLineIndex(int lineIndex) {
        this.lineIndex = lineIndex;
    }

    public int getLineLen() {
        return lineLen;
    }

    public void setLineLen(int lineLen) {
        this.lineLen = lineLen;
    }

    public int getSumLength() {
        return sumLength;
    }

    public void setSumLength(int sumLength) {
        this.sumLength = sumLength;
    }

    public Term getTerm() {
        return term;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public int getClosestDisToExpert() {
        return closestDisToExpert;
    }

    public void setClosestDisToExpert(int closestDisToExpert) {
        this.closestDisToExpert = closestDisToExpert;
    }

    @Override
    public String toString() {
        return "MyTerm{" +
                "lineIndex=" + lineIndex +
                ", lineLen=" + lineLen +
                ", sumLength=" + sumLength +
                ", type=" + type +
                ", term.word=" + term.word +
                '}';
    }
}
