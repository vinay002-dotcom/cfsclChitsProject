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

//import java.io.FileReader;
import java.io.IOException;
// import java.io.Reader;
// import java.nio.file.Files;
// import java.nio.file.Paths;
// import java.util.Properties;
import java.util.concurrent.TimeUnit;

//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.context.annotation.Scope;
//import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
@Component


public class ExternalApiReadService
{
	
	
	 public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	 OkHttpClient client = new OkHttpClient.Builder()
		      .readTimeout(120, TimeUnit.MILLISECONDS)
		      .build();

	    public String post(String karza,String value,String url, String json,String timeout) throws IOException,Exception {
	    	
	    	int time = Integer.parseInt(timeout);
	        @SuppressWarnings("deprecation")
			RequestBody body = RequestBody.create(JSON, json);
	        Request request = new Request.Builder().header(karza,value)
	        		.url(url)
	        		.post(body)
	        		.build();
	        
	        OkHttpClient extendedTimeoutClient = client.newBuilder()
	        	      .readTimeout(time, TimeUnit.SECONDS)
	        	      .build();
	        try (Response response = extendedTimeoutClient.newCall(request).execute()) {
	            return response.body().string();
	        }
	    
	    }
}
