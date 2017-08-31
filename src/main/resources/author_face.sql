CREATE TABLE `author_face_livelib` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ll_author_id` int(11) NOT NULL,
  `nl_author_id` int(11) DEFAULT NULL,
  `author_name` varchar(512) NOT NULL,
  `author_alt` varchar(512),
  `face_url` varchar(512) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20881 DEFAULT CHARSET=utf8;