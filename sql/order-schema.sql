-- MySQL dump 10.13  Distrib 5.7.29, for Win64 (x86_64)
--
-- Host: localhost    Database: oomall_orders
-- ------------------------------------------------------
-- Server version	5.7.29

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
-- Table structure for table `oomall_error_payment`
--

DROP TABLE IF EXISTS `oomall_error_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oomall_error_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trade_sn` varchar(128) DEFAULT NULL,
  `pattern_id` bigint(20) DEFAULT NULL,
  `income` bigint(20) DEFAULT NULL,
  `expenditure` bigint(20) DEFAULT NULL,
  `document_id` varchar(128) DEFAULT NULL,
  `state` tinyint(4) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `descr` varchar(256) DEFAULT NULL,
  `adjust_id` bigint(20) DEFAULT NULL,
  `adjust_name` varchar(128) DEFAULT NULL,
  `adjust_time` datetime DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint(20) DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_order`
--

DROP TABLE IF EXISTS `oomall_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oomall_order` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `customer_id` bigint(20) DEFAULT NULL,
  `shop_id` bigint(20) DEFAULT NULL,
  `order_sn` varchar(128) DEFAULT NULL,
  `pid` bigint(20) DEFAULT NULL,
  `consignee` varchar(128) DEFAULT NULL,
  `region_id` bigint(20) DEFAULT NULL,
  `address` varchar(500) DEFAULT NULL,
  `mobile` varchar(255) DEFAULT NULL,
  `message` varchar(500) DEFAULT NULL,
  `advancesale_id` bigint(20) DEFAULT NULL,
  `groupon_id` bigint(20) DEFAULT NULL,
  `express_fee` bigint(20) DEFAULT NULL,
  `discount_price` bigint(20) DEFAULT NULL,
  `origin_price` bigint(20) DEFAULT NULL,
  `point` bigint(20) DEFAULT NULL,
  `confirm_time` datetime DEFAULT NULL,
  `shipment_sn` varchar(128) DEFAULT NULL,
  `state` int DEFAULT NULL,
  `be_deleted` tinyint(4) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint(20) DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_order_item`
--

DROP TABLE IF EXISTS `oomall_order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oomall_order_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `order_id` bigint(20) DEFAULT NULL,
  `shop_id` bigint(20) DEFAULT NULL,
  `product_id` bigint(20) DEFAULT NULL,
  `onsale_id` bigint(20) DEFAULT NULL,
  `quantity` bigint(20) DEFAULT NULL,
  `price` bigint(20) DEFAULT NULL,
  `discount_price` bigint(20) DEFAULT NULL,
  `point` bigint(20) DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL,
  `coupon_activity_id` bigint(20) DEFAULT NULL,
  `coupon_id` bigint(20) DEFAULT NULL,
  `commented` tinyint(4) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_by` bigint(20) DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_payment`
--

DROP TABLE IF EXISTS `oomall_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oomall_payment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trade_sn` varchar(128) DEFAULT NULL,
  `pattern_id` bigint(20) DEFAULT NULL,
  `amount` bigint(20) DEFAULT NULL,
  `actual_amount` bigint(20) DEFAULT NULL,
  `document_id` varchar(128) DEFAULT NULL,
  `document_type` tinyint(4) DEFAULT NULL,
  `pay_time` datetime DEFAULT NULL,
  `begin_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `state` tinyint(4) DEFAULT NULL,
  `descr` varchar(256) DEFAULT NULL,
  `adjust_id` bigint(20) DEFAULT NULL,
  `adjust_name` varchar(128) DEFAULT NULL,
  `adjust_time` datetime DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint(20) DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_payment_pattern`
--

DROP TABLE IF EXISTS `oomall_payment_pattern`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oomall_payment_pattern` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `state` tinyint(4) DEFAULT NULL,
  `begin_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `class_name` varchar(128) DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint(20) DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `oomall_refund`
--

DROP TABLE IF EXISTS `oomall_refund`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `oomall_refund` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `trade_sn` varchar(128) DEFAULT NULL,
  `pattern_id` bigint(20) DEFAULT NULL,
  `payment_id` bigint(20) DEFAULT NULL,
  `amount` bigint(20) DEFAULT NULL,
  `document_id` varchar(128) DEFAULT NULL,
  `document_type` tinyint(4) DEFAULT NULL,
  `refund_time` datetime DEFAULT NULL,
  `state` tinyint(4) DEFAULT NULL,
  `descr` varchar(256) DEFAULT NULL,
  `adjust_id` bigint(20) DEFAULT NULL,
  `adjust_name` varchar(128) DEFAULT NULL,
  `adjust_time` datetime DEFAULT NULL,
  `creator_id` bigint(20) DEFAULT NULL,
  `creator_name` varchar(128) DEFAULT NULL,
  `modifier_id` bigint(20) DEFAULT NULL,
  `modifier_name` varchar(128) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-11-20 15:01:09
