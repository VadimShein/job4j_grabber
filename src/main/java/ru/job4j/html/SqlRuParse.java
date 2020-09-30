package ru.job4j.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        DateFormatSymbols myDateFormatSymbols = new DateFormatSymbols(){
            @Override
            public String[] getMonths() {
                return new String[] {"янв", "фев", "мар", "апр", "мая", "июн",
                        "июл", "авг", "сен", "окт", "ноя", "дек"};
            }
        };

        int ind = 0;
        while (ind < 5) {
            String url = "https://www.sql.ru/forum/job-offers/" + (ind + 1);
            Document doc = Jsoup.connect(url).get();
            Elements row = doc.select(".postslisttopic");

            System.out.println("page " + (ind + 1) + System.lineSeparator());
            for (Element td : row) {
                Element href = td.child(0);
                Element hrefDate = td.lastElementSibling();
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                String hrefDateTxT = hrefDate.text();

                if (hrefDateTxT.contains("сегодня")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("d M yyyy H:m");
                    final Calendar cal = Calendar.getInstance();
                    System.out.println(sdf.format(cal.getTime()));

                }
                else if (hrefDateTxT.contains("вчера")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("d M yyyy H:m");
                    final Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, -1);
                    System.out.println(sdf.format(cal.getTime()));
                } else {
                    Date date = new SimpleDateFormat("d MMM yy, H:m", myDateFormatSymbols).parse(hrefDateTxT);
                    Locale locales = Locale.getDefault();   //ru_RU
                    String patterns = "d M yyyy H:m";
                    String modData = new SimpleDateFormat(patterns, locales).format(date);
                    System.out.println(modData);
                }
            }
            ind++;
        }
    }
}
