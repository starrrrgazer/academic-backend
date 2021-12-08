package com.example.portal.entity;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Document(indexName = "paper")
public class Paper {
    @Id
    private String id;

    private String title;
    private List<AuthorList> authors;
    private List<String> url;
    private String issue;
    private String doi;
    private int n_citation;
    private List<String> keywords;
    private String page_start;
    private String page_end;
    private Venue venue;
    private String volume;
    private int year;
    private String isbn;
    private String issn;
    private String pdf;
    private String abstracts;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<AuthorList> getAuthors() {
        return authors;
    }

    public List<String> getUrl() {
        return url;
    }

    public String getIssue() {
        return issue;
    }

    public String getDoi() {
        return doi;
    }

    public int getN_citation() {
        return n_citation;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public String getPage_start() {
        return page_start;
    }

    public String getPage_end() {
        return page_end;
    }

    public Venue getVenues() {
        return venue;
    }

    public String getVolume() {
        return volume;
    }

    public int getYear() {
        return year;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getIssn() {
        return issn;
    }

    public String getPdf() {
        return pdf;
    }

    public String getAbstracts() {
        return abstracts;
    }

    private void parseVenue(Map<String, Object> map) {
        Map<String, Object> mapve = (Map<String, Object>) map.get("venue");
        if (mapve != null) {
            venue = new Venue();
            venue.setRaw((String) mapve.get("raw"));
            venue.setId(((String) mapve.get("id")));
        }
    }

    private void parseAuthors(Map<String, Object> map) {
        this.authors = new ArrayList<>();
        ArrayList<Map<String, Object>> ls = (ArrayList<Map<String, Object>>) map.get("authors");
        if (ls != null) {
            for (Map<String, Object> l : ls) {
                if (l.get("id") != null) {
                    authors.add(new AuthorList(l));
                }
            }
        }
    }

    public Paper(Map<String, Object> map) {
        Object obj;
        parseVenue(map);
        parseAuthors(map);
        title = (String) map.get("title");
        id = (String) map.get("id");
        doi = (String) map.get("doi");
        url = (List<String>) map.get("url");
        issue = (String) map.get("issue");
        obj = map.get("n_citation");
        n_citation = obj == null ? 0 : (Integer) obj;
        keywords = (List<String>) map.get("keywords");
        page_start = (String) map.get("page_start");
        page_end = (String) map.get("page_end");
        volume = (String) map.get("volume");
        obj = map.get("year");
        year = obj == null ? 0 : (Integer) obj;
        issn = (String) map.get("issn");
        isbn = (String) map.get("isbn");
        pdf = (String) map.get("pdf");
        abstracts = (String) map.get("abstract");
    }
}
/**
 * venue:class java.util.HashMap
 * {
 * raw:venue name
 * id :venue id
 * }
 * authors:class java.util.ArrayList *
 * {
 * org,
 * name,
 * id
 * }
 * doi:class java.lang.String *
 * id:class java.lang.String *
 * title:class java.lang.String *
 * isbn:null
 * issn:class java.lang.String
 * issue:class java.lang.String
 * keywords:class java.util.ArrayList
 * {
 * keywords
 * }
 * lang:class java.lang.String
 * n_citation:class java.lang.Integer
 * page_end:class java.lang.String
 * page_start:class java.lang.String
 * pdf:class java.lang.String
 * url:class java.util.ArrayList
 * volume:class java.lang.String
 * year:class java.lang.Integer
 */