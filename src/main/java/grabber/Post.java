package grabber;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс описывает модель данных.
 *
 * @author Svistunov Mikhail
 * @version 1.0
 */
public class Post {
    /**
     * id - идентификатор вакансии (берется из нашей базы данных);
     * title - название вакансии;
     * link - ссылка на описание вакансии;
     * description - описание вакансии;
     * created - дата создания вакансии.
     */
    private int id;
    private String title;
    private String link;
    private String description;
    private LocalDateTime created;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return id == post.id && Objects.equals(title, post.title)
                && Objects.equals(link, post.link)
                && Objects.equals(description, post.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, link, description);
    }

    @Override
    public String toString() {
        return "Post{" + "id=" + id + ", title='" + title + '\''
                + ", link='" + link + '\'' + ", description='"
                + description + '\'' + ", created=" + created + '}';
    }
}


