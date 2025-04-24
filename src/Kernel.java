import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class Kernel extends Process implements Device {

    private final Scheduler scheduler;
    private final VFS vfs;
    private final boolean[] freeMemory;

    public Kernel() {
        scheduler = new Scheduler(this);
        vfs = new VFS();
        freeMemory = new boolean[1024];
        Arrays.fill(freeMemory, true);
    }

    @Override
    public void main() throws InterruptedException, IOException {

        while (true) { // Warning on infinite loop is OK...
            switch (OS.currentCall) { // get a job from OS, do it
                case CreateProcess ->  // Note how we get parameters from OS and set the return value
                        OS.retVal = CreateProcess((UserlandProcess) OS.parameters.get(0), (OS.PriorityType) OS.parameters.get(1));
                case SwitchProcess -> SwitchProcess();
                // Priority Scheduler
                case Sleep -> Sleep((int) OS.parameters.get(0));
                case GetPID -> OS.retVal = GetPid();
                case Exit -> Exit();

                // Devices
                case Open -> OS.retVal = Open((String) OS.parameters.getFirst());
                case Close -> Close((int) OS.parameters.getFirst());
                case Read -> OS.retVal = Read((int) OS.parameters.get(0), (int) OS.parameters.get(1));
                case Seek -> Seek((int) OS.parameters.get(0), (int) OS.parameters.get(1));
                case Write -> OS.retVal = Write((int) OS.parameters.get(0), (byte[]) OS.parameters.get(1));

                // Messages
                case GetPIDByName -> OS.retVal = GetPidByName((String) OS.parameters.getFirst());
                case SendMessage -> SendMessage((KernelMessage) OS.parameters.getFirst());
                case WaitForMessage -> OS.retVal = WaitForMessage();

                // Memory
                case GetMapping -> GetMapping((int)OS.parameters.getFirst());
                case AllocateMemory -> OS.retVal = AllocateMemory((int)OS.parameters.getFirst());
                case FreeMemory -> OS.retVal = FreeMemory((int)OS.parameters.get(0), (int)OS.parameters.get(1));

            }

            scheduler.currentlyRunning.start();
            this.stop();
        }
    }

    private void SwitchProcess() throws IOException {
        Hardware.ClearTLB();
        scheduler.SwitchProcess();
    }

    // For assignment 1, you can ignore the priority. We will use that in assignment 2
    private int CreateProcess(UserlandProcess up, OS.PriorityType priority) throws IOException {
        return scheduler.CreateProcess(up, priority);
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    private void Sleep(int mills) throws InterruptedException, IOException {
        scheduler.Sleep(mills);
    }

    private void Exit() throws IOException {

        //Close all devices for exiting program
        for (int i = 0; i < scheduler.GetCurrentlyRunning().devices.length - 1; i++) {
            int vfsID = scheduler.GetCurrentlyRunning().devices[i];
            if (vfsID != -1) {
                vfs.Close(vfsID);
            }
        }
        FreeAllMemory(scheduler.GetCurrentlyRunning());
        scheduler.unSchedule();
    }

    private int GetPid() {
        return scheduler.currentlyRunning.pid;
    }

    public int Open(String s) throws FileNotFoundException {

        PCB currentlyRunning = scheduler.GetCurrentlyRunning();

        for (int i = 0; i < currentlyRunning.devices.length; i++) { //Checks for open devices in PCB array to associate with vfs

            if (currentlyRunning.devices[i] == -1) {

                int deviceID = vfs.Open(s);

                if (deviceID == -1) {
                    return deviceID;
                }

                currentlyRunning.devices[i] = deviceID;
                return i;
            }
        }
        return -1;
    }

    public void Close(int id) throws IOException {

        if (scheduler.GetCurrentlyRunning().devices[id] != -1) {
            int vfsID = scheduler.GetCurrentlyRunning().devices[id];
            scheduler.GetCurrentlyRunning().devices[id] = -1;
            vfs.Close(vfsID);
            return;
        } else if (scheduler.GetCurrentlyRunning().devices[id] == -1) {
            return;
        }
        throw new FileNotFoundException("Device " + id + " not found");
    }

    public byte[] Read(int id, int size) throws IOException {

        byte[] buffer;

        if (scheduler.GetCurrentlyRunning().devices[id] != -1) {
            int vfsID = scheduler.GetCurrentlyRunning().devices[id];
            buffer = vfs.Read(vfsID, size);
            return buffer;
        }
        throw new FileNotFoundException("Device " + id + " not found");
    }

    public void Seek(int id, int to) throws IOException {

        if (scheduler.GetCurrentlyRunning().devices[id] != -1) {
            int vfsID = scheduler.GetCurrentlyRunning().devices[id];
            vfs.Seek(vfsID, to);
            return;
        }
        throw new FileNotFoundException("Device " + id + " not found");
    }

    public int Write(int id, byte[] data) throws IOException {

        int size;

        if (scheduler.GetCurrentlyRunning().devices[id] != -1) {
            int vfsID = scheduler.GetCurrentlyRunning().devices[id];
            size = vfs.Write(vfsID, data);
            return size;
        }
        throw new FileNotFoundException("Device " + id + " not found");
    }

    private void SendMessage(KernelMessage km) {

        KernelMessage copyKM = KernelMessage.copyConstructor(km);
        if (scheduler.processes.containsKey(copyKM.getTargetPID())) {
            scheduler.processes.get(copyKM.getTargetPID()).messages.add(copyKM);
            scheduler.waitingProcesses.remove(copyKM.getTargetPID());
        }

    }

    private KernelMessage WaitForMessage() throws IOException, InterruptedException {

        if (!scheduler.GetCurrentlyRunning().messages.isEmpty()) {
            return scheduler.GetCurrentlyRunning().messages.removeFirst();
        } else {
            scheduler.Wait(scheduler.GetCurrentlyRunning().pid, scheduler.GetCurrentlyRunning());
        }
        return null;
    }

    private int GetPidByName(String name) {
        int pid = scheduler.getPidByName(name);
        if (pid != -1) {
            return pid;
        } else
            throw new RuntimeException("PID " + name + " not found");
    }

    private void GetMapping(int virtualPage) throws IOException {
        int physicalPage = scheduler.GetCurrentlyRunning().pageMap[virtualPage];
        if (physicalPage != -1)
            Hardware.updateTLB(virtualPage, physicalPage);
        else {
            System.out.println("SEGFAULT: " + scheduler.GetCurrentlyRunning().name);
            Exit();
        }
    }

    private int AllocateMemory(int size) {
        
        int[] physicalPages = new int[size];
        int j = 0;
        //Here check the size, and find any (size) pages that are free. Remember these pages addresses.
        for (int i = 0; i < freeMemory.length - 1; i++) {
            if (freeMemory[i]) {
                physicalPages[j++] = i;
                freeMemory[i] = false;
            }
            if (j == size) {break;}
        }
        if (j != size) {
            throw new RuntimeException("Allocate memory failed. Process: " + scheduler.GetCurrentlyRunning().name);
        }

        //Then look at process' PCB page map and find (size) pages in a row of open space (array value of -1).
        int address = -1, openSpace = 0;
        for (int i = 0; i < scheduler.GetCurrentlyRunning().pageMap.length - 1; i++) {
            if (scheduler.GetCurrentlyRunning().pageMap[i] == -1) {
                if (address == -1)
                    address = i;
                openSpace++;
            } else {
                address = -1;
                openSpace = 0;
            }
            if (openSpace == size) break;
        }

        //Once these are found, assign the physical page mappings found here in Kernel to the open spaces in the PCB and return the first virtual address (first open index into the contiguous open space in the array)
        j = 0;
        for (int i = address; i < address + size; i++) {
            scheduler.GetCurrentlyRunning().pageMap[i] = physicalPages[j++];
        }

        System.out.println("Allocated memory. Virtual address: " + address * 1024 + ", physical address: " + scheduler.GetCurrentlyRunning().pageMap[address]);
        return address * 1024;
    }

    private boolean FreeMemory(int pointer, int size) {
        //Go to the virtual address referenced by pointer in the PCB and for (size) switch the associated physical address here in Kernel to true;
        try {
            for (int i = pointer; i < pointer + size; i++) {
                freeMemory[scheduler.GetCurrentlyRunning().pageMap[i]] = true;
                scheduler.GetCurrentlyRunning().pageMap[i] = -1;
            }
            return true;
        } catch (IndexOutOfBoundsException e) {
            return false; //Invalid pointer
        }
    }

    private void FreeAllMemory(PCB currentlyRunning) {
        //Free memory on Exit() or process is not alive. Loop through PCB virtual memory array and for every physical address set the boolean to true here.
        for (int i = 0; i < currentlyRunning.pageMap.length - 1; i++) {
            if (currentlyRunning.pageMap[i] != -1) {
                freeMemory[currentlyRunning.pageMap[i]] = true;
                currentlyRunning.pageMap[i] = -1;
            }
        }
        System.out.println("Freed all memory.");
    }

}