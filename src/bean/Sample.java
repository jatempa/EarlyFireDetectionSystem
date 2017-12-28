package bean;

import java.sql.Time;
import java.text.DecimalFormat;
import java.util.Date;

/**
 *
 * @author Jorge Antonio Atempa Camacho (atempaitm at gmail.com)
 */
public class Sample {
    private DecimalFormat f1 = new DecimalFormat("0.00");
    private int node_id;
    private Date today;
    private Time now;
    private float temperature;
    private float humidity;

    public Sample() {
        node_id = 0;
    }

    public int getNodeId() {
        return node_id;
    }

    public void setNodeId(int node_id) {
        this.node_id = node_id;
    }

    public Date getToday() {
        return today;
    }

    public void setToday(Date today) {
        this.today = today;
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
}
