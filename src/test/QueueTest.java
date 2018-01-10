package test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.Queue;

public class QueueTest {
    private Queue queue;
    private final static int SIZE = 15;
    private float[] temperatures = new float[]{
            26.5f, 26.6f, 26.6f, 26.7f, 26.8f, 26.9f, 27.0f, 27.2f, 27.5f, 27.7f,
            28.1f, 28.4f, 28.8f, 29.1f, 29.5f, 29.9f, 30.2f, 30.6f, 31.0f, 31.3f,
            31.7f, 32.0f, 32.3f, 32.7f, 33.0f, 33.2f, 33.5f, 33.8f, 34.1f, 34.5f,
            34.8f, 35.1f, 35.5f, 35.8f, 36.1f, 36.5f, 36.8f, 37.1f, 37.4f, 37.7f,
            38.1f, 38.5f, 38.9f, 39.3f, 39.7f, 40.1f, 40.5f, 40.8f, 41.1f, 41.4f,
            41.6f, 41.9f, 42.2f, 42.5f, 42.7f, 43.0f, 43.2f, 43.5f, 43.7f, 43.9f};

    @Before
    public void setUp() {
        queue = new Queue(SIZE);
    }

    @Test
    public void testIsNotNull() {
        Assert.assertNotNull(queue);
    }

    @Test
    public void testSize() {
        Assert.assertTrue(queue.getSize() == SIZE);
    }

    @Test
    public void testSum() {
        for (int i = 0; i < SIZE; i++) {
            queue.insert(temperatures[i]);
        }

        Assert.assertEquals(queue.getSum(),413.4f,0);
    }

    @Test
    public void testCheckIsFull() {
        for (int i = 0; i < SIZE; i++) {
            queue.insert(temperatures[i]);
        }

        Assert.assertTrue(queue.isFull());
    }

    @Test
    public void testPopElementAndCheckSum() {
        for (int i = 0; i < SIZE; i++) {
            queue.insert(temperatures[i]);
        }
        // Delete a element
        queue.pop();

        Assert.assertEquals(queue.getSum(),386.9f,0);
    }

    @Test
    public void testInsertElementAndCheckSum() {
        for (int i = 0; i < SIZE; i++) {
            queue.insert(temperatures[i]);
        }
        // Delete two elements
        queue.pop();
        queue.pop();
        queue.insert(temperatures[SIZE+1]);

        Assert.assertEquals(queue.getSum(),390.6f,0);
    }
}