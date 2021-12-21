package com.example.portal.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.util.ArrayList;
import java.util.Map;

@Data
@Document(indexName = "paper")
public class Paper {
    @Id
    @Field(name = "id", type = FieldType.Keyword)
    private String id;

    @Field(name = "title", type = FieldType.Text)
    private String title;

    @Field(name = "year", type = FieldType.Long)
    private int year;

    @Field(name = "n_citation", type = FieldType.Long)
    private int n_citation;

    @Field(name = "url", type = FieldType.Text)
    private String url;

    @Field(name = "doi", type = FieldType.Keyword)
    private String doi;

    @Field(name = "abstract", type = FieldType.Text)
    private String abstract_;

    @Field(name = "keywords", type = FieldType.Text)
    private ArrayList<String> keywords;

    @Field(name = "authors", type = FieldType.Nested)
    private ArrayList<Map<?,?>> authors;

    @Field(name = "venue", type = FieldType.Nested)
    private Map<?,?> venue;

    @Field(name = "pdf", type = FieldType.Text)
    private String pdf;
}
