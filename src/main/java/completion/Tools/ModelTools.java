package completion.Tools;


import java.util.List;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/11/23 15:07
 **/
public class ModelTools {

    /**
     * 目前只支持二分类
     * @param allType 保存数据集中出现的所有的类型
     * @param realLabelList 真实类型
     * @param predictLabelList 预测类型
     * @return
     */
    public static String computeIndicator(int[] allType,List<Integer> realLabelList, List<Integer> predictLabelList){
        int totalCount = realLabelList.size();
        if(predictLabelList.size() != totalCount){
            System.out.println("error in ModelTools.computeIndicator(), realLabelList与predictLabelList长度不相等。");
            System.out.println("realLabelList长度 = " + totalCount + ", predictLabelList长度 = " + predictLabelList.size());
            return null;
        }

        StringBuilder outSb = new StringBuilder();

        //准确率与错误率
        double accuracy = 0.0;
        int equalPair = 0;
        for(int i=0; i<totalCount; i++){
            if(realLabelList.get(i).equals(predictLabelList.get(i))){
                equalPair++;
            }
        }
        accuracy = (double)equalPair / (double)totalCount;
        double errorRate = 1.0 - accuracy;

        outSb.append("测试集总数 total = ").append(totalCount).append("\n");
        outSb.append("错误总数 unequal = ").append(totalCount-equalPair).append("\n");
        outSb.append("准确率 accuracy = ").append(accuracy).append("\n");
        outSb.append("错误率 errorRate = ").append(errorRate).append("\n");

        //对于每个类别，计算混淆矩阵
        for(int type : allType){
            int TP = 0, FP = 0, FN = 0, TN = 0;
            for(int i=0; i<totalCount; i++){
                int nowRealType = realLabelList.get(i);
                int nowPredictType = predictLabelList.get(i);

                if(nowRealType == type){
                    if(nowPredictType == type){
                        TP++;
                    }
                    else{
                        FN++;
                    }
                }
                else{
                    if(nowPredictType == type){
                        FP++;
                    }
                    else{
                        TN++;
                    }
                }
            }

            double precision = (double)TP / (double)(TP + FP);
            double recall = (double)TP / (double)(TP + FN);
            double f1 = (double)(2*precision*recall) / (double)(precision + recall);

            outSb.append("\n");
            outSb.append("类别 ").append(type).append("\n");
            outSb.append("TP = ").append(TP).append("\t").append("FN = ").append(FN).append("\n");
            outSb.append("FP = ").append(FP).append("\t").append("TN = ").append(TN).append("\n");
            outSb.append("精确率 precision = ").append(precision).append("\n");
            outSb.append("召回率 recall = ").append(recall).append("\n");
            outSb.append("f1值 f1 = ").append(f1).append("\n");
        }

        return outSb.toString();
    }

}
