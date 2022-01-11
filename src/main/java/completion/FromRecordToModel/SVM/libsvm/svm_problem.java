package completion.FromRecordToModel.SVM.libsvm;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/11/22 20:50
 **/
public class svm_problem implements java.io.Serializable
{
    public int l;
    public double[] y;
    public svm_node[][] x;
}
