package completion.FromRecordToModel;

import completion.Tools.JdbcUtils;
import completion.Tools.SoftParameters;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/11/22 11:35
 **/
public class ModelRegressionLinear {
    private final ArrayList<float[]> trainDataList = new ArrayList<>(100000);   //训练数据，一个float[] 保存一个向量
    private final ArrayList<Float> trainLabelList = new ArrayList<>(100000);    //训练数据，一个Float保存一个标签，即分值
    private int trainCount;     //训练数据 行数

    private final ArrayList<float[]> testDataList = new ArrayList<>(10000);    //测试数据，一个float[] 保存一个向量
    private final ArrayList<Float> testLabelList = new ArrayList<>(10000);     //测试数据，一个Float保存一个标签，即分值
    private int testCount;      //测试数据 行数

    private final int dataColumn = SoftParameters.TermVectorLength + 1;   //最后一个位置恒为1，保存偏置b
    private final float [] thetaArr = new float[dataColumn]; //参数theta
    private final float alpha;//训练步长
    private final int iteration;//迭代次数

    private final ArrayList<Float> predictLabelList = new ArrayList<>(10000);  //预测的分值
    private float AVE = 0.0f;  //误差的平均值
    private float SSE = 0.0f;   //误差平方和，越小越好
    private float RSquare = 0.0f;   //决定系数， 越大越好。
    private float AdjRSquare = 0.0f;    //校正决定系数，越大越好。改进的RSquare，使不受样本数量的影响。

    /**
     * @param tableName 存储数据的表明，将该表划分为训练集与测试集，大概比例9:1
     */
    public ModelRegressionLinear(String tableName) throws InterruptedException {
        System.out.println("开始初始化 线性回归器，从表中获取数据，表名：" + tableName);

        //先获取数据，再获取数目，因为table中并不是所有table都是有效的
        ArrayList<String> vectorStrList = new ArrayList<>(160000);
        ArrayList<Float> labelList = new ArrayList<>(16000);
        JdbcUtils.getDataAndLabel(vectorStrList, labelList, 10000);

        splitDataToTrainAndTest(vectorStrList, labelList);

        trainCount = trainLabelList.size();
        testCount = testLabelList.size();

        Arrays.fill(thetaArr, 1.0f);    //将theta各个参数全部初始化为1.0

        this.alpha = 0.001f;    //步长默认为0.001
        this.iteration=100000;//迭代次数默认为 100000
    }

    public ModelRegressionLinear(String tableName,float alpha, int iteration) throws InterruptedException {
        System.out.println("开始初始化 线性回归器，从表中获取数据，表名：" + tableName);

        //先获取数据，再获取数目，因为table中并不是所有table都是有效的
        ArrayList<String> vectorStrList = new ArrayList<>(160000);
        ArrayList<Float> labelList = new ArrayList<>(16000);
        JdbcUtils.getDataAndLabel(vectorStrList, labelList, 10000);

        splitDataToTrainAndTest(vectorStrList, labelList);

        trainCount = trainLabelList.size();
        testCount = testLabelList.size();

        Arrays.fill(thetaArr, 1.0f);    //将theta各个参数全部初始化为1.0

        this.alpha = alpha;    //步长默认为0.001
        this.iteration = iteration;//迭代次数默认为 100000
    }

    /**
     * 将从表中抽取的数据划分为训练集与测试集
     */
    private void splitDataToTrainAndTest(ArrayList<String> vectorStrList, ArrayList<Float> labelList) throws InterruptedException {
        int allCount = labelList.size();
        int initTrainCount = (int)(allCount * 0.9);

        for(int i=0; i<allCount; i++){
            try{
                String[] tempArr = vectorStrList.get(i).split(",");
                if(!isLegalVector(tempArr)) continue;

                float[] vector = new float[dataColumn];
                for(int j=0; j<tempArr.length; j++) vector[j] = Float.parseFloat(tempArr[j]);
                vector[dataColumn-1] = 1.0f;

                if(i < initTrainCount){
                    trainDataList.add(vector);
                    trainLabelList.add(labelList.get(i));
                }
                else{
                    testDataList.add(vector);
                    testLabelList.add(labelList.get(i));
                }
            } catch (Exception e){
                System.out.println("error in splitDataToTrainAndTest, i = " + i);
                e.printStackTrace();
                Thread.sleep(2000);
            }

        }
    }

    /**
     * 在词向量中，有连续的5个维度为0，认定为无效词向量（即该分词不在分词表中）
     * @param tempArr
     * @return
     */
    private boolean isLegalVector(String[] tempArr){
        if(tempArr.length != SoftParameters.TermVectorLength) return false;

        for(int i=SoftParameters.WordVectorBeginIndex; i<SoftParameters.WordVectorBeginIndex+5; i++){
            if(!tempArr[i].equals("0.0")){
                return true;
            }
        }
        return false;
    }

    public void train()
    {
        int iteration = this.iteration;
        while( (iteration--) > 0)
        {
            //对每个theta i 求 偏导数
            float[] partial_derivative = compute_partial_derivative();  //偏导数
            //更新每个theta
            for(int i =0; i< thetaArr.length;i++)
                thetaArr[i] -= alpha * partial_derivative[i];
        }
    }

    private float[] compute_partial_derivative()
    {
        float[] partial_derivative = new float[thetaArr.length];
        for(int j =0;j<thetaArr.length;j++)//遍历，对每个theta求偏导数
        {
            partial_derivative[j]= compute_partial_derivative_for_theta(j);//对 theta i 求 偏导
        }
        return partial_derivative;
    }

    private float compute_partial_derivative_for_theta(int j)
    {
        float sum = 0.0f;
        for(int i=0;i<trainCount;i++)//遍历 每一行数据
        {
            sum += h_theta_x_i_minus_y_i_times_x_j_i(i, j);
        }
        return sum / trainCount;
    }

    private float h_theta_x_i_minus_y_i_times_x_j_i(int i,int j)
    {
        float[] oneRow = trainDataList.get(i);
        float label = trainLabelList.get(i);

        float result = 0.0f;

        for(int k=0; k<oneRow.length; k++)
            result += thetaArr[k] * oneRow[k];

        result -= label;
        result *= oneRow[j];
        return result;
    }

    public void printDataInformation()
    {
        System.out.println("train data size： " + trainDataList.size());
        int trainLevel5 = 0, trainLevel4 = 0, trainLevel3 = 0, trainLevel2 = 0, trainLevel1 = 0;
        for(Float label : trainLabelList){
            if(label >= 0.8) trainLevel5++;
            else if(label >= 0.6) trainLevel4++;
            else if(label >= 0.4) trainLevel3++;
            else if(label >= 0.2) trainLevel2++;
            else trainLevel1++;
        }
        System.out.println("训练集 相似等级 5 的向量数量为： " + trainLevel5);
        System.out.println("训练集 相似等级 4 的向量数量为： " + trainLevel4);
        System.out.println("训练集 相似等级 3 的向量数量为： " + trainLevel3);
        System.out.println("训练集 相似等级 2 的向量数量为： " + trainLevel2);
        System.out.println("训练集 相似等级 1 的向量数量为： " + trainLevel1);

        System.out.println("test data size: " + testDataList.size());
        int testLevel5 = 0, testLevel4 = 0, testLevel3 = 0, testLevel2 = 0, testLevel1 = 0;
        for(Float label : testLabelList){
            if(label >= 0.8) testLevel5++;
            else if(label >= 0.6) testLevel4++;
            else if(label >= 0.4) testLevel3++;
            else if(label >= 0.2) testLevel2++;
            else testLevel1++;
        }
        System.out.println("测试集 相似等级 5 的向量数量为： " + testLevel5);
        System.out.println("测试集 相似等级 4 的向量数量为： " + testLevel4);
        System.out.println("测试集 相似等级 3 的向量数量为： " + testLevel3);
        System.out.println("测试集 相似等级 2 的向量数量为： " + testLevel2);
        System.out.println("测试集 相似等级 1 的向量数量为： " + testLevel1);

        System.out.println("迭代次数： " + iteration);
        System.out.println("训练步长： " + alpha);
        System.out.println("向量长度：" + dataColumn);
    }

    public void printTheta()
    {
        for(float a : thetaArr)
            System.out.print(a+" ");
    }

    private void test(){
        for(int i=0; i<testCount; i++){
            float nowPredict = -1.0f;
            float[] vector = testDataList.get(i);
            for(int j=0; j<dataColumn; j++){
                nowPredict += vector[j] * thetaArr[j];
            }

            //if(nowPredict < 0.0f) nowPredict = 0.0f;
            //if(nowPredict > 1.0f) nowPredict = 1.0f;
            predictLabelList.add(nowPredict);
        }

        //计算误差平均值
        for(int i=0; i<testCount; i++){
            AVE += Math.abs(predictLabelList.get(i) - testLabelList.get(i));
        }
        AVE /= testCount;

        //计算SSE
        for(int i=0; i<testCount; i++){
            SSE += Math.pow((predictLabelList.get(i) - testLabelList.get(i)), 2);
        }

        //计算RSquare
        float testLabelMean = 0.0f;     //测试集中，label的均值
        for(int i=0; i<testCount; i++){
            testLabelMean += testLabelList.get(i);
        }
        testLabelMean /= testCount;

        float testLabelVariance = 0.0f;     //测试集中，label的方差
        for(int i=0; i<testCount; i++){
            testLabelVariance += Math.pow((testLabelList.get(i) - testLabelMean), 2);
        }

        RSquare = 1 - SSE / testLabelVariance;

        //计算AdjRSquare
        AdjRSquare = 1 - (1 - RSquare)*(testCount - 1) / (testCount - dataColumn - 1);
    }

    private void printResult(){
        System.out.println("误差平均值 AVE = " + AVE);
        System.out.println("误差平方和 SSE = " + SSE);
        System.out.println("决定系数 RSquare = " + RSquare);
        System.out.println("校正决定系数 AdjRSquare = " + AdjRSquare);
    }

    public void run(){
        System.out.println("数据集信息如下：");
        printDataInformation();
        System.out.println("开始训练");
        long startTime = System.currentTimeMillis();    //获取开始时间
        train();
        long endTime = System.currentTimeMillis();    //获取结束时间
        System.out.println("训练时长：" + (endTime - startTime) + " ms");
        startTime = System.currentTimeMillis();    //获取开始时间
        test();
        endTime = System.currentTimeMillis();    //获取结束时间
        System.out.println("测试时长：" + (endTime - startTime) + " ms");
        System.out.println("模型效果如下");
        printResult();
    }
}
