/**
 * This file is part of mycollab-mobile.
 *
 * mycollab-mobile is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-mobile is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-mobile.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.esofthead.mycollab.mobile.ui;

import java.util.Arrays;
import java.util.List;

import com.esofthead.mycollab.vaadin.AppContext;

/**
 * 
 * @author MyCollab Ltd.
 * @since 4.5.0
 * 
 */
public class I18nValueComboBox extends ValueComboBox {

	private static final long serialVersionUID = 7466956429723924052L;

	public I18nValueComboBox() {
		super();
	}

	public I18nValueComboBox(boolean nullIsAllowable, Enum<?>... keys) {
		super();
		setNullSelectionAllowed(nullIsAllowable);
		loadData(Arrays.asList(keys));
	}

	public final void loadData(List<? extends Enum<?>> values) {
		this.setItemCaptionMode(ItemCaptionMode.EXPLICIT_DEFAULTS_ID);

		for (Enum<?> entry : values) {
			this.addItem(entry.name());
			this.setItemCaption(entry.name(), AppContext.getMessage(entry));
		}

		if (!this.isNullSelectionAllowed()) {
			this.select(this.getItemIds().iterator().next());
		}
	}
}
