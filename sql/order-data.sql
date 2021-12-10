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
-- Dumping data for table `oomall_error_payment`
--

LOCK TABLES `oomall_error_account` WRITE;
/*!40000 ALTER TABLE `oomall_error_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `oomall_error_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping data for table `oomall_order`
--

LOCK TABLES `oomall_order` WRITE;
/*!40000 ALTER TABLE `oomall_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `oomall_order` ENABLE KEYS */;
INSERT INTO `oomall_order` VALUES (1, 1, NULL, '20216453652635231006', 0, 'gyt', 1, '临沂', '16253645342', '好耶', NULL, NULL, 8, 10, 100, 6, NULL, NULL, 101, NULL, 1, 'gyt', 2, 'admin2', '2021-12-1 15:59:56', '2021-12-2 16:31:06');
INSERT INTO `oomall_order` VALUES (2, 1, 1, '20216487652635231002', 1, 'gyt', 1, '临沂', '16253645342', '好耶', NULL, NULL, NULL, 5, 50, 3, NULL, NULL, NULL, NULL, 1, 'gyt', NULL, NULL, '2021-12-2 16:41:29', NULL);
INSERT INTO `oomall_order` VALUES (3, 1, 2, '20216487872635231007', 1, 'gyt', 1, '临沂', '16253645342', '好耶', NULL, NULL, NULL, 5, 50, 3, NULL, NULL, NULL, NULL, 1, 'gyt', NULL, NULL, '2021-12-2 16:48:17', NULL);
INSERT INTO `oomall_order` VALUES (4, 2, 2, '20216489872635231004', 0, 'hty', 2, '宁波', '15267541524', '唔', 1, NULL, 7, 12, 200, NULL, NULL, NULL, 102, NULL, 2, 'hty', NULL, NULL, '2021-12-2 16:58:28', NULL);
INSERT INTO `oomall_order` VALUES (5, 2, 3, '20216476872635231008', 0, 'hty', 3, '宁波', '15267541524', '噢噢', 1, NULL, 7, 15, 240, NULL, NULL, NULL, 201, NULL, 2, 'hty', NULL, NULL, '2021-12-2 17:01:57', NULL);
INSERT INTO `oomall_order` VALUES (6, 3, 3, '20218589872635231004', 0, 'lxc', 3, '济南', '18129346534', '嗯嗯', NULL, 1, 3, NULL, 100, NULL, NULL, NULL, 201, NULL, 3, 'lxc', NULL, NULL, '2021-12-2 17:10:32', NULL);
INSERT INTO `oomall_order` VALUES (7, 3, 4, '20218589972635231004', 0, 'lxc', 3, '济南', '18129346534', '嗯嗯', NULL, 2, 3, NULL, 120, NULL, NULL, NULL, 202, NULL, 3, 'lxc', NULL, NULL, '2021-12-2 17:12:33', NULL);
INSERT INTO `oomall_order` VALUES (8, 3, 5, '20218589972635231009', 0, 'lxc', 3, '济南', '18129346534', '嗯嗯', NULL, 3, 3, NULL, 230, NULL, NULL, NULL, 203, NULL, 3, 'lxc', NULL, NULL, '2021-12-2 17:15:06', NULL);
INSERT INTO `oomall_order` VALUES (9, 4, 1, '20218576972638731004', 0, 'hqg', 4, '福州', '17276541624', '啦啦', NULL, NULL, 6, 15, 280, 12, '2021-11-17 17:20:20', '36527364532', 300, NULL, 4, 'hqg', NULL, NULL, '2021-12-2 17:18:19', NULL);
INSERT INTO `oomall_order` VALUES (10, 4, 6, '20218987972635231004', 0, 'hqg', 4, '福州', '17276541624', '啦啦', NULL, NULL, 8, 12, 231, 22, '2021-11-11 17:24:20', '65442635211', 400, NULL, 4, 'hqg', NULL, NULL, '2021-12-2 17:23:28', NULL);
INSERT INTO `oomall_order` VALUES (11, 5, 2, '20218576972638731004', 0, 'fz', 5, '宁德', '17265448765', '确实', NULL, NULL, 12, 23, 500, 23, '2021-12-2 17:25:43', '76543429999', 500, 1, 5, 'fz', NULL, NULL, '2021-12-2 17:24:56', NULL);

UNLOCK TABLES;

--
-- Dumping data for table `oomall_order_item`
--

LOCK TABLES `oomall_order_item` WRITE;
/*!40000 ALTER TABLE `oomall_order_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `oomall_order_item` ENABLE KEYS */;

INSERT INTO `oomall_order_item` VALUES (1, 2, 1, 1, 1, 1, 50, 5, 3, '巧克力', 1, 1, NULL, 1, 'gyt', NULL, NULL, '2021-12-2 17:33:33', NULL);
INSERT INTO `oomall_order_item` VALUES (2, 3, 2, 2, 2, 1, 50, 5, 3, '薯片', 2, 2, NULL, 1, 'gyt', NULL, NULL, '2021-12-2 17:34:20', NULL);

UNLOCK TABLES;

--
-- Dumping data for table `oomall_payment`
--

LOCK TABLES `oomall_payment` WRITE;
/*!40000 ALTER TABLE `oomall_payment` DISABLE KEYS */;
/*!40000 ALTER TABLE `oomall_payment` ENABLE KEYS */;

INSERT INTO `oomall_payment` VALUES (1, '7363522113', 0, 92, 76, '20216453652635231006', 0, '2021-12-1 15:43:38', NULL, NULL, 1, NULL, NULL, NULL, NULL, 1, 'gyt', NULL, NULL, '2021-12-2 17:46:10', NULL);
INSERT INTO `oomall_payment` VALUES (2, '7363522132', 0, 195, 195, '20216489872635231004', 0, '2021-12-2 16:51:38', NULL, NULL, 1, NULL, NULL, NULL, NULL, 2, 'hty', NULL, NULL, '2021-12-2 17:46:12', NULL);
INSERT INTO `oomall_payment` VALUES (3, '7363872113', 1, 232, 232, '20216476872635231008', 0, '2021-12-2 00:36:19', NULL, NULL, 1, NULL, NULL, NULL, NULL, 2, 'hty', NULL, NULL, '2021-12-7 00:31:23', NULL);
INSERT INTO `oomall_payment` VALUES (4, '7363598113', 1, 103, 103, '20218589872635231004', 0, '2021-12-1 00:36:56', NULL, NULL, 1, NULL, NULL, NULL, NULL, 3, 'lxc', NULL, NULL, '2021-12-7 00:33:01', NULL);
INSERT INTO `oomall_payment` VALUES (5, '8963522113', 1, 123, 123, '20218589972635231004', 0, '2021-12-1 00:40:57', NULL, NULL, 1, NULL, NULL, NULL, NULL, 3, 'lxc', NULL, NULL, '2021-12-7 00:37:21', NULL);
INSERT INTO `oomall_payment` VALUES (6, '7363522113', 1, 233, 233, '20218589972635231009', 0, '2021-12-1 00:42:22', NULL, NULL, 1, NULL, NULL, NULL, NULL, 3, 'lxc', NULL, NULL, '2021-12-7 00:42:12', NULL);
INSERT INTO `oomall_payment` VALUES (7, '7363522178', 0, 259, 259, '20218576972638731004', 0, '2021-12-1 14:33:43', NULL, NULL, 1, NULL, NULL, NULL, NULL, 4, 'hqg', NULL, NULL, '2021-12-7 00:43:26', NULL);
INSERT INTO `oomall_payment` VALUES (8, '7363522195', 1, 205, 205, '20218987972635231004', 0, '2021-12-2 14:35:07', NULL, NULL, 1, NULL, NULL, NULL, NULL, 4, 'hqg', NULL, NULL, '2021-12-7 14:34:04', NULL);
INSERT INTO `oomall_payment` VALUES (9, '5363522113', 1, 466, 466, '20218576972638731004', 0, '2021-12-2 14:37:18', NULL, NULL, 1, NULL, NULL, NULL, NULL, 5, 'fz', NULL, NULL, '2021-12-7 14:35:38', NULL);

UNLOCK TABLES;

--
-- Dumping data for table `oomall_payment_pattern`
--

LOCK TABLES `oomall_payment_pattern` WRITE;
/*!40000 ALTER TABLE `oomall_payment_pattern` DISABLE KEYS */;
/*!40000 ALTER TABLE `oomall_payment_pattern` ENABLE KEYS */;
INSERT INTO `oomall_payment_pattern` (`id`, `name`, `state`, `begin_time`, `end_time`, `class_name`, `creator_id`, `creator_name`, `modifier_id`, `modifier_name`, `gmt_create`, `gmt_modified`) VALUES (1, '支付宝', NULL, NULL, NULL, 'AlipayTransaction', NULL, NULL, NULL, NULL, '2021-12-10 22:33:58', NULL);
INSERT INTO `oomall_payment_pattern` (`id`, `name`, `state`, `begin_time`, `end_time`, `class_name`, `creator_id`, `creator_name`, `modifier_id`, `modifier_name`, `gmt_create`, `gmt_modified`) VALUES (2, '微信', NULL, NULL, NULL, 'WechatpayTransaction', NULL, NULL, NULL, NULL, '2021-12-10 22:33:58', NULL);

UNLOCK TABLES;

--
-- Dumping data for table `oomall_refund`
--

LOCK TABLES `oomall_refund` WRITE;
/*!40000 ALTER TABLE `oomall_refund` DISABLE KEYS */;
/*!40000 ALTER TABLE `oomall_refund` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-11-20 15:00:54
