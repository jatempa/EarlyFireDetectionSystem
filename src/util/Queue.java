package util;

public class Queue {
    private int size = 0, index = 0, front = 0;
    private float sliding_window[], sum = 0, firstElement = 0;
    private boolean firstElementlock = false;

    public Queue(int size) {
        this.size = size;
        sliding_window = new float[this.size];
    }

    public boolean isFull(){
        if (index == size)
            return true;

        return false;
    }

    public void insert(float item) {
        int position = (front + index) % size;
        sliding_window[position] = item;
        if (!firstElementlock) {
            firstElement = sliding_window[front];
            sum += item;
            firstElementlock = true;
        } else {
            sum += item;
        }

        index++;
    }

    public void pop() {
        front = (front + 1) % size;
        sum -= firstElement;
        firstElementlock = false;
        index--;
    }

    public float getSum() {
        return sum;
    }

    public int getSize() {
        return size;
    }
}

