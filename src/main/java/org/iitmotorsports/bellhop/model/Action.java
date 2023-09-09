package org.iitmotorsports.bellhop.model;

public enum Action {
    SUBMITTED_FOR_APPROVAL, APPROVED, EDITING_REQUIRED, INVALID;

    public boolean isInvalid() {
        return this == INVALID;
    }
}
