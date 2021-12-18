package com.example.portal.controller;


import com.example.portal.dao.UserRepository;
import com.example.portal.entity.User;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:8000",allowCredentials = "true",maxAge =
        3600)
@RestController
public class PersonalCenterController {

    @Autowired
    UserRepository userRepository;

    @PostMapping("/showinfo")
    public Map<String, Object> getUseInfo(@RequestBody Map<String, Object> arg) throws IOException {
        Map<String, Object> ret = new HashMap<>();
        String uid = (String) arg.get("uid");
        User target = userRepository.findByUserID(uid);
        if(target == null) {
            ret.put("success", "false");
            ret.put("msg","发生未知错误，找不到该用户");
            return ret;
        }
        else {
            ret.put("username", target.getUsername());
            ret.put("phoneNum", target.getPhoneNumber());
            ret.put("email", target.getEmailAddress());

            File avatar = new File(target.getImage());
            if(avatar.exists() && avatar.canRead()) {
                byte[] buffer = new byte[(int) avatar.length()];
                InputStream in = new FileInputStream(avatar);
                in.read(buffer);
                ret.put("avatar", buffer);
            }
            else {
                File dft = new File("./static/image/default.jpg");
                if(dft.exists()) {
                    byte[] buffer = new byte[(int) dft.length()];
                    InputStream in = new FileInputStream(dft);
                    in.read(buffer);
                    ret.put("avatar", buffer);
                }
                else
                    ret.put("avatar", null);
            }
            ret.put("success", "true");
            ret.put("msg", "找到该用户");
            return ret;
        }
    }

    @PostMapping("/editinfo")
    public Map<String, Object> updateInfo(@RequestBody Map<String, Object> arg) {
        Map<String, Object> ret = new HashMap<>();
        String username = (String) arg.get("username");
        String phoneNumber = (String) arg.get("phoneNum");
        String tc = (String) arg.get("testcode");

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        String vc = (String) session.getAttribute("verification_code");
        String origin_username = (String) session.getAttribute("username");

        User user = userRepository.findByUsername(origin_username);
        if(user != null) {
            if(vc.equals(tc)) {
                if(username.length() == 0 || userRepository.findByUsername(username) == null || username.equals(user.getUsername())) {
                    if(phoneNumber.length() == 0 || userRepository.findByPhoneNumber(phoneNumber) == null || phoneNumber.equals(user.getPhoneNumber())) {
                        if(username.length() > 0 || phoneNumber.length() > 0) {
                            if(phoneNumber.length() > 0) {
                                userRepository.updatePhoneNumber(origin_username, phoneNumber);
                                ret.put("msg", " 手机号修改成功");
                            }
                            if(username.length() > 0) {
                                userRepository.updateUsername(origin_username, username);
                                session.setAttribute("username", username);
                                if(ret.get("msg") != null)
                                    ret.put("msg", ret.get("msg") + " 用户名修改成功");
                                else
                                    ret.put("msg", "用户名修改成功");
                            }
                            ret.put("status", "success");
                        }
                        else {
                            ret.put("status", "warning");
                            ret.put("msg", "未修改任何信息");
                        }
                    }
                    else {
                        ret.put("status", "error");
                        ret.put("msg", "手机号已经被注册。");
                    }
                }
                else {
                    ret.put("status", "error");
                    ret.put("msg", "用户名重复，请尝试更换。");
                }
            }
            else {
                ret.put("status", "error");
                ret.put("msg", "验证码错误！");
            }
        }
        else {
            ret.put("status", "error");
            ret.put("msg", "发生未知错误：找不到该用户");
        }
        return ret;
    }

    @Transactional
    @PostMapping("/postavatar")
    public Map<String, Object> uploadPics(@RequestBody Map<String, Object> arg) throws IOException {
        String image_base64 = ((String) arg.get("avatar")).trim();
        int index = image_base64.indexOf("base64,") + 7;
        String newImage = image_base64.substring(index);
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] imageBuf = decoder.decode(newImage);
        for(byte b : imageBuf)
            if(b < 0)
                b += 256;

        Map<String, Object> ret = new HashMap<>();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        String username = (String) session.getAttribute("username");

        User user = userRepository.findByUsername(username);
        if(user != null) {
            File avatar = new File(user.getImage());
            File avatarPath = new File(StringUtils.substringBeforeLast(user.getImage(), "/"));
            if(!avatarPath.exists())
                avatarPath.mkdirs();
            if(!avatar.exists())
                avatar.createNewFile();
            FileOutputStream  out = new FileOutputStream(avatar);
            out.write(imageBuf);
            out.flush();
            out.close();
            ret.put("status", "success");
        }
        else {
            ret.put("status", "error");
            ret.put("msg", "未知错误：找不到该用户");
        }
        return ret;
    }

    @PostMapping("/findpwd")
    public Map<String, Object> findPwd(@RequestBody Map<String, Object> arg) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        String tc = (String) session.getAttribute("verification_code");


        Map<String, Object> ret = new HashMap<>();

        String email = (String) arg.get("email");
        String code = (String) arg.get("testcode");
        String pwd = (String) arg.get("pwd");
        String pwd2 = (String) arg.get("confirmPwd");

        User user = userRepository.findByEmailAddress(email);
        if(user != null) {
            String username = user.getUsername();
            if(user.getEmailAddress().equals(email)) {
                if(code.equals(tc)) {
                    if(pwd.equals(pwd2)) {
                        userRepository.updatePassword(username, pwd);
                        ret.put("status", "success");
                    }
                    else {
                        ret.put("status", "error");
                        ret.put("msg", "两次密码输入不一致");
                    }
                }
                else {
                    ret.put("status", "error");
                    ret.put("msg", "验证码填写错误");
                }
            }
            else {
                ret.put("status", "error");
                ret.put("msg", "请填写与账户绑定的邮箱");
            }
        }
        else {
            ret.put("status", "error");
            ret.put("msg", "发生未知错误：找不到该用户");
        }
        return ret;
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(@RequestBody Map<String, Object> arg) {
        Map<String, Object> ret = new HashMap<>();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);

        if(session.getAttribute("username") == null) {
            ret.put("success", "false");
            ret.put("msg", "found no user login");
        }
        else {
            session.removeAttribute("userID");
            session.removeAttribute("username");
            session.removeAttribute("isBanned");
            session.removeAttribute("password");
            ret.put("success", "true");
        }
        return ret;
    }
}
