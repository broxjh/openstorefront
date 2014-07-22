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

package edu.usu.sdl.openstorefront.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.usu.sdl.openstorefront.model.jpa.BaseEntity;
import edu.usu.sdl.openstorefront.util.ServiceUtil;
import edu.usu.sdl.openstorefront.util.TimeUtil;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentAttribute;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentContact;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentDetail;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentEvaluation;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentEvaluationSchedule;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentEvaluationSection;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentExternalDependancy;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentMedia;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentMetadata;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentQuestion;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentQuestionResponse;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentRelationship;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentResource;
import edu.usu.sdl.openstorefront.web.rest.model.ComponentReview;
import edu.usu.sdl.openstorefront.web.tool.OldAsset;
import edu.usu.sdl.openstorefront.web.tool.OldDataWrapper;
import java.io.File;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

/**
 *
 * @author dshurtleff
 */
public class DataConvertTest
{
	@Test
	public void testConvert() throws JsonProcessingException, IOException
	{
		
		ObjectMapper objectMapper = ServiceUtil.defaultObjectMapper();		
		OldDataWrapper oldDataWrapper = objectMapper.readValue(new File("C:\\development\\storefront\\source\\old\\old_data\\new-asset-data-all.json"), new TypeReference<OldDataWrapper>() {});
		
		List<OldAsset> assets = oldDataWrapper.getData();
		 
		List<ComponentDetail> newAssetMin = processData(assets, false);
		List<ComponentDetail> newAssetComplete =  processData(assets, true);
		
		System.out.println(objectMapper.writeValueAsString(newAssetMin));		
		objectMapper.writeValue(new File("c:/development/storefront/data/components-min.json"), newAssetMin);
		objectMapper.writeValue(new File("c:/development/storefront/data/components-full.json"), newAssetComplete);
	}
	
	private List<ComponentDetail> processData(List<OldAsset> assets, boolean generateData)
	{
		
		
		Set<String> metaTypeToSkip = new HashSet<>();
		metaTypeToSkip.add("Government Point of Contact Email Address");
		metaTypeToSkip.add("Government Point of Contact Name");
		metaTypeToSkip.add("Government Point of Contact Phone");
		metaTypeToSkip.add("Code Location URL");
		metaTypeToSkip.add("Product Homepage");
		metaTypeToSkip.add("DI2E Framework Evaluation Report URL");
		
		
		//Dummy data
		List<String> peopleNames = Arrays.asList("Cathy TEST", "Jack TEST", "Colby TEST", "Abby TEST", "Dawson TEST");
		List<String> userTypes = Arrays.asList("End User", "Developer", "Project Manager");
		
		//questions
		Map<String, List<String>> questionMap = new HashMap<>();
		questionMap.put("Which version supports y-docs 1.5+? (SAMPLE)", Arrays.asList("Version 3.1 added support.(SAMPLE)",  "They depreciated support in version 4.0 since it was a rarely used feature.(SAMPLE)"));
		questionMap.put("Which os platforms does this support? (SAMPLE)", Arrays.asList("CentOS 6.5 and MorphOS(SAMPLE)",  "'I think they also have Windows  and ReactOS support. (SAMPLE)"));
		questionMap.put("Does this support the 2.0 specs? (SAMPLE)", Arrays.asList("No,  they planned to add support next Version(SAMPLE)",  "Update: they backport support to version 1.6(SAMPLE)"));
		questionMap.put("Are samples included? (SAMPLE)", Arrays.asList("They are included in a separate download.(SAMPLE)"));
		questionMap.put("Are there alternate licenses? (SAMPLE)", Arrays.asList("You can ask their support team for a custom commerical license that should cover you needs.(SAMPLE)",  "We've try to get an alternate license and we've been waiting for over 6 months for thier legal department.(SAMPLE)"));
		
		List<Date> questionDates = Arrays.asList(TimeUtil.fromString("2014-1-03T10:15:30.00Z"), 
									TimeUtil.fromString("2012-2-04T11:15:30.00Z"), 
									TimeUtil.fromString("2013-10-13T12:15:30.00Z"), 
									TimeUtil.fromString("2013-3-03T1:15:30.00Z"), 
									TimeUtil.fromString("2013-4-30T2:15:30.00Z"));
		
		List<Date> answerDates = Arrays.asList(TimeUtil.fromString("2014-2-03T10:15:30.00Z"), 
									TimeUtil.fromString("2014-2-04T11:15:30.00Z"), 
									TimeUtil.fromString("2014-5-13T12:15:30.00Z"), 
									TimeUtil.fromString("2014-3-03T1:15:30.00Z"), 
									TimeUtil.fromString("2014-4-30T2:15:30.00Z"));		
		
		Map<String, String> metadataMap = new HashMap<>();
		metadataMap.put("Extra Metadata (SAMPLE)","Unfiltered");
		metadataMap.put("Support Common Interface 2.1 (SAMPLE)","No");
		metadataMap.put("Provides Web Hooks via RPC service(SAMPLE)","Yes");
		metadataMap.put("Common Uses (SAMPLE)","UDOP, Information Sharing, Research");
		metadataMap.put("Available to public (SAMPLE)","YES");
		
		Map<String, String> attriubuteMap = new HashMap<>();
		attriubuteMap.put("License Restrictions (SAMPLE)", "Per CPU");		
		attriubuteMap.put("Funded (SAMPLE)", "Yes");
		attriubuteMap.put("Network(s) Fielded (SAMPLE)", "NIPR, SIPR");
		attriubuteMap.put("Accreditation Status (SAMPLE)", "ATO");
		attriubuteMap.put("License Type (SAMPLE)", "FOSS");
		attriubuteMap.put("Protection/ Impact Level (SAMPLE)", "DoD MAC Level");
		attriubuteMap.put("Lifecycle Stage (SAMPLE)", "Development");
		
		Map<String, String> reviewMap = new HashMap<>();
		reviewMap.put("Good Product (SAMPLE)", "It's a good product.  The features are nice and performed well.  It quite configurable without a lot of setup and it worked out of the box.");
		reviewMap.put("Just what I was looking for (SAMPLE)", "This was perfect it solved our issues and provided tools for things we didn't even anticipate.");
		reviewMap.put("Great but missing features (SAMPLE)", "This is a great product however, it's missing what I think are critical features.  Hopefully, they are working on it for future updates.");
		reviewMap.put("Confused (SAMPLE)", "This wasn't qutie what I thought it was.");
		reviewMap.put("Hassle (SAMPLE)", "I had issues trying to obtain the component and once I got it is very to difficult to install.");
		
		
		List<String> pros = new ArrayList<>();
		pros.add("Well tested");
		pros.add("Active Development");		
		pros.add("Reliable");
		pros.add("Open Source");
		pros.add("Compact");
		pros.add("Well Tested");
		
		List<String> cons = new ArrayList<>();
		cons.add("Poorly Tested");
		cons.add("Security Concerns");		
		cons.add("Difficult Installation");
		cons.add("Poor Documentation");
		cons.add("Bulky");
		cons.add("Legacy system");	
		
		List<String> organizations = new ArrayList<>();
		organizations.add("NGA");
		organizations.add("NSA");
		organizations.add("DCGS Army");
		organizations.add("DCGS Navy");
		organizations.add("Private Company");
		
		List<String> usedTimeCode = new ArrayList<>();
		usedTimeCode.add("< 1 month'");
		usedTimeCode.add("< 3 Months");
		usedTimeCode.add("< 6 Months");
		usedTimeCode.add("< 1 year");
		usedTimeCode.add("< 3 years");
		usedTimeCode.add("> 3 years");
		
		Map<String, String> externalDependancyMap = new HashMap<>();
		externalDependancyMap.put("Ruby", "Version 2.0+");
		externalDependancyMap.put("Erlang", "Version 3.0");
		externalDependancyMap.put("Linux", "Tested on CentOS 5 and Ubuntu Server 11.0");
		externalDependancyMap.put("Windows", "Version 8.1");
		externalDependancyMap.put("Tomcat", "Version 7 or 8");
		
		
		List<String> evalSections = new ArrayList<>();
		evalSections.add("Discoverable");
		evalSections.add("Accessible");
		evalSections.add("Documentation");
		evalSections.add("Deployable");
		evalSections.add("Usable");
		evalSections.add("Error Handling");
		evalSections.add("Integrable");
		evalSections.add("I/O Validation");
		evalSections.add("Testing");
		evalSections.add("Monitoring");
		evalSections.add("Performance");
		evalSections.add("Scalability");
		evalSections.add("Security");
		evalSections.add("Maintainability");
		evalSections.add("Community");		
		evalSections.add("Change Management");	
		evalSections.add("CA");	
		evalSections.add("Licensing");	
		evalSections.add("Roadmap");	
		evalSections.add("Willingness");		
		evalSections.add("Architecture Alignment");			
		
	
		
		List<String> evalLevelStatus = new ArrayList<>();		
		evalLevelStatus.add("P");		
		evalLevelStatus.add("H");
		
	
		SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy");
		
		List<ComponentDetail> newAssets = new ArrayList<>();		
		assets.forEach(oldAsset -> {
			
			ComponentDetail componentDetail = new ComponentDetail();
			//defaults
			componentDetail.setActiveStatus(BaseEntity.ACTIVE_STATUS);
			
			//map form old			
			componentDetail.setComponentId(oldAsset.getId());
			componentDetail.setGuid(oldAsset.getUuid());
			componentDetail.setUpdateDts(oldAsset.getEditedDate());
			componentDetail.setDescription(oldAsset.getDescription());
			componentDetail.setApprovedDate(oldAsset.getApprovalDate());
			componentDetail.setCreateDts(oldAsset.getCreateDate());
			componentDetail.setVersion(oldAsset.getVersionName());
			
			if ("Approved".equalsIgnoreCase(oldAsset.getApprovalStatus()))
			{
				componentDetail.setApprovalState("A");
			}
			else
			{
				componentDetail.setApprovalState("P");
			}
			
			componentDetail.setCreateUser(oldAsset.getCreatedBy().getUsername());
			componentDetail.setUpdateUser(oldAsset.getEditedBy().getUsername());
			componentDetail.setOrganization(oldAsset.getOrganization());
			componentDetail.setName(oldAsset.getTitle());
			if (StringUtils.isNotBlank(oldAsset.getReleaseDate()))
			{
				componentDetail.setReleaseDate(sdfDate.parse(oldAsset.getReleaseDate(), new ParsePosition(0)));			
			}
			componentDetail.setLastActivityDate(oldAsset.getLastActivity().getActivitiyDate());
						
			oldAsset.getTechPocs().forEach(poc ->{
				//Need to generate 				
				Collections.shuffle(peopleNames);
				ComponentContact contact = new ComponentContact();
				contact.setPostionDescription("Technical POC");
				contact.setName(peopleNames.get(0));
				contact.setEmail("sample_email@test.com");
				contact.setPhone("555-555-5555");
				componentDetail.getContacts().add(contact);
			});
			
			//Government Contact
			oldAsset.getCustomFields().forEach(field ->{
				if ("Government Point of Contact Name".equalsIgnoreCase(field.getName()))
				{
					Collections.shuffle(peopleNames);
					ComponentContact contact = new ComponentContact();
					contact.setPostionDescription("Government POC");
					contact.setName(peopleNames.get(0));
					contact.setEmail("sample_email@test.com");
					contact.setPhone("555-555-5555");
					componentDetail.getContacts().add(contact);							
				}
			});
			
			
			//media
			oldAsset.getScreenShots().forEach(screen -> {				
				componentDetail.getComponentMedia().add(createMediaFromUrl(screen.getLargeImageUrl()));			
			});
			if (StringUtils.isNotBlank(oldAsset.getImageLargeUrl()))
			{
				componentDetail.getComponentMedia().add(createMediaFromUrl(oldAsset.getImageLargeUrl()));
			}
		
			//resources
			oldAsset.getDocUrls().forEach(doc -> {
				ComponentResource componentResource = new ComponentResource();
				componentResource.setName(doc.getName());
				componentResource.setLink(doc.getUrl());
				componentResource.setType("Document");
				componentDetail.getResources().add(componentResource);
			});
			
			oldAsset.getCustomFields().forEach(field ->{
				if ("Code Location URL".equals(field.getName())){
					ComponentResource componentResource = new ComponentResource();
					componentResource.setName(field.getName());
					componentResource.setLink(field.getValue());
					componentResource.setType("Code");
					componentDetail.getResources().add(componentResource);					
				} else 	if ("Product Homepage".equals(field.getName())){
					ComponentResource componentResource = new ComponentResource();
					componentResource.setName(field.getName());
					componentResource.setLink(field.getValue());
					componentResource.setType("Homepage");
					componentDetail.getResources().add(componentResource);						
				} else 	if ("DI2E Framework Evaluation Report URL".equals(field.getName())){
					ComponentResource componentResource = new ComponentResource();
					componentResource.setName(field.getName());
					componentResource.setLink(field.getValue());
					componentResource.setType("DI2E Framework Evaluation Report URL");
					componentDetail.getResources().add(componentResource);						
				}
			});
			
			//metadata/attributes
			ComponentAttribute attribute = new ComponentAttribute();
			attribute.setImportant(true);
			attribute.setTypeDescription("Type");
			attribute.setCodeDescription(oldAsset.getTypes().getTitle());
			componentDetail.getAttributes().add(attribute);
			
			attribute = new ComponentAttribute();
			attribute.setImportant(true);
			attribute.setTypeDescription("Evaluation Level");
			attribute.setCodeDescription(oldAsset.getState().getTitle());
			componentDetail.getAttributes().add(attribute);
			
			String level[] = oldAsset.getState().getTitle().split("-");
			componentDetail.getEvaluation().setCurrentLevelCode(level[0].trim().toUpperCase());			
			
			oldAsset.getCategories().forEach(category -> {
				ComponentAttribute catAttribute = new ComponentAttribute();				
				catAttribute.setTypeDescription("Category");
				catAttribute.setCodeDescription(category.getTitle());
				componentDetail.getAttributes().add(catAttribute);
			});
						
			
			oldAsset.getCustomFields().forEach(field ->{
				if (StringUtils.isNotBlank(field.getValue()) &&
					!metaTypeToSkip.contains(field.getName()))
				{
					ComponentAttribute metaAttribute = new ComponentAttribute();					
					metaAttribute.setTypeDescription(field.getName());
					metaAttribute.setCodeDescription(field.getValue());
					componentDetail.getAttributes().add(metaAttribute);		
				}			
			});
			
			
			if (generateData)	
			{
				//filling some details at random with dummy data
				Random random = new Random(System.currentTimeMillis());
								
				
				//component relationships
				if (random.nextInt(10) < 4)
				{
					if (newAssets.size() > 2)
					{
						if (random.nextInt(10) < 3)
						{
							ComponentDetail component = newAssets.get(random.nextInt(newAssets.size()));
							ComponentRelationship relationship = new ComponentRelationship();
							relationship.setComponentId(component.getComponentId());
							relationship.setName(component.getName());							
							componentDetail.setParentComponent(relationship);							
						} else 	if (random.nextInt(10) < 3) {
							ComponentDetail component = newAssets.get(random.nextInt(newAssets.size()));
							ComponentRelationship relationship = new ComponentRelationship();
							relationship.setComponentId(component.getComponentId());
							relationship.setName(component.getName());							
							componentDetail.getSubComponents().add(relationship);
						} else	if (random.nextInt(10) < 3)
						{
							ComponentDetail component = newAssets.get(random.nextInt(newAssets.size()));
							ComponentRelationship relationship = new ComponentRelationship();
							relationship.setComponentId(component.getComponentId());
							relationship.setName(component.getName());							
							componentDetail.getRelatedComponents().add(relationship);
						}	
					}
				}
				
				
				//questions and responses
				if (random.nextInt(10) < 5)
				{
					List<String> keys = new ArrayList<>();
					keys.addAll(questionMap.keySet());
					Collections.shuffle(keys);
					int maxQuestions = random.nextInt(4);
					for (int i=0; i<maxQuestions; i++)
					{
						String question = keys.remove(0);
						List<String> responses = questionMap.get(question);
						ComponentQuestion componentQuestion = new ComponentQuestion();
						componentQuestion.setQuestion(question);
						
						Collections.shuffle(peopleNames);
						componentQuestion.setUsername(peopleNames.get(0));
						
						Collections.shuffle(userTypes);
						componentQuestion.setUserType(userTypes.get(0));
						
						Collections.shuffle(questionDates);
						componentQuestion.setCreateDts(questionDates.get(0));
						componentQuestion.setUpdateDts(questionDates.get(0));
												
						for (String reponse : responses)
						{
							ComponentQuestionResponse questionResponse = new ComponentQuestionResponse();
							Collections.shuffle(peopleNames);
							questionResponse.setUsername(peopleNames.get(0));

							Collections.shuffle(userTypes);
							questionResponse.setUserType(userTypes.get(0));	
							
							Collections.shuffle(answerDates);
							questionResponse.setAnsweredDate(answerDates.get(0));									
														
							questionResponse.setResponse(reponse);	
							componentQuestion.getResponses().add(questionResponse);
						}
						
						componentDetail.getQuestions().add(componentQuestion);
					}
				}
				
				//attributes (see the confluence)
				if (random.nextInt(10) < 8)
				{
					List<String> keys = new ArrayList<>();
					keys.addAll(attriubuteMap.keySet());
					Collections.shuffle(keys);
					int maxAttr = random.nextInt(4);
					for (int i=0; i<maxAttr; i++)
					{					
						String  typeDescription = keys.remove(0);
						String  codeDescription =  attriubuteMap.get(typeDescription);
						
						ComponentAttribute metaAttribute = new ComponentAttribute();					
						metaAttribute.setTypeDescription(typeDescription);
						metaAttribute.setCodeDescription(codeDescription);
						componentDetail.getAttributes().add(metaAttribute);						
					}
				}				
				
				//more metadata  
				if (random.nextInt(10) < 2)
				{
					List<String> keys = new ArrayList<>();
					keys.addAll(metadataMap.keySet());
					Collections.shuffle(keys);
					int maxAttr = random.nextInt(4);
					for (int i=0; i<maxAttr; i++)
					{					
						String  label = keys.remove(0);
						String value = metadataMap.get(label);
						ComponentMetadata metadata = new ComponentMetadata();
						metadata.setLabel(label);
						metadata.setValue(value);
						componentDetail.getMetadata().add(metadata);
					}
				}
				
				//reviews
				if (random.nextInt(10) < 4)
				{
					List<String> keys = new ArrayList<>();
					keys.addAll(reviewMap.keySet());
					Collections.shuffle(keys);
					int maxreviews = random.nextInt(3);
					for (int i=0; i<maxreviews; i++)
					{					
						String  title = keys.remove(0);
						String comment = reviewMap.get(title);
						
						ComponentReview componentReview = new ComponentReview();
						componentReview.setTitle(title);
						componentReview.setComment(comment);
						
						List<String> tempPros = new ArrayList<>(pros);
						Collections.shuffle(tempPros);
						int max = random.nextInt(3);
						for (int j=0; j<max; j++)
						{
							componentReview.getPros().add(tempPros.remove(0));
						}
						
						List<String> tempCons = new ArrayList<>(cons);
						Collections.shuffle(tempCons);
						max = random.nextInt(3);
						for (int j=0; j<max; j++)
						{
							componentReview.getCons().add(tempCons.remove(0));
						}	
						Collections.shuffle(peopleNames);
						componentReview.setUsername(peopleNames.get(0));
						
						Collections.shuffle(userTypes);
						componentReview.setUserType(userTypes.get(0));		
						
						Collections.shuffle(questionDates);
						componentReview.setLastUsed(questionDates.get(0));
						
						Collections.shuffle(answerDates);
						componentReview.setUpdateDate(answerDates.get(0));
						
						componentReview.setRating(random.nextInt(5) + 1);
						componentReview.setRecommend(random.nextBoolean());
						
						Collections.shuffle(organizations);
						componentReview.setOrganization(organizations.get(0));
						
						Collections.shuffle(usedTimeCode);
						componentReview.setUsedTimeCode(usedTimeCode.get(0));
												
						componentDetail.getReviews().add(componentReview);
					}
					
				}
				
				//external Dependancies
				if (random.nextInt(10) < 4)
				{
					List<String> keys = new ArrayList<>();
					keys.addAll(externalDependancyMap.keySet());
					Collections.shuffle(keys);
					int max = random.nextInt(2);
					for (int i=0; i<max; i++)
					{					
						String  dependancy = keys.remove(0);
						String comment = externalDependancyMap.get(dependancy);
						
						ComponentExternalDependancy externalDependancy = new ComponentExternalDependancy();
						externalDependancy.setComment(comment);
						externalDependancy.setDependancy(dependancy);
						componentDetail.getDependencies().add(externalDependancy);
					}					
				}
				
				
				//eval data
				ComponentEvaluation evaluation = componentDetail.getEvaluation();

				if (!"NA".equals(evaluation.getCurrentLevelCode()))
				{
					//Fill in the general eval info
					evaluation.setReviewedVersion("1.0");
					evaluation.setStartDate(TimeUtil.fromString("2014-1-03T10:15:30.00Z"));
					evaluation.setEndDate(TimeUtil.fromString("2014-3-01T10:15:30.00Z"));										
					
					if ("LEVEL 0".equals(componentDetail.getEvaluation().getCurrentLevelCode()))
					{						
						//set status to C - complete						
						ComponentEvaluationSchedule componentEvaluationSchedule = new ComponentEvaluationSchedule();
						componentEvaluationSchedule.setLevelStatus("C");
						componentEvaluationSchedule.setEvaluationLevelCode("LEVEL 0");						
						componentEvaluationSchedule.setActualCompeletionDate(TimeUtil.fromString("2014-1-11T10:15:30.00Z"));
						evaluation.getEvaluationSchedule().add(componentEvaluationSchedule);
						
						//set status to C - complete						
						componentEvaluationSchedule = new ComponentEvaluationSchedule();
						componentEvaluationSchedule.setLevelStatus("N");
						componentEvaluationSchedule.setEvaluationLevelCode("LEVEL 1");						
						componentEvaluationSchedule.setEstimatedCompeletionDate(TimeUtil.fromString("2014-2-11T10:15:30.00Z"));
						evaluation.getEvaluationSchedule().add(componentEvaluationSchedule);

						//set status to C - complete						
						componentEvaluationSchedule = new ComponentEvaluationSchedule();
						componentEvaluationSchedule.setLevelStatus("N");
						componentEvaluationSchedule.setEvaluationLevelCode("LEVEL 2");						
						componentEvaluationSchedule.setEstimatedCompeletionDate(TimeUtil.fromString("2014-2-21T10:15:30.00Z"));						
						evaluation.getEvaluationSchedule().add(componentEvaluationSchedule);
						
						//set status to C - complete						
						componentEvaluationSchedule = new ComponentEvaluationSchedule();
						componentEvaluationSchedule.setLevelStatus("N");
						componentEvaluationSchedule.setEvaluationLevelCode("LEVEL 3");						
						componentEvaluationSchedule.setEstimatedCompeletionDate(TimeUtil.fromString("2014-3-01T10:15:30.00Z"));						
						evaluation.getEvaluationSchedule().add(componentEvaluationSchedule);
						
					}
					else if ("LEVEL 1".equals(componentDetail.getEvaluation().getCurrentLevelCode()))
					{
						//set status to C - complete						
						ComponentEvaluationSchedule componentEvaluationSchedule = new ComponentEvaluationSchedule();
						componentEvaluationSchedule.setLevelStatus("C");
						componentEvaluationSchedule.setEvaluationLevelCode("LEVEL 0");						
						componentEvaluationSchedule.setActualCompeletionDate(TimeUtil.fromString("2014-1-11T10:15:30.00Z"));
						evaluation.getEvaluationSchedule().add(componentEvaluationSchedule);
																	
						componentEvaluationSchedule = new ComponentEvaluationSchedule();
						int check = random.nextInt(10);
						if (check < 2)
						{
							componentEvaluationSchedule.setLevelStatus("H");
							componentEvaluationSchedule.setActualCompeletionDate(TimeUtil.fromString("2014-2-11T10:15:30.00Z"));
						}
						else if (check < 5)
						{
							componentEvaluationSchedule.setLevelStatus("P");
							componentEvaluationSchedule.setEstimatedCompeletionDate(TimeUtil.fromString("2014-2-11T10:15:30.00Z"));
						}						
						else
						{
							componentEvaluationSchedule.setLevelStatus("C");
							componentEvaluationSchedule.setActualCompeletionDate(TimeUtil.fromString("2014-2-11T10:15:30.00Z"));
							
							for (String sectionName : evalSections)
							{
								ComponentEvaluationSection section = new ComponentEvaluationSection();
								section.setName(sectionName);
								section.setScore(random.nextInt(5) + 1);								
								if (random.nextInt(10) < 1)
								{
									section.setScore(0);
								}
								evaluation.getEvaulationSections().add(section);
							}
							
						}
						
						componentEvaluationSchedule.setEvaluationLevelCode("LEVEL 1");												
						evaluation.getEvaluationSchedule().add(componentEvaluationSchedule);

						//set status to C - complete						
						componentEvaluationSchedule = new ComponentEvaluationSchedule();
						componentEvaluationSchedule.setLevelStatus("N");
						componentEvaluationSchedule.setEvaluationLevelCode("LEVEL 2");						
						componentEvaluationSchedule.setEstimatedCompeletionDate(TimeUtil.fromString("2014-2-21T10:15:30.00Z"));						
						evaluation.getEvaluationSchedule().add(componentEvaluationSchedule);
						
						//set status to C - complete						
						componentEvaluationSchedule = new ComponentEvaluationSchedule();
						componentEvaluationSchedule.setLevelStatus("N");
						componentEvaluationSchedule.setEvaluationLevelCode("LEVEL 3");						
						componentEvaluationSchedule.setEstimatedCompeletionDate(TimeUtil.fromString("2014-3-01T10:15:30.00Z"));						
						evaluation.getEvaluationSchedule().add(componentEvaluationSchedule);
					}
				}
				
//				else if ("LEVEL 2".equals(componentDetail.getEvaluation().getCurrentLevelCode()))
//				{
//
//				} else if ("LEVEL 3".equals(componentDetail.getEvaluation().getCurrentLevelCode()))
//				{
//							//set lower levels to complete
//
//				}
				
				
				
				
								
			}				

			newAssets.add(componentDetail);
		});	
		return newAssets;
	}
	
	private ComponentMedia createMediaFromUrl(String url)
	{
		ComponentMedia media = new ComponentMedia();
		String baseImagePath = "images/oldsite/";
		String resource = ServiceUtil.getResourceNameFromUrl(url);
		media.setLink(baseImagePath + resource);								
		media.setContentType("image/" +  resource.substring(resource.lastIndexOf(".") + 1, resource.length()));							
		return media;
	}
	
}