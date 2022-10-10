package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SqlRuParse implements Parse {
    public static String convertDate(String postDate) throws ParseException {
        String modDate;
        DateFormatSymbols myDateFormatSymbols = new DateFormatSymbols(){
            @Override
            public String[] getMonths() {
                return new String[] {"янв", "фев", "мар", "апр", "май", "июн",
                        "июл", "авг", "сен", "окт", "ноя", "дек"};
            }
        };
        if (postDate.contains("сегодня")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            final Calendar cal = Calendar.getInstance();
            modDate = sdf.format(cal.getTime());
        } else if (postDate.contains("вчера")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            modDate = sdf.format(cal.getTime());
        } else {
            Date date = new SimpleDateFormat("d MMM yy, H:m", myDateFormatSymbols).parse(postDate);
            Locale locales = Locale.getDefault();
            String patterns = "yyyy-MM-dd hh:mm:ss";
            modDate = new SimpleDateFormat(patterns, locales).format(date);
        }
        return modDate;
    }

    @Override
    public List<Post> list(String link) throws IOException, ParseException {
        List<Post> posts = new ArrayList<>();

        Document docPages = Jsoup.connect(link).get();
        Elements pg = docPages.select(".sort_options").select("a");
        int pages = 0;
        for (Element a : pg) {
            pages = Integer.parseInt(a.text());
        }

        int index = 0;
        while (index < 10) {
            String PageUrl = link + "/" + (index + 1);
            Document doc = Jsoup.connect(PageUrl).get();
            Elements row = doc.select(".postslisttopic");
            System.out.println(System.lineSeparator() + "Page: " + (index + 1) + System.lineSeparator());
            for (Element td : row) {
                Element href = td.child(0);
                String postLink = href.attr("href");
                posts.add(detail(postLink));
            }
            index++;
        }
        return posts;
    }

    @Override
    public Post detail(String postLink) throws IOException, ParseException {
        Document doc = Jsoup.connect(postLink).get();

        String postName = doc.select(".messageHeader").get(0).text();
        String postText = doc.select("td[class='msgBody']").get(1).text();
        String postDate = SqlRuParse.convertDate(doc.select(".msgFooter").text());

        System.out.println(postLink);
        System.out.println(postName);
        System.out.println(postText);
        System.out.println(postDate);

        Post post = new Post();
        post.setPostLink(postLink);
        post.setPostName(postName);
        post.setPostText(postText);
        post.setPostDate(postDate);
        return post;
    }

    public static void main(String[] args) throws IOException, ParseException {
        SqlRuParse sqlParse = new SqlRuParse();
        sqlParse.detail("https://www.sql.ru/forum/1329625/java-razrabotchik-sankt-peterburg");
        sqlParse.list("https://www.sql.ru/forum/job-offers");
    }
}
