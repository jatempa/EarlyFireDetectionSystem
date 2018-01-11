import detector.Detector;

public class Main {
    private static final float ALPHA = (float) 0.7, BETHA = (float) 0.4;

    public static void main(String[] args) {

        if (args.length < 3) {
            System.err.println("usage: java -jar Analysis.jar date time_initial time_final");
            System.exit(1);
        }

//        System.out.println("Start !!!");
//        Detector n1 = new Detector(1, args[0], args[1], args[2],15, false);
//        float[] evidenceA = checkMass(1, n1.getSamplesByNodeId(1));
//        Detector n2 = new Detector(2, args[0], args[1], args[2],15, false);
//        float[] evidenceB = checkMass(2, n2.getSamplesByNodeId(2));
//        Detector n3 = new Detector(3, args[0], args[1], args[2],15, false);
//        float[] evidenceC = checkMass(3, n3.getSamplesByNodeId(3));
//
//        System.out.println("Node ID 1 Evidence:\tF\t"+ evidenceA[0] + "\tU\t" + evidenceA[1] + "\tN\t" + evidenceA[2]);
//        System.out.println("Node ID 2 Evidence:\tF\t"+ evidenceB[0] + "\tU\t" + evidenceB[1] + "\tN\t" + evidenceB[2]);
//        System.out.println("Node ID 3 Evidence:\tF\t"+ evidenceC[0] + "\tU\t" + evidenceC[1] + "\tN\t" + evidenceC[2]);
//
//        float resultDS = fusionEvidence(evidenceA, evidenceB, evidenceC);
//
//        if (resultDS >= 0.7){
//            System.out.println("Fire event " + args[0] + "\t" + args[1] + "-" + args[2] + "\tMassDS\t" + resultDS);
//        } else {
//            System.out.println("No fire event " + args[0] + "\t" + args[1] + "-" + args[2] + "\tMassDS\t" + resultDS);
//        }
    }

    private static float[] checkMass(int nodeId, float total_mass) {
        float[] mass = new float[3];
//        boolean debug = false;

        if (total_mass >= ALPHA) {
//            if (debug) System.out.println("FIRE\tNode ID " +  nodeId + "\tMass\t" + total_mass);
            mass[0] = total_mass;
            mass[1] = mass[2] = (1 - total_mass) / 2;
        } else if(total_mass > -1 && total_mass <= BETHA) {
//            if (debug) System.out.println("NO FIRE\tNode ID " +  nodeId + "\tMass\t" + total_mass);
            mass[2] = total_mass;
            mass[0] = mass[1] = (1 - total_mass) / 2;
        } else if (total_mass > -1) {
//            if (debug) System.out.println("UNKNOWN\tNode ID " +  nodeId + "\tMass\t" + total_mass);
            mass[1] = total_mass;
            mass[0] = mass[2] = (1 - total_mass) / 2;
        }

        return mass;
    }

    private static float fusionEvidence(float[] evidenceA, float[] evidenceB, float[] evidenceC) {
        return DempsterShaferRule(getMax(evidenceA, evidenceB), evidenceC);
    }

    private static float DempsterShaferRule(float[] eventA, float[] eventB){
        float fusionResult = 0, fusion, k;

        if (eventA != null) {
            fusion = (eventA[0]*eventB[0]) + (eventA[1]*eventB[0]) + (eventA[0]*eventB[1]);
            k = 1 - ((eventA[2]*eventB[0])+(eventA[0]*eventB[2]));
            fusionResult = (fusion/k);

            return fusionResult;
        }

        return fusionResult;
    }

    private static float[] getMax(float[] evidenceA, float[] evidenceB){

        if (evidenceA[0] >= evidenceB[0])
            return evidenceA;
        else if(evidenceB[0] > evidenceA[0])
            return evidenceB;
        else if(evidenceA[0] > evidenceB[1])
            return evidenceA;
        else if(evidenceB[0] > evidenceA[1])
            return evidenceB;

        return null;
    }
}
