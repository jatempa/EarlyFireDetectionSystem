package detector;

import bean.Sample;
import util.DatabaseConnection;
import util.Queue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Detector {
    private Queue W_temperature; // sliding window
    private Sample firstSample  = null, lastSample  = null, temporalSample  = null;
    private float omegaRatio = 0, mse_temperature = 0, mse_humidity = 0;
    private static final float TEMPERATURE_THRESHOLD = (float) 1.01;
    private String date, time_ini, time_fin;
    private boolean debug = true, isFirstSample = false, isTemporalSample = false;
    private int windowSize = 0, elapsedTime = 0;
    private NumberFormat f1 = new DecimalFormat("#0.0000000000"), g1 = new DecimalFormat("#0.000");

    public Detector(int node_id, String date, String time_ini, String time_fin, int windowSize) {
        W_temperature = new Queue(windowSize);
        // Query parameters
        this.date = date;
        this.time_ini = time_ini;
        this.time_fin = time_fin;
        this.windowSize = windowSize;
        // Execute
        getSamplesByNodeId(node_id);
    }

    private void getSamplesByNodeId(int node_id) {
        try {
            // Get Samples
            PreparedStatement ps = DatabaseConnection.getInstance()
                                                     .getConnection()
                                                     .prepareStatement("SELECT * FROM samples " +
                                                                           "WHERE node_id = " + node_id + " " +
                                                                           "AND temperature > 0 " +
                                                                           "AND today = '" + this.date + "' " +
                                                                           "AND now >= '" + this.time_ini + "' " +
                                                                           "AND now < '" + this.time_fin + "' " +
                                                                           "ORDER BY today ASC, now ASC;");
            ResultSet rs = ps.executeQuery();
            System.out.println("Start !!!");
            while (rs.next()) {
                float rcvdTemperature = rs.getFloat("temperature");
                float rcvdHumidity = rs.getFloat("humidity");
                Time rcvdTimestamp = rs.getTime("now");
                // Print the data for debug
                // if (debug) System.out.println("Node " + node_id + "\t" + rcvdTimestamp + "\tT\t" + rcvdTemperature + "\tH\t" + rcvdHumidity);
                if (W_temperature.isFull()) {
                    omegaRatio = (rcvdTemperature / (W_temperature.sumQ() / windowSize));// Calculate Temperature ratio
                    if (omegaRatio > TEMPERATURE_THRESHOLD) { // Comparing Temperature ratio
                        if (!isFirstSample) {
                            firstSample = buildSample(node_id, rcvdTemperature, rcvdHumidity, rcvdTimestamp);
                            isFirstSample = true;
                            // Print the data for debug
                            if (debug)
                                System.out.println("Fst Sample Node " + firstSample.getNodeId() + "\t" + firstSample.getNow() + "\tT\t" + firstSample.getTemperature() + "\tH\t" + firstSample.getHumidity());
                        } else {
                            lastSample = buildSample(node_id, rcvdTemperature, rcvdHumidity, rcvdTimestamp);
                            // Print the data for debug
                            if (debug)
                                System.out.println("Lst Sample Node " + lastSample.getNodeId() + "\t" + lastSample.getNow() + "\tT\t" + lastSample.getTemperature() + "\tH\t" + lastSample.getHumidity());

                            if (lastSample.getTemperature() > firstSample.getTemperature()) {
                                if (isTemporalSample) {
                                    checkSamples(temporalSample, lastSample);
                                    // Clean variables
                                    isTemporalSample = false;
                                    temporalSample = null;
                                } else { // If not exist a temporal sample
                                    checkSamples(firstSample, lastSample);
                                }
                            } else { // If the temperature not change
                                if (!isTemporalSample) {
                                    temporalSample = buildSample(node_id, firstSample.getTemperature(), firstSample.getHumidity(), firstSample.getNow());
                                    isTemporalSample = true;
                                    // Print the data for debug
                                    if (debug)
                                        System.out.println("Tmp Sample Node " + temporalSample.getNodeId() + "\t" + temporalSample.getNow() + "\tT\t" + temporalSample.getTemperature() + "\tH\t" + temporalSample.getHumidity());
                                }
                            }
                            // Update first sample
                            firstSample = lastSample;
                        }
                    }

                    W_temperature.popQ(); // I delete old element in temperature's sliding window.
                    W_temperature.insertQ(rcvdTemperature); // I insert new element in temperature's sliding window.
                } else {
                    W_temperature.insertQ(rcvdTemperature); // I insert new element in temperature's sliding window.
                }
            }
        } catch (SQLException e) {
            // If another exception is generated, print a stack trace
            e.printStackTrace();
        }
    }

    private Sample buildSample(int nodeId, float temperature, float humidity, Time timestamp) {
        Sample sample = new Sample();
        sample.setNodeId(nodeId);
        sample.setTemperature(temperature);
        sample.setHumidity(humidity);
        sample.setNow(timestamp);

        return sample;
    }

    private void checkSamples(Sample firstSample, Sample lastSample) {
        // Calculate the time difference
        int timeDifference = calculateTimeDifference(lastSample.getNow().getTime(), firstSample.getNow().getTime());
        elapsedTime += timeDifference; // Increments the elapsed time
        // TEMPERATURE
        float vSlpT = getSlope(timeDifference, firstSample.getTemperature(), lastSample.getTemperature());// Get the temperature slope
        float vRgrT = getVariationTemperature(elapsedTime);
        // HUMIDITY
        float vSlpH = getSlope(timeDifference, firstSample.getHumidity(), lastSample.getHumidity());// Get the humidity slope
        float vRgrH = getVariationHumidity(elapsedTime);// Get the slope of the function based on humidity
        // Calculate and accumulate the temperature squared difference
        mse_temperature += (float) Math.pow((vRgrT-vSlpT),2);
        // Calculate and accumulate the humidity squared difference
        mse_humidity += (float) Math.pow((vRgrH-vSlpH),2);

        if (debug) { // Print the data for debug
            System.out.println("Evaluación");
            System.out.println("Omega " + omegaRatio + " > " + TEMPERATURE_THRESHOLD);
            System.out.println("Time\tvTRgr\tvTSlp\tDiff\tDiff^2\tvHRgr\tvHSlp\tDiff\tDiff^2\tMSE_Temperature\t\tMSE_Humidity");
            System.out.println(elapsedTime + "\t\t" +
                    g1.format(vRgrT) + "\t" + g1.format(vSlpT) + "\t" + g1.format(vRgrT -vSlpT) + "\t" + g1.format(Math.pow((vRgrT -vSlpT), 2)) + "\t" +
                    g1.format(vRgrH) + "\t" + g1.format(vSlpH) + "\t" + g1.format(vRgrH-vSlpH) + "\t" + g1.format(Math.pow((vRgrH-vSlpH), 2)) + "\t" +
                    f1.format(mse_temperature) + "\t\t" + f1.format(mse_humidity));
        }
    }
    //Get time difference
    private int calculateTimeDifference(long sup, long inf) {
        return (int) ((sup - inf) / 1000);
    }
    //Get Slope
    private float getSlope(long diff, float y1, float y2){
        return ((y2 - y1) / diff );
    }
    // Calculate the derivate of Temperature base function
    private float getVariationTemperature(long elapsedTime) {
        // March 2014
        //T = (float) ((-0.0000002477*Math.pow(elapsedTime, 3)) + (0.000112*Math.pow(elapsedTime, 2)) + (0.022441*elapsedTime) + 28.8861);
        return (float) ((-0.0000007431*Math.pow(elapsedTime, 2)) + (0.000224*elapsedTime) + 0.022441);
    }
    // Calculate the derivate of Humidity base function
    private float getVariationHumidity(long elapsedTime) {
        //March 2014
        //H = (float) ((0.0000000226*Math.pow(elapsedTime, 3)) + (0.000017*Math.pow(elapsedTime, 2)) - (0.027444*elapsedTime) + 17.505);
        return (float) ((0.0000000678*Math.pow(elapsedTime, 2)) + (0.000034*elapsedTime) - 0.027444);
    }
}

