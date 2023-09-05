package com.mixzing.musicobject;

public enum EnumSignatureProcessingStatus {
    UNKNOWN(-1),
    REQUESTED(0),
    ERRORED(1),
    DONE(2);

    private int intValue;

    EnumSignatureProcessingStatus (int intValue) {
        this.intValue = intValue;
    }

    public static EnumSignatureProcessingStatus fromIntValue(int intValue) {
        for (EnumSignatureProcessingStatus rtes: values()) {
            if (rtes.getIntValue() == intValue) {
                return rtes;
            }
        }
        return UNKNOWN;
    }

    public int getIntValue() {
        return intValue;
    }
}
