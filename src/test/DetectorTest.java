package test;

import bean.Sample;
import detector.Detector;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import util.Queue;

import java.sql.Time;
import java.text.DecimalFormat;

public class DetectorTest {
    private Detector detector;
    private Sample firstSample, lastSample;
    private String date = "2014-03-31", time_ini = "11:12:00", time_fin = "11:18:00";
    private boolean debug = false;
    private DecimalFormat f1 = new DecimalFormat("#0.00");

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

    @Test
    public void calculateDerivateTest() {
        float result = detector.calculateDerivate(12, 'T');

        Assert.assertEquals(0.02502199448645115, result,0);

        result = detector.calculateDerivate(12, 'H');

        Assert.assertEquals(-0.027026236057281494, result,0);
    }

    @Test
    public void porcentualMassTest() {
        float result = detector.setPorcentualMass((float) 0.00000003);

        Assert.assertEquals( 99, result, 0);

        result = detector.setPorcentualMass((float) 0.01000003);

        Assert.assertEquals( 90, result, 0);

        result = detector.setPorcentualMass((float) 0.02000003);

        Assert.assertEquals( 80, result, 0);

        result = detector.setPorcentualMass((float) 0.03000003);

        Assert.assertEquals( 70, result, 0);

        result = detector.setPorcentualMass((float) 0.04000003);

        Assert.assertEquals( 60, result, 0);

        result = detector.setPorcentualMass((float) 0.05000003);

        Assert.assertEquals( 50, result, 0);

        result = detector.setPorcentualMass((float) 0.06000003);

        Assert.assertEquals( 40, result, 0);

        result = detector.setPorcentualMass((float) 0.07000003);

        Assert.assertEquals( 30, result, 0);

        result = detector.setPorcentualMass((float) 0.08000003);

        Assert.assertEquals( 20, result, 0);

        result = detector.setPorcentualMass((float) 0.09000003);

        Assert.assertEquals( 10, result, 0);

        result = detector.setPorcentualMass((float) 0.10000003);

        Assert.assertEquals( 1, result, 0);
    }
}
