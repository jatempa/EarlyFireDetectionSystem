package test;

import bean.Sample;
import detector.Detector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.Queue;

import java.sql.Time;

public class DetectorTest {
    private Detector detector;
    private Sample firstSample, lastSample;
    private String date = "2014-03-31", time_ini = "11:12:00", time_fin = "11:18:00";
    private boolean debug = false;

    @Before
    public void setUp() {
        detector = new Detector(1, date, time_ini, time_fin, debug);
        firstSample = new Sample();
        lastSample = new Sample();
    }

    @Test
    public void checkRatioTest() {
        float[] temperatures = new float[]{
                26.5f, 26.6f, 26.6f, 26.7f, 26.8f,
                26.9f, 27.0f, 27.2f, 27.5f, 27.7f,
                28.1f, 28.4f, 28.8f, 29.1f, 29.5f};

        Queue queue = new Queue(temperatures.length);

        for (int i = 0; i < temperatures.length; i++) {
            queue.insert(temperatures[i]);
        }

        Assert.assertTrue(detector.checkRatio(29.9f, queue));
    }

    @Test
    public void calculateTimeDifferenceTest() {
        firstSample.setNow(Time.valueOf("11:18:00"));
        lastSample.setNow(Time.valueOf("11:48:35"));

        int timeDifference = detector.calculateTimeDifference(lastSample.getNow().getTime(), firstSample.getNow().getTime());

        Assert.assertEquals(timeDifference, 1835);
    }

}
