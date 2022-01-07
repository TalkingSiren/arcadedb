/*
 * Copyright © 2021-present Arcade Data Ltd (info@arcadedata.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/* Generated By:JJTree: Do not edit this line. OCreateClassStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_USERTYPE_VISIBILITY_PUBLIC=true */
package com.arcadedb.query.sql.parser;

import com.arcadedb.engine.Bucket;
import com.arcadedb.schema.DocumentType;
import com.arcadedb.schema.Schema;
import com.arcadedb.schema.VertexType;

import java.util.*;

public class CreateVertexTypeStatement extends CreateTypeAbstractStatement {
  public CreateVertexTypeStatement(final int id) {
    super(id);
  }

  public CreateVertexTypeStatement(final SqlParser p, final int id) {
    super(p, id);
  }

  @Override
  protected String commandType() {
    return "create vertex type";
  }

  @Override
  protected DocumentType createType(Schema schema) {
    final VertexType type;
    if (totalBucketNo != null)
      type = schema.createVertexType(name.getStringValue(), totalBucketNo.getValue().intValue());
    else {
      if (buckets == null || buckets.isEmpty())
        type = schema.createVertexType(name.getStringValue());
      else {
        // CHECK THE BUCKETS FIRST
        final List<Bucket> bucketInstances = new ArrayList<>();
        for (BucketIdentifier b : buckets)
          bucketInstances.add(b.bucketName != null ? schema.getBucketByName(b.bucketName.getStringValue()) : schema.getBucketById(b.bucketId.value.intValue()));

        type = schema.createVertexType(name.getStringValue(), bucketInstances);
      }
    }
    return type;
  }

  @Override
  public CreateVertexTypeStatement copy() {
    return (CreateVertexTypeStatement) super.copy(new CreateVertexTypeStatement(-1));
  }
}
/* JavaCC - OriginalChecksum=4043013624f55fdf0ea8fee6d4f211b0 (do not edit this line) */
