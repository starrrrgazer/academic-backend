package com.example.portal.entity;

import javax.persistence.*;

@Entity
@Table(name = "QuestionTag")
public class QuestionTag {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "questionID")
    private int questionID;

    @Column(name = "tagsID")
    private int tagsID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public int getTagsID() {
        return tagsID;
    }

    public void setTagsID(int tagsID) {
        this.tagsID = tagsID;
    }
}
