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

import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
import java.util.Random;

public class ExternalApiServices
{
	public String getcibilScore()
	{
		 Random rand = new Random();
		 int random1 = rand.nextInt(16);
		 //////System.out.println(random1);
		 ArrayList<Cibil> CibilScore = new ArrayList<Cibil>();
		 CibilScore.add(new Cibil("User1","Mobile1",-1,"A-Very Low Risk"));
		 CibilScore.add(new Cibil("User2","Mobile2",0,"B-Very Low Risk"));
		 CibilScore.add(new Cibil("User3","Mobile3",14,"C-Low Risk"));
		 CibilScore.add(new Cibil("User4","Mobile4",17,"D-Low Risk"));
		 CibilScore.add(new Cibil("User5","Mobile5",18,"E-Low Risk"));
		 CibilScore.add(new Cibil("User6","Mobile6",200,"k-Very High Risk"));
		 CibilScore.add(new Cibil("User7","Mobile7",250,"L-Very High Risk"));
		 CibilScore.add(new Cibil("User8","Mobile8",300,"M-Very High Risk"));
		 CibilScore.add(new Cibil("User9","Mobile9",350,"I-High Risk"));
		 CibilScore.add(new Cibil("User10","Mobile10",375,"J-High Risk"));
		 CibilScore.add(new Cibil("User11","Mobile11",400,"F-Medium Risk"));
		 CibilScore.add(new Cibil("User12","Mobile12",425,"G-Medium Risk"));
		 CibilScore.add(new Cibil("User13","Mobile13",475,"H-Medium Risk"));
		 CibilScore.add(new Cibil("User14","Mobile14",550,"C-Low Risk"));
		 CibilScore.add(new Cibil("User15","Mobile15",650,"A-Very Low Risk"));
		 CibilScore.add(new Cibil("User16","Mobile16",750,"A-Very Low Risk"));
		 
		return CibilScore.get(random1).toString();
	}
}
