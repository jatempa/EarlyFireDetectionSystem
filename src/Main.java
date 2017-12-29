import detector.Detector;

public class Main {
    private static final float ALPHA = (float) 0.7, BETHA = (float) 0.4;
    private static float total_mass = 0;

    public static void main(String[] args) {
        Detector n1 = new Detector(1, "2014-03-31", "10:40:00", "10:46:00",15, false);
        total_mass = n1.getSamplesByNodeId(1);
        checkMass(1, total_mass);
        Detector n2 = new Detector(2, "2014-03-31", "10:40:00", "10:46:00",15, false);
        total_mass = n2.getSamplesByNodeId(2);
        checkMass(2, total_mass);
        Detector n3 = new Detector(3, "2014-03-31", "10:40:00", "10:46:00",15, false);
        total_mass = n3.getSamplesByNodeId(3);
        checkMass(3, total_mass);
    }

    public static void checkMass(int nodeId, float total_mass) {
        if (total_mass >= ALPHA) {
            System.out.println("FIRE\tNode ID " +  nodeId + "\tMass\t" + total_mass);
        } else if(total_mass <= BETHA) {
            System.out.println("NO FIRE\tNode ID " +  nodeId + "\tMass\t" + total_mass);
        } else {
            System.out.println("UNKNOWN\tNode ID " +  nodeId + "\tMass\t" + total_mass);
        }
    }
}
