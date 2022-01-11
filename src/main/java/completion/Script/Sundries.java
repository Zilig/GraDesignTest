package completion.Script;

import java.io.File;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/8/29 22:16
 **/
public class Sundries {
    public static void main(String[] args) {
        renameDirectory();
    }

    //将文件夹下所有的文件重命名
    private static void renameDirectory(){
        String[] dirList = {//"D:\\Java\\Workspace\\GraDesign1\\src\\main\\resources\\HTMLs_mis",
                //"D:\\Java\\Workspace\\GraDesign1\\src\\main\\resources\\URLs_mis"
                "D:\\Java\\Workspace\\GraDesign1\\src\\main\\resources\\Contents_mis"
                            };

        for(String dir : dirList){
            File contentDirMis = new File(dir);
            File[] contentMisDirs = contentDirMis.listFiles();//注意:这里只能用listFiles()，不能使用list()
            int count = 0;
            for(int i=0; i<contentMisDirs.length; i++) {
                String oldName = contentMisDirs[i].getName();
                File oldFile = contentMisDirs[i];


                System.out.println(oldName);
                String[] nameArr = oldName.split(" ");
                //String suffix = nameArr[3];
                //int length = suffix.length();
                //suffix = suffix.substring(length-4, length);

                String newName = dir + "\\" + nameArr[0] + " " + nameArr[1];
                System.out.println("newName = " + newName);
                File newFile = new File(newName);

                if(oldFile.exists()){
                    oldFile.renameTo(newFile);
                }
            }
        }
    }
}
