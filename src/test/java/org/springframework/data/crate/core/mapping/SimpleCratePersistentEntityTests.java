/*
 * Copyright 2002-2014 the original author or authors.
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

package org.springframework.data.crate.core.mapping;

import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.junit.Test;
import org.springframework.data.crate.core.mapping.annotations.Table;
import org.springframework.data.sample.entities.Book;
import org.springframework.data.sample.entities.PropertiesContainer;
import org.springframework.data.sample.entities.SampleEntity;

/**
 * 
 * @author Hasnain Javed
 *
 */
public class SimpleCratePersistentEntityTests {
	
	@Test
	public void shouldGetTableNameFromClass() {
		
		CrateMappingContext mappingContext = prepareMappingContext(SampleEntity.class);
		
		String tableName = mappingContext.getPersistentEntity(SampleEntity.class).getTableName();
		
		assertThat(tableName, is(notNullValue()));
		assertThat(tableName, is(SampleEntity.class.getSimpleName().toUpperCase()));
	}
	
	@Test
	public void shouldGetTableNameFromAnnotation() {
		
		CrateMappingContext mappingContext = prepareMappingContext(Book.class);
		
		String tableName = mappingContext.getPersistentEntity(Book.class).getTableName();
		
		assertThat(tableName, is(notNullValue()));
		assertThat(tableName, is(Book.class.getAnnotation(Table.class).name()));
	}
	
	@Test
	public void shouldFilterPrimitiveFields() {
		
		Set<CratePersistentProperty> simpleProperties = prepareMappingContext(PropertiesContainer.class).
														getPersistentEntity(PropertiesContainer.class).
													    getPrimitiveProperties();
		assertThat(simpleProperties, is(notNullValue()));
		assertThat(simpleProperties.isEmpty(), is(false));
		assertThat(simpleProperties.size(), is(1));
		
		CratePersistentProperty simpleProperty = simpleProperties.iterator().next();
		assertThat(simpleProperty.isEntity(), is(false));
		assertThat(simpleProperty.isArray(), is(false));
		assertThat(simpleProperty.isCollectionLike(), is(false));
		assertThat(simpleProperty.isMap(), is(false));
		assertThat(simpleProperty.getActualType().getName(), is(String.class.getName()));
	}
	
	@Test
	public void shouldFilterEntityFields() {
		
		Set<CratePersistentProperty> compoisteProperties = prepareMappingContext(PropertiesContainer.class).
														   getPersistentEntity(PropertiesContainer.class).
														   getEntityProperties();
		assertThat(compoisteProperties, is(notNullValue()));
		assertThat(compoisteProperties.isEmpty(), is(false));
		assertThat(compoisteProperties.size(), is(1));
		
		CratePersistentProperty compositeProperty = compoisteProperties.iterator().next();
		assertThat(compositeProperty.isEntity(), is(true));
		assertThat(compositeProperty.isArray(), is(false));
		assertThat(compositeProperty.isCollectionLike(), is(false));
		assertThat(compositeProperty.isMap(), is(false));
		assertThat(compositeProperty.getActualType().getName(), is(Book.class.getName()));
	}
	
	@Test
	public void shouldFilterArrayFields() {
		
		Set<CratePersistentProperty> arrayProperties = prepareMappingContext(PropertiesContainer.class).
													   getPersistentEntity(PropertiesContainer.class).
													   getArrayProperties();
		assertThat(arrayProperties, is(notNullValue()));
		assertThat(arrayProperties.isEmpty(), is(false));
		assertThat(arrayProperties.size(), is(1));
		
		CratePersistentProperty arrayProperty = arrayProperties.iterator().next();
		assertThat(arrayProperty.isArray(), is(true));
		assertThat(arrayProperty.getRawType().isArray(), is(true));
	}
	
	@Test
	public void shouldFilterCollectionFields() {
		
		Set<CratePersistentProperty> collectionProperties = prepareMappingContext(PropertiesContainer.class).
														    getPersistentEntity(PropertiesContainer.class).
														    getCollectionProperties();
		assertThat(collectionProperties, is(notNullValue()));
		assertThat(collectionProperties.isEmpty(), is(false));
		assertThat(collectionProperties.size(), is(2));
		
		for(CratePersistentProperty collectionProperty : collectionProperties) {
			assertThat(collectionProperty.isArray(), is(false));
			assertThat(collectionProperty.isMap(), is(false));
			assertThat(collectionProperty.isCollectionLike(), is(true));
		}
	}
	
	@Test
	public void shouldFilterMapFields() {
		
		Set<CratePersistentProperty> mapProperties = prepareMappingContext(PropertiesContainer.class).
												     getPersistentEntity(PropertiesContainer.class).
												     getMapProperties();
		assertThat(mapProperties, is(notNullValue()));
		assertThat(mapProperties.isEmpty(), is(false));
		assertThat(mapProperties.size(), is(1));
		
		CratePersistentProperty mapProperty = mapProperties.iterator().next();
		assertThat(mapProperty.isArray(), is(false));
		assertThat(mapProperty.isCollectionLike(), is(false));
		assertThat(mapProperty.isMap(), is(true));
	}
	
	private static CrateMappingContext prepareMappingContext(Class<?> type) {
		
		CrateMappingContext mappingContext = new CrateMappingContext();
		mappingContext.setInitialEntitySet(singleton(type));
		mappingContext.initialize();
		
		return mappingContext;
	}
}