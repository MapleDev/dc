package me.crycry.file.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashSet;
import java.util.List;

@Component
public class FileUtils {
    @Value("${DC.BASE_PATH}")
    public static String BASE_PATH="C:/Users/CRY/Desktop/毕业设计/testdoc";

    public static void listFiles(File dir,HashSet<File> files) {
        System.out.println(dir+"-------------- "+files);
        File[] subFiles = dir.listFiles();
        for (File f : subFiles) {
            if (!f.isDirectory()) {//是文件
                if (!f.getName().contains("~$") && (f.getName().endsWith(".docx"))) {
                    files.add(f);
                    System.out.println("找到文件：" + f.getName());
                } else {
                    // System.out.println("文件类型不支持！跳过处理该文件:"+f.getName());
                    continue;
                }
            } else {//是文件夹
                listFiles(f, files);
            }
        }
    }
}
