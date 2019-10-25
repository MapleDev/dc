package me.crycry.file.entity;

import java.util.HashMap;

public class Result extends HashMap<String, Object> {
    public Result() {

    }

    public static Result ok(String msg) {
        Result result = new Result();
        result.put("msg", msg);
        result.put("success",true);
        return result;
    }
    public static Result ok() {
        Result result = new Result();
        result.put("success",true);
        return result;
    }

    public static Result error(String msg) {
        Result result = new Result();
        result.put("msg", msg);
        result.put("success",false);
        return result;
    }

    public static Result error(String msg, int code) {
        Result result = new Result();
        result.put("msg", msg);
        result.put("code",code);
        return result;
    }

}
