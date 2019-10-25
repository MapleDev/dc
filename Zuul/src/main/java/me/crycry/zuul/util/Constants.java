package me.crycry.zuul.util;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public  static Map<String,Object> passUri=new HashMap<>();
    static {
        passUri.put("/login.html","");
        passUri.put("/doLogin","");
        passUri.put("/getVCode","");
    }
}
