CREATE DATABASE  IF NOT EXISTS `campus_food_order` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `campus_food_order`;
-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: campus_food_order
-- ------------------------------------------------------
-- Server version	8.0.46

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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `canteen`
--

LOCK TABLES `canteen` WRITE;
/*!40000 ALTER TABLE `canteen` DISABLE KEYS */;
INSERT INTO `canteen` VALUES (1,'第一食堂','学校北区','2026-10-12 21:45:27','2026-10-12 21:45:27'),(2,'第二食堂','学校南区','2026-10-12 21:45:27','2026-10-12 21:45:27');
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_item`
--

LOCK TABLES `cart_item` WRITE;
/*!40000 ALTER TABLE `cart_item` DISABLE KEYS */;
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
INSERT INTO `delivery_address` VALUES (1,1,'困困','15383452536','32舍一层',1,1,'2026-10-12 21:58:54','2026-10-12 21:58:54');
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
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dish`
--

LOCK TABLES `dish` WRITE;
/*!40000 ALTER TABLE `dish` DISABLE KEYS */;
INSERT INTO `dish` VALUES (1,1,20,'豆乳玉麒麟',14.00,100,'茶百道饮品',NULL,1,'2026-10-12 21:49:43','2026-10-12 21:49:43'),(2,1,20,'豆乳米麻薯',16.00,100,'茶百道饮品',NULL,1,'2026-10-12 21:49:43','2026-10-12 21:49:43'),(3,1,20,'冰鲜柠檬水',4.00,100,'茶百道饮品',NULL,1,'2026-10-12 21:49:43','2026-10-12 21:49:43'),(4,1,20,'杨枝甘露',8.00,100,'茶百道饮品',NULL,1,'2026-10-12 21:49:43','2026-10-12 21:49:43'),(5,1,20,'芋圆葡萄',12.00,100,'茶百道饮品',NULL,1,'2026-10-12 21:49:43','2026-10-12 21:49:43'),(6,1,20,'黑糖珍珠奶茶',10.00,100,'茶百道饮品',NULL,1,'2026-10-12 21:49:43','2026-10-12 21:49:43'),(8,4,12,'薯条',15.00,100,'麦当劳餐品',NULL,1,'2026-10-12 21:53:29','2026-10-12 21:53:29'),(9,4,12,'蛋挞',8.00,100,'麦当劳餐品',NULL,1,'2026-10-12 21:53:29','2026-10-12 21:53:29'),(10,4,22,'汉堡',16.00,100,'麦当劳餐品',NULL,1,'2026-10-12 21:53:29','2026-10-12 21:53:29'),(11,4,22,'鸡肉卷',15.00,100,'麦当劳餐品',NULL,1,'2026-10-12 21:53:29','2026-10-12 21:53:29'),(12,4,17,'豆浆',4.00,100,'麦当劳餐品',NULL,1,'2026-10-12 21:53:29','2026-10-12 21:53:29'),(13,4,17,'可乐',5.00,100,'麦当劳餐品',NULL,1,'2026-10-12 21:53:29','2026-10-12 21:53:29'),(15,2,3,'鱼香肉丝盖饭',15.00,100,'炒菜馆菜品',NULL,1,'2026-10-12 22:01:21','2026-10-12 22:01:21'),(16,2,3,'宫保鸡丁盖饭',15.00,100,'炒菜馆菜品',NULL,1,'2026-10-12 22:01:21','2026-10-12 22:01:21'),(17,2,3,'土豆牛肉盖饭',15.00,100,'炒菜馆菜品',NULL,1,'2026-10-12 22:01:21','2026-10-12 22:01:21'),(18,2,8,'火腿鸡蛋炒面',17.00,100,'炒菜馆菜品',NULL,1,'2026-10-12 22:01:21','2026-10-12 22:01:21'),(19,2,8,'番茄鸡蛋汤面',16.00,100,'炒菜馆菜品',NULL,1,'2026-10-12 22:01:21','2026-10-12 22:01:21'),(20,2,18,'红茶',3.00,100,'炒菜馆菜品',NULL,1,'2026-10-12 22:01:21','2026-10-12 22:01:21'),(21,2,18,'绿茶',3.00,100,'炒菜馆菜品',NULL,1,'2026-10-12 22:01:21','2026-10-12 22:01:21'),(22,2,18,'牛奶',3.00,100,'炒菜馆菜品',NULL,1,'2026-10-12 22:01:21','2026-10-12 22:01:21'),(30,5,6,'兰州拉面',12.00,100,'清真馆菜品',NULL,1,'2026-10-12 22:01:21','2026-10-12 22:01:21'),(31,5,1,'炒饭',10.00,100,'清真馆菜品',NULL,1,'2026-10-12 22:01:21','2026-10-12 22:01:21'),(33,3,24,'麻辣烫',20.00,100,'张师傅麻辣烫',NULL,1,'2026-10-12 22:01:21','2026-10-12 22:01:21');
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
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dish_category`
--

LOCK TABLES `dish_category` WRITE;
/*!40000 ALTER TABLE `dish_category` DISABLE KEYS */;
INSERT INTO `dish_category` VALUES (1,5,'米饭','2026-10-12 21:45:27','2026-10-12 21:45:27'),(2,4,'米饭','2026-10-12 21:45:27','2026-10-12 21:45:27'),(3,2,'米饭','2026-10-12 21:45:27','2026-10-12 21:45:27'),(4,3,'米饭','2026-10-12 21:45:27','2026-10-12 21:45:27'),(6,5,'面条','2026-10-12 21:45:27','2026-10-12 21:45:27'),(7,4,'面条','2026-10-12 21:45:27','2026-10-12 21:45:27'),(8,2,'面条','2026-10-12 21:45:27','2026-10-12 21:45:27'),(9,3,'面条','2026-10-12 21:45:27','2026-10-12 21:45:27'),(11,5,'小吃','2026-10-12 21:45:27','2026-10-12 21:45:27'),(12,4,'小吃','2026-10-12 21:45:27','2026-10-12 21:45:27'),(13,2,'小吃','2026-10-12 21:45:27','2026-10-12 21:45:27'),(14,3,'小吃','2026-10-12 21:45:27','2026-10-12 21:45:27'),(16,5,'饮品','2026-10-12 21:45:27','2026-10-12 21:45:27'),(17,4,'饮品','2026-10-12 21:45:27','2026-10-12 21:45:27'),(18,2,'饮品','2026-10-12 21:45:27','2026-10-12 21:45:27'),(19,3,'饮品','2026-10-12 21:45:27','2026-10-12 21:45:27'),(20,1,'饮品','2026-10-12 21:45:27','2026-10-12 21:45:27'),(21,5,'快餐','2026-10-12 21:45:27','2026-10-12 21:45:27'),(22,4,'快餐','2026-10-12 21:45:27','2026-10-12 21:45:27'),(23,2,'快餐','2026-10-12 21:45:27','2026-10-12 21:45:27'),(24,3,'快餐','2026-10-12 21:45:27','2026-10-12 21:45:27');
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `food_order`
--

LOCK TABLES `food_order` WRITE;
/*!40000 ALTER TABLE `food_order` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_item`
--

LOCK TABLES `order_item` WRITE;
/*!40000 ALTER TABLE `order_item` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_status_log`
--

LOCK TABLES `order_status_log` WRITE;
/*!40000 ALTER TABLE `order_status_log` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `review`
--

LOCK TABLES `review` WRITE;
/*!40000 ALTER TABLE `review` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stall`
--

LOCK TABLES `stall` WRITE;
/*!40000 ALTER TABLE `stall` DISABLE KEYS */;
INSERT INTO `stall` VALUES (1,1,3,'茶百道','09:00-22:00',1,'2026-10-12 21:45:27','2026-10-12 21:45:27'),(2,1,5,'炒菜馆','10:00-21:00',1,'2026-10-12 21:45:27','2026-10-12 21:45:27'),(3,2,4,'张师傅麻辣烫','10:00-22:00',1,'2026-10-12 21:45:27','2026-10-12 21:45:27'),(4,2,6,'麦当劳','07:00-23:00',1,'2026-10-12 21:45:27','2026-10-12 21:45:27'),(5,2,7,'清真馆','08:00-21:00',1,'2026-10-12 21:45:27','2026-10-12 21:45:27');
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
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,'1','$2a$10$B1W7.mSDVlqjkXtuehis9uemWdQlV3sxGK2h9lTXyKEdnSIxMxCaK','困困','15383452536','STUDENT',1,'2026-10-12 21:24:02','2026-10-12 21:24:02'),(3,'茶百道','$2a$10$H4suuuzGFctqWIeyed9w/eD5GtBjrnNFmw6c.nt7Te4ok7qKdZ4Ia','茶百道','10101010101','MERCHANT',1,'2026-10-12 21:26:10','2026-10-12 21:26:10'),(4,'张师傅麻辣烫','$2a$10$fUSDazUgnw/z3xHcrp8fve7iRbJibKhqUJizT9bj.DbcGXyZy9Oj2','张师傅麻辣烫','12121212121','MERCHANT',1,'2026-10-12 21:26:50','2026-10-12 21:26:50'),(5,'炒菜馆','$2a$10$SsY8UG3Q9./D8klbSCcDcuoHhJmTAULytI6X29qnejIrtvyYFgQbu','炒菜馆','12121212123','MERCHANT',1,'2026-10-12 21:27:13','2026-10-12 21:27:13'),(6,'麦当劳','$2a$10$ADcxGnTsuAuF8MEUNFmZGuvTFMelcEL0xQKy5b8jx2BJJs9THSFfO','麦当劳','12121212124','MERCHANT',1,'2026-10-12 21:27:30','2026-10-12 21:27:30'),(7,'清真馆','$2a$10$cODM6DnVkZXLUeMC38D3W.1UgLRR9VocExOrxe5mkh2aB/2hcHSDi','清真馆','12121212125','MERCHANT',1,'2026-10-12 21:29:18','2026-10-12 21:29:18');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-12 22:20:49
