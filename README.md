# Тестовое задание на позицию Junior/Middle Backend Developer (Java).

## The task is:
### Создать приложение для скачивания новостных статей.
## Источник:
В качестве источника новостей использовать REST API: https://api.spaceflightnewsapi.net/documentation#/Article/get_v3_articles
Требуется создать приложение, выполняющее следующие функции:

## Реализовать Скачивание новостей:
Для новостных статей должна быть подготовлена таблица ARTICLES в базе данных:
id title news_site published_date article
Идентификатор статьи Название статьи Новостной сайт Дата публикации Содержимое статьи

 - При старте приложения должен создаваться пул потоков (количество потоков должно быть настраиваемым). Потоки из данного пула должны параллельно скачивать информацию о новостных статьях (использовать API, описанное выше).
Общее количество скачиваемых записей, а также количество записей, скачиваемых одним потоком за один цикл работы, должно быть настраиваемым.
 - Создать настраиваемый "Черный список новостей". Он должен представлять из себя список слов. Если какое-либо из слов входит в название статьи (поле title), то эта статья должна быть исключена из дальнейшей обработки.
 - Полученные записи должны быть отсортированы по дате публикации (поле publishedAt).
 - После сортировки записи должны быть сгруппированы по названию новостного сайта (поле newsSite).
 - Создать буфер для накопления сгруппированных статей. Буфер должен быть общим для всего пула потоков.
 - Каждый раз, когда поток помещает записи в буфер, должна происходить проверка количества записей в буфере для каждого новостного сайта. Если количество записей в буфере больше или равно заданного (настраиваемого) лимита, то необходимо:
     - взять все записи для данного новостного сайта
     - скачать содержимое статей, используя поле url
     - поместить всю необходимую информацию для скачанных статей в таблицу ARTICLES записи о скачанных статьях должны быть удалены из буфера

 Данный механизм должен запускаться в случае, если скачаны все необходимые записи, независимо от достижения лимита.

## Реализовать API по выдаче новостных статей:
### Реализовать HTTP API по выдаче новостных статей, который :
 - Выдает все сохраненные в БД статьи
 - Даёт возможность запросить конкретную статью по идентификатору
 - Даёт возможность запросить список статей по новостному сайту 
## Используемые технологии и прочие требования:

Самая свежая версия Java (https://openjdk.java.net/)
Любые инструменты из экосистемы Spring (Spring boot, Spring MVC и т.д.)
Хостинг исходного кода Github. Приложение должно быть доступно по прямой ссылке
Легковесная SQL БД, рекомендуется использовать H2
Использование прочих библиотек и фрэймворков не возбраняется

Приложение должно компилироваться и исполняться без ошибок, выполняя свои функции в соответствии с заданием

Условия выполнения для Junior
Пример обучающий, приближенный к реальной задаче. Реализовать ту часть задания и в том объеме, на которую хватает знаний и гугла. Если используются примеры из сети, то нужно их понять и суметь объяснить на собеседовании.
##  - <a href="http://localhost:8080/swagger-ui/index.html">Swagger REST API documentation</a>
##  - <a href="https://github.com/BusyDizzy/NewsDownloader">GitHub</a>

