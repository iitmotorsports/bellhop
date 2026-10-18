package org.iitmotorsports.bellhop.config;

import java.util.List;
import java.util.Map;

public record BellhopConfig(int smtpPort, List<EndpointConfig> endpoints) {

    public record EndpointConfig(String type, boolean enabled, Map<String, String> config) {}
}
