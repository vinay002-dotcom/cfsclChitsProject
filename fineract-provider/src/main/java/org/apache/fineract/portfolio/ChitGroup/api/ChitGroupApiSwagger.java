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
package org.apache.fineract.portfolio.ChitGroup.api;


import java.util.Set;

import io.swagger.v3.oas.annotations.media.Schema;
public class ChitGroupApiSwagger
{
	private ChitGroupApiSwagger()
	{
		
	}
	 @Schema(description = "PostChitGroupRequest")
	    public static final class PostChitGroupRequest {

	        private PostChitGroupRequest() {

	        }
	        @Schema(example = "1")
	        public Long id;
	        @Schema(example = "John")
	        public String name;
	 }
	 
	    @Schema(description = "PostChitGroupResponse")
	    public static final class CreateChitGroupResponse {

	        private CreateChitGroupResponse() {

	        }

	        @Schema(example = "1")
	        public Long id;
	        @Schema(example = "1")
	        public Long resourceId;
	    }
	    
	    @Schema(description = "GetChitGroupResponse")
	    public static final class RetrieveOneResponse {

	        private RetrieveOneResponse() {

	        }
	        @Schema(example = "1")
	        public Long id;
	        @Schema(example = "John")
	        public String name;
	 
	    }
		@Schema(description = "PostGroupsGroupIdRequest")
		public static final class PostGroupsGroupIdRequest {
	
			private PostGroupsGroupIdRequest() {}
	
			static final class PostGroupsGroupIdClients {
	
				private PostGroupsGroupIdClients() {}
	
				@Schema(example = "1")
				public Integer id;
			}
	
			public Set<PostGroupsGroupIdClients> clients;
		}
	
		@Schema(description = "PostGroupsGroupIdResponse")
		public static final class PostGroupsGroupIdResponse {
	
			private PostGroupsGroupIdResponse() {}
	
			@Schema(example = "1")
			public Integer resourceId;
		}
}
