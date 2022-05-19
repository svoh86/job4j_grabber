package grabber;

import java.util.List;

/**
 * Описание компонента через интерфейс позволяет расширить наш проект:
 * осуществить сбор данных с других площадок
 */
public interface Store {
    void save(Post post);

    List<Post> getAll();

    Post findById(int id);
}
