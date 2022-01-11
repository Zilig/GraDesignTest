package completion.FromContentToRecord;

import com.hankcs.hanlp.seg.common.Term;

import java.io.Serializable;
import java.util.List;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2021/9/14 18:00
 **/
public class FileTermInfor implements Serializable {
    private static final long serialVersionUID = 1946463362048618615L;
    private String fileName;
    private int webType;     //网页类型，不同的网页类型影响识别结果。（还没想好如何影响）
    private int sort;       //网页的排名[1,50]
    private int fileLineCount;  //文件总行数
    private int fileLegalLineCount; //文件有效行数
    private int sumLegalLineLength; //文件有效字符数
    private int expertTypeTermCount;    //该文件中有多个少专家term
    private int institutionTypeTermCount;   //该文件中有多个少单位term
    private List<MyTerm> institutionTermList;
    private List<Float> institutionScoreList;
    private List<List<Term>> institutionTermContextList;

    public FileTermInfor(String fileName, int sort){
        this.sort = sort;
        webType = 0;
        this.fileName = fileName;
    }

    public int getSort() {
        return sort;
    }

    public int getSumLegalLineLength() {
        return sumLegalLineLength;
    }

    public void setSumLegalLineLength(int sumLegalLineLength) {
        this.sumLegalLineLength = sumLegalLineLength;
    }

    public int getExpertTypeTermCount() {
        return expertTypeTermCount;
    }

    public void setExpertTypeTermCount(int expertTypeTermCount) {
        this.expertTypeTermCount = expertTypeTermCount;
    }

    public int getInstitutionTypeTermCount() {
        return institutionTypeTermCount;
    }

    public void setInstitutionTypeTermCount(int institutionTypeTermCount) {
        this.institutionTypeTermCount = institutionTypeTermCount;
    }

    public List<Float> getInstitutionScoreList() {
        return institutionScoreList;
    }

    public void setInstitutionScoreList(List<Float> institutionScoreList) {
        this.institutionScoreList = institutionScoreList;
    }

    public FileTermInfor(String fileName, int sort, int fileLineCount, int fileLegalLineCount, int sumLegalLineLength,
                         int expertTypeTermCount, int institutionTypeTermCount,
                         List<MyTerm> institutionTermList, List<Float>institutionScoreList, List<List<Term>> institutionTermContextList){
        this.fileName = fileName;
        webType = 0;
        this.sort = sort;
        this.fileLineCount = fileLineCount;
        this.fileLegalLineCount = fileLegalLineCount;
        this.sumLegalLineLength = sumLegalLineLength;
        this.expertTypeTermCount = expertTypeTermCount;
        this.institutionTypeTermCount = institutionTypeTermCount;
        this.institutionTermList = institutionTermList;
        this.institutionScoreList = institutionScoreList;
        this.institutionTermContextList = institutionTermContextList;
    }

    public String getFileName() {
        return fileName;
    }

    public int getWebType() {
        return webType;
    }

    public int getFileLineCount() {
        return fileLineCount;
    }

    public int getFileLegalLineCount() {
        return fileLegalLineCount;
    }

    public List<MyTerm> getInstitutionTermList() {
        return institutionTermList;
    }

    public List<List<Term>> getInstitutionTermContextList() {
        return institutionTermContextList;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setWebType(int webType) {
        this.webType = webType;
    }

    public void setFileLineCount(int fileLineCount) {
        this.fileLineCount = fileLineCount;
    }

    public void setFileLegalLineCount(int fileLegalLineCount) {
        this.fileLegalLineCount = fileLegalLineCount;
    }

    public void setInstitutionTermList(List<MyTerm> institutionTermList) {
        this.institutionTermList = institutionTermList;
    }

    public void setInstitutionTermContextList(List<List<Term>> institutionTermContextList) {
        this.institutionTermContextList = institutionTermContextList;
    }

    public String toString(){
        return "{fileName = " + fileName
                + ", webType = " + webType
                + ", sort = " + sort
                + ", fileLineCount = " + fileLineCount
                + ", fileLegalLineCount = " + fileLegalLineCount
                + ", sumLegalLineLength = " + sumLegalLineLength
                + ", expertTypeTermCount = " + expertTypeTermCount
                + ", institutionTypeTermCount = " + institutionTypeTermCount
                + ", termList.size() = " + institutionTermList.size()
                + "}";
    }
}
