package es.javier.timerPlugin.configs;

public  class TimerConfig {
    private final int totalSeconds;

    public TimerConfig(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }
}