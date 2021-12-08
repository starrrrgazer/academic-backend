package com.example.portal.controller;

import com.example.portal.dao.UserRepository;
import com.example.portal.entity.User;
import com.example.portal.service.MailServiceImpl;

import com.sun.imageio.plugins.common.ImageUtil;
import jdk.tools.jlink.plugin.Plugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:8000",allowCredentials = "true",maxAge =
        3600)
@RestController
public class RegisterController {
    @Autowired
    UserRepository userRepository;

    @PostMapping("/getCode")
    public Map<String, String> sendCode(HttpServletRequest request) {
        String emailAddress = request.getParameter("emailAddress");
        String username = request.getParameter("username");
        Map<String, String> ret = new HashMap<>();
        if(userRepository.findByUsername(username) != null) {
            ret.put("success", "false");
            ret.put("msg", "用户名已注册");
            return ret;
        }
        String vc = generateVC();
        request.getSession().setAttribute("verification_code", vc);
        MailServiceImpl mailService = new MailServiceImpl();
        mailService.sendMail(emailAddress, "Verification", vc);
        ret.put("success","true");
        ret.put("msg","邮件发送成功");
        return ret;
    }

    @GetMapping("/submitForm")
    public Map<String, String> registerNewUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String emailAddress = request.getParameter("emailAddress");
        String vc = session.getAttribute("verification_code").toString();

        Map<String, String> ret = new HashMap<>();

        if(userRepository.findByUsername(username) == null) {
            if(vc.equals(request.getParameter("verification_code"))) {
                UUID uuid = UUID.randomUUID();
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(password);
                newUser.setEmailAddress(emailAddress);
                newUser.setUserID(uuid);
                newUser.setPhoneNumber(request.getParameter("phoneNumber"));
                newUser.setRealName(request.getParameter("realName"));
                newUser.setAuthorID(request.getParameter("authorID"));
                newUser.setIntroduction(request.getParameter("introduction"));
                newUser.setOrganization(request.getParameter("organization"));
                newUser.setIsBanned(0);
                newUser.setUnblockTime(null);
                newUser.setUserPosition(request.getParameter("userPosition"));
                newUser.setUserIdentity(1);
                newUser.setImage("/static/image/" + username + ".jpg");

                userRepository.save(newUser);
            }
            else {
                ret.put("success", "true");
                ret.put("msg", "注册成功");
            }
        }
        else {
            ret.put("success", "false");
            ret.put("msg", "用户名重复");
        }
        return ret;
    }

    @Transactional
    @PutMapping("/uploading")
    public Map<String, String> update( MultipartFile image, String username, HttpServletRequest request) throws IOException{
        Map<String, String> ret = new HashMap<>();
        if( image!=null ) {
            saveOrUpdateImageFile(image, username, request);
            ret.put("success", "true");
            ret.put("msg", "图像上传成功");
        }
        else {
            ret.put("success", "false");
            ret.put("msg", "图片上传失败");
        }
        return ret;
    }

    public void saveOrUpdateImageFile(MultipartFile image, String username, HttpServletRequest request)
            throws IOException {
        File imageFolder= new File(request.getServletContext().getRealPath("static/image"));
        File file = new File(imageFolder,username +".jpg");
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        image.transferTo(file);
        BufferedImage img = ImageIO.read(new FileInputStream(file));
        ImageIO.write(img, "jpg", file);
    }

    private String generateVC() {
        char[] dic = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
        char[] vc = new char[6];
        SecureRandom sr = new SecureRandom();
        for(int i = 0; i < 6; i++) {
            int index = sr.nextInt(dic.length);
            vc[i] = dic[index];
        }
        return new String(vc);
    }

}
