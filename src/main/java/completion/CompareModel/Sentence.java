package completion.CompareModel;

import java.io.File;

/**
 * @authorE Zilig Guan
 * @authorC 关哲林
 * @date 2022/4/25 20:17
 * @Description TODO
 **/
public class Sentence {
    private final File file;
    private final String entityCode;
    private final String fileName;
    private final String headName;
    private int headIndex;
    private final String tailName;
    private final String entityName;
    private int entityIndex;
    private final int sort;       //网页的排名[1,50]
    private final String value;

    public Sentence(File file, String entityCode, String fileName, String headName, int headIndex, String tailName, String entityName, int entityIndex, int sort, String value) {
        this.file = file;
        this.entityCode = entityCode;
        this.fileName = fileName;
        this.headName = headName;
        this.headIndex = headIndex;
        this.tailName = tailName;
        this.entityName = entityName;
        this.entityIndex = entityIndex;
        this.value = value;
        this.sort = sort;
    }


}
