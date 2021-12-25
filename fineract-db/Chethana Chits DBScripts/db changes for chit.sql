ALTER TABLE chit_group_subscriber ADD COLUMN prized_subscriber TINYINT , ADD COLUMN prized_cycle INT;
ALTER TABLE `chit_group_cycle`
	CHANGE COLUMN `start_date` `start_date` DATE NULL AFTER `auction_date`,
	ADD COLUMN `end_date` DATE NULL DEFAULT NULL AFTER `start_date`,
	ADD COLUMN `dividend` BIGINT(20) NULL DEFAULT NULL AFTER `end_date`,
	ADD COLUMN `bid_minutes_filing_due_date` DATE NULL DEFAULT NULL AFTER `dividend`;
	
alter table chit_group_bids add column bid_date DATE NOT NULL;
ALTER TABLE `chit_group_bids`
ADD COLUMN `bidder_participation` INT NULL AFTER `bid_won`;
ALTER TABLE `chit_group_bids`
ADD CONSTRAINT `bidder_participants_to m_code_value` FOREIGN KEY (`bidder_participation`) REFERENCES `fineract_default`.`m_code_value` (`id`);



INSERT INTO m_code (code_name,is_system_defined) VALUES ('BidderParticipation',1);
INSERT INTO m_code_value (code_id,code_value,order_position,is_active,is_mandatory) VALUES 
((SELECT id FROM m_code WHERE code_name = 'BidderParticipation'),'Prize Bidder',1,1,1);
INSERT INTO m_code_value (code_id,code_value,order_position,is_active,is_mandatory) VALUES 
((SELECT id FROM m_code WHERE code_name = 'BidderParticipation'),'Bid Offer',2,1,1);
INSERT INTO m_code_value (code_id,code_value,order_position,is_active,is_mandatory) VALUES 
((SELECT id FROM m_code WHERE code_name = 'BidderParticipation'),'Proxy',3,1,1);

ALTER TABLE m_client_transaction ADD COLUMN adjusted TINYINT ;

ALTER table chit_subscriber_transaction ADD column transaction_date timestamp not null default current_timestamp on update CURRENT_TIMESTAMP;

ALTER TABLE `chit_demand_schedule` ADD COLUMN `is_calculated` TINYINT NOT NULL DEFAULT '0';




//Advance Payment 16/07/2021

INSERT INTO m_payment_type (value,description,is_cash_payment,order_position) values ('CASH','',0,1);
INSERT INTO m_payment_type (value,description,is_cash_payment,order_position) values ('CHEQUE','',0,2);
INSERT INTO m_payment_type (value,description,is_cash_payment,order_position) values ('NEFT','',0,3);
INSERT INTO m_payment_type (value,description,is_cash_payment,order_position) values ('UPI','',0,4);
INSERT INTO m_payment_type (value,description,is_cash_payment,order_position) values ('QR CODE','',0,5);
INSERT INTO m_payment_type (value,description,is_cash_payment,order_position) values ('IMPS','',0,6);
INSERT INTO m_payment_type (value,description,is_cash_payment,order_position) values ('RTGS','',0,7);

// c_configuration
INSERT INTO c_configuration (name, value, description) values ('Foreman-Commission',7,'Value Should Be Considered In Percentage');
INSERT INTO c_configuration (name, value) values ('Verification-Fee',100);
INSERT INTO c_configuration (name, value, description) values ('GST',12,'Value Should Be Considered In Percentage');
INSERT INTO c_configuration (`name`, `value`, `enabled`, `description`) VALUES ('DemandAmountRoundOffValue', '10', '1', 'This is used in demand schedule calculation for rounding off');
INSERT INTO c_configuration (name, value, description) VALUES ('CGST', '6', 'Value Should Be Considerd In Percentage');
INSERT INTO c_configuration (name, value, description) VALUES ('SGST', '6', 'Value Should Be Considerd In Percentage');

ALTER TABLE `fineract_default`.`m_appuser` CHANGE COLUMN `lastname` `lastname` VARCHAR(100) NULL ;

INSERT INTO `fineract_default`.`chit_charge` (`name`) VALUES ('ENROLLMENT_FEE');
INSERT INTO `fineract_default`.`chit_charge` (`name`) VALUES ('MONTHLY_INSTALLMENT');

ALTER TABLE `chit_charge` ADD COLUMN `isEnabled` TINYINT NULL DEFAULT NULL;

ALTER TABLE `chit_subscriber_charge` CHANGE COLUMN `chit_cycle_id` `chit_cycle_id` BIGINT(20) NULL ;

ALTER TABLE chit_subscriber_charge ADD COLUMN staff_id INT ;

ALTER TABLE chit_group_cycle ADD COLUMN subscriptionPayble DOUBLE DEFAULT NULL, ADD COLUMN gstAmount DOUBLE DEFAULT NULL,
ADD COLUMN foremanCommissionAmount DOUBLE DEFAULT NULL,ADD COLUMN verificationAmount DOUBLE DEFAULT NULL ;

ALTER TABLE acc_gl_journal_entry ADD COLUMN chitgprSubs INT;
ALTER TABLE m_client ADD COLUMN amt_applied INT ; 
ALTER TABLE `m_client` ADD CONSTRAINT `FK_m_client_ns_chit_group` FOREIGN KEY (`amt_applied`) REFERENCES `fineract_default`.`ns_chit_group` (`id`);


CREATE TABLE `ns_chit_group` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`chit_name` VARCHAR (50) NOT NULL,
	`chit_duration` INT NOT NULL,
	`chit_value` DOUBLE NOT NULL DEFAULT 0,
	`no_of_subscribers` INT NOT NULL,
	PRIMARY KEY (`id`)
)

ALTER TABLE chit_demand_schedule ADD COLUMN chit_id INT(11);

ALTER TABLE ns_chit_group ADD COLUMN min_bid_perct DECIMAL ,ADD COLUMN max_bid_perct DECIMAL,ADD COLUMN isEnabled TINYINT; 

ALTER TABLE acc_gl_journal_entry ADD COLUMN chit_id INT;

ALTER TABLE chit_group ADD COLUMN end_date DATE;


ALTER TABLE `chit_group_bids`
	ADD COLUMN `is_prize_money_paid` TINYINT NULL DEFAULT 0 ;
	
ALTER TABLE `m_guarantor`
DROP COLUMN `loan_id`,
DROP FOREIGN KEY `FK_m_guarantor_m_loan`; ALTER TABLE `m_guarantor` ADD COLUMN `client_id` BIGINT NULL DEFAULT NULL AFTER `is_active`, ADD CONSTRAINT `FK_m_guarantor_m_client` FOREIGN KEY (`client_id`) REFERENCES `fineract_default`.`m_client` (`id`);
ALTER TABLE m_guarantor ADD COLUMN `qualification` INT DEFAULT NULL,
ADD COLUMN profession int DEFAULT NULL;
ALTER TABLE `m_guarantor`
CHANGE COLUMN `entity_id` `entity_id` VARCHAR(50) NULL DEFAULT NULL;  


ALTER TABLE m_document ADD COLUMN requestid VARCHAR(50);
ALTER TABLE m_permission ADD COLUMN is_Enabled TINYINT DEFAULT 0;


ALTER TABLE m_client ADD COLUMN cpv_data TINYINT DEFAULT 0;
ALTER TABLE `m_client`
	CHANGE COLUMN `middlename` `spousename` VARCHAR(50) NULL DEFAULT NULL;
	
ALTER TABLE ns_chit_group ADD COLUMN enrollment_fee DOUBLE DEFAULT 0;


INSERT INTO m_code (code_name,is_system_defined) VALUES ('paymentStatus',1);
INSERT INTO m_code_value (code_id,code_value,order_position,is_active,is_mandatory)VALUES((SELECT id FROM m_code c WHERE code_name = 'paymentStatus'),'Honored',1,1,1);
INSERT INTO m_code_value (code_id,code_value,order_position,is_active,is_mandatory)VALUES((SELECT id FROM m_code c WHERE code_name = 'paymentStatus'),'Returned',2,1,1);
INSERT INTO m_code_value (code_id,code_value,order_position,is_active,is_mandatory)VALUES((SELECT id FROM m_code c WHERE code_name = 'paymentStatus'),'Deposited',3,1,1);

CREATE TABLE `payment_status` (
	`id` INT NOT NULL AUTO_INCREMENT,
	`payment_type` INT NOT NULL,
	`status` INT NOT NULL,
	`date` DATE NOT NULL,
	`tran_id` INT NOT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `FK1_payment_type to payment_type` FOREIGN KEY (`payment_type`) REFERENCES `m_payment_type` (`id`),
	CONSTRAINT `FK2_status_to_m_code_value` FOREIGN KEY (`status`) REFERENCES `m_code_value` (`id`)
);


ALTER TABLE `payment_status`
	ADD COLUMN `officeId` BIGINT NOT NULL AFTER `tran_id`,
	ADD CONSTRAINT `FK3 office_id_to_branch_id` FOREIGN KEY (`officeId`) REFERENCES `m_office` (`id`);


ALTER TABLE chit_group_subscriber ADD COLUMN bid_advance Boolean DEFAULT 0;
ALTER TABLE chit_group_subscriber ADD COLUMN toBePaidAmount Double ;
ALTER TABLE chit_group_subscriber ADD COLUMN is_processed Boolean DEFAULT 0;


ALTER TABLE `m_payment_detail`
	ADD COLUMN `officeId` BIGINT NULL DEFAULT NULL AFTER `transactionNo`,
	ADD INDEX `officeId` (`officeId`),ADD CONSTRAINT `FK_m_payment_detial_m_office` FOREIGN KEY (`officeId`) REFERENCES `m_office` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE m_address ADD COLUMN taluka VARCHAR(50),ADD COLUMN district VARCHAR(50),ADD COLUMN landmark VARCHAR(50);



INSERT INTO m_code (code_name,is_system_defined) VALUES ('subscriberStatus',1);
INSERT INTO m_code_value (code_id,code_value,order_position,is_active,is_mandatory)VALUES((SELECT id FROM m_code c WHERE code_name = 'subscriberStatus'),'SuccessfulBidder',1,1,1);
INSERT INTO m_code_value (code_id,code_value,order_position,is_active,is_mandatory)VALUES((SELECT id FROM m_code c WHERE code_name = 'subscriberStatus'),'Terminated',2,1,1);
INSERT INTO m_code_value (code_id,code_value,order_position,is_active,is_mandatory)VALUES((SELECT id FROM m_code c WHERE code_name = 'subscriberStatus'),'Fore-Closed',3,1,1);
INSERT INTO m_code_value (code_id,code_value,order_position,is_active,is_mandatory)VALUES((SELECT id FROM m_code c WHERE code_name = 'subscriberStatus'),'Replaced',4,1,1);
INSERT INTO m_code_value (code_id,code_value,order_position,is_active,is_mandatory)VALUES((SELECT id FROM m_code c WHERE code_name = 'subscriberStatus'),'Re-Bidder',5,1,1);
INSERT INTO m_code_value (code_id,code_value,order_position,is_active,is_mandatory)VALUES((SELECT id FROM m_code c WHERE code_name = 'subscriberStatus'),'AwardedPrizemoney',6,1,1);
INSERT INTO m_code_value (code_id,code_value,order_position,is_active,is_mandatory)VALUES((SELECT id FROM m_code c WHERE code_name = 'subscriberStatus'),'Closed',7,1,1);
INSERT INTO m_code_value (code_id,code_value,order_position,is_active,is_mandatory)VALUES((SELECT id FROM m_code c WHERE code_name = 'subscriberStatus'),'BidAdvance',8,1,1);


ALTER TABLE chit_group_subscriber ADD COLUMN status_id INT  NULL DEFAULT NULL;
	
ALTER TABLE `chit_group_subscriber`  ADD INDEX `code_value_to_status_idx` (`status_id`),
ADD CONSTRAINT `code_value_to_status_idx` FOREIGN KEY (`status_id`) REFERENCES `fineract_default`.`m_code_value` (`id`) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE chit_group_subscriber ADD COLUMN sub_status_type TINYINTINT  NULL DEFAULT NULL;

ALTER TABLE chit_subscriber_transaction ADD COLUMN waiveoff_amount BIGINT(20) NULL DEFAULT NULL;



INSERT INTO m_code (code_name,is_system_defined) VALUES ('VoucherTypes',1);
INSERT INTO m_code_value (code_id,code_value,code_description,order_position,is_active,is_mandatory) VALUES 
((SELECT id FROM m_code WHERE code_name = 'VoucherTypes'),'Payment Voucher','PV',1,1,1);
INSERT INTO m_code_value (code_id,code_value,code_description,order_position,is_active,is_mandatory) VALUES 
((SELECT id FROM m_code WHERE code_name = 'VoucherTypes'),'Reciept Voucher', 'RV',2,1,1);
INSERT INTO m_code_value (code_id,code_value,code_description,order_position,is_active,is_mandatory) VALUES 
((SELECT id FROM m_code WHERE code_name = 'VoucherTypes'),'Contra Voucher','CV',3,1,1);
INSERT INTO m_code_value (code_id,code_value,code_description,order_position,is_active,is_mandatory) VALUES 
((SELECT id FROM m_code WHERE code_name = 'VoucherTypes'),'Journal Voucher','JV',4,1,1);
INSERT INTO m_code_value (code_id,code_value,code_description,order_position,is_active,is_mandatory) VALUES 
((SELECT id FROM m_code WHERE code_name = 'VoucherTypes'),'Adjustment Voucher', 'AV',5,1,1);
INSERT INTO m_code_value (code_id,code_value,code_description,order_position,is_active,is_mandatory) VALUES 
((SELECT id FROM m_code WHERE code_name = 'VoucherTypes'),'Fund Transfer','FV',6,1,1);
INSERT INTO m_code_value (code_id,code_value,code_description,order_position,is_active,is_mandatory) VALUES 
((SELECT id FROM m_code WHERE code_name = 'VoucherTypes'),'Journal Entry','JE',7,1,1);


CREATE TABLE `m_journal_vouchers` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`journal_transaction_id` VARCHAR(50) NOT NULL, 
	`voucher_type_id` INT(11) NOT NULL,
	`voucher_number` VARCHAR(50) NOT NULL,
	`vendor_name` VARCHAR(50) NULL DEFAULT NULL,
	PRIMARY KEY (`id`) USING BTREE
)