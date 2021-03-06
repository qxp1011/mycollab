/**
 * This file is part of mycollab-web.
 *
 * mycollab-web is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * mycollab-web is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with mycollab-web.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.esofthead.mycollab.module.crm.ui.components;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;

import com.esofthead.mycollab.core.MyCollabException;
import com.esofthead.mycollab.core.arguments.SearchCriteria;
import com.esofthead.mycollab.vaadin.ui.table.IPagedBeanTable;
import com.esofthead.mycollab.vaadin.ui.table.IPagedBeanTable.TableClickEvent;
import com.esofthead.mycollab.vaadin.ui.table.IPagedBeanTable.TableClickListener;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.vaadin.maddon.layouts.MVerticalLayout;

/**
 * 
 * @author MyCollab Ltd.
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
public abstract class RelatedItemSelectionWindow<T, S extends SearchCriteria>
		extends Window {
	private static final long serialVersionUID = 1L;

	private static final String selectedFieldName = "selected";

	protected RelatedListComp2 relatedListComp;
	protected IPagedBeanTable<S, T> tableItem;
	protected Set selectedItems = new HashSet();
	protected MVerticalLayout bodyContent;

	public RelatedItemSelectionWindow(String title, RelatedListComp2 relatedList) {
		super(title);
		center();
		bodyContent = new MVerticalLayout();
		this.setContent(bodyContent);
		this.relatedListComp = relatedList;
		this.setModal(true);
		this.setResizable(false);
		initUI();

		tableItem.addTableListener(new TableClickListener() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("unchecked")
			@Override
			public void itemClick(TableClickEvent event) {
				try {
					Object rowItem = event.getData();
					Boolean selectedVal = (Boolean) PropertyUtils.getProperty(
							rowItem, selectedFieldName);
					if (selectedVal) {
						selectedItems.remove(rowItem);
						PropertyUtils.setProperty(rowItem, selectedFieldName,
								false);
					} else {
						selectedItems.add(rowItem);
						PropertyUtils.setProperty(rowItem, selectedFieldName,
								true);
					}
				} catch (Exception ex) {
					throw new MyCollabException(ex);
				}
			}
		});
	}

	@Override
	public void close() {
		super.close();
		if (!selectedItems.isEmpty()) {
			relatedListComp.fireSelectedRelatedItems(selectedItems);
		}
	}

	protected abstract void initUI();

	public void setSearchCriteria(S criteria) {
		tableItem.setSearchCriteria(criteria);
	}
}
