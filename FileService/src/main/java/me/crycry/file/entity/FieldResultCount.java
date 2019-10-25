package me.crycry.file.entity;

public class FieldResultCount {
    private FieldResult fieldResult;
    private Long count;

    public FieldResult getFieldResult() {
        return fieldResult;
    }

    public void setFieldResult(FieldResult fieldResult) {
        this.fieldResult = fieldResult;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "FieldResultCount{" +
                "fieldResult=" + fieldResult +
                ", count=" + count +
                '}';
    }
}
