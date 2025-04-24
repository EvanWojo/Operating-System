
import java.io.IOException;

public class HelloWorld extends UserlandProcess {

    public void main() throws InterruptedException, IOException {

        while (true) {

            System.out.println("Hello World");

            Thread.sleep(1000);
            OS.Sleep(100);
            cooperate();
        }

    }
}
