package com.example.literature.entity;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;

@Document(indexName = "author")
public class Author{
    @Id
    private String id;

    private String name;
    private String org;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getOrg() {
        return org;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrg(String org) {
        this.org = org;
    }
}