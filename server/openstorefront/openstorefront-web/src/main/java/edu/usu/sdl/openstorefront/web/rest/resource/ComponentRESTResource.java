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
package edu.usu.sdl.openstorefront.web.rest.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.usu.sdl.openstorefront.doc.APIDescription;
import edu.usu.sdl.openstorefront.doc.DataType;
import edu.usu.sdl.openstorefront.doc.RequireAdmin;
import edu.usu.sdl.openstorefront.doc.RequiredParam;
import edu.usu.sdl.openstorefront.exception.OpenStorefrontRuntimeException;
import edu.usu.sdl.openstorefront.service.manager.JobManager;
import edu.usu.sdl.openstorefront.service.query.QueryByExample;
import edu.usu.sdl.openstorefront.service.query.QueryType;
import edu.usu.sdl.openstorefront.service.transfermodel.ComponentAll;
import edu.usu.sdl.openstorefront.sort.BeanComparator;
import edu.usu.sdl.openstorefront.sort.SortUtil;
import edu.usu.sdl.openstorefront.storage.model.AttributeCode;
import edu.usu.sdl.openstorefront.storage.model.AttributeCodePk;
import edu.usu.sdl.openstorefront.storage.model.BaseComponent;
import edu.usu.sdl.openstorefront.storage.model.BaseEntity;
import edu.usu.sdl.openstorefront.storage.model.Component;
import edu.usu.sdl.openstorefront.storage.model.ComponentAttribute;
import edu.usu.sdl.openstorefront.storage.model.ComponentAttributePk;
import edu.usu.sdl.openstorefront.storage.model.ComponentContact;
import edu.usu.sdl.openstorefront.storage.model.ComponentEvaluationSection;
import edu.usu.sdl.openstorefront.storage.model.ComponentEvaluationSectionPk;
import edu.usu.sdl.openstorefront.storage.model.ComponentExternalDependency;
import edu.usu.sdl.openstorefront.storage.model.ComponentIntegration;
import edu.usu.sdl.openstorefront.storage.model.ComponentIntegrationConfig;
import edu.usu.sdl.openstorefront.storage.model.ComponentMedia;
import edu.usu.sdl.openstorefront.storage.model.ComponentMetadata;
import edu.usu.sdl.openstorefront.storage.model.ComponentQuestion;
import edu.usu.sdl.openstorefront.storage.model.ComponentQuestionResponse;
import edu.usu.sdl.openstorefront.storage.model.ComponentResource;
import edu.usu.sdl.openstorefront.storage.model.ComponentReview;
import edu.usu.sdl.openstorefront.storage.model.ComponentReviewCon;
import edu.usu.sdl.openstorefront.storage.model.ComponentReviewConPk;
import edu.usu.sdl.openstorefront.storage.model.ComponentReviewPro;
import edu.usu.sdl.openstorefront.storage.model.ComponentReviewProPk;
import edu.usu.sdl.openstorefront.storage.model.ComponentTag;
import edu.usu.sdl.openstorefront.storage.model.ComponentTracking;
import edu.usu.sdl.openstorefront.storage.model.ReviewCon;
import edu.usu.sdl.openstorefront.storage.model.ReviewPro;
import edu.usu.sdl.openstorefront.storage.model.TrackEventCode;
import edu.usu.sdl.openstorefront.util.OpenStorefrontConstant;
import edu.usu.sdl.openstorefront.util.SecurityUtil;
import edu.usu.sdl.openstorefront.util.StringProcessor;
import edu.usu.sdl.openstorefront.util.TimeUtil;
import edu.usu.sdl.openstorefront.validation.ValidationModel;
import edu.usu.sdl.openstorefront.validation.ValidationResult;
import edu.usu.sdl.openstorefront.validation.ValidationUtil;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentAdminView;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentAdminWrapper;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentAttributeView;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentContactView;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentDetailView;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentEvaluationSectionView;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentExternalDependencyView;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentIntegrationView;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentMediaView;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentMetadataView;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentPrintView;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentQuestionResponseView;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentQuestionView;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentResourceView;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentReviewProCon;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentReviewView;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentSearchView;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentTrackingWrapper;
import edu.usu.sdl.openstorefront.web.rest.model.FilterQueryParams;
import edu.usu.sdl.openstorefront.web.rest.model.RequiredForComponent;
import edu.usu.sdl.openstorefront.web.rest.model.TagView;
import edu.usu.sdl.openstorefront.web.viewmodel.LookupModel;
import edu.usu.sdl.openstorefront.web.viewmodel.RestErrorModel;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import jersey.repackaged.com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

/**
 * ComponentRESTResource Resource
 *
 * @author dshurtleff
 * @author jlaw
 */
@Path("v1/resource/components")
@APIDescription("Components are the central resource of the system.  The majority of the listing are components.")
public class ComponentRESTResource
		extends BaseResource
{

	@Context
	HttpServletRequest request;

	// <editor-fold defaultstate="collapsed"  desc="COMPONENT GENERAL FUNCTIONS">
	@GET
	@APIDescription("Get a list of components <br>(Note: this only the top level component object, See Component Detail for composite resource.)")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentSearchView.class)
	public List<ComponentSearchView> getComponents()
	{
		return service.getComponentService().getComponents();
	}

	@GET
	@APIDescription("Get a list of active and approved components for selection list.")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(LookupModel.class)
	@Path("/lookup")
	public Response getComponentLookupList()
	{
		List<LookupModel> lookupModels = new ArrayList<>();

		Component componentExample = new Component();
		componentExample.setActiveStatus(Component.ACTIVE_STATUS);
		componentExample.setApprovalState(OpenStorefrontConstant.ComponentApprovalStatus.APPROVED);
		List<Component> components = service.getPersistenceService().queryByExample(Component.class, componentExample);
		components.forEach(component -> {
			LookupModel lookupModel = new LookupModel();
			lookupModel.setCode(component.getComponentId());
			lookupModel.setDescription(component.getName());
			lookupModels.add(lookupModel);
		});

		GenericEntity<List<LookupModel>> entity = new GenericEntity<List<LookupModel>>(lookupModels)
		{
		};
		return sendSingleEntityResponse(entity);
	}

	@GET
	@APIDescription("Get valid component approval statuses")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(LookupModel.class)
	@Path("/approvalStatus")
	public List<LookupModel> getComponentApprovalStatus()
	{
		List<LookupModel> lookupModels = new ArrayList<>();

		for (OpenStorefrontConstant.ComponentApprovalStatus approvalStatus : OpenStorefrontConstant.ComponentApprovalStatus.values()) {
			LookupModel lookupModel = new LookupModel();
			lookupModel.setCode(approvalStatus.name());
			lookupModel.setDescription(approvalStatus.getDescription());
			lookupModels.add(lookupModel);
		}

		return lookupModels;
	}

	@GET
	@RequireAdmin
	@APIDescription("Get a list of all components <br>(Note: this only the top level component object, See Component Detail for composite resource.)")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(Component.class)
	@Path("/filterable")
	public Response getComponentList(@BeanParam FilterQueryParams filterQueryParams)
	{
		ValidationResult validationResult = filterQueryParams.validate();
		if (!validationResult.valid()) {
			return sendSingleEntityResponse(validationResult.toRestError());
		}

		Component componentExample = new Component();
		componentExample.setActiveStatus(filterQueryParams.getStatus());
		List<Component> components = service.getPersistenceService().queryByExample(Component.class, componentExample);
		long total = components.size();
		components = filterQueryParams.filter(components);

		ComponentIntegrationConfig integrationConfigExample = new ComponentIntegrationConfig();
		integrationConfigExample.setActiveStatus(ComponentIntegrationConfig.ACTIVE_STATUS);

		List<ComponentIntegrationConfig> componentIntegrationConfigs = service.getPersistenceService().queryByExample(ComponentIntegrationConfig.class, integrationConfigExample);
		Map<String, List<ComponentIntegrationConfig>> configMap = new HashMap<>();
		componentIntegrationConfigs.forEach(config -> {
			if (configMap.containsKey(config.getComponentId())) {
				configMap.get(config.getComponentId()).add(config);
			} else {
				List<ComponentIntegrationConfig> configList = new ArrayList<>();
				configList.add(config);
				configMap.put(config.getComponentId(), configList);
			}
		});

		List<ComponentAdminView> componentAdminViews = new ArrayList<>();
		for (Component component : components) {
			ComponentAdminView componentAdminView = new ComponentAdminView();
			componentAdminView.setComponent(component);
			StringBuilder configs = new StringBuilder();
			List<ComponentIntegrationConfig> configList = configMap.get(component.getComponentId());
			if (configList != null) {
				configList.forEach(config -> {
					if (StringUtils.isNotBlank(config.getIssueNumber())) {
						configs.append("(").append(config.getIntegrationType()).append(" - ").append(config.getIssueNumber()).append(") ");
					} else {
						configs.append("(").append(config.getIntegrationType()).append(") ");
					}
				});
			}
			componentAdminView.setIntegrationManagement(configs.toString());
			componentAdminView.setComponent(component);
			componentAdminViews.add(componentAdminView);
		}
		ComponentAdminWrapper componentAdminWrapper = new ComponentAdminWrapper(componentAdminViews, total);
		return sendSingleEntityResponse(componentAdminWrapper);
	}

	@GET
	@APIDescription("Get a list of components by an id set.  If it can't find a component for a griven id it's not returned.")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(Component.class)
	@Path("/list")
	public List<Component> batchGetComponents(
			@QueryParam("idList")
			@RequiredParam List<String> idList
	)
	{
		List<Component> componentViews = new ArrayList<>();
		idList.forEach(componentId -> {
			Component view = service.getPersistenceService().findById(Component.class, componentId);
			if (view != null) {
				componentViews.add(view);
			}
		});

		return componentViews;
	}

	@GET
	@APIDescription("Gets a component <br>(Note: this only the top level component object only)")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(Component.class)
	@Path("/{id}")
	public Response getComponentSingle(
			@PathParam("id")
			@RequiredParam String componentId)
	{
		Component view = service.getPersistenceService().findById(Component.class, componentId);
		return sendSingleEntityResponse(view);
	}

	@GET
	@APIDescription("Gets a component <br>(Note: this only the top level component object only)")
	@RequireAdmin
	@Produces({MediaType.WILDCARD})
	@DataType(ComponentAll.class)
	@Path("/{id}/export")
	public Response getComponentExport(
			@PathParam("id")
			@RequiredParam String componentId)
	{
		ComponentAll componentAll = service.getComponentService().getFullComponent(componentId);
		if (componentAll != null) {
			List<ComponentAll> fullComponents = new ArrayList<>();
			fullComponents.add(componentAll);

			String componentJson;
			try {
				componentJson = StringProcessor.defaultObjectMapper().writeValueAsString(fullComponents);
			} catch (JsonProcessingException ex) {
				throw new OpenStorefrontRuntimeException("Unable to export component.", ex);
			}
			Response.ResponseBuilder response = Response.ok(componentJson);
			response.header("Content-Disposition", "attachment; filename=\"" + componentAll.getComponent().getName() + ".json\"");
			return response.build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@POST
	@APIDescription("Gets a component (Full)")
	@RequireAdmin
	@Produces({MediaType.WILDCARD})
	@DataType(ComponentAll.class)
	@Path("/export")
	public Response getComponentExport(
			@FormParam("id")
			@RequiredParam List<String> ids)
	{
		if (ids.isEmpty() == false) {
			List<ComponentAll> fullComponents = new ArrayList<>();
			for (String componentId : ids) {
				ComponentAll componentAll = service.getComponentService().getFullComponent(componentId);
				fullComponents.add(componentAll);
			}

			String componentJson;
			try {
				componentJson = StringProcessor.defaultObjectMapper().writeValueAsString(fullComponents);
				Response.ResponseBuilder response = Response.ok(componentJson);
				response.header("Content-Disposition", "attachment; filename=\"ExportedComponents.json\"");
				return response.build();
			} catch (JsonProcessingException ex) {
				throw new OpenStorefrontRuntimeException("Unable to export components.", ex);
			}
		} else {
			Response.ResponseBuilder response = Response.ok("[]");
			response.header("Content-Disposition", "attachment; filename=\"ExportedComponents.json\"");
			return response.build();
		}
	}

	@GET
	@APIDescription("Get a list of components tags")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(TagView.class)
	@Path("/tagviews")
	public Response getAllComponentTags()
	{
		List<TagView> views = new ArrayList<>();

		ComponentTag componentTagExample = new ComponentTag();
		componentTagExample.setActiveStatus(Component.ACTIVE_STATUS);

		List<ComponentTag> tags = service.getPersistenceService().queryByExample(ComponentTag.class, componentTagExample);
		if (!tags.isEmpty()) {

			tags.forEach(tag -> {
				TagView tagView = new TagView();
				tagView.setTagId(tag.getTagId());
				tagView.setText(tag.getText());
				tagView.setCreateDts(tag.getCreateDts());
				tagView.setCreateUser(tag.getCreateUser());
				tagView.setComponentId(tag.getComponentId());
				String componentName = service.getComponentService().getComponentName(tag.getComponentId());
				if (componentName != null) {
					tagView.setComponentName(componentName);
				} else {
					tagView.setComponentName("Missing Component (Orphaned Tag)");
				}
				views.add(tagView);
			});
		}

		GenericEntity<List<TagView>> entity = new GenericEntity<List<TagView>>(views)
		{
		};
		return sendSingleEntityResponse(entity);
	}

	@GET
	@APIDescription("Get a list of components reviews")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentReviewView.class)
	@Path("/reviewviews")
	public Response getAllComponentReviews(@BeanParam FilterQueryParams filterQueryParams)
	{
		ValidationResult validationResult = filterQueryParams.validate();
		if (!validationResult.valid()) {
			return sendSingleEntityResponse(validationResult.toRestError());
		}

		ComponentReview reviewExample = new ComponentReview();
		reviewExample.setActiveStatus(filterQueryParams.getStatus());

		List<ComponentReview> componentReviews = service.getPersistenceService().queryByExample(ComponentReview.class, reviewExample);
		componentReviews = filterQueryParams.filter(componentReviews);
		List<ComponentReviewView> views = ComponentReviewView.toViewList(componentReviews);

		GenericEntity<List<ComponentReviewView>> entity = new GenericEntity<List<ComponentReviewView>>(views)
		{
		};
		return sendSingleEntityResponse(entity);
	}

	@GET
	@APIDescription("Get a list of components questions")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentQuestionView.class)
	@Path("/questionviews")
	public Response getAllComponentQuestions(@BeanParam FilterQueryParams filterQueryParams)
	{
		ValidationResult validationResult = filterQueryParams.validate();
		if (!validationResult.valid()) {
			return sendSingleEntityResponse(validationResult.toRestError());
		}

		ComponentQuestion questionExample = new ComponentQuestion();
		questionExample.setActiveStatus(filterQueryParams.getStatus());

		List<ComponentQuestion> componentQuestions = service.getPersistenceService().queryByExample(ComponentQuestion.class, questionExample);
		componentQuestions = filterQueryParams.filter(componentQuestions);

		ComponentQuestionResponse responseExample = new ComponentQuestionResponse();
		List<ComponentQuestionResponse> componentQuestionResponses = service.getPersistenceService().queryByExample(ComponentQuestionResponse.class, responseExample);
		Map<String, List<ComponentQuestionResponseView>> responseMap = new HashMap<>();
		for (ComponentQuestionResponse componentQuestionResponse : componentQuestionResponses) {
			if (responseMap.containsKey(componentQuestionResponse.getQuestionId())) {
				responseMap.get(componentQuestionResponse.getQuestionId()).add(ComponentQuestionResponseView.toView(componentQuestionResponse));
			} else {
				List<ComponentQuestionResponseView> responseViews = new ArrayList<>();
				responseViews.add(ComponentQuestionResponseView.toView(componentQuestionResponse));
				responseMap.put(componentQuestionResponse.getQuestionId(), responseViews);
			}
		}
		List<ComponentQuestionView> views = ComponentQuestionView.toViewList(componentQuestions, responseMap);

		GenericEntity<List<ComponentQuestionView>> entity = new GenericEntity<List<ComponentQuestionView>>(views)
		{
		};
		return sendSingleEntityResponse(entity);
	}

	@POST
	@RequireAdmin
	@APIDescription("Creates a component")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createComponent(
			@RequiredParam RequiredForComponent component)
	{
		ValidationModel validationModel = new ValidationModel(component);
		validationModel.setConsumeFieldsOnly(true);
		ValidationResult validationResult = ValidationUtil.validate(validationModel);
		if (validationResult.valid()) {
			component.getComponent().setActiveStatus(Component.ACTIVE_STATUS);
			component.getComponent().setCreateUser(SecurityUtil.getCurrentUserName());
			component.getComponent().setUpdateUser(SecurityUtil.getCurrentUserName());
			RequiredForComponent savedComponent = service.getComponentService().saveComponent(component);
			return Response.created(URI.create("v1/resource/components/" + savedComponent.getComponent().getComponentId())).entity(savedComponent).build();
		} else {
			return Response.ok(validationResult.toRestError()).build();
		}
	}

	@PUT
	@RequireAdmin
	@APIDescription("Updates a component")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public Response updateComponent(
			@PathParam("id")
			@RequiredParam String componentId,
			@RequiredParam RequiredForComponent component)
	{
		component.getComponent().setComponentId(componentId);
		component.getAttributes().forEach(attribute -> {
			attribute.getComponentAttributePk().setComponentId(componentId);
			attribute.setComponentId(componentId);
		});
		ValidationModel validationModel = new ValidationModel(component);
		validationModel.setConsumeFieldsOnly(true);
		ValidationResult validationResult = ValidationUtil.validate(validationModel);
		if (validationResult.valid()) {
			component.getComponent().setActiveStatus(Component.ACTIVE_STATUS);
			component.getComponent().setCreateUser(SecurityUtil.getCurrentUserName());
			component.getComponent().setUpdateUser(SecurityUtil.getCurrentUserName());
			return Response.ok(service.getComponentService().saveComponent(component)).build();
		} else {
			return Response.ok(validationResult.toRestError()).build();
		}
	}

	@PUT
	@RequireAdmin
	@APIDescription("Activates a component")
	@Path("/{id}/activate")
	public Response activateComponent(
			@PathParam("id")
			@RequiredParam String componentId)
	{
		Component view = service.getPersistenceService().findById(Component.class, componentId);
		if (view != null) {
			view = service.getComponentService().activateComponent(componentId);
		}
		return sendSingleEntityResponse(view);
	}

	@DELETE
	@RequireAdmin
	@APIDescription("Inactivates Component and removes any assoicated user watches.")
	@Path("/{id}")
	public void deleteComponentSingle(
			@PathParam("id")
			@RequiredParam String componentId)
	{
		service.getComponentService().deactivateComponent(componentId);
	}

	@GET
	@APIDescription("Gets full component details (This the packed view for displaying)")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentDetailView.class)
	@Path("/{id}/detail")
	public Response getComponentDetails(
			@PathParam("id")
			@RequiredParam String componentId,
			@QueryParam("type")
			@DefaultValue("default")
			@APIDescription("Pass 'Print' to retrieve special print view") String type)
	{
		ComponentPrintView componentPrint = null;
		ComponentDetailView componentDetail = null;
		if (type.equals("print")) {
			ComponentDetailView temp = service.getComponentService().getComponentDetails(componentId);
			componentPrint = ComponentPrintView.toView(temp);
		} else {
			componentDetail = service.getComponentService().getComponentDetails(componentId);
		}
		//Track Views
		if (componentDetail != null || componentPrint != null) {
			ComponentTracking componentTracking = new ComponentTracking();
			componentTracking.setClientIp(SecurityUtil.getClientIp(request));
			componentTracking.setComponentId(componentId);
			componentTracking.setEventDts(TimeUtil.currentDate());
			componentTracking.setTrackEventTypeCode(TrackEventCode.VIEW);
			componentTracking.setActiveStatus(ComponentTracking.ACTIVE_STATUS);
			componentTracking.setCreateUser(SecurityUtil.getCurrentUserName());
			componentTracking.setUpdateUser(SecurityUtil.getCurrentUserName());
			service.getComponentService().saveComponentTracking(componentTracking);
		}
		service.getComponentService().setLastViewDts(componentId, SecurityUtil.getCurrentUserName());
		if (componentDetail != null) {
			return sendSingleEntityResponse(componentDetail);
		} else if (componentPrint != null) {
			return sendSingleEntityResponse(componentPrint);
		} else {
			return Response.ok().build();
		}
	}

	@DELETE
	@RequireAdmin
	@APIDescription("Delete component and all related entities")
	@Path("/{id}/cascade")
	public void deleteComponentTag(
			@PathParam("id")
			@RequiredParam String componentId)
	{
		service.getComponentService().cascadeDeleteOfComponent(componentId);
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed"  desc="ComponentRESTResource ATTRIBUTE Section">
	@GET
	@APIDescription("Gets attributes for a component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentAttribute.class)
	@Path("/{id}/attributes")
	public Response getComponentAttribute(
			@PathParam("id")
			@RequiredParam String componentId,
			@BeanParam FilterQueryParams filterQueryParams)
	{
		ValidationResult validationResult = filterQueryParams.validate();
		if (!validationResult.valid()) {
			return sendSingleEntityResponse(validationResult.toRestError());
		}

		List<ComponentAttribute> componentAttributes = new ArrayList<>();

		ComponentAttribute componentAttributeExample = new ComponentAttribute();
		componentAttributeExample.setActiveStatus(filterQueryParams.getStatus());
		componentAttributeExample.setComponentId(componentId);
		componentAttributes = service.getPersistenceService().queryByExample(ComponentAttribute.class, componentAttributeExample);
		componentAttributes = filterQueryParams.filter(componentAttributes);

		GenericEntity<List<ComponentAttribute>> entity = new GenericEntity<List<ComponentAttribute>>(componentAttributes)
		{
		};
		return sendSingleEntityResponse(entity);
	}

	@GET
	@APIDescription("Gets attributes for a component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentAttributeView.class)
	@Path("/{id}/attributes/view")
	public Response getComponentAttributeView(
			@PathParam("id")
			@RequiredParam String componentId,
			@BeanParam FilterQueryParams filterQueryParams)
	{
		ValidationResult validationResult = filterQueryParams.validate();
		if (!validationResult.valid()) {
			return sendSingleEntityResponse(validationResult.toRestError());
		}

		ComponentAttribute componentAttributeExample = new ComponentAttribute();
		componentAttributeExample.setActiveStatus(filterQueryParams.getStatus());
		componentAttributeExample.setComponentId(componentId);
		List<ComponentAttribute> componentAttributes = service.getPersistenceService().queryByExample(ComponentAttribute.class, componentAttributeExample);
		componentAttributes = filterQueryParams.filter(componentAttributes);
		List<ComponentAttributeView> componentAttributeViews = ComponentAttributeView.toViewList(componentAttributes);

		GenericEntity<List<ComponentAttributeView>> entity = new GenericEntity<List<ComponentAttributeView>>(componentAttributeViews)
		{
		};
		return sendSingleEntityResponse(entity);
	}

	@GET
	@APIDescription("Gets attributes for component and a given type")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentAttribute.class)
	@Path("/{id}/attributes/{attributeType}")
	public List<ComponentAttribute> getComponentAttribute(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("attributeType")
			@RequiredParam String attributeType)
	{
		ComponentAttribute componentAttributeExample = new ComponentAttribute();
		componentAttributeExample.setComponentId(componentId);
		ComponentAttributePk componentAttributePk = new ComponentAttributePk();
		componentAttributePk.setAttributeType(attributeType);
		componentAttributeExample.setComponentAttributePk(componentAttributePk);
		List<ComponentAttribute> componentAttributes = service.getPersistenceService().queryByExample(ComponentAttribute.class, new QueryByExample(componentAttributeExample));
		return componentAttributes;
	}

	@GET
	@APIDescription("Get the codes for a specified attribute type of an entity")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(AttributeCode.class)
	@Path("/{id}/attributes/{attributeType}/codes")
	public List<AttributeCode> getComponentAttributeCodes(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("attributeType")
			@RequiredParam String attributeType)
	{
		List<AttributeCode> attributeCodes = new ArrayList<>();

		ComponentAttribute componentAttributeExample = new ComponentAttribute();
		componentAttributeExample.setComponentId(componentId);
		ComponentAttributePk componentAttributePk = new ComponentAttributePk();
		componentAttributePk.setAttributeType(attributeType);
		componentAttributeExample.setComponentAttributePk(componentAttributePk);
		List<ComponentAttribute> componentAttributes = service.getPersistenceService().queryByExample(ComponentAttribute.class, new QueryByExample(componentAttributeExample));
		for (ComponentAttribute attribute : componentAttributes) {
			AttributeCodePk attributeCodePk = new AttributeCodePk();
			attributeCodePk.setAttributeCode(attribute.getComponentAttributePk().getAttributeCode());
			attributeCodePk.setAttributeType(attribute.getComponentAttributePk().getAttributeType());
			AttributeCode attributCode = service.getAttributeService().findCodeForType(attributeCodePk);
			if (attributCode != null) {
				attributeCodes.add(attributCode);
			}
		}
		return attributeCodes;
	}

	@DELETE
	@RequireAdmin
	@APIDescription("Remove all attributes from the entity")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}/attributes")
	public void deleteComponentAttributes(
			@PathParam("id")
			@RequiredParam String componentId)
	{
		ComponentAttribute attribute = new ComponentAttribute();
		attribute.setComponentId(componentId);
		service.getPersistenceService().deleteByExample(attribute);
	}

	@DELETE
	@RequireAdmin
	@APIDescription("Remove an attribute from the entity")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}/attributes/{attributeType}/{attributeCode}")
	public void deleteComponentAttribute(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("attributeType")
			@RequiredParam String attributeType,
			@PathParam("attributeCode")
			@RequiredParam String attributeCode)
	{
		ComponentAttributePk pk = new ComponentAttributePk();
		pk.setAttributeCode(attributeCode);
		pk.setAttributeType(attributeType);
		pk.setComponentId(componentId);
		service.getComponentService().deactivateBaseComponent(ComponentAttribute.class, pk);
	}

	@PUT
	@RequireAdmin
	@APIDescription("Activates an attribute on the component")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}/attributes/{attributeType}/{attributeCode}/activate")
	public Response activateComponentAttribute(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("attributeType")
			@RequiredParam String attributeType,
			@PathParam("attributeCode")
			@RequiredParam String attributeCode)
	{
		ComponentAttributePk pk = new ComponentAttributePk();
		pk.setAttributeCode(attributeCode);
		pk.setAttributeType(attributeType);
		pk.setComponentId(componentId);
		service.getComponentService().activateBaseComponent(ComponentAttribute.class, pk);
		ComponentAttribute componentAttribute = service.getPersistenceService().findById(ComponentAttribute.class, pk);
		return sendSingleEntityResponse(componentAttribute);
	}

	@POST
	@RequireAdmin
	@APIDescription("Add an attribute to the entity")
	@Consumes(MediaType.APPLICATION_JSON)
	@DataType(ComponentContact.class)
	@Path("/{id}/attributes")
	public Response addComponentAttribute(
			@PathParam("id")
			@RequiredParam String componentId,
			@RequiredParam ComponentAttribute attribute)
	{
		attribute.setActiveStatus(ComponentAttribute.ACTIVE_STATUS);
		attribute.setComponentId(componentId);
		attribute.getComponentAttributePk().setComponentId(componentId);

		ValidationModel validationModel = new ValidationModel(attribute);
		validationModel.setConsumeFieldsOnly(true);
		ValidationResult validationResult = ValidationUtil.validate(validationModel);
		if (validationResult.valid() && service.getComponentService().checkComponentAttribute(attribute)) {
			attribute.setCreateUser(SecurityUtil.getCurrentUserName());
			attribute.setUpdateUser(SecurityUtil.getCurrentUserName());
			service.getComponentService().saveComponentAttribute(attribute);
		} else {
			return Response.ok(validationResult.toRestError()).build();
		}
		return Response.created(URI.create("v1/resource/components/" + attribute.getComponentAttributePk().getComponentId() + "/attributes/" + attribute.getComponentAttributePk().getAttributeType() + "/" + attribute.getComponentAttributePk().getAttributeCode())).entity(attribute).build();
	}
	// </editor-fold>

	//<editor-fold defaultstate="collapsed"  desc="ComponentRESTResource DEPENDENCY section">
	@GET
	@APIDescription("Get the dependencies for the component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentExternalDependency.class)
	@Path("/{id}/dependencies")
	public List<ComponentExternalDependency> getComponentDependencies(
			@PathParam("id")
			@RequiredParam String componentId)
	{
		return service.getComponentService().getBaseComponent(ComponentExternalDependency.class, componentId);
	}

	@GET
	@APIDescription("Gets a dependency for a component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentExternalDependency.class)
	@Path("/{id}/dependencies/{dependencyId}")
	public Response getComponentDependency(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("dependencyId")
			@RequiredParam String dependencyId)
	{
		ComponentExternalDependency dependencyExample = new ComponentExternalDependency();
		dependencyExample.setDependencyId(dependencyId);
		dependencyExample.setComponentId(componentId);
		ComponentExternalDependency componentExternalDependency = service.getPersistenceService().queryOneByExample(ComponentExternalDependency.class, dependencyExample);
		return sendSingleEntityResponse(componentExternalDependency);
	}

	@GET
	@APIDescription("Get the dependencies from the entity")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentExternalDependencyView.class)
	@Path("/{id}/dependencies/view")
	public Response getComponentDependencies(
			@PathParam("id")
			@RequiredParam String componentId,
			@BeanParam FilterQueryParams filterQueryParams)
	{
		ValidationResult validationResult = filterQueryParams.validate();
		if (!validationResult.valid()) {
			return sendSingleEntityResponse(validationResult.toRestError());
		}

		ComponentExternalDependency dependencyExample = new ComponentExternalDependency();
		dependencyExample.setActiveStatus(filterQueryParams.getStatus());
		dependencyExample.setComponentId(componentId);

		List<ComponentExternalDependency> componentExternalDependencies = service.getPersistenceService().queryByExample(ComponentExternalDependency.class, dependencyExample);
		componentExternalDependencies = filterQueryParams.filter(componentExternalDependencies);
		List<ComponentExternalDependencyView> views = ComponentExternalDependencyView.toViewList(componentExternalDependencies);

		GenericEntity<List<ComponentExternalDependencyView>> entity = new GenericEntity<List<ComponentExternalDependencyView>>(views)
		{
		};
		return sendSingleEntityResponse(entity);
	}

	@DELETE
	@RequireAdmin
	@APIDescription("Inactivates a dependency from the component")
	@Path("/{id}/dependencies/{dependencyId}")
	public void deleteComponentDependency(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("dependencyId")
			@RequiredParam String dependencyId)
	{
		ComponentExternalDependency componentExternalDependency = service.getPersistenceService().findById(ComponentExternalDependency.class, dependencyId);
		if (componentExternalDependency != null) {
			checkBaseComponentBelongsToComponent(componentExternalDependency, componentId);
			service.getComponentService().deactivateBaseComponent(ComponentExternalDependency.class, dependencyId);
		}
	}

	@PUT
	@RequireAdmin
	@APIDescription("Activates a dependency from the component")
	@Path("/{id}/dependencies/{dependencyId}/activate")
	public Response activatieComponentDependency(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("dependencyId")
			@RequiredParam String dependencyId)
	{
		ComponentExternalDependency dependencyExample = new ComponentExternalDependency();
		dependencyExample.setDependencyId(dependencyId);
		dependencyExample.setComponentId(componentId);
		ComponentExternalDependency componentExternalDependency = service.getPersistenceService().queryOneByExample(ComponentExternalDependency.class, dependencyExample);
		if (componentExternalDependency != null) {
			service.getComponentService().activateBaseComponent(ComponentExternalDependency.class, dependencyId);
		}
		return sendSingleEntityResponse(componentExternalDependency);
	}

	@POST
	@RequireAdmin
	@APIDescription("Add a dependency to the entity")
	@Consumes(MediaType.APPLICATION_JSON)
	@DataType(ComponentExternalDependency.class)
	@Path("/{id}/dependencies")
	public Response addComponentDependency(
			@PathParam("id")
			@RequiredParam String componentId,
			@RequiredParam ComponentExternalDependency dependency)
	{
		dependency.setComponentId(componentId);
		return saveDependency(dependency, true);
	}

	@PUT
	@RequireAdmin
	@APIDescription("Update a contact associated to the entity")
	@Consumes(MediaType.APPLICATION_JSON)
	@DataType(ComponentExternalDependency.class)
	@Path("/{id}/dependencies/{dependencyId}")
	public Response updateComponentDependency(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("dependencyId")
			@RequiredParam String dependencyId,
			ComponentExternalDependency dependency)
	{
		Response response = Response.status(Response.Status.NOT_FOUND).build();
		ComponentExternalDependency componentExternalDependency = service.getPersistenceService().findById(ComponentExternalDependency.class, dependencyId);
		if (componentExternalDependency != null) {
			checkBaseComponentBelongsToComponent(componentExternalDependency, componentId);
			dependency.setComponentId(componentId);
			dependency.setDependencyId(dependencyId);
			response = saveDependency(dependency, false);
		}
		return response;
	}

	private Response saveDependency(ComponentExternalDependency dependency, Boolean post)
	{
		ValidationModel validationModel = new ValidationModel(dependency);
		validationModel.setConsumeFieldsOnly(true);
		ValidationResult validationResult = ValidationUtil.validate(validationModel);
		if (validationResult.valid()) {
			dependency.setActiveStatus(ComponentExternalDependency.ACTIVE_STATUS);
			dependency.setCreateUser(SecurityUtil.getCurrentUserName());
			dependency.setUpdateUser(SecurityUtil.getCurrentUserName());
			service.getComponentService().saveComponentDependency(dependency);
		} else {
			return Response.ok(validationResult.toRestError()).build();
		}
		if (post) {
			return Response.created(URI.create("v1/resource/components/" + dependency.getComponentId() + "/dependency/" + dependency.getDependencyId())).entity(dependency).build();
		} else {
			return Response.ok(dependency).build();
		}
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed"  desc="ComponentRESTResource CONTACT section">
	@GET
	@APIDescription("Gets all contact for a component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentContact.class)
	@Path("/{id}/contacts")
	public List<ComponentContact> getComponentContact(
			@PathParam("id")
			@RequiredParam String componentId)
	{
		return service.getComponentService().getBaseComponent(ComponentContact.class, componentId);
	}

	@GET
	@APIDescription("Gets all contact for a component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentContactView.class)
	@Path("/{id}/contacts/view")
	public Response getComponentContact(
			@PathParam("id")
			@RequiredParam String componentId,
			@BeanParam FilterQueryParams filterQueryParams)
	{
		ValidationResult validationResult = filterQueryParams.validate();
		if (!validationResult.valid()) {
			return sendSingleEntityResponse(validationResult.toRestError());
		}

		ComponentContact componentContactExample = new ComponentContact();
		componentContactExample.setActiveStatus(filterQueryParams.getStatus());
		componentContactExample.setComponentId(componentId);

		List<ComponentContact> contacts = service.getPersistenceService().queryByExample(ComponentContact.class, componentContactExample);
		List<ComponentContactView> contactViews = new ArrayList<>();
		contacts.forEach(contact -> {
			contactViews.add(ComponentContactView.toView(contact));
		});

		GenericEntity<List<ComponentContactView>> entity = new GenericEntity<List<ComponentContactView>>(contactViews)
		{
		};
		return sendSingleEntityResponse(entity);
	}

	@DELETE
	@RequireAdmin
	@APIDescription("Remove a contact from the component")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}/contacts/{contactId}")
	public void deleteComponentContact(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("contactId")
			@RequiredParam String contactId)
	{

		ComponentContact componentContact = service.getPersistenceService().findById(ComponentContact.class, contactId);
		if (componentContact != null) {
			checkBaseComponentBelongsToComponent(componentContact, componentId);
			service.getComponentService().deactivateBaseComponent(ComponentContact.class, contactId);
		}
	}

	@PUT
	@RequireAdmin
	@APIDescription("Activate a contact on the component")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}/contacts/{contactId}/activate")
	public Response activateComponentContact(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("contactId")
			@RequiredParam String contactId)
	{
		ComponentContact componentContact = service.getPersistenceService().findById(ComponentContact.class, contactId);
		if (componentContact != null) {
			checkBaseComponentBelongsToComponent(componentContact, componentId);
			service.getComponentService().activateBaseComponent(ComponentContact.class, contactId);
			componentContact = service.getPersistenceService().findById(ComponentContact.class, contactId);
		}
		return sendSingleEntityResponse(componentContact);
	}

	@POST
	@RequireAdmin
	@APIDescription("Add a contact to the component")
	@Consumes(MediaType.APPLICATION_JSON)
	@DataType(ComponentContact.class)
	@Path("/{id}/contacts")
	public Response addComponentContact(
			@PathParam("id")
			@RequiredParam String componentId,
			@RequiredParam ComponentContact contact)
	{
		contact.setComponentId(componentId);
		return saveContact(contact, true);
	}

	@PUT
	@RequireAdmin
	@APIDescription("Update a contact associated to the component")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{id}/contacts/{contactId}")
	public Response updateComponentContact(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("contactId")
			@RequiredParam String contactId,
			ComponentContact contact)
	{
		Response response = Response.status(Response.Status.NOT_FOUND).build();
		ComponentContact componentContact = service.getPersistenceService().findById(ComponentContact.class, contactId);
		if (componentContact != null) {
			checkBaseComponentBelongsToComponent(componentContact, componentId);
			contact.setComponentId(componentId);
			contact.setContactId(contactId);
			response = saveContact(contact, false);
		}
		return response;
	}

	private Response saveContact(ComponentContact contact, Boolean post)
	{
		ValidationModel validationModel = new ValidationModel(contact);
		validationModel.setConsumeFieldsOnly(true);
		ValidationResult validationResult = ValidationUtil.validate(validationModel);
		if (validationResult.valid()) {
			contact.setActiveStatus(ComponentContact.ACTIVE_STATUS);
			contact.setCreateUser(SecurityUtil.getCurrentUserName());
			contact.setUpdateUser(SecurityUtil.getCurrentUserName());
			service.getComponentService().saveComponentContact(contact);
		} else {
			return Response.ok(validationResult.toRestError()).build();
		}
		if (post) {
			return Response.created(URI.create("v1/resource/components/" + contact.getComponentId() + "/contacts/" + contact.getContactId())).entity(contact).build();
		} else {
			return Response.ok(contact).build();
		}
	}
	//</editor-fold>

	// <editor-fold defaultstate="collapsed"  desc="ComponentRESTResource Evaluation Section section">
	@GET
	@APIDescription("Gets evaluation sections associated to the component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentEvaluationSection.class)
	@Path("/{id}/sections")
	public List<ComponentEvaluationSection> getComponentEvaluationSection(
			@PathParam("id")
			@RequiredParam String componentId)
	{
		return service.getComponentService().getBaseComponent(ComponentEvaluationSection.class, componentId);
	}

	@GET
	@APIDescription("Gets  evaluation sections associated to the component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentEvaluationSectionView.class)
	@Path("/{id}/sections/view")
	public List<ComponentEvaluationSectionView> getComponentEvaluationSectionView(
			@PathParam("id")
			@RequiredParam String componentId)
	{
		List<ComponentEvaluationSection> sections = service.getComponentService().getBaseComponent(ComponentEvaluationSection.class, componentId);
		List<ComponentEvaluationSectionView> views = ComponentEvaluationSectionView.toViewList(sections);
		views.sort(new BeanComparator<>(OpenStorefrontConstant.SORT_DESCENDING, ComponentEvaluationSectionView.NAME_FIELD));
		return views;
	}

	@DELETE
	@RequireAdmin
	@APIDescription("Removes an evaluation section from the component")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}/sections/{evalSection}")
	public void deleteComponentEvaluationSection(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("evalSection")
			@RequiredParam String evalSection)
	{
		ComponentEvaluationSectionPk pk = new ComponentEvaluationSectionPk();
		pk.setComponentId(componentId);
		pk.setEvaluationSection(evalSection);
		service.getComponentService().deleteBaseComponent(ComponentEvaluationSection.class, pk);
	}

	@DELETE
	@RequireAdmin
	@APIDescription("Removes all evaluation section from the component")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}/sections")
	public void deleteAllComponentEvaluationSections(
			@PathParam("id")
			@RequiredParam String componentId)
	{
		service.getComponentService().deleteAllBaseComponent(ComponentEvaluationSection.class, componentId);
	}

	@POST
	@RequireAdmin
	@APIDescription("Adds a group evaluation section to the component")
	@Consumes(MediaType.APPLICATION_JSON)
	@DataType(ComponentEvaluationSection.class)
	@Path("/{id}/sections/all")
	public Response saveComponentEvaluationSections(
			@PathParam("id")
			@RequiredParam String componentId,
			@RequiredParam List<ComponentEvaluationSection> sections)
	{
		ValidationResult allValidationResult = new ValidationResult();
		for (ComponentEvaluationSection section : sections) {
			section.setComponentId(componentId);
			section.getComponentEvaluationSectionPk().setComponentId(componentId);

			ValidationModel validationModel = new ValidationModel(section);
			validationModel.setConsumeFieldsOnly(true);
			ValidationResult validationResult = ValidationUtil.validate(validationModel);
			if (!validationResult.valid()) {
				allValidationResult.merge(validationResult);
			}
		}

		if (allValidationResult.valid()) {
			for (ComponentEvaluationSection section : sections) {
				section.setActiveStatus(ComponentEvaluationSection.ACTIVE_STATUS);
				section.setCreateUser(SecurityUtil.getCurrentUserName());
				section.setUpdateUser(SecurityUtil.getCurrentUserName());
			}
			service.getComponentService().saveComponentEvaluationSection(sections);
		} else {
			return Response.ok(allValidationResult.toRestError()).build();
		}
		return Response.ok().build();
	}

	@POST
	@RequireAdmin
	@APIDescription("Adds an evaluation section to the component")
	@Consumes(MediaType.APPLICATION_JSON)
	@DataType(ComponentEvaluationSection.class)
	@Path("/{id}/sections")
	public Response addComponentEvaluationSection(
			@PathParam("id")
			@RequiredParam String componentId,
			@RequiredParam ComponentEvaluationSection section)
	{
		section.setComponentId(componentId);
		section.getComponentEvaluationSectionPk().setComponentId(componentId);
		return saveSection(section, true);
	}

	@PUT
	@RequireAdmin
	@APIDescription("Updates an evaluation section for a component")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{id}/sections/{evalSectionId}")
	public Response updateComponentEvaluationSection(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("evalSectionId")
			@RequiredParam String evalSectionId,
			@RequiredParam ComponentEvaluationSection section)
	{
		Response response = Response.status(Response.Status.NOT_FOUND).build();
		ComponentEvaluationSectionPk pk = new ComponentEvaluationSectionPk();
		pk.setComponentId(componentId);
		pk.setEvaluationSection(evalSectionId);
		ComponentEvaluationSection componentEvaluationSection = service.getPersistenceService().findById(ComponentEvaluationSection.class, pk);
		if (componentEvaluationSection != null) {
			section.setComponentId(componentId);
			section.setComponentEvaluationSectionPk(pk);
			response = saveSection(section, false);
		}
		return response;
	}

	private Response saveSection(ComponentEvaluationSection section, Boolean post)
	{
		ValidationModel validationModel = new ValidationModel(section);
		validationModel.setConsumeFieldsOnly(true);
		ValidationResult validationResult = ValidationUtil.validate(validationModel);
		if (validationResult.valid()) {
			section.setActiveStatus(ComponentEvaluationSection.ACTIVE_STATUS);
			section.setCreateUser(SecurityUtil.getCurrentUserName());
			section.setUpdateUser(SecurityUtil.getCurrentUserName());
			service.getComponentService().saveComponentEvaluationSection(section);
		} else {
			return Response.ok(validationResult.toRestError()).build();
		}
		if (post) {
			return Response.created(URI.create("v1/resource/components/" + section.getComponentId() + "/sections/" + section.getComponentEvaluationSectionPk().getEvaluationSection())).entity(section).build();
		} else {
			return Response.ok(section).build();
		}
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed"  desc="ComponentRESTResource RESOURCE section">
	@GET
	@APIDescription("Get the resources associated to the given component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentResource.class)
	@Path("/{id}/resources")
	public List<ComponentResource> getComponentResource(
			@PathParam("id")
			@RequiredParam String componentId)
	{
		List<ComponentResource> componentResources = service.getComponentService().getBaseComponent(ComponentResource.class, componentId);
		componentResources = SortUtil.sortComponentResource(componentResources);
		return componentResources;
	}

	@GET
	@APIDescription("Get the resources associated to the given component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentResourceView.class)
	@Path("/{id}/resources/view")
	public Response getComponentResourceView(
			@PathParam("id")
			@RequiredParam String componentId,
			@BeanParam FilterQueryParams filterQueryParams)
	{
		ValidationResult validationResult = filterQueryParams.validate();
		if (!validationResult.valid()) {
			return sendSingleEntityResponse(validationResult.toRestError());
		}

		ComponentResource componentResourceExample = new ComponentResource();
		componentResourceExample.setActiveStatus(filterQueryParams.getStatus());
		componentResourceExample.setComponentId(componentId);
		List<ComponentResource> componentResources = service.getPersistenceService().queryByExample(ComponentResource.class, componentResourceExample);
		componentResources = filterQueryParams.filter(componentResources);
		List<ComponentResourceView> views = ComponentResourceView.toViewList(componentResources);

		GenericEntity<List<ComponentResourceView>> entity = new GenericEntity<List<ComponentResourceView>>(views)
		{
		};
		return sendSingleEntityResponse(entity);
	}

	@GET
	@APIDescription("Get a resource associated to the given component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentResource.class)
	@Path("/{id}/resources/{resourceId}")
	public Response getComponentResource(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("resourceId")
			@RequiredParam String resourceId)
	{
		ComponentResource componentResourceExample = new ComponentResource();
		componentResourceExample.setComponentId(componentId);
		componentResourceExample.setResourceId(resourceId);
		ComponentResource componentResource = service.getPersistenceService().queryOneByExample(ComponentResource.class, componentResourceExample);
		return sendSingleEntityResponse(componentResource);
	}

	@DELETE
	@RequireAdmin
	@APIDescription("Inactivates a given resource from the specified component")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}/resources/{resourceId}")
	public void inactivateComponentResource(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("resourceId")
			@RequiredParam String resourceId)
	{
		ComponentResource componentResourceExample = new ComponentResource();
		componentResourceExample.setComponentId(componentId);
		componentResourceExample.setResourceId(resourceId);
		ComponentResource componentResource = service.getPersistenceService().queryOneByExample(ComponentResource.class, componentResourceExample);
		if (componentResource != null) {
			service.getComponentService().deactivateBaseComponent(ComponentResource.class, resourceId);
		}
	}

	@DELETE
	@RequireAdmin
	@APIDescription("Remove a given resource from the specified component")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}/resources/{resourceId}/force")
	public void deleteComponentResource(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("resourceId")
			@RequiredParam String resourceId)
	{
		ComponentResource componentResourceExample = new ComponentResource();
		componentResourceExample.setComponentId(componentId);
		componentResourceExample.setResourceId(resourceId);
		ComponentResource componentResource = service.getPersistenceService().queryOneByExample(ComponentResource.class, componentResourceExample);
		if (componentResource != null) {
			service.getComponentService().deleteBaseComponent(ComponentResource.class, resourceId);
		}
	}

	@PUT
	@RequireAdmin
	@APIDescription("Activates a resource")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}/resources/{resourceId}/activate")
	public Response activateComponentResource(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("resourceId")
			@RequiredParam String resourceId)
	{
		ComponentResource componentResourceExample = new ComponentResource();
		componentResourceExample.setComponentId(componentId);
		componentResourceExample.setResourceId(resourceId);
		ComponentResource componentResource = service.getPersistenceService().queryOneByExample(ComponentResource.class, componentResourceExample);
		if (componentResource != null) {
			service.getComponentService().activateBaseComponent(ComponentResource.class, resourceId);
			componentResource.setActiveStatus(Component.ACTIVE_STATUS);
		}
		return sendSingleEntityResponse(componentResource);
	}

	@POST
	@RequireAdmin
	@APIDescription("Add a resource to the given entity.  Use a form to POST Resource.action?UploadResource to upload file.  "
			+ "To upload: pass the componentResource.resourceType...etc and 'file'.")
	@Consumes(MediaType.APPLICATION_JSON)
	@DataType(ComponentResource.class)
	@Path("/{id}/resources")
	public Response addComponentResource(
			@PathParam("id")
			@RequiredParam String componentId,
			@RequiredParam ComponentResource resource)
	{
		resource.setComponentId(componentId);
		return saveResource(resource, true);
	}

	@PUT
	@RequireAdmin
	@APIDescription("Update a resource associated with a given entity. Use a form to POST Resource.action?UploadResource to upload file. "
			+ " To upload: pass the componentResource.resourceType...etc and 'file'.")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{id}/resources/{resourceId}")
	public Response updateComponentResource(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("resourceId")
			@RequiredParam String resourceId,
			@RequiredParam ComponentResource resource)
	{
		Response response = Response.status(Response.Status.NOT_FOUND).build();
		ComponentResource componentResourceExample = new ComponentResource();
		componentResourceExample.setComponentId(componentId);
		componentResourceExample.setResourceId(resourceId);
		ComponentResource componentResource = service.getPersistenceService().queryOneByExample(ComponentResource.class, componentResourceExample);
		if (componentResource != null) {
			resource.setComponentId(componentId);
			resource.setResourceId(resourceId);
			response = saveResource(resource, false);
		}
		return response;
	}

	private Response saveResource(ComponentResource resource, Boolean post)
	{
		ValidationModel validationModel = new ValidationModel(resource);
		validationModel.setConsumeFieldsOnly(true);
		ValidationResult validationResult = ValidationUtil.validate(validationModel);
		if (validationResult.valid()) {
			resource.setActiveStatus(ComponentResource.ACTIVE_STATUS);
			resource.setCreateUser(SecurityUtil.getCurrentUserName());
			resource.setUpdateUser(SecurityUtil.getCurrentUserName());
			service.getComponentService().saveComponentResource(resource);
		} else {
			return Response.ok(validationResult.toRestError()).build();
		}
		if (post) {
			return Response.created(URI.create("v1/resource/components/" + resource.getComponentId() + "/resources/" + resource.getResourceId())).entity(resource).build();
		} else {
			return Response.ok(resource).build();
		}
	}
	// </editor-fold>

	// <editor-fold  defaultstate="collapsed"  desc="ComponentRESTResource MEDIA section">
	@GET
	@APIDescription("Gets the list of media associated to an entity")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentMedia.class)
	@Path("/{id}/media")
	public List<ComponentMedia> getComponentMedia(
			@PathParam("id")
			@RequiredParam String componentId)
	{
		return service.getComponentService().getBaseComponent(ComponentMedia.class, componentId);
	}

	@GET
	@APIDescription("Get the resources associated to the given component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentMediaView.class)
	@Path("/{id}/media/view")
	public Response getComponentMediaView(
			@PathParam("id")
			@RequiredParam String componentId,
			@BeanParam FilterQueryParams filterQueryParams)
	{
		ValidationResult validationResult = filterQueryParams.validate();
		if (!validationResult.valid()) {
			return sendSingleEntityResponse(validationResult.toRestError());
		}

		ComponentMedia componentMediaExample = new ComponentMedia();
		componentMediaExample.setActiveStatus(filterQueryParams.getStatus());
		componentMediaExample.setComponentId(componentId);
		List<ComponentMedia> componentMedia = service.getPersistenceService().queryByExample(ComponentMedia.class, componentMediaExample);
		componentMedia = filterQueryParams.filter(componentMedia);
		List<ComponentMediaView> views = ComponentMediaView.toViewList(componentMedia);

		GenericEntity<List<ComponentMediaView>> entity = new GenericEntity<List<ComponentMediaView>>(views)
		{
		};
		return sendSingleEntityResponse(entity);
	}

	@GET
	@APIDescription("Get a media item associated to the given component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentMedia.class)
	@Path("/{id}/media/{mediaId}")
	public Response getComponentMedia(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("mediaId")
			@RequiredParam String mediaId)
	{
		ComponentMedia componentMediaExample = new ComponentMedia();
		componentMediaExample.setComponentId(componentId);
		componentMediaExample.setComponentMediaId(mediaId);
		ComponentMedia componentMedia = service.getPersistenceService().queryOneByExample(ComponentMedia.class, componentMediaExample);
		return sendSingleEntityResponse(componentMedia);
	}

	@DELETE
	@RequireAdmin
	@APIDescription("Inactivates media from the specified component")
	@Path("/{id}/media/{mediaId}")
	public void inactivateComponentMedia(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("mediaId")
			@RequiredParam String mediaId)
	{
		ComponentMedia componentMedia = service.getPersistenceService().findById(ComponentMedia.class, mediaId);
		if (componentMedia != null) {
			checkBaseComponentBelongsToComponent(componentMedia, componentId);
			service.getComponentService().deactivateBaseComponent(ComponentMedia.class, mediaId);
		}
	}

	@DELETE
	@RequireAdmin
	@APIDescription("Removes media from the specified entity")
	@Path("/{id}/media/{mediaId}/force")
	public void deleteComponentMedia(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("mediaId")
			@RequiredParam String mediaId)
	{
		ComponentMedia componentMedia = service.getPersistenceService().findById(ComponentMedia.class, mediaId);
		if (componentMedia != null) {
			checkBaseComponentBelongsToComponent(componentMedia, componentId);
			service.getComponentService().deleteBaseComponent(ComponentMedia.class, mediaId);
		}
	}

	@PUT
	@RequireAdmin
	@APIDescription("Activates media from the specified component")
	@Consumes({MediaType.APPLICATION_JSON})
	@DataType(ComponentMedia.class)
	@Path("/{id}/media/{mediaId}/activate")
	public Response activateComponentMedia(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("mediaId")
			@RequiredParam String mediaId)
	{
		ComponentMedia componentMediaExample = new ComponentMedia();
		componentMediaExample.setComponentId(componentId);
		componentMediaExample.setComponentMediaId(mediaId);
		ComponentMedia componentMedia = service.getPersistenceService().queryOneByExample(ComponentMedia.class, componentMediaExample);
		if (componentMedia != null) {
			checkBaseComponentBelongsToComponent(componentMedia, componentId);
			service.getComponentService().activateBaseComponent(ComponentMedia.class, mediaId);
			componentMedia.setActiveStatus(ComponentMedia.ACTIVE_STATUS);
		}
		return sendSingleEntityResponse(componentMedia);
	}

	@POST
	@RequireAdmin
	@APIDescription("Add media to the specified entity (leave the fileName blank if you want your supplied link to be it's location) "
			+ " Use a form to POST Media.action?UploadMedia to upload file.  To upload: pass the componentMedia.mediaTypeCode...etc and 'file'.")
	@Consumes(MediaType.APPLICATION_JSON)
	@DataType(ComponentMedia.class)
	@Path("/{id}/media")
	public Response addComponentMedia(
			@PathParam("id")
			@RequiredParam String componentId,
			@RequiredParam ComponentMedia media)
	{
		media.setComponentId(componentId);
		return saveMedia(media, true);
	}

	@PUT
	@RequireAdmin
	@APIDescription("Update media associated to the specified entity (leave the fileName blank if you want your supplied link to be it's location) "
			+ " Use a form to POST Media.action?UploadMedia to upload file.  To upload: pass the componentMedia.mediaTypeCode...etc and 'file'.")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{id}/media/{mediaId}")
	public Response updateComponentMedia(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("mediaId")
			@RequiredParam String mediaId,
			@RequiredParam ComponentMedia media)
	{
		Response response = Response.status(Response.Status.NOT_FOUND).build();
		ComponentMedia componentMedia = service.getPersistenceService().findById(ComponentMedia.class, mediaId);
		if (componentMedia != null) {
			checkBaseComponentBelongsToComponent(componentMedia, componentId);
			media.setComponentId(componentId);
			media.setComponentMediaId(mediaId);
			response = saveMedia(media, false);
		}
		return response;
	}

	private Response saveMedia(ComponentMedia media, Boolean post)
	{
		ValidationModel validationModel = new ValidationModel(media);
		validationModel.setConsumeFieldsOnly(true);
		ValidationResult validationResult = ValidationUtil.validate(validationModel);
		if (validationResult.valid()) {
			media.setActiveStatus(ComponentMedia.ACTIVE_STATUS);
			media.setCreateUser(SecurityUtil.getCurrentUserName());
			media.setUpdateUser(SecurityUtil.getCurrentUserName());
			service.getComponentService().saveComponentMedia(media);
		} else {
			return Response.ok(validationResult.toRestError()).build();
		}
		if (post) {
			return Response.created(URI.create("v1/resource/components/" + media.getComponentId() + "/media/" + media.getComponentMediaId())).entity(media).build();
		} else {
			return Response.ok(media).build();
		}
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed"  desc="ComponentRESTResource METADATA section">
	@GET
	@APIDescription("Gets full component details (This the packed view for displaying)")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentMetadata.class)
	@Path("/{id}/metadata")
	public List<ComponentMetadata> getComponentMetadata(
			@PathParam("id")
			@RequiredParam String componentId)
	{
		return service.getComponentService().getBaseComponent(ComponentMetadata.class, componentId);
	}

	@GET
	@APIDescription("Gets a metadata entity for a component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentMetadata.class)
	@Path("/{id}/metadata/{metadataId}")
	public Response getComponentMetadataEntity(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("metadataId")
			@RequiredParam String metadataId)
	{
		ComponentMetadata metadataExample = new ComponentMetadata();
		metadataExample.setMetadataId(metadataId);
		metadataExample.setComponentId(componentId);
		ComponentMetadata componentMetadata = service.getPersistenceService().queryOneByExample(ComponentMetadata.class, metadataExample);
		return sendSingleEntityResponse(componentMetadata);
	}

	@GET
	@APIDescription("Get the dependencies from the entity")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentMetadataView.class)
	@Path("/{id}/metadata/view")
	public Response getComponentMetadataView(
			@PathParam("id")
			@RequiredParam String componentId,
			@BeanParam FilterQueryParams filterQueryParams)
	{
		ValidationResult validationResult = filterQueryParams.validate();
		if (!validationResult.valid()) {
			return sendSingleEntityResponse(validationResult.toRestError());
		}

		ComponentMetadata metadataExample = new ComponentMetadata();
		metadataExample.setActiveStatus(filterQueryParams.getStatus());
		metadataExample.setComponentId(componentId);

		List<ComponentMetadata> componentMetadata = service.getPersistenceService().queryByExample(ComponentMetadata.class, metadataExample);
		componentMetadata = filterQueryParams.filter(componentMetadata);
		List<ComponentMetadataView> views = ComponentMetadataView.toViewList(componentMetadata);

		GenericEntity<List<ComponentMetadataView>> entity = new GenericEntity<List<ComponentMetadataView>>(views)
		{
		};
		return sendSingleEntityResponse(entity);
	}

	@DELETE
	@RequireAdmin
	@APIDescription("Inactivates metadata from the specified component")
	@Path("/{id}/metadata/{metadataId}")
	public void deleteComponentMetadata(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("metadataId")
			@RequiredParam String metadataId)
	{
		ComponentMetadata componentMetadata = service.getPersistenceService().findById(ComponentMetadata.class, metadataId);
		if (componentMetadata != null) {
			checkBaseComponentBelongsToComponent(componentMetadata, componentId);
			service.getComponentService().deactivateBaseComponent(ComponentMetadata.class, metadataId);
		}
	}

	@PUT
	@RequireAdmin
	@APIDescription("Removes metadata from the specified component")
	@Path("/{id}/metadata/{metadataId}/activate")
	public Response activateComponentMetadata(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("metadataId")
			@RequiredParam String metadataId)
	{
		ComponentMetadata metadataExample = new ComponentMetadata();
		metadataExample.setMetadataId(metadataId);
		metadataExample.setComponentId(componentId);
		ComponentMetadata componentMetadata = service.getPersistenceService().queryOneByExample(ComponentMetadata.class, metadataExample);
		if (componentMetadata != null) {
			service.getComponentService().activateBaseComponent(ComponentMetadata.class, metadataId);
		}
		return sendSingleEntityResponse(componentMetadata);
	}

	@POST
	@RequireAdmin
	@APIDescription("Add metadata to the specified entity")
	@Consumes(MediaType.APPLICATION_JSON)
	@DataType(ComponentMetadata.class)
	@Path("/{id}/metadata")
	public Response addComponentMetadata(
			@PathParam("id")
			@RequiredParam String componentId,
			@RequiredParam ComponentMetadata metadata)
	{
		metadata.setComponentId(componentId);
		return saveMetadata(metadata, true);
	}

	@PUT
	@RequireAdmin
	@APIDescription("Update metadata associated to the specified entity")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{id}/metadata/{metadataId}")
	public Response updateComponentMetadata(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("metadataId")
			@RequiredParam String metadataId,
			@RequiredParam ComponentMetadata metadata)
	{
		Response response = Response.status(Response.Status.NOT_FOUND).build();
		ComponentMetadata componentMetadata = service.getPersistenceService().findById(ComponentMetadata.class, metadataId);
		if (componentMetadata != null) {
			checkBaseComponentBelongsToComponent(componentMetadata, componentId);
			metadata.setMetadataId(metadataId);
			metadata.setComponentId(componentId);
			response = saveMetadata(metadata, false);
		}
		return response;
	}

	private Response saveMetadata(ComponentMetadata metadata, Boolean post)
	{
		ValidationModel validationModel = new ValidationModel(metadata);
		validationModel.setConsumeFieldsOnly(true);
		ValidationResult validationResult = ValidationUtil.validate(validationModel);
		if (validationResult.valid()) {
			metadata.setActiveStatus(ComponentMetadata.ACTIVE_STATUS);
			metadata.setCreateUser(SecurityUtil.getCurrentUserName());
			metadata.setUpdateUser(SecurityUtil.getCurrentUserName());
			service.getComponentService().saveComponentMetadata(metadata);
		} else {
			return Response.ok(validationResult.toRestError()).build();
		}
		if (post) {
			return Response.created(URI.create("v1/resource/components/" + metadata.getComponentId() + "/metadata/" + metadata.getMetadataId())).entity(metadata).build();
		} else {
			return Response.ok(metadata).build();
		}
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed"  desc="ComponentRESTResource QUESTION section">
	@GET
	@APIDescription("Get the questions associated with the specified component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentQuestion.class)
	@Path("/{id}/questions")
	public List<ComponentQuestion> getComponentQuestions(
			@PathParam("id")
			@RequiredParam String componentId)
	{
		return service.getComponentService().getBaseComponent(ComponentQuestion.class, componentId);
	}

	@GET
	@APIDescription("Get the questions associated with the specified component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentQuestionView.class)
	@Path("/{id}/questions/view")
	public Response getComponentQuestionView(
			@PathParam("id")
			@RequiredParam String componentId,
			@BeanParam FilterQueryParams filterQueryParams)
	{
		ValidationResult validationResult = filterQueryParams.validate();
		if (!validationResult.valid()) {
			return sendSingleEntityResponse(validationResult.toRestError());
		}

		ComponentQuestion questionExample = new ComponentQuestion();
		questionExample.setActiveStatus(filterQueryParams.getStatus());
		questionExample.setComponentId(componentId);

		List<ComponentQuestion> componentQuestions = service.getPersistenceService().queryByExample(ComponentQuestion.class, questionExample);
		componentQuestions = filterQueryParams.filter(componentQuestions);

		ComponentQuestionResponse responseExample = new ComponentQuestionResponse();
		responseExample.setComponentId(componentId);

		List<ComponentQuestionResponse> componentQuestionResponses = service.getPersistenceService().queryByExample(ComponentQuestionResponse.class, responseExample);
		Map<String, List<ComponentQuestionResponseView>> responseMap = new HashMap<>();
		for (ComponentQuestionResponse componentQuestionResponse : componentQuestionResponses) {
			if (responseMap.containsKey(componentQuestionResponse.getQuestionId())) {
				responseMap.get(componentQuestionResponse.getQuestionId()).add(ComponentQuestionResponseView.toView(componentQuestionResponse));
			} else {
				List<ComponentQuestionResponseView> responseViews = new ArrayList<>();
				responseViews.add(ComponentQuestionResponseView.toView(componentQuestionResponse));
				responseMap.put(componentQuestionResponse.getQuestionId(), responseViews);
			}
		}
		List<ComponentQuestionView> views = ComponentQuestionView.toViewList(componentQuestions, responseMap);

		GenericEntity<List<ComponentQuestionView>> entity = new GenericEntity<List<ComponentQuestionView>>(views)
		{
		};
		return sendSingleEntityResponse(entity);
	}

	@GET
	@APIDescription("Get the questions associated with the specified component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentQuestionView.class)
	@Path("/{id}/questions/details")
	public List<ComponentQuestionView> getDetailComponentQuestions(
			@PathParam("id")
			@RequiredParam String componentId)
	{
		List<ComponentQuestionView> componentQuestionViews = new ArrayList<>();
		List<ComponentQuestion> questions = service.getComponentService().getBaseComponent(ComponentQuestion.class, componentId);
		for (ComponentQuestion question : questions) {
			ComponentQuestionResponse responseExample = new ComponentQuestionResponse();
			responseExample.setActiveStatus(ComponentQuestionResponse.ACTIVE_STATUS);
			responseExample.setQuestionId(question.getQuestionId());

			List<ComponentQuestionResponse> responses = service.getPersistenceService().queryByExample(ComponentQuestionResponse.class, new QueryByExample(responseExample));
			ComponentQuestionView questionView = ComponentQuestionView.toView(question, ComponentQuestionResponseView.toViewList(responses));
			componentQuestionViews.add(questionView);
		}
		return componentQuestionViews;
	}

	@GET
	@APIDescription("Get a question for the specified component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentQuestion.class)
	@Path("/{id}/questions/{questionId}")
	public Response getComponentQuestionResponses(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("questionId")
			@RequiredParam String questionId)
	{
		ComponentQuestion componentQuestion = service.getPersistenceService().findById(ComponentQuestion.class, questionId);
		if (componentQuestion != null) {
			checkBaseComponentBelongsToComponent(componentQuestion, componentId);
		}
		return sendSingleEntityResponse(componentQuestion);
	}

	@DELETE
	@APIDescription("Inactivates a question from the specified entity")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}/questions/{questionId}")
	public Response deleteComponentQuestion(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("questionId")
			@RequiredParam String questionId)
	{
		Response response = Response.ok().build();
		ComponentQuestion componentQuestion = service.getPersistenceService().findById(ComponentQuestion.class, questionId);
		if (componentQuestion != null) {
			checkBaseComponentBelongsToComponent(componentQuestion, componentId);
			response = ownerCheck(componentQuestion);
			if (response == null) {
				service.getComponentService().deactivateBaseComponent(ComponentQuestion.class, questionId);
				response = Response.ok().build();
			}
		}
		return response;
	}

	@PUT
	@RequireAdmin
	@APIDescription("Activates a question from the specified entity")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}/questions/{questionId}/activate")
	public Response activateComponentQuestion(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("questionId")
			@RequiredParam String questionId)
	{
		ComponentQuestion componentQuestionExample = new ComponentQuestion();
		componentQuestionExample.setComponentId(componentId);
		componentQuestionExample.setQuestionId(questionId);

		ComponentQuestion componentQuestion = service.getPersistenceService().queryOneByExample(ComponentQuestion.class, componentQuestionExample);
		if (componentQuestion != null) {
			service.getComponentService().activateBaseComponent(ComponentQuestion.class, questionId);
			componentQuestion.setActiveStatus(ComponentQuestion.ACTIVE_STATUS);
		}
		return sendSingleEntityResponse(componentQuestion);
	}

	@POST
	@APIDescription("Add a new question to the specified entity")
	@Consumes(MediaType.APPLICATION_JSON)
	@DataType(ComponentQuestion.class)
	@Path("/{id}/questions")
	public Response addComponentQuestion(
			@PathParam("id")
			@RequiredParam String componentId,
			@RequiredParam ComponentQuestion question)
	{
		question.setComponentId(componentId);
		return saveQuestion(question, true);
	}

	@PUT
	@APIDescription("Update a question associated with the specified entity")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{id}/questions/{questionId}")
	public Response updateComponentQuestion(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("questionId")
			@RequiredParam String questionId,
			@RequiredParam ComponentQuestion question)
	{
		Response response = Response.ok().build();
		ComponentQuestion componentQuestion = service.getPersistenceService().findById(ComponentQuestion.class, questionId);
		if (componentQuestion != null) {
			checkBaseComponentBelongsToComponent(componentQuestion, componentId);
			response = ownerCheck(componentQuestion);
			if (response == null) {
				question.setComponentId(componentId);
				question.setQuestionId(questionId);
				response = saveQuestion(question, false);
			}
		}
		return response;
	}

	private Response saveQuestion(ComponentQuestion question, Boolean post)
	{
		ValidationModel validationModel = new ValidationModel(question);
		validationModel.setConsumeFieldsOnly(true);
		ValidationResult validationResult = ValidationUtil.validate(validationModel);
		if (validationResult.valid()) {
			question.setActiveStatus(ComponentQuestion.ACTIVE_STATUS);
			question.setCreateUser(SecurityUtil.getCurrentUserName());
			question.setUpdateUser(SecurityUtil.getCurrentUserName());
			service.getComponentService().saveComponentQuestion(question);
		} else {
			return Response.ok(validationResult.toRestError()).build();
		}
		if (post) {
			return Response.created(URI.create("v1/resource/components/" + question.getComponentId() + "/questions/" + question.getQuestionId())).entity(question).build();
		} else {
			return Response.ok(question).build();
		}
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed"  desc="ComponentRESTResource QUESTION RESPONSE section">
	@GET
	@APIDescription("Gets the responses for a given question associated to the specified component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentQuestionResponse.class)
	@Path("/{id}/questions/{questionId}/responses")
	public List<ComponentQuestionResponse> getComponentQuestionResponse(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("questionId")
			@RequiredParam String questionId)
	{
		ComponentQuestionResponse responseExample = new ComponentQuestionResponse();
		responseExample.setComponentId(componentId);
		responseExample.setQuestionId(questionId);
		List<ComponentQuestionResponse> responses = service.getPersistenceService().queryByExample(ComponentQuestionResponse.class, responseExample);
		return responses;
	}

	@GET
	@APIDescription("Gets the responses for a given question associated to the specified component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentQuestionResponse.class)
	@Path("/{id}/questions/{questionId}/responses/{responseId}")
	public Response getComponentQuestionResponse(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("questionId")
			@RequiredParam String questionId,
			@PathParam("responseId")
			@RequiredParam String responseId)
	{
		ComponentQuestionResponse responseExample = new ComponentQuestionResponse();
		responseExample.setComponentId(componentId);
		responseExample.setQuestionId(questionId);
		responseExample.setResponseId(responseId);
		ComponentQuestionResponse questionResponse = service.getPersistenceService().queryOneByExample(ComponentQuestionResponse.class, responseExample);
		return sendSingleEntityResponse(questionResponse);
	}

	@DELETE
	@APIDescription("Inactivates a response from the given question on the specified component")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}/questions/{questionId}/responses/{responseId}")
	public Response deleteComponentQuestionResponse(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("questionId")
			@RequiredParam String questionId,
			@PathParam("responseId")
			@RequiredParam String responseId)
	{
		Response response = Response.ok().build();
		ComponentQuestionResponse responseExample = new ComponentQuestionResponse();
		responseExample.setComponentId(componentId);
		responseExample.setQuestionId(questionId);
		responseExample.setResponseId(responseId);
		ComponentQuestionResponse questionResponse = service.getPersistenceService().queryOneByExample(ComponentQuestionResponse.class, responseExample);
		if (questionResponse != null) {
			response = ownerCheck(questionResponse);
			if (response == null) {
				service.getComponentService().deactivateBaseComponent(ComponentQuestionResponse.class, responseId);
				response = Response.ok().build();
			}
		}
		return response;
	}

	@PUT
	@RequireAdmin
	@APIDescription("Activates a response from the given question on the specified component")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}/questions/{questionId}/responses/{responseId}/activate")
	public Response activateComponentQuestionResponse(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("questionId")
			@RequiredParam String questionId,
			@PathParam("responseId")
			@RequiredParam String responseId)
	{
		ComponentQuestionResponse responseExample = new ComponentQuestionResponse();
		responseExample.setComponentId(componentId);
		responseExample.setQuestionId(questionId);
		responseExample.setResponseId(responseId);
		ComponentQuestionResponse questionResponse = service.getPersistenceService().queryOneByExample(ComponentQuestionResponse.class, responseExample);
		if (questionResponse != null) {
			service.getComponentService().activateBaseComponent(ComponentQuestionResponse.class, responseId);
			questionResponse.setActiveStatus(ComponentQuestionResponse.ACTIVE_STATUS);
		}
		return sendSingleEntityResponse(questionResponse);
	}

	@POST
	@APIDescription("Add a response to the given question on the specified component")
	@Consumes(MediaType.APPLICATION_JSON)
	@DataType(ComponentQuestionResponse.class)
	@Path("/{id}/questions/{questionId}/responses")
	public Response addComponentQuestionResponse(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("questionId")
			@RequiredParam String questionId,
			@RequiredParam ComponentQuestionResponse response)
	{
		response.setComponentId(componentId);
		response.setQuestionId(questionId);
		return saveQuestionResponse(response, true);
	}

	@PUT
	@APIDescription("Gets full component details (This the packed view for displaying)")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{id}/questions/{questionId}/responses/{responseId}")
	public Response updateComponentQuestionResponse(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("questionId")
			@RequiredParam String questionId,
			@PathParam("responseId")
			@RequiredParam String responseId,
			@RequiredParam ComponentQuestionResponse questionResponseInput)
	{
		Response response = Response.status(Response.Status.NOT_FOUND).build();

		ComponentQuestionResponse responseExample = new ComponentQuestionResponse();
		responseExample.setComponentId(componentId);
		responseExample.setQuestionId(questionId);
		responseExample.setResponseId(responseId);
		ComponentQuestionResponse questionResponse = service.getPersistenceService().queryOneByExample(ComponentQuestionResponse.class, responseExample);
		if (questionResponse != null) {
			response = ownerCheck(questionResponse);
			if (response == null) {
				questionResponseInput.setComponentId(componentId);
				questionResponseInput.setQuestionId(questionId);
				questionResponseInput.setResponseId(responseId);
				response = saveQuestionResponse(questionResponseInput, false);
			}
		}
		return response;
	}

	private Response saveQuestionResponse(ComponentQuestionResponse response, Boolean post)
	{
		ValidationModel validationModel = new ValidationModel(response);
		validationModel.setConsumeFieldsOnly(true);
		ValidationResult validationResult = ValidationUtil.validate(validationModel);
		if (validationResult.valid()) {
			response.setActiveStatus(ComponentQuestionResponse.ACTIVE_STATUS);
			response.setCreateUser(SecurityUtil.getCurrentUserName());
			response.setUpdateUser(SecurityUtil.getCurrentUserName());
			service.getComponentService().saveComponentQuestionResponse(response);
		} else {
			return Response.ok(validationResult.toRestError()).build();
		}
		if (post) {

			return Response.created(URI.create("v1/resource/components/" + response.getComponentId() + "/questions/" + response.getQuestionId() + "/responses/" + response.getResponseId())).entity(response).build();
		} else {
			return Response.ok(response).build();
		}
	}
	// </editor-fold>

	//<editor-fold defaultstate="collapsed"  desc="ComponentRESTResource REVIEW section">
	@GET
	@APIDescription("Get the reviews for a specified entity")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentReview.class)
	@Path("/{id}/reviews")
	public List<ComponentReview> getComponentReview(
			@PathParam("id")
			@RequiredParam String componentId)
	{
		return service.getComponentService().getBaseComponent(ComponentReview.class, componentId);
	}

	@GET
	@APIDescription("Get the reviews for a specified entity")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentReviewView.class)
	@Path("/{id}/reviews/view")
	public Response getComponentReviewView(
			@PathParam("id")
			@RequiredParam String componentId,
			@BeanParam FilterQueryParams filterQueryParams)
	{
		ValidationResult validationResult = filterQueryParams.validate();
		if (!validationResult.valid()) {
			return sendSingleEntityResponse(validationResult.toRestError());
		}

		ComponentReview reviewExample = new ComponentReview();
		reviewExample.setActiveStatus(filterQueryParams.getStatus());
		reviewExample.setComponentId(componentId);

		List<ComponentReview> componentReviews = service.getPersistenceService().queryByExample(ComponentReview.class, reviewExample);
		componentReviews = filterQueryParams.filter(componentReviews);
		List<ComponentReviewView> views = ComponentReviewView.toViewList(componentReviews);

		GenericEntity<List<ComponentReviewView>> entity = new GenericEntity<List<ComponentReviewView>>(views)
		{
		};
		return sendSingleEntityResponse(entity);
	}

	@GET
	@APIDescription("Get the reviews for a specified user")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentReviewView.class)
	@Path("/reviews/{username}")
	public List<ComponentReviewView> getComponentReviewsByUsername(
			@PathParam("username")
			@RequiredParam String username)
	{
		return service.getComponentService().getReviewByUser(username);
	}

	@DELETE
	@APIDescription("Remove a review from the specified entity")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}/reviews/{reviewId}")
	public Response deleteComponentReview(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("reviewId")
			@RequiredParam String reviewId)
	{
		Response response = Response.ok().build();
		ComponentReview componentReview = service.getPersistenceService().findById(ComponentReview.class, reviewId);
		if (componentReview != null) {
			response = ownerCheck(componentReview);
			if (response == null) {
				service.getComponentService().deactivateBaseComponent(ComponentReview.class, reviewId);
			}
		}
		return response;
	}

	@PUT
	@RequireAdmin
	@APIDescription("Activate a review on  the specified component")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}/reviews/{reviewId}/activate")
	public Response activateComponentReview(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("reviewId")
			@RequiredParam String reviewId)
	{
		ComponentReview componentReviewExample = new ComponentReview();
		componentReviewExample.setComponentId(componentId);
		componentReviewExample.setComponentReviewId(reviewId);

		ComponentReview componentReview = service.getPersistenceService().queryOneByExample(ComponentReview.class, componentReviewExample);
		if (componentReview != null) {
			service.getComponentService().activateBaseComponent(ComponentReview.class, reviewId);
			componentReview.setActiveStatus(ComponentReview.ACTIVE_STATUS);
		}
		return sendSingleEntityResponse(componentReviewExample);
	}

	@POST
	@APIDescription("Add a review to the specified entity")
	@Consumes(MediaType.APPLICATION_JSON)
	@DataType(ComponentReview.class)
	@Path("/{id}/reviews")
	public Response addComponentReview(
			@PathParam("id")
			@RequiredParam String componentId,
			@RequiredParam ComponentReview review)
	{
		review.setComponentId(componentId);
		return saveReview(review, true);
	}

	@PUT
	@APIDescription("Update a review associated with the given entity")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{id}/reviews/{reviewId}")
	public Response updateComponentReview(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("reviewId")
			@RequiredParam String reviewId,
			@RequiredParam ComponentReview review)
	{
		Response response = Response.status(Response.Status.NOT_FOUND).build();
		ComponentReview componentReview = service.getPersistenceService().findById(ComponentReview.class, reviewId);
		if (componentReview != null) {
			checkBaseComponentBelongsToComponent(componentReview, componentId);
			response = ownerCheck(componentReview);
			if (response == null) {
				review.setComponentId(componentId);
				review.setComponentReviewId(reviewId);
				response = saveReview(review, false);
			}
		}
		return response;
	}

	private Response saveReview(ComponentReview review, Boolean post)
	{
		ValidationModel validationModel = new ValidationModel(review);
		validationModel.setConsumeFieldsOnly(true);
		ValidationResult validationResult = ValidationUtil.validate(validationModel);
		if (validationResult.valid()) {
			review.setActiveStatus(ComponentReview.ACTIVE_STATUS);
			review.setCreateUser(SecurityUtil.getCurrentUserName());
			review.setUpdateUser(SecurityUtil.getCurrentUserName());
			service.getComponentService().saveComponentReview(review);
		} else {
			return Response.ok(validationResult.toRestError()).build();
		}
		if (post) {
			return Response.created(URI.create("v1/resource/components/" + review.getComponentId() + "/review/" + review.getComponentReviewId())).entity(review).build();
		} else {
			return Response.ok(review).build();
		}
	}

	@POST
	@APIDescription("Add a review to the specified entity")
	@Consumes(MediaType.APPLICATION_JSON)
	@DataType(ComponentReviewView.class)
	@Path("/{id}/reviews/detail")
	public Response addComponentReviewDetail(
			@PathParam("id")
			@RequiredParam String componentId,
			@RequiredParam ComponentReviewView review)
	{
		review.setComponentId(componentId);
		return saveFullReview(review, true);
	}

	@PUT
	@APIDescription("Update a review associated with the given entity")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{id}/reviews/{reviewId}/detail")
	public Response updateComponentReviewDetail(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("reviewId")
			@RequiredParam String reviewId,
			@RequiredParam ComponentReviewView review)
	{
		Response response = Response.status(Response.Status.NOT_FOUND).build();
		ComponentReview componentReview = service.getPersistenceService().findById(ComponentReview.class, reviewId);
		if (componentReview != null) {
			checkBaseComponentBelongsToComponent(componentReview, componentId);
			response = ownerCheck(componentReview);
			if (response == null) {
				review.setComponentId(componentId);
				review.setReviewId(reviewId);
				response = saveFullReview(review, false);
			}
		}
		return response;
	}

	private Response saveFullReview(ComponentReviewView review, Boolean post)
	{
		ComponentReview componentReview = new ComponentReview();
		componentReview.setComponentId(review.getComponentId());
		componentReview.setComponentReviewId(review.getReviewId());
		componentReview.setComment(review.getComment());
		componentReview.setLastUsed(review.getLastUsed());
		componentReview.setOrganization(review.getOrganization());
		componentReview.setRating(review.getRating());
		componentReview.setRecommend(review.isRecommend());
		componentReview.setTitle(review.getTitle());
		componentReview.setUserTimeCode(review.getUserTimeCode());
		componentReview.setUserTypeCode(review.getUserTypeCode());

		List<ComponentReviewPro> pros = new ArrayList<>();
		for (ComponentReviewProCon pro : review.getPros()) {
			ComponentReviewPro componentReviewPro = new ComponentReviewPro();
			ComponentReviewProPk reviewProPk = new ComponentReviewProPk();
			reviewProPk.setReviewPro(pro.getText());
			componentReviewPro.setComponentReviewProPk(reviewProPk);
			pros.add(componentReviewPro);
		}

		List<ComponentReviewCon> cons = new ArrayList<>();
		for (ComponentReviewProCon con : review.getCons()) {
			ComponentReviewCon componentReviewCon = new ComponentReviewCon();
			ComponentReviewConPk reviewConPk = new ComponentReviewConPk();
			reviewConPk.setReviewCon(con.getText());
			componentReviewCon.setComponentReviewConPk(reviewConPk);
			cons.add(componentReviewCon);
		}

		ValidationResult validationResult = service.getComponentService().saveDetailReview(componentReview, pros, cons);

		if (validationResult.valid() == false) {
			return Response.ok(validationResult.toRestError()).build();
		}
		if (post) {
			return Response.created(URI.create("v1/resource/components/" + componentReview.getComponentId() + "/review/" + componentReview.getComponentReviewId())).entity(review).build();
		} else {
			return Response.ok(review).build();
		}
	}
	//</editor-fold>

	//<editor-fold defaultstate="collapsed"  desc="ComponentRESTResource REVIEW CON section">
	@GET
	@APIDescription("Get the cons associated to a review")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentReviewCon.class)
	@Path("/{id}/reviews/{reviewId}/cons")
	public List<ComponentReviewCon> getComponentReviewCon(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("reviewId")
			@RequiredParam String reviewId)
	{
		ComponentReviewCon componentReviewConExample = new ComponentReviewCon();
		ComponentReviewConPk componentReviewConPk = new ComponentReviewConPk();
		componentReviewConPk.setComponentReviewId(reviewId);
		componentReviewConExample.setComponentReviewConPk(componentReviewConPk);
		componentReviewConExample.setComponentId(componentId);
		return service.getPersistenceService().queryByExample(ComponentReviewCon.class, new QueryByExample(componentReviewConExample));
	}

	@GET
	@APIDescription("Get the cons associated to a review")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentReviewCon.class)
	@Path("/{id}/reviews/{reviewId}/cons/{conId}")
	public Response getComponentReviewCon(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("reviewId")
			@RequiredParam String reviewId,
			@PathParam("conId")
			@RequiredParam String conId)
	{
		ComponentReviewCon componentReviewConExample = new ComponentReviewCon();
		ComponentReviewConPk componentReviewConPk = new ComponentReviewConPk();
		componentReviewConPk.setComponentReviewId(reviewId);
		componentReviewConPk.setReviewCon(conId);
		componentReviewConExample.setComponentReviewConPk(componentReviewConPk);
		componentReviewConExample.setComponentId(componentId);

		ComponentReviewCon reviewCon = service.getPersistenceService().queryOneByExample(ComponentReviewCon.class, new QueryByExample(componentReviewConExample));
		return sendSingleEntityResponse(reviewCon);
	}

	@DELETE
	@APIDescription("Removes all cons from the given review accociated with the specified entity")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}/reviews/{reviewId}/cons")
	public Response deleteComponentReviewCon(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("reviewId")
			@RequiredParam String reviewId)
	{
		Response response = Response.ok().build();
		ComponentReview componentReview = service.getPersistenceService().findById(ComponentReview.class, reviewId);
		if (componentReview != null) {
			checkBaseComponentBelongsToComponent(componentReview, componentId);
			response = ownerCheck(componentReview);
			if (response == null) {
				ComponentReviewCon example = new ComponentReviewCon();
				ComponentReviewConPk pk = new ComponentReviewConPk();
				pk.setComponentReviewId(reviewId);
				example.setComponentReviewConPk(pk);
				service.getPersistenceService().deleteByExample(example);
			}
		}
		return response;
	}

	@POST
	@APIDescription("Add a cons to the given review associated with the specified entity")
	@Consumes(MediaType.APPLICATION_JSON)
	@DataType(ComponentReviewCon.class)
	@Path("/{id}/reviews/{reviewId}/cons")
	public Response addComponentReviewCon(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("reviewId")
			@RequiredParam String reviewId,
			@RequiredParam String text)
	{
		Response response = Response.status(Response.Status.NOT_FOUND).build();
		ComponentReview componentReview = service.getPersistenceService().findById(ComponentReview.class, reviewId);
		if (componentReview != null) {
			checkBaseComponentBelongsToComponent(componentReview, componentId);
			response = ownerCheck(componentReview);
			if (response == null) {
				ComponentReviewCon con = new ComponentReviewCon();
				ComponentReviewConPk pk = new ComponentReviewConPk();
				pk.setComponentReviewId(reviewId);
				ReviewCon conCode = service.getLookupService().getLookupEnity(ReviewCon.class, text);
				if (conCode == null) {
					conCode = service.getLookupService().getLookupEnityByDesc(ReviewCon.class, text);
					if (conCode == null) {
						pk.setReviewCon(null);
					} else {
						pk.setReviewCon(conCode.getCode());
					}
				} else {
					pk.setReviewCon(conCode.getCode());
				}
				con.setComponentReviewConPk(pk);
				con.setActiveStatus(ComponentReviewCon.ACTIVE_STATUS);
				con.setComponentId(componentId);
				ValidationModel validationModel = new ValidationModel(con);
				validationModel.setConsumeFieldsOnly(true);
				ValidationResult validationResult = ValidationUtil.validate(validationModel);

				if (validationResult.valid()) {
					con.setCreateUser(SecurityUtil.getCurrentUserName());
					con.setUpdateUser(SecurityUtil.getCurrentUserName());
					service.getComponentService().saveComponentReviewCon(con);
					response = Response.created(URI.create("v1/resource/components/" + con.getComponentId()
							+ "/reviews/" + con.getComponentReviewConPk().getComponentReviewId()
							+ "/cons/" + con.getComponentReviewConPk().getReviewCon())).entity(con).build();

				} else {
					response = Response.ok(validationResult.toRestError()).build();
				}
			}
		}
		return response;
	}
	// </editor-fold>

	//<editor-fold defaultstate="collapsed"  desc="ComponentRESTResource REVIEW PRO section">
	@GET
	@APIDescription("Get the pros for a review associated with the given entity")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentReviewPro.class)
	@Path("/{id}/reviews/{reviewId}/pros")
	public List<ComponentReviewPro> getComponentReviewPro(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("reviewId")
			@RequiredParam String reviewId)
	{
		ComponentReviewPro componentReviewProExample = new ComponentReviewPro();
		componentReviewProExample.setComponentId(componentId);
		ComponentReviewProPk componentReviewProPk = new ComponentReviewProPk();
		componentReviewProPk.setComponentReviewId(reviewId);
		componentReviewProExample.setComponentReviewProPk(componentReviewProPk);
		return service.getPersistenceService().queryByExample(ComponentReviewPro.class, new QueryByExample(componentReviewProExample));
	}

	@GET
	@APIDescription("Get the pros for a review associated with the given entity")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentReviewPro.class)
	@Path("/{id}/reviews/{reviewId}/pros/{proId}")
	public Response getComponentReviewPro(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("reviewId")
			@RequiredParam String reviewId,
			@PathParam("proId")
			@RequiredParam String proId)
	{
		ComponentReviewPro componentReviewProExample = new ComponentReviewPro();
		componentReviewProExample.setComponentId(componentId);
		ComponentReviewProPk componentReviewProPk = new ComponentReviewProPk();
		componentReviewProPk.setComponentReviewId(reviewId);
		componentReviewProPk.setReviewPro(proId);
		componentReviewProExample.setComponentReviewProPk(componentReviewProPk);
		ComponentReviewPro componentReviewPro = service.getPersistenceService().queryOneByExample(ComponentReviewPro.class, new QueryByExample(componentReviewProExample));
		return sendSingleEntityResponse(componentReviewPro);
	}

	@DELETE
	@APIDescription("Removes all pros from the review associated with a specified entity")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}/reviews/{reviewId}/pros")
	public Response deleteComponentReviewPro(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("reviewId")
			@RequiredParam String reviewId)
	{
		Response response = Response.ok().build();
		ComponentReview componentReview = service.getPersistenceService().findById(ComponentReview.class, reviewId);
		if (componentReview != null) {
			checkBaseComponentBelongsToComponent(componentReview, componentId);
			response = ownerCheck(componentReview);
			if (response == null) {
				ComponentReviewPro example = new ComponentReviewPro();
				ComponentReviewProPk pk = new ComponentReviewProPk();
				pk.setComponentReviewId(reviewId);
				example.setComponentReviewProPk(pk);
				service.getPersistenceService().deleteByExample(example);
				service.getComponentService().deactivateBaseComponent(ComponentReviewPro.class, pk);
			}
		}
		return response;
	}

	@POST
	@APIDescription("Add a pro to the review associated with the specified entity")
	@Consumes(MediaType.APPLICATION_JSON)
	@DataType(ComponentReviewPro.class)
	@Path("/{id}/reviews/{reviewId}/pros")
	public Response addComponentReviewPro(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("reviewId")
			@RequiredParam String reviewId,
			@RequiredParam String text)
	{
		Response response = Response.status(Response.Status.NOT_FOUND).build();
		ComponentReview componentReview = service.getPersistenceService().findById(ComponentReview.class, reviewId);
		if (componentReview != null) {
			checkBaseComponentBelongsToComponent(componentReview, componentId);
			response = ownerCheck(componentReview);
			if (response == null) {
				ComponentReviewPro pro = new ComponentReviewPro();
				ComponentReviewProPk pk = new ComponentReviewProPk();
				pk.setComponentReviewId(reviewId);
				ReviewPro proCode = service.getLookupService().getLookupEnity(ReviewPro.class, text);
				if (proCode == null) {
					proCode = service.getLookupService().getLookupEnityByDesc(ReviewPro.class, text);
					if (proCode == null) {
						pk.setReviewPro(null);
					} else {
						pk.setReviewPro(proCode.getCode());
					}
				} else {
					pk.setReviewPro(proCode.getCode());
				}
				pro.setComponentReviewProPk(pk);
				pro.setActiveStatus(ComponentReviewPro.ACTIVE_STATUS);
				pro.setComponentId(componentId);

				ValidationModel validationModel = new ValidationModel(pro);
				validationModel.setConsumeFieldsOnly(true);
				ValidationResult validationResult = ValidationUtil.validate(validationModel);
				if (validationResult.valid()) {
					pro.setCreateUser(SecurityUtil.getCurrentUserName());
					pro.setUpdateUser(SecurityUtil.getCurrentUserName());
					service.getComponentService().saveComponentReviewPro(pro);
					response = Response.created(URI.create("v1/resource/components/" + pro.getComponentId()
							+ "/reviews/" + pro.getComponentReviewProPk().getComponentReviewId()
							+ "/pros/" + pro.getComponentReviewProPk().getReviewPro())).entity(pro).build();
				} else {
					response = Response.ok(validationResult.toRestError()).build();
				}
			}
		}
		return response;
	}
	// </editor-fold>

	//<editor-fold defaultstate="collapsed"  desc="ComponentRESTResource TAG section">
	@GET
	@APIDescription("Get the entire tag list (Tag Cloud)")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentTag.class)
	@Path("/tags")
	public List<ComponentTag> getComponentTags()
	{
		return service.getComponentService().getTagCloud();
	}

	@GET
	@APIDescription("Get all the tag list for a specified component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentTag.class)
	@Path("/{id}/tags")
	public List<ComponentTag> getComponentTag(
			@PathParam("id")
			@RequiredParam String componentId)
	{
		return service.getComponentService().getBaseComponent(ComponentTag.class, componentId);
	}

	@GET
	@APIDescription("Get a tag for a component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentTag.class)
	@Path("/{id}/tags/{tagId}")
	public Response getComponentTag(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("tagId")
			@RequiredParam String tagId)
	{
		ComponentTag componentTagExample = new ComponentTag();
		componentTagExample.setComponentId(componentId);
		componentTagExample.setTagId(tagId);
		ComponentTag componentTag = service.getPersistenceService().queryOneByExample(ComponentTag.class, new QueryByExample(componentTagExample));
		return sendSingleEntityResponse(componentTag);
	}

	@DELETE
	@RequireAdmin
	@APIDescription("Remove all tags from the specified component")
	@Consumes({MediaType.APPLICATION_JSON})
	@DataType(ComponentTag.class)
	@Path("/{id}/tags")
	public void deleteComponentTags(
			@PathParam("id")
			@RequiredParam String componentId)
	{
		ComponentTag example = new ComponentTag();
		example.setComponentId(componentId);
		service.getPersistenceService().deleteByExample(example);
		Component temp = service.getPersistenceService().findById(Component.class, componentId);
		service.getSearchService().addIndex(temp);
	}

	@DELETE
	@APIDescription("Remove a tag by id from the specified entity")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}/tags/{tagId}")
	public Response deleteComponentTagById(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("tagId")
			@RequiredParam String tagId)
	{
		Response response = Response.status(Response.Status.NOT_FOUND).build();
		ComponentTag example = new ComponentTag();
		example.setTagId(tagId);
		example.setComponentId(componentId);
		ComponentTag componentTag = service.getPersistenceService().queryOneByExample(ComponentTag.class, new QueryByExample(example));
		if (componentTag != null) {
			response = ownerCheck(componentTag);
			if (response == null) {
				service.getComponentService().deactivateBaseComponent(ComponentTag.class, tagId);
				Component temp = service.getPersistenceService().findById(Component.class, componentId);
				service.getSearchService().addIndex(temp);
			}
		}
		return response;
	}

	@DELETE
	@APIDescription("Remove a single tag from the specified entity by the Tag Text")
	@Consumes({MediaType.APPLICATION_JSON})
	@Path("/{id}/tags/text")
	public Response deleteComponentTag(
			@PathParam("id")
			@RequiredParam String componentId,
			@RequiredParam ComponentTag example)
	{
		Response response = Response.status(Response.Status.NOT_FOUND).build();

		ComponentTag componentTagExample = new ComponentTag();
		componentTagExample.setComponentId(componentId);
		componentTagExample.setText(example.getText());
		ComponentTag tag = service.getPersistenceService().queryOneByExample(ComponentTag.class, new QueryByExample(componentTagExample));

		if (tag != null) {
			response = ownerCheck(tag);
			if (response == null) {
				service.getComponentService().deleteBaseComponent(ComponentTag.class, tag.getTagId());
				Component temp = service.getPersistenceService().findById(Component.class, componentId);
				service.getSearchService().addIndex(temp);
				response = Response.ok().build();
			}
		}
		return response;
	}

	@POST
	@APIDescription("Add tags to the specified component")
	@Consumes(MediaType.APPLICATION_JSON)
	@DataType(ComponentTag.class)
	@Path("/{id}/tags/list")
	public Response addComponentTags(
			@PathParam("id")
			@RequiredParam String componentId,
			@RequiredParam
			@DataType(ComponentTag.class) List<ComponentTag> tags)
	{
		Boolean valid = Boolean.TRUE;
		List<ComponentTag> verified = new ArrayList<>();
		List<RestErrorModel> unVerified = new ArrayList<>();
		if (tags.size() > 0) {
			for (ComponentTag tag : tags) {
				tag.setActiveStatus(ComponentTag.ACTIVE_STATUS);
				tag.setComponentId(componentId);
				ValidationModel validationModel = new ValidationModel(tag);
				validationModel.setConsumeFieldsOnly(true);
				ValidationResult validationResult = ValidationUtil.validate(validationModel);
				if (validationResult.valid()) {
					tag.setCreateUser(SecurityUtil.getCurrentUserName());
					tag.setUpdateUser(SecurityUtil.getCurrentUserName());
					verified.add(tag);
				} else {
					valid = Boolean.FALSE;
					unVerified.add(validationResult.toRestError());
				}
			}
			if (valid) {
				if (verified.size() > 0) {
					verified.stream().forEach((tag) -> {
						service.getComponentService().saveComponentTag(tag);
					});
					GenericEntity<List<ComponentTag>> entity = new GenericEntity<List<ComponentTag>>(Lists.newArrayList(verified))
					{
					};
					return Response.created(URI.create("v1/resource/components/" + verified.get(0).getComponentId() + "/tags/" + verified.get(0).getTagId())).entity(entity).build();
				} else {
					return Response.notAcceptable(null).build();
				}
			} else {
				GenericEntity<List<RestErrorModel>> entity = new GenericEntity<List<RestErrorModel>>(Lists.newArrayList(unVerified))
				{
				};
				return Response.ok(entity).build();
			}
		} else {
			return Response.notAcceptable(null).build();
		}
	}

	@POST
	@APIDescription("Add a single tag to the specified entity")
	@Consumes(MediaType.APPLICATION_JSON)
	@DataType(ComponentTag.class)
	@Path("/{id}/tags")
	public Response addComponentTag(
			@PathParam("id")
			@RequiredParam String componentId,
			@RequiredParam ComponentTag tag)
	{
		tag.setActiveStatus(ComponentTag.ACTIVE_STATUS);
		tag.setComponentId(componentId);

		List<ComponentTag> currentTags = service.getComponentService().getBaseComponent(ComponentTag.class, componentId);
		Boolean cont = Boolean.TRUE;
		if (currentTags != null && currentTags.size() > 0) {
			for (ComponentTag item : currentTags) {
				if (item.getText().equals(tag.getText())) {
					cont = Boolean.FALSE;
				}
			}
		}

		ValidationModel validationModel = new ValidationModel(tag);
		validationModel.setConsumeFieldsOnly(true);
		ValidationResult validationResult = ValidationUtil.validate(validationModel);
		if (validationResult.valid()) {
			tag.setCreateUser(SecurityUtil.getCurrentUserName());
			tag.setUpdateUser(SecurityUtil.getCurrentUserName());
			if (cont) {
				service.getComponentService().saveComponentTag(tag);
			}
		} else {
			return Response.ok(validationResult.toRestError()).build();
		}
		return Response.created(URI.create("v1/resource/components/" + tag.getComponentId() + "/tags/" + tag.getTagId())).entity(tag).build();
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed"  desc="ComponentRESTResource TRACKING section">
	@GET
	@RequireAdmin
	@APIDescription("Get the list of tracking details on a specified component. Always sorts by create date.")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentTrackingWrapper.class)
	@Path("/{id}/tracking")
	public Response getActiveComponentTracking(
			@PathParam("id")
			@RequiredParam String componentId,
			@BeanParam FilterQueryParams filterQueryParams)
	{
		ValidationResult validationResult = filterQueryParams.validate();
		if (!validationResult.valid()) {
			return sendSingleEntityResponse(validationResult.toRestError());
		}

		ComponentTracking trackingExample = new ComponentTracking();
		trackingExample.setComponentId(componentId);
		trackingExample.setActiveStatus(filterQueryParams.getStatus());

		QueryByExample<ComponentTracking> queryByExample = new QueryByExample(trackingExample);
		queryByExample.setMaxResults(filterQueryParams.getMax());
		queryByExample.setFirstResult(filterQueryParams.getOffset());

		ComponentTracking trackingOrderExample = new ComponentTracking();
		trackingOrderExample.setCreateDts(QueryByExample.DATE_FLAG);
		queryByExample.setOrderBy(trackingOrderExample);
		queryByExample.setSortDirection(OpenStorefrontConstant.SORT_DESCENDING);

		List<ComponentTracking> componentTrackings = service.getPersistenceService().queryByExample(ComponentTracking.class, queryByExample);

		long total = service.getPersistenceService().countByExample(new QueryByExample(QueryType.COUNT, trackingExample));
		return sendSingleEntityResponse(new ComponentTrackingWrapper(componentTrackings, total));
	}

	@GET
	@RequireAdmin
	@APIDescription("Get the list of tracking details on a specified component")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentTracking.class)
	@Path("/{id}/tracking/{trackingId}")
	public Response getComponentTracking(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("id")
			@RequiredParam String trackingId)
	{
		ComponentTracking componentTrackingExample = new ComponentTracking();
		componentTrackingExample.setComponentId(componentId);
		componentTrackingExample.setComponentTrackingId(trackingId);
		ComponentTracking componentTracking = service.getPersistenceService().queryOneByExample(ComponentTracking.class, componentTrackingExample);
		return sendSingleEntityResponse(componentTracking);
	}

	@DELETE
	@RequireAdmin
	@APIDescription("Remove a tracking entry from the specified component")
	@Path("/{id}/tracking/{trackingId}")
	public void deleteComponentTracking(
			@PathParam("id")
			@RequiredParam String componentId,
			@PathParam("id")
			@RequiredParam String trackingId)
	{
		ComponentTracking componentTrackingExample = new ComponentTracking();
		componentTrackingExample.setComponentId(componentId);
		componentTrackingExample.setComponentTrackingId(trackingId);
		ComponentTracking componentTracking = service.getPersistenceService().queryOneByExample(ComponentTracking.class, componentTrackingExample);
		if (componentTracking != null) {
			service.getComponentService().deactivateBaseComponent(ComponentTracking.class, trackingId);
		}
	}

	@DELETE
	@RequireAdmin
	@APIDescription("Remove all tracking entries from the specified component")
	@Path("/{id}/tracking")
	public void deleteAllComponentTracking(
			@PathParam("id")
			@RequiredParam String componentId)
	{
		service.getComponentService().deleteAllBaseComponent(ComponentTracking.class, componentId);
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed"  desc="Integrations">
	@GET
	@RequireAdmin
	@APIDescription("Gets all integration models from the database.")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentIntegration.class)
	@Path("/integration")
	public List<ComponentIntegrationView> getIntegrations(
			@QueryParam("status")
			@DefaultValue("A")
			@APIDescription("Pass 'ALL' to view active and inactive") String status)
	{
		if (OpenStorefrontConstant.STATUS_VIEW_ALL.equals(status)) {
			status = null;
		}
		List<ComponentIntegration> integrationModels = service.getComponentService().getComponentIntegrationModels(status);
		List<ComponentIntegrationView> views = new ArrayList<>();
		for (ComponentIntegration temp : integrationModels) {
			views.add(ComponentIntegrationView.toView(temp));
		}
		return views;
	}

	@GET
	@RequireAdmin
	@APIDescription("Gets a integration model")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentIntegration.class)
	@Path("/{id}/integration")
	public Response getIntegration(
			@QueryParam("id") String componentId)
	{
		ComponentIntegration integration = service.getPersistenceService().findById(ComponentIntegration.class, componentId);
		ComponentIntegrationView view = ComponentIntegrationView.toView(integration);
		return sendSingleEntityResponse(view);
	}

	@POST
	@RequireAdmin
	@APIDescription("Saves a component integration model")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{componentId}/integration")
	public Response saveIntegration(
			@PathParam("componentId")
			@RequiredParam String componentId,
			ComponentIntegration integration)
	{
		ValidationModel validationModel = new ValidationModel(integration);
		validationModel.setConsumeFieldsOnly(true);
		ValidationResult validationResult = ValidationUtil.validate(validationModel);
		if (validationResult.valid()) {
			service.getComponentService().saveComponentIntegration(integration);
			return Response.created(URI.create("v1/resource/components/" + componentId + "/integration")).entity(integration).build();
		} else {
			return Response.ok(validationResult.toRestError()).build();
		}
	}

	@POST
	@RequireAdmin
	@APIDescription("Saves a component integration model")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{componentId}/integration/cron")
	public Response saveRefreshRate(
			@PathParam("componentId")
			@RequiredParam String componentId,
			String cron)
	{
		ComponentIntegration integration = service.getPersistenceService().findById(ComponentIntegration.class, componentId);
		if (integration != null) {
			integration.setRefreshRate(cron);
			ValidationModel validationModel = new ValidationModel(integration);
			validationModel.setConsumeFieldsOnly(true);
			ValidationResult validationResult = ValidationUtil.validate(validationModel);
			if (validationResult.valid()) {
				service.getComponentService().saveComponentIntegration(integration);
				return Response.created(URI.create("v1/resource/components/" + componentId + "/integration")).entity(integration).build();
			} else {
				return Response.ok(validationResult.toRestError()).build();
			}
		} else {
			return Response.ok().build();
		}

	}

	@DELETE
	@RequireAdmin
	@APIDescription("Saves a component integration model")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{componentId}/integration/cron")
	public Response deleteRefreshRate(
			@PathParam("componentId")
			@RequiredParam String componentId,
			String cron)
	{
		ComponentIntegration integration = service.getPersistenceService().findById(ComponentIntegration.class, componentId);
		if (integration != null) {
			integration.setRefreshRate(null);
			ValidationModel validationModel = new ValidationModel(integration);
			validationModel.setConsumeFieldsOnly(true);
			ValidationResult validationResult = ValidationUtil.validate(validationModel);
			if (validationResult.valid()) {
				service.getComponentService().saveComponentIntegration(integration);
				return Response.created(URI.create("v1/resource/components/" + componentId + "/integration")).entity(integration).build();
			} else {
				return Response.ok(validationResult.toRestError()).build();
			}
		} else {
			return Response.ok().build();
		}
	}

	@PUT
	@RequireAdmin
	@APIDescription("Activates  a component integration model")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{componentId}/integration/activate")
	public Response activateIntegration(
			@PathParam("componentId")
			@RequiredParam String componentId)
	{
		ComponentIntegration componentIntegration = service.getPersistenceService().findById(ComponentIntegration.class, componentId);
		if (componentIntegration != null) {
			service.getComponentService().setStatusOnComponentIntegration(componentId, ComponentIntegration.ACTIVE_STATUS);
		}
		return sendSingleEntityResponse(componentIntegration, Response.Status.NOT_MODIFIED);
	}

	@PUT
	@RequireAdmin
	@APIDescription("Inactivates a component integration model")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{componentId}/integration/inactivate")
	public Response inactiveIntegration(
			@PathParam("componentId")
			@RequiredParam String componentId)
	{
		ComponentIntegration componentIntegration = service.getPersistenceService().findById(ComponentIntegration.class, componentId);
		if (componentIntegration != null) {
			service.getComponentService().setStatusOnComponentIntegration(componentId, ComponentIntegration.INACTIVE_STATUS);
		}
		return sendSingleEntityResponse(componentIntegration, Response.Status.NOT_MODIFIED);
	}

	@DELETE
	@RequireAdmin
	@APIDescription("Removes component integration and all child configs.")
	@Path("/{componentId}/integration")
	public void deleteComponentConfig(
			@PathParam("componentId")
			@RequiredParam String componentId)
	{
		service.getComponentService().deleteComponentIntegration(componentId);
	}

	@POST
	@RequireAdmin
	@APIDescription("Runs a full component integration")
	@Path("/{componentId}/integration/run")
	public Response runComponentIntegration(
			@PathParam("componentId")
			@RequiredParam String componentId)
	{
		ComponentIntegration integration = service.getPersistenceService().findById(ComponentIntegration.class, componentId);
		if (integration != null) {
			JobManager.runComponentIntegrationNow(componentId, null);
			return Response.ok().build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}

	@POST
	@RequireAdmin
	@APIDescription("Runs all active component integrations")
	@Path("/integrations/run")
	public Response runAllComponentIntegration()
	{
		List<ComponentIntegration> componentIntegrations = service.getComponentService().getComponentIntegrationModels(ComponentIntegration.ACTIVE_STATUS);
		for (ComponentIntegration componentIntegration : componentIntegrations) {
			JobManager.runComponentIntegrationNow(componentIntegration.getComponentId(), null);
		}
		return Response.ok().build();
	}

	@GET
	@RequireAdmin
	@APIDescription("Gets all component integration configs")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentIntegrationConfig.class)
	@Path("/{componentId}/integration/configs")
	public List<ComponentIntegrationConfig> getIntegrationConfigs(
			@PathParam("componentId") String componentId)
	{
		List<ComponentIntegrationConfig> configs;
		ComponentIntegrationConfig integrationConfigExample = new ComponentIntegrationConfig();
		integrationConfigExample.setComponentId(componentId);
		configs = service.getPersistenceService().queryByExample(null, integrationConfigExample);
		return configs;
	}

	@GET
	@RequireAdmin
	@APIDescription("Gets all component integration configs")
	@Produces({MediaType.APPLICATION_JSON})
	@DataType(ComponentIntegrationConfig.class)
	@Path("/{componentId}/integration/configs/{configId}")
	public Response getIntegrationConfigs(
			@PathParam("componentId") String componentId,
			@PathParam("configId") String configId)
	{
		ComponentIntegrationConfig integrationConfigExample = new ComponentIntegrationConfig();
		integrationConfigExample.setComponentId(componentId);
		integrationConfigExample.setIntegrationConfigId(configId);
		ComponentIntegrationConfig integrationConfig = service.getPersistenceService().queryOneByExample(ComponentIntegrationConfig.class, integrationConfigExample);
		return sendSingleEntityResponse(integrationConfig);
	}

	@POST
	@RequireAdmin
	@APIDescription("Saves a component integration model")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{componentId}/integration/configs")
	public Response saveIntegrationConfig(
			@PathParam("componentId")
			@RequiredParam String componentId,
			ComponentIntegrationConfig integrationConfig)
	{
		integrationConfig.setComponentId(componentId);

		ValidationModel validationModel = new ValidationModel(integrationConfig);
		validationModel.setConsumeFieldsOnly(true);
		ValidationResult validationResult = ValidationUtil.validate(validationModel);

		if (validationResult.valid()) {
			integrationConfig = service.getComponentService().saveComponentIntegrationConfig(integrationConfig);
			return Response.created(URI.create("v1/resource/components/" + componentId + "/integration/configs/" + integrationConfig.getIntegrationConfigId())).entity(integrationConfig).build();
		} else {
			return Response.ok(validationResult.toRestError()).build();
		}
	}

	@PUT
	@RequireAdmin
	@APIDescription("Activates a component integration config")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{componentId}/integration/configs/{configId}/activate")
	public Response activateIntegrationConfig(
			@PathParam("componentId")
			@RequiredParam String componentId,
			@PathParam("configId") String configId)
	{
		ComponentIntegrationConfig integrationConfigExample = new ComponentIntegrationConfig();
		integrationConfigExample.setComponentId(componentId);
		integrationConfigExample.setIntegrationConfigId(configId);
		ComponentIntegrationConfig integrationConfig = service.getPersistenceService().queryOneByExample(ComponentIntegrationConfig.class, integrationConfigExample);

		if (integrationConfig != null) {
			service.getComponentService().setStatusOnComponentIntegrationConfig(configId, ComponentIntegrationConfig.ACTIVE_STATUS);
		}
		return sendSingleEntityResponse(integrationConfig, Response.Status.NOT_MODIFIED);
	}

	@PUT
	@RequireAdmin
	@APIDescription("Saves a component integration model")
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{componentId}/integration/configs/{configId}/inactivate")
	public Response inactiveIntegrationConfig(
			@PathParam("componentId")
			@RequiredParam String componentId,
			@PathParam("configId") String configId)
	{
		ComponentIntegrationConfig integrationConfigExample = new ComponentIntegrationConfig();
		integrationConfigExample.setComponentId(componentId);
		integrationConfigExample.setIntegrationConfigId(configId);
		ComponentIntegrationConfig integrationConfig = service.getPersistenceService().queryOneByExample(ComponentIntegrationConfig.class, integrationConfigExample);

		if (integrationConfig != null) {
			service.getComponentService().setStatusOnComponentIntegrationConfig(configId, ComponentIntegrationConfig.INACTIVE_STATUS);
		}
		return sendSingleEntityResponse(integrationConfig, Response.Status.NOT_MODIFIED);
	}

	@DELETE
	@RequireAdmin
	@APIDescription("Removes component integration config")
	@Path("/{componentId}/integration/configs/{configId}")
	public void deleteComponentIntegrationConfig(
			@PathParam("componentId")
			@RequiredParam String componentId,
			@PathParam("configId") String configId)
	{
		ComponentIntegrationConfig integrationConfigExample = new ComponentIntegrationConfig();
		integrationConfigExample.setComponentId(componentId);
		integrationConfigExample.setIntegrationConfigId(configId);
		ComponentIntegrationConfig integrationConfig = service.getPersistenceService().queryOneByExample(ComponentIntegrationConfig.class, integrationConfigExample);

		if (integrationConfig != null) {
			service.getComponentService().deleteComponentIntegrationConfig(configId);
		}
	}

	@POST
	@RequireAdmin
	@APIDescription("Runs a component integration config.")
	@Path("/{componentId}/integration/configs/{configId}/run")
	public Response runComponentIntegrationConfig(
			@PathParam("componentId")
			@RequiredParam String componentId,
			@PathParam("configId") String configId)
	{
		ComponentIntegrationConfig integrationConfigExample = new ComponentIntegrationConfig();
		integrationConfigExample.setComponentId(componentId);
		integrationConfigExample.setIntegrationConfigId(configId);
		ComponentIntegrationConfig integrationConfig = service.getPersistenceService().queryOneByExample(ComponentIntegrationConfig.class, integrationConfigExample);

		if (integrationConfig != null) {
			JobManager.runComponentIntegrationNow(componentId, configId);
			return Response.ok().build();
		} else {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
	}
	// </editor-fold>

	// <editor-fold defaultstate="collapsed"  desc="Private Utils">
	private void checkBaseComponentBelongsToComponent(BaseComponent component, String componentId)
	{
		if (component.getComponentId().equals(componentId) == false) {
			throw new OpenStorefrontRuntimeException("Entity does not belong to component", "Check input.");
		}
	}

	private Response ownerCheck(BaseEntity entity)
	{
		if (SecurityUtil.isCurrentUserTheOwner(entity)
				|| SecurityUtil.isAdminUser()) {
			return null;
		} else {
			return Response.status(Response.Status.FORBIDDEN)
					.type(MediaType.TEXT_PLAIN)
					.entity("User cannot modify resource.")
					.build();
		}
	}
	// </editor-fold>

}
