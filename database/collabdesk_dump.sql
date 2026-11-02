CREATE DATABASE  IF NOT EXISTS `collabdesk` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `collabdesk`;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: collabdesk
-- ------------------------------------------------------
-- Server version	9.7.2

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;



--
-- Table structure for table `brand_profiles`
--

DROP TABLE IF EXISTS `brand_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `brand_profiles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `company_name` varchar(100) NOT NULL,
  `industry` varchar(60) DEFAULT NULL,
  `website` varchar(200) DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKia41rgy64n1kt462fhjhvfmw6` (`user_id`),
  CONSTRAINT `FKsj9au0kfjkqbmlqja9q47nilo` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `brand_profiles`
--

LOCK TABLES `brand_profiles` WRITE;
/*!40000 ALTER TABLE `brand_profiles` DISABLE KEYS */;
INSERT INTO `brand_profiles` VALUES (1,'Bobo Bubble Tea','F&B','https://example.com/bobo',5),(2,'TechGear Asia','Consumer electronics','https://example.com/techgear',6);
/*!40000 ALTER TABLE `brand_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creator_profiles`
--

DROP TABLE IF EXISTS `creator_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creator_profiles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `base_rate` decimal(10,2) DEFAULT NULL,
  `bio` varchar(500) DEFAULT NULL,
  `follower_count` int DEFAULT NULL,
  `handle` varchar(60) NOT NULL,
  `niche` varchar(60) DEFAULT NULL,
  `platform` enum('INSTAGRAM','TIKTOK','XIAOHONGSHU','YOUTUBE') NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKgsos4mp0tlrht38c1wn24ehn6` (`user_id`),
  CONSTRAINT `FK48jx3726hqfmcyksfm6rysgw1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creator_profiles`
--

LOCK TABLES `creator_profiles` WRITE;
/*!40000 ALTER TABLE `creator_profiles` DISABLE KEYS */;
INSERT INTO `creator_profiles` VALUES (1,450.00,'Weekly cafe hopping and hawker food reviews in Singapore.',48000,'@amyeats.sg','Food & cafe','TIKTOK',2),(2,1200.00,'Honest gadget reviews for students on a budget.',126000,'KenTechTalk','Tech reviews','YOUTUBE',3),(3,200.00,'Study-abroad vlogs, dorm life and NTU campus tips.',9500,'梅子的留学日记','Student life','XIAOHONGSHU',4);
/*!40000 ALTER TABLE `creator_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `deal_status_history`
--

DROP TABLE IF EXISTS `deal_status_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deal_status_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `changed_at` datetime(6) NOT NULL,
  `from_status` enum('ACCEPTED','CANCELLED','COUNTERED','PAID','PROPOSED','PUBLISHED','REJECTED') DEFAULT NULL,
  `note` varchar(500) DEFAULT NULL,
  `to_status` enum('ACCEPTED','CANCELLED','COUNTERED','PAID','PROPOSED','PUBLISHED','REJECTED') NOT NULL,
  `changed_by` bigint DEFAULT NULL,
  `deal_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK4e7gqtntihake0xjrnls6gkkg` (`changed_by`),
  KEY `FKgcmdo4lohlnlmq3vtow0fk583` (`deal_id`),
  CONSTRAINT `FK4e7gqtntihake0xjrnls6gkkg` FOREIGN KEY (`changed_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKgcmdo4lohlnlmq3vtow0fk583` FOREIGN KEY (`deal_id`) REFERENCES `deals` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deal_status_history`
--

LOCK TABLES `deal_status_history` WRITE;
/*!40000 ALTER TABLE `deal_status_history` DISABLE KEYS */;
INSERT INTO `deal_status_history` VALUES (1,'2026-10-23 09:24:12.377753',NULL,'Proposal sent','PROPOSED',5,1),(2,'2026-10-23 09:24:12.377753',NULL,'Proposal sent','PROPOSED',6,2),(3,'2026-10-23 09:24:12.377753','PROPOSED','Counter-offer 480 - recipe videos need extra filming time','COUNTERED',2,2),(4,'2026-10-23 09:24:12.377753',NULL,'Proposal sent','PROPOSED',5,3),(5,'2026-10-23 09:24:12.377753','PROPOSED','Agreed at 900','ACCEPTED',3,3),(6,'2026-10-23 09:24:12.377753',NULL,'Proposal sent','PROPOSED',6,4),(7,'2026-10-23 09:24:12.377753','PROPOSED','Agreed at 1500','ACCEPTED',3,4),(8,'2026-10-23 09:24:12.377753','ACCEPTED','Published: https://youtube.com/watch?v=demo-earbuds','PUBLISHED',3,4),(9,'2026-10-23 09:24:12.377753',NULL,'Proposal sent','PROPOSED',5,5),(10,'2026-10-23 09:24:12.377753','PUBLISHED','Payment confirmed: 380','PAID',5,5),(11,'2026-10-23 09:24:12.377753',NULL,'Proposal sent','PROPOSED',6,6),(12,'2026-10-23 09:24:12.377753','PROPOSED','Agreed at 600','ACCEPTED',2,6),(13,'2026-10-23 09:25:52.635652',NULL,'Proposal sent','PROPOSED',5,7);
/*!40000 ALTER TABLE `deal_status_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `deals`
--

DROP TABLE IF EXISTS `deals`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deals` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `agreed_amount` decimal(10,2) DEFAULT NULL,
  `brief` varchar(2000) DEFAULT NULL,
  `budget` decimal(10,2) NOT NULL,
  `counter_amount` decimal(10,2) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `due_date` date NOT NULL,
  `max_revisions` int NOT NULL,
  `payment_due_date` date DEFAULT NULL,
  `payment_overdue` bit(1) NOT NULL,
  `payment_term_days` int NOT NULL,
  `published_date` date DEFAULT NULL,
  `published_url` varchar(300) DEFAULT NULL,
  `status` enum('ACCEPTED','CANCELLED','COUNTERED','PAID','PROPOSED','PUBLISHED','REJECTED') NOT NULL,
  `title` varchar(120) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `brand_id` bigint NOT NULL,
  `creator_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgip3i3ybe68dbjt0esuwcaxfr` (`brand_id`),
  KEY `FK7i0sorvf3egbfbq98fya571t3` (`creator_id`),
  CONSTRAINT `FK7i0sorvf3egbfbq98fya571t3` FOREIGN KEY (`creator_id`) REFERENCES `creator_profiles` (`id`),
  CONSTRAINT `FKgip3i3ybe68dbjt0esuwcaxfr` FOREIGN KEY (`brand_id`) REFERENCES `brand_profiles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deals`
--

LOCK TABLES `deals` WRITE;
/*!40000 ALTER TABLE `deals` DISABLE KEYS */;
INSERT INTO `deals` VALUES (1,NULL,'One 30-60s TikTok tasting video at our Jurong outlet.',400.00,NULL,'2026-10-23 09:24:12.377753','2026-11-13',2,NULL,_binary '\0',30,NULL,NULL,'PROPOSED','New brown-sugar series launch','2026-10-23 09:24:12.377753',1,1),(2,NULL,'Show the blender in a smoothie recipe video.',300.00,480.00,'2026-10-23 09:24:12.377753','2026-11-22',2,NULL,_binary '\0',30,NULL,NULL,'COUNTERED','Portable blender review','2026-10-23 09:24:12.377753',2,1),(3,900.00,'Short segment in a desk setup video.',900.00,NULL,'2026-10-23 09:24:12.377753','2026-11-06',2,NULL,_binary '\0',30,NULL,NULL,'ACCEPTED','Bubble tea x desk setup crossover','2026-10-23 09:24:12.377753',1,2),(4,1500.00,'Dedicated 8-10 min YouTube review.',1500.00,NULL,'2026-10-23 09:24:12.377753','2026-09-11',2,'2026-10-13',_binary '\0',30,'2026-09-13','https://youtube.com/watch?v=demo-earbuds','PUBLISHED','Noise-cancelling earbuds review','2026-10-23 09:24:12.377753',2,2),(5,380.00,'Festive TikTok, 1 video.',380.00,NULL,'2026-10-23 09:24:12.377753','2026-09-28',2,'2026-10-27',_binary '\0',30,'2026-09-27','https://tiktok.com/@amyeats.sg/video/demo','PAID','Mid-autumn mooncake tea','2026-10-23 09:24:12.377753',1,1),(6,600.00,'2 recipes using our air fryer.',600.00,NULL,'2026-10-23 09:24:12.377753','2026-10-27',2,NULL,_binary '\0',30,NULL,NULL,'ACCEPTED','Air fryer recipe series','2026-10-23 09:24:12.377753',2,1),(7,NULL,'',301.00,NULL,'2026-10-23 09:25:52.635652','2026-10-31',2,NULL,_binary '\0',30,NULL,NULL,'PROPOSED','MySQL Test','2026-10-23 09:25:52.635652',1,2);
/*!40000 ALTER TABLE `deals` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `deliverables`
--

DROP TABLE IF EXISTS `deliverables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deliverables` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `approved_at` datetime(6) DEFAULT NULL,
  `content` varchar(4000) DEFAULT NULL,
  `due_date` date NOT NULL,
  `feedback` varchar(1000) DEFAULT NULL,
  `revision_count` int NOT NULL,
  `status` enum('APPROVED','OVERDUE','PENDING','REVISION_REQUESTED','SUBMITTED') NOT NULL,
  `submitted_at` datetime(6) DEFAULT NULL,
  `type` enum('DRAFT_VIDEO','SCRIPT') NOT NULL,
  `deal_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKdt6xa9ynqc24yjsx3y9yku2gt` (`deal_id`),
  CONSTRAINT `FKdt6xa9ynqc24yjsx3y9yku2gt` FOREIGN KEY (`deal_id`) REFERENCES `deals` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deliverables`
--

LOCK TABLES `deliverables` WRITE;
/*!40000 ALTER TABLE `deliverables` DISABLE KEYS */;
INSERT INTO `deliverables` VALUES (1,NULL,'Intro: desk tour... (0:08) \'Use code KEN1FOR1 for 1-for-1 at Bobo!\' ...','2026-11-01','Please mention the 1-for-1 promo code in the first 10 seconds.',1,'SUBMITTED','2026-10-22 20:15:00.000000','SCRIPT',3),(2,NULL,'Full review script v2','2026-09-06',NULL,0,'APPROVED',NULL,'SCRIPT',4),(3,NULL,NULL,'2026-10-22',NULL,0,'PENDING',NULL,'SCRIPT',6);
/*!40000 ALTER TABLE `deliverables` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `display_name` varchar(100) NOT NULL,
  `email` varchar(120) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` enum('ADMIN','BRAND','CREATOR') NOT NULL,
  `username` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2026-10-23 09:24:11.844022','Platform Admin','admin@collabdesk.test',_binary '','$2a$10$/lpucXA5zXVrxhD/C9eAjOuzW9WrGsxv1ROL37kFeN.z8KaQlb8XO','ADMIN','admin'),(2,'2026-10-23 09:24:11.972086','Amy Tan','creator_amy@collabdesk.test',_binary '','$2a$10$4ovyaH2imA.K7TjTla2qlueIPyn7.B3fSLnot3PXKpkMwBycHNBoS','CREATOR','creator_amy'),(3,'2026-10-23 09:24:12.089890','Ken Lim','creator_ken@collabdesk.test',_binary '','$2a$10$rFTe7t39MSiuuPpS7xhoJOu0PLCsECJV82AnssEVHEr5TGOlRxL6m','CREATOR','creator_ken'),(4,'2026-10-23 09:24:12.195905','Mei Chen','creator_mei@collabdesk.test',_binary '','$2a$10$88kqOOUbtdqw8x6UBDmfeePJAp./pqzJ1hGoC0CLZ961c3avTt8hu','CREATOR','creator_mei'),(5,'2026-10-23 09:24:12.275908','Lily (Marketing)','brand_bubbletea@collabdesk.test',_binary '','$2a$10$odpE.ewI/8luISSFincpueJTlWzZj/3.iWJECm3atpe3hXACARCxa','BRAND','brand_bubbletea'),(6,'2026-10-23 09:24:12.371913','Raj (Partnerships)','brand_techgear@collabdesk.test',_binary '','$2a$10$kI.AXXojknkI7l78U2lB0O0dYrPRBUBX5MCeHr5LhFcFoP2es4uyG','BRAND','brand_techgear');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-02 13:15:11
