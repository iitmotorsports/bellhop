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
    private final Map<String, Action> actions = Map.of(
            "Under Editing,Waiting for Approval", Action.SUBMITTED_FOR_APPROVAL,
            "Under Change,Change Pending Approval", Action.SUBMITTED_FOR_APPROVAL,
            "Waiting for Approval,Under Editing", Action.EDITING_REQUIRED,
            "Change Pending Approval,Under Change", Action.EDITING_REQUIRED,
            "Approved,Under Change", Action.EDITING_REQUIRED,
            "Waiting for Approval,Approved", Action.APPROVED,
            "Under Editing,Approved", Action.APPROVED,
            "Change Pending Approval,Approved", Action.APPROVED
    );

    public static final String MSG_SUBMITTED_FOR_APPROVAL = ":vertical_traffic_light: _%s_ has submitted *%s* for approval. The file is awaiting review and feedback from a team lead.";
    public static final String MSG_APPROVAL = ":white_check_mark: _%s_ has approved *%s*.";
    public static final String MSG_EDITING_REQUIRED = ":warning: _%s_ has requested changes for *%s*.";

    public static Action getActionFromSourceAndDest(@NonNull String source, @NonNull String dest) {
        return actions.getOrDefault(source + "," + dest, Action.INVALID);
    }

    public static String getNotificationMessage(Notification notification) {
        String username = notification.getUsername();
        String filename = notification.getFilename();

        if (notification.getAction() == Action.SUBMITTED_FOR_APPROVAL) {
            return String.format(MSG_SUBMITTED_FOR_APPROVAL, username, filename);
        } else if (notification.getAction() == Action.APPROVED) {
            return String.format(MSG_APPROVAL, username, filename);
        } else if (notification.getAction() == Action.EDITING_REQUIRED) {
            return String.format(MSG_EDITING_REQUIRED, username, filename);
        }

        log.error("Failed to get notification message due to an unknown state action");
        return null;
    }
}
