CREATE TABLE `credit_report` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `client_id` bigint(20) NOT NULL,
  `bureau` varchar(100) DEFAULT NULL,
  `score_type` varchar(100) DEFAULT NULL,
  `score_value` varchar(10) DEFAULT NULL,
  `score_comments` varchar(500) DEFAULT NULL,
  `report_id` varchar(40) DEFAULT NULL,
  `date_of_issue` varchar(25) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_client_id` (`client_id`),
  CONSTRAINT `fk_credit_report_client_id` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

INSERT INTO `fineract_default`.`c_configuration` (`name`, `value`, `description`) VALUES ('Credit-Score', '600', 'minimum credit score inorder to get approval');