package me.crycry.user.controller;


import me.crycry.user.entity.Result;
import me.crycry.user.entity.User;
import me.crycry.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/getUser/{id}")
    public Result getUser(@PathVariable Long id){
        User user = userRepository.findOne(id);
        Result result = Result.ok();
        result.put("data",user);
        return result;
    }


    @RequestMapping("/getUserByName/{username}")
    public Result getUserByName(@PathVariable String username){
        User user = userRepository.findFirstByUsername(username);
        Result result = Result.ok();
        result.put("data",user);
        return result;
    }

    @RequestMapping("/permitUser/{id}")
    public Result permitUser(@PathVariable Long id){
        User user = userRepository.findOne(id);
        user.setStatus("1");
        user = userRepository.save(user);
        Result result = Result.ok();
        result.put("data",user);
        return  result;
    }

    @RequestMapping("/checkUser")
    public User checkUser(HttpServletRequest request) throws NoSuchAlgorithmException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        String md5Password = new BigInteger(1, md.digest()).toString(16);
        System.out.println(username+"    "+password);
        User user = userRepository.findFirstByUsernameAndPassword(username,md5Password);
        System.out.println(username);
        return user;
    }

    @RequestMapping("/insertUser")
    public Result User(HttpServletRequest request) throws NoSuchAlgorithmException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println(username+"    "+password);
        User newUser = new User();
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        String md5Password = new BigInteger(1, md.digest()).toString(16);
        newUser.setPassword(md5Password);
        newUser.setUsername(username);
        userRepository.save(newUser);
        System.out.println(username);
        return Result.ok();
    }


    @RequestMapping("/update")
    public Result update(HttpServletRequest request){
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        Long id =Long.parseLong( request.getParameter("id"));
        User user = userRepository.findOne(id);
        if(username!=null) user.setUsername(username);
        if(password!=null) user.setPassword(password);
        if(email!=null) user.setEmail(email);
        userRepository.save(user);
        return Result.ok();
    }

    @RequestMapping("/delUser/{id}")
    public Result delUser(@PathVariable Long id){
        userRepository.delete(id);
        return Result.ok();
    }


    @RequestMapping("/listUnPermitUser")
    public Result listUnPermitUser(){
        Result result = Result.ok();
        result.put("data", userRepository.listUnPermitUser());
        return result;
    }




}
