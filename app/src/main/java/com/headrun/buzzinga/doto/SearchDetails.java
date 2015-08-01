package com.headrun.buzzinga.doto;

/**
 * Created by headrun on 10/7/15.
 */
public class SearchDetails {

    public String title;
    public String url;
    public String text;
    public String articledate;
    public String author;

    public SearchDetails() {
    }

    public SearchDetails(String title, String url, String text, String articledate,String author) {
        this.title = title;
        this.url = url;
        this.text = text;
        this.articledate = articledate;
        this.author=author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getArticledate() {
        return articledate;
    }

    public void setArticledate(String articledate) {
        this.articledate = articledate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String articledate) {
        this.author = author;
    }
}
