package com.example.management.Service;

import com.example.management.Entity.Comment;
import com.example.management.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {


    @Autowired
    private CommentMapper commentMapper;


    public Map<String,Object> getCommentList(Map<String,Object> map){
        Map<String,Object> returnObject = new HashMap<>();
        List<Map<String,Object>> comments= new ArrayList<>();
        String id = (String) map.get("id");
        try {
            Comment tmpComment = commentMapper.getCommentById(id);
            Map<String,Object> tmp = new HashMap<>();
            tmp.put("commentID",tmpComment.getCommentID());
            tmp.put("content",tmpComment.getContent());
            tmp.put("toID",tmpComment.getToID());
            tmp.put("userID",tmpComment.getUserID());
            tmp.put("toType",tmpComment.getToType());
            tmp.put("commentTime",tmpComment.getCommentTime());
            comments.add(tmp);
        }catch (Exception e) {
            returnObject.put("status","401");
            returnObject.put("result","未知错误");
            return returnObject;
        }
        returnObject.put("status","200");
        returnObject.put("result","处理成功");
        returnObject.put("commentList",comments);
        return returnObject;
    }

    public Map<String,Object> deleteComment(Map<String,Object> map){

        Map<String,Object> returnObject = new HashMap<>();
        try{
            String commentId = (String) map.get("commentId");
            Comment c = commentMapper.getCommentById(commentId);
            if (c == null) {
                returnObject.put("status", "402");
                returnObject.put("result", "评论不存在");
                return returnObject;
            }
            commentMapper.deleteComment(commentId);
        }catch (Exception e){
            returnObject.put("status", "401");
            returnObject.put("result", "未知错误");
            return returnObject;
        }
        returnObject.put("status","200");
        returnObject.put("result","删除成功");
        return returnObject;
    }
}
