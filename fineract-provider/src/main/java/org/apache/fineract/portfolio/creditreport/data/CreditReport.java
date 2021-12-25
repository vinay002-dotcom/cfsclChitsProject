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

package org.apache.fineract.portfolio.creditreport.data;
import java.io.Serializable;

//import java.time.LocalDateTime;

//import org.apache.fineract.organisation.staff.data.StaffData;



public class CreditReport implements Serializable
{
	public String getBureau() {
		return bureau;
	}



	public void setBureau(String bureau) {
		this.bureau = bureau;
	}



	public String getScoretype() {
		return scoretype;
	}



	public void setScoretype(String scoretype) {
		this.scoretype = scoretype;
	}



	public String getScorevalue() {
		return scorevalue;
	}



	public void setScorevalue(String scorevalue) {
		this.scorevalue = scorevalue;
	}



	public String getScorecomments() {
		return scorecomments;
	}



	public void setScorecomments(String scorecomments) {
		this.scorecomments = scorecomments;
	}



	public String getReportid() {
		return reportid;
	}



	public void setReportid(String reportid) {
		this.reportid = reportid;
	}



	public String getDateofissue() {
		return dateofissue;
	}



	public void setDateofissue(String dateofissue) {
		this.dateofissue = dateofissue;
	}



	public Long getId() {
		return id;
	}



	public Long getClientId() {
		return clientId;
	}

	private final Long id;
	 private final Long clientId;
	private String bureau;
	private String scoretype;
	private String scorevalue;
	private String scorecomments;
	private String reportid;
	private String dateofissue;
	
	public static CreditReport importInstance(Long id,Long clientId,String bureau,String scoretype,String scorevalue,String scorecomments,String reportid,String dateofissue)
	{
		return new CreditReport(id,clientId,bureau,scoretype,scorevalue,scorecomments,reportid,dateofissue);
	}



	public static CreditReport instance(Long id,Long clientId,String bureau,String scoretype,String scorevalue,String scorecomments,String reportid,String dateofissue) {
		return new CreditReport(id,clientId,bureau,scoretype,scorevalue,scorecomments,reportid,dateofissue);
	
	}
    public static CreditReport templateData(Long id,Long clientId,String bureau,String scoretype,String scorevalue,String scorecomments,String reportid,String dateofissue) {
    	
    	return new CreditReport(id,clientId,bureau,scoretype,scorevalue,scorecomments,reportid,dateofissue);
    }
	
	private CreditReport(Long id,Long clientId,String bureau,String scoretype,String scorevalue,String scorecomments,String reportid,String dateofissue) {
		
		this.id = id ; 
		this.bureau = bureau ;
		this.scoretype = scoretype;
		this.scorevalue = scorevalue;
		this.scorecomments=scorecomments;
		this.reportid = reportid;
		this.dateofissue = dateofissue;
		this.clientId = clientId;
	}



}
