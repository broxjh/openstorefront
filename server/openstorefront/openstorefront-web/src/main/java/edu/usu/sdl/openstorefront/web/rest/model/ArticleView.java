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

import edu.usu.sdl.openstorefront.doc.ConsumeField;
import edu.usu.sdl.openstorefront.service.ServiceProxy;
import edu.usu.sdl.openstorefront.storage.model.AttributeCode;
import edu.usu.sdl.openstorefront.storage.model.AttributeType;
import edu.usu.sdl.openstorefront.util.OpenStorefrontConstant;
import edu.usu.sdl.openstorefront.validation.BasicHTMLSanitizer;
import edu.usu.sdl.openstorefront.validation.Sanitize;
import edu.usu.sdl.openstorefront.validation.TextSanitizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;

/**
 * Topic landing page/article
 *
 * @author dshurtleff
 */
public class ArticleView
{

	private String attributeCode;
	private String attributeType;
	private String typeDescription;
	private String codeDescription;
	private String typeLongDescription;
	private String codeLongDescription;

	@ConsumeField
	@Size(min = 0, max = OpenStorefrontConstant.FIELD_SIZE_GENERAL_TEXT)
	@Sanitize(TextSanitizer.class)
	private String title;

	@ConsumeField
	@Size(min = 0, max = OpenStorefrontConstant.FIELD_SIZE_DETAILED_DESCRIPTION)
	@Sanitize(BasicHTMLSanitizer.class)
	private String description;

	@ConsumeField
	@NotNull
	@Size(min = 1, max = OpenStorefrontConstant.FIELD_SIZE_ARTICLE_SIZE)
	private String html;
	private Date updateDts;

	public ArticleView()
	{
	}

	public static ArticleView toView(AttributeCode attributeCode)
	{
		ServiceProxy service = new ServiceProxy();
		ArticleView articleView = new ArticleView();
		articleView.setAttributeCode(attributeCode.getAttributeCodePk().getAttributeCode());
		articleView.setAttributeType(attributeCode.getAttributeCodePk().getAttributeType());

		articleView.setCodeDescription(attributeCode.getLabel());
		articleView.setCodeLongDescription(attributeCode.getDescription());

		AttributeType type = service.getPersistenceService().findById(AttributeType.class, attributeCode.getAttributeCodePk().getAttributeType());
		if (type != null) {
			articleView.setTypeDescription(type.getDescription());
			articleView.setTypeLongDescription(type.getDescription());
		}
		if (attributeCode.getArticle() != null) {
			if (StringUtils.isNotBlank(attributeCode.getArticle().getTitle())) {
				articleView.setTitle(attributeCode.getArticle().getTitle());
			} else {
				articleView.setTitle(attributeCode.getLabel());
			}

			if (StringUtils.isNotBlank(attributeCode.getArticle().getDescription())) {
				articleView.setDescription(attributeCode.getArticle().getDescription());
			} else {
				articleView.setDescription(attributeCode.getDescription());
			}

			articleView.setUpdateDts(attributeCode.getArticle().getUpdateDts());
		}

		return articleView;
	}

	public static ArticleView toViewHtml(AttributeCode attributeCode, String content)
	{
		ArticleView articleView = toView(attributeCode);
		articleView.setHtml(content);
		return articleView;
	}

	public static List<ArticleView> toViewList(List<AttributeCode> attributes)
	{
		List<ArticleView> views = new ArrayList<>();
		attributes.stream().forEach((attribute) -> {
			views.add(ArticleView.toView(attribute));
		});
		return views;
	}

	public String getAttributeCode()
	{
		return attributeCode;
	}

	public void setAttributeCode(String attributeCode)
	{
		this.attributeCode = attributeCode;
	}

	public String getAttributeType()
	{
		return attributeType;
	}

	public void setAttributeType(String attributeType)
	{
		this.attributeType = attributeType;
	}

	public String getHtml()
	{
		return html;
	}

	public void setHtml(String html)
	{
		this.html = html;
	}

	public Date getUpdateDts()
	{
		return updateDts;
	}

	public void setUpdateDts(Date updateDts)
	{
		this.updateDts = updateDts;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * @return the typeDescription
	 */
	public String getTypeDescription()
	{
		return typeDescription;
	}

	/**
	 * @param typeDescription the typeDescription to set
	 */
	public void setTypeDescription(String typeDescription)
	{
		this.typeDescription = typeDescription;
	}

	/**
	 * @return the codeDescription
	 */
	public String getCodeDescription()
	{
		return codeDescription;
	}

	/**
	 * @param codeDescription the codeDescription to set
	 */
	public void setCodeDescription(String codeDescription)
	{
		this.codeDescription = codeDescription;
	}

	/**
	 * @return the typeLongDescription
	 */
	public String getTypeLongDescription()
	{
		return typeLongDescription;
	}

	/**
	 * @param typeLongDescription the typeLongDescription to set
	 */
	public void setTypeLongDescription(String typeLongDescription)
	{
		this.typeLongDescription = typeLongDescription;
	}

	/**
	 * @return the codeLongDescription
	 */
	public String getCodeLongDescription()
	{
		return codeLongDescription;
	}

	/**
	 * @param codeLongDescription the codeLongDescription to set
	 */
	public void setCodeLongDescription(String codeLongDescription)
	{
		this.codeLongDescription = codeLongDescription;
	}

}
