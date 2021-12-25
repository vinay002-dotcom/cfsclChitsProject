ALTER TABLE `m_address`
ADD COLUMN taluka_id INT(11) NULL,
ADD COLUMN district_id INT(11) NULL,
ADD COLUMN houseNo VARCHAR(45) NULL;

ALTER TABLE `m_family_members` 
CHANGE COLUMN `qualification` `qualification_cv_id` INT(11) NULL;
ALTER TABLE m_family_members
ADD COLUMN nom_adhar VARCHAR(45) NULL ,
ADD COLUMN nom_secondary_id INT(11),
ADD COLUMN nom_secondary_id_num VARCHAR(45),
ADD COLUMN nom_house_no varchar(45),
ADD COLUMN nom_street_no varchar(45),
ADD COLUMN nom_area_locality VARCHAR(45) ,
ADD COLUMN nom_taluka int(11),
ADD COLUMN nom_district int(11),
ADD COLUMN nom_state int(11),
ADD COLUMN nom_pincode INT(11),
ADD COLUMN is_nominee INT(11),
ADD COLUMN nom_village varchar(50);

ALTER TABLE `m_family_members` 
ADD COLUMN is_nominee_addr INT(11); 

UPDATE m_field_configuration set is_enabled =1  where entity='ADDRESS' AND  subentity='CLIENT' AND  field='townVillage';

INSERT INTO `fineract_default`.`m_client` (`account_no`,`status_enum`,`activation_date`,`office_id`,`firstname`,`display_name`,`is_staff`,`adhar`,`mobile_no`)
VALUES ('001', 300, '2021-07-01', 1, 'Chetana Chits Haveri Pvt Ltd', 'Chetana Chits Haveri Pvt Ltd ',  0,   '111111111111',  '1111111111');

ALTER TABLE `m_office`ADD COLUMN `address` VARCHAR(100) NULL DEFAULT NULL;


INSERT INTO `fineract_default`.`job` (`name`, `display_name`, `cron_expression`, `create_time`, `job_key`) VALUES ('Daily Demand Update', 'Daily Demand Update', '*/5 * * * *', '2021-08-13 13:09:44', 'Daily Demand Update _ DEFAULT');