package me.crycry.file.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class FieldResult {
    //结果的id
    @Id
    @GeneratedValue
    private Long frid;
    private String prefix;
    private String suffix;
    //结果所属的属性名
    private String fieldName;
    //所属的任务
    private Long taskId;
    private String result;
    private String file;


    public Long getFrid() {
        return frid;
    }

    public void setFrid(Long frid) {
        this.frid = frid;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        return "FieldResult{" +
                "frid=" + frid +
                ", prefix='" + prefix + '\'' +
                ", suffix='" + suffix + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", taskId=" + taskId +
                ", result='" + result + '\'' +
                ", file='" + file + '\'' +
                '}';
    }
}
