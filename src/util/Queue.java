package util;

public class Queue {
    private int index = 0;
    private int max = 0;
    private int front = 0;
    private float sliding_window[];
    // Ratios of slide window
    private float sum = 0;
    private boolean firstElementlock = false;
    private float firstElement;

    public Queue(int size) {
        max = size;
        sliding_window = new float[max];
    }

    public boolean isFull(){
        if (index == max)
            return true;
        else
            return false;
    }

    public void insertQ(float item) {
        int position = (front + index) % max;
        sliding_window[position] = item;
        if (!firstElementlock) {
            firstElement = sliding_window[front];
            sum = sum + item;
            firstElementlock = true;
        } else {
            sum = sum + item;
        }

        index++;
    }

    public void popQ() {
        front = (front + 1) % max;
        sum = sum - firstElement;
        firstElementlock = false;
        index--;
    }

    public float sumQ() {
        return sum;
    }
}

