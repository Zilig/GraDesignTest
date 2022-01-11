package completion.FromContentToRecord;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/9/14 20:47
 **/
public class EntityTermInfor implements Serializable {
    private static final long serialVersionUID = 1156346730345579983L;
    private int groupID;    //不同的实体可能从属于同一个机构，或者有密切联系，进行分组。对于训练集，暂时不需要group，值为-1.
    private int entityType;     //1为训练集，2为测试集。
    private String entityCode;  //实体编码，即16进制名
    private String expertName;  //专家名称
    private String institutionName; //单位名称，如果entityType=1，非空，否则必为空。
    private ArrayList<FileTermInfor> fileTermInforList;     //保存该实体所有的文件的FileTermInfor

    public EntityTermInfor(int groupID, int entityType, String entityCode, String expertName, String institutionName){
        this.groupID = groupID;
        this.entityType = entityType;
        this.entityCode = entityCode;
        this.expertName = expertName;
        this.institutionName = institutionName;
    }

    public EntityTermInfor(int groupID, int entityType, String entityCode, String expertName, String institutionName,
                           ArrayList<FileTermInfor> fileTermInforList){
        this.groupID = groupID;
        this.entityType = entityType;
        this.entityCode = entityCode;
        this.expertName = expertName;
        this.institutionName = institutionName;
        this.fileTermInforList = fileTermInforList;
    }

    public String getEntityCode() {
        return entityCode;
    }

    public String getExpertName() {
        return expertName;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public ArrayList<FileTermInfor> getFileTermInforList() {
        return fileTermInforList;
    }

    public void setFileTermInforList(ArrayList<FileTermInfor> fileTermInforList) {
        this.fileTermInforList = fileTermInforList;
    }

    public String toString(){
        return    "{groupID = " + groupID
                + ", entityType = " + entityType
                + ", entityCode = " + entityCode
                + ", expertName = " + expertName
                + ", institutionName = " + institutionName
                + ", fileTermInforList.size() = " + fileTermInforList.size()
                + "}";
    }

    public void printDetail(){
        System.out.println("entityTermInfor.toString() = " + toString());
        for(int j=0; j<fileTermInforList.size(); j++){
            FileTermInfor fileTermInfor = fileTermInforList.get(j);
            System.out.println("fileTermInfor.toString() = " + fileTermInfor.toString());
        }
    }
}
