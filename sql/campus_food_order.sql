-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: campus_food_order
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `canteen`
--

DROP TABLE IF EXISTS `canteen`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `canteen` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(80) NOT NULL,
  `location` varchar(80) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `canteen`
--

LOCK TABLES `canteen` WRITE;
/*!40000 ALTER TABLE `canteen` DISABLE KEYS */;
INSERT INTO `canteen` VALUES (1,'第一食堂','学校北区','2026-09-14 21:55:04','2026-09-14 21:55:04');
/*!40000 ALTER TABLE `canteen` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_item`
--

DROP TABLE IF EXISTS `cart_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `dish_id` bigint NOT NULL,
  `quantity` int NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_cart_user_dish` (`user_id`,`dish_id`),
  KEY `fk_cart_dish` (`dish_id`),
  CONSTRAINT `fk_cart_dish` FOREIGN KEY (`dish_id`) REFERENCES `dish` (`id`),
  CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_item`
--

LOCK TABLES `cart_item` WRITE;
/*!40000 ALTER TABLE `cart_item` DISABLE KEYS */;
INSERT INTO `cart_item` VALUES (7,5,4,1,'2026-09-24 19:23:19','2026-09-24 19:23:19');
/*!40000 ALTER TABLE `cart_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `delivery_address`
--

DROP TABLE IF EXISTS `delivery_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery_address` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `contact_name` varchar(50) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `detail` varchar(200) NOT NULL,
  `is_default` tinyint NOT NULL DEFAULT '0',
  `status` tinyint NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_address_student_id` (`student_id`),
  CONSTRAINT `fk_address_student` FOREIGN KEY (`student_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery_address`
--

LOCK TABLES `delivery_address` WRITE;
/*!40000 ALTER TABLE `delivery_address` DISABLE KEYS */;
INSERT INTO `delivery_address` VALUES (1,5,'地址测试学生','13900000001','第一食堂门口',1,1,'2026-09-22 22:45:15','2026-09-22 22:45:15');
/*!40000 ALTER TABLE `delivery_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dish`
--

DROP TABLE IF EXISTS `dish`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dish` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `stall_id` bigint NOT NULL,
  `category_id` bigint NOT NULL,
  `name` varchar(80) NOT NULL,
  `price` decimal(10,2) NOT NULL,
  `stock` int NOT NULL DEFAULT '0',
  `description` varchar(500) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_dish_query` (`stall_id`,`category_id`,`status`),
  KEY `fk_dish_category` (`category_id`),
  CONSTRAINT `fk_dish_category` FOREIGN KEY (`category_id`) REFERENCES `dish_category` (`id`),
  CONSTRAINT `fk_dish_stall` FOREIGN KEY (`stall_id`) REFERENCES `stall` (`id`),
  CONSTRAINT `chk_dish_price` CHECK ((`price` > 0)),
  CONSTRAINT `chk_dish_stock` CHECK ((`stock` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dish`
--

LOCK TABLES `dish` WRITE;
/*!40000 ALTER TABLE `dish` DISABLE KEYS */;
INSERT INTO `dish` VALUES (2,1,1,'宫保鸡丁盖饭升级版',18.00,30,'经典盖饭套餐',NULL,0,'2026-09-14 22:09:40','2026-09-19 00:07:13'),(3,1,1,'鱼香肉丝盖饭',16.00,30,NULL,NULL,1,'2026-09-15 19:42:19','2026-09-15 19:42:19'),(4,2,2,'商家测试盖饭升级版',13.80,13,NULL,NULL,1,'2026-09-22 22:32:07','2026-09-24 18:48:15'),(5,2,2,'图文测试盖饭',12.80,10,'鸡肉、时蔬和米饭搭配的套餐。','https://example.com/images/test-rice.jpg',1,'2026-09-23 19:21:35','2026-09-24 18:48:15'),(6,2,3,'选择下单测试菜',10.00,19,'用于测试指定购物车项下单',NULL,1,'2026-09-24 19:22:51','2026-09-24 19:33:38');
/*!40000 ALTER TABLE `dish` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dish_category`
--

DROP TABLE IF EXISTS `dish_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `dish_category` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `stall_id` bigint NOT NULL,
  `name` varchar(50) NOT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_dish_category_stall_name` (`stall_id`,`name`),
  KEY `idx_dish_category_stall_id` (`stall_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dish_category`
--

LOCK TABLES `dish_category` WRITE;
/*!40000 ALTER TABLE `dish_category` DISABLE KEYS */;
INSERT INTO `dish_category` VALUES (1,1,'盖饭','2026-09-14 22:04:53','2026-09-24 18:48:04'),(2,2,'盖饭','2026-09-24 18:48:10','2026-09-24 18:48:10'),(3,2,'饮品','2026-09-24 19:01:27','2026-09-24 19:01:27');
/*!40000 ALTER TABLE `dish_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `food_order`
--

DROP TABLE IF EXISTS `food_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `food_order` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_id` bigint NOT NULL,
  `stall_id` bigint NOT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `pay_amount` decimal(10,2) NOT NULL DEFAULT '0.00',
  `status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `delivery_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address_id` bigint DEFAULT NULL,
  `remark` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `stall_id` (`stall_id`),
  KEY `address_id` (`address_id`),
  CONSTRAINT `food_order_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`),
  CONSTRAINT `food_order_ibfk_2` FOREIGN KEY (`stall_id`) REFERENCES `stall` (`id`),
  CONSTRAINT `food_order_ibfk_3` FOREIGN KEY (`address_id`) REFERENCES `delivery_address` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food_order`
--

LOCK TABLES `food_order` WRITE;
/*!40000 ALTER TABLE `food_order` DISABLE KEYS */;
INSERT INTO `food_order` VALUES (1,NULL,1,1,18.00,0.00,'ACCEPTED','PICKUP',NULL,NULL,'2026-09-15 20:00:11','2026-09-15 20:02:53'),(2,NULL,5,2,69.00,0.00,'CANCELLED','DELIVERY',1,NULL,'2026-09-22 22:57:20','2026-09-22 23:05:48'),(3,NULL,5,2,27.60,0.00,'COMPLETED','DELIVERY',1,NULL,'2026-09-22 23:00:37','2026-09-22 23:02:28'),(4,NULL,5,2,13.80,0.00,'COMPLETED','PICKUP',NULL,NULL,'2026-09-22 23:07:06','2026-09-22 23:08:38'),(5,NULL,5,2,13.80,0.00,'WAIT_ACCEPT','PICKUP',NULL,NULL,'2026-09-22 23:53:00','2026-09-22 23:56:33'),(6,'ORD85A28FFA92AB45EAA763',5,2,13.80,0.00,'CANCELLED','PICKUP',NULL,'不要香菜','2026-09-24 18:14:53','2026-09-24 18:15:16'),(7,'ORDA120BA61542648A09C34',5,2,13.80,13.80,'WAIT_ACCEPT','PICKUP',NULL,NULL,'2026-09-24 18:30:26','2026-09-24 18:30:45'),(8,'ORDCEE8E82CB6F5491296A9',5,2,10.00,10.00,'WAIT_ACCEPT','PICKUP',NULL,'只结算测试菜','2026-09-24 19:24:03','2026-09-24 19:33:38');
/*!40000 ALTER TABLE `food_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `dish_id` bigint NOT NULL,
  `quantity` int NOT NULL,
  `price` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  KEY `dish_id` (`dish_id`),
  CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `food_order` (`id`),
  CONSTRAINT `order_item_ibfk_2` FOREIGN KEY (`dish_id`) REFERENCES `dish` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
INSERT INTO `order_item` VALUES (1,1,2,2,15.00),(2,1,2,2,15.00),(3,2,4,5,13.80),(4,3,4,2,13.80),(5,4,4,1,13.80),(6,5,4,1,13.80),(7,6,4,1,13.80),(8,7,4,1,13.80),(9,8,6,1,10.00);
/*!40000 ALTER TABLE `order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_status_log`
--

DROP TABLE IF EXISTS `order_status_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_status_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `old_status` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `new_status` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `order_status_log_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `food_order` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_status_log`
--

LOCK TABLES `order_status_log` WRITE;
/*!40000 ALTER TABLE `order_status_log` DISABLE KEYS */;
INSERT INTO `order_status_log` VALUES (1,1,'ACCEPTED','ACCEPTED','2026-09-15 20:13:14'),(2,2,'CREATE','WAIT_PAY','2026-09-22 22:57:20'),(3,3,'CREATE','WAIT_PAY','2026-09-22 23:00:37'),(4,3,'WAIT_PAY','WAIT_ACCEPT','2026-09-22 23:01:09'),(5,3,'WAIT_ACCEPT','MAKING','2026-09-22 23:02:09'),(6,3,'MAKING','DELIVERING','2026-09-22 23:02:14'),(7,3,'DELIVERING','DELIVERED','2026-09-22 23:02:18'),(8,3,'DELIVERED','COMPLETED','2026-09-22 23:02:28'),(9,2,'WAIT_PAY','CANCELLED','2026-09-22 23:05:48'),(10,4,'CREATE','WAIT_PAY','2026-09-22 23:07:06'),(11,4,'WAIT_PAY','WAIT_ACCEPT','2026-09-22 23:07:28'),(12,4,'WAIT_ACCEPT','MAKING','2026-09-22 23:07:54'),(13,4,'MAKING','READY_PICKUP','2026-09-22 23:08:02'),(14,4,'READY_PICKUP','COMPLETED','2026-09-22 23:08:38'),(15,5,'CREATE','WAIT_PAY','2026-09-22 23:53:00'),(16,5,'WAIT_PAY','WAIT_ACCEPT','2026-09-22 23:56:33'),(17,6,'CREATE','WAIT_PAY','2026-09-24 18:14:53'),(18,6,'WAIT_PAY','CANCELLED','2026-09-24 18:15:16'),(19,7,'CREATE','WAIT_PAY','2026-09-24 18:30:26'),(20,7,'WAIT_PAY','WAIT_ACCEPT','2026-09-24 18:30:45'),(21,8,'CREATE','WAIT_PAY','2026-09-24 19:24:03'),(22,8,'WAIT_PAY','WAIT_ACCEPT','2026-09-24 19:33:38');
/*!40000 ALTER TABLE `order_status_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `payment_no` varchar(32) COLLATE utf8mb4_unicode_ci NOT NULL,
  `order_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `pay_amount` decimal(10,2) NOT NULL,
  `pay_method` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `pay_status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `paid_at` datetime NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_order` (`order_id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `fk_payment_user` (`user_id`),
  CONSTRAINT `fk_payment_order` FOREIGN KEY (`order_id`) REFERENCES `food_order` (`id`),
  CONSTRAINT `fk_payment_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
INSERT INTO `payment` VALUES (1,'PAY17900925931535',5,5,13.80,'MOCK','SUCCESS','2026-09-22 23:56:33','2026-09-22 23:56:33'),(2,'PAY17902458456297',7,5,13.80,'MOCK','SUCCESS','2026-09-24 18:30:45','2026-09-24 18:30:45'),(3,'PAY17902496187378',8,5,10.00,'MOCK','SUCCESS','2026-09-24 19:33:38','2026-09-24 19:33:38');
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `review`
--

DROP TABLE IF EXISTS `review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `review` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `rating` tinyint NOT NULL,
  `content` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_review_order` (`order_id`),
  KEY `fk_review_user` (`user_id`),
  CONSTRAINT `fk_review_order` FOREIGN KEY (`order_id`) REFERENCES `food_order` (`id`),
  CONSTRAINT `fk_review_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
INSERT INTO `review` VALUES (1,3,5,5,'菜品味道很好，配送很及时。',1,'2026-09-22 23:13:15','2026-09-22 23:13:15');
/*!40000 ALTER TABLE `review` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stall`
--

DROP TABLE IF EXISTS `stall`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stall` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `canteen_id` bigint NOT NULL,
  `merchant_id` bigint NOT NULL,
  `name` varchar(80) NOT NULL,
  `business_hours` varchar(100) DEFAULT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_stall_canteen_id` (`canteen_id`),
  KEY `idx_stall_merchant_id` (`merchant_id`),
  CONSTRAINT `fk_stall_canteen` FOREIGN KEY (`canteen_id`) REFERENCES `canteen` (`id`),
  CONSTRAINT `fk_stall_merchant` FOREIGN KEY (`merchant_id`) REFERENCES `sys_user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stall`
--

LOCK TABLES `stall` WRITE;
/*!40000 ALTER TABLE `stall` DISABLE KEYS */;
INSERT INTO `stall` VALUES (1,1,2,'一楼快餐档口','08:00-20:00',1,'2026-09-14 22:01:26','2026-09-22 22:30:31'),(2,1,4,'商家测试档口','07:00-21:00',1,'2026-09-22 22:23:19','2026-09-23 19:42:37');
/*!40000 ALTER TABLE `stall` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(32) NOT NULL,
  `password_hash` varchar(100) NOT NULL,
  `real_name` varchar(50) NOT NULL,
  `phone` varchar(20) NOT NULL,
  `role_code` varchar(20) NOT NULL,
  `status` tinyint NOT NULL DEFAULT '1',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'test001','$2a$10$X1ufsUkP1BYV9wpDm8DfKuq3DAE6KP14Q.Tmv6t8x4uqkv/4I6pBa','测试用户','13800000001','STUDENT',1,'2026-09-13 21:37:53','2026-09-13 21:37:53'),(2,'merchant001','$2a$10$FcGOUtWF5fmC.8/v4OtG9urjhvFaIOzZnid2PKUL2PpeZzQ6Y339G','张老板','13800000002','MERCHANT',1,'2026-09-14 21:59:30','2026-09-14 21:59:30'),(3,'student1','$2a$10$UKzBxODR39UkvQ7aDZbcIut60zq17zCeKIjOqDqat.xCqs8be.uS2','学生','11111111111','STUDENT',1,'2026-09-19 00:20:06','2026-09-19 00:20:06'),(4,'merchant_test','$2a$10$YVVoC.Q/Drzoo7driVgDhujec6uAeUeOKTGj5XvQz68ZppeZm7F3m','测试商家','33333333333','MERCHANT',1,'2026-09-19 00:27:57','2026-09-19 00:27:57'),(5,'address_student_0922','$2a$10$duzt15UAH4LRgDPQDn/8GO9yVqGjqfBT7t3cHY5T0x9NlU3PmeGzy','测试学生已修改','13900009220','STUDENT',1,'2026-09-22 22:43:07','2026-09-24 17:02:31');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping events for database 'campus_food_order'
--

--
-- Dumping routines for database 'campus_food_order'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-24 20:33:56
