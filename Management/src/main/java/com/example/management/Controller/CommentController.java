package com.example.management.Controller;

import com.example.management.Service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/getCommentList")
    public Map<String,Object> getCommentList(){
        return commentService.getCommentList();
    }

    @PostMapping("/deleteComment")
    public Map<String,Object> deleteComment(@RequestBody Map<String,Object>map){
        return commentService.deleteComment(map);
    }

}
