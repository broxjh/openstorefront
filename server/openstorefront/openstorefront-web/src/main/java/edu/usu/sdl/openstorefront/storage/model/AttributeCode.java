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
package edu.usu.sdl.openstorefront.storage.model;

import edu.usu.sdl.openstorefront.doc.APIDescription;
import edu.usu.sdl.openstorefront.doc.ConsumeField;
import edu.usu.sdl.openstorefront.service.io.ExportImport;
import edu.usu.sdl.openstorefront.util.OpenStorefrontConstant;
import edu.usu.sdl.openstorefront.util.PK;
import edu.usu.sdl.openstorefront.validation.BasicHTMLSanitizer;
import edu.usu.sdl.openstorefront.validation.LinkSanitizer;
import edu.usu.sdl.openstorefront.validation.Sanitize;
import edu.usu.sdl.openstorefront.validation.TextSanitizer;
import edu.usu.sdl.openstorefront.web.rest.model.AttributeCodeView;
import java.util.Objects;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author jlaw
 */
@APIDescription("Attribute code are used to link metadata and create articles on topics")
public class AttributeCode
		extends BaseEntity
		implements ExportImport
{

	@PK
	@NotNull
	@ConsumeField
	private AttributeCodePk attributeCodePk;

	@Size(min = 0, max = OpenStorefrontConstant.FIELD_SIZE_GENERAL_TEXT)
	@Sanitize(TextSanitizer.class)
	@ConsumeField
	private String architectureCode;

	@NotNull
	@Size(min = 1, max = OpenStorefrontConstant.FIELD_SIZE_GENERAL_TEXT)
	@Sanitize(TextSanitizer.class)
	@ConsumeField
	private String label;

	@Size(min = 0, max = OpenStorefrontConstant.FIELD_SIZE_DETAILED_DESCRIPTION)
	@Sanitize(BasicHTMLSanitizer.class)
	@ConsumeField
	private String description;

	@OneToOne(orphanRemoval = true)
	@ConsumeField
	private Article article;

	@Size(min = 0, max = OpenStorefrontConstant.FIELD_SIZE_URL)
	@Sanitize(LinkSanitizer.class)
	@ConsumeField
	private String detailUrl;

	@Size(min = 0, max = OpenStorefrontConstant.FIELD_SIZE_URL)
	@Sanitize(LinkSanitizer.class)
	@ConsumeField
	private String badgeUrl;

	@Size(min = 0, max = OpenStorefrontConstant.FIELD_SIZE_GENERAL_TEXT)
	@Sanitize(TextSanitizer.class)
	@ConsumeField
	private String groupCode;

	@ConsumeField
	private Integer sortOrder;

	public static final String DI2ELEVEL_NA = "NA";
	public static final String DI2ELEVEL_LEVEL0 = "LEVEL0";
	public static final String DI2ELEVEL_LEVEL1 = "LEVEL1";
	public static final String DI2ELEVEL_LEVEL2 = "LEVEL2";
	public static final String DI2ELEVEL_LEVEL3 = "LEVEL3";

	public AttributeCode()
	{
	}

	/**
	 * Gets the correct architecture code
	 *
	 * @return code
	 */
	public String architectureCode()
	{
		if (StringUtils.isNotBlank(getArchitectureCode())) {
			return getArchitectureCode();
		} else {
			return getAttributeCodePk().getAttributeCode();
		}
	}

	@Override
	public String export()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void importData(String[] data)
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int hashCode()
	{
		int hash = 7;
		hash = 79 * hash + Objects.hashCode(this.attributeCodePk);
		hash = 79 * hash + Objects.hashCode(this.label);
		hash = 79 * hash + Objects.hashCode(this.description);
		hash = 79 * hash + Objects.hashCode(this.architectureCode);
		hash = 79 * hash + Objects.hashCode(this.badgeUrl);
		hash = 79 * hash + Objects.hashCode(this.article);
		hash = 79 * hash + Objects.hashCode(this.detailUrl);
		hash = 79 * hash + Objects.hashCode(this.groupCode);
		hash = 79 * hash + Objects.hashCode(this.sortOrder);
		return hash;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AttributeCode other = (AttributeCode) obj;
		if (!Objects.equals(this.attributeCodePk, other.attributeCodePk)) {
			return false;
		}
		if (!Objects.equals(this.label, other.label)) {
			return false;
		}
		if (!Objects.equals(this.description, other.description)) {
			return false;
		}
		if (!Objects.equals(this.article, other.article)) {
			return false;
		}
		if (!Objects.equals(this.architectureCode, other.architectureCode)) {
			return false;
		}
		if (!Objects.equals(this.badgeUrl, other.badgeUrl)) {
			return false;
		}
		if (!Objects.equals(this.detailUrl, other.detailUrl)) {
			return false;
		}
		if (!Objects.equals(this.groupCode, other.groupCode)) {
			return false;
		}
		if (!Objects.equals(this.sortOrder, other.sortOrder)) {
			return false;
		}
		return true;
	}

	public void copyFromView(AttributeCodeView code, AttributeCodePk attributeCodePk)
	{
		setAttributeCodePk(attributeCodePk);
		setDescription(code.getDescription());
		setDetailUrl(code.getFullTextLink());
		setArchitectureCode(code.getArchitectureCode());
		setBadgeUrl(code.getBadgeUrl());
		setGroupCode(code.getGroupCode());
		setLabel(code.getLabel());
		setSortOrder(code.getSortOrder());
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getDetailUrl()
	{
		return detailUrl;
	}

	public void setDetailUrl(String detailUrl)
	{
		this.detailUrl = detailUrl;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}

	public AttributeCodePk getAttributeCodePk()
	{
		return attributeCodePk;
	}

	public void setAttributeCodePk(AttributeCodePk attributeCodePk)
	{
		this.attributeCodePk = attributeCodePk;
	}

	public Integer getSortOrder()
	{
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder)
	{
		this.sortOrder = sortOrder;
	}

	public String getGroupCode()
	{
		return groupCode;
	}

	public void setGroupCode(String groupCode)
	{
		this.groupCode = groupCode;
	}

	public Article getArticle()
	{
		return article;
	}

	public void setArticle(Article article)
	{
		this.article = article;
	}

	public String getArchitectureCode()
	{
		return architectureCode;
	}

	public void setArchitectureCode(String architectureCode)
	{
		this.architectureCode = architectureCode;
	}

	public String getBadgeUrl()
	{
		return badgeUrl;
	}

	public void setBadgeUrl(String badgeUrl)
	{
		this.badgeUrl = badgeUrl;
	}

}
