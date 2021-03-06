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
package edu.usu.sdl.openstorefront.service.message;

import edu.usu.sdl.openstorefront.service.transfermodel.ComponentAll;
import edu.usu.sdl.openstorefront.storage.model.UserMessage;
import edu.usu.sdl.openstorefront.storage.model.UserProfile;

/**
 * Holds message properties
 *
 * @author dshurtleff
 */
public class MessageContext
{

	private UserProfile userProfile;
	private UserMessage userMessage;
	private ComponentAll componentAll;
	private RecentChangeMessage recentChangeMessage;

	public MessageContext(UserProfile userProfile)
	{
		this.userProfile = userProfile;
	}

	public UserMessage getUserMessage()
	{
		return userMessage;
	}

	public void setUserMessage(UserMessage userMessage)
	{
		this.userMessage = userMessage;
	}

	public UserProfile getUserProfile()
	{
		return userProfile;
	}

	public void setUserProfile(UserProfile userProfile)
	{
		this.userProfile = userProfile;
	}

	public ComponentAll getComponentAll()
	{
		return componentAll;
	}

	public void setComponentAll(ComponentAll componentAll)
	{
		this.componentAll = componentAll;
	}

	public RecentChangeMessage getRecentChangeMessage()
	{
		return recentChangeMessage;
	}

	public void setRecentChangeMessage(RecentChangeMessage recentChangeMessage)
	{
		this.recentChangeMessage = recentChangeMessage;
	}

}
