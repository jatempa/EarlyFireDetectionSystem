package bean;

public class Evidence {
    private int nodeId;
    private float fireEvent;
    private float noFireEvent;
    private float unknownEvent;

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public float getFireEvent() {
        return fireEvent;
    }

    public void setFireEvent(float fireEvent) {
        this.fireEvent = fireEvent;
    }

    public float getNoFireEvent() {
        return noFireEvent;
    }

    public void setNoFireEvent(float noFireEvent) {
        this.noFireEvent = noFireEvent;
    }

    public float getUnknownEvent() {
        return unknownEvent;
    }

    public void setUnknownEvent(float unknownEvent) {
        this.unknownEvent = unknownEvent;
    }
}
