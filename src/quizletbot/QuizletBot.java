package quizletbot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.Scanner;

public class QuizletBot {
    public static final String quizletPrefix = "https://www.google.com/url?q=https://quizlet.com";
    public static void main(String[] args) throws IOException {
        Scanner in = new java.util.Scanner(System.in);
        String query;
        while (!(query = in.nextLine()).equalsIgnoreCase("exit")) {
            search(query);
            System.out.println("Search completed.");
        }
    }
    public static void search(String query) throws IOException {
        Document doc = Jsoup.connect("https://google.com/search?q=\"" + query.replace(" ", "%20") + "\"%20quizlet")
                .userAgent("Mozilla")
                .cookie("auth", "token")
                .timeout(5000)
                .get();
        Elements links = doc.select("a[href]");
        for (Element link : links) {
            if (link.attr("abs:href").startsWith("https://www.google.com/url?q=https://quizlet.com")) {
                try {
                    Document page = Jsoup.connect(link.attr("abs:href").substring(29))
                            .userAgent("Mozilla")
                            .cookie("auth", "token")
                            .timeout(5000)
                            .get();
                    Elements terms = page.select("span[class='TermText notranslate lang-en']");
                    for (int i = 0; i < terms.size(); i++) {
                        if ((i % 2 == 0) && ((terms.get(i)).toString().toLowerCase().indexOf(query.toLowerCase()) > 0)) {
                            System.out.println(chop(terms.get(i)));
                            System.out.println(chop(terms.get(i + 1)));
                            System.out.println();
                            i++;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error parsing link. Onwards!");
                }
            }
        }
    }
    public static String chop(Element a) {
        String s = a.toString();
        try {
            s = s.substring(s.indexOf(">") + 1);
            return s.substring(0, s.indexOf("<"));
        } catch (Exception e) {
            return "Error parsing term.";
        }
    }
}