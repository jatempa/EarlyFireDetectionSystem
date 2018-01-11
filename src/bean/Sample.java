package bean;

import java.sql.Time;
import java.text.DecimalFormat;

/**
 *
 * @author Jorge Antonio Atempa Camacho (atempaitm at gmail.com)
 */
public class Sample {
    private DecimalFormat f1 = new DecimalFormat("0.00");
    private int node_id;
    private float temperature;
    private float humidity;
    private Time now;

    public Sample() {
    }

    public Sample(int node_id, float temperature, float humidity, Time now) {
        this.node_id = node_id;
        this.temperature = temperature;
        this.humidity = humidity;
        this.now = now;
    }

    public int getNodeId() {
        return node_id;
    }

    public void setNodeId(int node_id) {
        this.node_id = node_id;
    }

    public Time getNow() {
        return now;
    }

    public void setNow(Time now) {
        this.now = now;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = Float.parseFloat(f1.format(humidity));
    }

    @Override
    public String toString() {
        return "Sample{node_id=" + node_id +
                ", now=" + now +
                ", temperature=" + temperature +
                ", humidity=" + humidity +
                '}';
    }
}
