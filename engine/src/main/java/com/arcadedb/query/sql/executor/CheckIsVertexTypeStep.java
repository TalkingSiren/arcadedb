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
package com.arcadedb.query.sql.executor;

import com.arcadedb.database.Database;
import com.arcadedb.exception.CommandExecutionException;
import com.arcadedb.exception.TimeoutException;
import com.arcadedb.schema.DocumentType;
import com.arcadedb.schema.EmbeddedVertexType;
import com.arcadedb.schema.Schema;

/**
 * This step is used just as a gate check for classes (eg. for CREATE VERTEX to make sure that the passed class is a vertex class).
 *
 * @author Luigi Dell'Aquila (luigi.dellaquila-(at)-gmail.com)
 */
public class CheckIsVertexTypeStep extends AbstractExecutionStep {
  private final String targetClass;
  boolean found = false;

  /**
   * @param targetClass      a type to be checked
   * @param context          execution context
   * @param profilingEnabled true to collect execution stats
   */
  public CheckIsVertexTypeStep(final String targetClass, final CommandContext context, final boolean profilingEnabled) {
    super(context, profilingEnabled);
    this.targetClass = targetClass;
  }

  @Override
  public ResultSet syncPull(final CommandContext context, final int nRecords) throws TimeoutException {
    pullPrevious(context, nRecords);

    final long begin = profilingEnabled ? System.nanoTime() : 0;
    try {
      if (found) {
        return new InternalResultSet();
      }

      final Database db = context.getDatabase();

      final Schema schema = db.getSchema();

      final DocumentType targettypez = schema.getType(this.targetClass);
      if (targettypez == null) {
        throw new CommandExecutionException("Type not found: " + this.targetClass);
      }

      if (targettypez instanceof EmbeddedVertexType) {
        found = true;
      }
      if (!found) {
        throw new CommandExecutionException("Type ' '" + this.targetClass + "' is not a Vertex type");
      }

      return new InternalResultSet();
    } finally {
      if (profilingEnabled) {
        cost += (System.nanoTime() - begin);
      }
    }
  }

  @Override
  public String prettyPrint(final int depth, final int indent) {
    final String spaces = ExecutionStepInternal.getIndent(depth, indent);
    final StringBuilder result = new StringBuilder();
    result.append(spaces);
    result.append("+ CHECK TYPE HIERARCHY (V)");
    if (profilingEnabled) {
      result.append(" (").append(getCostFormatted()).append(")");
    }
    return result.toString();
  }
}
