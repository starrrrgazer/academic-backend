//package com.example.community.bean;
//
//
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//public class LoginInterceptor implements HandlerInterceptor {
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
//            throws Exception {
//
//        response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, accept, content-type, xxxx");
//        response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, PATCH");
//        response.setHeader("Access-Control-Allow-Origin", "http://127.0.0.1:8080");
//        response.setHeader("Access-Control-Allow-Credentials", "true");
//
//
//        return true;
//    }
//}