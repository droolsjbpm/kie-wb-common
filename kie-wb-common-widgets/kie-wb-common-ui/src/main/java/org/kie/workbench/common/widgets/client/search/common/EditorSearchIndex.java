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

package org.kie.workbench.common.widgets.client.search.common;

import java.util.List;
import java.util.function.Supplier;

import org.uberfire.mvp.Command;

/**
 * {@link EditorSearchIndex} holds the search logic.
 * ---
 * The {@link EditorSearchIndex} can have many sub-indexes that index different kinds of elements. Each sub-index, or
 * {@link HasSearchableElements}, is naive and will load its searchable elements for every call.
 * Thus, implementations of this interface control lazy approaches, cached approaches, or even the use of a third party
 * library.
 * ---
 * @param <T> represents the type of {@link Searchable} element.
 */
public interface EditorSearchIndex<T extends Searchable> {

    /**
     * Returns the list of sub-indexes.
     * @return the list of {@link HasSearchableElements}.
     */
    List<HasSearchableElements<T>> getSubIndexes();

    /**
     * Registers a new sub-index into {@link EditorSearchIndex}.
     * @param hasSearchableElements represents a new index.
     */
    void registerSubIndex(final HasSearchableElements<T> hasSearchableElements);

    /**
     * Searches for any {@link Searchable} that satisfies the parameter, to finally trigger the <code>onFound</code>
     * callback.
     * @param term the string that will trigger the search
     */
    void search(final String term);

    /**
     * Sets the callback that will be triggered when no result is found.
     * @param callback the callback that will be triggered
     */
    void setNoResultsFoundCallback(final Command callback);

    /**
     * Sets the <code>isDirty</code> logic.
     * @param isDirtySupplier represents the <code>isDirty</code> logic.
     */
    void setIsDirtySupplier(final Supplier<Boolean> isDirtySupplier);

    /**
     * Check if the index is dirty.
     * @return true if the index is dirty.
     */
    boolean isDirty();
}
