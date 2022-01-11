package completion.FromRecordToModel;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/11/20 21:44
 **/
public class TestModel {
    public static void main(String[] args) throws InterruptedException {
        runLinearRegression();
    }

    /**
     * 目前训练时长太久，需要多线程，或者优化。
     * 参数训练出的都是NAN，这是为啥
     * @throws InterruptedException
     */
    private static void runLinearRegression() throws InterruptedException {
        ModelRegressionLinear m = new ModelRegressionLinear("trainData",0.01f,10);
        m.run();
    }
}
