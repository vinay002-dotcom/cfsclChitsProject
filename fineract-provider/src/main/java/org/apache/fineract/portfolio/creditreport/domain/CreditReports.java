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

package org.apache.fineract.portfolio.creditreport.domain;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.client.domain.Client;

@Entity
@Table(name = "credit_report")
public class CreditReports extends AbstractPersistableCustom {
	
	@ManyToOne
	@JoinColumn(name="client_id")
	private Client client;
	
	@Column(name="id")
	private Long Sid;
	
	public Long getSId() {
		return Sid;
	}


	public void setSId(Long Sid) {
		this.Sid = Sid;
	}


	@Column(name="bureau")
	private String bureau;
	
	@Column(name="score_type")
	private String scoretype;
	
	@Column(name="score_value")
	private String scorevalue;
	
	@Column(name="score_comments")
	private String scorecomments;
	
	@Column(name="report_id")
	private String reportid;
	
	@Column(name="date_of_issue")
	private String dateofissue;
	

	
		private CreditReports(final Client client,final String bureau,
				final String scoretype,final String scorevalue,final String scorecomments,final String reportid,final String dateofissue,final Long Sid)
		{
			
			this.client=client;
			this.bureau = bureau ;
			this.scoretype = scoretype;
			this.scorevalue = scorevalue;
			this.scorecomments=scorecomments;
			this.reportid = reportid;
			this.dateofissue = dateofissue;
			this.Sid = Sid;
		}
		
		
		public CreditReports()
		{
			
		}
		
		public static CreditReports fromJson(final Client client,final String bureau,
				final String scoretype,final String scorevalue,final String scorecomments,final String reportid,final String dateofissue,final Long Sid)
		{
			return new CreditReports(client,bureau,scoretype,scorevalue,scorecomments,reportid,dateofissue,Sid);
		}


		public Client getClient() {
			return client;
		}


		public void setClient(Client client) {
			this.client = client;
		}


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

		


		
		
}
