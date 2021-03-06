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
import edu.usu.sdl.openstorefront.util.OpenStorefrontConstant;
import edu.usu.sdl.openstorefront.util.ServiceUtil;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author jlaw
 */
public class ComponentAttributePk
		extends BasePK<ComponentAttributePk>
{

	@NotNull
	private String componentId;

	@NotNull
	@ConsumeField
	@Size(min = 1, max = OpenStorefrontConstant.FIELD_SIZE_CODE)
	private String attributeType;

	@NotNull
	@ConsumeField
	@Size(min = 1, max = OpenStorefrontConstant.FIELD_SIZE_CODE)
	private String attributeCode;

	public ComponentAttributePk()
	{
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || (obj instanceof ComponentAttributePk == false)) {
			return false;
		}
		ComponentAttributePk compareObj = (ComponentAttributePk) obj;
		return pkValue().equals(compareObj.pkValue());
	}

	@Override
	public int hashCode()
	{
		int hash = 5;
		hash = 37 * hash + Objects.hashCode(getComponentId());
		hash = 37 * hash + Objects.hashCode(getAttributeType());
		hash = 37 * hash + Objects.hashCode(getAttributeCode());
		return hash;
	}

	@Override
	public String pkValue()
	{
		return getComponentId() + ServiceUtil.COMPOSITE_KEY_SEPERATOR
				+ getAttributeType() + ServiceUtil.COMPOSITE_KEY_SEPERATOR
				+ getAttributeCode() + ServiceUtil.COMPOSITE_KEY_SEPERATOR;
	}

	@Override
	public String toString()
	{
		return pkValue();
	}

	public String getComponentId()
	{
		return componentId;
	}

	public void setComponentId(String componentId)
	{
		this.componentId = componentId;
	}

	public String getAttributeType()
	{
		return attributeType;
	}

	public void setAttributeType(String attributeType)
	{
		this.attributeType = attributeType;
	}

	public String getAttributeCode()
	{
		return attributeCode;
	}

	public void setAttributeCode(String attributeCode)
	{
		this.attributeCode = attributeCode;
	}

}
