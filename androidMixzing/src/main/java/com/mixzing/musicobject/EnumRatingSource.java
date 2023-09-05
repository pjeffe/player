package com.mixzing.musicobject;

public enum EnumRatingSource {
    UNKNOWN(0),
    INFERRED_ADD(1),
    INFERRED_REMOVE(2),
    USER_SELECTION(3),
    INFERRED_RATING_TAG(4),
    INFERRRED_PLAY_COUNT(5),
    FROM_HATE_ARTIST(6),
    FROM_IGNORE_ARTIST(7),
    INFERRED_LISTEN(8);

    private int intValue;

    EnumRatingSource (int intValue) {
        this.intValue = intValue;
    }

    public static EnumRatingSource fromIntValue(int intValue) {
        for (EnumRatingSource rtes: values()) {
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
