package grabber;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Класс описывает запись, получение из БД и поиск в БД.
 *
 * @author Svistunov Mikhail
 * @version 1.0
 */
public class PsqlStore implements Store, AutoCloseable {
    private Connection cnn;

    public PsqlStore(Properties config) {
        try {
            Class.forName(config.getProperty("driver_class"));
            cnn = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement ps = cnn.prepareStatement(
                "insert into post(name, text, link, created) values(?, ?, ?, ?)")) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getDescription());
            ps.setString(3, post.getLink());
            ps.setTimestamp(4, Timestamp.valueOf(post.getCreated()));
            ps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> postList = new ArrayList<>();
        try (PreparedStatement ps = cnn.prepareStatement("select * from post")) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    postList.add(getPost(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return postList;
    }

    @Override
    public Post findById(int id) {
        Post post = null;
        try (PreparedStatement ps = cnn.prepareStatement(
                "select * from post where id=?")) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    post = getPost(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    private Post getPost(ResultSet resultSet) throws SQLException {
        return new Post(
                resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4),
                resultSet.getTimestamp(5).toLocalDateTime()
        );
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }

    public static void main(String[] args) throws IOException {
        Properties config = new Properties();
        try (InputStream in = PsqlStore.class.getClassLoader().getResourceAsStream("grabber.properties")) {
            config.load(in);
        }
        PsqlStore psqlStore = new PsqlStore(config);
        Post first = new Post("Android разработчик",
                "https://career.habr.com/vacancies/1000097828",
                """
                        myTarget и AdTech - самая крупная в РФ рекламная сеть, мы создали такие SDK как:
                        https://github.com/myTargetSDK... и https://github.com/myTrackerSD...
                        Мы написали адаптеры для таких сетей, как Admob, Facebook, Unity,IronSource, Mintegral, InMobi и TikTok.\s
                        """,
                LocalDateTime.now());
        Post second = new Post("Java Developer",
                "https://career.habr.com/vacancies/1000103713",
                """
                        Чем предстоит заниматься:
                        разработка и поддержка сервисов взаимодействия с клиентами на Java 8-17, Spring;
                        участие в планировании микросервисной архитектуры;
                        взаимодействие с разработчиками back/front, аналитиками, тестировщиками;
                        участие в Code review.""",
                LocalDateTime.of(2022, 5, 19, 10, 0));
        psqlStore.save(first);
        psqlStore.save(second);
        psqlStore.getAll().forEach(System.out::println);
        System.out.println("---".repeat(20));
        System.out.println(psqlStore.findById(1));
    }
}
