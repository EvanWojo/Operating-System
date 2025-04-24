import java.io.IOException;

public class PagingTest2 extends UserlandProcess {
    public void main() throws InterruptedException, IOException {

        //Test multiple processes using memory
        int pointer = OS.AllocateMemory(1024 * 5);
        for (int i = 0; i < 5; i++) {
            Hardware.Write(pointer + (i * 1024), (byte) i);
            System.out.println("Page test 2: " + Hardware.Read(pointer + (i * 1024)));
        }
        OS.FreeMemory(pointer, 1024 * 5);

        OS.Sleep(1000);
        cooperate();

        //Test to fill page completely with data, then read it back, and access said data at any point in page.
        pointer = OS.AllocateMemory(1024 * 5);
        for (int i = 0; i < 1024 * 5 - 1; i++) {
            Hardware.Write(pointer + i, (byte) (i % 128));
        }
        //Prints every value this wrote in memory, commented out for testing visibility
//        for (int i = 0; i < 1024 * 5 - 1; i++) {
//            System.out.println("Page test 3: " + Hardware.Read(pointer + i));
//        }
        //Accessing a specific point
        System.out.println("Value at: " + pointer + 2143 + ": " + Hardware.Read(pointer + 2143));
        OS.FreeMemory(pointer, 1024 * 5);

        OS.Sleep(1000);
        cooperate();

        //Test for SEGFAULT
//        Hardware.Write(0, (byte) 0);
//        Hardware.Read(0);
//        cooperate();

        OS.Exit();
    }
}
