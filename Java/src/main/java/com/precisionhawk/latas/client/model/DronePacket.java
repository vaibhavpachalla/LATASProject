package com.precisionhawk.latas.client.model;

public class DronePacket extends Drone {

    private int id;
    private long sequence;
    private PacketStatus status;

    public DronePacket(Drone drone) {
        super(drone);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public void setStatus(PacketStatus status) {
        this.status = status;
    }

    public PacketStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DronePacket that = (DronePacket) o;
        return id == that.id && sequence == that.sequence;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (int) (sequence ^ (sequence >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "DronePacket{" +
            "id=" + id +
            ", sequence=" + sequence +
            ", status=" + status +
            ", lat=" + getLat() +
            ", lon=" + getLon() +
            ", alt=" + getAlt() +
            ", track=" + getTrack() +
            ", speed=" + getSpeed() +
            ", battery=" + getBattery() +
            '}';
    }
}
