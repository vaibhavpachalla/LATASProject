package com.precisionhawk.latas.client.stats;

class MessageStats<T> {

    private final T message;
    private final long timestamp = System.currentTimeMillis();
    private long elapsed;

    MessageStats(T message) {
        this.message = message;
    }

    T getMessage() {
        return message;
    }

    long getTimestamp() {
        return timestamp;
    }

    long getElapsed() {
        return elapsed;
    }

    void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageStats that = (MessageStats) o;
        return timestamp == that.timestamp && message.equals(that.message);
    }

    @Override
    public int hashCode() {
        int result = message.hashCode();
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }
}
