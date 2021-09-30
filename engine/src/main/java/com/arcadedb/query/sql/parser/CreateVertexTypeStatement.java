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

import com.arcadedb.exception.CommandExecutionException;
import com.arcadedb.query.sql.executor.CommandContext;
import com.arcadedb.query.sql.executor.InternalResultSet;
import com.arcadedb.query.sql.executor.ResultInternal;
import com.arcadedb.query.sql.executor.ResultSet;
import com.arcadedb.schema.Schema;
import com.arcadedb.schema.VertexType;

import java.util.*;
import java.util.stream.*;

public class CreateVertexTypeStatement extends DDLStatement {
  /**
   * Class name
   */
  public Identifier name;

  public boolean ifNotExists;

  /**
   * Direct superclasses for this class
   */
  protected List<Identifier> supertypes;

  /**
   * Cluster IDs for this class
   */
  protected List<PInteger> buckets;

  /**
   * Total number clusters for this class
   */
  protected PInteger totalBucketNo;

  protected boolean abstractType = false;

  public CreateVertexTypeStatement(int id) {
    super(id);
  }

  public CreateVertexTypeStatement(SqlParser p, int id) {
    super(p, id);
  }

  @Override
  public ResultSet executeDDL(CommandContext ctx) {

    Schema schema = ctx.getDatabase().getSchema();
    if (schema.existsType(name.getStringValue())) {
      if (ifNotExists) {
        return new InternalResultSet();
      } else {
        throw new CommandExecutionException("Type " + name + " already exists");
      }
    }
    checkSuperTypes(schema, ctx);

    ResultInternal result = new ResultInternal();
    result.setProperty("operation", "create vertex type");
    result.setProperty("typeName", name.getStringValue());

    VertexType type;
    final VertexType[] superclasses = getSuperTypes(schema);

    if (totalBucketNo != null) {
      type = schema.createVertexType(name.getStringValue(), totalBucketNo.getValue().intValue());
    } else {
      type = schema.createVertexType(name.getStringValue());
    }

    for (VertexType c : superclasses)
      type.addParentType(c);

    return new InternalResultSet(result);
  }

  private VertexType[] getSuperTypes(Schema schema) {
    if (supertypes == null) {
      return new VertexType[] {};
    }
    return supertypes.stream().map(x -> schema.getType(x.getStringValue())).filter(x -> x != null).collect(Collectors.toList()).toArray(new VertexType[] {});
  }

  private void checkSuperTypes(Schema schema, CommandContext ctx) {
    if (supertypes != null) {
      for (Identifier superType : supertypes) {
        if (!schema.existsType(superType.value)) {
          throw new CommandExecutionException("Supertype " + superType + " not found");
        }
      }
    }
  }

  @Override
  public void toString(Map<String, Object> params, StringBuilder builder) {
    builder.append("CREATE VERTEX TYPE ");
    name.toString(params, builder);
    if (ifNotExists) {
      builder.append(" IF NOT EXISTS");
    }
    if (supertypes != null && supertypes.size() > 0) {
      builder.append(" EXTENDS ");
      boolean first = true;
      for (Identifier sup : supertypes) {
        if (!first) {
          builder.append(", ");
        }
        sup.toString(params, builder);
        first = false;
      }
    }
    if (buckets != null && buckets.size() > 0) {
      builder.append(" BUCKET ");
      boolean first = true;
      for (PInteger bucket : buckets) {
        if (!first) {
          builder.append(",");
        }
        bucket.toString(params, builder);
        first = false;
      }
    }
    if (totalBucketNo != null) {
      builder.append(" BUCKETS ");
      totalBucketNo.toString(params, builder);
    }
    if (abstractType) {
      builder.append(" ABSTRACT");
    }
  }

  @Override
  public CreateVertexTypeStatement copy() {
    CreateVertexTypeStatement result = new CreateVertexTypeStatement(-1);
    result.name = name == null ? null : name.copy();
    result.supertypes = supertypes == null ? null : supertypes.stream().map(x -> x.copy()).collect(Collectors.toList());
    result.buckets = buckets == null ? null : buckets.stream().map(x -> x.copy()).collect(Collectors.toList());
    result.totalBucketNo = totalBucketNo == null ? null : totalBucketNo.copy();
    result.abstractType = abstractType;
    result.ifNotExists = ifNotExists;
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    CreateVertexTypeStatement that = (CreateVertexTypeStatement) o;

    if (abstractType != that.abstractType)
      return false;
    if (name != null ? !name.equals(that.name) : that.name != null)
      return false;
    if (supertypes != null ? !supertypes.equals(that.supertypes) : that.supertypes != null)
      return false;
    if (buckets != null ? !buckets.equals(that.buckets) : that.buckets != null)
      return false;
    if (totalBucketNo != null ? !totalBucketNo.equals(that.totalBucketNo) : that.totalBucketNo != null)
      return false;
    return ifNotExists == that.ifNotExists;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (supertypes != null ? supertypes.hashCode() : 0);
    result = 31 * result + (buckets != null ? buckets.hashCode() : 0);
    result = 31 * result + (totalBucketNo != null ? totalBucketNo.hashCode() : 0);
    result = 31 * result + (abstractType ? 1 : 0);
    return result;
  }

  public List<Identifier> getSupertypes() {
    return supertypes;
  }
}
/* JavaCC - OriginalChecksum=4043013624f55fdf0ea8fee6d4f211b0 (do not edit this line) */
