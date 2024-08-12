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

    /** создание URL(запроса)*/
    private static String getUrl(String keyWord) {
        String encode = URLEncoder.encode(keyWord, StandardCharsets.UTF_8); // для коректного создания части URL адреса,
        // т.е если будут пробелы или дрегие знаки, то они будут заменены ликвидными символами для url адреса
        return "https://www.google.com/search?q=" + encode; // сам URL адрес
    }

    /** получение ссылок и добавление их в лист
     * данный метод принимает urlPage. urlPage - это готовый url(он как раз и получается из метода getUrl())*/
    private static List<String> getRefList(String urlPage) throws IOException {
        Document document = Jsoup.connect(urlPage).get(); // получение доступа к странице на которой собраны все ссылки
        List<String> references = new ArrayList<>(); // список для хранения ссылок(его этот метод и возвращает)

        /* получение группы элементов html по атрибутам их значениям этих атрибутов
        * данный метод getElementsByAttributeValue() возвращает Elements, который в свою очередь расширяет ArrayList<>()*/
        Elements elements = document.getElementsByAttributeValue("jsname", "UWckNb");

        /*c помощью Stream Api пробегаюсь по списку Elements и добавляю их в свой лист: references*/
        elements.forEach(element -> references.add(element.attr("href")));
        return references;
    }

    /** метод благодаря которому открываются вкладки в браузере */
    private static void getPageBrowser(String keyWord) throws IOException {
//        getRefList(getUrl(keyWord)).forEach(System.out::println); // для наглядности списка в котором хранятся ссылки

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
                        /* с помощью Desktop получаю доступ к браузеру, который запускается по дефолту */
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