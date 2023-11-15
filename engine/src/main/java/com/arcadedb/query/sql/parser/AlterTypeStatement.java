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
 *
 * SPDX-FileCopyrightText: 2021-present Arcade Data Ltd (info@arcadedata.com)
 * SPDX-License-Identifier: Apache-2.0
 */
/* Generated By:JJTree: Do not edit this line. OAlterClassStatement.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_USERTYPE_VISIBILITY_PUBLIC=true */
package com.arcadedb.query.sql.parser;

import com.arcadedb.database.Identifiable;
import com.arcadedb.exception.CommandExecutionException;
import com.arcadedb.exception.CommandSQLParsingException;
import com.arcadedb.query.sql.executor.CommandContext;
import com.arcadedb.query.sql.executor.InternalResultSet;
import com.arcadedb.query.sql.executor.ResultInternal;
import com.arcadedb.query.sql.executor.ResultSet;
import com.arcadedb.schema.DocumentType;
import com.arcadedb.schema.EmbeddedDocumentType;

import java.util.*;
import java.util.stream.*;

public class AlterTypeStatement extends DDLStatement {

  /**
   * the name of the class
   */
  protected Identifier       name;
  /**
   * the class property to be altered
   */
  public    String           property;
  protected Identifier       identifierValue;
  protected List<Boolean>    identifierListAddRemove = new ArrayList<>();
  protected List<Identifier> identifierListValue     = new ArrayList<>();
  protected PNumber          numberValue;
  protected Boolean          booleanValue;
  protected Identifier       customKey;
  protected Expression       customValue;

  public AlterTypeStatement(final int id) {
    super(id);
  }

  @Override
  public void toString(final Map<String, Object> params, final StringBuilder builder) {
    builder.append("ALTER TYPE ");
    name.toString(params, builder);
    if (property != null) {
      builder.append(" " + property + " ");

      if (numberValue != null) {
        numberValue.toString(params, builder); // clusters only
      } else if (identifierValue != null) {
        identifierValue.toString(params, builder);
      } else {
        builder.append("null");
      }
    }

    if (customKey != null) {
      builder.append(" CUSTOM ");
      customKey.toString(params, builder);
      builder.append("=");
      if (customValue == null) {
        builder.append("null");
      } else {
        customValue.toString(params, builder);
      }
    }
  }

  public Statement copy() {
    final AlterTypeStatement result = new AlterTypeStatement(-1);
    result.name = name == null ? null : name.copy();
    result.property = property;
    result.identifierListValue = identifierListValue.stream().map(x -> x.copy()).collect(Collectors.toList());
    result.identifierListAddRemove = new ArrayList<>(identifierListAddRemove);
    result.numberValue = numberValue == null ? null : numberValue.copy();
    result.booleanValue = booleanValue;
    result.customKey = customKey == null ? null : customKey.copy();
    result.customValue = customValue == null ? null : customValue.copy();
    return result;
  }

  @Override
  protected Object[] getIdentityElements() {
    return new Object[] { name, property, identifierListValue, identifierListAddRemove, numberValue, booleanValue, customKey,
        customValue };
  }

  @Override
  public ResultSet executeDDL(final CommandContext context) {
    final DocumentType type = context.getDatabase().getSchema().getType(name.getStringValue());
    if (type == null)
      throw new CommandExecutionException("Type not found: " + name);

    final ResultInternal result = new ResultInternal();

    if (property != null) {
      switch (property.toLowerCase(Locale.ENGLISH)) {
      case "bucket":
        for (int i = 0; i < identifierListValue.size(); i++) {
          final Identifier identifierValue = identifierListValue.get(i);
          final Boolean add = identifierListAddRemove.get(i);

          if (Boolean.TRUE.equals(add)) {

            if (identifierValue != null) {
              if (!context.getDatabase().getSchema().existsBucket(identifierValue.getStringValue()))
                context.getDatabase().getSchema().createBucket(identifierValue.getStringValue());

              type.addBucket(context.getDatabase().getSchema().getBucketByName(identifierValue.getStringValue()));
              result.setProperty("addBucket", identifierValue.getStringValue());

            } else if (numberValue != null) {
              type.addBucket(context.getDatabase().getSchema().getBucketById(numberValue.getValue().intValue()));
              result.setProperty("addBucket", numberValue.getValue().intValue());
            } else
              throw new CommandExecutionException("Invalid bucket value: " + this);

          } else if (Boolean.FALSE.equals(add)) {

            if (identifierValue != null) {
              type.removeBucket(context.getDatabase().getSchema().getBucketByName(identifierValue.getStringValue()));
              result.setProperty("removeBucket", identifierValue.getStringValue());
            } else if (numberValue != null) {
              type.removeBucket(context.getDatabase().getSchema().getBucketById(numberValue.getValue().intValue()));
              result.setProperty("removeBucket", numberValue.getValue().intValue());
            } else
              throw new CommandExecutionException("Invalid bucket value: " + this);
          }
        }
        break;

      case "supertype":
        doSetSuperType(context, type);
        result.setProperty("supertype", type);
        break;

      case "bucketselectionstrategy": {
        final String implName = identifierValue.getStringValue();
        try {
          type.setBucketSelectionStrategy(implName);
          result.setProperty("bucketSelectionStrategy", implName);
        } catch (Exception e) {
          throw new CommandSQLParsingException("Bucket selection strategy implementation '" + implName + "' was not found", e);
        }
        break;
      }

      default:
        throw new CommandExecutionException("Error on alter type: property '" + property + "' not valid");
      }
    }

    if (customKey != null) {
      Object value = null;
      if (customValue != null) {
        value = customValue.execute((Identifiable) null, context);
      }

      type.setCustomValue(customKey.getStringValue(), value);
      result.setProperty("custom", customKey.getStringValue() + "=" + value);
    }

    result.setProperty("operation", "ALTER TYPE");
    result.setProperty("typeName", name.getStringValue());
    result.setProperty("result", "OK");

    final InternalResultSet resultSet = new InternalResultSet();
    resultSet.add(result);

    return resultSet;
  }

  private void doSetSuperType(final CommandContext context, final DocumentType type) {
    if (identifierListValue == null)
      throw new CommandExecutionException("Invalid super type names");

    for (int i = 0; i < identifierListValue.size(); i++) {
      final Identifier superTypeName = identifierListValue.get(i);
      final Boolean add = identifierListAddRemove.get(i);

      final DocumentType superclass = context.getDatabase().getSchema().getType(superTypeName.getStringValue());
      if (superclass == null)
        throw new CommandExecutionException("Super type '" + superTypeName.getStringValue() + "' not found");

      if (add)
        type.addSuperType(superclass);
      else
        type.removeSuperType(superclass);
    }
  }
}
/* JavaCC - OriginalChecksum=4668bb1cd336844052df941f39bdb634 (do not edit this line) */
