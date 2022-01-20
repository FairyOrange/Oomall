--
-- 购物车数据在cart下的testdata里
--


--
-- 增加goods_sku的数据，以便购物车服务的测试
--

LOCK TABLES `goods_sku` WRITE;
/*!40000 ALTER TABLE `goods_sku` DISABLE KEYS */;
INSERT INTO `goods_sku` VALUES
(6800,6800,NULL,'+',6698,NULL,12,'http://47.52.88.176/file/images/201912/file_5df048b5be111.jpg',1000,NULL,0,'2020-12-10 22:36:00','2020-12-10 22:36:00',4),
(6810,6800,NULL,'+',6697,NULL,12,'http://47.52.88.176/file/images/201912/file_5df048b5be112.jpg',1000,NULL,0,'2020-12-10 22:36:00','2020-12-10 22:36:00',4),
(6820,6800,NULL,'+',6696,NULL,12,'http://47.52.88.176/file/images/201912/file_5df048b5be113.jpg',1000,NULL,0,'2020-12-10 22:36:00','2020-12-10 22:36:00',4),
(6830,6800,NULL,'+',6695,NULL,12,'http://47.52.88.176/file/images/201912/file_5df048b5be114.jpg',1000,NULL,0,'2020-12-10 22:36:00','2020-12-10 22:36:00',4),
(6840,6800,NULL,'+',6695,NULL,12,'http://47.52.88.176/file/images/201912/file_5df048b5be115.jpg',1000,NULL,0,'2020-12-10 22:36:00','2020-12-10 22:36:00',4),
(6850,6850,NULL,'+',6695,NULL,12,'http://47.52.88.176/file/images/201912/file_5df048b5be116.jpg',1000,NULL,0,'2020-12-10 22:36:00','2020-12-10 22:36:00',4),
(6860,6850,NULL,'+',6695,NULL,12,'http://47.52.88.176/file/images/201912/file_5df048b5be117.jpg',1000,NULL,0,'2020-12-10 22:36:00','2020-12-10 22:36:00',4),
(6870,6850,NULL,'+',6697,NULL,12,'http://47.52.88.176/file/images/201912/file_5df048b5be118.jpg',1000,NULL,0,'2020-12-10 22:36:00','2020-12-10 22:36:00',4),
(6880,6880,NULL,'+',6696,NULL,12,'http://47.52.88.176/file/images/201912/file_5df048b5be119.jpg',1000,NULL,0,'2020-12-10 22:36:00','2020-12-10 22:36:00',4),
(6890,6880,NULL,'+',6694,NULL,12,'http://47.52.88.176/file/images/201912/file_5df048b5be120.jpg',1000,NULL,0,'2020-12-10 22:36:00','2020-12-10 22:36:00',4),
(6900,6880,NULL,'+',6695,NULL,12,'http://47.52.88.176/file/images/201912/file_5df048b5be121.jpg',1000,NULL,0,'2020-12-10 22:36:00','2020-12-10 22:36:00',4),
(6910,6880,NULL,'+',6692,NULL,12,'http://47.52.88.176/file/images/201912/file_5df048b5be122.jpg',1000,NULL,0,'2020-12-10 22:36:00','2020-12-10 22:36:00',4);
/*!40000 ALTER TABLE `goods_sku` ENABLE KEYS */;
UNLOCK TABLES;


--
-- Dumping data for table `goods_spu`
--

LOCK TABLES `goods_spu` WRITE;
/*!40000 ALTER TABLE `goods_spu` DISABLE KEYS */;

INSERT INTO `goods_spu` VALUES
(6800,'yang8miao玩具',71,123,11,22,'yang-888',NULL,'http://47.52.88.176/file/images/201412/file_file_1132asd32222.jpg','default',0,'2020-12-10 22:36:01','2020-12-10 22:36:01'),
(6850,'yang8miao三国杀',71,123,11,22,'yang-889',NULL,'http://47.52.88.176/file/images/201412/file_file_1132asd32223.jpg','default',0,'2020-12-10 22:36:01','2020-12-10 22:36:01'),
(6880,'yang8miao王者荣耀',71,123,11,22,'yang-880',NULL,'http://47.52.88.176/file/images/201412/file_file_1132asd32224.jpg','default',0,'2020-12-10 22:36:01','2020-12-10 22:36:01');
/*!40000 ALTER TABLE `goods_spu` ENABLE KEYS */;
UNLOCK TABLES;