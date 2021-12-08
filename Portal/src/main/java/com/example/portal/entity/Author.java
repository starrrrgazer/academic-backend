package com.example.portal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Document(indexName = "author")
public class Author implements Serializable {
    @Id
    @Field(type = FieldType.Keyword)
    private String id;

    @Field(type = FieldType.Long)
    private long h_index;

    @Field(type = FieldType.Keyword)
    private String orgs;

    @Field(type = FieldType.Long)
    private int n_citation;

    @Field(type = FieldType.Long)
    private long n_pubs;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Text)
    private String position;

    // TODO: database
    @Field
    private Pubs pubs;

    @Field
    private Tags tags;

    public String getPubs_i() {return pubs.i;}
    public int getPubs_r() {return pubs.r;}
    public String getTags_t() {return tags.t;}
    public int getTags_w() {return tags.w;}
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Pubs {
    String i;
    int r;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Tags{
    String t;
    int w;
}