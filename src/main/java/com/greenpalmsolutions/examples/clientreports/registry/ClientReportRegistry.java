package com.greenpalmsolutions.examples.clientreports.registry;

public enum ClientReportRegistry {

    FINANCE("finance"),
    MARKETING("marketing"),
    INFORMATION_TECHNOLOGY("information_technology");

    private final String id;

    ClientReportRegistry(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
