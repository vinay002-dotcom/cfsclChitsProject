CREATE TABLE IF NOT EXISTS `fineract_default`.`chit_group` (
  `id` BIGINT(20)   AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  `start_date` DATETIME NULL,
  `branch_id` BIGINT(20) NULL,
  `staff_id` BIGINT(20) NULL,
  `chit_cycle_frequency` VARCHAR(20) NULL COMMENT 'Monthly\n		',
  `chit_collection_frequency` VARCHAR(20) NULL,
  `chit_duration` TINYINT(4) NULL,
  `chit_value` INT NULL,
  `monthly_contribution` INT NULL,
  `auction_day` VARCHAR(20) NULL COMMENT 'CalendarDay or FlexibleDay\n',
  `auction_day_value` TINYINT(4) NULL,
  `auction_day_type` VARCHAR(20) NULL,
  `auction_week_value` VARCHAR(20) NULL,
  `auction_time` TIME NULL,
  `current_cycle` TINYINT(4) NULL,
  `next_auction_date` DATETIME NULL,
  `status` TINYINT(4) NULL DEFAULT 10,
  `commission_earned` INT NULL,
  `chit_aum` INT NULL,
  `amount_disbursed` INT NULL,
  `amount_not_disbursed` INT NULL,
  `enrollment_fees` INT NULL,
  `min_bid_perct` DECIMAL(4,2) NULL,
  `max_bid_perct` DECIMAL(4,2) NULL,
  `priz_mem_pen_perct` DECIMAL(4,2) NULL,
  `non_priz_mem_pen_perct` DECIMAL(4,2) NULL,
  `fdr_ac_number` VARCHAR(45) NULL,
  `fdr_issue_date` DATETIME NULL,
  `fdr_matu_date` DATETIME NULL,
  `fdr_dep_amount` INT NULL,
  `fdr_duration` TINYINT(4) NULL,
  `fdr_rat_int_perct` DECIMAL(4,2) NULL,
  `fdr_rate_int_amt` INT NULL,
  `fdr_int_pay_cycle` VARCHAR(20) NULL,
  `fdr_bankname` VARCHAR(45) NULL,
  `fdr_bankbranch_name` VARCHAR(45) NULL,
  `fdr_matu_amount` INT NULL,
  `pso_appl_date` DATETIME NULL, 
  `pso_issue_date` DATETIME NULL, 
  `pso_number` VARCHAR(45) NULL, 
  `cc_appl_date` DATETIME NULL, 
  `cc_issue_date` DATETIME NULL, 
  `cc_number` VARCHAR(45) NULL,  
  PRIMARY KEY (`id`));
  
  CREATE TABLE `fineract_default`.`chit_group_subscriber` (
  `id` BIGINT(20) AUTO_INCREMENT,
  `chit_id` BIGINT(20) NOT NULL,
  `client_id` BIGINT(20) NOT NULL,
  `chit_number` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `chitsubscriber_to_chit_idx` (`chit_id` ASC),
  INDEX `chitsubscriber_to_client_idx` (`client_id` ASC),
  CONSTRAINT `chitsubscriber_to_chit`
  FOREIGN KEY (`chit_id`)
  REFERENCES `fineract_default`.`chit_group` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
  CONSTRAINT `chitsubscriber_to_client`
  FOREIGN KEY (`client_id`)
  REFERENCES `fineract_default`.`m_client` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION);

  CREATE TABLE `fineract_default`.`chit_group_cycle` (
  `id` BIGINT(20) AUTO_INCREMENT,
  `chit_id` BIGINT(20) NOT NULL,
  `cycle_number` INT NOT NULL,
  `auction_date` DATETIME NULL,
  PRIMARY KEY (`id`),
  INDEX `chitcycle_to_chit_idx` (`chit_id` ASC),
  CONSTRAINT `chitcycle_to_chit`
  FOREIGN KEY (`chit_id`)
  REFERENCES `fineract_default`.`chit_group` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION);
  
  CREATE TABLE `fineract_default`.`chit_group_bids` (
  `id` BIGINT(20) AUTO_INCREMENT,
  `chit_cycle_id` BIGINT(20) NOT NULL,
  `chit_subscriber_id` BIGINT(20) NOT NULL,
  `bid_amount` INT NOT NULL,
  `bid_won` TINYINT NULL,
  PRIMARY KEY (`id`),
  INDEX `chitbids_to_chitcycle_idx` (`chit_cycle_id` ASC),
  INDEX `chitbids_to_chitsubscriber_idx` (`chit_subscriber_id` ASC),
  CONSTRAINT `chitbids_to_chitcycle`
  FOREIGN KEY (`chit_cycle_id`)
  REFERENCES `fineract_default`.`chit_group_cycle` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
  CONSTRAINT `chitbids_to_chitsubscriber`
  FOREIGN KEY (`chit_subscriber_id`)
  REFERENCES `fineract_default`.`chit_group_subscriber` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
  );

  
  
INSERT INTO m_permission(grouping,CODE,entity_name,action_name,can_maker_checker)VALUES('portfolio','CREATE_CHITGROUP','CHITGROUP','CREATE',1);
INSERT INTO m_permission(grouping,CODE,entity_name,action_name,can_maker_checker)VALUES('portfolio','UPDATE_CHITGROUP','CHITGROUP','UPDATE',1);
INSERT INTO m_permission(grouping,CODE,entity_name,action_name,can_maker_checker)VALUES('portfolio','READ_CHITGROUP','CHITGROUP','READ',1);

INSERT INTO m_permission(grouping,CODE,entity_name,action_name,can_maker_checker)VALUES('portfolio','CREATE_CHITGROUPBID','CHITGROUPBID','CREATE',1);
INSERT INTO m_permission(grouping,CODE,entity_name,action_name,can_maker_checker)VALUES('portfolio','UPDATE_CHITGROUPBID','CHITGROUPBID','UPDATE',1);
INSERT INTO m_permission(grouping,CODE,entity_name,action_name,can_maker_checker)VALUES('portfolio','DELETE_CHITGROUPBID','CHITGROUPBID','DELETE',1);

INSERT INTO m_permission(grouping,CODE,entity_name,action_name,can_maker_checker)VALUES('portfolio','CREATE_CLITRANCHITADV','CLITRANCHITADV','CREATE',1);

CREATE TABLE `fineract_default`.`chit_charge` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(20) NOT NULL,
  `amount` INT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC));

CREATE TABLE `fineract_default`.`chit_subscriber_charge` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `chit_subscriber_id` BIGINT(20) NOT NULL,
  `chit_charge_id` BIGINT(20) NOT NULL,
  `chit_cycle_id` BIGINT(20) NOT NULL,
  `amount` DECIMAL(19,6) NOT NULL,
  `due_date` DATE NULL,
  `is_paid` TINYINT NULL,
  `is_waived` TINYINT NULL,
  PRIMARY KEY (`id`),
  INDEX `chit_subs_chrg_subscriber_idx` (`chit_subscriber_id` ASC),
  INDEX `chit_subs_chrg_charge_idx` (`chit_charge_id` ASC),
  INDEX `chit_subs_chrg_cycle_idx` (`chit_cycle_id` ASC),
  CONSTRAINT `chit_subs_chrg_subscriber`
    FOREIGN KEY (`chit_subscriber_id`)
    REFERENCES `fineract_default`.`chit_group_subscriber` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `chit_subs_chrg_charge`
    FOREIGN KEY (`chit_charge_id`)
    REFERENCES `fineract_default`.`chit_charge` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `chit_subs_chrg_cycle`
    FOREIGN KEY (`chit_cycle_id`)
    REFERENCES `fineract_default`.`chit_group_cycle` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE TABLE `fineract_default`.`chit_demand_schedule` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `chit_subscriber_charge_id` BIGINT(20) NOT NULL,
  `staff_id` BIGINT(20) NOT NULL,
  `demand_date` DATE NOT NULL,
  `installment_amount` DECIMAL(19,6) NOT NULL,
  `due_amount` DECIMAL(19,6) NOT NULL,
  `overdue_amount` DECIMAL(19,6) NOT NULL,
  `penalty_amount` DECIMAL(19,6) NOT NULL,
  `collected_amount` DECIMAL(19,6) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `chit_demand_to_chit_subs_charge_idx` (`chit_subscriber_charge_id` ASC),
  CONSTRAINT `chit_demand_to_chit_subs_charge`
    FOREIGN KEY (`chit_subscriber_charge_id`)
    REFERENCES `fineract_default`.`chit_subscriber_charge` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE TABLE `fineract_default`.`chit_subscriber_transaction` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `chit_demand_schedule_id` BIGINT(20) NULL,
  `chit_subscriber_id` BIGINT(20) NULL,
  `chit_subscriber_charge_id` BIGINT(20) NULL,
  `amount` DECIMAL(19,6) NOT NULL,
  `tran_type_enum` TINYINT(2) NOT NULL,
  `payment_detail_id` BIGINT(20) NOT NULL,
  `transaction_date` DATETIME NOT NULL,
  `is_reversed` TINYINT NULL DEFAULT 0,
  `is_processed` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `chit_tran_to_demand_sch_idx` (`chit_demand_schedule_id` ASC),
  INDEX `chit_tran_to_chit_subs_charg_idx` (`chit_subscriber_charge_id` ASC),
  INDEX `chit_tran_to_chit_subs_idx` (`chit_subscriber_id` ASC),
  CONSTRAINT `chit_tran_to_demand_sch`
    FOREIGN KEY (`chit_demand_schedule_id`)
    REFERENCES `fineract_default`.`chit_demand_schedule` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `chit_tran_to_chit_subs_charg`
    FOREIGN KEY (`chit_subscriber_charge_id`)
    REFERENCES `fineract_default`.`chit_subscriber_charge` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `chit_tran_to_chit_subs`
    FOREIGN KEY (`chit_subscriber_id`)
    REFERENCES `fineract_default`.`chit_group_subscriber` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

ALTER TABLE chit_group_subscriber ADD COLUMN prized_subscriber TINYINT , ADD COLUMN prized_cycle INT;

ALTER TABLE m_payment_detail ADD COLUMN depositedDate DATE,ADD COLUMN transactionNo BIGINT;