package me.crycry.field.controller;


import me.crycry.field.entity.Field;
import me.crycry.field.entity.Result;
import me.crycry.field.repository.FieldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;

@RestController
public class FieldController {
    @Autowired
    private FieldRepository fieldRepository;

    @RequestMapping("/listAll")
    public Result listAll() {

        List<Field> fields = fieldRepository.findAll();
        Result result = Result.ok("");
        result.put("data", fields);

        return result;
    }

    @RequestMapping("/delete/{id}")
    @Transactional
    public Result deleteByFid(@PathVariable Long id) {

        int r = fieldRepository.deleteByFid(id);
        if (r < 1) {
            return Result.error("删除失败");
        }
        return Result.ok(r + "");
    }


    @RequestMapping("/save")
    public Result save(@RequestBody Field field) {
        System.out.println(field);
        field = fieldRepository.save(field);
        if (field != null)
            return Result.ok("");
        return Result.error("error");
    }


    @RequestMapping("/find/{id}")
    public Result find(@PathVariable Long id) {

        Field field = fieldRepository.findOne(id);
        Result result = Result.ok();
        result.put("data", field);
        return result;
    }

    @RequestMapping("/getFieldCount")
    public Result getFieldCount(){
        int count = fieldRepository.getFieldCount();
        Result result = Result.ok();
        result.put("count", count);
        return result;
    }

}
