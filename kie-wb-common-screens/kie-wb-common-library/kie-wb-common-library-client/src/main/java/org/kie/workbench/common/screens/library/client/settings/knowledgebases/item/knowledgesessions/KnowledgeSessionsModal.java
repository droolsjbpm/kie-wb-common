/*
 * Copyright (C) 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.settings.knowledgebases.item.knowledgesessions;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.client.settings.knowledgebases.item.KnowledgeBaseItemPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.ListPresenter;
import org.kie.workbench.common.screens.library.client.settings.util.modal.Elemental2Modal;
import org.kie.workbench.common.services.shared.kmodule.KBaseModel;
import org.kie.workbench.common.services.shared.kmodule.KSessionModel;

@Dependent
public class KnowledgeSessionsModal extends Elemental2Modal<KnowledgeSessionsModal.View> {

    private final KnowledgeBasesListPresenter knowledgeBasesListPresenter;

    private KnowledgeBaseItemPresenter parentPresenter;

    @Inject
    public KnowledgeSessionsModal(final View view,
                                  final KnowledgeBasesListPresenter knowledgeBasesListPresenter) {
        super(view);
        this.knowledgeBasesListPresenter = knowledgeBasesListPresenter;
    }

    public void setup(final KnowledgeBaseItemPresenter parentPresenter) {

        this.parentPresenter = parentPresenter;

        knowledgeBasesListPresenter.setup(
                getView().getKnowledgeSessionsTable(),
                parentPresenter.getObject().getKSessions(),
                (kSessionModel, presenter) -> presenter.setup(kSessionModel, this));

        super.setup();

        setWidth("1200px"); //FIXME: ugly
    }

    public KBaseModel getObject() {
        return parentPresenter.getObject();
    }

    public void add() {
        final KSessionModel kSessionModel = new KSessionModel();
        kSessionModel.setName("");
        kSessionModel.setDefault(knowledgeBasesListPresenter.getObjectsList().isEmpty());
        knowledgeBasesListPresenter.add(kSessionModel);
        signalKnowledgeBaseAddedOrRemoved();
    }

    public void signalKnowledgeBaseAddedOrRemoved() {
        parentPresenter.signalAddedOrRemoved();
    }

    public void fireChangeEvent() {
        parentPresenter.fireChangeEvent();
    }

    public void done() {
        knowledgeBasesListPresenter.getPresenters().forEach(KnowledgeSessionListItemPresenter::closeAllExpandableListItems);
        hide();
    }

    public interface View extends Elemental2Modal.View<KnowledgeSessionsModal> {

        HTMLElement getKnowledgeSessionsTable();
    }

    @Dependent
    public static class KnowledgeBasesListPresenter extends ListPresenter<KSessionModel, KnowledgeSessionListItemPresenter> {

        @Inject
        public KnowledgeBasesListPresenter(final ManagedInstance<KnowledgeSessionListItemPresenter> itemPresenters) {
            super(itemPresenters);
        }
    }
}
