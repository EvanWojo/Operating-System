
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class GoodbyeWorld extends UserlandProcess {

    public void main() throws InterruptedException, IOException {

        while (true) {

            System.out.println("Goodbye world");

            Thread.sleep(1000);
            OS.Sleep(100);
            cooperate();
        }

    }
}
