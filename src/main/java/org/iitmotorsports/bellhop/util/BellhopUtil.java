package org.iitmotorsports.bellhop.util;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.iitmotorsports.bellhop.model.Action;
import org.iitmotorsports.bellhop.model.Notification;

import java.util.Map;

@UtilityClass
@Slf4j
public class BellhopUtil {
    private static final Map<String, Action> actions = Map.of(
            "Under Editing,Waiting for Approval", Action.SUBMITTED_FOR_APPROVAL,
            "Under Change,Change Pending Approval", Action.SUBMITTED_FOR_APPROVAL,
            "Waiting for Approval,Under Editing", Action.EDITING_REQUIRED,
            "Change Pending Approval,Under Change", Action.EDITING_REQUIRED,
            "Approved,Under Change", Action.EDITING_REQUIRED,
            "Waiting for Approval,Approved", Action.APPROVED,
            "Under Editing,Approved", Action.APPROVED,
            "Change Pending Approval,Approved", Action.APPROVED
    );

    public static Action getActionFromSourceAndDest(@NonNull String source, @NonNull String dest) {
        return actions.getOrDefault(source + "," + dest, Action.INVALID);
    }
}
