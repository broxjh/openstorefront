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

import edu.usu.sdl.openstorefront.doc.ConsumeField;
import edu.usu.sdl.openstorefront.doc.ValidValueType;
import edu.usu.sdl.openstorefront.service.manager.FileSystemManager;
import edu.usu.sdl.openstorefront.util.OpenStorefrontConstant;
import edu.usu.sdl.openstorefront.util.PK;
import edu.usu.sdl.openstorefront.util.ServiceUtil;
import edu.usu.sdl.openstorefront.validation.BasicHTMLSanitizer;
import edu.usu.sdl.openstorefront.validation.LinkSanitizer;
import edu.usu.sdl.openstorefront.validation.Sanitize;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author jlaw
 */
public class ComponentResource
		extends BaseComponent
{

	@PK(generated = true)
	@NotNull
	private String resourceId;

	@Size(min = 0, max = OpenStorefrontConstant.FIELD_SIZE_GENERAL_TEXT)
	private String fileName;

	@Size(min = 0, max = OpenStorefrontConstant.FIELD_SIZE_GENERAL_TEXT)
	private String originalName;

	@Size(min = 0, max = OpenStorefrontConstant.FIELD_SIZE_GENERAL_TEXT)
	private String mimeType;

	@NotNull
	@ConsumeField
	@Size(min = 1, max = OpenStorefrontConstant.FIELD_SIZE_CODE)
	@ValidValueType(value = {}, lookupClass = ResourceType.class)
	private String resourceType;

	@ConsumeField
	@Size(min = 0, max = OpenStorefrontConstant.FIELD_SIZE_URL)
	@Sanitize(LinkSanitizer.class)
	private String link;

	@ConsumeField
	@Size(min = 0, max = OpenStorefrontConstant.FIELD_SIZE_DESCRIPTION)
	@Sanitize(BasicHTMLSanitizer.class)
	private String description;

	@ConsumeField
	private Boolean restricted;

	public ComponentResource()
	{
	}

	@Override
	public int customCompareTo(BaseComponent o)
	{
		int value = ServiceUtil.compareObjects(getFileName(), ((ComponentResource) o).getFileName());
		if (value == 0) {
			value = ServiceUtil.compareObjects(getMimeType(), ((ComponentResource) o).getMimeType());
		}
		return value;
	}

	/**
	 * Get the path to the resource on disk. Note: this may be ran from a proxy
	 * so don't use variable directly
	 *
	 * @return Resource or null if this doesn't represent a disk resource
	 */
	public Path pathToResource()
	{
		Path path = null;
		if (StringUtils.isNotBlank(getFileName())) {
			File resourceDir = FileSystemManager.getDir(FileSystemManager.RESOURCE_DIR);
			path = Paths.get(resourceDir.getPath() + "/" + getFileName());
		}
		return path;
	}

	public String getResourceId()
	{
		return resourceId;
	}

	public void setResourceId(String resourceId)
	{
		this.resourceId = resourceId;
	}

	public String getLink()
	{
		return link;
	}

	public void setLink(String link)
	{
		this.link = link;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getResourceType()
	{
		return resourceType;
	}

	public void setResourceType(String resourceType)
	{
		this.resourceType = resourceType;
	}

	public Boolean getRestricted()
	{
		return restricted;
	}

	public void setRestricted(Boolean restricted)
	{
		this.restricted = restricted;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getOriginalName()
	{
		return originalName;
	}

	public void setOriginalName(String originalName)
	{
		this.originalName = originalName;
	}

	public String getMimeType()
	{
		return mimeType;
	}

	public void setMimeType(String mimeType)
	{
		this.mimeType = mimeType;
	}

}
