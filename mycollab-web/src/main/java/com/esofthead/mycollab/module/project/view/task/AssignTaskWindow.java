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

package com.esofthead.mycollab.module.project.view.task;

import com.esofthead.mycollab.common.domain.CommentWithBLOBs;
import com.esofthead.mycollab.common.i18n.GenericI18Enum;
import com.esofthead.mycollab.common.service.CommentService;
import com.esofthead.mycollab.eventmanager.EventBusFactory;
import com.esofthead.mycollab.module.project.CurrentProjectVariables;
import com.esofthead.mycollab.module.project.ProjectTypeConstants;
import com.esofthead.mycollab.module.project.domain.Task;
import com.esofthead.mycollab.module.project.events.TaskEvent;
import com.esofthead.mycollab.module.project.i18n.TaskI18nEnum;
import com.esofthead.mycollab.module.project.service.ProjectTaskService;
import com.esofthead.mycollab.module.project.view.settings.component.ProjectMemberSelectionField;
import com.esofthead.mycollab.spring.ApplicationContextUtil;
import com.esofthead.mycollab.vaadin.AppContext;
import com.esofthead.mycollab.vaadin.ui.*;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import org.vaadin.maddon.layouts.MHorizontalLayout;
import org.vaadin.maddon.layouts.MVerticalLayout;

import java.util.GregorianCalendar;

/**
 * @author MyCollab Ltd.
 * @since 1.0
 */
public class AssignTaskWindow extends Window {
    private static final long serialVersionUID = 1L;
    private final Task task;
    private final EditForm editForm;

    public AssignTaskWindow(Task task) {
        super(AppContext.getMessage(TaskI18nEnum.DIALOG_ASSIGN_TASK_TITLE,
                task.getTaskname()));

        MVerticalLayout contentLayout = new MVerticalLayout()
                .withMargin(new MarginInfo(false, false, true, false));

        this.task = task;
        this.setWidth("750px");
        this.setResizable(false);
        this.setModal(true);
        editForm = new EditForm();
        contentLayout.addComponent(editForm);
        editForm.setBean(task);

        this.setContent(contentLayout);

        center();
    }

    private class EditForm extends AdvancedEditBeanForm<Task> {
        private static final long serialVersionUID = 1L;
        private RichTextArea commentArea;

        @Override
        public void setBean(Task newDataSource) {
            this.setFormLayoutFactory(new FormLayoutFactory());
            this.setBeanFormFieldFactory(new EditFormFieldFactory(EditForm.this));
            super.setBean(newDataSource);
        }

        class FormLayoutFactory implements IFormLayoutFactory {
            private static final long serialVersionUID = 1L;
            private GridFormLayoutHelper informationLayout;

            @Override
            public ComponentContainer getLayout() {
                VerticalLayout layout = new VerticalLayout();
                this.informationLayout = GridFormLayoutHelper.defaultFormLayoutHelper(2, 2);

                layout.addComponent(informationLayout.getLayout());

                MHorizontalLayout controlsBtn = new MHorizontalLayout().withMargin(new MarginInfo(true, true, true, false));
                layout.addComponent(controlsBtn);

                Button cancelBtn = new Button(
                        AppContext.getMessage(GenericI18Enum.BUTTON_CANCEL),
                        new Button.ClickListener() {
                            private static final long serialVersionUID = 1L;

                            @Override
                            public void buttonClick(Button.ClickEvent event) {
                                AssignTaskWindow.this.close();
                            }
                        });
                cancelBtn.setStyleName(UIConstants.THEME_GRAY_LINK);

                Button approveBtn = new Button(
                        AppContext.getMessage(GenericI18Enum.BUTTON_ASSIGN),
                        new Button.ClickListener() {
                            private static final long serialVersionUID = 1L;

                            @Override
                            public void buttonClick(Button.ClickEvent event) {
                                if (EditForm.this.validateForm()) {
                                    // Save task status and assignee
                                    ProjectTaskService taskService = ApplicationContextUtil
                                            .getSpringBean(ProjectTaskService.class);
                                    taskService.updateWithSession(task,
                                            AppContext.getUsername());

                                    // Save comment
                                    String commentValue = commentArea
                                            .getValue();
                                    if (commentValue != null
                                            && !commentValue.trim().equals("")) {
                                        CommentWithBLOBs comment = new CommentWithBLOBs();
                                        comment.setComment(commentArea
                                                .getValue());
                                        comment.setCreatedtime(new GregorianCalendar()
                                                .getTime());
                                        comment.setCreateduser(AppContext
                                                .getUsername());
                                        comment.setSaccountid(AppContext
                                                .getAccountId());
                                        comment.setType(ProjectTypeConstants.TASK);
                                        comment.setTypeid("" + task.getId());
                                        comment.setExtratypeid(CurrentProjectVariables
                                                .getProjectId());

                                        CommentService commentService = ApplicationContextUtil
                                                .getSpringBean(CommentService.class);
                                        commentService.saveWithSession(comment,
                                                AppContext.getUsername());
                                    }

                                    AssignTaskWindow.this.close();
                                    EventBusFactory.getInstance().post(
                                            new TaskEvent.GotoRead(this, task
                                                    .getId()));
                                }
                            }
                        });
                approveBtn.setIcon(FontAwesome.SHARE);
                approveBtn.setStyleName(UIConstants.THEME_GREEN_LINK);
                approveBtn.setClickShortcut(ShortcutAction.KeyCode.ENTER);

                controlsBtn.with(cancelBtn, approveBtn).alignAll(Alignment.MIDDLE_RIGHT);

                layout.setComponentAlignment(controlsBtn,
                        Alignment.MIDDLE_RIGHT);

                return layout;
            }

            @Override
            public void attachField(Object propertyId, Field<?> field) {
                if (Task.Field.assignuser.equalTo(propertyId)) {
                    informationLayout
                            .addComponent(field, AppContext
                                            .getMessage(GenericI18Enum.FORM_ASSIGNEE),
                                    0, 0);
                } else if (propertyId.equals("comment")) {
                    informationLayout.addComponent(field,
                            AppContext.getMessage(TaskI18nEnum.FORM_COMMENT),
                            0, 1, 2, "100%", Alignment.MIDDLE_LEFT);
                }
            }
        }

        private class EditFormFieldFactory extends
                AbstractBeanFieldGroupEditFieldFactory<Task> {
            private static final long serialVersionUID = 1L;

            public EditFormFieldFactory(GenericBeanForm<Task> form) {
                super(form);
            }

            @Override
            protected Field<?> onCreateField(Object propertyId) {
                if (propertyId.equals("assignuser")) {
                    return new ProjectMemberSelectionField();
                } else if (propertyId.equals("comment")) {
                    commentArea = new RichTextArea();
                    commentArea.setNullRepresentation("");
                    return commentArea;
                }
                return null;
            }
        }
    }
}
