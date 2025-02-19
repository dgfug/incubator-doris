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
import org.apache.doris.common.Config;
import org.apache.doris.nereids.trees.expressions.Expression;
import org.apache.doris.nereids.trees.expressions.functions.ExplicitlyCastableSignature;
import org.apache.doris.nereids.trees.expressions.functions.PropagateNullableOnDateLikeV2Args;
import org.apache.doris.nereids.trees.expressions.shape.UnaryExpression;
import org.apache.doris.nereids.trees.expressions.visitor.ExpressionVisitor;
import org.apache.doris.nereids.types.DateTimeType;
import org.apache.doris.nereids.types.DateTimeV2Type;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * ScalarFunction 'timestamp'. This class is generated by GenerateFunction.
 */
public class Timestamp extends ScalarFunction
        implements UnaryExpression, ExplicitlyCastableSignature, PropagateNullableOnDateLikeV2Args {
    //When enable_date_conversion is true, we prefer to V2 signature.
    // This preference follows original planner. refer to ScalarType.getDefaultDateType()
    public static final List<FunctionSignature> SIGNATURES = Config.enable_date_conversion ? ImmutableList.of(
            FunctionSignature.ret(DateTimeV2Type.SYSTEM_DEFAULT).args(DateTimeV2Type.SYSTEM_DEFAULT),
            FunctionSignature.ret(DateTimeType.INSTANCE).args(DateTimeType.INSTANCE)
    ) : ImmutableList.of(
            FunctionSignature.ret(DateTimeType.INSTANCE).args(DateTimeType.INSTANCE),
            FunctionSignature.ret(DateTimeV2Type.SYSTEM_DEFAULT).args(DateTimeV2Type.SYSTEM_DEFAULT)
    );

    /**
     * constructor with 1 argument.
     */
    public Timestamp(Expression arg) {
        super("timestamp", arg);
    }

    /**
     * withChildren.
     */
    @Override
    public Timestamp withChildren(List<Expression> children) {
        Preconditions.checkArgument(children.size() == 1);
        return new Timestamp(children.get(0));
    }

    @Override
    public List<FunctionSignature> getSignatures() {
        return SIGNATURES;
    }

    @Override
    public <R, C> R accept(ExpressionVisitor<R, C> visitor, C context) {
        return visitor.visitTimestamp(this, context);
    }
}
