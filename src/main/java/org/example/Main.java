package org.example;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;

public class Main {
    private static final Scanner SCANNER_USER = new Scanner(System.in);
    private static String keyWordUsers() {
        System.out.println("Введите то что вы хотите найти: ");
        return SCANNER_USER.nextLine();
    }

    private static String getUrl(String keyWord) {
        String encode = URLEncoder.encode(keyWord, StandardCharsets.UTF_8);
        return "https://www.google.com/search?q=" + encode;
    }

    private static List<String> getRefList(String urlPage) throws IOException {
        Document document = Jsoup.connect(urlPage).get();
        List<String> references = new ArrayList<>();
        Elements elements = document.getElementsByAttributeValue("jsname", "UWckNb");
        elements.forEach(element -> references.add(element.attr("href")));
        return references;
    }

    private static void getPageBrowser(String keyWord) throws URISyntaxException, IOException {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI(getRefList(getUrl(keyWord)).get(0)));
        }
    }

    public static void main(String[] args) {
        String findUrl = keyWordUsers();
        try {
            getPageBrowser(findUrl);
        } catch (URISyntaxException | IOException exception) {
            exception.addSuppressed(new Exception());
        } finally {
            SCANNER_USER.close();
        }
    }
}