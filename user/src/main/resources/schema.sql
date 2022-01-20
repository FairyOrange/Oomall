--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `user_name` varchar(32) DEFAULT NULL,
                            `password` varchar(128) DEFAULT NULL,
                            `real_name` varchar(32) DEFAULT NULL,
                            `gender` tinyint DEFAULT NULL,
                            `birthday` date DEFAULT NULL,
                            `point` int DEFAULT NULL,
                            `state` tinyint DEFAULT NULL,
                            `email` varchar(128) DEFAULT NULL,
                            `mobile` varchar(128) DEFAULT NULL,
                            `be_deleted` tinyint DEFAULT NULL,
                            `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `gmt_modified` datetime DEFAULT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17330 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;