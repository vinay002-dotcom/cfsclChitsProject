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
/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/
 */
package org.apache.fineract.infrastructure.highmark.service;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.fineract.infrastructure.codes.data.CodeValueData;
import org.apache.fineract.infrastructure.codes.service.CodeValueReadPlatformService;
import org.apache.fineract.infrastructure.configuration.data.GlobalConfigurationPropertyData;
import org.apache.fineract.infrastructure.configuration.service.ConfigurationReadPlatformService;
import org.apache.fineract.infrastructure.dataqueries.data.GenericResultsetData;
import org.apache.fineract.infrastructure.dataqueries.data.ResultsetRowData;
import org.apache.fineract.infrastructure.dataqueries.service.ReadWriteNonCoreDataService;
import org.apache.fineract.infrastructure.documentmanagement.command.DocumentCommand;
import org.apache.fineract.infrastructure.documentmanagement.service.DocumentWritePlatformService;
import org.apache.fineract.infrastructure.highmark.Exception.AdharNotFoundException;
import org.apache.fineract.infrastructure.highmark.Exception.MobileNotFoundException;
import org.apache.fineract.infrastructure.highmark.Exception.SecondaryIdNotFoundException;
import org.apache.fineract.infrastructure.highmark.generated.CHM_Request_INDV.ADDRESSSEGMENT;
import org.apache.fineract.infrastructure.highmark.generated.CHM_Request_INDV.APPLICANTSEGMENT;
import org.apache.fineract.infrastructure.highmark.generated.CHM_Request_INDV.APPLICANTSEGMENT.APPLICANTNAME;
import org.apache.fineract.infrastructure.highmark.generated.CHM_Request_INDV.APPLICANTSEGMENT.DOB;
import org.apache.fineract.infrastructure.highmark.generated.CHM_Request_INDV.APPLICATIONSEGMENT;
import org.apache.fineract.infrastructure.highmark.generated.CHM_Request_INDV.HEADERSEGMENT;
import org.apache.fineract.infrastructure.highmark.generated.CHM_Request_INDV.HEADERSEGMENT.CONSUMER;
import org.apache.fineract.infrastructure.highmark.generated.CHM_Request_INDV.HEADERSEGMENT.MFI;
import org.apache.fineract.infrastructure.highmark.generated.CHM_Request_INDV.INQUIRY;
import org.apache.fineract.infrastructure.highmark.generated.CHM_Request_INDV.REQUESTREQUESTFILE;
import org.apache.fineract.infrastructure.highmark.generated.CHM_Response_INDV.INDVREPORTFILE;
import org.apache.fineract.infrastructure.highmark.generated.CHM_Response_INDV.SCORES;
import org.apache.fineract.infrastructure.highmark.generated.CHM_Response_INDV.SCORES.SCORE;
import org.apache.fineract.infrastructure.highmark.generated.HM_Acknowledgement_Single.REPORTFILE;
import org.apache.fineract.infrastructure.utills.CommonMethodsUtil;
import org.apache.fineract.infrastructure.utills.HighMarkInquiryReferenceNumberGenerator;
import org.apache.fineract.infrastructure.utills.vo.GenericResponseVO;
import org.apache.fineract.portfolio.address.data.AddressData;
import org.apache.fineract.portfolio.address.exception.AddressNotFoundException;
import org.apache.fineract.portfolio.address.service.AddressReadPlatformService;
import org.apache.fineract.portfolio.client.api.ClientsApiResource;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.data.ClientIdentifierData;
import org.apache.fineract.portfolio.client.service.ClientIdentifierReadPlatformService;
import org.apache.fineract.portfolio.creditreport.data.CreditReport;
import org.apache.fineract.portfolio.creditreport.domain.CreditRepository;
import org.apache.fineract.portfolio.creditreport.service.CreditReportsReadPlatformService;
import org.apache.fineract.portfolio.creditreport.service.CreditReportsWritePlatformService;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.google.common.base.Splitter;
import com.google.gson.JsonObject;

@Service
public class HighMarkProcessingServiceImpl implements HighMarkProcessingService {

	private final static Logger logger = LoggerFactory.getLogger(HighMarkProcessingServiceImpl.class);


	@Autowired
	private  Environment propertyEnv;

	@Autowired
	private AddressReadPlatformService addressService;

	@Autowired
	private ClientIdentifierReadPlatformService clientIdentifierService;

	@Autowired
	private ReadWriteNonCoreDataService readWriteNonCoreDataService;

	@Autowired
	private CodeValueReadPlatformService codeValueReadPlatformService;

	@Autowired
	private DocumentWritePlatformService documentWritePlatformService;

	private final CreditReportsWritePlatformService creditreportswriteplatformservice ;

	private final CreditReportsReadPlatformService creditreadPlatformService;

	private final CreditRepository creditRepository;

	final ConfigurationReadPlatformService configurationReadPlatformService;
	
	private final ClientsApiResource clientsApiResource;

	@Autowired
	public HighMarkProcessingServiceImpl(CreditReportsWritePlatformService creditreportswriteplatformservice,final CreditRepository creditRepository,final ConfigurationReadPlatformService configurationReadPlatformService,
			final CreditReportsReadPlatformService creditreadPlatformService,final ClientsApiResource clientsApiResource) {
		super();
		this.creditreportswriteplatformservice = creditreportswriteplatformservice;
		this.creditRepository = creditRepository;
		this.configurationReadPlatformService = configurationReadPlatformService;
		this.creditreadPlatformService = creditreadPlatformService;
		this.clientsApiResource = clientsApiResource;
	}


	private Long id;

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}

	private Long addresstypeid;

	public Long getAddresstypeid() {
		return addresstypeid;
	}


	public void setAddresstypeid(Long addresstypeid) {
		this.addresstypeid = addresstypeid;
	}


	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	@Override
	public GenericResponseVO initateClientCreditRequest(ClientData client,double loanAmount) {

		GenericResponseVO responseVO= null ;
		try {

			if (CommonMethodsUtil.isBlank(client)) {
				return new GenericResponseVO(" ",404,"Client Details Not Found",new java.util.Date().getTime());
			}
			if (CommonMethodsUtil.isBlank(client.getFirstname())&&CommonMethodsUtil.isBlank(client.getLastname())) {
				return new GenericResponseVO(" ",404,"Client Name is empty",new java.util.Date().getTime());
			}

			//	loanAmount=20000;

			if(client.getId()!=null)
			{
				this.setId(client.getId());
			}

			REQUESTREQUESTFILE requestXml = new REQUESTREQUESTFILE();
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");  
			LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
			String currentTime = dtf.format(now).toString();
			HEADERSEGMENT hs = new HEADERSEGMENT();
			MFI mfi = new MFI();
			CONSUMER consumer = new CONSUMER();
			hs.setINQDTTM(currentTime);
			hs.setAUTHFLG("Y");
			hs.setAUTHTITLE("USER");
			hs.setMEMBERPREOVERRIDE("N");
			hs.setREQACTNTYP("SUBMIT");
			hs.setRESFRMT("XML/HTML");
			hs.setRESFRMTEMBD("N");
			hs.setIOI(true);
			hs.setTESTFLG(propertyEnv.getProperty("highmark.reqTestFLG"));
			hs.setREQVOLTYP(propertyEnv.getProperty("highmark.reqVolType"));
			hs.setSUBMBRID(propertyEnv.getProperty("highmark.subMbrid"));

			mfi.setGROUP(true);
			mfi.setINDV(true);
			mfi.setSCORE(false);

			consumer.setINDV(false);
			consumer.setSCORE(false);

			hs.setMFI(mfi);
			hs.setCONSUMER(consumer);
			requestXml.setHEADERSEGMENT(hs);



			String inquiryReferenceNO = HighMarkInquiryReferenceNumberGenerator.generate();

			INQUIRY inquiry = new INQUIRY();
			APPLICATIONSEGMENT applicationSegment = new APPLICATIONSEGMENT();

			applicationSegment.setINQUIRYUNIQUEREFNO(inquiryReferenceNO);
			applicationSegment.setCREDTINQPURPSTYP(propertyEnv.getProperty("highmark.CREDTINQPURPSTYP"));
			applicationSegment.setCREDITINQUIRYSTAGE(propertyEnv.getProperty("highmark.CREDITINQUIRYSTAGE"));
			applicationSegment.setCREDTREQTYP(propertyEnv.getProperty("highmark.reqVolType"));
			applicationSegment.setLOSAPPID(inquiryReferenceNO);
			applicationSegment.setBRANCHID("CHETANA FINANCIAL SERVICES LIMITED");

			if(CommonMethodsUtil.isNotBlank(loanAmount)) {
				try {
					BigDecimal loanPrincipalAmnt = new BigDecimal(loanAmount);   
					applicationSegment.setLOANAMOUNT(loanPrincipalAmnt.setScale(2, BigDecimal.ROUND_UP).toString());
				}
				catch(NumberFormatException nfe) {
					////System.out.println("something went wrong");
				}
			}

			APPLICANTSEGMENT applicantSegement =new APPLICANTSEGMENT();
			APPLICANTNAME applicantName = new APPLICANTNAME();

			applicantName.setNAME1(client.getFirstname());
			applicantSegement.setAPPLICANTNAME(applicantName);



			if(CommonMethodsUtil.isNotBlank(client.getDateOfBirth())) {
				DOB dob = new DOB();

				LocalDate dateOfBirth = client.getDateOfBirth();
				dob.setDOBDATE(dateOfBirth.getDayOfMonth()+"/"+dateOfBirth.getMonth().getValue()+"/"+dateOfBirth.getYear());
				DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");  
				LocalDateTime now1 = LocalDateTime.now(ZoneId.systemDefault());
				String currentTime1 = dtf1.format(now1).toString();
				int a = dateOfBirth.getYear();
				int b = now1.getYear();
				int age = b - a;

				applicantSegement.setDOB(dob);
				dob.setAGE(age+"");
				dob.setAGEASON(currentTime1);



			}
			if(client.getMobileNo()==null)
			{
				throw new MobileNotFoundException(client.getId());
			}
			if(CommonMethodsUtil.isNotBlank(client.getMobileNo())) {
				List<APPLICANTSEGMENT.PHONES.PHONE> applicantPhoneList= new ArrayList<APPLICANTSEGMENT.PHONES.PHONE>();
				APPLICANTSEGMENT.PHONES phones = new APPLICANTSEGMENT.PHONES();
				APPLICANTSEGMENT.PHONES.PHONE phone = new APPLICANTSEGMENT.PHONES.PHONE();
				phone.setTELENO(BigInteger.valueOf(Long.parseLong(client.getMobileNo())));
				phone.setTELENOTYPE(propertyEnv.getProperty("highmark.config.phone.type"));
				applicantPhoneList.add(phone);
				phones.setPhone(applicantPhoneList);
				applicantSegement.setPHONES(phones);
			}

			if(CommonMethodsUtil.isNotBlank(client.getGender())) {
				if(client.getGender().getName().equalsIgnoreCase("Male")||client.getGender().getName().equalsIgnoreCase("M"))
					applicantSegement.setGENDER("G01");
				else if(client.getGender().getName().equalsIgnoreCase("Female")||client.getGender().getName().equalsIgnoreCase("F"))
					applicantSegement.setGENDER("G02");
				else
					applicantSegement.setGENDER("G03");
			}


			//ID Segment
			Iterable<String> ary = Splitter.on(',').split(propertyEnv.getProperty("highmark.config.ID.data"));
			ArrayList<Integer> integerArrID = new ArrayList<Integer>();


			for(String count:ary) {
				try {
					integerArrID.add(Integer.parseInt(count));
				}
				catch(NumberFormatException formatEx)
				{
					integerArrID.add(-1);
				}
			}
			boolean isIDDatable = Boolean.valueOf(propertyEnv.getProperty("highmark.config.ID.dataTable.enable"));
			List<APPLICANTSEGMENT.IDS.ID> applicantIdList= new ArrayList<APPLICANTSEGMENT.IDS.ID>();
			if(isIDDatable) {
				GenericResultsetData results = this.readWriteNonCoreDataService.retrieveDataTableGenericResultSet(propertyEnv.getProperty("highmark.config.ID.dataTable.name"), client.getId(),
						null, null);

				if(CommonMethodsUtil.isNotBlank(results.getData())&&CommonMethodsUtil.isNotBlank(integerArrID)&&results.getData().size()>0) {
					for(ResultsetRowData resultRowData : results.getData()) {
						for(int i=0;i<integerArrID.size();i++) {
							Integer indexValue = integerArrID.get(i); 
							if(indexValue!=0&&indexValue<resultRowData.getRow().size()) {
								String idValue =resultRowData.getRow().get(indexValue);
								if(CommonMethodsUtil.isNotBlank(idValue)) {
									APPLICANTSEGMENT.IDS.ID id = new APPLICANTSEGMENT.IDS.ID();
									id.setTYPE("ID0"+(i+1));
									id.setVALUE(idValue);
									applicantIdList.add(id);
								}
							}
						}
					}
				}
			}
			else {

				if(client.getAdhar()!=null)
				{
					APPLICANTSEGMENT.IDS.ID id = new APPLICANTSEGMENT.IDS.ID();
					id.setTYPE("ID03");
					id.setVALUE(client.getAdhar());
					applicantIdList.add(id);
				}
				else
				{
					throw new AdharNotFoundException(client.getId());
				}
				if(client.getSecIdProofNo()!=null)
				{
					APPLICANTSEGMENT.IDS.ID id = new APPLICANTSEGMENT.IDS.ID();
					CodeValueData codeValueData = codeValueReadPlatformService.retrieveCodeValue(client.getIdproof().getId());
					String sec = codeValueData.getName();
					////System.out.println(sec+"---");
					if(sec.equals("Pan Card"))
					{
						id.setTYPE("ID07");
					}
					else if(sec.equals("Drivers License"))
					{
						id.setTYPE("ID06");
					}
					else
					{
						id.setTYPE("ID04");
					}
					id.setVALUE(client.getSecIdProofNo());
					applicantIdList.add(id);
				}
				else
				{
					throw new SecondaryIdNotFoundException(client.getId());
				}
			}

			APPLICANTSEGMENT.IDS applicantIds= new APPLICANTSEGMENT.IDS();
			applicantIds.setId(applicantIdList);
			applicantSegement.setIDS(applicantIds);



			//Address Segemet 
			Iterable<String>  addressTypeArr = Splitter.on(',').split(propertyEnv.getProperty("highmark.config.address.data"));
			ArrayList<Integer> integerAddressType = new ArrayList<Integer>();


			for(String count:addressTypeArr) {
				try {
					integerAddressType.add(Integer.parseInt(count));
				}
				catch(NumberFormatException formatEx)
				{
					integerAddressType.add(-1);
				}
			}

			boolean isAddressDatable = Boolean.valueOf(propertyEnv.getProperty("highmark.config.address.dataTable.enable"));
			List<ADDRESSSEGMENT.ADDRESS> applicantAddressList= new ArrayList<ADDRESSSEGMENT.ADDRESS>();
			if(isAddressDatable) {
				GenericResultsetData results = this.readWriteNonCoreDataService.retrieveDataTableGenericResultSet(propertyEnv.getProperty("highmark.config.address.dataTable.name"), client.getId(),
						null, null);
				Iterable<String>  aryAddress = Splitter.on(',').split(propertyEnv.getProperty("highmark.config.address.fieldData"));
				ArrayList<Integer> integerArrAddress = new ArrayList<Integer>();


				for(String count:aryAddress) {
					try {
						integerArrAddress.add(Integer.parseInt(count));
					}
					catch(NumberFormatException formatEx)
					{
						integerArrAddress.add(-1);
					}
				}

				if(CommonMethodsUtil.isNotBlank(results.getData())&&CommonMethodsUtil.isNotBlank(integerArrAddress)&&results.getData().size()>0) {
					for(ResultsetRowData resultRowData : results.getData()) {
						for(int i=0;i<integerAddressType.size()&&integerAddressType.get(i)>-1;i++) {
							ADDRESSSEGMENT.ADDRESS address = new ADDRESSSEGMENT.ADDRESS();
							address.setTYPE("D0"+(i+1));
							boolean setAddress = true;
							for(int j=0;j<integerArrAddress.size();j++) {
								Integer indexValue = integerArrAddress.get(j); 
								if(indexValue>-1&&indexValue<resultRowData.getRow().size()) {
									switch(j) {
									case 0:
										address.setADDRESS1(resultRowData.getRow().get(indexValue));
										break;
									case 1:
										address.setCITY(resultRowData.getRow().get(indexValue));
										break;
									case 2:
										String stateCode = resultRowData.getRow().get(indexValue);
										if(stateCode.length()==2)
											address.setSTATE(stateCode);
										break;
									case 3:
										try {
											address.setPIN(Long.parseLong(resultRowData.getRow().get(indexValue)));
										}
										catch(NumberFormatException formatEx)
										{
											setAddress = false;
											//										return new GenericResponseVO(" ",400,"Postal Code is Invalid",new java.util.Date().getTime());
										}
										break;
									}

								}
							}
							if(setAddress&&CommonMethodsUtil.isBlank(address.getSTATE())) {
								try {
									String defaultState = propertyEnv.getProperty("highmark.config.address.state.default");
									if(CommonMethodsUtil.isNotBlank(defaultState)&&defaultState.length()==2)
									{
										address.setSTATE(defaultState);
									}
								}
								catch(Exception e) {
									setAddress = false;
									//										return new GenericResponseVO(" ",400,"State Code is invalid",new java.util.Date().getTime());
								}
							}
							if(setAddress)
								applicantAddressList.add(address);
						}
					}
				}
			}
			else {

				int count=0;

				Collection<AddressData> clientAddressDataList = this.addressService.retrieveAllClientAddress(client.getId());
				if(clientAddressDataList.isEmpty())
				{
					throw new AddressNotFoundException(client.getId());
				}
				if(CommonMethodsUtil.isNotBlank(clientAddressDataList)) {
					for(AddressData clientAddressData: clientAddressDataList ) {
						//							Integer indexValue = integerAddressType.indexOf(clientAddressData.getAddressTypeId().intValue());
						Long indexValue = clientAddressData.getAddressTypeId();
						////System.out.println("index value is "+indexValue);
						Collection<CodeValueData> addr = codeValueReadPlatformService.retrieveCodeValuesByCode("ADDRESS_TYPE");
						List<CodeValueData> addrList = new ArrayList<CodeValueData>(addr);
						for(int i = 0 ; i < addrList.size();i++)
						{
							Object obj = addrList.get(i);
							if(obj instanceof CodeValueData)
							{
								if(((CodeValueData) obj).getName().equals("Communication Address"))
								{
									Long adrtypeid = ((CodeValueData) obj).getId();
									this.setAddresstypeid(adrtypeid);
									////System.out.println("adressid "+this.getAddresstypeid());
									break;
								}
							}
						}

						////System.out.println("if condition "+indexValue.equals(this.getAddresstypeid()));
						if(indexValue.equals(this.getAddresstypeid())) {

							boolean setAddress = true;
							ADDRESSSEGMENT.ADDRESS address = new ADDRESSSEGMENT.ADDRESS();

							address.setTYPE("D01");
							address.setADDRESS1(clientAddressData.getAddressLine1());
							address.setCITY(clientAddressData.getTownVillage());
							try {
								address.setPIN(Long.parseLong(clientAddressData.getPostalCode()));
							}
							catch(NumberFormatException formatEx)
							{
								setAddress = false;
							}
							if(clientAddressData.getStateName().length()==2)
								address.setSTATE(clientAddressData.getStateName());
							else {
								CodeValueData codeValueData = codeValueReadPlatformService.retrieveCodeValue(clientAddressData.getStateProvinceId());
								if(codeValueData.getDescription().length()==2)
									address.setSTATE(clientAddressData.getStateName());
								else {
									try {
										String defaultState = propertyEnv.getProperty("highmark.config.address.state.default");
										if(CommonMethodsUtil.isNotBlank(defaultState)&&defaultState.length()==2)
										{
											address.setSTATE(defaultState);
										}
									}
									catch(Exception e) {
										setAddress = false;
										//											return new GenericResponseVO(" ",400,"State Code is invalid",new java.util.Date().getTime());
									}
								}
							}
							if(setAddress)
							{
								count++;
								applicantAddressList.add(address);
							}
						}


					}
				}

				if(count==0)
				{
					throw new AddressNotFoundException(client.getId());
				}

			}


			ADDRESSSEGMENT applicantAddress = new ADDRESSSEGMENT();
			applicantAddress.setAddress(applicantAddressList);
			inquiry.setADDRESSSEGMENT(applicantAddress);
			inquiry.setAPPLICANTSEGMENT(applicantSegement);
			inquiry.setAPPLICATIONSEGMENT(applicationSegment);
			requestXml.setINQUIRY(inquiry);

			JAXBContext jaxbContextRequest = JAXBContext.newInstance(REQUESTREQUESTFILE.class);
			Marshaller jaxbMarshaller = jaxbContextRequest.createMarshaller();
			//			jaxbMarshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);

			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(requestXml, sw);
			String xmlString = sw.toString();

			logger.info("High Mark Inquiry Request XML - "+ xmlString);

			String requestUrl = propertyEnv.getProperty("highmark.url.indvReqUrl");

			//			String url = "https://test.crifhighmark.com/Inquiry/doGet.service/requestResponse";
			System.setProperty("jsse.enableSNIExtension", propertyEnv.getProperty("highmark.ssl.enableSNIExtension"));
			URL obj = new URL(requestUrl);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			//add reuqest header
			con.setRequestMethod("POST");
			//			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setRequestProperty("Content-Type", "application/xml");
			con.setRequestProperty("userId",propertyEnv.getProperty("highmark.userid"));
			con.setRequestProperty("password", propertyEnv.getProperty("highmark.password"));
			con.setRequestProperty("mbrid", propertyEnv.getProperty("highmark.mbrid"));
			con.setRequestProperty("productType",propertyEnv.getProperty("highmark.productType"));
			con.setRequestProperty("productVersion", propertyEnv.getProperty("highmark.productVersion"));
			con.setRequestProperty("reqVolType", propertyEnv.getProperty("highmark.reqVolType"));
			con.setRequestProperty("requestXML",xmlString);

			//			con.setRequestProperty("requestXML",propertyEnv.getProperty("highmark.requestXML"));
			////System.out.println("Request Data inside credit request "+con.getRequestProperties());
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes("");
			wr.flush();
			wr.close();

			if (con.getResponseCode()==200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream(),Charset.defaultCharset()));
				String inputLine;
				StringBuilder response = new StringBuilder();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				con.disconnect();
				JAXBContext jaxbContext = JAXBContext.newInstance(REPORTFILE.class);
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

				StringReader reader = new StringReader(response.toString());
				REPORTFILE report = (REPORTFILE) unmarshaller.unmarshal(reader);

				//					TimeUnit.SECONDS.sleep(8);

				logger.info("High Mark Inquiry response - " + response.toString());

				if(CommonMethodsUtil.isNotBlank(report.getINQUIRYSTATUS().getINQUIRY().get(0))&&CommonMethodsUtil.isNotBlank(report.getINQUIRYSTATUS().getINQUIRY().get(0).getERRORS()))
					responseVO = new GenericResponseVO(report,401,"Report Fetching Failed",new java.util.Date().getTime());
				else 
					responseVO = this.retreiveCreditReport(report,client.getId(),"client");

				//					responseVO = new GenericResponseVO(report,200,"Report Fetched Successfully",new java.util.Date().getTime());
				return responseVO;
			}
			else
			{
				con.disconnect();
				responseVO = new GenericResponseVO("",401,"Report Fetching Failed",new java.util.Date().getTime());
				return responseVO;
			}

		} catch (Exception ex) {
			logger.error("Exception in fecthing HighMark Report - ");
			ex.printStackTrace(System.out);
			responseVO = new GenericResponseVO(ex.getMessage(),500,"Exception in fecthing HighMark Report",new java.util.Date().getTime());
		}

		return responseVO;
	}


	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	@Override
	public GenericResponseVO initateLoanCreditRequest(Loan loan) {

		GenericResponseVO responseVO= null ;
		try {

			if (CommonMethodsUtil.isBlank(loan.getClient())) {
				return new GenericResponseVO(" ",404,"Client Details Not Found",new java.util.Date().getTime());
			}
			if (CommonMethodsUtil.isBlank(loan.getClient().getFirstname())) {
				return new GenericResponseVO(" ",404,"Client Name is empty",new java.util.Date().getTime());
			}



			REQUESTREQUESTFILE requestXml = new REQUESTREQUESTFILE();
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");  
			LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
			String currentTime = dtf.format(now).toString();
			HEADERSEGMENT hs = new HEADERSEGMENT();
			MFI mfi = new MFI();
			CONSUMER consumer = new CONSUMER();
			hs.setINQDTTM(currentTime);
			hs.setAUTHFLG("Y");
			hs.setAUTHTITLE("USER");
			hs.setMEMBERPREOVERRIDE("N");
			hs.setREQACTNTYP("SUBMIT");
			hs.setRESFRMT("XML/HTML");
			hs.setRESFRMTEMBD("Y");
			hs.setIOI(true);
			hs.setTESTFLG(propertyEnv.getProperty("highmark.reqTestFLG"));
			hs.setREQVOLTYP(propertyEnv.getProperty("highmark.reqVolType"));
			hs.setSUBMBRID(propertyEnv.getProperty("highmark.subMbrid"));

			mfi.setGROUP(true);
			mfi.setINDV(true);
			mfi.setSCORE(false);

			consumer.setINDV(false);
			consumer.setSCORE(false);

			hs.setMFI(mfi);
			hs.setCONSUMER(consumer);
			requestXml.setHEADERSEGMENT(hs);



			String inquiryReferenceNO = HighMarkInquiryReferenceNumberGenerator.generate();

			INQUIRY inquiry = new INQUIRY();
			APPLICATIONSEGMENT applicationSegment = new APPLICATIONSEGMENT();

			applicationSegment.setINQUIRYUNIQUEREFNO(inquiryReferenceNO);
			applicationSegment.setCREDTINQPURPSTYP(propertyEnv.getProperty("highmark.CREDTINQPURPSTYP"));
			applicationSegment.setCREDITINQUIRYSTAGE(propertyEnv.getProperty("highmark.CREDITINQUIRYSTAGE"));
			applicationSegment.setCREDTREQTYP(propertyEnv.getProperty("highmark.reqVolType"));
			applicationSegment.setLOSAPPID(inquiryReferenceNO);
			applicationSegment.setBRANCHID(loan.getOffice().getName());
			applicationSegment.setLOANAMOUNT(loan.getProposedPrincipal().setScale(2, BigDecimal.ROUND_UP).toString());


			APPLICANTSEGMENT applicantSegement =new APPLICANTSEGMENT();
			APPLICANTNAME applicantName = new APPLICANTNAME();

			applicantName.setNAME1(loan.getClient().getFirstname());
			applicantSegement.setAPPLICANTNAME(applicantName);



			if(CommonMethodsUtil.isNotBlank(loan.getClient().dateOfBirth())) {
				DOB dob = new DOB();
				dob.setDOBDATE(new SimpleDateFormat("dd/MM/yyyy").format(loan.getClient().dateOfBirth()));
				applicantSegement.setDOB(dob);

			}

			if(CommonMethodsUtil.isNotBlank(loan.getClient().mobileNo())) {
				List<APPLICANTSEGMENT.PHONES.PHONE> applicantPhoneList= new ArrayList<APPLICANTSEGMENT.PHONES.PHONE>();
				APPLICANTSEGMENT.PHONES phones = new APPLICANTSEGMENT.PHONES();
				APPLICANTSEGMENT.PHONES.PHONE phone = new APPLICANTSEGMENT.PHONES.PHONE();
				phone.setTELENO(BigInteger.valueOf(Long.parseLong(loan.getClient().mobileNo())));
				phone.setTELENOTYPE(propertyEnv.getProperty("highmark.config.phone.type"));
				applicantPhoneList.add(phone);
				phones.setPhone(applicantPhoneList);
				applicantSegement.setPHONES(phones);
			}

			if(CommonMethodsUtil.isNotBlank(loan.getClient().gender())) {
				if(loan.getClient().gender().toData().getName().equalsIgnoreCase("Male")||loan.getClient().gender().toData().getName().equalsIgnoreCase("M"))
					applicantSegement.setGENDER("G01");
				else if(loan.getClient().gender().toData().getName().equalsIgnoreCase("Female")||loan.getClient().gender().toData().getName().equalsIgnoreCase("F"))
					applicantSegement.setGENDER("G02");
				else
					applicantSegement.setGENDER("G03");
			}



			//ID Segment
			Iterable<String> ary = Splitter.on(',').split(propertyEnv.getProperty("highmark.config.ID.data"));
			ArrayList<Integer> integerArrID = new ArrayList<Integer>();


			for(String count:ary) {
				try {
					integerArrID.add(Integer.parseInt(count));
				}
				catch(NumberFormatException formatEx)
				{
					integerArrID.add(-1);
				}
			}
			boolean isIDDatable = Boolean.valueOf(propertyEnv.getProperty("highmark.config.ID.dataTable.enable"));
			List<APPLICANTSEGMENT.IDS.ID> applicantIdList= new ArrayList<APPLICANTSEGMENT.IDS.ID>();
			if(isIDDatable) {
				GenericResultsetData results = this.readWriteNonCoreDataService.retrieveDataTableGenericResultSet(propertyEnv.getProperty("highmark.config.ID.dataTable.name"), loan.getClientId(),
						null, null);

				if(CommonMethodsUtil.isNotBlank(results.getData())&&CommonMethodsUtil.isNotBlank(integerArrID)&&results.getData().size()>0) {
					for(ResultsetRowData resultRowData : results.getData()) {
						for(int i=0;i<integerArrID.size();i++) {
							Integer indexValue = integerArrID.get(i); 
							if(indexValue>-1&&indexValue<resultRowData.getRow().size()) {
								String idValue =resultRowData.getRow().get(indexValue);
								if(CommonMethodsUtil.isNotBlank(idValue)) {
									APPLICANTSEGMENT.IDS.ID id = new APPLICANTSEGMENT.IDS.ID();
									id.setTYPE("ID0"+(i+1));
									id.setVALUE(idValue);
									applicantIdList.add(id);
								}
							}
						}
					}
				}
			}
			else {
				Collection<ClientIdentifierData> clientIDDataList = this.clientIdentifierService.retrieveClientIdentifiers(loan.getClientId());
				if(CommonMethodsUtil.isNotBlank(clientIDDataList)) {
					for(ClientIdentifierData clientIDData: clientIDDataList ) {
						Integer indexValue = integerArrID.indexOf(clientIDData.getDocumentType().getId().intValue());

						if(indexValue!=0) {
							String idValue =clientIDData.getDocumentKey();
							if(CommonMethodsUtil.isNotBlank(idValue)) {
								APPLICANTSEGMENT.IDS.ID id = new APPLICANTSEGMENT.IDS.ID();
								id.setTYPE("ID0"+indexValue+1);
								id.setVALUE(clientIDData.getDocumentKey());
								applicantIdList.add(id);
							}
						}

					}
				}

			}

			APPLICANTSEGMENT.IDS applicantIds= new APPLICANTSEGMENT.IDS();
			applicantIds.setId(applicantIdList);
			applicantSegement.setIDS(applicantIds);


			//Address Segemet 
			Iterable<String>  addressTypeArr = Splitter.on(',').split(propertyEnv.getProperty("highmark.config.address.data"));
			ArrayList<Integer> integerAddressType = new ArrayList<Integer>();


			for(String count:addressTypeArr) {
				try {
					integerAddressType.add(Integer.parseInt(count));
				}
				catch(NumberFormatException formatEx)
				{
					integerAddressType.add(-1);
				}
			}

			boolean isAddressDatable = Boolean.valueOf(propertyEnv.getProperty("highmark.config.address.dataTable.enable"));
			List<ADDRESSSEGMENT.ADDRESS> applicantAddressList= new ArrayList<ADDRESSSEGMENT.ADDRESS>();
			if(isAddressDatable) {
				GenericResultsetData results = this.readWriteNonCoreDataService.retrieveDataTableGenericResultSet(propertyEnv.getProperty("highmark.config.address.dataTable.name"), loan.getClientId(),
						null, null);
				Iterable<String>  aryAddress = Splitter.on(',').split(propertyEnv.getProperty("highmark.config.address.fieldData"));
				ArrayList<Integer> integerArrAddress = new ArrayList<Integer>();


				for(String count:aryAddress) {
					try {
						integerArrAddress.add(Integer.parseInt(count));
					}
					catch(NumberFormatException formatEx)
					{
						integerArrAddress.add(-1);
					}
				}

				if(CommonMethodsUtil.isNotBlank(results.getData())&&CommonMethodsUtil.isNotBlank(integerArrAddress)&&results.getData().size()>0) {
					for(ResultsetRowData resultRowData : results.getData()) {
						for(int i=0;i<integerAddressType.size()&&integerAddressType.get(i)>-1;i++) {
							boolean setAddress = true;
							ADDRESSSEGMENT.ADDRESS address = new ADDRESSSEGMENT.ADDRESS();
							address.setTYPE("D0"+(i+1));
							for(int j=0;j<integerArrAddress.size();j++) {
								Integer indexValue = integerArrAddress.get(j); 
								if(indexValue>-1&&indexValue<resultRowData.getRow().size()) {
									switch(j) {
									case 0:
										address.setADDRESS1(resultRowData.getRow().get(indexValue));
										break;
									case 1:
										address.setCITY(resultRowData.getRow().get(indexValue));
										break;
									case 2:
										String stateCode = resultRowData.getRow().get(indexValue);
										if(stateCode.length()==2)
											address.setSTATE(stateCode);
										break;
									case 3:
										try {
											address.setPIN(Long.parseLong(resultRowData.getRow().get(indexValue)));
										}
										catch(NumberFormatException formatEx)
										{
											setAddress = false;
											//										return new GenericResponseVO(" ",400,"Postal Code is Invalid",new java.util.Date().getTime());
										}
										break;
									}

								}
							}
							if(setAddress&&CommonMethodsUtil.isBlank(address.getSTATE())) {
								try {
									String defaultState = propertyEnv.getProperty("highmark.config.address.state.default");
									if(CommonMethodsUtil.isNotBlank(defaultState)&&defaultState.length()==2)
									{
										address.setSTATE(defaultState);
									}
								}
								catch(Exception e) {
									setAddress = false;
									//									return new GenericResponseVO(" ",400,"State Code is invalid",new java.util.Date().getTime());
								}
							}
							if(setAddress)
								applicantAddressList.add(address);
						}
					}
				}
			}
			else {

				Collection<AddressData> clientAddressDataList = this.addressService.retrieveAllClientAddress(loan.getClientId());
				if(CommonMethodsUtil.isNotBlank(clientAddressDataList)) {
					for(AddressData clientAddressData: clientAddressDataList ) {
						Integer indexValue = integerAddressType.indexOf(clientAddressData.getAddressTypeId().intValue());
						if(indexValue!=0) {
							boolean setAddress = true;
							ADDRESSSEGMENT.ADDRESS address = new ADDRESSSEGMENT.ADDRESS();
							address.setTYPE("D0"+(indexValue+1));
							address.setADDRESS1(clientAddressData.getAddressLine1()+clientAddressData.getAddressLine2()+clientAddressData.getAddressLine3());
							address.setCITY(clientAddressData.getCity());
							if(clientAddressData.getStateName().length()==2)
								address.setSTATE(clientAddressData.getStateName());
							else {
								CodeValueData codeValueData = codeValueReadPlatformService.retrieveCodeValue(clientAddressData.getStateProvinceId());
								if(codeValueData.getDescription().length()==2)
									address.setSTATE(clientAddressData.getStateName());
								else {
									try {
										String defaultState = propertyEnv.getProperty("highmark.config.address.state.default");
										if(CommonMethodsUtil.isNotBlank(defaultState)&&defaultState.length()==2)
										{
											address.setSTATE(defaultState);
										}
									}
									catch(Exception e) {
										setAddress = false;
										//											return new GenericResponseVO(" ",400,"State Code is invalid",new java.util.Date().getTime());
									}
								}
							}
							try {
								address.setPIN(Long.parseLong(clientAddressData.getPostalCode()));
							}
							catch(NumberFormatException formatEx)
							{
								setAddress = false;
								//									return new GenericResponseVO(" ",400,"Postal Code is Invalid",new java.util.Date().getTime());
							}
							if(setAddress)
								applicantAddressList.add(address);
						}

					}
				}

			}


			ADDRESSSEGMENT applicantAddress = new ADDRESSSEGMENT();
			applicantAddress.setAddress(applicantAddressList);
			inquiry.setADDRESSSEGMENT(applicantAddress);
			inquiry.setAPPLICANTSEGMENT(applicantSegement);
			inquiry.setAPPLICATIONSEGMENT(applicationSegment);
			requestXml.setINQUIRY(inquiry);

			JAXBContext jaxbContextRequest = JAXBContext.newInstance(REQUESTREQUESTFILE.class);
			Marshaller jaxbMarshaller = jaxbContextRequest.createMarshaller();
			//			jaxbMarshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);

			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(requestXml, sw);
			String xmlString = sw.toString();

			logger.info("High Mark Inquiry Request XML - "+ xmlString);

			String requestUrl = propertyEnv.getProperty("highmark.url.indvReqUrl");

			//			String url = "https://test.crifhighmark.com/Inquiry/doGet.service/requestResponse";
			System.setProperty("jsse.enableSNIExtension", propertyEnv.getProperty("highmark.ssl.enableSNIExtension"));
			URL obj = new URL(requestUrl);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			//add reuqest header
			con.setRequestMethod("POST");
			//			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setRequestProperty("Content-Type", "application/xml");
			con.setRequestProperty("userId",propertyEnv.getProperty("highmark.userid"));
			con.setRequestProperty("password", propertyEnv.getProperty("highmark.password"));
			con.setRequestProperty("mbrid", propertyEnv.getProperty("highmark.mbrid"));
			con.setRequestProperty("productType",propertyEnv.getProperty("highmark.productType"));
			con.setRequestProperty("productVersion", propertyEnv.getProperty("highmark.productVersion"));
			con.setRequestProperty("reqVolType", propertyEnv.getProperty("highmark.reqVolType"));
			con.setRequestProperty("requestXML",xmlString);

			////System.out.println("Request Data inside credit Request "+con.getRequestProperties());
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes("");
			wr.flush();
			wr.close();

			if (con.getResponseCode()==200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream(),Charset.defaultCharset()));
				String inputLine;
				StringBuilder response = new StringBuilder();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				con.disconnect();
				JAXBContext jaxbContext = JAXBContext.newInstance(REPORTFILE.class);
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

				StringReader reader = new StringReader(response.toString());
				REPORTFILE report = (REPORTFILE) unmarshaller.unmarshal(reader);

				logger.info("High Mark Inquiry Response - "+ response.toString());

				//					TimeUnit.SECONDS.sleep(8);

				responseVO = this.retreiveCreditReport(report,loan.getId(),"loan");
				//					responseVO = new GenericResponseVO(report,200,"Report Fetched Successfully",new java.util.Date().getTime());

				return responseVO;
			}
			else
			{
				con.disconnect();
				responseVO = new GenericResponseVO("",401,"Report Fetching Failed",new java.util.Date().getTime());
				return responseVO;
			}

		} catch (Exception ex) {
			logger.error("Exception in fecthing HighMark Report - ");
			ex.printStackTrace(System.out);
			responseVO = new GenericResponseVO(ex.getMessage(),500,"Exception in fecthing HighMark Report",new java.util.Date().getTime());
		}

		return responseVO;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public GenericResponseVO retreiveCreditReport(REPORTFILE issueReport,long appTableId,String inquiryFor) {

		////System.out.println(issueReport.toString()+"--------");
		////System.out.println(appTableId +"--------------");
		////System.out.println(inquiryFor+"--------------------");




		GenericResponseVO responseVO= null ;
		try {

			if (CommonMethodsUtil.isBlank(issueReport.getINQUIRYSTATUS())&&CommonMethodsUtil.isBlank(issueReport.getINQUIRYSTATUS().getINQUIRY())) {
				return new GenericResponseVO(" ",400,"Bad Request",new java.util.Date().getTime());
			}
			if (CommonMethodsUtil.isBlank(issueReport.getINQUIRYSTATUS().getINQUIRY().size()>0)&&CommonMethodsUtil.isBlank(issueReport.getINQUIRYSTATUS().getINQUIRY().get(0).getREPORTID())) {
				return new GenericResponseVO(" ",400,"Please check the input request",new java.util.Date().getTime());
			}


			org.apache.fineract.infrastructure.highmark.generated.HM_Issue_Single.REQUESTREQUESTFILE requestXml = new org.apache.fineract.infrastructure.highmark.generated.HM_Issue_Single.REQUESTREQUESTFILE();
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");  
			LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
			String currentTime = dtf.format(now).toString();
			org.apache.fineract.infrastructure.highmark.generated.HM_Issue_Single.HEADERSEGMENT hs = new org.apache.fineract.infrastructure.highmark.generated.HM_Issue_Single.HEADERSEGMENT();

			hs.setINQDTTM(currentTime);
			hs.setAUTHFLG("Y");
			hs.setAUTHTITLE("USER");
			hs.setMEMBERPREOVERRIDE("N");
			hs.setREQACTNTYP("AT02");
			hs.setRESFRMT("XML/HTML");
			hs.setRESFRMTEMBD("Y");
			hs.setTESTFLG(propertyEnv.getProperty("highmark.reqTestFLG"));
			hs.setREQVOLTYP(propertyEnv.getProperty("highmark.reqVolType"));
			hs.setSUBMBRID(propertyEnv.getProperty("highmark.subMbrid"));

			org.apache.fineract.infrastructure.highmark.generated.HM_Issue_Single.INQUIRY inquiry = new org.apache.fineract.infrastructure.highmark.generated.HM_Issue_Single.INQUIRY();
			inquiry.setINQUIRYUNIQUEREFNO(issueReport.getINQUIRYSTATUS().getINQUIRY().get(0).getINQUIRYUNIQUEREFNO());
			inquiry.setREPORTID(issueReport.getINQUIRYSTATUS().getINQUIRY().get(0).getREPORTID());
			inquiry.setREQUESTDTTM(issueReport.getINQUIRYSTATUS().getINQUIRY().get(0).getRESPONSEDTTM());
			List<org.apache.fineract.infrastructure.highmark.generated.HM_Issue_Single.INQUIRY> inquiryList = new ArrayList<org.apache.fineract.infrastructure.highmark.generated.HM_Issue_Single.INQUIRY>();
			inquiryList.add(inquiry);

			requestXml.setHEADERSEGMENT(hs);
			requestXml.setInquiry(inquiryList);

			JAXBContext jaxbContextRequest = JAXBContext.newInstance(org.apache.fineract.infrastructure.highmark.generated.HM_Issue_Single.REQUESTREQUESTFILE.class);
			Marshaller jaxbMarshaller = jaxbContextRequest.createMarshaller();


			//			jaxbMarshaller.setProperty("com.sun.xml.bind.xmlDeclaration", Boolean.FALSE);
			jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);

			StringWriter sw = new StringWriter();
			jaxbMarshaller.marshal(requestXml, sw);
			String xmlString = sw.toString();

			logger.info("High Mark Issue Request XML - "+ xmlString);

			TimeUnit.SECONDS.sleep(Integer.valueOf(propertyEnv.getProperty("highmark.sleep.seconds")));

			String requestUrl = propertyEnv.getProperty("highmark.url.indvReqUrl");

			System.setProperty("jsse.enableSNIExtension", propertyEnv.getProperty("highmark.ssl.enableSNIExtension"));
			URL obj = new URL(requestUrl);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			//add reuqest header
			con.setRequestMethod("POST");
			//			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			con.setRequestProperty("Content-Type", "application/xml");
			con.setRequestProperty("userId",propertyEnv.getProperty("highmark.userid"));
			con.setRequestProperty("password", propertyEnv.getProperty("highmark.password"));
			con.setRequestProperty("mbrid", propertyEnv.getProperty("highmark.mbrid"));
			con.setRequestProperty("productType",propertyEnv.getProperty("highmark.productType"));
			con.setRequestProperty("productVersion", propertyEnv.getProperty("highmark.productVersion"));
			con.setRequestProperty("reqVolType", propertyEnv.getProperty("highmark.reqVolType"));
			con.setRequestProperty("requestXML",xmlString);

			////System.out.println("inside anfsd");
			// Send post request
			con.setDoOutput(true);
			////System.out.println("request credit report "+con.getRequestProperties());
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes("");
			wr.flush();
			wr.close();

			if (con.getResponseCode()==200) {

//									Reader fr=Files.newBufferedReader(Paths.get("F:\\ChethanaChits\\Backend\\cchpl_backend\\response.xml"), StandardCharsets.UTF_8);  
//									BufferedReader in = new BufferedReader(fr);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream(),StandardCharsets.UTF_8));
				String inputLine;
				StringBuilder response = new StringBuilder();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				con.disconnect();

				try {
					JAXBContext jaxbContext = JAXBContext.newInstance(INDVREPORTFILE.class);
					Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

					StringReader reader = new StringReader(response.toString());
					INDVREPORTFILE report = (INDVREPORTFILE) unmarshaller.unmarshal(reader);
					if(CommonMethodsUtil.isNotBlank(report.getINDVREPORTS())) {
						if(CommonMethodsUtil.isNotBlank(appTableId)&&CommonMethodsUtil.isNotBlank(inquiryFor)) {
							try {
								String propertyName = "highmark.config."+inquiryFor+".creditScore.dataTable.name";
								String dataTableName= propertyEnv.getProperty(propertyName);
								List<SCORES.SCORE> scores = report.getINDVREPORTS().getINDVREPORT().getSCORES().getSCORE();					

								if(CommonMethodsUtil.isNotBlank(scores)&&CommonMethodsUtil.isNotBlank(dataTableName)) {
									for(SCORE score:scores) {
										JsonObject jsonObject = new JsonObject();
										jsonObject.addProperty("bureau", "High Mark");
										jsonObject.addProperty("scoretype", score.getSCORETYPE());
										jsonObject.addProperty("scorevalue", score.getSCOREVALUE());
										jsonObject.addProperty("scorecomments", score.getSCORECOMMENTS());
										if(CommonMethodsUtil.isNotBlank(report.getINDVREPORTS().getINDVREPORT())) {
											jsonObject.addProperty("reportid", report.getINDVREPORTS().getINDVREPORT().getHEADER().getREPORTID());
											jsonObject.addProperty("dateofissue", report.getINDVREPORTS().getINDVREPORT().getHEADER().getDATEOFISSUE());
										}

										if(this.creditRepository.existsById(this.getId()))
										{
											this.creditreportswriteplatformservice.updateCreditReport(this.getId(), jsonObject.toString());
										}
										else
										{
											this.creditreportswriteplatformservice.addCreditReport(this.getId(), jsonObject.toString());
										}
										
										Boolean isCheck = this.checkCibil(this.getId());
										if(!isCheck)
										{
											System.out.println("inside check");
											JsonObject data = new JsonObject();
											data.addProperty("locale", "en");
											data.addProperty("dateFormat", "dd MMMM yyyy");
											LocalDate date = LocalDate.now(ZoneId.systemDefault());
											DateTimeFormatter dt =  DateTimeFormatter.ofPattern("dd MMMM yyyy"); 
											String activationDate = date.format(dt);
											data.addProperty("activationDate", activationDate);
											this.clientsApiResource.activate(this.getId(), "activate", data.toString());
										}
									}
								}
							}
							catch(Exception ex) {
								logger.error("Exception in storing Highmark Scores in datatable - ");
								ex.printStackTrace(System.out);
							}
							try {
								final DocumentCommand documentCommand = new DocumentCommand(null, null, inquiryFor.toLowerCase()+"s", appTableId, report.getINDVREPORTS().getINDVREPORT().getHEADER().getREPORTID()+"_"+report.getINDVREPORTS().getINDVREPORT().getHEADER().getDATEOFISSUE(), report.getINDVREPORTS().getINDVREPORT().getPRINTABLEREPORT().getFILENAME(),
										Long.parseLong("102400"), report.getINDVREPORTS().getINDVREPORT().getPRINTABLEREPORT().getTYPE(), "High Mark Reports", null,null);
								InputStream stream = new ByteArrayInputStream(report.getINDVREPORTS().getINDVREPORT().getPRINTABLEREPORT().getCONTENT().getBytes(StandardCharsets.UTF_8));
								this.documentWritePlatformService.createDocument(documentCommand, stream);
							}
							catch(Exception ex) {
								logger.error(ex.getMessage(), ex);
							}
						}
					}
					//					else if(CommonMethodsUtil.isNotBlank(report.getINQUIRYSTATUS())&&report.getINQUIRYSTATUS().getINQUIRY().getRESPONSETYPE().equalsIgnoreCase("INPROCESS")) {
					//						retreiveCreditReport(issueReport,appTableId,inquiryFor);
					//					}


					responseVO = new GenericResponseVO(report,200,"Report Fetched Successfully",new java.util.Date().getTime());
				}
				catch(Exception e) {
					JAXBContext jaxbContext = JAXBContext.newInstance(REPORTFILE.class);
					Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

					StringReader reader = new StringReader(response.toString());
					REPORTFILE report = (REPORTFILE) unmarshaller.unmarshal(reader);
					responseVO = new GenericResponseVO(report,400,"Report Fetching Failed",new java.util.Date().getTime());
				}

				//					logger.info("High Mark Issue request response - "
				//							+response.toString());
				return responseVO;
			}
			else
			{
				con.disconnect();
				responseVO = new GenericResponseVO("",401,"Report Fetching Failed",new java.util.Date().getTime());
				return responseVO;
			}

		} catch (Exception ex) {
			logger.error("Exception in issuing HighMark Report - ");
			ex.printStackTrace(System.out);
			responseVO = new GenericResponseVO(ex.getMessage(),500,"Exception in issuing HighMark Report",new java.util.Date().getTime());
		}

		return responseVO;
	}


	private Boolean checkCibil(Long clientId) {


		Collection<CreditReport> read = creditreadPlatformService.getreport(clientId);
		List<CreditReport>  CreditReportList = new ArrayList<CreditReport>(read);


		final GlobalConfigurationPropertyData Configuredscore = this.configurationReadPlatformService
				.retrieveGlobalConfiguration("Credit-Score");
		////System.out.println(Configuredscore.getValue());
		for(int i=0;i<CreditReportList.size();i++)
		{
			Object obj=CreditReportList.get(i);
			if(obj instanceof CreditReport)
			{
				CreditReport scorereport=(CreditReport)obj;
				Long ClientScore=Long.parseLong(scorereport.getScorevalue());  
				if(ClientScore>25l && ClientScore<Configuredscore.getValue())
				{
					System.out.println("true");
					return true;
				}
				break;
			}
		}
		return false;

	}


}