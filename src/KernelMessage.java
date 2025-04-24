public class KernelMessage {

    private final int senderPID;
    private final int targetPID;
    public final int messageType; //For this case a message type of 0 will be an int
    public final byte[] data;

    public KernelMessage(int senderPID, int targetPID, int messageType, byte[] data) {
        this.senderPID = senderPID;
        this.targetPID = targetPID;
        this.messageType = messageType;
        this.data = data;
    }

    public static KernelMessage copyConstructor (KernelMessage copy) {
        return new KernelMessage(copy.senderPID, copy.targetPID, copy.messageType, copy.data);
    }

    public int getSenderPID() {
        return senderPID;
    }

    public int getTargetPID() {
        return targetPID;
    }

    public String toString() {
        return "Sender PID: " + senderPID + "\nTarget PID: " + targetPID + "\nMessage Type: " + messageType;
    }

}
