package org.kie.workbench.common.profile.api.preferences;

import java.util.Arrays;
import java.util.List;

public enum Profile {
    
    PLANNER_AND_RULES("Planner and Rules",
            Arrays.asList(
                "wb_entry_pages",
                "wb_entry_process_definitions",
                "wb_entry_process_instances",
                "wb_entry_task_administration",
                "wb_entry_jobs",
                "wb_execution_errors",
                "wb_entry_tasks_list",
                "wb_entry_process_dashboard",
                "wb_entry_task_dashboard",
                // groups go last
                "wb_group_manage",
                "wb_group_track"
            )), 
    FULL("Full",
            Arrays.asList(""));

    private String profileName;
    private List<String> menuBlackList;
    
    private Profile(String name, List<String> menuBlackList) {
        this.profileName = name;
        this.menuBlackList = menuBlackList;
    }
    
    public List<String> getMenuBlackList() {
        return menuBlackList;
    }
    
    @Override
    public String toString() {
        return this.profileName;
    }
    
    public String getName() {
        return this.profileName;
    }
    
    public static Profile withName(String name) {
        return Arrays.stream(Profile.values())
              .filter(p -> p.getName().equals(name))
              .findFirst().orElse(Profile.FULL);
    }

}
