package me.crycry.file.controller;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeUtility;
import me.crycry.file.common.ProcessQueue;
import me.crycry.file.entity.Result;
import me.crycry.file.repository.TaskRepository;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class FileController {
    @Value("${DC.BASE_PATH}")
    private String REPOSITORY_BASE_PATH;

    @Autowired
    private TaskRepository taskRepository;

    private String DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss z"; // (Wed, 4 Jul 2001 12:08:56)

    private static final long serialVersionUID = -8453502699403909016L;



    @RequestMapping("/list")
    public Map<String,Object> list(HttpServletRequest request,@RequestBody Map<String,Object> map){
        System.out.println(map);
        try {
            boolean onlyFolders = "true".equalsIgnoreCase(request.getParameter("onlyFolders"));
            String path = (String) map.get("path");
            //LOG.debug("list path: Paths.get('{}', '{}'), onlyFolders: {}", REPOSITORY_BASE_PATH, path, onlyFolders);
            System.out.println(REPOSITORY_BASE_PATH+"  "+path);
            List<Map<String,Object>> resultList = new ArrayList<>();
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(REPOSITORY_BASE_PATH, path))) {
                SimpleDateFormat dt = new SimpleDateFormat(DATE_FORMAT);
                // Calendar cal = Calendar.getInstance();
                for (Path pathObj : directoryStream) {
                    BasicFileAttributes attrs = Files.readAttributes(pathObj, BasicFileAttributes.class);
                    if (onlyFolders&&!attrs.isDirectory()) {
                        continue;
                    }
                    Map<String,Object> el = new HashMap<>();
                    el.put("name", pathObj.getFileName().toString());
                    el.put("rights", "drwxrwxrwx");
                    el.put("date", dt.format(new Date(attrs.lastModifiedTime().toMillis())));
                    el.put("size", attrs.size());
                    el.put("type", attrs.isDirectory() ? "dir" : "file");
                    resultList.add(el);
                }
            } catch (IOException ex) {
            }
            Map<String,Object> result = new HashMap<>();
            result.put("result", resultList);
            return result;
        } catch (Exception e) {
            // LOG.error("list:" + e.getMessage(), e);
            e.printStackTrace();
            Map<String,Object> result = new HashMap<>();
            result.put("error", true);
            return result;
        }
    }


    @RequestMapping("/download")
    public void download(HttpServletResponse response,@RequestParam(name = "path") String path) throws IOException {
       // String path =".gitignore";// (String)para.get("path");
        File file = new File(REPOSITORY_BASE_PATH, path);
        if (!file.isFile()) {
            // if not a file, it is a folder, show this error.
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Resource Not Found");
            return;
        }
        response.setHeader("Content-Type", "application/force-download");
        response.setHeader("Content-Disposition", "inline; filename=\"" + MimeUtility.encodeWord(file.getName()) + "\"");
        try (SeekableByteChannel channel = Files.newByteChannel(file.toPath())) {
            byte[] buffer = new byte[256 * 1024];
            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
            for (int length = 0; (length = channel.read(byteBuffer)) != -1;) {
                response.getOutputStream().write(buffer, 0, length);
                byteBuffer.clear();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw ex;
        } finally {
            response.getOutputStream().flush();
        }
    }




   @RequestMapping("/upload")
    private void uploadFile(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        // URL: $config.uploadUrl, Method: POST, Content-Type: multipart/form-data
        // Unlimited file upload, each item will be enumerated as file-1, file-2, etc.
        // [$config.uploadUrl]?destination=/public_html/image.jpg&file-1={..}&file-2={...}
        if (true) {
            try {
                String destination = null;
                Map<String, InputStream> files = new HashMap<>();
                ServletFileUpload sfu = new ServletFileUpload(new DiskFileItemFactory());
                sfu.setHeaderEncoding("UTF-8");
                Map<String, List<FileItem>> fileMap = sfu.parseParameterMap(request);
                System.out.println(fileMap.keySet());

                for (FileItem item : new ArrayList<FileItem>()) {
                    if (item.isFormField()) {
                        // Process regular form field (input type="text|radio|checkbox|etc", select, etc).
                        if ("destination".equals(item.getFieldName())) {
                            destination = item.getString("UTF-8");
                        }
                    } else {
                        // Process form file field (input type="file").
                        files.put(item.getName(), item.getInputStream());
                    }
                }
                if (files.isEmpty()) {
                    throw new Exception("file size  = 0");
                } else {
                    for (Map.Entry<String, InputStream> fileEntry : files.entrySet()) {
                        Path path = Paths.get(REPOSITORY_BASE_PATH + destination, fileEntry.getKey());
                        if (!write(fileEntry.getValue(), path)) {
                            throw new Exception("write error");
                        }
                    }

                    Result result = Result.ok();
                    result.put("success",true);
                    response.setContentType("application/json;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.print(result);
                    out.flush();
                }
            } catch (Exception e) {
                //LOG.error("Cannot write file", e);
                throw new ServletException("Cannot write file", e);
            }
        } else {
            throw new ServletException("error");
        }
    }

    @RequestMapping("/createFolder")
    public  Result createFolder(@RequestBody Map<String,String> params) throws ServletException {
        try {
            Path path = Paths.get(REPOSITORY_BASE_PATH, params.get("newPath"));
            Files.createDirectories(path);
            return Result.ok();
        } catch (FileAlreadyExistsException ex) {
            return Result.ok();
        } catch (IOException e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping("/remove")
    private Result remove(@RequestBody Map<String,Object> params) throws ServletException, IOException {
        List<String> paths = (List<String>) params.get("items");
        StringBuilder error = new StringBuilder();
        StringBuilder success = new StringBuilder();
        for (Object obj : paths) {
            Path path = Paths.get(REPOSITORY_BASE_PATH, obj.toString());
            if (true) {
                FileUtils.deleteDirectory(path.toFile());
                error.append(error.length() > 0 ? "\n" : "Can't remove: \n/")
                        .append(path.subpath(1, path.getNameCount()).toString());
            } else {
                success.append(error.length() > 0 ? "\n" : "\nBut remove remove: \n/")
                        .append(path.subpath(1, path.getNameCount()).toString());

            }
        }
        if (error.length() > 0) {
            if (success.length() > 0) {
                success.append("\nPlease refresh this folder to list last result.");
            }
            return Result.error("error");
           // throw new ServletException(error.toString() + success.toString());
        } else {
            return Result.ok();
        }
    }

    @RequestMapping("/rename")
    public  Result rename(@RequestBody Map<String,String> params) throws ServletException {
        try {
            String path = params.get("item");
            String newpath = params.get("newItemPath");
            File srcFile = new File(REPOSITORY_BASE_PATH, path);
            File destFile = new File(REPOSITORY_BASE_PATH, newpath);
            if (srcFile.isFile()) {
                FileUtils.moveFile(srcFile, destFile);
            } else {
                FileUtils.moveDirectory(srcFile, destFile);
            }
            return Result.ok();
        } catch (IOException e) {
            return Result.error("error");
        }
    }


    @RequestMapping("/fileinfo")
    public Result getFileCount(){
        HashSet<File> r  = new HashSet<>();
        me.crycry.file.common.FileUtils.listFiles(new File(REPOSITORY_BASE_PATH),r);
        Result result = Result.ok();
        int queueCount = ProcessQueue.taskQueue.size();
        int finishedCount = taskRepository.queryFinishedTaskCount();
        result.put("queueCount",queueCount);
        result.put("finishedCount",finishedCount);
        result.put("fileCount",r.size());
        return result;
    }


    public Map<String,Object> createFolder(HttpServletRequest request){
        Map<String,Object> result = new HashMap<>();
        try {
            Path path = Paths.get(REPOSITORY_BASE_PATH, request.getParameter("newPath"));
            Files.createDirectories(path);
            result.put("success",true);
            return result;
        } catch (FileAlreadyExistsException ex) {
            result.put("success",true);
            return result;
        } catch (IOException e) {
            result.put("success",null);
            result.put("error",true);
            return result;
        }
    }
    private String getPermissions(Path path) throws IOException {
        System.out.println(path.getFileName());
        PosixFileAttributeView fileAttributeView = Files.getFileAttributeView(path, PosixFileAttributeView.class);
        System.out.println(fileAttributeView);
        PosixFileAttributes readAttributes = fileAttributeView.readAttributes();
        Set<PosixFilePermission> permissions = readAttributes.permissions();
        return PosixFilePermissions.toString(permissions);
    }


    private boolean write(InputStream inputStream, Path path) {
        try {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }



}
