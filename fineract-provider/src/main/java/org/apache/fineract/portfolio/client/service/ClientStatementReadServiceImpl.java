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
package org.apache.fineract.portfolio.client.service;


import java.time.LocalDate;
import java.util.ArrayList;

import java.util.Iterator;

import java.util.List;


import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.ChitGroup.data.ChitBidsWinnerData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitCycleData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitDemandScheduleData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitGroupSubscriberData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitSubscriberChargeData;
import org.apache.fineract.portfolio.ChitGroup.data.ChitSubscriberTransactionData;
import org.apache.fineract.portfolio.ChitGroup.domain.ChitTransactionEnum;
import org.apache.fineract.portfolio.ChitGroup.handler.FindWorkingDays;
import org.apache.fineract.portfolio.ChitGroup.service.ChitBidsReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitChargeReadPlatformServices;
import org.apache.fineract.portfolio.ChitGroup.service.ChitCycleReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitDemandScheduleReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitGroupReadPlatformService;
import org.apache.fineract.portfolio.ChitGroup.service.ChitSubscriberChargeReadPlatformServices;
import org.apache.fineract.portfolio.ChitGroup.service.ChitSubscriberTransactionReadPlatformService;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.paymentdetail.data.PaymentDetailData;
import org.apache.fineract.portfolio.paymentdetail.service.PaymentDetailsReadPlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class ClientStatementReadServiceImpl implements ClientStatementReadService 
{

	private final ChitGroupReadPlatformService chitGroupReadPlatformService;
	private final ChitSubscriberChargeReadPlatformServices chitSubscriberChargeReadPlatformServices;
	private final ChitDemandScheduleReadPlatformService chitDemandScheduleReadPlatformService;
	private FindWorkingDays findWorkingDays = new FindWorkingDays();
	private final ChitSubscriberTransactionReadPlatformService chitSubscriberTransactionReadPlatformService;
	private final PaymentDetailsReadPlatformService paymentDetailsReadPlatformService;
	private final ChitCycleReadPlatformService chitCycleReadPlatformService;
	private final ChitChargeReadPlatformServices chitChargeReadPlatformServices;
	private final ClientReadPlatformService clientReadPlatformService;
	private final ChitBidsReadPlatformService chitBidsReadPlatformService;
	private final FromJsonHelper fromJsonHelper;

	@Autowired
	public ClientStatementReadServiceImpl(ChitGroupReadPlatformService chitGroupReadPlatformService,
			ChitSubscriberChargeReadPlatformServices chitSubscriberChargeReadPlatformServices,
			final ChitDemandScheduleReadPlatformService chitDemandScheduleReadPlatformService,
			final ChitSubscriberTransactionReadPlatformService chitSubscriberTransactionReadPlatformService,
			final PaymentDetailsReadPlatformService paymentDetailsReadPlatformService, 
			final ChitCycleReadPlatformService chitCycleReadPlatformService,
			final ChitChargeReadPlatformServices chitChargeReadPlatformServices,final ClientReadPlatformService clientReadPlatformService,
			final ChitBidsReadPlatformService chitBidsReadPlatformService,final FromJsonHelper fromJsonHelper) {

		this.chitGroupReadPlatformService = chitGroupReadPlatformService;
		this.chitSubscriberChargeReadPlatformServices = chitSubscriberChargeReadPlatformServices;
		this.chitDemandScheduleReadPlatformService = chitDemandScheduleReadPlatformService;
		this.chitSubscriberTransactionReadPlatformService = chitSubscriberTransactionReadPlatformService;
		this.paymentDetailsReadPlatformService = paymentDetailsReadPlatformService;
		this.chitCycleReadPlatformService = chitCycleReadPlatformService;
		this.chitChargeReadPlatformServices = chitChargeReadPlatformServices;
		this.clientReadPlatformService = clientReadPlatformService;
		this.chitBidsReadPlatformService = chitBidsReadPlatformService;
		this.fromJsonHelper = fromJsonHelper;
	}



	@Override
	public List<JsonObject> memberstatement(Long clientId,Long chitId,Long ticketNum) 
	{
		
		Long chitchargeId = chitChargeReadPlatformServices.retrieveIdByName("MONTHLY_INSTALLMENT").getId();
		List<JsonObject> g = new ArrayList<>();
		JsonObject d = new JsonObject();
		JsonObject ot = this.getMemberProfile(clientId, chitId, ticketNum);
		d.add("OtherDetails", ot);
		
		if(chitId!=0 && chitId!=null && ticketNum!=0 && ticketNum!=null)
		{
			ChitGroupSubscriberData chitsubsData = this.chitGroupReadPlatformService.getChitSubscriberUsingChitIDClientId(chitId, clientId, ticketNum);
			
			ChitGroupData ChitGroup = this.chitGroupReadPlatformService.retrieveChitGroup(chitId);
			
			JsonArray ae = new JsonArray();
			
			Double OverallTotal = 0.0;
			Double AmountAdjusted = 0.0;
			Double TotalInstallmentAmount = 0.0;
			
			Boolean fg = false;
			if(chitsubsData!=null)
			{
				Iterator<ChitSubscriberChargeData> subsChargeData = this.chitSubscriberChargeReadPlatformServices.retrieveByChitSubscriberId(chitsubsData.getId()).iterator();
			
				while(subsChargeData.hasNext())
				{
					JsonArray ar = new JsonArray();
					ChitSubscriberChargeData charges = subsChargeData.next();
					
					if(charges.getChitChargeId().compareTo(chitchargeId)==0)
					{
						
						JsonObject jd = new JsonObject();
						Long cycleId = charges.getChitCycleId();
						ChitCycleData cycleData = chitCycleReadPlatformService.retrievecycleid(cycleId);
						LocalDate auctiondate = cycleData.getAuctiondate().withDayOfMonth(1);
						Long SubscriptionPayable = ChitGroup.getMonthlycontribution();
						TotalInstallmentAmount = TotalInstallmentAmount + ChitGroup.getMonthlycontribution();
						Long dividend = cycleData.getDividend();
						if(dividend!=null && dividend!=0)
						{
							AmountAdjusted = AmountAdjusted + dividend;
							jd.addProperty("DividendEarned", dividend);
							SubscriptionPayable =  ChitGroup.getMonthlycontribution() - (dividend/ChitGroup.getChitduration()) ;

						}
						auctiondate = findWorkingDays.validateworkingDayorNot(auctiondate);
						jd.addProperty("monthlyInstallment", ChitGroup.getMonthlycontribution());
						jd.addProperty("cycleId", cycleData.getCycleNumber());
						jd.addProperty("InstallmentMonthYear", auctiondate.toString());
						jd.addProperty("NetPayable", SubscriptionPayable);
						JsonObject f = new JsonObject();
						f.add("MonthlyContribution", jd);
						ar.add(f);
						Boolean flag = true;
						JsonArray je = new JsonArray();
						Iterator<ChitDemandScheduleData> chitdemand = this.chitDemandScheduleReadPlatformService.retriveDemandSchedules(charges.getId(), true).iterator();
						while(chitdemand.hasNext() && flag)
						{
							JsonArray j = new JsonArray();
							Double totalAmountCollected = 0.0;
							JsonObject dataTobeSent = new JsonObject();
							JsonObject demand = new JsonObject();
							ChitDemandScheduleData demandData = chitdemand.next();
							Long Demandid = demandData.getId();

							LocalDate date = demandData.getDemandDate();
							Long staffId = demandData.getStaffId();	
							Double due = demandData.getDueAmount();
							Double collectedAmount = demandData.getCollectedAmount();
							Double installmentAmt = demandData.getInstallmentAmount();
							totalAmountCollected = totalAmountCollected+collectedAmount;


							demand.addProperty("collectedAmount", collectedAmount);
							demand.addProperty("demandId", Demandid);
							demand.addProperty("date", date.toString());

							dataTobeSent.add("ReceiptInformation", demand);
						
							Boolean fl = true;
							JsonObject CrDr = new JsonObject();
							if(due>0.0 || (installmentAmt>0.0 && collectedAmount==0.0))
							{
								fl  = false;
								CrDr.addProperty("Credit/Debit", "Debit");
								dataTobeSent.add("CrDr", CrDr);
							}
							else if(due<=0.0)
							{
								CrDr.addProperty("Credit/Debit", "Credit");
								dataTobeSent.add("CrDr", CrDr);
							}
							Iterator<ChitSubscriberTransactionData> TransactionData = chitSubscriberTransactionReadPlatformService.retrieveDataUsingDemandId(Demandid).iterator();
							if(chitSubscriberTransactionReadPlatformService.retrieveDataUsingDemandId(Demandid).isEmpty() && fl)
							{

								JsonObject receiptDetails = new JsonObject();
								receiptDetails.addProperty("mode", "Adjustment");
								
								dataTobeSent.add("receiptDetails", receiptDetails);

							}
							while(TransactionData.hasNext())
							{
								JsonObject receiptDetails = new JsonObject();

								ChitSubscriberTransactionData chitSubscriberTransactionData = TransactionData.next();

								Long paymentId = chitSubscriberTransactionData.getPaymentdetailId();

								receiptDetails.addProperty("date",chitSubscriberTransactionData.getTransactionDate().toString());

								if(paymentId==null || due<0 || paymentId.compareTo(0l)==0)
								{
									receiptDetails.addProperty("mode", "Adjustment");
								}
								else
								{
									PaymentDetailData paymentDetails = paymentDetailsReadPlatformService.retrivePaymentDetails(paymentId);
									String PaymentType = paymentDetails.getPaymentType().getName();

									if(PaymentType.compareToIgnoreCase("cash")==0)
									{
										receiptDetails.addProperty("mode", PaymentType);
									}
									else
									{
										receiptDetails.addProperty("Bank", paymentDetails.getBankNumber());
										receiptDetails.addProperty("RecieptNumber", paymentDetails.getReceiptNumber());
									}
								}
								dataTobeSent.add("receiptDetails", receiptDetails);
							}
							j.add(dataTobeSent);	
							
							if(j.size()!=0)
							{
								JsonElement ars = this.fromJsonHelper.parse(j.toString());
								je.add(ars);
							}
							
							if(SubscriptionPayable.compareTo(totalAmountCollected.longValue())==0)
							{
								OverallTotal = totalAmountCollected;
								flag = false;
								fg = true;
							}
						}
						
						
						
						if(je.size()!=0)
						{
							
							JsonElement ars = this.fromJsonHelper.parse(je.toString());
							JsonObject f1 = new JsonObject();
							f1.add("DataDetais", ars);
							ar.add(f1);
						}
						
					}
					
					if(ar.size()!=0)
					{
						JsonElement ars = this.fromJsonHelper.parse(ar.toString());
						ae.add(ars);
					}
					
					
				}
				
				if(fg)
				{
					JsonObject total = new JsonObject();
					total.addProperty("TotalInstallmentAmount", TotalInstallmentAmount);
					total.addProperty("AmountAdjusted", AmountAdjusted);
					total.addProperty("OverallTotal", OverallTotal);
					JsonObject totals = new JsonObject();
					totals.add("Total", total);
					ae.add(totals);
					
				}
			
				
				if(ae.size()!=0)
				{
					String b = ae.toString();
					
					JsonElement ele = this.fromJsonHelper.parse(b);
					JsonObject f = new JsonObject();
					f.add("DataDetais", ele);
					g.add(f);
				}
			}
		}
		g.add(d);
		return g;
	}


	@Override
	public JsonObject getMemberProfile(Long clientId,Long chitId,Long ticketnum)
	{
		Long chitchargeId = chitChargeReadPlatformServices.retrieveIdByName("MONTHLY_INSTALLMENT").getId();
		JsonObject OverAllData = new JsonObject();
		JsonObject memberProfile = new JsonObject();
		JsonObject chitDetails = new JsonObject();
		JsonObject clientChitActivities = new JsonObject();
		ClientData clientData = clientReadPlatformService.retrieveOne(clientId);
		//clientDetails
		memberProfile.addProperty("Name", clientData.getDisplayName());
		memberProfile.addProperty("FatherOrSpouse", clientData.getFsFirstname());
		memberProfile.addProperty("Mobile", clientData.getMobileNo());
		memberProfile.addProperty("AdharNo", clientData.getAdhar());
		memberProfile.addProperty("SecondaryId", clientData.getSecIdProofNo());
		memberProfile.addProperty("SecondaryAddressProof", clientData.getSecaddressproofno());
		OverAllData.add("MemberProfileDetails", memberProfile);

		if(chitId!=0)
		{
			//Group Details
			ChitGroupData chitData = this.chitGroupReadPlatformService.retrieveChitGroup(chitId);
			chitDetails.addProperty("ChitGroup", chitData.getName());
			chitDetails.addProperty("ChitValue", chitData.getChitvalue());
			chitDetails.addProperty("NoOfInstallment", chitData.getChitduration());
			chitDetails.addProperty("StartDate", chitData.getStartdate().toString());
			chitDetails.addProperty("PSONumber", chitData.getPsoNumber());
			chitDetails.addProperty("PSODate", chitData.getPsoIssueDate().toString());
			chitDetails.addProperty("CCDate", chitData.getCcIssueDate().toString());
			OverAllData.add("ChitGroupDetails", chitDetails);

			//Chitrelated Activities Of Client
			ChitGroupSubscriberData subscriberData = this.chitGroupReadPlatformService.getChitSubscriberUsingChitIDClientId(chitId, clientId, ticketnum);
			clientChitActivities.addProperty("EnrollDate",  chitData.getStartdate().toString());
			clientChitActivities.addProperty("Position",  subscriberData.getPrizedsubscriber());
			clientChitActivities.addProperty("PrizedCycle",  subscriberData.getPrizedcycle());
			OverAllData.add("clientActivities", clientChitActivities);
			Long cyclenumber = subscriberData.getPrizedcycle();

			if(subscriberData.getPrizedsubscriber())
			{
				//Auction Particulars
				JsonObject AuctionParticulars = new JsonObject();
				ChitCycleData cycleData = this.chitCycleReadPlatformService.getChitDataByChitIdAndCycleNumber(chitId, cyclenumber);
				ChitBidsWinnerData bids = chitBidsReadPlatformService.getChitWinnerData(chitId, cyclenumber);
				AuctionParticulars.addProperty("DateOfBid", bids.getBidDate().toString());
				AuctionParticulars.addProperty("BidNo", cyclenumber);
				AuctionParticulars.addProperty("ChitAmount", chitData.getChitvalue());
				Double bidPercentage = (cycleData.getDividend().doubleValue()/chitData.getChitvalue().doubleValue()) * 100;
				AuctionParticulars.addProperty("Bid%", bidPercentage);
				AuctionParticulars.addProperty("CompanyCommission", cycleData.getForemanCommissionAmount());
				AuctionParticulars.addProperty("DividendToMember", cycleData.getDividend());
				AuctionParticulars.addProperty("VerificationCharges", cycleData.getVerificationAmount());
				AuctionParticulars.addProperty("BidPayable", cycleData.getSubscriptionPayble());
				ChitSubscriberTransactionData chittran = chitSubscriberTransactionReadPlatformService.retrieveData(subscriberData.getId(), ChitTransactionEnum.WINNERPRIZEMONEY.getValue());
				AuctionParticulars.addProperty("PaymentDate", chittran.getTransactionDate().toString());
				AuctionParticulars.addProperty("PaidAmount", chittran.getAmount());
				Double RemainingAmount = cycleData.getSubscriptionPayble() - chittran.getAmount();
				AuctionParticulars.addProperty("BalanceAmount", RemainingAmount);
				OverAllData.add("AuctionParticulars", AuctionParticulars);
				//Payment Info
				JsonObject paymentInfo = new JsonObject();
				paymentInfo.addProperty("PaymentDate",  chittran.getTransactionDate().toString());
				paymentInfo.addProperty("PaymentSeries", "BPY");
				paymentInfo.addProperty("PaymentNo", chittran.getPaymentdetailId());
				paymentInfo.addProperty("PaidAmount", chittran.getAmount());
				if(chittran.getPaymentdetailId()!=null && chittran.getPaymentdetailId()!=0.0)
				{
					PaymentDetailData payment = this.paymentDetailsReadPlatformService.retrivePaymentDetails(chittran.getPaymentdetailId());
					paymentInfo.addProperty("Mode",payment.getPaymentType().getName());
					paymentInfo.addProperty("RefNo",payment.getReceiptNumber());
					paymentInfo.addProperty("BankName",payment.getBankNumber());
				}
				OverAllData.add("paymentInfo", paymentInfo);
			}
			//Charges Details
			
			Double Amt = 0.0;
			Iterator<ChitSubscriberChargeData> subscribercharge = this.chitSubscriberChargeReadPlatformServices.retrieveByChitSubscriberId(subscriberData.getId()).iterator();
			JsonArray s = new JsonArray();
			while(subscribercharge.hasNext())
			{
				ChitSubscriberChargeData itr = subscribercharge.next();
				JsonArray a = new JsonArray();
				if(itr.getChitChargeId().compareTo(chitchargeId)!=0)
				{
					JsonObject chargesDetails = new JsonObject();
					chargesDetails.addProperty("ChargeType", chitChargeReadPlatformServices.retrieveNameById(itr.getChitChargeId()).getName());
					chargesDetails.addProperty("Amount", itr.getAmount());
					Amt = Amt+ itr.getAmount();
					JsonObject ad = new JsonObject();
					ad.add("Charges", chargesDetails);
					a.add(ad);
				}
				if(a.size()!=0)
				{
					JsonElement d = this.fromJsonHelper.parse(a.toString());
					s.add(d);
				}
			}
			if(Amt!=0.0)
			{
				JsonObject chargesDetails = new JsonObject();
				chargesDetails.addProperty("TotalAmount", Amt);
				s.add(chargesDetails);
				OverAllData.add("chargesDetails", s);
			}
		}
		
		return OverAllData;
	}
	
	
	
}