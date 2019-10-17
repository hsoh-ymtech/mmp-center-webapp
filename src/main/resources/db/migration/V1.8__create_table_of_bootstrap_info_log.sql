DROP TABLE IF EXISTS `bootstrap_info_log`;
CREATE TABLE `bootstrap_info_log` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`mesh_id` varchar(32) NOT NULL,
`public_ip` varchar(32) NOT NULL,
`mac` varchar(32) NOT NULL,
`outbound_ip` varchar(32) NOT NULL,
`write_time` timestamp default current_timestamp() not null,
PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
