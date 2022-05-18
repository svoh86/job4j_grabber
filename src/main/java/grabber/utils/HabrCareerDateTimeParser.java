package grabber.utils;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * Класс преобразовывает дату из формата career.habr.com "2022-05-18T10:15:30+03:00"
 * в объект LocalDateTime без часового пояса.
 *
 * @author Svistunov Mikhail
 * @version 1.0
 */
public class HabrCareerDateTimeParser implements DateTimeParser {
    @Override
    public LocalDateTime parse(String parse) {
        return ZonedDateTime.parse(parse).toLocalDateTime();
    }
}