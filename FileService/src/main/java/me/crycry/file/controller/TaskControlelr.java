package me.crycry.file.controller;

import me.crycry.file.common.ProcessQueue;
import me.crycry.file.common.TaskRunnable;
import me.crycry.file.entity.*;
import me.crycry.file.repository.FieldResultRepository;
import me.crycry.file.repository.TaskRepository;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

@RestController()
public class TaskControlelr {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private FieldResultRepository fieldResultRepository;


    @RequestMapping("/task/listAll")
    public Result listAll() {
        Result result = Result.ok();
        List<Task> tasks = taskRepository.findAll();
        System.out.println(tasks);
        result.put("data", tasks);
        return result;
    }


    @RequestMapping("/task/find/{id}")
    public Result find(@PathVariable Long id) {

        Result result = Result.ok();
        Task task = taskRepository.findOne(id);
        result.put("data", task);
        return result;
    }


    @RequestMapping("/task/del/{id}")
    public Result del(@PathVariable Long id) {
        Result result = Result.ok();
        taskRepository.delete(id);
        return result;
    }


    @RequestMapping("/task/submit")
    public Result submit(HttpServletRequest request) {
        String field_ids = request.getParameter("field_ids");
        String files = request.getParameter("files");
        String task_name = request.getParameter("task_name");
        String task_desc = request.getParameter("task_desc");
        System.out.println(field_ids);
        System.out.println(files);
        System.out.println(task_desc);
        System.out.println(task_name);
        Task task = new Task();
        task.setStartTime(new Date());
        task.setCreater("chenruiyin");
        task.setDescription(task_desc);
        task.setTaskName(task_name);
        task.setFiles(files);
        List<Field> fields = new ArrayList<>();
        String[] fieldsStrs = field_ids.split(",");
        for (String id : fieldsStrs) {
            Field field = new Field();
            field.setFid(Long.parseLong(id));
            fields.add(field);
        }

        System.out.println(fields);

        task.setFields(fields);
        task.setStatus("队列中");
        TaskRunnable taskRunnable = new TaskRunnable(task, taskRepository, fieldResultRepository);
        ProcessQueue.taskQueue.add(taskRunnable);
        taskRepository.save(task);
        return Result.ok();

    }


    /**
     * 获取所有的清洗结果，每个文件一条记录
     *
     * @param id
     * @return
     */
    @RequestMapping("/task/listResult/{id}")
    public Result listResult(@PathVariable Long id) {

        List<String> files = fieldResultRepository.listFiles(id);
        List resultList = new ArrayList();
        System.out.println(files);
        for (String file : files) {
            List<Map<String, Object>> r = fieldResultRepository.listRsultGroupByFieldName(id, file);
            System.out.println("rrr   " + r);
            Map record = new HashMap();
            for (Map<String, Object> m : r) {
                FieldResultCount frc = new FieldResultCount();
                frc.setCount((Long) m.get("count"));
                frc.setFieldResult((FieldResult) m.get("result"));
                record.put(((FieldResult) m.get("result")).getFieldName(), frc);
            }

            resultList.add(record);


        }

        Result result = Result.ok();
        result.put("data", resultList);
        //System.out.println(results);
        return result;

    }


    /**
     * 获取任务中所有的属性（作为table的thead）
     *
     * @param id
     * @return
     */
    @RequestMapping("/task/listResultFieldNames/{id}")
    public Result listResultFieldNames(@PathVariable Long id) {
        List<String> fieldNames = fieldResultRepository.listResultFieldNames(id);
        Result result = Result.ok();
        result.put("data", fieldNames);
        return result;
    }

    /**
     * 删除结果的一行记录（一个文件的记录）
     *
     * @param request
     * @return
     */
    @RequestMapping("/task/delResultByFile")
    @Transactional
    public Result delResultByFile(HttpServletRequest request) {
        String taskId = request.getParameter("taskId");
        String frid = request.getParameter("frid");
        System.out.println(taskId + "   " + frid);

        FieldResult r = fieldResultRepository.findByTaskIdAnAndFrid(Long.parseLong(taskId), Long.parseLong(frid));
        int count = fieldResultRepository.deleteByFile(r.getFile());
        System.out.println("影响行数：" + count);

        if (count == 0) return Result.error("删除失败");
        return Result.ok();
    }


    @RequestMapping("/task/listMultiResult/{frid}")
    public Result listMultiResult(@PathVariable Long frid) {

        FieldResult fr = fieldResultRepository.findOne(frid);
        List<FieldResult> fieldResults = fieldResultRepository.listMultiResult(fr.getTaskId(), fr.getFieldName(), fr.getFile());
        Result result = Result.ok();
        result.put("data", fieldResults);
        return result;

    }

    @RequestMapping("/task/checkResult/{frid}")
    public Result checkResult(@PathVariable Long frid) {
        FieldResult fr = fieldResultRepository.findOne(frid);
        int c = fieldResultRepository.checkResult(fr.getTaskId(), frid, fr.getFieldName(), fr.getFile());
        if (c > 0) return Result.ok();
        else return Result.error("删除0条记录");

    }

    @RequestMapping(value = "/task/downloadResult/{taskId}")
    public void downloadResult(@PathVariable Long taskId, @RequestBody Map<String,List<List<String>>> data,HttpServletResponse response, HttpServletRequest request) throws IOException {

        System.out.println("===============\n\n"+data+"\n\n============");
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("result");
        XSSFRow row = sheet.createRow(0);
        List<String> fieldNames = fieldResultRepository.listResultFieldNames(taskId);
        for (int i = 0; i < fieldNames.size(); i++) {
            XSSFCell cell = row.createCell(i);
            cell.setCellValue(fieldNames.get(i));


        }

        List<List<String>> records = data.get("data");
        for(int i=1;i<=records.size();i++){
                XSSFRow r = sheet.createRow(i);
               for(int j=0;j<records.size();j++){
                   XSSFCell c = r.createCell(j);
                   c.setCellValue(records.get(i-1).get(j));
               }
        }

        FileOutputStream fos = new FileOutputStream("D:\\test\\1.xlsx");
        response.setHeader("Content-Disposition", "attachment; filename=ccc.xlsx");
        //OutputStream outputStream = response.getOutputStream();
        workbook.write(fos);
        fos.flush();
        fos.close();


    }


}
