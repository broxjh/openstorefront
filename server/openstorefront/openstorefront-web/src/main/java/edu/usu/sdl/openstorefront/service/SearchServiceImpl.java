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
package edu.usu.sdl.openstorefront.service;

import edu.usu.sdl.openstorefront.exception.OpenStorefrontRuntimeException;
import edu.usu.sdl.openstorefront.service.api.SearchService;
import edu.usu.sdl.openstorefront.service.manager.SolrManager;
import edu.usu.sdl.openstorefront.service.manager.SolrManager.SolrAndOr;
import edu.usu.sdl.openstorefront.service.manager.SolrManager.SolrEquals;
import edu.usu.sdl.openstorefront.service.query.QueryByExample;
import edu.usu.sdl.openstorefront.storage.model.AttributeCode;
import edu.usu.sdl.openstorefront.storage.model.AttributeCodePk;
import edu.usu.sdl.openstorefront.storage.model.AttributeType;
import edu.usu.sdl.openstorefront.storage.model.Component;
import edu.usu.sdl.openstorefront.storage.model.ComponentAttribute;
import edu.usu.sdl.openstorefront.storage.model.ComponentAttributePk;
import edu.usu.sdl.openstorefront.storage.model.ComponentTag;
import edu.usu.sdl.openstorefront.util.OpenStorefrontConstant;
import edu.usu.sdl.openstorefront.util.StringProcessor;
import edu.usu.sdl.openstorefront.web.rest.model.ArticleView;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentSearchView;
import edu.usu.sdl.openstorefront.web.rest.model.FilterQueryParams;
import edu.usu.sdl.openstorefront.web.rest.model.SearchQuery;
import edu.usu.sdl.openstorefront.web.rest.model.SolrComponentModel;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.beans.DocumentObjectBinder;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

/**
 * Handles Searching the data set and sync the indexes
 *
 * @author gbagley
 * @author dshurleff
 */
public class SearchServiceImpl
		extends ServiceProxy
		implements SearchService
{

	private static final Logger log = Logger.getLogger(SearchServiceImpl.class.getName());

	@Override
	public List<ComponentSearchView> getAll()
	{
		ServiceProxy service = new ServiceProxy();
		List<ComponentSearchView> list = new ArrayList<>();
		List<ComponentSearchView> components = service.getComponentService().getComponents();
		list.addAll(components);
		list.addAll(service.getAttributeService().getArticlesSearchView());
		return list;
	}

	@Override
	public List<ComponentSearchView> getSearchItems(SearchQuery query, FilterQueryParams filter)
	{
		// use for advanced search with And - Or combinations on separate fields
		String queryOperator = " " + SolrAndOr.OR + " ";
		String myQueryString;

		// If incoming query string is blank, default to solar *:* for the full query
		if (StringUtils.isNotBlank(query.getQuery())) {
			StringBuilder queryData = new StringBuilder();

			Field fields[] = SolrComponentModel.class.getDeclaredFields();
			for (Field field : fields) {
				org.apache.solr.client.solrj.beans.Field fieldAnnotation = field.getAnnotation(org.apache.solr.client.solrj.beans.Field.class);
				if (fieldAnnotation != null && field.getType() == String.class) {
					String name = field.getName();
					if (StringUtils.isNotBlank(fieldAnnotation.value())
							&& org.apache.solr.client.solrj.beans.Field.DEFAULT.equals(fieldAnnotation.value()) == false) {
						name = fieldAnnotation.value();
					}

					queryData.append(SolrEquals.EQUAL.getSolrOperator())
							.append(name)
							.append(SolrManager.SOLR_QUERY_SEPERATOR)
							.append(query.getQuery())
							.append(queryOperator);
				}
			}
			myQueryString = queryData.toString();
			if (myQueryString.endsWith(queryOperator)) {
				queryData.delete((myQueryString.length() - (queryOperator.length())), myQueryString.length());
				myQueryString = queryData.toString();
			}
		} else {
			myQueryString = SolrManager.SOLR_ALL_QUERY;
		}
		log.log(Level.FINER, myQueryString);

		// execute the searchComponent method and bring back from solr a list array
		List<SolrComponentModel> resultsList = new ArrayList<>();
		try {
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery(myQueryString);

			// fields to be returned back from solr
			solrQuery.setFields(SolrComponentModel.ID_FIELD, SolrComponentModel.ISCOMPONENT_FIELD);
			solrQuery.setStart(filter.getOffset());
			solrQuery.setRows(filter.getMax());
			solrQuery.setIncludeScore(true);

			QueryResponse response = SolrManager.getServer().query(solrQuery);
			SolrDocumentList results = response.getResults();
			DocumentObjectBinder binder = new DocumentObjectBinder();
			resultsList = binder.getBeans(SolrComponentModel.class, results);
		} catch (SolrServerException ex) {
			throw new OpenStorefrontRuntimeException("Search Failed", "Contact System Admin.  Seach server maybe Unavailable", ex);
		} catch (Exception ex) {
			log.log(Level.WARNING, "Solr query failed unexpectly; likely bad input.", ex);
		}

		//Pulling the full object on the return
		List<ComponentSearchView> views = new ArrayList<>();

		List<String> componentIds = new ArrayList<>();
		for (SolrComponentModel result : resultsList) {
			if (result.getIsComponent()) {
				componentIds.add(result.getId());
			}
		}

		//remove bad indexes, if any
		List<ComponentSearchView> componentSearchViews = getComponentService().getSearchComponentList(componentIds);
		Set<String> goodComponentIdSet = new HashSet<>();
		for (ComponentSearchView view : componentSearchViews) {
			goodComponentIdSet.add(view.getComponentId());
		}

		for (String componentId : componentIds) {
			if (goodComponentIdSet.contains(componentId) == false) {
				log.log(Level.FINE, "Removing bad index: " + componentId);
				deleteById(componentId);
			}
		}
		views.addAll(componentSearchViews);

		List<ComponentSearchView> articleViews = getAttributeService().getArticlesSearchView();
		Map<String, ComponentSearchView> allViews = new HashMap<>();
		for (ComponentSearchView componentSearchView : articleViews) {
			AttributeCodePk attributeCodePk = new AttributeCodePk();
			attributeCodePk.setAttributeType(componentSearchView.getArticleAttributeType());
			attributeCodePk.setAttributeCode(componentSearchView.getArticleAttributeCode());
			allViews.put(attributeCodePk.toKey(), componentSearchView);
		}
		for (SolrComponentModel result : resultsList) {

			if (result.getIsComponent() == false) {
				ComponentSearchView view = allViews.get(result.getId());
				if (view != null) {
					views.add(view);
				} else {
					log.log(Level.FINE, "Removing bad index: " + result.getId());
					deleteById(result.getId());
				}
			}
		}

		//TODO: Get the score and sort by score
		return views;
	}

	@Override
	public void addIndex(Component component)
	{
		// initialize solr server
		SolrServer solrService = SolrManager.getServer();

		//add document using the example schema
		SolrComponentModel solrDocModel = new SolrComponentModel();

		solrDocModel.setIsComponent(Boolean.TRUE);
		solrDocModel.setId(component.getComponentId());
		solrDocModel.setNameString(component.getName());
		solrDocModel.setName(component.getName());
		String description = StringProcessor.stripHtml(component.getDescription());
		solrDocModel.setDescription(description.replace("<>", "").replace("\n", ""));
		solrDocModel.setUpdateDts(component.getUpdateDts());
		solrDocModel.setOrganization(component.getOrganization());

		List<ComponentTag> tags = getComponentService().getBaseComponent(ComponentTag.class, component.getComponentId());
		List<ComponentAttribute> attributes = getComponentService().getBaseComponent(ComponentAttribute.class, component.getComponentId());

		String tagList = "";
		String attributeList = "";

		for (ComponentTag tag : tags) {
			tagList = tagList + tag.getText() + ",";
		}
		for (ComponentAttribute attribute : attributes) {
			ComponentAttributePk pk = attribute.getComponentAttributePk();
			AttributeCodePk codePk = new AttributeCodePk();
			codePk.setAttributeCode(pk.getAttributeCode());
			codePk.setAttributeType(pk.getAttributeType());
			AttributeCode code = this.getPersistenceService().findById(AttributeCode.class, codePk);
			AttributeType type = this.getPersistenceService().findById(AttributeType.class, pk.getAttributeType());
			attributeList = attributeList + pk.getAttributeCode() + ",";
			attributeList = attributeList + pk.getAttributeType() + ",";

			if (code != null && type != null) {
				attributeList = attributeList + code.getLabel() + ",";
				if (!code.getDescription().equals("")) {
					attributeList = attributeList + code.getDescription() + ",";
				}
				if (!type.getDescription().equals("")) {
					attributeList = attributeList + type.getDescription() + ",";
				}
			}
		}

		solrDocModel.setTags(tagList);
		solrDocModel.setAttributes(attributeList);
		solrDocModel.setArticleHtml("");

		try {
			solrService.addBean(solrDocModel);
			solrService.commit();
		} catch (IOException | SolrServerException ex) {
			throw new OpenStorefrontRuntimeException("Failed Adding Component", ex);
		}
	}

	@Override
	public void addIndex(ArticleView article)
	{
		Objects.requireNonNull(article.getHtml(), "Html content is required for an article");

		// initialize solr server
		SolrServer solrService = SolrManager.getServer();

		//add document using the example schema
		SolrComponentModel solrDocModel = new SolrComponentModel();
		AttributeCodePk pk = new AttributeCodePk();
		pk.setAttributeCode(article.getAttributeCode());
		pk.setAttributeType(article.getAttributeType());
		AttributeCode code = this.getPersistenceService().findById(AttributeCode.class, pk);
		AttributeType type = this.getPersistenceService().findById(AttributeType.class, article.getAttributeType());

		solrDocModel.setIsComponent(Boolean.FALSE);

		solrDocModel.setId(pk.toKey());
		solrDocModel.setNameString(article.getTitle());
		solrDocModel.setName(article.getTitle());
		solrDocModel.setDescription(article.getDescription());
		solrDocModel.setUpdateDts(article.getUpdateDts());

		String attributeList = type.getAttributeType() + "," + type.getDescription() + "," + code.getLabel() + "," + code.getDescription();
		solrDocModel.setTags("");
		solrDocModel.setAttributes(attributeList);

		String htmlArticle = article.getHtml();
		String plainText = StringProcessor.stripHtml(htmlArticle);

		solrDocModel.setArticleHtml(plainText.replace("<>", "").replace("\n", ""));

		try {
			solrService.addBean(solrDocModel);
			solrService.commit();
		} catch (IOException | SolrServerException ex) {
			throw new OpenStorefrontRuntimeException("Failed Adding Article", ex);
		}
	}

	@Override
	public List<ComponentSearchView> architectureSearch(AttributeCodePk pk, FilterQueryParams filter)
	{
		List<ArticleView> articles = this.getAttributeService().getArticlesForCodeLike(pk);
		Map<String, Component> componentMap = new HashMap<>();
		List<Component> components = new ArrayList<>();

		AttributeCode attributeCode = persistenceService.findById(AttributeCode.class, pk);
		if (attributeCode == null) {
			AttributeCode attributeCodeExample = new AttributeCode();
			AttributeCodePk attributeCodePkExample = new AttributeCodePk();
			attributeCodePkExample.setAttributeType(pk.getAttributeType());
			attributeCodeExample.setAttributeCodePk(attributeCodePkExample);
			attributeCodeExample.setArchitectureCode(pk.getAttributeCode());

			attributeCode = persistenceService.queryOneByExample(AttributeCode.class, attributeCodeExample);
		}

		AttributeCode attributeExample = new AttributeCode();
		AttributeCodePk attributePkExample = new AttributeCodePk();
		attributePkExample.setAttributeType(pk.getAttributeType());
		attributeExample.setAttributeCodePk(attributePkExample);

		AttributeCode attributeCodeLikeExample = new AttributeCode();
		if (attributeCode != null && StringUtils.isNotBlank(attributeCode.getArchitectureCode())) {
			attributeCodeLikeExample.setArchitectureCode(attributeCode.getArchitectureCode() + "%");
		} else {
			AttributeCodePk attributePkLikeExample = new AttributeCodePk();
			attributePkLikeExample.setAttributeCode(pk.getAttributeCode() + "%");
			attributeCodeLikeExample.setAttributeCodePk(attributePkLikeExample);
		}

		QueryByExample queryByExample = new QueryByExample(attributeExample);
		queryByExample.setLikeExample(attributeCodeLikeExample);

		List<AttributeCode> attributeCodes = persistenceService.queryByExample(AttributeCode.class, queryByExample);
		List<String> ids = new ArrayList();
		attributeCodes.forEach(code -> {
			ids.add("'" + code.getAttributeCodePk().getAttributeCode() + "'");
		});

		String componentAttributeQuery = "select from " + ComponentAttribute.class.getSimpleName() + " where componentAttributePk.attributeType = :attributeType and componentAttributePk.attributeCode in [";
		componentAttributeQuery = componentAttributeQuery + StringUtils.join(ids, ",") + "]";

		Map<String, Object> params = new HashMap<>();
		params.put("attributeType", pk.getAttributeType());
		List<ComponentAttribute> componentAttributes = persistenceService.query(componentAttributeQuery, params);

		for (ComponentAttribute componentAttribute : componentAttributes) {
			Component temp = persistenceService.findById(Component.class, componentAttribute.getComponentAttributePk().getComponentId());
			if (OpenStorefrontConstant.ComponentApprovalStatus.APPROVED.equals(temp.getApprovalState())) {
				componentMap.put(temp.getComponentId(), temp);
			}
		}

		// eliminate duplicate componentID on search results
		components.addAll(componentMap.values());

		List<ComponentSearchView> views = new ArrayList<>();
		for (ArticleView article : articles) {
			views.add(ComponentSearchView.toView(article));
		}
		for (Component component : components) {
			views.add(ComponentSearchView.toView(component));
		}
		return views;
	}

	@Override
	public void deleteById(String id)
	{
		SolrServer solrService = SolrManager.getServer();

		try {
			solrService.deleteById(id);
			solrService.commit();
		} catch (IOException | SolrServerException ex) {
			throw new OpenStorefrontRuntimeException("Failed Deleting Index", "Make sure Search is active and can be reached", ex);
		}
	}

	@Override
	public void deleteAll()
	{
		SolrServer solrService = SolrManager.getServer();
		try {
			// CAUTION: deletes everything!
			solrService.deleteByQuery(SolrManager.SOLR_ALL_QUERY);
		} catch (SolrServerException | IOException ex) {
			throw new OpenStorefrontRuntimeException("Unable to clear all indexes", "Make sure Search is active and can be reached", ex);
		}
	}

	@Override
	public void saveAll()
	{
		Component temp = new Component();
		temp.setActiveStatus(Component.ACTIVE_STATUS);
		List<Component> components = persistenceService.queryByExample(Component.class, new QueryByExample(temp));
		List<ArticleView> articles = getAttributeService().getArticles();

		components.stream().forEach((component) -> {
			addIndex(component);
		});
		articles.stream().forEach((article) -> {
			addIndex(article);
		});
	}

	@Override
	public void indexArticlesAndComponents(List<ArticleView> articles, List<Component> components)
	{
		components.stream().forEach((component) -> {
			addIndex(component);
		});
		articles.stream().forEach((article) -> {
			addIndex(article);
		});
	}

	@Override
	public void resetIndexer()
	{
		deleteAll();
		saveAll();
	}
}
