package com.example.portal.controller;

import com.example.portal.dao.UserRepository;
import com.example.portal.entity.User;
import com.example.portal.service.MailServiceImpl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
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
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:8000",allowCredentials = "true",maxAge =
        3600)
@RestController
public class RegisterController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    MailServiceImpl mailService;

    @PostMapping("/getCode")
    public Map<String, Object> sendCode(@RequestBody Map<String, String> arg) {
        String emailAddress = arg.get("emailAddress");
        String username = arg.get("username");
        Map<String, Object> ret = new HashMap<>();
        System.out.println("email: " + emailAddress);
        System.out.println("username: " + username);
        if(userRepository.findByUsername(username) != null) {
            ret.put("success", "false");
            ret.put("msg", "用户名已被注册");
            return ret;
        }
        else if(userRepository.findByEmailAddress(emailAddress) != null) {
            ret.put("success", "false");
            ret.put("msg", "邮箱已被注册");
            return ret;
        }
        String vc = generateVC();
        System.out.println(vc);

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        session.setAttribute("verification_code", vc);

        mailService.sendMail(emailAddress, "Verification", vc);
        ret.put("success","true");
        ret.put("msg","邮件发送成功");
        return ret;
    }

    @PostMapping("/regist")
    public Map<String, Object> registerNewUser(@RequestBody Map<String, String> arg) {
        String username = arg.get("username");
        String password = arg.get("password");
        String emailAddress = arg.get("emailAddress");

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        String vc = (String) session.getAttribute("verification_code");

        System.out.println(vc);

        Map<String, Object> ret = new HashMap<>();

        if(userRepository.findByUsername(username) == null) {
            if(userRepository.findByEmailAddress(emailAddress) == null) {
                if(vc.equals(arg.get("verification_code"))) {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setPassword(password);
                    newUser.setEmailAddress(emailAddress);
                    newUser.setUserID(UUID.randomUUID().toString().replaceAll("-",""));
                    if(arg.get("phoneNumber") != null)
                        newUser.setPhoneNumber(arg.get("phoneNumber"));
                    if(arg.get("realName") != null)
                        newUser.setRealName(arg.get("realName"));
                    if(arg.get("authorID") != null)
                        newUser.setAuthorID(arg.get("authorID"));
                    if(arg.get("introduction") != null)
                        newUser.setIntroduction(arg.get("introduction"));
                    if(arg.get("organization") != null)
                        newUser.setOrganization(arg.get("organization"));
                    newUser.setIsBanned(0);
                    newUser.setUnblockTime(null);
                    if(arg.get("userPosition") != null)
                        newUser.setUserPosition(arg.get("userPosition"));
                    newUser.setUserIdentity(1);
                    newUser.setImage("./static/image/" + newUser.getUserID() + ".jpg");

                    userRepository.save(newUser);

                    ret.put("success", "true");
                    ret.put("msg", "注册成功，即将转到登陆页面");
                }
                else {
                    ret.put("success", "false");
                    ret.put("msg", "邮箱重复");
                }
            }
            else {
                ret.put("success", "false");
                ret.put("msg", "验证码错误");
            }
        }
        else {
            ret.put("success", "false");
            ret.put("msg", "用户名重复");
        }
        return ret;
    }
/*
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
*/
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
