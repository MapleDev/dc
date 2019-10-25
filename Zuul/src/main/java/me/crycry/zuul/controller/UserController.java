package me.crycry.zuul.controller;

import me.crycry.zuul.entity.Result;
import me.crycry.zuul.entity.User;
import me.crycry.zuul.service.VerifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import sun.security.provider.MD5;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private VerifyService verifyService;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/getVCode")
    public void getVCode(HttpServletRequest request, HttpServletResponse response) {

        BufferedImage bi = verifyService.createVcode(request);

        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            ImageIO.write(bi, "jpg", out);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @RequestMapping("/doLogin")
    public Map<String, Object> login(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String vcode = request.getParameter("vcode");

        Map<String, Object> result = new HashMap<>();
        if (!verifyService.checkVcode(request, vcode)) {
            result.put("success", false);
            result.put("msg", "验证码错误");
            return result;
        }

        User user = restTemplate.getForObject("http://userservice/checkUser?username=" + username + "&password=" + password, User.class);
        System.out.println("=========  " + user);
        if (user != null) {
            System.out.println("login:" + request.getSession().getId());
            request.getSession().setAttribute("user", user);
            request.getSession().setAttribute("username",user.getUsername());
            result.put("success", true);
            return result;
        }
        result.put("success", false);
        return result;
    }

    @RequestMapping("/permitUser/{id}")
    public Result permitUser(HttpServletRequest request, @PathVariable Long id) {
        if (!checkAdmin(request)) {
            Result result = Result.error("没有权限");
            return result;
        }
        Result result = restTemplate.getForObject("http://userservice/permitUser/" + id, Result.class);
        result.put("data", "");
        return result;
    }


    @RequestMapping("/listUnPermitUser")
    public Result listUnPermitUser(HttpServletRequest request) {
        if (!checkAdmin(request)) {
            Result result = Result.error("没有权限");
            return result;
        }
        Result result = restTemplate.getForObject("http://userservice/listUnPermitUser/", Result.class);
        return result;
    }


    @RequestMapping("/delUser/{id}")
    public Result delUser(HttpServletRequest request, @PathVariable Long id) {
        if (!checkAdmin(request)) {
            Result result = Result.error("没有权限");
            return result;
        }
        Result result = restTemplate.getForObject("http://userservice/delUser/" + id, Result.class);
        return result;
    }


    private boolean checkAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        System.out.println("current user" + user);
        if (user == null || !user.getUsername().equals("admin")) {
            return false;
        }
        return true;
    }

    @RequestMapping("/doRegister")
    public Map<String, Object> register(HttpServletRequest request) throws NoSuchAlgorithmException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String vcode = request.getParameter("vcode");
        Map<String, Object> result = new HashMap<>();
        if (!verifyService.checkVcode(request, vcode)) {
            result.put("success", false);
            result.put("msg", "验证码错误");
            return result;
        }
        User user = restTemplate.getForObject("http://userservice/getUserByName/"+username, User.class);
        if(user.getUsername()!=null){
            System.out.println("user----"+user);
            result.put("success", false);
            result.put("msg", "用户名已存在！");
            return result;
        }
        Result result1 = restTemplate.getForObject("http://userservice/insertUser?username=" + username + "&password=" + password, Result.class);
        result.put("success",result1.get("success"));
        System.out.println("user----"+user);
        return result;
    }

}