import java.io.IOException;

public class Pong extends UserlandProcess {

    public void main() throws InterruptedException, IOException {

        int targetPID = OS.GetPidByName("Ping");
        byte[] data = new byte[4];


        System.out.println("I am PONG, ping = " + targetPID);

        for (int i = 0; i < 100; i++) {

            data[0] = (byte) (i);
            OS.SendMessage(new KernelMessage(OS.GetPID(), targetPID, 0, data));
            KernelMessage km = OS.WaitForMessage();
            if (km != null) {
                System.out.println("PING OK");
            }
            cooperate();
            if (km != null)
                System.out.println("PING: from: " + OS.GetPID() + " to " + targetPID + ". What: " + data[0]);
            else {
                System.out.println("Error from PONG: Message not received");
            }
            km = null;
            Thread.sleep(500);
        }
        OS.Exit();
    }
}
