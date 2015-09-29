package com.sensis.voyager.app.preferences;

public enum UnitOption {
    DEFAULT(0, null), KILOMETER(1, "metric"), MILE(2, "imperial");

    private final int value;
    private final String name;

    UnitOption(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return this.value;
    }

    public String getName() {
        return name;
    }

    public static UnitOption fromInteger(int x) {
        switch (x) {
            case 0:
                return DEFAULT;
            case 1:
                return KILOMETER;
            case 2:
                return MILE;
        }
        return null;
    }
}
