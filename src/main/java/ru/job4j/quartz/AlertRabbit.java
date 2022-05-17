package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Класс описывает вывод сообщения в консоль каждые 10 секунд.
 * Работает через библиотеку http://www.quartz-scheduler.org/.
 *
 * @author Svistunov Mikhail
 * @version 1.0
 */
public class AlertRabbit {
    /**
     * 1. Конфигурирование.
     * Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
     * scheduler.start();
     * Начало работы происходит с создания класса управляющего всеми работами.
     * В объект Scheduler мы будем добавлять задачи, которые хотим выполнять периодически.
     * 2. Создание задачи. JobDetail job = newJob(Rabbit.class).build().
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
     *
     * @param args аргументы
     */
    public static void main(String[] args) {
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDetail job = newJob(Rabbit.class).build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(interval())
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
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
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.println("Rabbit runs here...");
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
}
