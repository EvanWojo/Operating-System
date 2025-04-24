import java.io.IOException;

public class PagingTest extends UserlandProcess {
    public void main() throws InterruptedException, IOException {

        for (int i = 0; i < 10; i++) { //Simple looping test
            int pointer = OS.AllocateMemory(1024);
            byte value = (byte) i;
            Hardware.Write(pointer, value);
            System.out.println("Page test 1: " + Hardware.Read(pointer));
        }
        OS.FreeMemory(0, (1024 * 10));

        OS.Sleep(1000);
        cooperate();

        //Test to check if allocate memory can effectively fill holes in memory
        int pointer = OS.AllocateMemory(1024 * 10);
        for (int i = 0; i < 10; i++) {
            Hardware.Write(pointer + (i * 1024), (byte) i);
        }
        OS.FreeMemory(pointer * 2, 1024);
        OS.FreeMemory(pointer * 4, 1024);

        OS.Sleep(1000);
        cooperate();

        //Test to catch SEGFAULT (Corresponds with PagingTest2)
//        int pointer = OS.AllocateMemory(1024);
//        Hardware.Write(pointer, (byte) 5);
//        cooperate();


        OS.Exit();
    }
}
