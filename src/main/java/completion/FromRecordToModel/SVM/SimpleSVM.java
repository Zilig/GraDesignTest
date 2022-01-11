package completion.FromRecordToModel.SVM;

import completion.Tools.FileTools;
import completion.Tools.JdbcUtils;
import completion.Tools.ModelTools;
import completion.Tools.SoftParameters;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/11/22 23:43
 **/
public class SimpleSVM {
    private static StringBuilder outSb = new StringBuilder();  //保存输出信息

    private static ArrayList<float[]> trainDataList = new ArrayList<>(100000);   //训练数据，一个float[] 保存一个向量
    private static ArrayList<Float> trainLabelList = new ArrayList<>(100000);    //训练数据，一个Float保存一个标签，即分值

    private static ArrayList<float[]> testDataList = new ArrayList<>(10000);    //测试数据，一个float[] 保存一个向量
    private static ArrayList<Float> testLabelList = new ArrayList<>(10000);     //测试数据，一个Float保存一个标签，即分值

    private static ArrayList<Integer> realLabelList = new ArrayList<>(10000);
    private static ArrayList<Integer> predictLabelList = new ArrayList<>(10000);

    private int exampleNum;//X数组行数
    private int exampleDim;//X数组列数
    private double[] w;//权值
    private double lambda;//损失函数的参数
    private double lr = 0.001;//0.00001 学习率
    private double threshold = 0.001; //迭代停止 权值变换小于threshold
    private double cost;//HingeLoss损失函数  cost = HingeLoss^2 + lambda*||w||^2   cost = err'*err + lambda*w'*w; grad = 2*X(idx,:)'*err + 2*lambda*w;

    private double[] grad;//存放需要更新的权值w
    private double[] yp;//存放每一行x和权值w的点积 yp【0】表示第一行x和w的点积
    public SimpleSVM(double paramLambda)
    {

        lambda = paramLambda;

    }

    private void CostAndGrad(double[][] X,double[] y)
    {
        cost =0;
        for(int m=0;m<exampleNum;m++)//从第一行开始进行循环
        {
            yp[m]=0;
            for(int d=0;d<exampleDim;d++)
            {
                yp[m]+=X[m][d]*w[d];//第一行x和权值w的点积
            }

            if(y[m]*yp[m]-1<0)
            {
                cost += (1-y[m]*yp[m]);//将y label（-1 or 1）和点积相乘 和1的差 相加
            }

        }

        for(int d=0;d<exampleDim;d++)
        {
            cost += 0.5*lambda*w[d]*w[d];
        }


        for(int d=0;d<exampleDim;d++)
        {
            grad[d] = Math.abs(lambda*w[d]);
            for(int m=0;m<exampleNum;m++)
            {
                if(y[m]*yp[m]-1<0)
                {
                    grad[d]-= y[m]*X[m][d];
                }
            }
        }
    }

    private void update()
    {
        for(int d=0;d<exampleDim;d++)
        {
            w[d] -= lr*grad[d];
        }
    }

    public void Train(double[][] X,double[] y,int maxIters)
    {
        exampleNum = X.length;
        if(exampleNum <=0)
        {
            System.out.println("num of example <=0!");
            return;
        }
        exampleDim = X[0].length;
        w = new double[exampleDim];
        grad = new double[exampleDim];
        yp = new double[exampleNum];

        for(int iter=0;iter<maxIters;iter++)
        {

            CostAndGrad(X,y);

            System.out.println("iter:" + iter + ", cost:"+cost);
            outSb.append("iter:").append(iter).append(", cost:").append(cost).append("\n");

            if(cost< threshold)
            {
                break;
            }
            update();

        }
    }


    private int predict(double[] x)
    {
        double pre=0;
        for(int j=0;j<x.length;j++)
        {
            pre+=x[j]*w[j];
        }
        if(pre >=0)//这个阈值一般位于-1到1
            return 1;
        else return -1;
    }

    public String Test(double[][] testX,double[] testY)
    {
        int error=0;
        for(int i=0;i<testX.length;i++)
        {
            int nowPredict = predict(testX[i]);
            int nowReal = (int)testY[i];

            realLabelList.add(nowReal);
            predictLabelList.add(nowPredict);
            if(nowPredict != nowReal)
            {
                error++;
            }

            outSb.append("真实值：").append(testY[i]).append(", 预测值：").append(nowPredict).append("\n");
        }

        System.out.println("total:"+testX.length);
        System.out.println("error:"+error);
        System.out.println("error rate:"+((double)error/testX.length));
        System.out.println("acc rate:"+((double)(testX.length-error)/testX.length));

        String result = ModelTools.computeIndicator(new int[]{1,-1}, realLabelList, predictLabelList);
        return result;
    }

    public static void loadData(double[][]X,double[] y,String trainFile) throws IOException
    {

        File file = new File(trainFile);
        RandomAccessFile raf = new RandomAccessFile(file,"r");
        StringTokenizer tokenizer,tokenizer2;

        int index=0;
        while(true)
        {
            String line = raf.readLine();

            if(line == null) break;
            tokenizer = new StringTokenizer(line," ");
            y[index] = Double.parseDouble(tokenizer.nextToken());
            //System.out.println(y[index]);
            while(tokenizer.hasMoreTokens())
            {
                tokenizer2 = new StringTokenizer(tokenizer.nextToken(),":");
                int k = Integer.parseInt(tokenizer2.nextToken());
                double v = Double.parseDouble(tokenizer2.nextToken());
                X[index][k] = v;
                //System.out.println(k);
                //System.out.println(v);
            }
            X[index][0] =1;
            index++;
        }
    }

    /**
     *
     * @param expectCount 期望的 训练集 + 测试集 数目
     */
    private static void readDataFromDB(int expectCount){
        ArrayList<String> vectorStrList = new ArrayList<>(160000);
        ArrayList<Float> labelList = new ArrayList<>(16000);
        JdbcUtils.getDataAndLabel(vectorStrList, labelList, expectCount);
        splitDataToTrainAndTest(vectorStrList, labelList);
    }

    /**
     * 将从表中抽取的数据划分为训练集与测试集
     */
    private static void splitDataToTrainAndTest(ArrayList<String> vectorStrList, ArrayList<Float> labelList){
        int allCount = labelList.size();
        int initTrainCount = (int)(allCount * 0.9);

        for(int i=0; i<allCount; i++){
            try{
                String[] tempArr = vectorStrList.get(i).split(",");
                if(!isLegalVector(tempArr)) continue;

                //只保留后300位特征值
//                int dataColumn = SoftParameters.TermVectorLength - SoftParameters.ContextVectorBeginIndex;
//                float[] vector = new float[dataColumn];
//                for(int j=SoftParameters.ContextVectorBeginIndex; j<tempArr.length; j++)
//                    vector[j-SoftParameters.ContextVectorBeginIndex] = Float.parseFloat(tempArr[j]);

                //只保留后600位特征值
                int dataColumn = SoftParameters.TermVectorLength - SoftParameters.WordVectorBeginIndex;
                float[] vector = new float[dataColumn];
                for(int j=SoftParameters.WordVectorBeginIndex; j<tempArr.length; j++)
                    vector[j-SoftParameters.WordVectorBeginIndex] = Float.parseFloat(tempArr[j]);

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
                try{
                    Thread.sleep(2000);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }

            }

        }
    }

    /**
     * 在词向量中，有连续的5个维度为0，认定为无效词向量（即该分词不在分词表中）
     * @param tempArr
     * @return
     */
    private static boolean isLegalVector(String[] tempArr){
        if(tempArr.length != SoftParameters.TermVectorLength) return false;

        for(int i=SoftParameters.WordVectorBeginIndex; i<SoftParameters.WordVectorBeginIndex+5; i++){
            if(!tempArr[i].equals("0.0")){
                return true;
            }
        }
        return false;
    }

    private static void convertTrainData(double[][]X, double[] y){
        int trainDataCount = trainDataList.size();
        int dataColumn = trainDataList.get(0).length;

        //X = new double[trainDataCount][dataColumn];
        //y = new double[trainDataCount];

        for(int i=0; i<trainDataCount; i++){
            int type = 0;
            float tempLabel = trainLabelList.get(i);
            if(tempLabel > 0.5) type = 1;
            else type = -1;
            y[i] = type;

            float[] tempFeature = trainDataList.get(i);
            for(int j=0; j<dataColumn; j++){
                X[i][j] = tempFeature[j];
            }
        }
    }

    private static void convertTestData(double[][]X, double[] y){
        int testDataCount = testDataList.size();
        int dataColumn = testDataList.get(0).length;

        //X = new double[testDataCount][dataColumn];
        //y = new double[testDataCount];

        for(int i=0; i<testDataCount; i++){
            int type = -1;
            float tempLabel = testLabelList.get(i);
            if(tempLabel > 0.5) type = 1;
            else type = -1;
            y[i] = type;

            float[] tempFeature = testDataList.get(i);
            for(int j=0; j<dataColumn; j++){
                X[i][j] = tempFeature[j];
            }
        }
    }



    public static void main(String[] args)
    {
        readDataFromDB(90000);
        int dataColumn = trainDataList.get(0).length;

        int trainDataCount = trainDataList.size();
        double[][] trainX = new double[trainDataCount][dataColumn];
        double[] trainY = new double[trainDataCount];
        convertTrainData(trainX, trainY);

        SimpleSVM svm = new SimpleSVM(0.0001);
        svm.Train(trainX,trainY,100);

        int testDataCount = testDataList.size();
        double[][] testX = new double[testDataCount][dataColumn];
        double[] testY = new double[testDataCount];
        convertTestData(testX, testY);

        String statisticalResult = svm.Test(testX, testY);

        String inforFileName = "D:\\Java\\Workspace\\GraduateDesign\\FrontHalf\\src\\main\\resources" +
                "\\Model\\SVM\\SimpleSVM-600维\\数据90000-迭代100-维度后600.txt";
        String splitLine = "\n--------------------------------------------------------------\n";
        FileTools.writeStrToFile(outSb.toString() + splitLine + statisticalResult, inforFileName);
        /*
        // TODO Auto-generated method stub
        double[] y = new double[400];
        double[][] X = new double[400][11];
        String trainFile = "D:\\train_bc";
        loadData(X,y,trainFile);

        SimpleSVM svm = new SimpleSVM(0.0001);
        svm.Train(X,y,7000);

        double[] test_y = new double[283];
        double[][] test_X = new double[283][11];
        String testFile = "D:\\test_bc";
        loadData(test_X,test_y,testFile);
        svm.Test(test_X, test_y);
        */
    }

}
