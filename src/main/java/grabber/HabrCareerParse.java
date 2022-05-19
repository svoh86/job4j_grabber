package grabber;

import grabber.utils.DateTimeParser;
import grabber.utils.HabrCareerDateTimeParser;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс описывает парсинг HTML страницы
 * https://career.habr.com/vacancies/java_developer через библиотеку jsoup.
 *
 * @author Svistunov Mikhail
 * @version 1.1
 * Добавлен парсинг первых 5 страниц.
 */
public class HabrCareerParse implements Parse {
    /**
     * Константы. Первая это ссылка на сайт в целом.
     * Вторая указывает на страницу с вакансиями непосредственно.
     */
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> posts = new ArrayList<>();
        HabrCareerParse habrCareerParse = new HabrCareerParse(dateTimeParser);
        for (int i = 1; i < 6; i++) {
            Connection connection = Jsoup.connect(String.format("%s%s", link, i));
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                Element dateElement = row.select(".vacancy-card__date").first();
                String title = titleElement.text();
                String linkDescription = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                String date = dateElement.child(0).attr("datetime");
                String description = habrCareerParse.retrieveDescription(linkDescription);
                LocalDateTime created = new HabrCareerDateTimeParser().parse(date);
                Post post = new Post(title, linkDescription, description, created);
                posts.add(post);
            });
        }
        return posts;
    }

    /**
     * Connection connection = Jsoup.connect(PAGE_LINK);
     * Document document = connection.get();
     * Сначала мы получаем страницу, чтобы с ней можно было работать.
     * Анализируя структуру страницы мы получаем, что признаком вакансии является CSS класс .vacancy-card__inner,
     * а признаком названия класс .vacancy-card__title.
     * Ссылка на вакансию вложена в элемент названия,
     * сама же ссылка содержит абсолютный путь к вакансии (относительно домена. Это наша константа SOURCE_LINK)
     * Elements rows = document.select(".vacancy-card__inner");
     * Сначала мы получаем все вакансии страницы.
     * Перед CSS классом ставится точка. Это правила CSS селекторов, с которыми работает метод JSOUP select().
     * Проходимся по каждой вакансии и извлекаем нужные для нас данные.
     * Сначала получаем элементы содержащие название и ссылку.
     * Дочерние элементы можно получать через индекс - метод child(0)
     * или же через селектор - select(".vacancy-card__title").
     * Наконец получаем данные непосредственно. text() возвращает все содержимое элемента в виде текста,
     * т.е. весь текст что находится вне тегов HTML.
     * Ссылку находится в виде атрибута, поэтому ее значение надо получить как значение атрибута.
     * Для этого служит метод attr()
     *
     * @param args аргументы
     * @throws IOException исключения
     */
    public static void main(String[] args) throws IOException {
        HabrCareerDateTimeParser habrCareerDateTimeParser = new HabrCareerDateTimeParser();
        HabrCareerParse habrCareerParse = new HabrCareerParse(habrCareerDateTimeParser);
        List<Post> list = habrCareerParse.list(PAGE_LINK);
        list.forEach(System.out::println);
    }

    private String retrieveDescription(String link) {
        Connection connection = Jsoup.connect(link);
        Document document = null;
        try {
            document = connection.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element descriptionElement = document.select(".job_show_description__vacancy_description").first();
        return descriptionElement.text();
    }
}
