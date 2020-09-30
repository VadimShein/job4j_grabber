package ru.job4j.html;

public class Post {
    private String name;
    private String text;
    private String link;
    private String created;

    public Post(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public String getLink() {
        return link;
    }

    public String getCreated() {
        return created;
    }
}
