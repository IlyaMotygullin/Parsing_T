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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private static void getPageBrowser(String keyWord) throws IOException {
        /** так как getRefList() возвращает список ссылок на главной странице поиска то длинна этого
         * списка и будет количеством потоков, которое я создам*/
        List<String> refList = getRefList(getUrl(keyWord));

        /** создание потоков для анализа всех ссылок */
        /** создание пула потоков*/
        ExecutorService analiseReferences = Executors.newFixedThreadPool(refList.size());
        analiseReferences.submit(() -> {
            for (int i = 0; i < refList.size(); i++) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI(refList.get(i)));
                    } catch (IOException | URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

        analiseReferences.shutdown();
    }

    public static void main(String[] args) {
        String findUrl = keyWordUsers();
        try {
            getPageBrowser(findUrl);
        } catch (IOException exception) {
            exception.addSuppressed(new Exception());
        } finally {
            SCANNER_USER.close();
        }
    }
}