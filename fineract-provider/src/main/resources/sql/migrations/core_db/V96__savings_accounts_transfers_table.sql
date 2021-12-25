--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements. See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership. The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License. You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied. See the License for the
-- specific language governing permissions and limitations
-- under the License.
--

INSERT INTO `m_permission`
(`grouping`,`code`,`entity_name`,`action_name`,`can_maker_checker`)
VALUES
('transaction_savings', 'READ_ACCOUNTTRANSFER', 'ACCOUNTTRANSFER', 'READ', 0),
('transaction_savings', 'CREATE_ACCOUNTTRANSFER', 'ACCOUNTTRANSFER', 'CREATE', 1),
('transaction_savings', 'CREATE_ACCOUNTTRANSFER_CHECKER', 'ACCOUNTTRANSFER', 'CREATE', 0);


DROP TABLE IF EXISTS `m_savings_account_transfer`;

CREATE TABLE `m_savings_account_transfer` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `from_office_id` BIGINT NOT NULL,
  `to_office_id` BIGINT NOT NULL,
  `from_client_id` BIGINT NOT NULL,
  `to_client_id` BIGINT NOT NULL,
  `from_savings_account_id` BIGINT DEFAULT NULL,
  `to_savings_account_id` BIGINT DEFAULT NULL,
  `from_loan_account_id` BIGINT DEFAULT NULL,
  `to_loan_account_id` BIGINT DEFAULT NULL,
  `from_savings_transaction_id` BIGINT DEFAULT NULL,
  `from_loan_transaction_id` BIGINT DEFAULT NULL,
  `to_savings_transaction_id` BIGINT DEFAULT NULL,
  `to_loan_transaction_id` BIGINT DEFAULT NULL,
  `is_reversed` tinyint NOT NULL,
  `transaction_date` date NOT NULL,
  `currency_code` varchar(3) NOT NULL,
  `currency_digits` SMALLINT NOT NULL,
  `amount` decimal(19,6) NOT NULL,
  `description` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKTRAN000000001` (`from_office_id`),
  KEY `FKTRAN000000002` (`from_client_id`),
  KEY `FKTRAN000000003` (`from_savings_account_id`),
  KEY `FKTRAN000000004` (`to_office_id`),
  KEY `FKTRAN000000005` (`to_client_id`),
  KEY `FKTRAN000000006` (`to_savings_account_id`),
  KEY `FKTRAN000000007` (`to_loan_account_id`),
  KEY `FKTRAN000000008` (`from_savings_transaction_id`),
  KEY `FKTRAN000000009` (`to_savings_transaction_id`),
  KEY `FKTRAN000000010` (`to_loan_transaction_id`),
  KEY `FKTRAN000000011` (`from_loan_account_id`),
  KEY `FKTRAN000000012` (`from_loan_transaction_id`),
  CONSTRAINT `FKTRAN000000001` FOREIGN KEY (`from_office_id`) REFERENCES `m_office` (`id`),
  CONSTRAINT `FKTRAN000000002` FOREIGN KEY (`from_client_id`) REFERENCES `m_client` (`id`),
  CONSTRAINT `FKTRAN000000003` FOREIGN KEY (`from_savings_account_id`) REFERENCES `m_savings_account` (`id`),
  CONSTRAINT `FKTRAN000000004` FOREIGN KEY (`to_office_id`) REFERENCES `m_office` (`id`),
  CONSTRAINT `FKTRAN000000005` FOREIGN KEY (`to_client_id`) REFERENCES `m_client` (`id`),
  CONSTRAINT `FKTRAN000000006` FOREIGN KEY (`to_savings_account_id`) REFERENCES `m_savings_account` (`id`),
  CONSTRAINT `FKTRAN000000007` FOREIGN KEY (`to_loan_account_id`) REFERENCES `m_loan` (`id`),
  CONSTRAINT `FKTRAN000000008` FOREIGN KEY (`from_savings_transaction_id`) REFERENCES `m_savings_account_transaction` (`id`),
  CONSTRAINT `FKTRAN000000009` FOREIGN KEY (`to_savings_transaction_id`) REFERENCES `m_savings_account_transaction` (`id`),
  CONSTRAINT `FKTRAN000000010` FOREIGN KEY (`to_loan_transaction_id`) REFERENCES `m_loan_transaction` (`id`),
  CONSTRAINT `FKTRAN000000011` FOREIGN KEY (`from_loan_account_id`) REFERENCES `m_loan` (`id`),
  CONSTRAINT `FKTRAN000000012` FOREIGN KEY (`from_loan_transaction_id`) REFERENCES `m_loan_transaction` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;
