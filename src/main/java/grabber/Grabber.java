package grabber;

import grabber.utils.HabrCareerDateTimeParser;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Класс описывает процесс парсинга страницы html, запись полученных даннных в БД
 * и работу с планировщиком.
 *
 * @author Svistunov Mikhail
 * @version 1.0
 */
public class Grabber implements Grab {
    private final Properties config = new Properties();
    private final static String LINK_HABR = "https://career.habr.com/vacancies/java_developer?page=";

    /**
     * Метод чоздает объект PsqlStore, который получает в конструктор Properties
     * и создает Connection
     *
     * @return объект PsqlStore
     */
    public Store store() {
        return new PsqlStore(config);
    }

    /**
     * Метод создает планировщика
     *
     * @return планировщик
     * @throws SchedulerException исключение
     */
    public Scheduler scheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();
        return scheduler;
    }

    /**
     * Чтение файла с настройками
     *
     * @throws IOException исключение
     */
    public void config() throws IOException {
        try (InputStream in = Grabber.class.getClassLoader().getResourceAsStream("app.properties")) {
            config.load(in);
        }
    }

    /**
     * Метод инициализирует работу
     *
     * @param parse     что парсим
     * @param store     куда парсим
     * @param scheduler когда парсим
     * @throws SchedulerException исключение
     */
    @Override
    public void init(Parse parse, Store store, Scheduler scheduler) throws SchedulerException {
        JobDataMap data = new JobDataMap();
        data.put("store", store);
        data.put("parse", parse);
        JobDetail job = newJob(GrabJob.class)
                .usingJobData(data)
                .build();
        SimpleScheduleBuilder times = simpleSchedule()
                .withIntervalInSeconds(Integer.parseInt(config.getProperty("time")))
                .repeatForever();
        Trigger trigger = newTrigger()
                .startNow()
                .withSchedule(times)
                .build();
        scheduler.scheduleJob(job, trigger);
    }

    public static class GrabJob implements Job {
        /**
         * Метод описывает выполнение самой работы
         *
         * @param context контекст
         * @throws JobExecutionException исключение
         */
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            JobDataMap map = context.getJobDetail().getJobDataMap();
            Store store = (Store) map.get("store");
            Parse parse = (Parse) map.get("parse");
            List<Post> posts = parse.list(LINK_HABR);
            posts.forEach(store::save);
        }
    }

    public void web(Store store) {
        new Thread(() -> {
            try (ServerSocket server = new ServerSocket(Integer.parseInt(config.getProperty("port")))) {
                while (!server.isClosed()) {
                    Socket socket = server.accept();
                    try (OutputStream out = socket.getOutputStream()) {
                        out.write("HTTP/1.1 200 OK\r\n\r\n".getBytes());
                        for (Post post : store.getAll()) {
                            out.write(post.toString().getBytes(Charset.forName("Windows-1251")));
                            out.write(System.lineSeparator().getBytes());
                        }
                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) throws Exception {
        Grabber grabber = new Grabber();
        grabber.config();
        Scheduler scheduler = grabber.scheduler();
        Store store = grabber.store();
        grabber.init(new HabrCareerParse(new HabrCareerDateTimeParser()), store, scheduler);
        grabber.web(store);
    }
}
