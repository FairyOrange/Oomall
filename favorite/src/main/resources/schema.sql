--
-- Table structure for table `favourite_goods`
--

DROP TABLE IF EXISTS `favourite_goods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favourite_goods` (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `customer_id` bigint DEFAULT NULL,
                                   `goods_sku_id` bigint DEFAULT NULL,
                                   `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   `gmt_modified` datetime DEFAULT NULL,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3833740 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;