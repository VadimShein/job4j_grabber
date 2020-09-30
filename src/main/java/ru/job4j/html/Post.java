package ru.job4j.html;

public class Post {
    private String topic;
    private String author;
    private String answers;
    private String views;
    private String date;
    private String link;

    public Post(String link) {
        this.link = link;
    }

    public String getTopic() {
        return topic;
    }

    public String getAuthor() {
        return author;
    }

    public String getAnswers() {
        return answers;
    }

    public String getViews() {
        return views;
    }

    public String getDate() {
        return date;
    }
}
