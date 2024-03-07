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

import com.arcadedb.database.Document;
import com.arcadedb.exception.CommandExecutionException;
import com.arcadedb.exception.TimeoutException;

/**
 * Checks that all the records from the upstream are of a particular type (or subTypes). Throws PCommandExecutionException in case
 * it's not true
 */
public class CheckRecordTypeStep extends AbstractExecutionStep {
  private final String typez;

  public CheckRecordTypeStep(final CommandContext context, final String className) {
    super(context);
    this.typez = className;
  }

  @Override
  public ResultSet syncPull(final CommandContext context, final int nRecords) throws TimeoutException {
    checkForPrevious();

    final ResultSet upstream = prev.syncPull(context, nRecords);
    return new ResultSet() {
      @Override
      public boolean hasNext() {
        return upstream.hasNext();
      }

      @Override
      public Result next() {
        final Result result = upstream.next();

        final long begin = context.isProfiling() ? System.nanoTime() : 0;
        try {
          if (!result.isElement())
            throw new CommandExecutionException("Record " + result + " is not an instance of " + typez);

          final Document record = result.getElement().get();
          if (record == null)
            throw new CommandExecutionException("Record " + result + " is not an instance of " + typez);

          if (!record.getType().isSubTypeOf(typez))
            throw new CommandExecutionException("Record " + result + " is not an instance of " + typez);

          return result;
        } finally {
          if (context.isProfiling())
            cost += (System.nanoTime() - begin);
        }
      }

      @Override
      public void close() {
        upstream.close();
      }
    };
  }

  @Override
  public String prettyPrint(final int depth, final int indent) {
    String result = ExecutionStepInternal.getIndent(depth, indent) + "+ CHECK RECORD TYPE";
    if (context.isProfiling())
      result += " (" + getCostFormatted() + ")";

    result += (ExecutionStepInternal.getIndent(depth, indent) + "  " + typez);
    return result;
  }

}
