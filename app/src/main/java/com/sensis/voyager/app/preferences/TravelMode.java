package com.sensis.voyager.app.preferences;

public enum TravelMode {
    DRIVING(0, "driving"), WALKING(1, "walking"), BIKING(2, "biking");

    private final int value;
    private final String name;

    TravelMode(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public static TravelMode fromInteger(int x) {
        switch (x) {
            case 0:
                return DRIVING;
            case 1:
                return WALKING;
            case 2:
                return BIKING;
        }
        return null;
    }
}
