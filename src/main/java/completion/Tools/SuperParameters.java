package completion.Tools;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/10/28 21:57
 **/
public class SuperParameters {
    //与ComputeVectorListWithContent有关的超参数
    public static final int ValidLeftDis = 20;      //当较近的expert在ITerm左边时，最大的合法字符距离
    public static final int ValidRighttDis = 30;    //当较近的expert在ITerm右边时，最大的合法字符距离
    public static final int ContextLeftTermCountWithExpert = 5;       //当较近的expert在ITerm左边时，取左边的几个term作为上下文
    public static final int ContextRightTermCountWithExpert = 5;      //当较近的expert在ITerm右边时，取右边的几个term作为上下文
    public static final int ContextLeftTermCountWithoutExpert = 3;        //当这一行没有expert时，取左边的几个term作为上下文
    public static final int ContextRightTermCountWithoutExpert = 3;        //当这一行没有expert时，取右边的几个term作为上下文
}
