/*
 * Copyright (C) 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.client.settings.validation;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.ProjectRepositories.ProjectRepository;
import org.jboss.errai.ui.client.local.api.elemental2.IsElement;
import org.uberfire.client.mvp.UberElemental;

@Dependent
public class ValidationItemPresenter {

    public interface View extends UberElemental<ValidationItemPresenter>,
                                  IsElement {

        void setInclude(boolean included);

        void setId(String id);

        void setUrl(String url);

        void setSource(String source);
    }

    private final View view;

    private ProjectRepository projectRepository;

    @Inject
    public ValidationItemPresenter(final View view) {
        this.view = view;
    }

    ValidationItemPresenter setup(final ProjectRepository projectRepository) {

        this.projectRepository = projectRepository;

        view.init(this);
        view.setInclude(projectRepository.isIncluded());
        view.setId(projectRepository.getMetadata().getId());
        view.setUrl(projectRepository.getMetadata().getUrl());
        view.setSource(projectRepository.getMetadata().getSource().name());

        return this;
    }

    public void setInclude(final boolean include) {
        projectRepository.setIncluded(include);
    }

    public View getView() {
        return view;
    }
}
