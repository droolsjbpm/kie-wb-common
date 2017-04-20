/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.library.api.preferences;

import org.uberfire.preferences.shared.annotations.Property;
import org.uberfire.preferences.shared.annotations.WorkbenchPreference;
import org.uberfire.preferences.shared.bean.BasePreference;

@WorkbenchPreference(identifier = "LibraryProjectPreferences",
        bundleKey = "LibraryProjectPreferences.Label")
public class LibraryProjectPreferences implements BasePreference<LibraryProjectPreferences> {

    @Property(bundleKey = "LibraryProjectPreferences.GroupId")
    String groupId;

    @Property(bundleKey = "LibraryProjectPreferences.Version")
    String version;

    @Property(bundleKey = "LibraryProjectPreferences.Description")
    String description;

    @Property(bundleKey = "LibraryProjectPreferences.Branch")
    String branch;

    public String getGroupId() {
        return groupId;
    }

    public String getVersion() {
        return version;
    }

    public String getDescription() {
        return description;
    }

    public String getBranch() {
        return branch;
    }
}
