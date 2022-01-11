package completion.Tools;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/5/25 16:47
 **/
public class NumberTools {
    //将int值转化为16位的二进制字符串，用于命名文件
    public static String intToBinStr(int number){
        if(number > 65535 || number < 1){
            System.out.println("function <intToBinStr>: illegal number, number = " + number);
            return "错误命名_" + String.valueOf(number);
        }
        String iniBinStr = Integer.toBinaryString(number);
        StringBuilder sb = new StringBuilder(iniBinStr);

        for(int i=sb.length(); i<16; i++){
            sb.insert(0,"0");
        }
        if(sb.length() != 16){
            System.out.println("错误命名_" + sb.toString());
        }
        return sb.toString();
    }

    //动态规划，计算两个word的编辑距离
    public static int computeEditDistance(String s1, String s2) {
        int[][] temp = new int[s1.length()][s2.length()];
        for (int i = 0; i < s1.toCharArray().length; i++) {
            temp[i][0] = i;
        }
        for (int j = 0; j < s2.toCharArray().length; j++) {
            temp[0][j] = j;
        }
        for (int i = 1; i < s1.toCharArray().length; i++) {
            for (int j = 1; j < s2.toCharArray().length; j++) {
                if (s1.charAt(i) == s2.charAt(j)) {
                    temp[i][j] = temp[i - 1][j - 1];
                } else {
                    int op1 = temp[i - 1][j] + 1;
                    int op2 = temp[i - 1][j - 1] + 1;
                    int op3 = temp[i][j - 1] + 1;
                    temp[i][j] = Math.min(Math.min(op1, op2), op3);
                }
            }
        }
        return temp[s1.length() - 1][s2.length() - 1];
    }

}
