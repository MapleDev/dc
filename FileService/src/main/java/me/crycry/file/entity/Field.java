package me.crycry.file.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Field {
    @Id
    @GeneratedValue
    private Long fid;
    private String fieldName;
    private String regex;
    private String description;
    private Integer useCount;
    private String remark;

    public Long getFid() {
        return fid;
    }

    public void setFid(Long fid) {
        this.fid = fid;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUseCount() {
        return useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }


    @Override
    public String toString() {
        return "Field{" +
                "fid=" + fid +
                ", fieldName='" + fieldName + '\'' +
                ", regex='" + regex + '\'' +
                ", description='" + description + '\'' +
                ", useCount=" + useCount +
                ", remark='" + remark + '\'' +
                '}';
    }
}
