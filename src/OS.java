import java.util.ArrayList;
import java.util.List;

public class OS {
    private static Kernel ki; // The one and only one instance of the kernel.

    public static List<Object> parameters = new ArrayList<>();
    public static Object retVal;

    public enum CallType {SwitchProcess,SendMessage, Open, Close, Read, Seek, Write, GetMapping, CreateProcess, Sleep, GetPID, AllocateMemory, FreeMemory, GetPIDByName, WaitForMessage, Exit}
    public static CallType currentCall;

    private static void startTheKernel() throws InterruptedException {

        PCB currentP = ki.getScheduler().currentlyRunning;

        ki.start();

        if (currentP != null) {
            currentP.stop();
        }
        while (retVal == null) {
            Thread.sleep(10);
        }

    }

    public static void switchProcess() throws InterruptedException {
        parameters.clear();
        currentCall = CallType.SwitchProcess;
        startTheKernel();
    }

    public static void Startup(UserlandProcess init) throws InterruptedException {
            ki = new Kernel();
            CreateProcess(init, PriorityType.interactive);
            CreateProcess(new IdleProcess(), PriorityType.background);

    }

    public enum PriorityType {realtime, interactive, background}
    public static int CreateProcess(UserlandProcess up) throws InterruptedException {
        return CreateProcess(up,PriorityType.interactive);
    }

    // For assignment 1, you can ignore the priority. We will use that in assignment 2
    public static int CreateProcess(UserlandProcess up, PriorityType priority) throws InterruptedException {
        parameters.clear();
        parameters.add(up);
        parameters.add(priority);
        currentCall = CallType.CreateProcess;
        startTheKernel();

        return (int) retVal;
    }

    public static int GetPID() throws InterruptedException {
        parameters.clear();
        currentCall = CallType.GetPID;
        startTheKernel();
        return (int) retVal;
    }

    public static void Exit() throws InterruptedException {
        parameters.clear();
        currentCall = CallType.Exit;
        startTheKernel();
    }

    public static void Sleep(int mills) throws InterruptedException {
        parameters.clear();
        parameters.add(mills);
        currentCall = CallType.Sleep;
        startTheKernel();
    }

    // Devices
    public static int Open(String s) throws InterruptedException {
        parameters.clear();
        parameters.add(s);
        currentCall = CallType.Open;
        startTheKernel();
        return (int) retVal;
    }

    public static void Close(int id) throws InterruptedException {
        parameters.clear();
        parameters.add(id);
        currentCall = CallType.Close;
        startTheKernel();
    }

    public static byte[] Read(int id, int size) throws InterruptedException {
        parameters.clear();
        parameters.add(id);
        parameters.add(size);
        currentCall = CallType.Read;
        startTheKernel();
        return (byte[]) retVal;
    }

    public static void Seek(int id, int to) throws InterruptedException {
        parameters.clear();
        parameters.add(id);
        parameters.add(to);
        currentCall = CallType.Seek;
        startTheKernel();
    }

    public static int Write(int id, byte[] data) throws InterruptedException {
        parameters.clear();
        parameters.add(id);
        parameters.add(data);
        currentCall = CallType.Write;
        startTheKernel();
        return (int) retVal;
    }

    // Messages
    public static void SendMessage(KernelMessage km) throws InterruptedException {
        parameters.clear();
        parameters.add(km);
        currentCall = CallType.SendMessage;
        startTheKernel();
    }

    public static KernelMessage WaitForMessage() throws InterruptedException {
        parameters.clear();
        currentCall = CallType.WaitForMessage;
        startTheKernel();
        return (KernelMessage) retVal;
    }

    public static int GetPidByName(String name) throws InterruptedException {
        parameters.clear();
        parameters.add(name);
        currentCall = CallType.GetPIDByName;
        startTheKernel();
        return (int) retVal;
    }

    // Memory
    public static void GetMapping(int virtualPage) throws InterruptedException {
        parameters.clear();
        parameters.add(virtualPage);
        currentCall = CallType.GetMapping;
        startTheKernel();
    }

    //Returns the start virtual address
    public static int AllocateMemory(int size) throws InterruptedException {
        //Ensure that size is a multiple of 1024, return failure if not.
        if (size % 1024 != 0)
            return -1;
        else {
            parameters.clear();
            parameters.add(size / 1024);
            currentCall = CallType.AllocateMemory;
            startTheKernel();
            return (int) retVal;
        }
    }

    //Takes the virtual address and the amount to free
    public static boolean FreeMemory(int pointer, int size) throws InterruptedException {
        //Ensure that size and pointer are a multiple of 1024, return failure if not.
        if (size % 1024 != 0 && pointer % 1024 != 0)
            return false;
        else {
            parameters.clear();
            parameters.add(pointer);
            parameters.add(size / 1024);
            currentCall = CallType.FreeMemory;
            startTheKernel();
            return (boolean) retVal;
        }
    }
}
