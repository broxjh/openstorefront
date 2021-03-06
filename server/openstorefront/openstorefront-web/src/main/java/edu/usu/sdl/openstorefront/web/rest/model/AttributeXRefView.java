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

import edu.usu.sdl.openstorefront.doc.DataType;
import edu.usu.sdl.openstorefront.storage.model.AttributeXRefMap;
import edu.usu.sdl.openstorefront.storage.model.AttributeXRefType;
import java.util.List;

/**
 *
 * @author jlaw
 */
public class AttributeXRefView
{

	private AttributeXRefType type;

	@DataType(AttributeXRefMap.class)
	private List<AttributeXRefMap> map;

	public AttributeXRefView()
	{

	}

	/**
	 * @return the type
	 */
	public AttributeXRefType getType()
	{
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(AttributeXRefType type)
	{
		this.type = type;
	}

	/**
	 * @return the map
	 */
	public List<AttributeXRefMap> getMap()
	{
		return map;
	}

	/**
	 * @param map the map to set
	 */
	public void setMap(List<AttributeXRefMap> map)
	{
		this.map = map;
	}
}
