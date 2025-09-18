package es.javier.timerPlugin.data;

public class TimerData {
    private int timeLeft;

    public TimerData(int initialTime) {
        this.timeLeft = initialTime;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void decrement() {
        if (timeLeft > 0) timeLeft--;
    }
}