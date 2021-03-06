/*
 * Copyright 2014 Space Dynamics Laboratory - Utah State University Research Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.usu.sdl.openstorefront.web.rest.model;

import au.com.bytecode.opencsv.CSVWriter;
import edu.usu.sdl.openstorefront.doc.DataType;
import edu.usu.sdl.openstorefront.service.io.ExportImport;
import edu.usu.sdl.openstorefront.storage.model.AttributeType;
import edu.usu.sdl.openstorefront.util.Convert;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;

/**
 *
 * @author dshurtleff
 */
public class AttributeTypeView
		implements ExportImport
{

	@NotNull
	private String type;

	@NotNull
	private String description;

	@NotNull
	private boolean visibleFlg;

	@NotNull
	private boolean requiredFlg;

	@NotNull
	private boolean architectureFlg;

	@NotNull
	private boolean importantFlg;

	@NotNull
	private boolean allowMultipleFlg;

	@NotNull
	private String activeStatus;

	@DataType(AttributeCodeView.class)
	private List<AttributeCodeView> codes = new ArrayList<>();

	public AttributeTypeView()
	{
	}

	public static AttributeTypeView toView(AttributeType attributeType)
	{
		AttributeTypeView attributeTypeView = new AttributeTypeView();
		attributeTypeView.setType(attributeType.getAttributeType());
		attributeTypeView.setAllowMultipleFlg(Convert.toBoolean(attributeType.getAllowMultipleFlg()));
		attributeTypeView.setArchitectureFlg(Convert.toBoolean(attributeType.getArchitectureFlg()));
		attributeTypeView.setDescription(attributeType.getDescription());
		attributeTypeView.setImportantFlg(Convert.toBoolean(attributeType.getImportantFlg()));
		attributeTypeView.setRequiredFlg(Convert.toBoolean(attributeType.getRequiredFlg()));
		attributeTypeView.setVisibleFlg(Convert.toBoolean(attributeType.getVisibleFlg()));
		attributeTypeView.setActiveStatus(attributeType.getActiveStatus());

		return attributeTypeView;
	}

	@Override
	public String export()
	{
		StringWriter stringWriter = new StringWriter();
		CSVWriter writer = new CSVWriter(stringWriter);
		codes.stream().forEach((code) -> {
			writer.writeNext(new String[]{getType(),
				getDescription(),
				Boolean.toString(getArchitectureFlg()),
				Boolean.toString(getVisibleFlg()),
				Boolean.toString(getImportantFlg()),
				Boolean.toString(getRequiredFlg()),
				code.getCode(),
				code.getLabel(),
				code.getDescription(),
				code.getFullTextLink(),
				code.getGroupCode(),
				code.getSortOrder() == null ? "" : code.getSortOrder().toString(),
				code.getArchitectureCode(),
				code.getBadgeUrl()
			});
		});
		return stringWriter.toString();
	}

	@Override
	public void importData(String[] data)
	{
		throw new UnsupportedOperationException("Use Parser");
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public List<AttributeCodeView> getCodes()
	{
		return codes;
	}

	public void setCodes(List<AttributeCodeView> codes)
	{
		this.codes = codes;
	}

	public boolean getVisibleFlg()
	{
		return visibleFlg;
	}

	public void setVisibleFlg(boolean visibleFlg)
	{
		this.visibleFlg = visibleFlg;
	}

	public boolean getRequiredFlg()
	{
		return requiredFlg;
	}

	public void setRequiredFlg(boolean requiredFlg)
	{
		this.requiredFlg = requiredFlg;
	}

	public boolean getArchitectureFlg()
	{
		return architectureFlg;
	}

	public void setArchitectureFlg(boolean architectureFlg)
	{
		this.architectureFlg = architectureFlg;
	}

	public boolean getImportantFlg()
	{
		return importantFlg;
	}

	public void setImportantFlg(boolean importantFlg)
	{
		this.importantFlg = importantFlg;
	}

	public boolean getAllowMultipleFlg()
	{
		return allowMultipleFlg;
	}

	public boolean isAllowMultipleFlg()
	{
		return allowMultipleFlg;
	}

	public void setAllowMultipleFlg(boolean allowMultipleFlg)
	{
		this.allowMultipleFlg = allowMultipleFlg;
	}

	public String getActiveStatus()
	{
		return activeStatus;
	}

	public void setActiveStatus(String activeStatus)
	{
		this.activeStatus = activeStatus;
	}

}
