package com.example.employee.common.enumerate;

public enum EPriorityLevel {

    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH")
    ;

    private final String level;

    EPriorityLevel(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }

}
