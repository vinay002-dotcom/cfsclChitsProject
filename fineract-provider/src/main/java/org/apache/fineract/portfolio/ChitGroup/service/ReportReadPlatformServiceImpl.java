/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.ChitGroup.service;



import java.util.ArrayList;



import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.infrastructure.security.utils.ColumnValidator;

import org.apache.fineract.portfolio.ChitGroup.exception.ChitGroupNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class ReportReadPlatformServiceImpl implements ReportReadPlatformService
{
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final ColumnValidator columnValidator;
	private final FromJsonHelper fromJsonHelper;
	@Autowired
	public ReportReadPlatformServiceImpl(PlatformSecurityContext context,
			ColumnValidator columnValidator,final RoutingDataSource dataSource,final FromJsonHelper fromJsonHelper) {
	
		this.jdbcTemplate = new JdbcTemplate(dataSource);
		this.context = context;
		this.columnValidator = columnValidator;
		this.fromJsonHelper = fromJsonHelper;
	}


		
	@Override
	public JsonObject retrievereportDailyCollection(Long officeId,String date) {
		
		try {
			JsonObject dataTobeSent = new JsonObject();
			final String sql = "SELECT\r\n"
					+ "of.name AS 'BranchName', cg.name AS 'GroupName', \r\n"
					+ "cg.start_date AS 'GroupStartDate', cds.demand_date AS 'ChitDate', \r\n"
					+ "cg.chit_duration AS 'TotalNoOfCustomers', \r\n"
					+ "(SUM(cds.installment_amount)) AS 'DailyDemand',\r\n"
					+ "(SUM(cds.collected_amount)) AS 'DailyCollection',\r\n"
					+ "((SUM(cds.installment_amount)) - SUM(cds.collected_amount)) AS 'ShortFallAdvance',\r\n"
					+ "(((SUM(cds.collected_amount))/(SUM(cds.installment_amount))) * 100) AS 'Percentage'\r\n"
					+ "\r\n"
					+ "FROM m_office of\r\n"
					+ "JOIN chit_group cg on cg.branch_id = of.id\r\n"
					+ "JOIN chit_demand_schedule cds ON cds.chit_id = cg.id \r\n"
					+ "INNER JOIN chit_subscriber_charge cs ON cs.id = cds.chit_subscriber_charge_id\r\n"
					+ "INNER JOIN chit_group_subscriber cgs ON cgs.id = cs.chit_subscriber_id\r\n"
					+ "\r\n"
					+ "WHERE cds.demand_date = DATE('"+date+"') AND cg.`status` = 20 AND cgs.chit_number!=1 AND of.id = "+officeId+" \r\n"
					+ "AND (of.hierarchy LIKE CONCAT((\r\n"
					+ "SELECT ino.hierarchy\r\n"
					+ "FROM m_office ino\r\n"
					+ "WHERE ino.id = "+officeId+"),\"%\"))\r\n"
					+ "GROUP BY cg.name";
			final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, new Object[] {});
			ArrayList<JsonObject> arr = new ArrayList<JsonObject>(); 

			while (rs.next()) {	
				JsonObject json = new JsonObject();
				json.addProperty("BranchName", rs.getString("BranchName"));
				json.addProperty("chitGroup", rs.getString("GroupName"));
				json.addProperty("ChitDate", rs.getString("ChitDate"));
				json.addProperty("DailyDemand", rs.getBigDecimal("DailyDemand"));
				json.addProperty("DailyCollection", rs.getBigDecimal("DailyCollection"));
				json.addProperty("Percentage", rs.getBigDecimal("Percentage"));
				json.addProperty("ShortFallAdvance", rs.getBigDecimal("ShortFallAdvance"));
				json.addProperty("TotalNoOfCustomers", rs.getInt("TotalNoOfCustomers"));
				json.addProperty("GroupStartDate", rs.getString("GroupStartDate"));
				arr.add(json);
			}
			JsonElement parse = fromJsonHelper.parse(arr.toString());
			
			dataTobeSent.add("TodaysDemand", parse);
			
			ArrayList<JsonObject> arr1 = new ArrayList<JsonObject>();
			final String sql1 = "SELECT\r\n"
					+ "of.name AS 'BranchName', cg.name AS 'GroupName', \r\n"
					+ "cg.start_date AS 'GroupStartDate', cds.demand_date AS 'ChitDate', \r\n"
					+ "cg.chit_duration AS 'TotalNoOfCustomers', \r\n"
					+ "(SUM(cds.installment_amount) + (SELECT MAX(cd.due_amount) FROM chit_demand_schedule cd \r\n"
					+ "WHERE cd.demand_date = MAX(cds.demand_date) )  + SUM(cds.overdue_amount)) DailyDemand,\r\n"
					+ "SUM(cds.collected_amount) AS DailyCollection,\r\n"
					+ "((SUM(cds.installment_amount) + (cds.due_amount) + SUM(cds.overdue_amount)) - (SUM(cds.collected_amount))) AS ShortFallAdvance,\r\n"
					+ "(((SUM(cds.collected_amount))/(SUM(cds.installment_amount) + (SELECT MAX(cd.due_amount) FROM chit_demand_schedule cd \r\n"
					+ "WHERE cd.demand_date = MAX(cds.demand_date) )  + SUM(cds.overdue_amount))) * 100) AS Percentage\r\n"
					+ "\r\n"
					+ "FROM m_office of\r\n"
					+ "INNER JOIN chit_group cg on cg.branch_id = of.id\r\n"
					+ "INNER JOIN chit_demand_schedule cds ON cds.chit_id = cg.id\r\n"
					+ "INNER JOIN chit_subscriber_charge cs ON cs.id = cds.chit_subscriber_charge_id\r\n"
					+ "INNER JOIN chit_group_subscriber cgs ON cgs.id = cs.chit_subscriber_id\r\n"
					+ "\r\n"
					+ "WHERE Month(cds.demand_date) = Month(DATE('"+date+"')) AND cds.demand_date <= DATE('"+date+"') AND cg.`status` = 20 \r\n"
					+ "AND  of.id = "+officeId+" AND cgs.chit_number!= 1 \r\n"
					+ "AND (of.hierarchy LIKE CONCAT((\r\n"
					+ "SELECT ino.hierarchy\r\n"
					+ "FROM m_office ino\r\n"
					+ "WHERE ino.id =  "+officeId+"),\"%\"))\r\n"
					+ "GROUP BY cg.name";
			final SqlRowSet rs1 = this.jdbcTemplate.queryForRowSet(sql1, new Object[] {});
			
			while (rs1.next()) {	
				JsonObject json = new JsonObject();
				//json.addProperty("Type", "CurrentMonth");
				json.addProperty("BranchName", rs1.getString("BranchName"));
				json.addProperty("chitGroup", rs1.getString("GroupName"));
				json.addProperty("DailyDemand", rs1.getBigDecimal("DailyDemand"));
				json.addProperty("DailyCollection", rs1.getBigDecimal("DailyCollection"));
				json.addProperty("Percentage", rs1.getBigDecimal("Percentage"));
				json.addProperty("ShortFallAdvance", rs1.getBigDecimal("ShortFallAdvance"));
				arr1.add(json);
			}
			JsonElement parse1 = fromJsonHelper.parse(arr1.toString());
			dataTobeSent.add("CurrentMonth", parse1);
			
			ArrayList<JsonObject> arr2 = new ArrayList<JsonObject>();
			final String sql2 = "SELECT\r\n"
					+ "of.name AS 'BranchName', cg.name AS 'GroupName', \r\n"
					+ "cg.start_date AS 'GroupStartDate', cds.demand_date AS 'ChitDate', \r\n"
					+ "cg.chit_duration AS 'TotalNoOfCustomers', \r\n"
					+ "(SUM(cds.installment_amount) + MAX(cds.due_amount) + MAX(cds.overdue_amount)) 'DailyDemand',\r\n"
					+ "(SUM(cds.collected_amount)) AS DailyCollection,\r\n"
					+ "((SUM(cds.installment_amount) + MAX(cds.due_amount) + MAX(cds.overdue_amount))-(SUM(cds.collected_amount))) AS ShortFallAdvance,\r\n"
					+ "((SUM(cds.collected_amount))/((SUM(cds.installment_amount) + MAX(cds.due_amount) + MAX(cds.overdue_amount)))*100) AS Percentage\r\n"
					+ "\r\n"
					+ "FROM m_office of\r\n"
					+ "INNER JOIN chit_group cg on cg.branch_id = of.id\r\n"
					+ "INNER JOIN chit_demand_schedule cds ON cds.chit_id = cg.id\r\n"
					+ "INNER JOIN chit_subscriber_charge cs ON cs.id = cds.chit_subscriber_charge_id \r\n"
					+ "INNER JOIN chit_group_subscriber cgs ON cgs.id = cs.chit_subscriber_id \r\n"
					+ "\r\n"
					+ "WHERE cds.demand_date <= DATE('"+date+"') AND cg.`status` = 20 AND of.id = "+officeId+" AND cgs.chit_number!= 1 \r\n"
					+ "AND (of.hierarchy LIKE CONCAT((\r\n"
					+ "SELECT ino.hierarchy\r\n"
					+ "FROM m_office ino\r\n"
					+ "WHERE ino.id = "+officeId+"),\"%\"))\r\n"
					+ "GROUP BY cg.name";
			final SqlRowSet rs2 = this.jdbcTemplate.queryForRowSet(sql2, new Object[] {});
			
			while (rs2.next()) {	
				JsonObject json = new JsonObject();
			//	json.addProperty("Type", "TillToday");
				json.addProperty("BranchName", rs2.getString("BranchName"));
				json.addProperty("chitGroup", rs2.getString("GroupName"));
				json.addProperty("DailyDemand", rs2.getBigDecimal("DailyDemand"));
				json.addProperty("DailyCollection", rs2.getBigDecimal("DailyCollection"));
				json.addProperty("Percentage", rs2.getBigDecimal("Percentage"));
				json.addProperty("ShortFallAdvance", rs2.getBigDecimal("ShortFallAdvance"));
				arr2.add(json);
			}
			JsonElement parse2 = fromJsonHelper.parse(arr2.toString());
			dataTobeSent.add("TillToday", parse2);
			
			return dataTobeSent;
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(null, e);
		}
			
	}
	
	@Override
	public JsonObject retrievereportNPSBucketWise(Long officeId,String date) 
	{
		
		try {
			
			JsonObject dataTobeSent = new JsonObject();
			final String sql =  "SELECT o.name AS 'BranchName'\r\n"
					+ ", IF(cgs.prized_subscriber = 1, 'PS','NPS') AS 'STATUS'\r\n"
					+ ", cg.start_date AS 'GroupStartDate'\r\n"
					+ ", CONCAT(cg.name,'/',cgs.chit_number) AS 'ChitGroupWithTicketNumber' \r\n"
					+ ", c.display_name AS 'SubscriberName'\r\n"
					+ ", (SUM(cds.installment_amount) + \r\n"
					+ "  (SELECT cd.due_amount FROM chit_demand_schedule cd  WHERE cd.demand_date = DATE('"+date+"') AND cd.chit_subscriber_charge_id = cds.chit_subscriber_charge_id) + \r\n"
					+ "  (SELECT cd.overdue_amount FROM chit_demand_schedule cd  WHERE cd.demand_date = DATE('"+date+"') AND cd.chit_subscriber_charge_id = cds.chit_subscriber_charge_id)) AS 'DemandAmount' \r\n"
					+ "\r\n"
					+ ", SUM(cds.collected_amount) AS 'CollectedAmount'\r\n"
					+ ", (SELECT COUNT(rcb.collected_amount) \r\n"
					+ "    FROM chit_demand_schedule rcb \r\n"
					+ "	 WHERE rcb.demand_date = DATE('"+date+"') AND rcb.collected_amount>0.0 ) AS 'NumberOfCollectedInst'\r\n"
					+ ", (SELECT COUNT(rcb.due_amount) \r\n"
					+ "    FROM chit_demand_schedule rcb \r\n"
					+ "	 WHERE rcb.demand_date = DATE('"+date+"') AND rcb.due_amount>0.0 ) AS 'NumberOfDueInst' \r\n"
					+ ",(SELECT cc.due_amount FROM chit_demand_schedule cc WHERE cc.demand_date = DATE('"+date+"') AND cc.chit_subscriber_charge_id = cds.chit_subscriber_charge_id) AS 'DueAmount'\r\n"
					+ ",(SELECT cd.penalty_amount FROM chit_demand_schedule cd WHERE cd.demand_date = DATE('"+date+"') AND cd.chit_subscriber_charge_id = cds.chit_subscriber_charge_id) AS 'PenaltyAmount'\r\n"
					+ ", (IF(DATEDIFF(DATE('"+date+"'), cg.start_date) BETWEEN 1 AND 7,(SELECT cd.due_amount FROM chit_demand_schedule cd  WHERE cd.demand_date = DATE('"+date+"') AND cd.chit_subscriber_charge_id = cds.chit_subscriber_charge_id),'0')) AS '1 - 7 Days'\r\n"
					+ ", (IF(DATEDIFF(DATE('"+date+"'), cg.start_date) BETWEEN 8 AND 15,(SELECT cd.due_amount FROM chit_demand_schedule cd WHERE cd.demand_date = DATE('"+date+"') AND cd.chit_subscriber_charge_id = cds.chit_subscriber_charge_id),'0')) AS '8 - 15 Days'\r\n"
					+ ", (IF(DATEDIFF(DATE('"+date+"'), cg.start_date) BETWEEN 16 AND 30,(SELECT cd.due_amount FROM chit_demand_schedule cd WHERE cd.demand_date = DATE('"+date+"') AND cd.chit_subscriber_charge_id = cds.chit_subscriber_charge_id),'0')) AS '16 - 30 Days'\r\n"
					+ ", (IF(DATEDIFF(DATE('"+date+"'), cg.start_date) BETWEEN 31 AND 90,(SELECT cd.due_amount FROM chit_demand_schedule cd WHERE cd.demand_date = DATE('"+date+"') AND cd.chit_subscriber_charge_id = cds.chit_subscriber_charge_id),'0')) AS '31 - 90 Days'\r\n"
					+ ", (IF(DATEDIFF(DATE('"+date+"'), cg.start_date) BETWEEN 91 AND 180,(SELECT cd.due_amount FROM chit_demand_schedule cd WHERE cd.demand_date = DATE('"+date+"') AND cd.chit_subscriber_charge_id = cds.chit_subscriber_charge_id),'0')) AS '91 - 180 Days'\r\n"
					+ ", (IF(DATEDIFF(DATE('"+date+"'), cg.start_date) BETWEEN 180 AND 365,(SELECT cd.due_amount FROM chit_demand_schedule cd WHERE cd.demand_date = DATE('"+date+"') AND cd.chit_subscriber_charge_id = cds.chit_subscriber_charge_id),'0')) AS 'Above 180 Days'\r\n"
					+ "\r\n"
					+ "FROM m_office o\r\n"
					+ "INNER JOIN chit_group cg ON cg.branch_id = o.id\r\n"
					+ "INNER JOIN chit_demand_schedule cds ON cds.chit_id = cg.id\r\n"
					+ "INNER JOIN chit_subscriber_charge csg on csg.id = cds.chit_subscriber_charge_id\r\n"
					+ "INNER JOIN chit_group_subscriber cgs ON cgs.id = csg.chit_subscriber_id\r\n"
					+ "INNER JOIN m_client c ON c.id = cgs.client_id\r\n"
					+ "\r\n"
					+ "WHERE o.id = "+officeId+"\r\n"
					+ "AND cg.status = 20 \r\n"
					+ "AND cds.demand_date <= DATE('"+date+"') \r\n"
					+ "AND cgs.prized_subscriber =  0\r\n"
					+ "AND c.account_no != '001'\r\n"
					+ "AND (o.hierarchy LIKE CONCAT((\r\n"
					+ "SELECT ino.hierarchy\r\n"
					+ "FROM m_office ino\r\n"
					+ "WHERE ino.id = "+officeId+"),\"%\"))\r\n"
					+ "\r\n"
					+ "GROUP BY \r\n"
					+ "c.display_name\r\n"
					+ "\r\n"
					+ "ORDER BY \r\n"
					+ "ChitGroupWithTicketNumber\r\n";
			final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, new Object[] {});
			ArrayList<JsonObject> arr = new ArrayList<JsonObject>(); 

			while (rs.next()) {	
				Double Days30 = 0.0;
				Double Days30to60 = 0.0;
				Double Daysto60 = 0.0;
				Double daysto100 = 0.0;
				Double days8to15 = 0.0;
				if(rs.getBigDecimal("16 - 30 Days")!=null)
				{
					Days30 = rs.getBigDecimal("16 - 30 Days").doubleValue();
				}
				if(rs.getBigDecimal("31 - 90 Days")!=null)
				{
					Days30to60 = rs.getBigDecimal("31 - 90 Days").doubleValue();
				}
				if(rs.getBigDecimal("91 - 180 Days")!=null)
				{
					Daysto60 = rs.getBigDecimal("91 - 180 Days").doubleValue();
				}
				if(rs.getBigDecimal("Above 180 Days")!=null)
				{
					daysto100 = rs.getBigDecimal("Above 180 Days").doubleValue();
				}
				if(rs.getBigDecimal("8 - 15 Days")!=null)
				{
					days8to15 = rs.getBigDecimal("8 - 15 Days").doubleValue();
				}
				Double days1to17 = 0.0;
				if(rs.getBigDecimal("1 - 7 Days")!=null)
				{
					days1to17 = rs.getBigDecimal("1 - 7 Days").doubleValue();
				}
				JsonObject json = new JsonObject();
				json.addProperty("BranchName", rs.getString("BranchName"));
				json.addProperty("STATUS",rs.getString("STATUS"));
				json.addProperty("GroupStartDate", rs.getString("GroupStartDate"));
				json.addProperty("ChitGroupWithTicketNumber", rs.getString("ChitGroupWithTicketNumber"));
				json.addProperty("SubscriberName", rs.getString("SubscriberName"));
				json.addProperty("DemandAmount", rs.getBigDecimal("DemandAmount"));
				json.addProperty("CollectedAmount", rs.getBigDecimal("CollectedAmount"));
				json.addProperty("NumberOfCollectedInst", rs.getLong("NumberOfCollectedInst"));
				json.addProperty("NumberOfDueInst", rs.getLong("NumberOfDueInst"));
				json.addProperty("DueAmount", rs.getBigDecimal("DueAmount"));
				json.addProperty("PenaltyAmount", rs.getBigDecimal("PenaltyAmount"));
				json.addProperty("Days_1_to_7", days1to17);
				json.addProperty("Days_8_to_15", days8to15);
				json.addProperty("Days_16_to_30", Days30);
				json.addProperty("Days_31_to_90", Days30to60);
				json.addProperty("Days_91_to_180", Daysto60);
				json.addProperty("Above_180_Days", daysto100);
				arr.add(json);
			}
			JsonElement parse = fromJsonHelper.parse(arr.toString());
			dataTobeSent.add("Non-Prized Subscribers", parse);	
			return dataTobeSent;
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(null, e);
		}
	}
	
	@Override
	public JsonObject retrievereportPSBucketWise(Long officeId,String date)
	{
		try {
			JsonObject dataTobeSent = new JsonObject();
			final String sql = "SELECT o.name AS 'BranchName'\r\n"
					+ ", IF(cgs.prized_subscriber = 1, 'PS','NPS') AS 'STATUS'\r\n"
					+ ", cg.start_date AS 'GroupStartDate'\r\n"
					+ ", CONCAT(cg.name,'/',cgs.chit_number) AS 'ChitGroupWithTicketNumber' \r\n"
					+ ", c.display_name AS 'SubscriberName'\r\n"
					+ ", (SUM(cds.installment_amount) + \r\n"
					+ "  (SELECT cd.due_amount FROM chit_demand_schedule cd  WHERE cd.demand_date = DATE('"+date+"') AND cd.chit_subscriber_charge_id = cds.chit_subscriber_charge_id) + \r\n"
					+ "  (SELECT cd.overdue_amount FROM chit_demand_schedule cd  WHERE cd.demand_date = DATE('"+date+"') AND cd.chit_subscriber_charge_id = cds.chit_subscriber_charge_id)) AS 'DemandAmount' \r\n"
					+ "\r\n"
					+ ", SUM(cds.collected_amount) AS 'CollectedAmount'\r\n"
					+ ", (SELECT COUNT(rcb.collected_amount) \r\n"
					+ "    FROM chit_demand_schedule rcb \r\n"
					+ "	 WHERE rcb.demand_date = DATE('"+date+"') AND rcb.collected_amount>0.0 ) AS 'NumberOfCollectedInst'\r\n"
					+ ", (SELECT COUNT(rcb.due_amount) \r\n"
					+ "    FROM chit_demand_schedule rcb \r\n"
					+ "	 WHERE rcb.demand_date = DATE('"+date+"') AND rcb.due_amount>0.0 ) AS 'NumberOfDueInst' \r\n"
					+ ",(SELECT cc.due_amount FROM chit_demand_schedule cc WHERE cc.demand_date = DATE('"+date+"') AND cc.chit_subscriber_charge_id = cds.chit_subscriber_charge_id) AS 'DueAmount'\r\n"
					+ ",(SELECT cd.penalty_amount FROM chit_demand_schedule cd WHERE cd.demand_date = DATE('"+date+"') AND cd.chit_subscriber_charge_id = cds.chit_subscriber_charge_id) AS 'PenaltyAmount'\r\n"
					+ ", (IF(DATEDIFF(DATE('"+date+"'), cg.start_date) BETWEEN 1 AND 7,(SELECT cd.due_amount FROM chit_demand_schedule cd  WHERE cd.demand_date = DATE('"+date+"') AND cd.chit_subscriber_charge_id = cds.chit_subscriber_charge_id),'0')) AS '1 - 7 Days'\r\n"
					+ ", (IF(DATEDIFF(DATE('"+date+"'), cg.start_date) BETWEEN 8 AND 15,(SELECT cd.due_amount FROM chit_demand_schedule cd WHERE cd.demand_date = DATE('"+date+"') AND cd.chit_subscriber_charge_id = cds.chit_subscriber_charge_id),'0')) AS '8 - 15 Days'\r\n"
					+ ", (IF(DATEDIFF(DATE('"+date+"'), cg.start_date) BETWEEN 16 AND 30,(SELECT cd.due_amount FROM chit_demand_schedule cd WHERE cd.demand_date = DATE('"+date+"') AND cd.chit_subscriber_charge_id = cds.chit_subscriber_charge_id),'0')) AS '16 - 30 Days'\r\n"
					+ ", (IF(DATEDIFF(DATE('"+date+"'), cg.start_date) BETWEEN 31 AND 90,(SELECT cd.due_amount FROM chit_demand_schedule cd WHERE cd.demand_date = DATE('"+date+"') AND cd.chit_subscriber_charge_id = cds.chit_subscriber_charge_id),'0')) AS '31 - 90 Days'\r\n"
					+ ", (IF(DATEDIFF(DATE('"+date+"'), cg.start_date) BETWEEN 91 AND 180,(SELECT cd.due_amount FROM chit_demand_schedule cd WHERE cd.demand_date = DATE('"+date+"') AND cd.chit_subscriber_charge_id = cds.chit_subscriber_charge_id),'0')) AS '91 - 180 Days'\r\n"
					+ ", (IF(DATEDIFF(DATE('"+date+"'), cg.start_date) BETWEEN 180 AND 365,(SELECT cd.due_amount FROM chit_demand_schedule cd WHERE cd.demand_date = DATE('"+date+"') AND cd.chit_subscriber_charge_id = cds.chit_subscriber_charge_id),'0')) AS 'Above 180 Days'\r\n"
					+ "\r\n"
					+ "FROM m_office o\r\n"
					+ "INNER JOIN chit_group cg ON cg.branch_id = o.id\r\n"
					+ "INNER JOIN chit_demand_schedule cds ON cds.chit_id = cg.id\r\n"
					+ "INNER JOIN chit_subscriber_charge csg on csg.id = cds.chit_subscriber_charge_id\r\n"
					+ "INNER JOIN chit_group_subscriber cgs ON cgs.id = csg.chit_subscriber_id\r\n"
					+ "INNER JOIN m_client c ON c.id = cgs.client_id\r\n"
					+ "\r\n"
					+ "WHERE o.id = "+officeId+"\r\n"
					+ "AND cg.status = 20 \r\n"
					+ "AND cds.demand_date <= DATE('"+date+"') \r\n"
					+ "AND cgs.prized_subscriber =  1\r\n"
					+ "AND c.account_no != '001'\r\n"
					+ "AND (o.hierarchy LIKE CONCAT((\r\n"
					+ "SELECT ino.hierarchy\r\n"
					+ "FROM m_office ino\r\n"
					+ "WHERE ino.id = "+officeId+"),\"%\"))\r\n"
					+ "\r\n"
					+ "GROUP BY \r\n"
					+ "c.display_name\r\n"
					+ "\r\n"
					+ "ORDER BY \r\n"
					+ "ChitGroupWithTicketNumber\r\n";
			final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, new Object[] {});
			ArrayList<JsonObject> arr = new ArrayList<JsonObject>(); 

			while (rs.next()) {	
				Double Days30 = 0.0;
				Double Days30to60 = 0.0;
				Double Daysto60 = 0.0;
				Double daysto100 = 0.0;
				Double days8to15 = 0.0;
				if(rs.getBigDecimal("16 - 30 Days")!=null)
				{
					Days30 = rs.getBigDecimal("16 - 30 Days").doubleValue();
				}
				if(rs.getBigDecimal("31 - 90 Days")!=null)
				{
					Days30to60 = rs.getBigDecimal("31 - 90 Days").doubleValue();
				}
				if(rs.getBigDecimal("91 - 180 Days")!=null)
				{
					Daysto60 = rs.getBigDecimal("91 - 180 Days").doubleValue();
				}
				if(rs.getBigDecimal("Above 180 Days")!=null)
				{
					daysto100 = rs.getBigDecimal("Above 180 Days").doubleValue();
				}
				if(rs.getBigDecimal("8 - 15 Days")!=null)
				{
					days8to15 = rs.getBigDecimal("8 - 15 Days").doubleValue();
				}
				Double days1to17 = 0.0;
				if(rs.getBigDecimal("1 - 7 Days")!=null)
				{
					days1to17 = rs.getBigDecimal("1 - 7 Days").doubleValue();
				}
				JsonObject json = new JsonObject();
				json.addProperty("BranchName", rs.getString("BranchName"));
				json.addProperty("STATUS",rs.getString("STATUS"));
				json.addProperty("GroupStartDate", rs.getString("GroupStartDate"));
				json.addProperty("ChitGroupWithTicketNumber", rs.getString("ChitGroupWithTicketNumber"));
				json.addProperty("SubscriberName", rs.getString("SubscriberName"));
				json.addProperty("DemandAmount", rs.getBigDecimal("DemandAmount"));
				json.addProperty("CollectedAmount", rs.getBigDecimal("CollectedAmount"));
				json.addProperty("NumberOfCollectedInst", rs.getLong("NumberOfCollectedInst"));
				json.addProperty("NumberOfDueInst", rs.getLong("NumberOfDueInst"));
				json.addProperty("DueAmount", rs.getBigDecimal("DueAmount"));
				json.addProperty("PenaltyAmount", rs.getBigDecimal("PenaltyAmount"));
				json.addProperty("Days_1_to_7", days1to17);
				json.addProperty("Days_8_to_15", days8to15);
				json.addProperty("Days_16_to_30", Days30);
				json.addProperty("Days_31_to_90", Days30to60);
				json.addProperty("Days_91_to_180", Daysto60);
				json.addProperty("Above_180_Days", daysto100);
				arr.add(json);
			}
			JsonElement parse = fromJsonHelper.parse(arr.toString());
			dataTobeSent.add("Prized Subscribers", parse);	
			return dataTobeSent;
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(null, e);
		}
	}
	
	@Override
	public JsonObject retrievereportNonStartedChitGroup(Long officeId,String date)
	{
		try {
			JsonObject dataTobeSent = new JsonObject();
			final String sql = "SELECT ounder.name AS BranchName, cli.id,cli.display_name AS CustomerName, \r\n"
					+ "	\r\n"
					+ "	((ncg.chit_value/ncg.chit_duration)/25)as Demand ,\r\n"
					+ "	ct.amount AS Collection,\r\n"
					+ "	(((ncg.chit_value/ncg.chit_duration)/25)-(ct.amount)) as ShortFall,\r\n"
					+ "	((((ncg.chit_value/ncg.chit_duration)/25)/(ct.amount))*100) AS Percentage\r\n"
					+ "	\r\n"
					+ "	\r\n"
					+ "	FROM m_office o\r\n"
					+ "	join m_office ounder on ounder.hierarchy like concat(o.hierarchy, '%')\r\n"
					+ "	and ounder.hierarchy like CONCAT('.', '%')\r\n"
					+ "	LEFT JOIN m_client cli ON cli.office_id = ounder.id\r\n"
					+ "	LEFT JOIN ns_chit_group ncg ON ncg.id = cli.amt_applied\r\n"
					+ "	LEFT JOIN m_client_transaction ct ON ct.client_id = cli.id\r\n"
					+ "	\r\n"
					+ "	WHERE o.id = " + officeId + " AND cli.account_no!= 001 and ct.transaction_type_enum = 3 \r\n"
					+ "	AND ct.adjusted = 0 and ct.is_processed = 1 and ct.transaction_date <= DATE('"+date+"') \r\n"
					+ "	\r\n"
					+ "	GROUP BY cli.firstname";
								
			final SqlRowSet rs = this.jdbcTemplate.queryForRowSet(sql, new Object[] {});
			ArrayList<JsonObject> arr = new ArrayList<JsonObject>(); 

			while (rs.next()) {	
				JsonObject json = new JsonObject();
				json.addProperty("BranchName", rs.getString("BranchName"));
				json.addProperty("CustomerName",rs.getString("CustomerName"));
				json.addProperty("DailyDemand", rs.getBigDecimal("Demand"));
				json.addProperty("CollectionAsOnToday", rs.getBigDecimal("Collection"));
				json.addProperty("ShortFall", rs.getBigDecimal("ShortFall"));
				json.addProperty("Percentage", rs.getBigDecimal("Percentage"));
				arr.add(json);
			}
			JsonElement parse = fromJsonHelper.parse(arr.toString());
			dataTobeSent.add("AsOnDate", parse);	
			
			
			final String sqls = "SELECT ounder.name AS BranchName, cli.id,cli.display_name AS CustomerName, \r\n"
					+ "	\r\n"
					+ "	((ncg.chit_value/ncg.chit_duration)/25)as Demand ,\r\n"
					+ "	ct.amount AS Collection,\r\n"
					+ "	(((ncg.chit_value/ncg.chit_duration)/25)-(ct.amount)) as ShortFall,\r\n"
					+ "	((((ncg.chit_value/ncg.chit_duration)/25)/(ct.amount))*100) AS Percentage\r\n"
					+ "	\r\n"
					+ "	\r\n"
					+ "	FROM m_office o\r\n"
					+ "	join m_office ounder on ounder.hierarchy like concat(o.hierarchy, '%')\r\n"
					+ "	and ounder.hierarchy like CONCAT('.', '%')\r\n"
					+ "	LEFT JOIN m_client cli ON cli.office_id = ounder.id\r\n"
					+ "	LEFT JOIN ns_chit_group ncg ON ncg.id = cli.amt_applied\r\n"
					+ "	LEFT JOIN m_client_transaction ct ON ct.client_id = cli.id\r\n"
					+ "	\r\n"
					+ "	WHERE o.id = " + officeId + " AND cli.account_no!= 001 and ct.transaction_type_enum = 3 \r\n"
					+ "	AND ct.adjusted = 0 and ct.is_processed = 1 and ct.transaction_date = DATE('"+date+"') \r\n"
					+ "	\r\n"
					+ "	GROUP BY cli.firstname";			
			
			final SqlRowSet rs1 = this.jdbcTemplate.queryForRowSet(sqls, new Object[] {});
			ArrayList<JsonObject> arr1 = new ArrayList<JsonObject>(); 

			while (rs1.next()) {	
				JsonObject json = new JsonObject();
				json.addProperty("BranchName", rs1.getString("BranchName"));
				json.addProperty("CustomerName",rs1.getString("CustomerName"));
				json.addProperty("DailyDemand", rs1.getBigDecimal("Demand"));
				json.addProperty("CollectionAsOnToday", rs1.getBigDecimal("Collection"));
				json.addProperty("ShortFall", rs1.getBigDecimal("ShortFall"));
				json.addProperty("Percentage", rs1.getBigDecimal("Percentage"));
				arr1.add(json);
			}
			JsonElement parse1 = fromJsonHelper.parse(arr1.toString());
			dataTobeSent.add("TodayDate", parse1);	
			return dataTobeSent;
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(null, e);
		}
	}
	
	@Override
	public
	JsonObject retrievereportDailyCollectionOne(Long officeId,String date) {
		
		try {
			JsonObject dataTobeSent = new JsonObject();
			
			final String sql = " SELECT   ounder.name as BranchName, cg.name as chitGroup,  cg.chit_value as chitValue,\r\n"
					+ "	cg.start_date as groupStartDate,  cgs.chit_number as ticketNumber, \r\n"
					+ "	c.display_name AS SubscriberName,\r\n"
					+ "	\r\n"
					+ "	if(cgs.prized_subscriber= 1, 'PS', 'NPS') AS 'STATUS',\r\n"
					+ "	cgb.bid_date AS 'BidDate',  cgb.bid_amount AS 'BidAmount',		\r\n"
					+ "	cst.transaction_date AS 'PrizeMoneyPaidDate',\r\n"
					+ "	\r\n"
					+ "	SUM(cds.installment_amount) AS 'DemandAmount',\r\n"
					+ "	SUM(cds.collected_amount ) AS 'CollectedAmount',\r\n"
					+ "	(SUM(cds.installment_amount)-SUM(cds.collected_amount)) AS 'DueAmount',\r\n"
					+ "	cg.current_cycle AS 'TotalInstaDemand',\r\n"
					+ "	\r\n"
					+ "	(cg.chit_duration - cg.current_cycle) AS 'FutureInsta',\r\n"
					+ "	(cg.chit_value - SUM(cds.collected_amount)) AS 'FutureInstAmount'\r\n"
					+ "	\r\n"
					+ "	FROM m_office o\r\n"
					+ "	join m_office ounder on ounder.hierarchy like concat(o.hierarchy, '%')\r\n"
					+ "	and ounder.hierarchy like CONCAT('.', '%')\r\n"
					+ "	INNER JOIN chit_group cg  ON cg.branch_id = ounder.id\r\n"
					+ "	INNER JOIN chit_demand_schedule cds ON cds.chit_id = cg.id\r\n"
					+ "	INNER JOIN chit_subscriber_charge csg on csg.id = cds.chit_subscriber_charge_id\r\n"
					+ "	INNER JOIN chit_group_cycle cgc ON cgc.id = csg.chit_cycle_id\r\n"
					+ "	INNER JOIN chit_group_bids cgb ON cgb.chit_cycle_id = cgc.id\r\n"
					+ "	INNER JOIN chit_group_subscriber cgs ON cgs.id = csg.chit_subscriber_id\r\n"
					+ "	INNER JOIN m_client c ON c.id = cgs.client_id\r\n"
					+ "	LEFT OUTER JOIN chit_subscriber_transaction cst ON cgs.id = cst.chit_subscriber_id\r\n"
					+ "	\r\n"
					+ "	where o.id = "+ officeId +"	AND cg.status = 20 \r\n"
					+ "	AND cgs.prized_subscriber =  1 AND c.account_no != '001' AND cds.demand_date <= DATE('"+date+"')  \r\n"
					+ "	\r\n"
					+ "	GROUP BY c.id "
					+ " order by ounder.hierarchy, cg.start_date ";
			final SqlRowSet srd = this.jdbcTemplate.queryForRowSet(sql, new Object[] {});

			ArrayList<JsonObject> arr3 = new ArrayList<JsonObject>(); 
			while(srd.next())
			{	
				JsonObject json = new JsonObject();
				json.addProperty("BranchName", srd.getString("BranchName"));
				json.addProperty("ChitGroup", srd.getString("chitGroup"));
				json.addProperty("ChitValue", srd.getInt("chitValue"));
				json.addProperty("GroupStartDate", srd.getString("groupStartDate"));
				json.addProperty("TicketNumber", srd.getInt("ticketNumber"));
				json.addProperty("SubscriberName", srd.getString("SubscriberName"));
				json.addProperty("BidDate", srd.getString("BidDate"));
				
				json.addProperty("STATUS",srd.getString("STATUS"));
				json.addProperty("BidAmount", srd.getInt("BidAmount"));
				json.addProperty("PrizeMoneyPaidDate", srd.getString("PrizeMoneyPaidDate"));
				
				json.addProperty("DemandAmount", srd.getBigDecimal("DemandAmount"));
				json.addProperty("CollectedAmount", srd.getBigDecimal("CollectedAmount"));
				json.addProperty("DueAmount", srd.getBigDecimal("DueAmount"));
				json.addProperty("TotalInstaDemand", srd.getInt("TotalInstaDemand"));
				
				json.addProperty("FutureInsta", srd.getInt("FutureInsta"));
				json.addProperty("FutureInstAmount", srd.getDouble("FutureInstAmount"));
				
				arr3.add(json);
			}
			
			JsonElement parse1 = fromJsonHelper.parse(arr3.toString());
			
			dataTobeSent.add("PSReport", parse1);
			
			ArrayList<JsonObject> arr5 = new ArrayList<JsonObject>(); 
			final String sql1 = "SELECT \r\n"
					+ "	(IF(DATEDIFF(DATE('"+date+"'), cg.start_date) BETWEEN 1 AND 7, cds.due_amount, 0))AS '1 - 7 Days',\r\n"
					+ "	(IF(DATEDIFF(DATE('"+date+"'), cg.start_date) BETWEEN 8 AND 15, cds.due_amount, 0))AS '8 - 15 Days',\r\n"
					+ "	(IF(DATEDIFF(DATE('"+date+"'), cg.start_date) BETWEEN 16 AND 30, cds.due_amount, 0))AS '16 - 30 Days',\r\n"
					+ "	(IF(DATEDIFF(DATE('"+date+"'), cg.start_date) BETWEEN 31 AND 90, cds.due_amount, 0))AS '31 - 90 Days',\r\n"
					+ "	(IF(DATEDIFF(DATE('"+date+"'), cg.start_date) BETWEEN 91 AND 180, cds.due_amount, 0))AS '91 - 180 Days',\r\n"
					+ "	(IF(DATEDIFF(DATE('"+date+"'), cg.start_date) >=180, cds.due_amount, 0))AS 'Above 180 Days'\r\n"
					+ "	\r\n"
					+ "	FROM m_office o\r\n"
					+ "	join m_office ounder on ounder.hierarchy like concat(o.hierarchy, '%')\r\n"
					+ "	and ounder.hierarchy like CONCAT('.', '%')\r\n"
					+ "	INNER JOIN chit_group cg  ON cg.branch_id = ounder.id\r\n"
					+ "	INNER JOIN chit_demand_schedule cds ON cds.chit_id = cg.id\r\n"
					+ "	INNER JOIN chit_subscriber_charge csg on csg.id = cds.chit_subscriber_charge_id\r\n"
					+ "	INNER JOIN chit_group_cycle cgc ON cgc.id = csg.chit_cycle_id\r\n"
					+ "	INNER JOIN chit_group_bids cgb ON cgb.chit_cycle_id = cgc.id\r\n"
					+ "	INNER JOIN chit_group_subscriber cgs ON cgs.id = csg.chit_subscriber_id\r\n"
					+ "	INNER JOIN m_client c ON c.id = cgs.client_id\r\n"
					+ "	LEFT OUTER JOIN chit_subscriber_transaction cst ON cgs.id = cst.chit_subscriber_id\r\n"
					+ "	where o.id = "+ officeId +"	AND cg.status = 20 AND cgs.prized_subscriber =  1 AND c.account_no != '001' AND cds.demand_date = DATE('"+date+"')\r\n"
					+ "	GROUP BY c.id "
					+ " order by ounder.hierarchy, cg.start_date ";
			final SqlRowSet srs2 = this.jdbcTemplate.queryForRowSet(sql1, new Object[] {});
			
			while (srs2.next()) {	
				
				Double Days1to7 = 0.0;
				Double Days8to15 = 0.0;
				Double Days16to30 = 0.0;
				Double Days31to90 = 0.0;
				Double Days91to180 = 0.0;
				Double above180 = 0.0;
				
				if(srs2.getBigDecimal("1 - 7 Days")!=null)
				{
					Days1to7 = srs2.getBigDecimal("1 - 7 Days").doubleValue();
				}	
				if(srs2.getBigDecimal("8 - 15 Days")!=null)
				{
					Days8to15 = srs2.getBigDecimal("8 - 15 Days").doubleValue();
				}
				if(srs2.getBigDecimal("16 - 30 Days")!=null)
				{
					Days16to30 = srs2.getBigDecimal("16 - 30 Days").doubleValue();
				}
				if(srs2.getBigDecimal("31 - 90 Days")!=null)
				{
					Days31to90 = srs2.getBigDecimal("31 - 90 Days").doubleValue();
				}
				if(srs2.getBigDecimal("91 - 180 Days")!=null)
				{
					Days91to180 = srs2.getBigDecimal("91 - 180 Days").doubleValue();
				}
				if(srs2.getBigDecimal("Above 180 Days")!=null)
				{
					above180 = srs2.getBigDecimal("Above 180 Days").doubleValue();
				}
				JsonObject json = new JsonObject();
				json.addProperty("Days 1-7", Days1to7);
				json.addProperty("Days 8-15", Days8to15);
				json.addProperty("Days 16-30", Days16to30);
				json.addProperty("Days 31-90", Days31to90);
				json.addProperty("Days 91-180", Days91to180);
				json.addProperty("above 180", above180);
				arr5.add(json);
			}
			JsonElement parse5 = fromJsonHelper.parse(arr5.toString());
			
			dataTobeSent.add("BucketsReport", parse5);
			return dataTobeSent;
			
		} catch (final EmptyResultDataAccessException e) {
			throw new  ChitGroupNotFoundException(null, e);
		}
			
	}

}
