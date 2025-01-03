// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.doris.nereids.trees.expressions.functions.scalar;

import org.apache.doris.catalog.FunctionSignature;
import org.apache.doris.nereids.trees.expressions.Expression;
import org.apache.doris.nereids.trees.expressions.functions.ExplicitlyCastableSignature;
import org.apache.doris.nereids.trees.expressions.functions.Monotonic;
import org.apache.doris.nereids.trees.expressions.functions.PropagateNullableOnDateLikeV2Args;
import org.apache.doris.nereids.trees.expressions.literal.Literal;
import org.apache.doris.nereids.trees.expressions.visitor.ExpressionVisitor;
import org.apache.doris.nereids.types.DateTimeType;
import org.apache.doris.nereids.types.DateTimeV2Type;
import org.apache.doris.nereids.types.DateV2Type;
import org.apache.doris.nereids.types.IntegerType;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * ScalarFunction 'yearweek'. This class is generated by GenerateFunction.
 */
public class YearWeek extends ScalarFunction
        implements ExplicitlyCastableSignature, PropagateNullableOnDateLikeV2Args, Monotonic {

    private static final List<FunctionSignature> SIGNATURES = ImmutableList.of(
            FunctionSignature.ret(IntegerType.INSTANCE).args(DateV2Type.INSTANCE),
            FunctionSignature.ret(IntegerType.INSTANCE).args(DateV2Type.INSTANCE, IntegerType.INSTANCE),
            FunctionSignature.ret(IntegerType.INSTANCE).args(DateTimeV2Type.SYSTEM_DEFAULT),
            FunctionSignature.ret(IntegerType.INSTANCE).args(DateTimeV2Type.SYSTEM_DEFAULT, IntegerType.INSTANCE),
            FunctionSignature.ret(IntegerType.INSTANCE).args(DateTimeType.INSTANCE),
            FunctionSignature.ret(IntegerType.INSTANCE).args(DateTimeType.INSTANCE, IntegerType.INSTANCE)
    );

    /**
     * constructor with 1 argument.
     */
    public YearWeek(Expression arg) {
        super("yearweek", arg);
    }

    /**
     * constructor with 2 arguments.
     */
    public YearWeek(Expression arg0, Expression arg1) {
        super("yearweek", arg0, arg1);
    }

    /**
     * withChildren.
     */
    @Override
    public YearWeek withChildren(List<Expression> children) {
        Preconditions.checkArgument(children.size() == 1
                || children.size() == 2);
        if (children.size() == 1) {
            return new YearWeek(children.get(0));
        } else {
            return new YearWeek(children.get(0), children.get(1));
        }
    }

    @Override
    public List<FunctionSignature> getSignatures() {
        return SIGNATURES;
    }

    @Override
    public <R, C> R accept(ExpressionVisitor<R, C> visitor, C context) {
        return visitor.visitYearWeek(this, context);
    }

    @Override
    public boolean isMonotonic(Literal lower, Literal upper) {
        if (arity() == 1) {
            return true;
        }
        return child(1) instanceof Literal;
    }

    @Override
    public boolean isPositive() {
        return true;
    }

    @Override
    public int getMonotonicFunctionChildIndex() {
        return 0;
    }

    @Override
    public Expression withConstantArgs(Expression literal) {
        if (arity() == 1) {
            return new YearWeek(literal);
        }
        return new YearWeek(literal, child(1));
    }
}
