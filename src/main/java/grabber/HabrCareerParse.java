package grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Класс описывает парсинг HTML страницы
 * https://career.habr.com/vacancies/java_developer через библиотеку jsoup.
 *
 * @author Svistunov Mikhail
 * @version 1.0
 */
public class HabrCareerParse {
    /**
     * Две константы. Первая это ссылка на сайт в целом.
     * Вторая указывает на страницу с вакансиями непосредственно.
     */
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

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
        Connection connection = Jsoup.connect(PAGE_LINK);
        Document document = connection.get();
        Elements rows = document.select(".vacancy-card__inner");
        rows.forEach(row -> {
            Element titleElement = row.select(".vacancy-card__title").first();
            Element linkElement = titleElement.child(0);
            Element dateElement = row.select(".vacancy-card__date").first();
            /*
            Element dateElement = row.child(0);
             */
            String vacancyName = titleElement.text();
            String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
            String date = dateElement.text();
            /*
            String date = dateElement.child(0).attr("datetime");
             */
            System.out.printf("%s %s %s%n", vacancyName, link, date);
        });
    }
}
