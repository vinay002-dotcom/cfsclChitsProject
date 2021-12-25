 Alter TABLE `m_client` 
ADD COLUMN `external_idd` VARCHAR(45) NULL,
ADD COLUMN `fsfirstname` VARCHAR(45) NULL ,
ADD COLUMN `maidenname` VARCHAR(45) NULL,
ADD COLUMN `custmothername` VARCHAR(45) NULL,
ADD COLUMN `adhar` VARCHAR(45) NULL,
ADD COLUMN `nrega` VARCHAR(45) NULL,
ADD COLUMN `pan` VARCHAR(45) NULL,
ADD COLUMN `gst_no` VARCHAR(45) NULL,
ADD COLUMN `age` INT NULL,
add column  inc_daily_sales varchar(50)NULL,	
add column  exp_raw_material varchar(50)NULL,		
add column  exp_staff_sal varchar(50)NULL,		
add column  exp_power_telephone varchar(50)NULL,		
add column  exp_repairs_maintainance varchar(50)NULL,		
add column  exp_comm_brokerage varchar(50)NULL,		
add column  exp_interest varchar(50)NULL,		
add column  exp_office_rent varchar(50)NULL,		
add column  exp_travel varchar(50)NULL,		
add column  exp_others varchar(50)NULL,		
add column  tot_business_profit varchar(50)NULL,		
add column  inc_spouse varchar(50)NULL,		
add column  inc_rent varchar(50)NULL,		
add column  inc_interest varchar(50)NULL,		
add column  inc_others varchar(50)NULL,		
add column  tot_house_hold_inc varchar(50)NULL,		
add column  exp_household varchar(50)NULL,		
add column  exp_other_loans varchar(50)NULL,		
add column  tot_net_disp_family varchar(50)NULL,
ADD COLUMN `idproof_no` VARCHAR(50) NULL,
ADD COLUMN `addrproof_no` VARCHAR(50) NULL,
ADD COLUMN `fatherspouse_fs_id` INT(11) NULL,
ADD COLUMN `education_vv_id` INT(11) NULL,
ADD COLUMN `marital_mm_id` INT(11) NULL,
ADD COLUMN `profession_pp_id` INT(11) NULL,
ADD COLUMN `belonging_bb_id` INT(11) NULL,
ADD COLUMN `annual_aa_id` INT(11) NULL,
ADD COLUMN `land_ll_id` INT(11) NULL,
ADD COLUMN `house_hh_id` INT(11) NULL,
ADD COLUMN `form_ff_id` INT(11) NULL,
ADD COLUMN `title_tt_id` INT(11) NULL,
ADD COLUMN `religion_rr_id` INT(11) NULL,
ADD COLUMN `idproof_dp_id` INT(11) NULL,
ADD COLUMN `addrproof_ap_id` INT(11) NULL,
ADD COLUMN `alternatemobileno` VARCHAR(50) NULL,
ADD COLUMN `secidproofno` VARCHAR(50) NULL,
ADD COLUMN  secondary_address_proof_no VARCHAR(50)NULL,
ADD COLUMN `alternateno_id` INT(11) NULL,
ADD COLUMN nom_relationship_id int(11) NULL,
ADD COLUMN nom_gender_id int(11) NULL,
ADD COLUMN nom_age int(11) NULL,
ADD COLUMN nom_profession_id int(11) NULL,
ADD COLUMN nom_education_id int(11)NULL,							
ADD COLUMN nom_marital_id int(11)NULL,
ADD COLUMN nom_date_of_birth DATE NULL,
ADD COLUMN nom_mobile_no varchar(50)NULL,								
ADD COLUMN other_obligations VARCHAR(500)NULL,
ADD COLUMN last_verified_mobile_date DATE,
ADD COLUMN last_verified_adhar_date DATE,
ADD COLUMN last_verified_secondaryid_date DATETIME,
ADD COLUMN last_verified_secondaryid VARCHAR(45),
ADD COLUMN last_verified_adhar VARCHAR(45),
ADD COLUMN other_expenses_tf VARCHAR(50) NULL,ADD COLUMN other_src_inc_tf VARCHAR (50) NULL;

ALTER TABLE fineract_default.m_client_identifier ADD COLUMN is_digiverified TINYINT(4);

ALTER TABLE `m_client_identifier`
	DROP INDEX `unique_active_client_identifier`;


ALTER TABLE `m_client_identifier`
	DROP INDEX `unique_identifier_key`;


ALTER TABLE m_client ADD COLUMN debt BIGINT(11),ADD COLUMN income BIGINT(11),ADD COLUMN debt_inc_ratio DOUBLE(11);
ALTER TABLE m_client ADD COLUMN last_verified_mobile VARCHAR(50);

alter table m_document add column updated_on timestamp not null default current_timestamp on update CURRENT_TIMESTAMP;

INSERT INTO m_code_value (code_id,code_value,order_position,is_active)VALUES((SELECT id FROM m_code WHERE code_name ="Customer Identifier"),"Nominee Adhar",6,1);
INSERT INTO m_code_value (code_id,code_value,order_position,is_active)VALUES((SELECT id FROM m_code WHERE code_name ="Customer Identifier"),"Nominee SecondaryId",7,1);


ALTER TABLE `m_client` CHANGE COLUMN `debt_inc_ratio` `debt_inc_ratio` DOUBLE NULL DEFAULT NULL AFTER `income`;

ALTER TABLE `m_client_transaction` ADD COLUMN `is_processed` TINYINT NULL DEFAULT '0' ;
