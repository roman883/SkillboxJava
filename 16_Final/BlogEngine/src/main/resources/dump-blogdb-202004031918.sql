-- MySQL dump 10.13  Distrib 5.5.62, for Win64 (AMD64)
--
-- Host: localhost    Database: blogdb
-- ------------------------------------------------------
-- Server version	8.0.19

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `captcha_codes`
--

DROP TABLE IF EXISTS `captcha_codes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `captcha_codes` (
  `id` int NOT NULL,
  `code` tinytext NOT NULL,
  `secret_code` tinytext NOT NULL,
  `time` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `captcha_codes`
--

LOCK TABLES `captcha_codes` WRITE;
/*!40000 ALTER TABLE `captcha_codes` DISABLE KEYS */;
INSERT INTO `captcha_codes` VALUES (80,'nicak','r227povza3erz511yh6epl','2020-04-03 16:09:54'),(84,'betov','kkcz5vwbsgsbkafxybtnmm','2020-04-03 17:39:46');
/*!40000 ALTER TABLE `captcha_codes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `global_settings`
--

DROP TABLE IF EXISTS `global_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `global_settings` (
  `id` int NOT NULL,
  `code` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `global_settings`
--

LOCK TABLES `global_settings` WRITE;
/*!40000 ALTER TABLE `global_settings` DISABLE KEYS */;
INSERT INTO `global_settings` VALUES (12,'MULTIUSER_MODE','Многопользовательский режим','YES'),(13,'POST_PREMODERATION','Премодерация постов','NO'),(14,'STATISTICS_IS_PUBLIC','Показывать всем статистику блога','NO');
/*!40000 ALTER TABLE `global_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hibernate_sequence`
--

DROP TABLE IF EXISTS `hibernate_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hibernate_sequence`
--

LOCK TABLES `hibernate_sequence` WRITE;
/*!40000 ALTER TABLE `hibernate_sequence` DISABLE KEYS */;
INSERT INTO `hibernate_sequence` VALUES (87),(87),(87),(87),(87),(87),(87),(87);
/*!40000 ALTER TABLE `hibernate_sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_comments`
--

DROP TABLE IF EXISTS `post_comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `post_comments` (
  `id` int NOT NULL,
  `text` text NOT NULL,
  `time` datetime NOT NULL,
  `parent_id` int DEFAULT NULL,
  `post_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKc3b7s6wypcsvua2ycn4o1lv2c` (`parent_id`),
  KEY `FKaawaqxjs3br8dw5v90w7uu514` (`post_id`),
  KEY `FKsnxoecngu89u3fh4wdrgf0f2g` (`user_id`),
  CONSTRAINT `FKaawaqxjs3br8dw5v90w7uu514` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
  CONSTRAINT `FKc3b7s6wypcsvua2ycn4o1lv2c` FOREIGN KEY (`parent_id`) REFERENCES `post_comments` (`id`),
  CONSTRAINT `FKsnxoecngu89u3fh4wdrgf0f2g` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_comments`
--

LOCK TABLES `post_comments` WRITE;
/*!40000 ALTER TABLE `post_comments` DISABLE KEYS */;
INSERT INTO `post_comments` VALUES (47,'Первый комментарий 17,34<div>МНого текста Первый комментарий 17,34 МНого текста Первый комментарий 17,34 МНого текста Первый комментарий 17,34 МНого текста Первый комментарий 17,34 МНого текста Первый комментарий 17,34 МНого текста Первый комментарий 17,34 МНого текста Первый комментарий 17,34</div>','2020-04-01 17:34:39',NULL,37,2),(57,'It\'s a comment for the post.    че 10 и 500 символов соответственное значение. Пост должен сохраняться со ста ошибку и не добавлять пост. Время публикации поста также должно проверяться: в случае, если время публикации раньше текущего времени, оно должно автоматически становиться текущим. Если позже случи позже текущего - необходимо устанавливать указанное значение. Пост должен сохраняться со стаобходимо устанавливать указанное значение. Пост должен сохраняться со ста','2020-04-01 19:44:24',NULL,40,2),(79,'It\'s a comment for the post. Метод добавляет комментарий к посту. Должны проверяться все три параметра. Если параметры parent_id и/или post_id неверные (соответствующие комментарий и/или пост не существуют), должна выдаваться ошибка 400 (см. ниже раздел “Обработка ошибок”). В случае, если текст комментария отсутствует (пустой) или слишком короткий, необходимо выдавать ошибку в JSON-формате. Метод добавляет комментарий к посту. Должны проверяться все три параметра. Если параметры parent_id и/или post_id неверные (соответствующие комментарий и/или пост не существуют), должна выдаваться ошибка 400 (см. ниже раздел “Обработка ошибок”). В случае, если текст комментария отсутствует (пустой) или слишком короткий, необходимо выдавать ошибку в JSON-формате.','2020-04-03 16:03:54',NULL,70,2);
/*!40000 ALTER TABLE `post_comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_votes`
--

DROP TABLE IF EXISTS `post_votes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `post_votes` (
  `id` int NOT NULL,
  `time` datetime NOT NULL,
  `value` tinyint NOT NULL,
  `post_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9jh5u17tmu1g7xnlxa77ilo3u` (`post_id`),
  KEY `FK9q09ho9p8fmo6rcysnci8rocc` (`user_id`),
  CONSTRAINT `FK9jh5u17tmu1g7xnlxa77ilo3u` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`),
  CONSTRAINT `FK9q09ho9p8fmo6rcysnci8rocc` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_votes`
--

LOCK TABLES `post_votes` WRITE;
/*!40000 ALTER TABLE `post_votes` DISABLE KEYS */;
INSERT INTO `post_votes` VALUES (20,'2020-04-01 03:38:13',-1,8,18),(25,'2020-04-01 04:03:00',1,8,22),(31,'2020-04-01 04:09:37',1,26,22),(42,'2020-04-01 17:33:30',1,26,2),(43,'2020-04-01 17:33:32',1,8,2),(46,'2020-04-01 17:34:12',1,37,2),(56,'2020-04-01 19:43:41',-1,40,2),(82,'2020-04-03 16:41:04',-1,41,2),(83,'2020-04-03 16:41:23',1,38,2);
/*!40000 ALTER TABLE `post_votes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `posts` (
  `id` int NOT NULL,
  `is_active` bit(1) NOT NULL,
  `moderation_status` varchar(255) NOT NULL,
  `moderator_id` int DEFAULT NULL,
  `text` text NOT NULL,
  `time` datetime NOT NULL,
  `title` varchar(255) NOT NULL,
  `view_count` int NOT NULL,
  `user_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5lidm6cqbc7u4xhqpxm898qme` (`user_id`),
  KEY `FK6m7nr3iwh1auer2hk7rd05riw` (`moderator_id`),
  CONSTRAINT `FK5lidm6cqbc7u4xhqpxm898qme` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK6m7nr3iwh1auer2hk7rd05riw` FOREIGN KEY (`moderator_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts`
--

LOCK TABLES `posts` WRITE;
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
INSERT INTO `posts` VALUES (3,'','DECLINED',2,'И тут начинается текст.<div><span style=\"color: rgb(34, 34, 34); font-family: -apple-system, BlinkMacSystemFont, Arial, sans-serif; font-size: 16px; background-color: rgb(255, 255, 255);\">В прошлой статье мы рассказали,&nbsp;</span><a href=\"http://habrahabr.ru/company/infobox/blog/237405/\" style=\"background-color: rgb(255, 255, 255); color: rgb(153, 34, 152); text-decoration-line: none; font-family: -apple-system, BlinkMacSystemFont, Arial, sans-serif; font-size: 16px;\">что такое Docker</a><span style=\"color: rgb(34, 34, 34); font-family: -apple-system, BlinkMacSystemFont, Arial, sans-serif; font-size: 16px; background-color: rgb(255, 255, 255);\">&nbsp;и как с его помощью можно обойти Vendor–lock. В этой статье мы поговорим о Dockerfile как о правильном способе подготовки образов для Docker. Также мы рассмотрим ситуацию, когда контейнерам&nbsp;</span></div>','2020-04-01 10:59:00','Первый пост о чем-то',0,2),(8,'','ACCEPTED',2,'Lots of text here.<div><div>В прошлой статье мы рассказали, что такое Docker и как с его помощью можно обойти Vendor–lock. В этой статье мы поговорим о Dockerfile как о правильном способе подготовки образов для Docker. Также мы рассмотрим ситуацию, когда контейнерам нужно взаимодействовать друг с другом.</div><div><br></div><div><br></div><div>В InfoboxCloud мы сделали готовый образ Ubuntu 14.04 с Docker. Не забудьте поставить галочку «Разрешить управление ядром ОС» при создани</div><div><div>Давайте создадим простой образ с веб-сервером с помощью&nbsp;</div></div><div>Давайте создадим простой образ с веб-сервером с помощью&nbsp;</div></div>','2020-04-01 11:01:00','Second test post',3,2),(26,'','ACCEPTED',2,'Some text here<div><span style=\"color: rgb(34, 34, 34); font-family: -apple-system, BlinkMacSystemFont, Arial, sans-serif; font-size: 16px; background-color: rgb(255, 255, 255);\">ava Generics — это одно из самых значительных изменений за всю историю языка Java. «Дженерики», доступные с Java 5, сделали использование Java Collection Framework проще, удобнее и безопаснее. Ошибки, связанные с некорректным использованием типов, теперь обнаруживаются на этапе компиляции. Да и сам язык Java стал еще безопаснее. Несмотря на кажущуюся простоту обобщенных типов, многие разработчики сталкиваются с трудностями при их использовании. В этом посте я расскажу об особенностях работы с Java Generics, чтобы этих трудностей у вас было поменьше. Пригодится, если вы не гуру в дженериках, и поможет избежать много трудностей при погружении в тему.</span></div><div><span style=\"color: rgb(34, 34, 34); font-family: -apple-system, BlinkMacSystemFont, Arial, sans-serif; font-size: 16px; background-color: rgb(255, 255, 255);\">ava Generics — это одно из самых значительных изменений за всю историю языка Java. «Дженерики», доступные с Java 5, сделали использование Java Collection Framework проще, удобнее и безопаснее. Ошибки, связанные с некорректным использованием типов, теперь обнаруживаются на этапе компиляции. Да и сам язык Java стал еще безопаснее. Несмотря на кажущуюся простоту обобщенных типов, многие разработчики сталкиваются с трудностями при их использовании. В этом посте я расскажу об особенностях работы с Java Generics, чтобы этих трудностей у вас было поменьше. Пригодится, если вы не гуру в дженериках, и поможет избежать много трудностей при погружении в тему.</span></div>','2020-04-01 13:06:50','Third Post to test',2,22),(32,'','ACCEPTED',2,'<ul style=\"box-sizing: border-box; padding-left: 2em; margin-top: 0px; color: rgb(36, 41, 46); font-family: -apple-system, BlinkMacSystemFont, &quot;Segoe UI&quot;, Helvetica, Arial, sans-serif, &quot;Apple Color Emoji&quot;, &quot;Segoe UI Emoji&quot;; font-size: 16px; background-color: rgb(255, 255, 255); margin-bottom: 0px !important;\"><li style=\"box-sizing: border-box;\"><span style=\"box-sizing: border-box; font-weight: 600;\">Страница не существует</span>&nbsp;- пустой ответ с кодом&nbsp;<span style=\"box-sizing: border-box; font-weight: 600;\">404 (Not found)</span></li><li style=\"box-sizing: border-box; margin-top: 0.25em;\">На frontend должна показываться заставка \"Запрошенная вами страница была скрыта или не существует\"</li><li style=\"box-sizing: border-box; margin-top: 0.25em;\"><span style=\"box-sizing: border-box; font-weight: 600;\">Пользователь не авторизован</span>&nbsp;- пустой ответ с кодом&nbsp;<span style=\"box-sizing: border-box; font-weight: 600;\">401 (Unauthorized)</span></li><li style=\"box-sizing: border-box; margin-top: 0.25em;\">атитесь к администратору\"</li></ul>','2019-04-01 13:42:24','new publication',0,22),(34,'\0','NEW',NULL,'Тест времени. 16.21<div><span style=\"font-family: Arial, Helvetica, sans-serif; font-size: 16px;\">В данном формате комментарии всегда начинаются с новой строки и с символа «#». Имя каждого параметра прописывается полностью (и это один из недостатков данного формата), затем идёт «=», затем само значение. Текстовые значения можно указывать как в кавычках, так и без них. Список значений, который в нашем приложении превратится в объект типа&nbsp;</span><span style=\"box-sizing: inherit; font-weight: bolder; font-family: Arial, Helvetica, sans-serif; font-size: 16px;\">List</span><span style=\"font-family: Arial, Helvetica, sans-serif; font-size: 16px;\">, в конце имени каждого значения имеет индекс в квадратных скобках. Такой синтаксис похож на объявление массива.</span><span style=\"font-family: Arial, Helvetica, sans-serif; font-size: 16px;\">В данном формате комментарии всегда начинаются с новой строки и с символа «#». Имя каждого параметра прописывается полностью (и это один из недостатков данного формата), затем идёт «=», затем само значе</span></div>','2020-04-01 07:21:56','Еще одна публикация',0,16),(37,'','ACCEPTED',2,'Тест времени 16.26<div><span style=\"font-family: Arial, Helvetica, sans-serif; font-size: 16px;\">В данном формате комментарии всегда начинаются с новой строки и с символа «#». Имя каждого параметра прописывается полностью (и это один из недостатков данного формата), затем идёт «=», затем само значение. Текстовые значения можно указывать как в кавычках, так и без них. Список значений, который в нашем приложении&nbsp;</span><span style=\"font-family: Arial, Helvetica, sans-serif; font-size: 16px;\">В данном формате комментарии всегда начинаются с новой строки и с символа «#». Имя каждого параметра прописывается полностью (и это один из недостатков данного формата), затем идёт «=», затем само значение. Текстовые значения мож</span></div>','2020-04-01 07:28:39','Очередная публикация',1,16),(38,'\0','ACCEPTED',2,'16.34 oClock<span class=\"token variable\" style=\"box-sizing: inherit; margin: 0px; padding: 0px; border: 0px; outline: 0px; font-size: 12.8304px; vertical-align: baseline; background: rgb(248, 248, 248); color: rgb(238, 153, 0); font-family: &quot;Liberation Mono&quot;, Consolas, Monaco, &quot;Andale Mono&quot;, &quot;Ubuntu Mono&quot;, monospace; white-space: pre;\">@@GLOBAL.time_zone</span><span class=\"token punctuation\" style=\"box-sizing: inherit; margin: 0px; padding: 0px; border: 0px; outline: 0px; font-size: 12.8304px; vertical-align: baseline; background: rgb(248, 248, 248); color: rgb(153, 153, 153); font-family: &quot;Liberation Mono&quot;, Consolas, Monaco, &quot;Andale Mono&quot;, &quot;Ubuntu Mono&quot;, monospace; white-space: pre;\">,</span><span style=\"font-family: &quot;Liberation Mono&quot;, Consolas, Monaco, &quot;Andale Mono&quot;, &quot;Ubuntu Mono&quot;, monospace; font-size: 12.8304px; white-space: pre; background-color: rgb(248, 248, 248);\"> </span><span class=\"token variable\" style=\"box-sizing: inherit; margin: 0px; padding: 0px; border: 0px; outline: 0px; font-size: 12.8304px; vertical-align: baseline; background: rgb(248, 248, 248); color: rgb(238, 153, 0); font-family: &quot;Liberation Mono&quot;, Consolas, Monaco, &quot;Andale Mono&quot;, &quot;Ubuntu Mono&quot;, monospace; white-space: pre;\">@@SESSION.time_zone</span><span class=\"token punctuation\" style=\"box-sizing: inherit; margin: 0px; padding: 0px; border: 0px; outline: 0px; font-size: 12.8304px; vertical-align: baseline; background: rgb(248, 248, 248); color: rgb(153, 153, 153); font-family: &quot;Liberation Mono&quot;, Consolas, Monaco, &quot;Andale Mono&quot;, &quot;Ubuntu Mono&quot;, monospace; white-space: pre;\">;</span>','2020-04-01 07:35:22','Publication at 16.34',0,16),(40,'','NEW',2,'New editted text body here. There is a lot of text here. Soooo lot. Метод отправляет данные поста, которые пользователь ввёл в форму публикации. В случае, если заголовок или текст поста не установлены и/или слишком короткие (короче 10 и 500 символов соответственно), метод должен выводить ошибку и не добавлять пост. Время публикации поста также должно проверяться: в случае, если время публикации раньше текущего времени, оно должно автоматически становиться текущим. Если позже текуще время публикации раньше текущеговлять пост. Время публикации поста также должно проверяться: в случае, если время публикации раньше текущего времени, оно должно автоматически становиться текущим. Если позже текущего - необходимо устанавливать указанное значение. Пост должен сохраняться со статусом модерации “NEW”. There is a lot of text here. Soooo lot. Метод отправляет данные поста, которые пользователь ввёл в форму публикации. В случае, если заголовок или текст поста не установлены и/или слишком короткие (короче 10 и 500 символов соответственно), метод должен выводить ошибку и не добавлять пост. Время публикации поста также должно проверяться: в с. В случае, если заголовок или текст поста не установлены и/или слишком короткие (короче 10 и 500 символов соответственно), метод должен выводить ошибку и не добавлять пост. Время публикации поста также должно проверяться: в случае, если время публикации раньше текущего времени, оно должно автоматически становиться текущим. Если позже текущего - необходимо устанавливать указанное значение. Пост должен сохраняться со статусом модерации','2020-04-01 19:50:42','New Postman test title',1,16),(41,'','ACCEPTED',2,'Здесь могла бы быть ваша реклама. Здесь могла бы быть ваша реклама. Здесь могла бы быть ваша реклама. Здесь могла бы быть ваша реклама. Здесь могла бы быть ваша реклама. Здесь могла бы быть ваша реклама. Здесь могла бы быть ваша реклама. Здесь могла бы быть ваша реклама. Здесь могла бы быть ваша реклама. Здесь могла бы быть ваша реклама. Здесь могла бы быть ваша реклама. Здесь могла бы быть ваша реклама. Здесь могла бы быть ваша реклама. Здесь могла бы быть ваша реклама. Здесь могла бы быть ваша реклама. Здесь могла бы быть ваша реклама. Здесь могла бы быть ваша реклама. Здесь могла бы быть ваша реклама.&nbsp;','2020-04-01 07:48:20','16 47 Очередная публикация',1,16),(50,'','NEW',NULL,'There is a lot of text here. SThere is a lot of text here. Soooo lot. Метод отправляет данные поста, которые пользователь ввёл в форму публикации. В случае, если заголовок или текст поста не установлены и/или слишком короткие (короче 10 и 5сции раньше текущ), методе текуще вреThere is a lot of text here. SThere is a lot of text here. Soooo lot. Метод отправляет данные поста, которые пользователь ввёл в форму публикации. В случае, если заголовок или текст поста не установлены и/или слишком короткие (короче 10 и 5сции раньше текущ), методе текуще вреThere is a lot of text here. SThere is a lot of text here. Soooo lot. Метод отправляет данные поста, которые пользователь ввёл в форму публикации. В случае, если заголовок или текст поста не установлены и/или слишком короткие (короче 10 и 5сции раньше текущ), методе текуще вре','2020-04-01 19:41:15','Title is here',0,2),(70,'','ACCEPTED',NULL,'Метод изменяет данные поста с идентификатором ID на те, которые пользователь ввёл в форму публикации. В случае, если заголовок или текст поста не установлены и/или слишком короткие (короче 10 и 500 символов соответственно), метод должен выводить ошибку и не изменять пост. Время публикации поста также должно проверяться: в случае, если время публикации раньше текущего времени, оно должно автоматически становиться текущим. Если позже текущего - необходимо устанавливать указанное значение.Пост должен сохраняться со статусом модерации “NEW”, если его изменил автор, и статус модерации не должен изменяться, если его изменил модератор.','2020-04-03 16:01:26','Редактирование поста',0,2),(71,'','NEW',NULL,'This lesson describes how to use NetBeans to build a simple application. With a good tool like NetBeans, you can assemble JavaBeans components into an application without having to write any code. The first three pages of this lesson show how to create a simple application using graphic beans that are part of the Java platform. The last page demonstrates how easy it is to incorporate a third-party bean into your application. Creating a Project describes the steps for setting up a new project in NetBeans. A Button is a Bean shows how to add a bean to the application\'s user interface and describes properties and events. Wiring the Application covers using NetBeans to respond to bean events in your application. Using a Third-Party Bean show how easy it is to add a new bean to the palette and use it in your application.','2020-04-03 13:18:12','Lesson: Quick Start',0,2);
/*!40000 ALTER TABLE `posts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tag2post`
--

DROP TABLE IF EXISTS `tag2post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tag2post` (
  `id` int NOT NULL,
  `post_id` int NOT NULL,
  `tag_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKpjoedhh4h917xf25el3odq20i` (`post_id`),
  KEY `FKjou6suf2w810t2u3l96uasw3r` (`tag_id`),
  CONSTRAINT `FKjou6suf2w810t2u3l96uasw3r` FOREIGN KEY (`tag_id`) REFERENCES `tags` (`id`),
  CONSTRAINT `FKpjoedhh4h917xf25el3odq20i` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tag2post`
--

LOCK TABLES `tag2post` WRITE;
/*!40000 ALTER TABLE `tag2post` DISABLE KEYS */;
INSERT INTO `tag2post` VALUES (5,3,4),(7,3,6),(10,8,9),(11,8,4),(28,26,27),(30,26,29),(33,32,6),(36,34,35),(39,38,6),(51,50,35),(53,50,52),(59,40,58),(61,40,60),(72,71,29),(74,71,73),(76,70,75),(78,70,77);
/*!40000 ALTER TABLE `tag2post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tags`
--

DROP TABLE IF EXISTS `tags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tags` (
  `id` int NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tags`
--

LOCK TABLES `tags` WRITE;
/*!40000 ALTER TABLE `tags` DISABLE KEYS */;
INSERT INTO `tags` VALUES (4,'Docker'),(6,'Tag'),(9,'Dock'),(27,'Generics'),(29,'Java'),(35,'Spring'),(52,'test tag'),(58,'some tag'),(60,'new test tag'),(73,'test'),(75,'Spring'),(77,'Java');
/*!40000 ALTER TABLE `tags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` int NOT NULL,
  `code` varchar(255) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `is_moderator` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `photo` text,
  `reg_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK_3g1j96g94xpk3lpxl2qbl985x` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (2,NULL,'mail@mail.ru','5F4DCC3B5AA765D61D8327DEB882CF99','','mail','images/upload/avatars/106A180143EA5304898AA20610A0410B.jpg','2020-04-01 10:47:57'),(16,NULL,'some@mail.ru','5F4DCC3B5AA765D61D8327DEB882CF99','\0','Fedya',NULL,'2020-04-01 12:10:44'),(18,NULL,'new@mail.ru','5F4DCC3B5AA765D61D8327DEB882CF99','\0','Petya',NULL,'2020-04-01 12:30:34'),(22,NULL,'somenew@mail.ru','5F4DCC3B5AA765D61D8327DEB882CF99','\0','Sasha',NULL,'2020-04-01 12:58:56'),(49,NULL,'newsome@mail.ru','5F4DCC3B5AA765D61D8327DEB882CF99','\0','Gena',NULL,'2020-04-01 19:37:56'),(81,'7ou6e18rlyyfuit0shd3y5iusjqqc2ua8cdr1i7vs6k8o','surkovr@list.ru','5F4DCC3B5AA765D61D8327DEB882CF99','\0','Egor',NULL,'2020-04-03 16:10:19'),(85,NULL,'newone@mail.ru','5F4DCC3B5AA765D61D8327DEB882CF99','\0','Masha',NULL,'2020-04-03 17:40:11'),(86,NULL,'vasya@mail.ru','5F4DCC3B5AA765D61D8327DEB882CF99','\0','vasya',NULL,'2020-04-03 18:05:29');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'blogdb'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-04-03 19:18:28
