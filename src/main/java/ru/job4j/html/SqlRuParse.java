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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

    public void postDetail(String postUrl) throws IOException, ParseException {
        Document doc = Jsoup.connect(postUrl).get();
        String postName = doc.select(".messageHeader").get(0).text();
        String postText = doc.select("td[class='msgBody']").get(1).text();
        String postDate = null;
        String msgFooter = doc.select(".msgFooter").text();
        Pattern pattern = Pattern.compile("^(\\d{1,2}\\s\\D{3}\\s\\d{1,2},\\s\\d{2}:\\d{2})");
        Matcher matcher = pattern.matcher(msgFooter);
        if (matcher.find()) {
            postDate = SqlRuParse.convertDate(matcher.group());
        }
        System.out.println(postUrl);
        System.out.println(postName);
        System.out.println(postText);
        System.out.println(postDate);
    }

    public static void main(String[] args) throws Exception {
        SqlRuParse sqlParse = new SqlRuParse();
        sqlParse.parseList("https://www.sql.ru/forum/job-offers/", 5);
        sqlParse.postDetail("https://www.sql.ru/forum/1325330/lidy-be-fe-senior-cistemnye-analitiki-qa-i-devops-moskva-do-200t");
    }
}
