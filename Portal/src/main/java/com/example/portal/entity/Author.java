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
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "author")
public class Author {
    @Id
    private String id;

    @Field(name = "h_index", type = FieldType.Long)
    private int h_index;

    @Field(name = "org", type = FieldType.Keyword)
    private String org;

    @Field(name = "n_citation", type = FieldType.Long)
    private int n_citation;

    @Field(name = "n_pubs", type = FieldType.Long)
    private int n_pubs;

    @Field(name = "name", type = FieldType.Keyword)
    private String name;

    @Field(name = "position", type = FieldType.Keyword)
    private String position;

    @Field(name = "pubs", type = FieldType.Nested)
    private Map<?,?> pubs;

    @Field(name = "tags", type = FieldType.Nested)
    private Map<?,?> tags;

    public String getPubs_i() {return (String) pubs.get("i");}
    public int getPubs_r() {return (Integer) pubs.get("r");}
    public String getTags_t() {return (String) tags.get("t");}
    public int getTags_w() {return (Integer) tags.get("w");}
}