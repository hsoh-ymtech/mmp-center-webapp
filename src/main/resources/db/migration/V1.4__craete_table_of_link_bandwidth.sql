DROP TABLE IF EXISTS `link_bandwidth`;
CREATE TABLE `link_bandwidth` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`measured_time` timestamp NOT NULL,
`mesh_id` VARCHAR(64) NOT NULL,
`src_ip_address` VARCHAR(16) NOT NULL,
`dest_ip_address` VARCHAR(16) NOT NULL,
`bandwidth` float NOT NULL,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
