import java.io.IOException;
import java.util.Arrays;

public class DeviceTest1 extends UserlandProcess {
    public void main() throws InterruptedException, IOException {

            int r1 = OS.Open("random 12345");
            int r2 = OS.Open("random");
            int r3 = OS.Open("random");

            byte[] data = OS.Read(r1, 100);
            System.out.println("r1:" + Arrays.toString(data));

            data = OS.Read(r2, 100);
            System.out.println("r2:" + Arrays.toString(data));

            data = OS.Read(r3, 100);
            System.out.println("r3:" + Arrays.toString(data));

            OS.Close(r1);
            OS.Close(r2);

            OS.Exit();

    }
}
