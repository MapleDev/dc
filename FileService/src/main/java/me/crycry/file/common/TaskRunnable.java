package me.crycry.file.common;

import me.crycry.file.entity.Field;
import me.crycry.file.entity.FieldResult;
import me.crycry.file.entity.Task;
import me.crycry.file.repository.FieldResultRepository;
import me.crycry.file.repository.TaskRepository;
import org.apache.poi.POIXMLException;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 该类实现Runnable接口，用于创建数据清洗线程，执行数据清洗任务
 */
public class TaskRunnable implements Runnable {
    private Task task;
    private TaskRepository taskRepository;
    private FieldResultRepository fieldResultRepository;

    public TaskRunnable(Task task, TaskRepository taskRepository, FieldResultRepository fieldResultRepository) {
        this.task = task;
        this.taskRepository = taskRepository;
        this.fieldResultRepository = fieldResultRepository;
    }

    @Override
    public void run() {
        task.setStatus("处理中");
        task = taskRepository.save(task);
        ProcessQueue.processing = true;

        String[] fileStrs = task.getFiles().split(",");
        List<Field> fields = task.getFields();
        //List<File> files = new ArrayList<File>();
        HashSet<File> files = new HashSet<>();
        for (String fileStr : fileStrs) {
            File file = new File(FileUtils.BASE_PATH + fileStr);
            if (!file.exists()) {
                System.out.println("文件 " + file.getAbsolutePath() + " 不存在！跳过处理该文件");
                continue;
            }
            if (!file.isDirectory()) {//是文件
                if (fileStr.endsWith(".doc") || fileStr.endsWith(".docx")) {
                    files.add(file);
                } else if(fileStr.endsWith(".zip")){
                    try {
                        ZipUtil.unzip(file.getAbsolutePath(),FileUtils.BASE_PATH+"/unzip",true);
                        FileUtils.listFiles(new File(FileUtils.BASE_PATH+"/unzip/"+file.getName().replace(".zip","")), files);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else {
                    System.out.println("文件类型不支持！跳过处理该文件:" + file.getName());
                    continue;
                }
            } else {//是文件夹
                FileUtils.listFiles(file, files);
            }
        }//foreach(fileStrs)


        for (File file : files) {
            System.out.println("处理：" + file.getName());
            String content = "";
            try {
                //获取文件的字符串内容
                FileInputStream fis = new FileInputStream(file);
                XWPFDocument document = new XWPFDocument(fis);
                List<XWPFParagraph> paragraphs = document.getParagraphs();
                for (XWPFParagraph p : paragraphs) {
                    content += p.getText();
                }
                System.out.println(content);
                //System.out.println(content);
                //遍历每个需要匹配的属性
                for (Field field : fields) {
                    String regexStrs = field.getRegex();
                    regexStrs="(.{0,10})"+regexStrs;
                    regexStrs+="(.{0,10})";
                    System.out.println("----------  "+regexStrs);
                    List<String> matchList = new ArrayList<>();
                    //属性中拿到所有的正则表达式，进行匹配
                    for (String regex : regexStrs.split("\\|")) {
                        System.out.println("---- " + regex);
                        Pattern p = Pattern.compile(regex);
                        Matcher matcher = p.matcher(content);
                        while (matcher.find()) {
                            //System.out.println(matcher.group(0));
                            FieldResult fr = new FieldResult();
                            fr.setFrid(System.currentTimeMillis());
                            fr.setFieldName(field.getFieldName());
                            fr.setPrefix(matcher.group(1));
                            fr.setResult(matcher.group(2));
                            fr.setSuffix(matcher.group(3));
                            fr.setTaskId(task.getTaskId());
                            fr.setFile(file.getAbsolutePath());
                            System.out.println(fr);
                            fieldResultRepository.save(fr);
                        }
                    }
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (POIXMLException e) {
                System.out.println(e.getMessage());
            } finally {
                ProcessQueue.processing = false;
            }
            //System.out.println(Database.to_String());
        }//foreach(files)

        task.setStatus("已完成");
        task.setEndTime(new Date());
        taskRepository.saveAndFlush(task);
        ProcessQueue.processing = false;

    }

}
