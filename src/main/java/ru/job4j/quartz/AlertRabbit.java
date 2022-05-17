package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Класс описывает работу планировщика с БД.
 * Работает через библиотеку http://www.quartz-scheduler.org/.
 *
 * @author Svistunov Mikhail
 * @version 1.1
 */
public class AlertRabbit {
    /**
     * 1. Конфигурирование.
     * Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
     * scheduler.start();
     * Начало работы происходит с создания класса управляющего всеми работами.
     * В объект Scheduler мы будем добавлять задачи, которые хотим выполнять периодически.
     * 2. Создание задачи.
     * JobDataMap data = new JobDataMap();
     * data.put("connection", cn);
     * JobDetail job = newJob(Rabbit.class).usingJobData(data).build();
     * При создании Job мы указываем параметры data. В них мы передаем ссылку на Connection.
     * quartz каждый раз создает объект с типом org.quartz.Job.
     * Нам нужно создать класс реализующий этот интерфейс. Внутри этого класса нужно описать требуемые действия.
     * В нашем случае - это вывод на консоль текста.
     * 3. Создание расписания.
     * SimpleScheduleBuilder times = simpleSchedule().withIntervalInSeconds(10).repeatForever();
     * Конструкция выше настраивает периодичность запуска.
     * В нашем случае, мы будем запускать задачу через 10 секунд и делать это бесконечно.
     * 4. Задача выполняется через триггер.
     * Trigger trigger = newTrigger().startNow().withSchedule(times).build();
     * Здесь можно указать, когда начинать запуск. Мы хотим сделать это сразу.
     * 5. Загрузка задачи и триггера в планировщик.
     * scheduler.scheduleJob(job, trigger);
     * 6. Thread.sleep(10000);
     * scheduler.shutdown();
     * Весь main должен работать 10 секунд.
     *
     * @param args аргументы
     */
    public static void main(String[] args) {
        Properties config = properties();
        try (Connection cn = getConnection(config)) {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("connection", cn);
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval())
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * quartz каждый раз создает объект с типом org.quartz.Job.
     * Нам нужно создать класс реализующий этот интерфейс.
     * Внутри этого класса нужно описать требуемые действия.
     * В нашем случае - это вывод на консоль текста.
     */
    public static class Rabbit implements Job {
        /**
         * Каждый запуск Job вызывает конструктор.
         */
        public Rabbit() {
            System.out.println(hashCode());
        }

        /**
         * Метод записывает в таблицу время, когда выполнена Job.
         * Чтобы получить объекты из context используется следующий вызов:
         * Connection cn = (Connection) context.getJobDetail().getJobDataMap().get("connection");
         *
         * @param context контекст
         * @throws JobExecutionException исключение
         */
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here...");
            Connection cn = (Connection) context.getJobDetail().getJobDataMap().get("connection");
            try (PreparedStatement ps = cn.prepareStatement(
                    "insert into rabbit(created_date) values(?)")) {
                ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Метод читает значение периода запуска в расписании из файла properties.
     *
     * @return значение периода запуска
     */
    private static int interval() {
        int interval = -1;
        Properties config = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader().
                getResourceAsStream("rabbit.properties")) {
            config.load(in);
            interval = Integer.parseInt(config.getProperty("rabbit.interval"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return interval;
    }

    private static Connection getConnection(Properties config) throws SQLException, ClassNotFoundException {
        Class.forName(config.getProperty("driver_class"));
        return DriverManager.getConnection(
                config.getProperty("url"),
                config.getProperty("username"),
                config.getProperty("password")
        );
    }

    private static Properties properties() {
        Properties config = new Properties();
        try (InputStream in = AlertRabbit.class.getClassLoader().
                getResourceAsStream("rabbit.properties")) {
            config.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }
}
