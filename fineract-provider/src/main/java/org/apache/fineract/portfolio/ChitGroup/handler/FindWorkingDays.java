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
package org.apache.fineract.portfolio.ChitGroup.handler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FindWorkingDays 
{
	public List<LocalDate> findWorkingDays(LocalDate localdate,Boolean isStart)
	{
		List<LocalDate> workingDays = new ArrayList<>();
		if(isStart)
		{
			LocalDate localdatebyUser = localdate.withDayOfMonth(1);
			int size =localdatebyUser.lengthOfMonth() - localdatebyUser.getDayOfMonth();
			for(int i = 0 ; i<=size ; i ++)
			{
				LocalDate temporaryValue = localdatebyUser;
				if(!temporaryValue.getDayOfWeek().name().equals("SUNDAY"))
				{
					workingDays.add(temporaryValue);
				}
				localdatebyUser = temporaryValue.plusDays(1);
			}
		}
		else
		{
			int date = localdate.getDayOfMonth();
			LocalDate localdatebyUser = localdate;
			int size = localdatebyUser.lengthOfMonth()-date;
			for(int i = 0 ; i<=size ; i ++)
			{
				LocalDate temporaryValue = localdatebyUser;
				if(!temporaryValue.getDayOfWeek().name().equals("SUNDAY"))
				{
					workingDays.add(temporaryValue);
				}
				localdatebyUser = temporaryValue.plusDays(1);
			}
		}
		return workingDays;
	}

	public LocalDate nextWorkingDay(LocalDate givenDate)
	{
		LocalDate val = null;
		int date = givenDate.getDayOfMonth();
		int month = givenDate.getMonthValue();
		LocalDate d1 = givenDate.plusDays(1l);
		if( date!=givenDate.lengthOfMonth())
		{
			if(d1.getDayOfWeek().name().equals("SUNDAY"))
			{
				d1 = d1.plusDays(1l);
			}

			val =  d1;
		}
		else
		{
			val =  givenDate;
		}
		
		if(val.getMonthValue()!=month)
		{
			return givenDate;
		}
		return val;

	}
	
	public LocalDate nextWorkingDayofNextMonth(LocalDate givenDate)
	{
			LocalDate d1 = givenDate.plusDays(1l);

			if(d1.getDayOfWeek().name().equals("SUNDAY"))
			{
				d1 = d1.plusDays(1l);
			}
			return d1;
	}
	
	public LocalDate validateworkingDayorNot(LocalDate givenDate)
	{
		if(givenDate.getDayOfWeek().name().equals("SUNDAY"))
		{
			return givenDate.plusDays(1l);
		}
		return givenDate;
	}
}
