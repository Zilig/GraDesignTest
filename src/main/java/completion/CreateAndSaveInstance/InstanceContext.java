package completion.CreateAndSaveInstance;

import com.hankcs.hanlp.seg.common.Term;
import completion.Tools.FileTools;
import completion.Tools.NumberTools;
import completion.Tools.SuperParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2022/3/23 11:36
 * @Description TODO
 **/
public class InstanceContext {
    private String entityWord;        //该实体
    private String nowLine;     //该实体所在行
    private int nowLineIndex;   //该实体所在行的位置
    private int beginFromLine;  //该实体在nowLine中的起始位置
    private List<String> aboveLineList;     //实体的上几行
    private List<String> belowLineList;     //实体的下几行

    private int closestHeadDistance;    //与实体最近的头实体的距离

    public InstanceContext(String word, String nowLine, int nowLineIndex, int beginFromLine, List<String> aboveLineList, List<String> belowLineList) {
        this.entityWord = word;
        this.nowLine = nowLine;
        this.nowLineIndex = nowLineIndex;
        this.beginFromLine = beginFromLine;
        this.aboveLineList = aboveLineList;
        this.belowLineList = belowLineList;
    }

    public int getClosestHeadDistance() {
        return closestHeadDistance;
    }

    //head是头实体，allTermList是文档的全部分词结果，在此处生成上文、内文、下文，均以"$"隔开
    public String[] findContext(String head, List<List<Term>> allTermList){
        int entityBias = 0;     //命名实体相较于原点的距离（原点指上下文开头）
        List<Integer> lenList = new ArrayList<>();  //上下文每一行的长度，为了计算头实体与原点的距离bias

        //headIndexesList 的形式如下，[[1,5], [2], [4,10,21]]
        //找出所有头实体出现的位置
        List<List<Integer>> headIndexesList = new ArrayList<>();
        for(String line : aboveLineList){
            entityBias += line.length();
            lenList.add(line.length());
            headIndexesList.add(NumberTools.findWordIndexInLine(line, head));
        }

        lenList.add(nowLine.length());
        headIndexesList.add(NumberTools.findWordIndexInLine(nowLine,head));

        for(String line : belowLineList){
            lenList.add(line.length());
            headIndexesList.add(NumberTools.findWordIndexInLine(line, head));
        }
        //现在entityBias 相当于 上三行的长度 + beginFromLine，也就是标注实体与上文原点的距离（字符个数）
        //此时，命名实体与原点的距离就得到了
        entityBias += beginFromLine;

        int firstAboveLineIndex = nowLineIndex - aboveLineList.size();  //上下文的第一行，处于全文档的位置
        int lineCount = lenList.size();    //上下文一共有几行（即两个超参数的和）
        int nowLenSum = 0;
        int nowMinDistance = Integer.MAX_VALUE;
        int closestHeadLineIndex = -1;      //最近的头实体所在行的位置
        int closestHeadEntityIndex = -1;    //最近的头实体在 所在行 的位置
        for(int i=0; i<lineCount; i++){
            List<Integer> indexes = headIndexesList.get(i);     //第i行中所有头实体出现的位置
            for(int index : indexes){
                int headBias = nowLenSum + index;   //头实体相对于原点的距离（原点指上下文开头）
                if(Math.abs(headBias - entityBias) < nowMinDistance){
                    nowMinDistance = Math.abs(headBias - entityBias);
                    closestHeadLineIndex = firstAboveLineIndex + i;
                    closestHeadEntityIndex = index;
                }
            }
            nowLenSum += lenList.get(i);
        }
        closestHeadDistance = nowMinDistance;

        //上下文中没有头实体
        if(nowMinDistance == Integer.MAX_VALUE){
            closestHeadDistance = SuperParameters.ClosestHeadDistance;
            return headNotExist(allTermList);
        }
        //上下文中有头实体
        else {
            return headExist(closestHeadLineIndex, closestHeadEntityIndex, allTermList);
        }
    }

    //上下文中没有头实体，计算 上内下 文
    private String[] headNotExist(List<List<Term>> allTermList){
        List<String> sentenceList = FileTools.splitLineToSentence(nowLine);
        int sentenceIndex = -1;
        int sumLen = 0;     //即命名实体别在句子的前边几句的总长度
        for(int i=0; i<sentenceList.size(); i++){
            sumLen += sentenceList.get(i).length();
            if(sumLen >= beginFromLine + entityWord.length()){
                sentenceIndex = i;
                sumLen -= sentenceList.get(i).length();
                break;
            }
        }
        //即命名实体别在句子的前边几句的总长度 + 命名实体所在句子的长度
        int sumLenAndInner =  sumLen + sentenceList.get(sentenceIndex).length();

        String[] contextArr = new String[3];
        List<String> aboveWordList = new ArrayList<>(16);
        List<String> innerWordList = new ArrayList<>(32);
        List<String> belowWordList = new ArrayList<>(16);

        //上下文中没有头实体
        List<Term> nowLineTermList = allTermList.get(nowLineIndex);

        int nowPos = 0;
        int headStopTermIndex = 0;     //此时不存在头实体，用来指示何时到达命名实体所在的句子
        int tailStopTermIndex = Integer.MAX_VALUE;     //指示何时达到命名实体所在句子的后一个句子

        //aboveContext。即命名实体别在句子的上一句
        for(int i=0; i<nowLineTermList.size(); i++){
            String word = nowLineTermList.get(i).word;
            aboveWordList.add(word);
            nowPos += word.length();
            if(nowPos > sumLen){
                aboveWordList.remove(aboveWordList.size()-1);
                headStopTermIndex = i;
                nowPos -= word.length();
                break;
            }
        }

        //innerContext
        for(int i=headStopTermIndex; i<nowLineTermList.size(); i++){
            String word = nowLineTermList.get(i).word;
            innerWordList.add(word);
            nowPos += word.length();
            //innerContext同时包括头实体和标注实体
            if(nowPos > sumLenAndInner){
                innerWordList.remove(innerWordList.size()-1);
                tailStopTermIndex = i;
                nowPos -= word.length();
                break;
            }
        }

        //如果tailStopTermIndex=Integer.MaxValue，说明命名实体在该句子的最后一个term，那么下文为空


        //belowContext
        for(int i=tailStopTermIndex; i<nowLineTermList.size(); i++){
            belowWordList.add(nowLineTermList.get(i).word);
        }

        contextArr[0] = handleAboveContext(aboveWordList, nowLineIndex, allTermList);
        contextArr[1] = handleInnerContext(innerWordList, -1, allTermList);
        contextArr[2] = handleBelowContext(belowWordList, nowLineIndex, allTermList);

        return contextArr;
    }

    //上下文中有头实体，计算 上内下 文。
    private String[] headExist(int closestHeadLineIndex, int closestHeadEntityIndex, List<List<Term>> allTermList){
        String[] contextArr = new String[3];
        List<String> aboveWordList = new ArrayList<>(16);
        List<String> innerWordList = new ArrayList<>(32);
        List<String> belowWordList = new ArrayList<>(16);

        int firstLineIndex = Math.min(closestHeadLineIndex, nowLineIndex);
        int secondLineIndex = Math.max(closestHeadLineIndex, nowLineIndex);

        List<Term> firstLineTermList = allTermList.get(firstLineIndex);
        List<Term> secondLineTermList = allTermList.get(secondLineIndex);

        int nowPos = 0;
        int headStopTermIndex = 0;
        int tailStopTermIndex = Integer.MAX_VALUE;     //指示何时达到命名实体所在句子的后一个句子

        //aboveContext
        for(int i=0; i<firstLineTermList.size(); i++){
            String word = firstLineTermList.get(i).word;
            aboveWordList.add(word);
            nowPos += word.length();
            if(nowPos > closestHeadEntityIndex){
                aboveWordList.remove(aboveWordList.size()-1);
                headStopTermIndex = i;
                nowPos -= word.length();
                break;
            }
        }

        //innerContext
        if(firstLineIndex == secondLineIndex){
            //头实体与命名实体在同一行
            for(int i=headStopTermIndex; i<firstLineTermList.size(); i++){
                String word = firstLineTermList.get(i).word;
                innerWordList.add(word);
                nowPos += word.length();
                //innerContext同时包括头实体和标注实体
                if(nowPos > beginFromLine + entityWord.length()){
                    innerWordList.remove(innerWordList.size()-1);
                    tailStopTermIndex = i;
                    nowPos -= word.length();
                    break;
                }
            }
        }
        else{
            //innerContext在第一实体所在行的部分
            for(int i=headStopTermIndex; i<firstLineTermList.size(); i++){
                innerWordList.add(firstLineTermList.get(i).word);
            }

            //innerContext在两个实体之间的所在行的部分
            for(int i=firstLineIndex+1; i<secondLineIndex; i++){
                List<Term> termList = allTermList.get(i);
                for(int j=0; j<termList.size(); j++){
                    innerWordList.add(termList.get(j).word);
                }
            }

            //innerContext在第二实体所在行的部分
            nowPos = 0;
            tailStopTermIndex = Integer.MAX_VALUE;
            for(int i=0; i<secondLineTermList.size(); i++){
                String word = secondLineTermList.get(i).word;
                innerWordList.add(word);
                nowPos += word.length();
                //innerContext同时包括头实体和标注实体
                if(nowPos > beginFromLine + entityWord.length()){
                    innerWordList.remove(innerWordList.size()-1);
                    tailStopTermIndex = i;
                    nowPos -= word.length();
                    break;
                }
            }
        }

        //belowContext
        for(int i=tailStopTermIndex; i<secondLineTermList.size(); i++){
            belowWordList.add(secondLineTermList.get(i).word);
        }

        contextArr[0] = handleAboveContext(aboveWordList, firstLineIndex, allTermList);
        contextArr[1] = handleInnerContext(innerWordList, 1, allTermList);
        contextArr[2] = handleBelowContext(belowWordList, secondLineIndex, allTermList);
        return contextArr;
    }



    //处理、生成上文。aboveLineIndex表示上文所在的行的位置。
    private String handleAboveContext(List<String> aboveWordList, int aboveLineIndex, List<List<Term>> allTermList){
        int sumCount = aboveWordList.size();

        //上文太短，则把上一行的word也加入
        if(sumCount < SuperParameters.ContextAboveWordMinCount){
            if(nowLineIndex != 0){
                List<Term> beforeLineTermList = allTermList.get(aboveLineIndex-1);
                List<String> beforeLineWordList = new ArrayList<>();
                for(Term term : beforeLineTermList){
                    beforeLineWordList.add(term.word);
                }
                int beforeSumCount = beforeLineWordList.size();

                beforeLineWordList.addAll(aboveWordList);
                aboveWordList = beforeLineWordList;
                sumCount += beforeSumCount;
            }
        }
        //上文太长，保留后边的几个word
        if(sumCount > SuperParameters.ContextAboveWordMaxCount){
            aboveWordList = aboveWordList.subList(aboveWordList.size() - SuperParameters.ContextAboveWordMaxCount, aboveWordList.size());
        }

        return combineWordListToStr(aboveWordList, 1);
    }

    //处理内文，将内文截短。如果头实体不存在，截掉两端。如果头实体存在，截掉中间。
    private String handleInnerContext(List<String> innerWordList, int type, List<List<Term>> allTermList){
        int wordCount = innerWordList.size();
        if(wordCount <= SuperParameters.ContextInnerWordMaxCount){
            return combineWordListToStr(innerWordList, 0);
        }
        //word太多
        if(type == -1){
            //头实体不存在，截掉两端
            int beginIndex = (wordCount - SuperParameters.ContextInnerWordMaxCount) / 2;
            innerWordList = innerWordList.subList(beginIndex, beginIndex+SuperParameters.ContextInnerWordMaxCount);
        }
        else{
            //头实体存在，截掉中间
            int firstHalfEndIndex = SuperParameters.ContextInnerWordMaxCount / 2;
            int secondHalfBeginIndex = wordCount - SuperParameters.ContextInnerWordMaxCount / 2;
            List<String> secondHalf = innerWordList.subList(secondHalfBeginIndex, wordCount);
            innerWordList = innerWordList.subList(0, firstHalfEndIndex + 1);
            innerWordList.addAll(secondHalf);
        }
        return combineWordListToStr(innerWordList, 0);
    }

    //处理、生成下文。belowLineIndex表示下文所在的行的位置。
    private String handleBelowContext(List<String> belowWordList, int belowLineIndex, List<List<Term>> allTermList){
        int sumCount = belowWordList.size();

        //下文太短，则把下一行的word也加入
        if(sumCount < SuperParameters.ContextAboveWordMinCount){
            if(nowLineIndex != allTermList.size()-1){
                List<Term> nextLineTermList = allTermList.get(belowLineIndex+1);
                List<String> nextLineWordList = new ArrayList<>();
                for(Term term : nextLineTermList){
                    nextLineWordList.add(term.word);
                }
                int nextSumCount = nextLineWordList.size();

                nextLineWordList.addAll(belowWordList);
                belowWordList = nextLineWordList;
                sumCount += nextSumCount;
            }
        }
        //下文太长，保留前边的几个word
        if(sumCount > SuperParameters.ContextBelowWordMaxCount){
            belowWordList = belowWordList.subList(0, SuperParameters.ContextBelowWordMaxCount);
        }

        return combineWordListToStr(belowWordList, -1);
    }

    //将termList组装为一个str，便于存入数据库；以”$“符号分隔term，便于读取后再分词。-1表示下文,0表示内文，1表示上文
    private String combineWordListToStr(List<String> wordList, int type){
        StringBuilder sb = new StringBuilder();
        for(String word : wordList){
            word = word.replace('\'', '"').replace('$', '#');
            sb.append(word).append("$");
            if(sb.length() >= SuperParameters.ContextInnerMaxLen){
                sb.delete(sb.length()-word.length(), sb.length());
                break;
            }
        }
        return sb.toString();
    }
}
