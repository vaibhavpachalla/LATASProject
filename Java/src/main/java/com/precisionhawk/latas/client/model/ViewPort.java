package com.precisionhawk.latas.client.model;

public class ViewPort {

    private final Location topRight;
    private final Location bottomLeft;

    public ViewPort(Location bottomLeft, Location topRight) {
        this.bottomLeft = bottomLeft;
        this.topRight = topRight;
    }

    public Location getTopRight() {
        return topRight;
    }

    public Location getBottomLeft() {
        return bottomLeft;
    }

    @Override
    public String toString() {
        return "ViewPort{" +
            "topRight=" + topRight +
            ", bottomLeft=" + bottomLeft +
            '}';
    }
}
