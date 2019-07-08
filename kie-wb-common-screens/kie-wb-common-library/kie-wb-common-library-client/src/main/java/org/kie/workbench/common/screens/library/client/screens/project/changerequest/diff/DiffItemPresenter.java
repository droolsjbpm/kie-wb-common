/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.screens.project.changerequest.diff;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import elemental2.dom.HTMLElement;
import org.guvnor.structure.repositories.changerequest.ChangeRequestDiff;
import org.guvnor.structure.repositories.changerequest.ChangeType;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.screens.library.client.resources.i18n.LibraryConstants;
import org.uberfire.backend.vfs.Path;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.ResourceTypeManagerCache;
import org.uberfire.client.mvp.UberElemental;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.PathPlaceRequest;
import org.uberfire.workbench.diff.DiffMode;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@Dependent
public class DiffItemPresenter {

    public interface View extends UberElemental<DiffItemPresenter> {

        void setupTextualContent(final String filename,
                                 final String changeType,
                                 final boolean conflict);

        void drawTextualContent(final String diffText,
                                final boolean isUnified);

        void setupCustomContent(final String filename,
                                final String changeType,
                                final boolean conflict);

        void expandCustomLeftContainer();

        void expandCustomRightContainer();

        HTMLElement getCustomLeftContainer();

        HTMLElement getCustomRightContainer();

        void clearCustomLeftContainer();

        void clearCustomRightContainer();

        void expandCollapsibleContainer(final boolean isOpened);
    }

    private final View view;
    private final ResourceTypeManagerCache resourceTypeManagerCache;
    private final PlaceManager placeManager;
    private final TranslationService ts;

    private DiffMode diffMode;
    private PlaceRequest placeRequestCustomLeft;
    private PlaceRequest placeRequestCustomRight;
    private ChangeRequestDiff diff;
    private boolean ready;
    private boolean open;

    @Inject
    public DiffItemPresenter(final View view,
                             final ResourceTypeManagerCache resourceTypeManagerCache,
                             final PlaceManager placeManager,
                             final TranslationService ts) {
        this.view = view;
        this.resourceTypeManagerCache = resourceTypeManagerCache;
        this.placeManager = placeManager;
        this.ts = ts;
    }

    @PostConstruct
    public void postConstruct() {
        this.prepareView();
    }

    @PreDestroy
    public void preDestroy() {
        if (ready && open && diffMode == DiffMode.VISUAL) {
            closeVisualContent();
        }
    }

    public View getView() {
        return view;
    }

    public void setup(final ChangeRequestDiff changeRequestDiff) {

        diff = changeRequestDiff;
        diffMode = resolveDiffMode(diff);

        final String resolveDiffFilename = resolveDiffFilename(diff.getChangeType(),
                                                               diff.getOldFilePath().getFileName(),
                                                               diff.getNewFilePath().getFileName());

        if (diffMode == DiffMode.VISUAL) {
            prepareVisualDiff(diff,
                              resolveDiffFilename);
        } else {
            prepareTextualDiff(diff,
                               resolveDiffFilename);
        }

        ready = true;

        view.expandCollapsibleContainer(open);
    }

    public void draw() {
        if (ready) {
            if (open) {
                if (diffMode == DiffMode.VISUAL) {
                    drawVisualContent();
                } else {
                    drawTextualContent();
                }
            }
        } else {
            throw new IllegalStateException("Item not ready - setup first.");
        }
    }

    public void toggleCollapsibleContainerState() {
        if (ready) {
            if (diffMode == DiffMode.VISUAL) {
                if (open) {
                    closeVisualContent();
                } else {
                    drawVisualContent();
                }
            } else {
                if (!open) {
                    drawTextualContent();
                }
            }
        }

        open = !open;
    }

    PlaceRequest createPlaceRequest(final Path path) {
        return new PathPlaceRequest(path,
                                    createPathPlaceRequestParameters());
    }

    private void drawTextualContent() {
        final boolean isUnified = diff.getChangeType() == ChangeType.ADD
                || diff.getChangeType() == ChangeType.DELETE;

        view.drawTextualContent(diff.getDiffText(),
                                isUnified);
    }

    private void drawVisualContent() {
        if (diff.getChangeType() != ChangeType.ADD) {
            placeRequestCustomLeft = createPlaceRequest(diff.getOldFilePath());
            placeManager.goTo(placeRequestCustomLeft,
                              view.getCustomLeftContainer());
        }

        if (diff.getChangeType() != ChangeType.DELETE) {
            placeRequestCustomRight = createPlaceRequest(diff.getNewFilePath());
            placeManager.goTo(placeRequestCustomRight,
                              view.getCustomRightContainer());
        }
    }

    private void closeVisualContent() {
        if (placeRequestCustomLeft != null) {
            view.clearCustomLeftContainer();
            placeManager.closePlace(placeRequestCustomLeft);
        }

        if (placeRequestCustomRight != null) {
            view.clearCustomRightContainer();
            placeManager.closePlace(placeRequestCustomRight);
        }
    }

    private void prepareView() {
        view.init(this);
    }

    private Map<String, String> createPathPlaceRequestParameters() {
        return new HashMap<String, String>() {{
            put("readOnly", "true");
            put("hiddenDocks", "true");
            put("embedded", "true");
            put("hash", generateRandomHash());
        }};
    }

    private String generateRandomHash() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 32;

        Random random = new Random();

        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    private void prepareTextualDiff(final ChangeRequestDiff diff,
                                    final String filename) {
        view.setupTextualContent(filename,
                                 resolveChangeTypeText(diff.getChangeType()),
                                 diff.isConflict());
    }

    private void prepareVisualDiff(final ChangeRequestDiff diff,
                                   final String filename) {
        view.setupCustomContent(filename,
                                resolveChangeTypeText(diff.getChangeType()),
                                diff.isConflict());

        if (diff.getChangeType() == ChangeType.ADD) {
            view.expandCustomRightContainer();
        } else if (diff.getChangeType() == ChangeType.DELETE) {
            view.expandCustomLeftContainer();
        }
    }

    private DiffMode resolveDiffMode(final ChangeRequestDiff diff) {
        final Path filePath = diff.getChangeType() == ChangeType.ADD ? diff.getNewFilePath() : diff.getOldFilePath();

        Optional<ResourceTypeDefinition> resourceTypeDefinition = resourceTypeManagerCache
                .getResourceTypeDefinitions()
                .stream()
                .filter(resource -> resource.accept(filePath))
                .findFirst();

        if (resourceTypeDefinition.isPresent()) {
            return resourceTypeDefinition.get().getDiffMode();
        }

        return DiffMode.TEXTUAL;
    }

    private String resolveDiffFilename(final ChangeType changeType,
                                       final String oldFilePath,
                                       final String newFilePath) {
        if (changeType == ChangeType.ADD) {
            return newFilePath;
        } else if (changeType == ChangeType.DELETE ||
                changeType == ChangeType.MODIFY) {
            return oldFilePath;
        } else { // COPY & RENAME
            return oldFilePath + " -> " + newFilePath;
        }
    }

    private String resolveChangeTypeText(final ChangeType changeType) {
        switch (changeType) {
            case ADD:
                return ts.getTranslation(LibraryConstants.Added);
            case DELETE:
                return ts.getTranslation(LibraryConstants.Deleted);
            case RENAME:
                return ts.getTranslation(LibraryConstants.Renamed);
            case COPY:
                return ts.getTranslation(LibraryConstants.Copied);
            case MODIFY:
            default:
                return ts.getTranslation(LibraryConstants.Updated);
        }
    }
}
