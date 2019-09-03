--
-- Table structure for table `kibana_info`
--

DROP TABLE IF EXISTS `kibana_info`;
CREATE TABLE `kibana_info` (
`kibana_id` int(11) NOT NULL AUTO_INCREMENT,
`host` varchar(32) NOT NULL,
`elastic_port` int(11) NOT NULL,
`kibana_port` int(11) NOT NULL,
`url` varchar(2083) NOT NULL,
PRIMARY KEY (`kibana_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

INSERT INTO `kibana_info` VALUES (2,'10.100.1.36',9200,5601,'http://escluster.happylife.io:5601/app/kibana#/dashboard/twamp_empty?_g=()&_a=(description:\'\',filters:!(),fullScreenMode:!f,options:(darkTheme:!f,hidePanelTitles:!t,useMargins:!t),panels:!(),query:(language:lucene,query:\'\'),timeRestore:!f,title:twamp-empty,viewMode:view)');

--
-- Table structure for table `lesion_history`
--

DROP TABLE IF EXISTS `lesion_history`;
CREATE TABLE `lesion_history` (
`lh_id` int(11) NOT NULL AUTO_INCREMENT,
`sess_id` int(11) NOT NULL,
`start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
`complete_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`sender_ip` varchar(16) NOT NULL,
`reflector_ip` varchar(16) NOT NULL,
`lesion_code` varchar(32) NOT NULL,
PRIMARY KEY (`lh_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12224 DEFAULT CHARSET=utf8;

--
-- Table structure for table `protocol_info`
--

DROP TABLE IF EXISTS `protocol_info`;
CREATE TABLE `protocol_info` (
`protocol_id` int(11) NOT NULL,
`type` varchar(16) NOT NULL,
PRIMARY KEY (`protocol_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `protocol_info`
--

INSERT INTO `protocol_info` VALUES (1,'Full TWAMP'),(2,'Light TWAMP');

--
-- Table structure for table `quality_history`
--

DROP TABLE IF EXISTS `quality_history`;
CREATE TABLE `quality_history` (
`sess_id` int(11) NOT NULL,
`sender_ip` varchar(16) NOT NULL DEFAULT '0',
`reflector_ip` varchar(16) NOT NULL DEFAULT '0',
`send_count` int(11) NOT NULL DEFAULT '0',
`repeat_count` int(11) NOT NULL DEFAULT '0',
`start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`complete_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`measure_protocol` varchar(32) NOT NULL DEFAULT '0',
`measure_mode` varchar(32) NOT NULL DEFAULT '0',
`sender_port` int(11) NOT NULL DEFAULT '0',
`reflector_port` int(11) NOT NULL DEFAULT '0',
`measure_result` varchar(32) NOT NULL DEFAULT '0',
PRIMARY KEY (`sess_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `reflector_info`
--

DROP TABLE IF EXISTS `reflector_info`;
CREATE TABLE `reflector_info` (
`reflector_id` int(11) NOT NULL AUTO_INCREMENT,
`reflector_ip` varchar(16) NOT NULL DEFAULT '0',
`port` int(11) NOT NULL DEFAULT '862',
`lat` float NOT NULL,
`lng` float NOT NULL,
`address` varchar(64) NOT NULL,
PRIMARY KEY (`reflector_id`)
) ENGINE=InnoDB AUTO_INCREMENT=127 DEFAULT CHARSET=utf8;

--
-- Dumping data for table `reflector_info`
--
INSERT INTO `reflector_info` (`reflector_id`,`address`,`reflector_ip`,`port`,`lat`,`lng`) VALUES(0,'미국 동부(버지니아 북부)','35.171.169.5',862,0,0);
INSERT INTO `reflector_info` (`reflector_id`,`address`,`reflector_ip`,`port`,`lat`,`lng`) VALUES(0,'미국 동부(오하이오)','3.16.89.112',862,0,0);
INSERT INTO `reflector_info` (`reflector_id`,`address`,`reflector_ip`,`port`,`lat`,`lng`) VALUES(0,'미국 서부(캘리포니아 북부)','18.144.18.28',862,0,0);
INSERT INTO `reflector_info` (`reflector_id`,`address`,`reflector_ip`,`port`,`lat`,`lng`) VALUES(0,'미국 서부(오레곤)','54.245.157.239',862,0,0);
INSERT INTO `reflector_info` (`reflector_id`,`address`,`reflector_ip`,`port`,`lat`,`lng`) VALUES(0,'아시아 태평양(홍콩)','18.162.115.73',862,0,0);
INSERT INTO `reflector_info` (`reflector_id`,`address`,`reflector_ip`,`port`,`lat`,`lng`) VALUES(0,'아이사 태평양(뭄바이)','13.232.151.185',862,0,0);
INSERT INTO `reflector_info` (`reflector_id`,`address`,`reflector_ip`,`port`,`lat`,`lng`) VALUES(0,'아이사 태평양(서울)','52.79.239.211',862,0,0);
INSERT INTO `reflector_info` (`reflector_id`,`address`,`reflector_ip`,`port`,`lat`,`lng`) VALUES(0,'아시아 태평양(싱가포르)','18.138.251.177',862,0,0);
INSERT INTO `reflector_info` (`reflector_id`,`address`,`reflector_ip`,`port`,`lat`,`lng`) VALUES(0,'아시아 태평양(시드니)','52.63.96.102',862,0,0);
INSERT INTO `reflector_info` (`reflector_id`,`address`,`reflector_ip`,`port`,`lat`,`lng`) VALUES(0,'아시아 태평양(도쿄)','13.114.206.157',862,0,0);
INSERT INTO `reflector_info` (`reflector_id`,`address`,`reflector_ip`,`port`,`lat`,`lng`) VALUES(0,'캐나다(중부)','35.183.30.204',862,0,0);
INSERT INTO `reflector_info` (`reflector_id`,`address`,`reflector_ip`,`port`,`lat`,`lng`) VALUES(0,'EU(프랑크프루트)','35.158.179.4',862,0,0);
INSERT INTO `reflector_info` (`reflector_id`,`address`,`reflector_ip`,`port`,`lat`,`lng`) VALUES(0,'EU(아일랜드)','34.247.87.72',862,0,0);
INSERT INTO `reflector_info` (`reflector_id`,`address`,`reflector_ip`,`port`,`lat`,`lng`) VALUES(0,'EU(런던)','3.9.16.124',862,0,0);
INSERT INTO `reflector_info` (`reflector_id`,`address`,`reflector_ip`,`port`,`lat`,`lng`) VALUES(0,'EU(파리)','35.180.230.36',862,0,0);
INSERT INTO `reflector_info` (`reflector_id`,`address`,`reflector_ip`,`port`,`lat`,`lng`) VALUES(0,'EU(스톡홀름)','13.48.68.8',862,0,0);
INSERT INTO `reflector_info` (`reflector_id`,`address`,`reflector_ip`,`port`,`lat`,`lng`) VALUES(0,'남아메리카(상파울루)','54.233.138.80',862,0,0);

--
-- Table structure for table `reflector_protocol_relationship`
--

DROP TABLE IF EXISTS `reflector_protocol_relationship`;
CREATE TABLE `reflector_protocol_relationship` (
`reflector_id` int(11) NOT NULL DEFAULT '0',
`protocol_id` int(11) NOT NULL DEFAULT '0',
PRIMARY KEY (`reflector_id`),
KEY `relation_reflector_id_idx` (`reflector_id`),
KEY `relation_protocol_id_idx` (`protocol_id`),
CONSTRAINT `relation_protocol_id` FOREIGN KEY (`protocol_id`) REFERENCES `protocol_info` (`protocol_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
CONSTRAINT `relation_reflector_id` FOREIGN KEY (`reflector_id`) REFERENCES `reflector_info` (`reflector_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


--
-- Table structure for table `current_status`
--

DROP TABLE IF EXISTS `current_status`;
CREATE TABLE `current_status` (
`sess_id` int(11) NOT NULL AUTO_INCREMENT,
`sender_ip` varchar(16) NOT NULL DEFAULT '0',
`sender_port` int(11) NOT NULL DEFAULT '0',
`reflector_id` int(11) NOT NULL DEFAULT '0',
`protocol_id` int(11) NOT NULL DEFAULT '0',
`measure_mode` varchar(16) NOT NULL DEFAULT '',
`send_count` int(11) NOT NULL DEFAULT '0',
`repeat_count` int(11) NOT NULL DEFAULT '0',
`start_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
`pid` int(11) NOT NULL DEFAULT '0',
`timeout` int(11) NOT NULL DEFAULT '0',
PRIMARY KEY (`sess_id`),
KEY `curr_reflector_id_idx` (`reflector_id`),
KEY `curr_protocol_id_idx` (`protocol_id`),
CONSTRAINT `curr_protocol_id` FOREIGN KEY (`protocol_id`) REFERENCES `protocol_info` (`protocol_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
CONSTRAINT `curr_reflector_id` FOREIGN KEY (`reflector_id`) REFERENCES `reflector_info` (`reflector_id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
