CREATE TABLE IF NOT EXISTS `orders` (
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `distance` int(11) DEFAULT NULL,
 `status` varchar(255) DEFAULT NULL,
 PRIMARY KEY (`id`)
);
