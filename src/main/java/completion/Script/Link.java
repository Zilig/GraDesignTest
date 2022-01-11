package completion.Script;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/11/5 18:28
 **/
public class Link {
    private static class LNode{
        int val;
        LNode next = null;

        public LNode(int val){
            this.val = val;
        }
    }

    public static void main(String[] args) {
        int[] input = {1,2,2,4,3,3,5,2,2,3};
        LNode emptyHead = new LNode(-1);
        LNode temp = emptyHead;
        for(int i=0; i<input.length; i++){
            temp.next = new LNode(input[i]);
            temp = temp.next;
        }

        solve(emptyHead);

        temp = emptyHead.next;
        while (temp.next != null){
            System.out.println(temp.val);
            temp = temp.next;
        }

    }

    private static void solve(LNode emptyHead){
        LNode indexNode = emptyHead.next;

        while (indexNode.next != null){
            LNode first = indexNode;
            LNode second = first.next;

            while(second != null){
                if(second.val == indexNode.val){
                    first.next = second.next;
                    first = first.next;
                    if(first != null) second = first.next;
                    else second = null;
                }
                else{
                    first = first.next;
                    second = second.next;
                }
            }

            indexNode = indexNode.next;
        }
    }

}
