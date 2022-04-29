package completion.CreateAndSaveInstance;

import com.hankcs.hanlp.seg.common.Term;
import completion.Tools.NumberTools;

import java.util.List;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2022/3/22 21:55
 * @Description TODO
 **/

public class Instance {
    private final String entityName;    //该标注实体的名称
    private final int digitCountFromBegin; //到该行为止，文件的总字符数

    private final String nowLine;     //该实体所在行
    private int nowLineIndex;   //该实体所在行的下标
    private final int beginFromLine;  //该实体在nowLine中的起始位置
    private final List<String> aboveContextList;   //上边几行
    private final List<String> belowContextList;  //下边几行

    private final List<List<Term>> allTermList;


    public Instance(String entityName, int digitCountFromBegin, String nowLine, int nowLineIndex, int beginFromLine,
                    List<String> aboveContextList, List<String> belowContextList, List<List<Term>> allTermList) {
        this.entityName = entityName;
        this.digitCountFromBegin = digitCountFromBegin;
        this.nowLine = nowLine;
        this.nowLineIndex = nowLineIndex;
        this.beginFromLine = beginFromLine;
        this.aboveContextList = aboveContextList;
        this.belowContextList = belowContextList;
        this.allTermList = allTermList;
    }

    public Record createSemiRecord(String headName, String tailName){
        InstanceContext context = new InstanceContext(entityName, nowLine, nowLineIndex,beginFromLine, aboveContextList, belowContextList);
        Record record = new Record();

        //设置一些指示信息
        record.setEntityName(entityName);
        record.setScore(computeLabelScore(tailName));

        //设置上下文
        String[] contextArr = context.findContext(headName, allTermList);
        record.setAboveContext(contextArr[0]);
        record.setInnerContext(contextArr[1]);
        record.setBelowContext(contextArr[2]);

        //设置词汇特征(其他词汇特征)
        record.setEntityLen(entityName.length());
        record.setEntityLineLen(nowLine.length());
        record.setDistanceFromLine(beginFromLine);
        record.setDistanceFromBegin(digitCountFromBegin + beginFromLine);
        record.setDistanceFromHead(context.getClosestHeadDistance());
        record.setContainKeyType(0);

        //全局特征 需要在FileInFor中设置

        return record;
    }

    /**评分计算函数，计算一个word的标签分值，根据自定义的相似度。
     * 1. 是否完全相同
     * 2. 是否有标志词（大学、学院、研究中心、省、市、乡、等），有的话加0.5分。可能是干扰项。
     * 3. 基于同义词词库进行距离调整
     * 4. 还是要建立层级体系？
     * 5. 太麻烦，暂时根据编辑距离，归一化到0-1之间。
     * @return  这个word与正确答案的接近程度。（0-1）
     */
    private float computeLabelScore(String institutionName){
        if(entityName.equals(institutionName)){
            return 1.0f;
        }
        int editDis = NumberTools.computeEditDistance(entityName, institutionName);
        int maxLen = Math.max(entityName.length(), institutionName.length());
        return 1 - (float)editDis / (float)maxLen;
    }
}
