import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DeviceTest2 extends UserlandProcess {
    public void main() throws InterruptedException, IOException {

            int f1 = OS.Open("file Documents");
            int f2 = OS.Open("file Essay");
            int f3 = OS.Open("file Program");

            String temp =  "Hello World";
            byte[] data = temp.getBytes(StandardCharsets.UTF_8);
            OS.Write(f1, data);

            temp = "Introduction";
            data = temp.getBytes(StandardCharsets.UTF_8);
            OS.Write(f2, data);

            temp = "public class HelloWorld () {}";
            data = temp.getBytes(StandardCharsets.UTF_8);
            OS.Write(f3, data);

            byte[] results = OS.Read(f1, 100);
            String output = new String(results, StandardCharsets.UTF_8);
            System.out.println("Documents output: " + output);

            results = OS.Read(f2, 100);
            output = new String(results, StandardCharsets.UTF_8);
            System.out.println("Essay output: " + output);

            results = OS.Read(f3, 100);
            output =  new String(results, StandardCharsets.UTF_8);
            System.out.println("Program output: " + output);

            OS.Close(f1);
            OS.Close(f2);

            OS.Exit();

    }
}
