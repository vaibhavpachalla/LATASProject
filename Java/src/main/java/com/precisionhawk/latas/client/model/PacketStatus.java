package com.precisionhawk.latas.client.model;

import com.google.gson.annotations.SerializedName;

public enum PacketStatus {

    @SerializedName("0")
    OK(0),

    @SerializedName("1")
    FIRST(1),

    @SerializedName("2")
    IFRAME(2),

    @SerializedName("3")
    LAST(3),

    @SerializedName("4")
    WARNING(4),

    @SerializedName("5")
    ALERT(5),

    @SerializedName("6")
    INVALID(6),

    @SerializedName("7")
    LASTWILL(7);

    private final int value;

    PacketStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
