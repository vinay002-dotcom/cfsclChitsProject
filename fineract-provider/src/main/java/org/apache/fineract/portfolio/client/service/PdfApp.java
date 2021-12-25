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



import java.io.FileOutputStream;
import com.google.common.io.BaseEncoding;
import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;


//import java.awt.*;

//import java.io.*;
//import java.util.zip.*;

public class PdfApp
{
	public static String path = "";
	
	
  public static void pdf(String[] clientArr,String image,String filename) throws Exception
  {
	   
	  Document document = new Document();
		try
		{
			 path = "./secondaryid/"+filename+".pdf";
		    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(path));
		    
		    document.open();
		    		
		    		if(!image.isEmpty())
		    		{
		    			////System.out.println("inside image if");
		    			
		    			byte[] imageData = BaseEncoding.base64().decode(image);
		    			////System.out.println("after byte");
			    		  Image image2 = Image.getInstance(imageData); 
			    		  ////System.out.println("after getting image");
			    		  document.add(image2);
		    		}
		    		

		    
		    // we will receive image as encoded string as below
	
			
			
			
			////System.out.println("firstelement "+clientArr[0]);
			//we will have to convert json response as an array;
			//String str=response.replace("\"","");
			
//			////System.out.println(str);
//			
//			String regex = "[^A-Za-z0-9,]"+" ";
//			
//			Pattern pattern = Pattern.compile(regex);
//			
//			Matcher matcher = pattern.matcher(str);
//			
//			String res = matcher.replaceAll("");
//			
//			////System.out.println(res);
				
			
		    //Add Image
		  
		    		//Image.getInstance("img.png");
		    
		    //Fixed Positioning
		    //image1.setAbsolutePosition(100f, 550f);
		    //Scale to new height and new width of image
		    //image1.scaleAbsolute(200, 200);
		    //Add to document
		  

		    ////System.out.println("setting table ");
		    PdfPTable table = new PdfPTable(2);
	        table.setWidthPercentage(100); //Width 100%
	        table.setSpacingBefore(10f); //Space before table
	        table.setSpacingAfter(10f); //Space after table
	 
	        //Set Column widths
	        float[] columnWidths = {1f, 1f};
	        table.setWidths(columnWidths);
	        
	        for (int i=0; i < clientArr.length; i=i+2) {

	        	////System.out.println("inside loop for "+i);
	        	PdfPCell cell1 = new PdfPCell(new Paragraph(clientArr[i]));
		        cell1.setPaddingLeft(10);
//		        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
//		        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
	        	
	        	PdfPCell cell2 = new PdfPCell(new Paragraph(clientArr[i+1]));
		        cell1.setPaddingLeft(10);
//		        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
//		        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
		        cell1.setUseBorderPadding(true);
		        cell2.setUseBorderPadding(true);
		 
		        table.addCell(cell1);
		        table.addCell(cell2);
	        	
	        	
	        }
	        ////System.out.println("success out of for loop");
	        
	        /*PdfPCell cell1 = new PdfPCell(new Paragraph("Cell 1"));
	        cell1.setBorderColor(Color.BLUE);
	        cell1.setPaddingLeft(10);
	        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
	 
	        PdfPCell cell2 = new PdfPCell(new Paragraph("Cell 2"));
	        cell2.setBorderColor(Color.GREEN);
	        cell2.setPaddingLeft(10);
	        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
	        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
	 
	 
	        //To avoid having the cell border and the content overlap, if you are having thick cell borders
	        cell1.setUseBorderPadding(true);
	        cell2.setUseBorderPadding(true);
	 
	        table.addCell(cell1);
	        table.addCell(cell2);*/
	        
	        document.add(table);
	        ////System.out.println("success");
		    document.close();
		    writer.close();
		} catch (Exception e)
		{
		  ////System.out.println(e.getMessage());
		}
		

  }
}