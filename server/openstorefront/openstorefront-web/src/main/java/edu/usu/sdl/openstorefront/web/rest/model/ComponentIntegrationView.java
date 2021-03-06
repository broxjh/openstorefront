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

import edu.usu.sdl.openstorefront.service.ServiceProxy;
import edu.usu.sdl.openstorefront.storage.model.Component;
import edu.usu.sdl.openstorefront.storage.model.ComponentIntegration;
import edu.usu.sdl.openstorefront.storage.model.ComponentIntegrationConfig;
import java.util.Date;

/**
 *
 * @author jlaw
 */
public class ComponentIntegrationView
{

	private String componentId;
	private String componentName;
	private String refreshRate;
	private String status;
	private Date lastStartTime;
	private Date lastEndTime;
	private String activeStatus;
	private String errorMessage;

	public ComponentIntegrationView()
	{

	}

	public static ComponentIntegrationView toView(ComponentIntegration integration, ComponentIntegrationConfig integrationConfig)
	{
		ServiceProxy proxy = new ServiceProxy();
		ComponentIntegrationView view = new ComponentIntegrationView();
		Component temp = proxy.getPersistenceService().findById(Component.class, integration.getComponentId());

		view.setComponentName(temp.getName());
		view.setComponentId(integration.getComponentId());
		view.setRefreshRate(integration.getRefreshRate());
		view.setStatus(integration.getStatus());
		view.setLastEndTime(integration.getLastEndTime());
		view.setLastStartTime(integration.getLastStartTime());
		view.setActiveStatus(integration.getActiveStatus());
		view.setErrorMessage(integrationConfig.getErrorMessage());

		return view;
	}
	
	public static ComponentIntegrationView toView(ComponentIntegration integration)
	{
		ServiceProxy proxy = new ServiceProxy();
		ComponentIntegrationView view = new ComponentIntegrationView();
		Component temp = proxy.getPersistenceService().findById(Component.class, integration.getComponentId());

		view.setComponentName(temp.getName());
		view.setComponentId(integration.getComponentId());
		view.setRefreshRate(integration.getRefreshRate());
		view.setStatus(integration.getStatus());
		view.setLastEndTime(integration.getLastEndTime());
		view.setLastStartTime(integration.getLastStartTime());
		view.setActiveStatus(integration.getActiveStatus());

		return view;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getComponentId()
	{
		return componentId;
	}

	public void setComponentId(String componentId)
	{
		this.componentId = componentId;
	}

	public String getComponentName()
	{
		return componentName;
	}

	public void setComponentName(String componentName)
	{
		this.componentName = componentName;
	}

	public String getRefreshRate()
	{
		return refreshRate;
	}

	public void setRefreshRate(String refreshRate)
	{
		this.refreshRate = refreshRate;
	}

	/**
	 * @return the lastStartTime
	 */
	public Date getLastStartTime()
	{
		return lastStartTime;
	}

	/**
	 * @param lastStartTime the lastStartTime to set
	 */
	public void setLastStartTime(Date lastStartTime)
	{
		this.lastStartTime = lastStartTime;
	}

	/**
	 * @return the lastEndTime
	 */
	public Date getLastEndTime()
	{
		return lastEndTime;
	}

	/**
	 * @param lastEndTime the lastEndTime to set
	 */
	public void setLastEndTime(Date lastEndTime)
	{
		this.lastEndTime = lastEndTime;
	}

	/**
	 * @return the activeStatus
	 */
	public String getActiveStatus()
	{
		return activeStatus;
	}

	/**
	 * @param activeStatus the activeStatus to set
	 */
	public void setActiveStatus(String activeStatus)
	{
		this.activeStatus = activeStatus;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage()
	{
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

}
