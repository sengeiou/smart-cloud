/*******************************************************************************
 * Copyright 2017 Bstek
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.bstek.ureport.console.importexcel;

import com.bstek.ureport.console.RenderPageServletAction;
import com.bstek.ureport.console.util.ActionResult;
import com.bstek.ureport.definition.ReportDefinition;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @since 5月25日
 */
public class ImportExcelServletAction extends RenderPageServletAction {
	private List<ExcelParser> excelParsers=new ArrayList<ExcelParser>();
	public ImportExcelServletAction(){
		excelParsers.add(new HSSFExcelParser());
		excelParsers.add(new XSSFExcelParser());
	}
	@Override
	public void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String tempDir=System.getProperty("java.io.tmpdir");
		FileItemFactory factory=new DiskFileItemFactory(1000240,new File(tempDir));
		ServletFileUpload upload=new ServletFileUpload(factory);
		ReportDefinition report=null;
		try {
			List<FileItem> items=upload.parseRequest(req);
			for(FileItem item:items){
				String name=item.getName().toLowerCase();
				if((name.endsWith(".xls") || name.endsWith(".xlsx"))){
					InputStream inputStream=item.getInputStream();
					for(ExcelParser parser:excelParsers){
						if(parser.support(name)){
							report=parser.parse(inputStream);
							break;
						}
					}
					inputStream.close();
					break;
				}
			}
		} catch (Exception e) {
			writeObjectToJson(resp,ActionResult.fail("请选择一个合法的Excel导入"));
		}
		if(report!=null){
			writeObjectToJson(resp, ActionResult.success(report));
		}else{
			writeObjectToJson(resp,ActionResult.fail("请选择一个合法的Excel导入"));
		}
	}

	@Override
	public String url() {
		return "/import";
	}
}
