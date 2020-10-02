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


public class SqlRuParse {
    public void parseList(String url, int pages) throws IOException, ParseException {
        String postUrl ;
        String postName;
        String postDate;

        int index = 0;
        while (index < pages) {
            String PageUrl = url + (index + 1); //"https://www.sql.ru/forum/job-offers/"
            Document doc = Jsoup.connect(PageUrl).get();
            Elements row = doc.select(".postslisttopic");
            System.out.println(System.lineSeparator() + "Page: " + (index + 1) + System.lineSeparator());
            for (Element td : row) {
                Element href = td.child(0);
                Element hrefDate = td.lastElementSibling();
                postUrl = href.attr("href");
                postName = href.text();
                postDate = SqlRuParse.convertDate(hrefDate.text());

                System.out.println(postUrl);
                System.out.println(postName);
                System.out.println(postDate);
            }
            index++;
        }
    }

    public static String convertDate(String postDate) throws ParseException {
        String modData;
        DateFormatSymbols myDateFormatSymbols = new DateFormatSymbols(){
            @Override
            public String[] getMonths() {
                return new String[] {"янв", "фев", "мар", "апр", "май", "июн",
                        "июл", "авг", "сен", "окт", "ноя", "дек"};
            }
        };
        if (postDate.contains("сегодня")) {
            SimpleDateFormat sdf = new SimpleDateFormat("d M yyyy H:m");
            final Calendar cal = Calendar.getInstance();
            modData = sdf.format(cal.getTime());
        } else if (postDate.contains("вчера")) {
            SimpleDateFormat sdf = new SimpleDateFormat("d M yyyy H:m");
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            modData = sdf.format(cal.getTime());
        } else {
            Date date = new SimpleDateFormat("d MMM yy, H:m", myDateFormatSymbols).parse(postDate);
            Locale locales = Locale.getDefault();   //ru_RU
            String patterns = "d M yyyy H:m";
            modData = new SimpleDateFormat(patterns, locales).format(date);
        }
        return modData;
    }

    public static void main(String[] args) throws Exception {
        SqlRuParse sqlParse = new SqlRuParse();
        sqlParse.parseList("https://www.sql.ru/forum/job-offers/", 5);
    }
}
