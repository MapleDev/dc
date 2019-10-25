package me.crycry.file.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
@Entity
public class Task {
    @Id
    @GeneratedValue
    private long taskId;
    private String taskName;
    private Date startTime;
    private Date endTime;
    private String creater;
    private String description;
    @OneToMany
    private List<Field> fields;
    private String files;
    private String status;//处理中，已完成，失败

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String tastName) {
        this.taskName = tastName;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "Task{" +
                "tastId=" + taskId +
                ", tastName='" + taskName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", creater='" + creater + '\'' +
                ", description='" + description + '\'' +
                ", fields=" + fields +
                ", files=" + files +
                ", status='" + status + '\'' +
                '}';
    }
}
