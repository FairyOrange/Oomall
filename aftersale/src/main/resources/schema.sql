-- MySQL dump 10.13  Distrib 8.0.22, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: oomall
-- ------------------------------------------------------
-- Server version	8.0.22

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
-- Table structure for table `aftersale_service`
--

--
-- Table structure for table `aftersale_service`
--

DROP TABLE IF EXISTS `aftersale_service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aftersale_service` (
                                     `id` bigint NOT NULL AUTO_INCREMENT,
                                     `order_item_id` bigint DEFAULT NULL,
                                     `customer_id` bigint DEFAULT NULL,
                                     `shop_id` bigint DEFAULT NULL,
                                     `service_sn` varchar(128) DEFAULT NULL,
                                     `type` tinyint DEFAULT NULL,
                                     `reason` varchar(500) DEFAULT NULL,
                                     `conclusion` varchar(500) DEFAULT NULL,
                                     `refund` bigint DEFAULT NULL,
                                     `quantity` int DEFAULT NULL,
                                     `region_id` bigint DEFAULT NULL,
                                     `detail` varchar(500) DEFAULT NULL,
                                     `consignee` varchar(64) DEFAULT NULL,
                                     `mobile` varchar(128) DEFAULT NULL,
                                     `customer_log_sn` varchar(128) DEFAULT NULL,
                                     `shop_log_sn` varchar(128) DEFAULT NULL,
                                     `state` tinyint DEFAULT NULL,
                                     `be_deleted` tinyint DEFAULT NULL,
                                     `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     `gmt_modified` datetime DEFAULT NULL,
                                     `order_id` bigint DEFAULT NULL,
                                     PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-11-21  7:57:21
