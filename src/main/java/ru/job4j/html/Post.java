package ru.job4j.html;

import java.util.Objects;

public class Post {
    private String postName;
    private String postText;
    private String postLink;
    private String postDate;

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public String getPostText() {
        return postText;
    }

    public void setPostText(String postTtext) {
        this.postText = postTtext;
    }

    public String getPostLink() {
        return postLink;
    }

    public void setPostLink(String link) {
        this.postLink = link;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return Objects.equals(postName, post.postName)
                && Objects.equals(postText, post.postText)
                && Objects.equals(postLink, post.postLink)
                && Objects.equals(postDate, post.postDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postName);
    }
}
