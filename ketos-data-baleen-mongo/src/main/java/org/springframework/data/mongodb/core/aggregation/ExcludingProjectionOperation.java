/*
 * Copyright 2013-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.data.mongodb.core.aggregation;

import org.bson.Document;

/**
 * Mongo 3.4 allows you to exclude any field by projects, but the ProjectionOperation asserts
 * haven't caught up.
 */
public class ExcludingProjectionOperation implements AggregationOperation {

  private final String field;


  public ExcludingProjectionOperation(final String field) {
    this.field = field;

  }

  @Override
  public Document toDocument(final AggregationOperationContext context) {
    final Document fieldObject = new Document();

    fieldObject.put(field, 0);


    return new Document("$project", fieldObject);
  }


}
