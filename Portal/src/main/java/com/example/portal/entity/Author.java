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
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "author")
public class Author {
    @Id
    private String id;

    private int h_index;
    private String orgs;
    private int n_citation;
    private int n_pubs;
    private String name;
    private String position;
    private Pubs pubs;
    private Tags tags;

    private void parseCompound(Map<String, Object> map) {
        Map<String, Object> map_pubs = (Map<String, Object>) map.get("pubs");
        Map<String, Object> map_tags = (Map<String, Object>) map.get("tags");
        pubs = new Pubs(map_pubs);
        tags = new Tags(map_tags);
    }

    public Author(String id, int h_index, String orgs, int n_citation, int n_pubs, String name, String position, Map<String, Object> pubs, Map<String, Object> tags) {
        this.id = id;
        this.h_index = h_index;
        this.orgs = orgs;
        this.n_citation = n_citation;
        this.n_pubs = n_pubs;
        this.name = name;
        this.position = position;
        this.pubs = new Pubs(pubs);
        this.tags = new Tags(tags);
    }

    public Author(Map<String, Object> map) {
        parseCompound(map);
        id = (String) map.get("id");
        h_index = Integer.parseInt((String) map.get("h_index"));
        orgs = (String) map.get("orgs");
        n_citation = Integer.parseInt((String) map.get("n_citation"));
        n_pubs = Integer.parseInt((String) map.get("n_pubs"));
        name = (String) map.get("name");
        position = (String) map.get("position");
    }

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
    public Pubs(Map<String,Object> arg) {
        i = (String) arg.get("i");
        r = Integer.parseInt((String) arg.get("r"));
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Tags{
    String t;
    int w;
    public Tags(Map<String, Object> arg) {
        t = (String) arg.get("t");
        w = Integer.parseInt((String) arg.get("w"));
    }
}