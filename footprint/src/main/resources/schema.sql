--
-- Table structure for table `foot_print`
--

DROP TABLE IF EXISTS `foot_print`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `foot_print` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `customer_id` bigint DEFAULT NULL,
                              `goods_sku_id` bigint DEFAULT NULL,
                              `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                              `gmt_modified` datetime DEFAULT NULL,
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1245147 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;