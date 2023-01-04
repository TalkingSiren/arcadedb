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
/* Generated By:JJTree: Do not edit this line. OBaseExpression.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=true,TRACK_TOKENS=true,NODE_PREFIX=O,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_USERTYPE_VISIBILITY_PUBLIC=true */
package com.arcadedb.query.sql.parser;

import com.arcadedb.database.Identifiable;
import com.arcadedb.database.Record;
import com.arcadedb.exception.CommandExecutionException;
import com.arcadedb.query.sql.executor.AggregationContext;
import com.arcadedb.query.sql.executor.CommandContext;
import com.arcadedb.query.sql.executor.Result;
import com.arcadedb.query.sql.executor.ResultInternal;
import com.arcadedb.utility.NumberUtils;

import java.util.*;

public class BaseExpression extends MathExpression {
  protected PNumber        number;
  protected BaseIdentifier identifier;
  protected InputParameter inputParam;
  protected String         string;
  protected Modifier       modifier;
  protected boolean        isNull = false;

  public BaseExpression(final int id) {
    super(id);
  }

  public BaseExpression(final SqlParser p, final int id) {
    super(p, id);
  }

  public BaseExpression(final Identifier identifier) {
    super(-1);
    this.identifier = new BaseIdentifier(identifier);
  }

  public BaseExpression(final String string) {
    super(-1);
    this.string = "\"" + encode(string) + "\"";
  }

  public BaseExpression(final Identifier identifier, final Modifier modifier) {
    this(identifier);
    if (modifier != null) {
      this.modifier = modifier;
    }
  }

  public BaseExpression(final RecordAttribute attr, final Modifier modifier) {
    super(-1);
    this.identifier = new BaseIdentifier(attr);
    if (modifier != null) {
      this.modifier = modifier;
    }
  }

  public void toString(final Map<String, Object> params, final StringBuilder builder) {
    if (isNull)
      builder.append("NULL");
    else if (number != null) {
      number.toString(params, builder);
    } else if (identifier != null) {
      identifier.toString(params, builder);
    } else if (string != null) {
      builder.append(string);
    } else if (inputParam != null) {
      inputParam.toString(params, builder);
    }

    if (modifier != null) {
      modifier.toString(params, builder);
    }

  }

  public Object execute(final Identifiable iCurrentRecord, final CommandContext ctx) {
    Object result = null;
    if (isNull)
      result = null;
    else if (number != null)
      result = number.getValue();
    else if (identifier != null)
      result = identifier.execute(iCurrentRecord.getRecord(), ctx);
    else if (string != null && string.length() > 1)
      result = decode(string.substring(1, string.length() - 1));
    else if (inputParam != null)
      result = inputParam.getValue(ctx.getInputParameters());

    if (modifier != null)
      result = modifier.execute(iCurrentRecord, result, ctx);

    return result;
  }

  public Object execute(final Result iCurrentRecord, final CommandContext ctx) {
    Object result = null;
    if (isNull)
      result = null;
    if (number != null) {
      result = number.getValue();
    } else {
      final Map<String, Object> params = ctx != null ? ctx.getInputParameters() : null;

      if (identifier != null) {
        // CHECK FOR SPECIAL CASE FOR POSTGRES DRIVER THAT TRANSLATES POSITIONAL PARAMETERS (?) WITH $N
        // THIS IS DIFFERENT FROM ORIENTDB CODE BASE
        // @author Luca Garulli
        // @see Postgres Driver
        if (params != null && //
            identifier.getSuffix() != null && identifier.getSuffix().identifier != null) {
          final String v = identifier.getSuffix().identifier.getValue();
          if (v.startsWith("$") && v.length() > 1) {
            final String toParse = v.substring(1);

            final Integer pos = NumberUtils.parsePositiveInteger(toParse);
            if (pos != null)
              // POSTGRES PARAMETERS JDBC DRIVER START FROM 1
              result = params.get(String.valueOf(pos - 1));
            else
              result = identifier.execute(iCurrentRecord, ctx);
          } else
            result = identifier.execute(iCurrentRecord, ctx);
        } else
          result = identifier.execute(iCurrentRecord, ctx);
      } else if (string != null && string.length() > 1) {
        result = decode(string.substring(1, string.length() - 1));
      } else if (inputParam != null) {
        result = inputParam.getValue(params);
      }
    }
    if (modifier != null) {
      result = modifier.execute(iCurrentRecord, result, ctx);
    }
    return result;
  }

  @Override
  protected boolean supportsBasicCalculation() {
    return true;
  }

  @Override
  public boolean isIndexedFunctionCall() {
    if (this.identifier == null)
      return false;

    return identifier.isIndexedFunctionCall();
  }

  public long estimateIndexedFunction(final FromClause target, final CommandContext context, final BinaryCompareOperator operator, final Object right) {
    if (this.identifier == null)
      return -1;

    return identifier.estimateIndexedFunction(target, context, operator, right);
  }

  public Iterable<Record> executeIndexedFunction(final FromClause target, final CommandContext context, final BinaryCompareOperator operator,
      final Object right) {
    if (this.identifier == null)
      return null;
    return identifier.executeIndexedFunction(target, context, operator, right);
  }

  /**
   * tests if current expression is an indexed function AND that function can also be executed without using the index
   *
   * @param target   the query target
   * @param context  the execution context
   * @param operator
   * @param right
   *
   * @return true if current expression is an indexed function AND that function can also be executed without using the index, false
   * otherwise
   */
  public boolean canExecuteIndexedFunctionWithoutIndex(final FromClause target, final CommandContext context, final BinaryCompareOperator operator,
      final Object right) {
    if (this.identifier == null)
      return false;
    return identifier.canExecuteIndexedFunctionWithoutIndex(target, context, operator, right);
  }

  /**
   * tests if current expression is an indexed function AND that function can be used on this target
   *
   * @param target   the query target
   * @param context  the execution context
   * @param operator
   * @param right
   *
   * @return true if current expression is an indexed function AND that function can be used on this target, false otherwise
   */
  public boolean allowsIndexedFunctionExecutionOnTarget(final FromClause target, final CommandContext context, final BinaryCompareOperator operator,
      final Object right) {
    if (this.identifier == null)
      return false;

    return identifier.allowsIndexedFunctionExecutionOnTarget(target, context, operator, right);
  }

  /**
   * tests if current expression is an indexed function AND the function has also to be executed after the index search. In some
   * cases, the index search is accurate, so this condition can be excluded from further evaluation. In other cases the result from
   * the index is a superset of the expected result, so the function has to be executed anyway for further filtering
   *
   * @param target  the query target
   * @param context the execution context
   *
   * @return true if current expression is an indexed function AND the function has also to be executed after the index search.
   */
  public boolean executeIndexedFunctionAfterIndexSearch(final FromClause target, final CommandContext context, final BinaryCompareOperator operator,
      final Object right) {
    if (this.identifier == null)
      return false;

    return identifier.executeIndexedFunctionAfterIndexSearch(target, context, operator, right);
  }

  @Override
  public boolean isBaseIdentifier() {
    return identifier != null && modifier == null && identifier.isBaseIdentifier();
  }

  public boolean isEarlyCalculated() {
    if (number != null || inputParam != null || string != null)
      return true;

    return identifier != null && identifier.isEarlyCalculated();
  }

  @Override
  public boolean isExpand() {
    if (identifier != null)
      return identifier.isExpand();
    return false;
  }

  @Override
  public Expression getExpandContent() {
    return this.identifier.getExpandContent();
  }

  public boolean needsAliases(final Set<String> aliases) {
    if (this.identifier != null && this.identifier.needsAliases(aliases))
      return true;

    return modifier != null && modifier.needsAliases(aliases);
  }

  @Override
  public boolean isAggregate() {
    return identifier != null && identifier.isAggregate();
  }

  @Override
  public boolean isCount() {
    return identifier != null && identifier.isCount();
  }

  public SimpleNode splitForAggregation(final AggregateProjectionSplit aggregateProj) {
    if (isAggregate()) {
      final SimpleNode splitResult = identifier.splitForAggregation(aggregateProj);
      if (splitResult instanceof BaseIdentifier) {
        final BaseExpression result = new BaseExpression(-1);
        result.identifier = (BaseIdentifier) splitResult;
        return result;
      }
      return splitResult;
    } else {
      return this;
    }
  }

  public AggregationContext getAggregationContext(final CommandContext ctx) {
    if (identifier != null) {
      return identifier.getAggregationContext(ctx);
    } else {
      throw new CommandExecutionException("cannot aggregate on " + this);
    }
  }

  @Override
  public BaseExpression copy() {
    final BaseExpression result = new BaseExpression(-1);
    result.isNull = isNull;
    result.number = number == null ? null : number.copy();
    result.identifier = identifier == null ? null : identifier.copy();
    result.inputParam = inputParam == null ? null : inputParam.copy();
    result.string = string;
    result.modifier = modifier == null ? null : modifier.copy();
    result.cachedStringForm = cachedStringForm;
    return result;
  }

  public boolean refersToParent() {
    if (identifier != null && identifier.refersToParent())
      return true;

    return modifier != null && modifier.refersToParent();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    final BaseExpression that = (BaseExpression) o;

    if (!Objects.equals(number, that.number))
      return false;
    if (!Objects.equals(identifier, that.identifier))
      return false;
    if (!Objects.equals(inputParam, that.inputParam))
      return false;
    if (!Objects.equals(string, that.string))
      return false;
    return Objects.equals(modifier, that.modifier);
  }

  @Override
  public int hashCode() {
    int result = number != null ? number.hashCode() : 0;
    result = 31 * result + (identifier != null ? identifier.hashCode() : 0);
    result = 31 * result + (inputParam != null ? inputParam.hashCode() : 0);
    result = 31 * result + (string != null ? string.hashCode() : 0);
    result = 31 * result + (modifier != null ? modifier.hashCode() : 0);
    result = 31 * result + (isNull ? 1 : 0);
    return result;
  }

  public void setIdentifier(final BaseIdentifier identifier) {
    this.identifier = identifier;
  }

  public BaseIdentifier getIdentifier() {
    return identifier;
  }

  public Modifier getModifier() {
    return modifier;
  }

  public List<String> getMatchPatternInvolvedAliases() {
    if (this.identifier != null && this.identifier.toString().equals("$matched")) {
      if (modifier != null && modifier.suffix != null && modifier.suffix.identifier != null) {
        return Collections.singletonList(modifier.suffix.identifier.toString());
      }
    }
    return null;
  }

  @Override
  public void applyRemove(final ResultInternal result, final CommandContext ctx) {
    if (identifier != null) {
      if (modifier == null) {
        identifier.applyRemove(result, ctx);
      } else {
        final Object val = identifier.execute(result, ctx);
        modifier.applyRemove(val, result, ctx);
      }
    }
  }

  public Result serialize() {
    final ResultInternal result = (ResultInternal) super.serialize();
    if (isNull)
      result.setProperty("isNull", true);
    if (number != null)
      result.setProperty("number", number.serialize());
    if (identifier != null)
      result.setProperty("identifier", identifier.serialize());
    if (inputParam != null)
      result.setProperty("inputParam", inputParam.serialize());
    if (string != null)
      result.setProperty("string", string);
    if (modifier != null)
      result.setProperty("modifier", modifier.serialize());
    return result;
  }

  public void deserialize(final Result fromResult) {
    super.deserialize(fromResult);

    if (fromResult.getProperty("isNull") != null)
      isNull = true;

    if (fromResult.getProperty("number") != null) {
      number = new PNumber(-1);
      number.deserialize(fromResult.getProperty("number"));
    }
    if (fromResult.getProperty("identifier") != null) {
      identifier = new BaseIdentifier(-1);
      identifier.deserialize(fromResult.getProperty("identifier"));
    }
    if (fromResult.getProperty("inputParam") != null) {
      inputParam = InputParameter.deserializeFromOResult(fromResult.getProperty("inputParam"));
    }

    if (fromResult.getProperty("string") != null) {
      string = fromResult.getProperty("string");
    }
    if (fromResult.getProperty("modifier") != null) {
      modifier = new Modifier(-1);
      modifier.deserialize(fromResult.getProperty("modifier"));
    }
  }

  @Override
  public boolean isDefinedFor(final Result currentRecord) {
    if (this.identifier != null) {
      if (modifier == null) {
        return identifier.isDefinedFor(currentRecord);
      }
    }
    return true;
  }

  @Override
  public boolean isDefinedFor(final Record currentRecord) {
    if (this.identifier != null) {
      if (modifier == null) {
        return identifier.isDefinedFor(currentRecord);
      }
    }
    return true;
  }

  public void extractSubQueries(final Identifier letAlias, final SubQueryCollector collector) {
    if (this.identifier != null)
      this.identifier.extractSubQueries(letAlias, collector);
  }

  public void extractSubQueries(final SubQueryCollector collector) {
    if (this.identifier != null)
      this.identifier.extractSubQueries(collector);
  }

  public boolean isCacheable() {
    if (modifier != null && !modifier.isCacheable())
      return false;
    if (identifier != null)
      return identifier.isCacheable();

    return true;
  }

  public void setInputParam(final InputParameter inputParam) {
    this.inputParam = inputParam;
  }

  /**
   * Transforms, only if needed, the source string escaping the characters \ and ".
   *
   * @param iText Input String
   *
   * @return Modified string if needed, otherwise the same input object
   */
  public static String encode(final String iText) {
    int pos = -1;

    final int newSize = iText.length();
    for (int i = 0; i < newSize; ++i) {
      final char c = iText.charAt(i);

      if (c == '"' || c == '\\') {
        pos = i;
        break;
      }
    }

    if (pos > -1) {
      // CHANGE THE INPUT STRING
      final StringBuilder iOutput = new StringBuilder((int) ((float) newSize * 1.5f));

      char c;
      for (int i = 0; i < newSize; ++i) {
        c = iText.charAt(i);

        if (c == '"' || c == '\\')
          iOutput.append('\\');

        iOutput.append(c);
      }
      return iOutput.toString();
    }

    return iText;
  }

  /**
   * Transforms, only if needed, the source string un-escaping the characters \ and ".
   *
   * @param iText Input String
   *
   * @return Modified string if needed, otherwise the same input object
   */
  public static String decode(final String iText) {
    int pos = -1;

    final int textSize = iText.length();
    for (int i = 0; i < textSize; ++i)
      if (iText.charAt(i) == '"' || iText.charAt(i) == '\\') {
        pos = i;
        break;
      }

    if (pos == -1)
      // NOT FOUND, RETURN THE SAME STRING (AVOID COPIES)
      return iText;

    // CHANGE THE INPUT STRING
    final StringBuilder buffer = new StringBuilder(textSize);
    buffer.append(iText, 0, pos);

    boolean escaped = false;
    for (int i = pos; i < textSize; ++i) {
      final char c = iText.charAt(i);

      if (escaped) {
        escaped = false;
        if (c == 'n')
          buffer.append('\n');
        else if (c == 't')
          buffer.append('\t');
        else if (c == 'r')
          buffer.append('\r');
        else if (c == '%')
          buffer.append("\\%");
        else
          buffer.append(c);
        continue;
      } else if (c == '\\') {
        escaped = true;
        continue;
      }

      buffer.append(c);
    }
    return buffer.toString();
  }
}
/* JavaCC - OriginalChecksum=71b3e2d1b65c923dc7cfe11f9f449d2b (do not edit this line) */
