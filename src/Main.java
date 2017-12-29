import bean.Evidence;
import detector.Detector;

public class Main {
    private static final float ALPHA = (float) 0.7, BETHA = (float) 0.4;
    // D-S
    private static float[] mass = new float[3];

    public static void main(String[] args) {
        Detector n1 = new Detector(1, "2014-03-31", "11:12:00", "11:18:00",15, false);
        mass = checkMass(1, n1.getSamplesByNodeId(1));
        System.out.println("Evidence:\tF\t"+ mass[0] + "\tU\t" + mass[1] + "\tN\t" + mass[2]);
        Detector n2 = new Detector(2, "2014-03-31", "11:12:00", "11:18:00",15, false);
        mass = checkMass(2, n2.getSamplesByNodeId(2));
        System.out.println("Evidence:\tF\t"+ mass[0] + "\tU\t" + mass[1] + "\tN\t" + mass[2]);
        Detector n3 = new Detector(3, "2014-03-31", "11:12:00", "11:18:00",15, false);
        mass = checkMass(3, n3.getSamplesByNodeId(3));
        System.out.println("Evidence:\tF\t"+ mass[0] + "\tU\t" + mass[1] + "\tN\t" + mass[2]);
    }

    private static float[] checkMass(int nodeId, float total_mass) {
        float[] mass = new float[3];

        if (total_mass >= ALPHA) {
            System.out.println("FIRE\tNode ID " +  nodeId + "\tMass\t" + total_mass);
            mass[0] = total_mass;
            mass[1] = mass[2] = (1 - total_mass) / 2;
        } else if(total_mass > -1 && total_mass <= BETHA) {
            System.out.println("NO FIRE\tNode ID " +  nodeId + "\tMass\t" + total_mass);
            mass[2] = total_mass;
            mass[0] = mass[1] = (1 - total_mass) / 2;
        } else if (total_mass > -1) {
            System.out.println("UNKNOWN\tNode ID " +  nodeId + "\tMass\t" + total_mass);
            mass[1] = total_mass;
            mass[0] = mass[2] = (1 - total_mass) / 2;
        }

        return mass;
    }

    private static float dempsterShaferRule(float[] eventA, float[] eventB){
        float fusionResult = 0, fusion = 0, k = 0;

        fusion = (eventA[1]*eventB[1]) + (eventA[2]*eventB[1]) + (eventA[1]*eventB[2]);
        k = 1 - ((eventA[3]*eventB[1])+(eventA[1]*eventB[3]));
        fusionResult = (fusion/k);

        return fusionResult;
    }

}
