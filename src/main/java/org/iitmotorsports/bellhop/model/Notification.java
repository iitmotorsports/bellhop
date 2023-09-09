package org.iitmotorsports.bellhop.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Notification {
    private final String username;
    private final String filename;
    private final Action action;
}
