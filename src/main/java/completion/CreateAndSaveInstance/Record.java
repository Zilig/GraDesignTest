package completion.CreateAndSaveInstance;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2022/3/23 14:52
 * @Description 数据库特征表中的一行
 **/
public class Record {
    //一些指示信息
    private String DictCode;
    private String fileName;
    private String expertName;
    private String institutionName;
    private String entityName;
    private float score;           //相似度，可转化为标签

    //词汇特征
    private int entityLen;   //实体长度
    private int entityLineLen;   //实体所在行的长度
    private int distanceFromLine;   //实体在行中的起始位置
    private int distanceFromBegin;  //从文档开头至该实体的字符的数目
    private int distanceFromHead;   //与最近的头实体间的字符数目
    private int containKeyType;     //实体包含的关键词的类型，0时表示不包含

    //文档特征
    private int sort;       //网页的排名[1,50]
    private int fileLineCount;  //文件总行数
    private int fileLegalLineCount; //文件有效行数
    private int fileDigitCount; //文件有效字符数
    private int expertTypeTermCount;    //该文件中有多个少专家term（头实体）
    private int institutionTypeTermCount;   //institutionTypeTermCount
    private int dispersion; //文档行离散度，目前设置为0

    //上下文文本
    private String aboveContext;    //实体的上文
    private String innerContext;    //实体的内文
    private String belowContext;    //实体的下文

    public String getDictCode() {
        return DictCode;
    }

    public void setDictCode(String dictCode) {
        DictCode = dictCode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getExpertName() {
        return expertName;
    }

    public void setExpertName(String expertName) {
        this.expertName = expertName;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getAboveContext() {
        return aboveContext;
    }

    public void setAboveContext(String aboveContext) {
        this.aboveContext = aboveContext;
    }

    public String getInnerContext() {
        return innerContext;
    }

    public void setInnerContext(String innerContext) {
        this.innerContext = innerContext;
    }

    public String getBelowContext() {
        return belowContext;
    }

    public void setBelowContext(String belowContext) {
        this.belowContext = belowContext;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getFileLineCount() {
        return fileLineCount;
    }

    public void setFileLineCount(int fileLineCount) {
        this.fileLineCount = fileLineCount;
    }

    public int getFileLegalLineCount() {
        return fileLegalLineCount;
    }

    public void setFileLegalLineCount(int fileLegalLineCount) {
        this.fileLegalLineCount = fileLegalLineCount;
    }

    public int getFileDigitCount() {
        return fileDigitCount;
    }

    public void setFileDigitCount(int fileDigitCount) {
        this.fileDigitCount = fileDigitCount;
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

    public int getDispersion() {
        return dispersion;
    }

    public int getEntityLen() {
        return entityLen;
    }

    public void setEntityLen(int entityLen) {
        this.entityLen = entityLen;
    }

    public int getEntityLineLen() {
        return entityLineLen;
    }

    public void setEntityLineLen(int entityLineLen) {
        this.entityLineLen = entityLineLen;
    }

    public int getDistanceFromLine() {
        return distanceFromLine;
    }

    public void setDistanceFromLine(int distanceFromLine) {
        this.distanceFromLine = distanceFromLine;
    }

    public int getDistanceFromBegin() {
        return distanceFromBegin;
    }

    public void setDistanceFromBegin(int distanceFromBegin) {
        this.distanceFromBegin = distanceFromBegin;
    }

    public int getDistanceFromHead() {
        return distanceFromHead;
    }

    public void setDistanceFromHead(int distanceFromHead) {
        this.distanceFromHead = distanceFromHead;
    }

    public int getContainKeyType() {
        return containKeyType;
    }

    public void setDispersion(int dispersion) {
        this.dispersion = dispersion;
    }

    public void setContainKeyType(int containKeyType) {
        this.containKeyType = containKeyType;
    }
}
