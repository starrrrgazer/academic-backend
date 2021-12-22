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

@CrossOrigin(origins = {"http://localhost:8000","http://localhost:80","http://localhost:443",
        "https://localhost:8000","https://localhost:80","https://localhost:443",
        "http://121.36.60.6:8000","http://121.36.60.6:80","http://121.36.60.6:443",
        "https://121.36.60.6:8000","https://121.36.60.6:80","https://121.36.60.6:443"},allowCredentials = "true",maxAge = 3600)
@RestController
public class RegisterController {
    @Autowired
    UserRepository userRepository;

    @Autowired
    MailServiceImpl mailService;

    @PostMapping("/getCode")
    public Map<String, Object> sendCode(@RequestParam(value = "move", defaultValue = "regist") String method,
                                        @RequestBody Map<String, Object> arg) {
        Map<String, Object> ret = new HashMap<>();

        String emailAddress = null;

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);

        assert session != null;

        if("regist".equals(method)) {
            emailAddress = (String) arg.get("emailAddress");
            String username = (String) arg.get("username");
            if(userRepository.findByUsername(username) != null) {
                ret.put("success", "false");
                ret.put("msg", "201");// 用户名已被注册
                return ret;
            }
            else if(userRepository.findByEmailAddress(emailAddress) != null) {
                ret.put("success", "false");
                ret.put("msg", "202");//邮箱已被注册
                return ret;
            }
        }
        else if("edit".equals(method)) {
//            String origin_username = (String) session.getAttribute("username");
//            String username = (String) arg.get("username");
//            String phoneNumber = (String) arg.get("phoneNum");
//            User user = userRepository.findByUsername(origin_username);
//            if(user != null) {
//                if(user == null || username.length() == 0 || userRepository.findByUsername(username) == null || username.equals(user.getUsername())) {
//                    if(phoneNumber == null || phoneNumber.length() == 0 || userRepository.findByPhoneNumber(phoneNumber) == null || phoneNumber.equals(user.getPhoneNumber())) {
//                        if(username != null && username.length() > 0 || phoneNumber != null && phoneNumber.length() > 0) {
//                            if(username != null && username.equals(user.getUsername())) {
//                                ret.put("success", "warning");
//                                ret.put("msg", "确定将用户名修改为原来的用户名？");
//                            }
//                            if(phoneNumber != null && phoneNumber.equals(user.getPhoneNumber())) {
//                                ret.put("success", "warning");
//                                if(ret.get("msg") != null)
//                                    ret.put("msg", ret.get("msg") + " 确定将手机号修改为原来的手机号？");
//                                else
//                                    ret.put("msg", " 确定将手机号修改为原来的手机号？");
//                            }
//                            emailAddress = user.getEmailAddress();
//                        }
//                        else {
//                            ret.put("success", "warning");
//                            ret.put("msg", "用户未修改任何信息");
//                        }
//                    }
//                    else {
//                        ret.put("success", "false");
//                        ret.put("msg", "手机号已经被注册过");
//                        return ret;
//                    }
//                }
//                else {
//                    ret.put("success", "false");
//                    ret.put("msg", "用户名重复");
//                    return ret;
//                }
//            }
//            else {
//                ret.put("success", "false");
//                ret.put("msg", "发生未知错误，找不到当前用户");
//                return ret;
//            }
            // 登录状态下才能修改个人信息
            String origin_username = (String) session.getAttribute("username");
            if(origin_username == null) {
                ret.put("success", "false");
                ret.put("msg1", "发生未知错误，找不到当前用户");
                return ret;
            }

            String username = (String) arg.get("username");
            String phoneNumber = (String) arg.get("phoneNum");
            User user = userRepository.findByUsername(origin_username);
            if(user != null) {
                if(user == null || username.length() == 0 || userRepository.findByUsername(username) == null || username.equals(user.getUsername())) {
                    if(phoneNumber == null || phoneNumber.length() == 0 || userRepository.findByPhoneNumber(phoneNumber) == null || phoneNumber.equals(user.getPhoneNumber())) {
                        if(username != null && username.length() > 0 || phoneNumber != null && phoneNumber.length() > 0) {
                            if(username != null && username.equals(user.getUsername())) {
                                ret.put("success", "warning");
                                ret.put("msg1", "405");//未修改用户名
                            }
                            if(phoneNumber != null && phoneNumber.equals(user.getPhoneNumber())) {
                                ret.put("success", "warning");
                                if(ret.get("msg") != null)
                                    ret.put("msg", ret.get("msg") + " 确定将手机号修改为原来的手机号？");
                                else
                                    ret.put("msg1", "401");//未修改手机号
                            }
                            emailAddress = user.getEmailAddress();
                        }
                        else {
                            ret.put("success", "warning");
                            ret.put("msg1", "402");//未修改任何信息
                        }
                    }
                    else {
                        ret.put("success", "false");
                        ret.put("msg1", "403");//手机号被注册过
                        return ret;
                    }
                }
                else {
                    ret.put("success", "false");
                    ret.put("msg1", "404");//用户名重复
                    return ret;
                }
            }
            else {
                ret.put("success", "false");
                ret.put("msg1", "发生未知错误，找不到当前用户");
                return ret;
            }
        }
        else if("reset".equals(method)) {
            emailAddress = (String) arg.get("email");
            String pwd = (String) arg.get("pwd");
            String pwd2 = (String) arg.get("confirmPwd");
            User user = userRepository.findByEmailAddress(emailAddress);
            if(user != null) {
                if(pwd.equals(pwd2)) {
                    if(pwd.equals(user.getPassword())) {
                        ret.put("success", "warning");
                        ret.put("msg", "您尝试修改的密码与原密码相同");
                        return ret;
                    }
                }
                else {
                    ret.put("success", "false");
                    ret.put("msg", "两次密码输入不一致");
                    return ret;
                }
            }
            else {
                ret.put("success", "false");
                ret.put("msg", "发生未知错误");
                return ret;
            }
        }
        else {
            ret.put("success", "false");
            ret.put("msg", "发生未知错误，在未知过程中尝试发送验证码");
            return ret;
        }

        // Sending Email - all situations are indifferent
        String vc = generateVC();
        //System.out.println(vc);

        session.setAttribute("verification_code", vc);

        String subject = null;
        if(method.equals("regist"))
            subject = "VERIFICATION: You Are Signing Up, Welcome!";
        else if(method.equals("edit"))
            subject = "VERIFICATION: You Are Editing Your Personal Info...";
        else if(method.equals("reset"))
            subject = "VERIFICATION: You Are Resetting Your Password...";

        mailService.sendMail(emailAddress, subject, vc);
        if(ret.get("success") == null)
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
                    newUser.setImage("./static/image/default.jpg");

                    userRepository.save(newUser);

                    ret.put("success", "true");
                    ret.put("msg", "201"); // 注册成功
                }
                else {
                    ret.put("success", "false");
                    ret.put("msg", "202"); // 验证码错误
                }
            }
            else {
                ret.put("success", "false");
                ret.put("msg", "203"); // 邮箱重复
            }
        }
        else {
            ret.put("success", "false");
            ret.put("msg", "204"); // 用户名重复
        }
        return ret;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, Object> loginMap) {

        System.out.println("someone try to login");
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        System.out.println(session.getId());
        Map<String, Object> map = new HashMap<>();
        if(session.getAttribute("userID") != null){
            map.put("message", "用户已登录！");
        }
        else{
            String username = (String) loginMap.get("username");
            String password = (String) loginMap.get("password");
            try {
                User user = userRepository.findByUsername(username);
                if (user != null) {
                    if(user.getPassword().equals(password)){
                        session.setAttribute("userID", user.getUserID());
                        session.setAttribute("username", username);
                        session.setAttribute("password", password);
                        session.setAttribute("isBanned", user.getIsBanned());
                        map.put("uid", user.getUserID());
                        map.put("success", true);
                        map.put("message", "用户登录成功！");
                    }
                    else {
                        map.put("success", false);
                        map.put("message", "403"); // 密码错误
                    }
                }
                else {
                    map.put("success", false);
                    map.put("message", "404"); // 不存在的用户名，是否注册？
                }
            } catch (Exception e) {
                e.printStackTrace();
                map.put("success", false);
                map.put("message", "405"); // 未知错误
            }
        }
        return map;
    }

    private String generateVC() {
        char[] dic = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
        char[] vc = new char[6];
        SecureRandom sr = new SecureRandom();
        for(int i = 0; i < 6; i++) {
            int index = sr.nextInt(dic.length);
            vc[i] = dic[index];
        }
        return new String(vc);
    }

}
