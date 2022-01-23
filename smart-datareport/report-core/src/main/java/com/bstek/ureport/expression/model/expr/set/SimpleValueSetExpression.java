/*******************************************************************************
 * Copyright 2017 Bstek
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.bstek.ureport.expression.model.expr.set;

import com.bstek.ureport.build.Context;
import com.bstek.ureport.expression.model.data.ExpressionData;
import com.bstek.ureport.expression.model.data.ObjectExpressionData;
import com.bstek.ureport.expression.model.expr.BaseExpression;
import com.bstek.ureport.model.Cell;

/**
 * @author
 * @since 1月1日
 */
public class SimpleValueSetExpression extends BaseExpression {
	private static final long serialVersionUID = -5433811018086391838L;
	private Object simpleValue;
	public SimpleValueSetExpression(Object simpleValue) {
		this.simpleValue=simpleValue;
	}
	@Override
	protected ExpressionData<?> compute(Cell cell,Cell currentCell,Context context) {
		return new ObjectExpressionData(simpleValue);
	}
}
