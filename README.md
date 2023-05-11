# Приложение Job4j_grabber

+ [О проекте](#О-проекте)
+ [Технологии](#Технологии)
+ [Требования к окружению](#Требования-к-окружению)
+ [Запуск проекта](#Запуск-проекта)
+ [Контакты](#Контакты)

## О проекте
Система запускается по расписанию. Период запуска указывается в настройках - app.properties. 
Сайт - career.habr.com. В нем есть раздел https://career.habr.com/vacancies/java_developer. 
Программа считывает все вакансии относящиеся к Java и записывать их в базу.

## Технологии

+ **Maven 3.8**
+ **PostgreSQL 14**
+ **Java 17**
+ **Checkstyle 3.1.2**

## Требования к окружению
+ **Java 17**
+ **Maven 3.8**
+ **PostgreSQL 14**

## Запуск проекта
Перед запуском проекта необходимо настроить подключение к БД в соответствии с параметрами,
указанными в src/main/resources/app.properties, или заменить на свои параметры.

Упаковать проект в jar архив (job4j_grabber/target/job4j_grabber-1.0-SNAPSHOT.jar):
``` 
mvn package
``` 
Запустить приложение:
```
java -jar job4j_grabber-1.0-SNAPSHOT.jar 
```

## Контакты

Свистунов Михаил Сергеевич

[![Telegram](https://img.shields.io/badge/Telegram-blue?logo=telegram)](https://t.me/svoh86)

Email: sms-86@mail.ru