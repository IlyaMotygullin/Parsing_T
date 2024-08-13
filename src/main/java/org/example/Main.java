package org.example;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
    private static void getPageBrowser(String keyWord) throws IOException, URISyntaxException {
//        getRefList(getUrl(keyWord)).forEach(System.out::println); // для наглядности списка в котором хранятся ссылки

        /** так как getRefList() возвращает список ссылок на главной странице поиска то длинна этого
         * списка и будет количеством потоков, которое я создам*/
        List<String> refList = getRefList(getUrl(keyWord));

        /** создание потоков для анализа всех ссылок */
        /** создание пула потоков*/
//        ExecutorService analiseReferences = Executors.newFixedThreadPool(refList.size());
//        analiseReferences.submit(new CheckWebSait(refList));
//        analiseReferences.shutdown();


//        System.out.println(refList.get(0));

        /** получение всех элементов html страницы METSO.COM */
        Document document = Jsoup.connect(refList.get(0)).get();

        /** получение элементов <li> (у этих элементов есть атрибут "role" и значение этого атрибута "option")</>*/
        Elements elements = document.getElementsByAttributeValue("role", "option");

        /** вывод всех элементов <li> </>*/
        elements.forEach(System.out::println);
        //        /ru/metals-refining/


        /** получение ссылок с атрибутом "data-nav-main-cat" и значением "1 Metals refining (hub card)", которые пренадлежат элемету <li></>*/
        Elements elRef = document.getElementsByAttributeValue("data-nav-main-cat", "1 Metals refining (hub card)");
        List<String> strings = new ArrayList<>();

        /** добавление всех ссылок в лист */
        elRef.forEach(element -> strings.add(element.attr("href")));

        /** вывод всех неправильных ссылок */
        strings.forEach(System.out::println);

        /** лист в котором будут хранится корректные ссылки */
        List<String> correctReferencesList = new ArrayList<>();

        for (int i = 0; i < strings.size(); i++) {

            /** преобразование всех ссылок в корректный вид и добавление их в список для корректных ссылок */
            String correctReferences = "https://www.metso.com".concat(strings.get(i));
            correctReferencesList.add(correctReferences);
        }

        /** отображение всех корректных ссылок */
        correctReferencesList.forEach(System.out::println);
    }

    public static void main(String[] args) {
        String findUrl = keyWordUsers();
        try {
            getPageBrowser(findUrl);
        } catch (IOException exception) {
            exception.addSuppressed(new Exception());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        } finally {
            SCANNER_USER.close();
        }
    }
}