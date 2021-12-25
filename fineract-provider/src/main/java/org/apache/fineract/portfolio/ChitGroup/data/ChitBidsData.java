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

package org.apache.fineract.portfolio.ChitGroup.data;

import java.io.Serializable;
import java.time.LocalDate;

public final class ChitBidsData implements Serializable {

	private final Long id;

    private final Long subscriberId;

	private final Long chitCycleId;
	
	private final Double bidAmount;
	
	private final Boolean bidWon;

	private final String bidderparticipation;
	
	private final Long bidderparticipationId;
	
	private final LocalDate biddate;
	
//	private  int count;
	
	private final Boolean isPaid;
	
//    private ChitBidsData(final Long id, final Long subscriberId,final Long chitCycleId, final Long bidAmount, final Boolean bidWon) {
//        this.id = id;
//        this.subscriberId=subscriberId;
//        this.chitCycleId=chitCycleId;
//        this.bidAmount=bidAmount;
//        this.bidWon=bidWon;
//    }
	
	

    public static ChitBidsData instance(final Long id, final Long subscriberId,final Long chitCycleId, final Double bidAmount, final Boolean bidWon,String bidderparticipation, Long bidderparticipationId, LocalDate biddate,
    		Boolean isPaid) {
        return new ChitBidsData(id, subscriberId,chitCycleId, bidAmount, bidWon,bidderparticipation,bidderparticipationId,biddate,isPaid);
    }

    public static ChitBidsData instanceForCount(final Long id, final Long subscriberId,final Long chitCycleId, final Double bidAmount,
    		final Boolean bidWon,String bidderparticipation, Long bidderparticipationId, LocalDate biddate,Boolean isPaid) {
        return new ChitBidsData(id, subscriberId,chitCycleId, bidAmount, bidWon,bidderparticipation,
        		bidderparticipationId,biddate,isPaid);
    }
//    public int getCount() {
//		return count;
//	}

	public String getBidderparticipation() {
		return bidderparticipation;
	}


	public Long getBidderparticipationId() {
		return bidderparticipationId;
	}

	public LocalDate getBiddate() {
		return biddate;
	}

	 public Boolean getIsPaid() {
			return isPaid;
		}


	private ChitBidsData(Long id, Long subscriberId, Long chitCycleId, Double bidAmount, Boolean bidWon,
		String bidderparticipation, Long bidderparticipationId, LocalDate biddate,Boolean isPaid) {
	
	this.id = id;
	this.subscriberId = subscriberId;
	this.chitCycleId = chitCycleId;
	this.bidAmount = bidAmount;
	this.bidWon = bidWon;
	this.bidderparticipation = bidderparticipation;
	this.bidderparticipationId = bidderparticipationId;
	this.biddate = biddate;
	this.isPaid = isPaid;
}
	
//	private ChitBidsData(Long id, Long subscriberId, Long chitCycleId, Double bidAmount, Boolean bidWon,
//			String bidderparticipation, Long bidderparticipationId, LocalDate biddate, int count,Boolean isPaid) {
//		this.id = id;
//		this.subscriberId = subscriberId;
//		this.chitCycleId = chitCycleId;
//		this.bidAmount = bidAmount;
//		this.bidWon = bidWon;
//		this.bidderparticipation = bidderparticipation;
//		this.bidderparticipationId = bidderparticipationId;
//		this.biddate = biddate;
//		//this.count = count;
//		this.isPaid = isPaid;
//	}


	public Long getId() {
        return id;
    }

    public Long getSubscriberId() {
        return subscriberId;
    }

    public Long getChitCycleId() {
        return chitCycleId;
    }

    public Double getBidAmount() {
        return bidAmount;
    }

    public Boolean getBidWon() {
        return bidWon;
    }

}
