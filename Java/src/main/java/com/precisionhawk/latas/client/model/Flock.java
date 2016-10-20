package com.precisionhawk.latas.client.model;

public class Flock extends Drone {

    private double separation;
    private int size;

    public double getSeparation() {
        return separation;
    }

    public void setSeparation(double separation) {
        this.separation = separation;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
