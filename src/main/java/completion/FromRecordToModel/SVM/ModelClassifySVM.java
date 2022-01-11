package completion.FromRecordToModel.SVM;

import completion.Tools.JdbcUtils;
import completion.Tools.SoftParameters;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/11/22 11:35
 **/
public class ModelClassifySVM {
    private final ArrayList<float[]> trainDataList = new ArrayList<>(100000);   //训练数据，一个float[] 保存一个向量
    private final ArrayList<Float> trainLabelList = new ArrayList<>(100000);    //训练数据，一个Float保存一个标签，即分值

    private final ArrayList<float[]> testDataList = new ArrayList<>(10000);    //测试数据，一个float[] 保存一个向量
    private final ArrayList<Float> testLabelList = new ArrayList<>(10000);     //测试数据，一个Float保存一个标签，即分值

    //参数设置和满足LibSVM输入格式的训练文本
    /**
     * 这是用来测试的样例，测试成功了。
    public String[] str_trained = {"-g","2.0","-c","32","-t","2","-m","500.0","-h","0","D:\\Java\\Workspace\\GraduateDesign\\FrontHalf\\src\\main\\resources\\trainSVM.txt"};
    private String str_model = "D:\\Java\\Workspace\\GraduateDesign\\FrontHalf\\src\\main\\resources\\trainSVM.txt.model";    //训练后得到的模型文件
    private String testTxt = "D:\\Java\\Workspace\\GraduateDesign\\FrontHalf\\src\\main\\resources\\testSVM.txt";
    //测试文件、模型文件、结果文件路径
    private final String[] str_result = {testTxt, str_model, "D:\\Java\\Workspace\\GraduateDesign\\FrontHalf\\src\\main\\resources\\result.txt"};
    private static ModelClassifySVM libSVM = null;
    */

    private String trainFileName = "D:\\Java\\Workspace\\GraduateDesign\\FrontHalf\\src\\main\\resources\\Model\\SVM\\DataSet-测试\\trainSVM.txt";
    private String modelFileName = "D:\\Java\\Workspace\\GraduateDesign\\FrontHalf\\src\\main\\resources\\Model\\SVM\\DataSet-测试\\trainSVM.txt.model";    //训练后得到的模型文件
    private String testFileName = "D:\\Java\\Workspace\\GraduateDesign\\FrontHalf\\src\\main\\resources\\Model\\SVM\\DataSet-测试\\testSVM.txt";
    private String resultFileName = "D:\\Java\\Workspace\\GraduateDesign\\FrontHalf\\src\\main\\resources\\Model\\SVM\\DataSet-测试\\result.txt";

    //将输入的train文件修改为trainDataList和trainLabelList
    public String[] str_trained = {"-s","0","-t","2","-g","2.0","-c","32","-m","500.0","-h","0", trainFileName};
    //将输入的 test 文件修改为 testDataList 和 testLabelList
    private final String[] str_result = {testFileName, modelFileName, resultFileName};
    private static ModelClassifySVM libSVM = null;
    /*
     * 私有化构造函数，并训练分类器，得到分类模型
     */
    private ModelClassifySVM(){

    }

    public static ModelClassifySVM getInstance(){
        if(libSVM==null)
            libSVM = new ModelClassifySVM();
        return libSVM;
    }

    private void readDataFromDB(){
        ArrayList<String> vectorStrList = new ArrayList<>(160000);
        ArrayList<Float> labelList = new ArrayList<>(16000);
        int expectCount = 10000; //期望的 训练集 + 测试集 数目
        JdbcUtils.getDataAndLabel(vectorStrList, labelList, expectCount);
        splitDataToTrainAndTest(vectorStrList, labelList);
    }

    /**
     * 将从表中抽取的数据划分为训练集与测试集
     */
    private void splitDataToTrainAndTest(ArrayList<String> vectorStrList, ArrayList<Float> labelList){
        int allCount = labelList.size();
        int initTrainCount = (int)(allCount * 0.9);

        for(int i=0; i<allCount; i++){
            try{
                String[] tempArr = vectorStrList.get(i).split(",");
                if(!isLegalVector(tempArr)) continue;

                //只保留后300位特征值
                int dataColumn = SoftParameters.TermVectorLength - SoftParameters.ContextVectorBeginIndex;
                float[] vector = new float[dataColumn];
                for(int j=SoftParameters.ContextVectorBeginIndex; j<tempArr.length; j++)
                    vector[j-SoftParameters.ContextVectorBeginIndex] = Float.parseFloat(tempArr[j]);

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
    private boolean isLegalVector(String[] tempArr){
        if(tempArr.length != SoftParameters.TermVectorLength) return false;

        for(int i=SoftParameters.WordVectorBeginIndex; i<SoftParameters.WordVectorBeginIndex+5; i++){
            if(!tempArr[i].equals("0.0")){
                return true;
            }
        }
        return false;
    }

    /*
     * 训练分类模型
     */
    public void trainByLibSVM(){
        try {
            //训练返回的是模型文件，其实是一个路径，可以看出要求改svm_train.java
			svm_train.main(str_trained, trainDataList, trainLabelList);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void originalTrainByLibSVM(){
        try {
            //训练返回的是模型文件，其实是一个路径，可以看出要求改svm_train.java
            svm_train_original.main(str_trained);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void dbTrainByLibSVM(){
        try {
            //训练返回的是模型文件，其实是一个路径，可以看出要求改svm_train.java
            svm_train_db.main(str_trained, trainDataList, trainLabelList);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /*
     * 预测分类,并返回准确率
     */
    public double testByLibSVM(){
        double accuracy=0;
        try {
            //测试返回的是准确率，可以看出要求改svm_predict.java
			svm_predict.main(str_result, testDataList, testLabelList);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return accuracy;
    }

    public double originalTestByLibSVM(){
        double accuracy=0;
        try {
            //测试返回的是准确率，可以看出要求改svm_predict.java
            svm_predict_original.main(str_result);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return accuracy;
    }

    public double dbTestByLibSVM(){
        double accuracy=0;
        try {
            //测试返回的是准确率，可以看出要求改svm_predict.java
            svm_predict_db.main(str_result, testDataList, testLabelList);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return accuracy;
    }

    public static void main(String[] args){
        ModelClassifySVM svmClassify = ModelClassifySVM.getInstance();
        svmClassify.readDataFromDB();

//        System.out.println("正在训练分类模型。。。。");
//        svmClassify.trainByLibSVM();
//        System.out.println("正在应用分类模型进行分类。。。。");
//        svmClassify.testByLibSVM();

//        System.out.println("正在训练分类模型。。。。");
//        svmClassify.originalTrainByLibSVM();
//        System.out.println("正在应用分类模型进行分类。。。。");
//        svmClassify.originalTestByLibSVM();

        System.out.println("正在训练分类模型。。。。");
        svmClassify.dbTrainByLibSVM();
        System.out.println("正在应用分类模型进行分类。。。。");
        svmClassify.dbTestByLibSVM();
    }
}
