-- MariaDB dump 10.19  Distrib 10.4.32-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: aurafarmer_local
-- ------------------------------------------------------
-- Server version	10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `arena_matches`
--

DROP TABLE IF EXISTS `arena_matches`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `arena_matches` (
  `match_id` int(11) NOT NULL AUTO_INCREMENT,
  `player1_id` int(11) NOT NULL,
  `player2_id` int(11) DEFAULT NULL,
  `player1_score` int(11) DEFAULT NULL,
  `player2_score` int(11) DEFAULT NULL,
  `status` enum('PENDING','PLAYER1_TURN','PLAYER2_TURN','COMPLETED','DISBANDED') NOT NULL,
  `challenge_type` varchar(100) NOT NULL,
  `time_limit_minutes` int(11) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `last_active_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `winner_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`match_id`),
  KEY `player1_id` (`player1_id`),
  KEY `player2_id` (`player2_id`),
  KEY `winner_id` (`winner_id`),
  CONSTRAINT `arena_matches_ibfk_1` FOREIGN KEY (`player1_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `arena_matches_ibfk_2` FOREIGN KEY (`player2_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `arena_matches_ibfk_3` FOREIGN KEY (`winner_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `arena_matches`
--

LOCK TABLES `arena_matches` WRITE;
/*!40000 ALTER TABLE `arena_matches` DISABLE KEYS */;
INSERT INTO `arena_matches` VALUES (1,1,2,0,0,'COMPLETED','AuraRush',10,'2025-08-21 04:15:59','2025-08-21 04:32:00',NULL),(2,1,NULL,NULL,NULL,'PENDING','AuraRush',10,'2025-08-21 07:44:06','2025-08-21 07:44:06',NULL);
/*!40000 ALTER TABLE `arena_matches` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `command_cooldowns`
--

DROP TABLE IF EXISTS `command_cooldowns`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `command_cooldowns` (
  `user_id` int(11) NOT NULL,
  `command_name` varchar(50) NOT NULL,
  `expires_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`user_id`,`command_name`),
  CONSTRAINT `command_cooldowns_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `command_cooldowns`
--

LOCK TABLES `command_cooldowns` WRITE;
/*!40000 ALTER TABLE `command_cooldowns` DISABLE KEYS */;
INSERT INTO `command_cooldowns` VALUES (1,'beg','2025-08-23 02:12:45'),(1,'clean','2025-08-21 07:47:59'),(1,'coinflip','2025-08-23 02:22:01'),(1,'crime','2025-08-21 07:43:26'),(1,'dig','2025-08-23 05:14:08'),(1,'fish','2025-08-21 07:46:45'),(1,'hunt','2025-08-23 02:12:55'),(1,'mines','2025-08-23 10:27:26'),(1,'search','2025-08-23 05:14:39'),(1,'twinroll','2025-08-23 02:19:59'),(1,'work','2025-08-21 08:28:35'),(3,'dig','2025-08-21 08:50:59'),(3,'mines','2025-08-21 08:57:54'),(3,'twinroll','2025-08-21 08:53:33'),(3,'work','2025-08-21 09:39:47'),(4,'coinflip','2025-08-21 09:10:35'),(4,'dig','2025-08-21 09:08:05'),(4,'mines','2025-08-21 09:12:13'),(4,'search','2025-08-21 09:09:06'),(4,'twinroll','2025-08-21 09:10:49'),(6,'dig','2025-08-22 17:42:24'),(8,'beg','2025-08-23 08:02:37'),(8,'mines','2025-08-23 07:58:47'),(8,'work','2025-08-23 08:41:16'),(9,'mines','2025-08-23 08:40:02'),(10,'coinflip','2025-08-23 09:13:16'),(10,'mines','2025-08-23 09:12:51'),(10,'twinroll','2025-08-23 09:12:53');
/*!40000 ALTER TABLE `command_cooldowns` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `items`
--

DROP TABLE IF EXISTS `items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `items` (
  `item_id` int(11) NOT NULL AUTO_INCREMENT,
  `item_name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `item_type` varchar(50) DEFAULT 'collectible',
  `buy_price` int(10) unsigned DEFAULT NULL,
  `sell_price` int(10) unsigned DEFAULT NULL,
  `uses_per_item` int(10) unsigned DEFAULT NULL,
  `max_quantity` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`item_id`),
  UNIQUE KEY `item_name` (`item_name`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items`
--

LOCK TABLES `items` WRITE;
/*!40000 ALTER TABLE `items` DISABLE KEYS */;
INSERT INTO `items` VALUES (1,'Rusty Key','An old, rusty key. Might open something?','collectible',NULL,2000,NULL,5),(2,'Rabbit Foot','A lucky rabbit foot. Maybe it brings good fortune.','collectible',NULL,2400,NULL,5),(3,'Strange Fossil','A fossil of a creature you don\'t recognize.','collectible',NULL,1900,NULL,5),(4,'Shiny Rock','A common rock, but it looks pretty.','collectible',NULL,2500,NULL,5),(5,'Shovel','A sturdy shovel. Might improve your digging results.','collectible',10000,5000,5,5),(6,'Rifle','A hunting rifle. Used for a successful hunt.','collectible',15000,7000,5,5),(7,'Mask','A mysterious mask that seems to have a stran...','collectible',10000,5000,1,2),(8,'Fishing Rod','A sturdy rod for catching fish.','collectible',10000,5000,5,5),(9,'Common Fish','A small, ordinary fish.','collectible',NULL,2000,NULL,5),(10,'Rare Fish','A shimmering, elusive fish.','collectible',NULL,4000,NULL,5),(11,'Legendary Fish','A mythical fish, worth a fortune!','collectible',NULL,5000,NULL,5);
/*!40000 ALTER TABLE `items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jobs`
--

DROP TABLE IF EXISTS `jobs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jobs` (
  `job_id` int(11) NOT NULL AUTO_INCREMENT,
  `job_name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `salary_per_shift` int(10) unsigned NOT NULL,
  `required_shifts` int(10) unsigned NOT NULL DEFAULT 0,
  `cooldown_minutes` int(10) unsigned NOT NULL DEFAULT 120,
  PRIMARY KEY (`job_id`),
  UNIQUE KEY `job_name` (`job_name`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jobs`
--

LOCK TABLES `jobs` WRITE;
/*!40000 ALTER TABLE `jobs` DISABLE KEYS */;
INSERT INTO `jobs` VALUES (1,'Babysitter','Watches over toddlers. Surprisingly tiring.',40000,0,40),(2,'Streamer','Plays games online for an audience.',45000,10,40),(3,'Influencer','Creates content and promotes products online.',48000,10,40),(4,'Professor','Educates students at a university level.',52000,20,40),(5,'Lawyer','Provides legal advice and representation.',55000,25,40),(6,'Developer','Writes and maintains software programs.',60000,25,40),(7,'Cops','Maintains law and order in the community.',70000,30,40);
/*!40000 ALTER TABLE `jobs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_inventory`
--

DROP TABLE IF EXISTS `user_inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_inventory` (
  `user_id` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `quantity` int(10) unsigned NOT NULL DEFAULT 1,
  `uses_left` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`user_id`,`item_id`),
  KEY `item_id` (`item_id`),
  CONSTRAINT `user_inventory_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `user_inventory_ibfk_2` FOREIGN KEY (`item_id`) REFERENCES `items` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_inventory`
--

LOCK TABLES `user_inventory` WRITE;
/*!40000 ALTER TABLE `user_inventory` DISABLE KEYS */;
INSERT INTO `user_inventory` VALUES (1,1,1,NULL),(1,6,1,4),(1,8,1,3),(1,9,1,NULL),(1,10,1,NULL),(3,5,1,4),(4,5,1,4),(6,5,1,4);
/*!40000 ALTER TABLE `user_inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_jobs`
--

DROP TABLE IF EXISTS `user_jobs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_jobs` (
  `user_id` int(11) NOT NULL,
  `job_id` int(11) NOT NULL,
  `hire_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `shifts_worked` int(10) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`user_id`),
  KEY `job_id` (`job_id`),
  CONSTRAINT `user_jobs_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `user_jobs_ibfk_2` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_jobs`
--

LOCK TABLES `user_jobs` WRITE;
/*!40000 ALTER TABLE `user_jobs` DISABLE KEYS */;
INSERT INTO `user_jobs` VALUES (1,1,'2025-08-21 07:47:52',1),(3,1,'2025-08-21 08:59:29',1),(8,1,'2025-08-23 08:01:02',1);
/*!40000 ALTER TABLE `user_jobs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `password_hash` varchar(64) NOT NULL,
  `aura_balance` int(10) unsigned NOT NULL DEFAULT 25000,
  `total_shifts_worked` int(10) unsigned NOT NULL DEFAULT 0,
  `passive_mode` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'meet','03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4',0,1,1,'2025-08-20 20:35:23'),(2,'kratos','03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4',25000,0,0,'2025-08-21 04:17:16'),(3,'user1','03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4',21892,1,0,'2025-08-21 08:49:49'),(4,'dakahs','d06bb3b9185e9482d738334a74bfe1598ce35cc246339574ccd891eeb2d8fd73',0,0,0,'2025-08-21 09:06:38'),(6,'tity','fe2592b42a727e977f055947385b709cc82b16b9a87f88c6abf3900d65d0cdc3',15000,0,0,'2025-08-22 17:41:30'),(7,'opop','03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4',25000,0,0,'2025-08-23 05:01:09'),(8,'neel','a754049ffb01baaea795203c823895120373619b79ac87fee9351ad3dea41064',37056,1,0,'2025-08-23 07:55:13'),(9,'pappu','c082136baa4389929f58595f91d2c439d785f3cfc3ffb4d29e8fd65a0e9dd470',20000,0,0,'2025-08-23 08:38:05'),(10,'krish','ef060aa43f32830e2a39a1840f820b093bc7d55392852fb50640657e5729a259',0,0,0,'2025-08-23 09:10:46');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`root`@`localhost`*/ /*!50003 TRIGGER before_user_insert
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
    SET NEW.password_hash = SHA2(NEW.password_hash, 256);
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Dumping routines for database 'aurafarmer_local'
--
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
/*!50003 DROP PROCEDURE IF EXISTS `get_user_by_username` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `get_user_by_username`(
    IN p_username VARCHAR(255)
)
BEGIN
    SELECT user_id, username, password_hash, aura_balance, total_shifts_worked, passive_mode, created_at
    FROM users
    WHERE username = p_username;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
/*!50003 DROP PROCEDURE IF EXISTS `register_user` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `register_user`(
    IN p_username VARCHAR(255),
    IN p_password VARCHAR(255)
)
BEGIN
    INSERT INTO users (username, password_hash) VALUES (p_username, p_password);
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_ZERO_IN_DATE,NO_ZERO_DATE,NO_ENGINE_SUBSTITUTION' */ ;
/*!50003 DROP PROCEDURE IF EXISTS `set_user_job` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_unicode_ci */ ;
DELIMITER ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `set_user_job`(
    IN p_user_id INT,
    IN p_job_id INT
)
BEGIN
    INSERT INTO user_jobs (user_id, job_id, shifts_worked)
    VALUES (p_user_id, p_job_id, 0)
    ON DUPLICATE KEY UPDATE
        job_id = VALUES(job_id),
        shifts_worked = 0;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-24 17:19:28
