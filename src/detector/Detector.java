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
    private Sample firstSample  = null, temporalSample  = null;
    private float omegaRatio = 0, mse_temperature = 0, mse_humidity = 0, resultMass = 0;
    private String date, time_ini, time_fin;
    private boolean debug;
    private int node_id = 0, elapsedTime = 0, subAlerts = 0;
    private NumberFormat f1 = new DecimalFormat("#0.0000000000"), g1 = new DecimalFormat("#0.000");
    // Constants
    private static final int WINDOW_SIZE = 15;
    private static final int SUBALERTSMAXIMUM = 5;
    private static final float TEMPERATURE_THRESHOLD = (float) 1.01;

    public Detector(int node_id, String date, String time_ini, String time_fin, boolean debug) {
        W_temperature = new Queue(WINDOW_SIZE);
        // Query parameters
        this.node_id = node_id;
        this.date = date;
        this.time_ini = time_ini;
        this.time_fin = time_fin;
        this.debug = debug;
    }

    // Get Samples by Node ID
    public void checkSamples() {
        try {
            PreparedStatement ps = DatabaseConnection.getInstance()
                    .getConnection()
                    .prepareStatement(
                            "SELECT temperature, humidity, now " +
                                "FROM samples " +
                                "WHERE node_id = " + this.node_id + " " +
                                "AND temperature > 0 " +
                                "AND today = '" + this.date + "' " +
                                "AND now >= '" + this.time_ini + "' " +
                                "AND now < '" + this.time_fin + "' " +
                                "ORDER BY today ASC, now ASC;");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                float temperature = rs.getFloat("temperature");

                if (W_temperature.isFull()) {
                    boolean isHigherTemperature = checkRatio(temperature, W_temperature);

                    if (isHigherTemperature) {
                        float humidity = rs.getFloat("humidity");
                        Time timestamp = rs.getTime("now");

                        if (firstSample == null) {
                            firstSample = buildSample(node_id, temperature, humidity, timestamp);
                            // Print the data for debug
                            if (debug)
                                System.out.println(firstSample.toString());
                        } else {
                            Sample lastSample = buildSample(node_id, temperature, humidity, timestamp);
                            // Print the data for debug
                            if (debug)
                                System.out.println(lastSample.toString());
                            compareSamples(firstSample, lastSample);
                            // Update first sample
                            firstSample = lastSample;
                        }
                    }

                    W_temperature.pop(); // I delete old element in temperature's sliding window.
                    W_temperature.insert(temperature); // I insert new element in temperature's sliding window.
                } else {
                    W_temperature.insert(temperature); // I insert new element in temperature's sliding window.
                }
            }
        } catch (SQLException e) {
            // If another exception is generated, print a stack trace
            e.printStackTrace();
        }
    }

    public boolean checkRatio(float temperature, Queue queue) {
        return (temperature / (queue.getSum() / queue.getSize())) > TEMPERATURE_THRESHOLD;
    }

    public Sample buildSample(int nodeId, float temperature, float humidity, Time timestamp) {
        return new Sample(nodeId, temperature, humidity, timestamp);
    }

    public void compareSamples(Sample firstSample, Sample lastSample) {
        if (lastSample.getTemperature() > firstSample.getTemperature()) {
            if (temporalSample != null) {
                calculateMeanSquareError(temporalSample, lastSample);
            } else { // If not exist a temporal sample
                calculateMeanSquareError(firstSample, lastSample);
            }
        } else { // If the temperature not change
            if (temporalSample == null) {
                temporalSample = buildSample(node_id, firstSample.getTemperature(), firstSample.getHumidity(), firstSample.getNow());
                // Print the data for debug
                if (debug)
                    System.out.println(temporalSample.toString());
            }
        }
    }

    public float calculateMeanSquareError(Sample firstSample, Sample lastSample) {
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
        // It increment the sub-alerts number
        subAlerts++;

        if (subAlerts == SUBALERTSMAXIMUM){
            if(debug) { // For debug I print the data
                System.out.println("*****Before To Mass Assignment*****");
                System.out.println("mse_temperature/subAlerts = "+f1.format(mse_temperature/subAlerts)+"\tmse_humidity/subAlerts = "+f1.format(mse_humidity/subAlerts));
            }

            float temperature_mass = setPorcentualMass(mse_temperature / subAlerts);
            float humidity_mass = setPorcentualMass(mse_humidity / subAlerts);
            // It is assigned the general mass
            float total_mass = (temperature_mass + humidity_mass) / 2;
            // float total_mass = setMassGeneral(temperature_mass, humidity_mass, firstSample.getTemperature(), lastSample.getTemperature(), firstSample.getHumidity(), lastSample.getHumidity());
            if(debug) { // For debug I print the data
                System.out.println("*****After To Mass Assignment*****");
                System.out.println("Mass General = " + total_mass + "\tMass T = " + temperature_mass + "\tMass H = " + humidity_mass);
                System.out.println("**********************************");
            }

            // Clean variables
            mse_temperature = 0;
            mse_humidity = 0;
            subAlerts = 0;

            return total_mass;
        }

        return -1;
    }
    //Get time difference
    public int calculateTimeDifference(long sup, long inf) {
        return (int) ((sup - inf) / 1000);
    }
    //Get Slope
    public float getSlope(long diff, float y1, float y2){
        return ((y2 - y1) / diff );
    }
    // Calculate the derivate of Temperature base function
    public float getVariationTemperature(long elapsedTime) {
        return (float) ((-0.0000007431*Math.pow(elapsedTime, 2)) + (0.000224*elapsedTime) + 0.022441);
    }
    // Calculate the derivate of Humidity base function
    public float getVariationHumidity(long elapsedTime) {
        return (float) ((0.0000000678*Math.pow(elapsedTime, 2)) + (0.000034*elapsedTime) - 0.027444);
    }
    //Table of mass assignment
    public float setPorcentualMass(float result){

        if ((result >= 0.00) && (result < 0.01) ){
            return (float) 0.99;
        }

        if ((result >= 0.01) && (result < 0.02) ){
            return (float) 0.9;
        }

        if ((result >= 0.02) && (result < 0.03) ){
            return (float) 0.8;
        }

        if ((result >= 0.03) && (result < 0.04) ){
            return (float) 0.7;
        }

        if ((result >= 0.04) && (result < 0.05) ){
            return (float) 0.6;
        }

        if ((result >= 0.05) && (result < 0.06) ){
            return (float) 0.5;
        }

        if ((result >= 0.06) && (result < 0.07) ){
            return (float) 0.4;
        }

        if ((result >= 0.07) && (result < 0.08) ){
            return (float) 0.3;
        }

        if ((result >= 0.08) && (result < 0.09) ){
            return (float) 0.2;
        }

        if ((result >= 0.09) && (result < 0.1) ){
            return (float) 0.1;
        }

        if ((result >= 0.1) && (result < 1) ){
            return (float) 0.01;
        }

        return 0;
    }
}
