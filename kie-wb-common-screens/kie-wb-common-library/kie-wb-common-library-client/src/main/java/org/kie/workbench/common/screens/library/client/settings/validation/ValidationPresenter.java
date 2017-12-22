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

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import elemental2.promise.Promise;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.screens.library.client.settings.Promises;
import org.kie.workbench.common.screens.library.client.settings.SettingsPresenter;
import org.kie.workbench.common.screens.projecteditor.model.ProjectScreenModel;
import org.uberfire.ext.widgets.common.client.common.HasBusyIndicator;

import static java.util.Comparator.comparing;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.guvnor.common.services.project.model.ProjectRepositories.ProjectRepository;

public class ValidationPresenter implements SettingsPresenter.Section {

    private final View view;
    private final ManagedInstance<ValidationItemPresenter> validationItemPresenters;

    private Map<String, ValidationItemPresenter> itemPresenters;
    private Set<ProjectRepository> repositories;

    public interface View extends SettingsPresenter.View.Section<ValidationPresenter> {

        void setItems(final List<ValidationItemPresenter.View> view);
    }

    @Inject
    public ValidationPresenter(final ValidationPresenter.View view,
                               final ManagedInstance<ValidationItemPresenter> validationItemPresenters) {

        this.view = view;
        this.validationItemPresenters = validationItemPresenters;
    }

    @Override
    public void setup(final HasBusyIndicator container,
                      final ProjectScreenModel model) {

        repositories = model.getRepositories().getRepositories();

        itemPresenters = repositories.stream()
                .map(repository -> validationItemPresenters.get().setup(repository))
                .collect(toMap(ValidationItemPresenter::getId, identity()));

        view.setItems(itemPresenters.values()
                              .stream()
                              .sorted(comparing(ValidationItemPresenter::getId))
                              .map(ValidationItemPresenter::getView)
                              .collect(toList()));
    }

    @Override
    public Promise<Void> beforeSave() {
        repositories.forEach(repository -> repository.setIncluded(itemPresenters.get(repository.getMetadata().getId()).getInclude()));
        return Promises.resolve();
    }

    @Override
    public SettingsPresenter.View.Section getView() {
        return view;
    }
}
