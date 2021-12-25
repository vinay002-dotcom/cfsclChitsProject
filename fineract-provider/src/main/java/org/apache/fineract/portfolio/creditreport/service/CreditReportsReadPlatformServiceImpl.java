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
package org.apache.fineract.portfolio.creditreport.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;

import org.apache.fineract.portfolio.creditreport.data.CreditReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class CreditReportsReadPlatformServiceImpl implements CreditReportsReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;


    @Autowired
    public CreditReportsReadPlatformServiceImpl(final PlatformSecurityContext context, final RoutingDataSource dataSource,
            final CodeValueReadPlatformService codeValueReadPlatformService) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
      

    }

    private static final class CreditReportMapper implements RowMapper<CreditReport> {

        public String schema() {
            return "fmb.id AS id, fmb.client_id AS clientId, fmb.bureau AS bureau, fmb.score_type AS scoretype,fmb.score_value as scorevalue,fmb.score_comments as scorecomments,fmb.report_id as reportid,fmb.date_of_issue as dateofissue"
            		+" FROM credit_report fmb";
        }

        @Override
        public CreditReport mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
            final long id = rs.getLong("id");
            final long clientId = rs.getLong("clientId");
            final String bureau = rs.getString("bureau");
            final String scoretype = rs.getString("scoretype");
            final String scorevalue = rs.getString("scorevalue");
            final String scorecomments = rs.getString("scorecomments");
            final String reportid = rs.getString("reportid");
            final String dateofissue = rs.getString("dateofissue");
          

            return CreditReport.instance(id,clientId,bureau,scoretype,scorevalue,scorecomments,reportid,dateofissue);

        }
    }



    @Override
    public Collection<CreditReport> getreport(long id) {
    	  this.context.authenticatedUser();

          final CreditReportMapper rm = new CreditReportMapper();
          final String sql = "select " + rm.schema() + " where fmb.client_id=?";

          return this.jdbcTemplate.query(sql, rm, new Object[] { id });
    }



}
