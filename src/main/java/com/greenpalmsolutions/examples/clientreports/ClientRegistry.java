package com.greenpalmsolutions.examples.clientreports;

public enum ClientRegistry {

    FINANCE("finance"),
    MARKETING("marketing"),
    INFORMATION_TECHNOLOGY("information_technology");

    private final String id;

    ClientRegistry(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
