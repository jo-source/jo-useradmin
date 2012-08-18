/*
 * Copyright (c) 2012, grossmann
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * * Neither the name of the jo-widgets.org nor the
 *   names of its contributors may be used to endorse or promote products
 *   derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL jo-widgets.org BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */

package org.jowidgets.useradmin.ui.filter;

import java.util.Collections;
import java.util.List;

import org.jowidgets.cap.common.api.filter.ArithmeticOperator;
import org.jowidgets.cap.ui.api.CapUiToolkit;
import org.jowidgets.cap.ui.api.filter.FilterType;
import org.jowidgets.cap.ui.api.filter.IFilterSupport;
import org.jowidgets.cap.ui.api.filter.IFilterSupportBuilder;
import org.jowidgets.cap.ui.api.filter.IFilterToolkit;
import org.jowidgets.cap.ui.api.filter.IFilterType;
import org.jowidgets.cap.ui.api.filter.IIncludingFilterFactory;
import org.jowidgets.cap.ui.api.filter.IOperatorProvider;
import org.jowidgets.cap.ui.api.filter.IUiArithmeticFilterBuilder;
import org.jowidgets.cap.ui.api.filter.IUiConfigurableFilter;
import org.jowidgets.cap.ui.api.filter.IUiFilterFactory;
import org.jowidgets.util.Assert;
import org.jowidgets.util.EmptyCheck;

public final class PasswordFilterSupportFactory {

	private PasswordFilterSupportFactory() {}

	public static IFilterSupport<String> create(final String propertyName) {
		Assert.paramNotNull(propertyName, "propertyName");

		final IFilterToolkit filterToolkit = CapUiToolkit.filterToolkit();
		final IFilterSupportBuilder<String> builder = filterToolkit.filterSupportBuilder();
		builder.addFilterPanelProvider(filterToolkit.arithmeticFilterPanel(
				propertyName,
				String.class,
				new EmptyOperatorProvider(),
				null,
				null));
		builder.setIncludingFilterFactory(new PasswordIncludingFilterFactory(propertyName));
		return builder.build();
	}

	private static final class EmptyOperatorProvider implements IOperatorProvider<ArithmeticOperator> {

		private static final List<ArithmeticOperator> OPERATORS = Collections.singletonList(ArithmeticOperator.EMPTY);

		@Override
		public List<ArithmeticOperator> getOperators() {
			return OPERATORS;
		}

		@Override
		public ArithmeticOperator getDefaultOperator() {
			return ArithmeticOperator.EMPTY;
		}

		@Override
		public boolean isInvertible(final ArithmeticOperator operator) {
			return true;
		}
	};

	private static final class PasswordIncludingFilterFactory implements IIncludingFilterFactory<String> {

		private final String propertyName;

		public PasswordIncludingFilterFactory(final String propertyName) {
			this.propertyName = propertyName;
		}

		@Override
		public IFilterType getFilterType() {
			return FilterType.ARITHMETIC_FILTER;
		}

		@Override
		public IUiConfigurableFilter<?> getIncludingFilter(final String value) {
			final IUiFilterFactory filterFactory = CapUiToolkit.filterToolkit().filterFactory();
			if (EmptyCheck.isEmpty(value)) {
				return filterFactory.arithmeticFilter(propertyName, ArithmeticOperator.EMPTY);
			}
			else {
				final IUiArithmeticFilterBuilder<Object> builder = filterFactory.arithmeticFilterBuilder();
				builder.setPropertyName(propertyName).setOperator(ArithmeticOperator.EMPTY).setInverted(true);
				return builder.build();
			}
		}
	}
}
