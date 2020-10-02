package ru.job4j.html;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface Parse {
    List<Post> list(String link) throws IOException, ParseException;
    Post detail(String postLink) throws IOException, ParseException;
}