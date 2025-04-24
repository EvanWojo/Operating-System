import java.util.Arrays;
import java.util.LinkedList;

public class PCB { // Process Control Block
    public String name;
    private static int nextPid = 1;
    public int pid;
    public int allowance; //Number that keeps track of how many times the program has run to timeout
    public int wakeUp; //Number that keeps track of when a sleeping program should be woken up
    private OS.PriorityType priority;
    UserlandProcess userlandProcess;
    public int[] devices;
    public LinkedList<KernelMessage> messages;
    public int[] pageMap; //The virtual page number is an index into this array. The value is the physical page number.

    PCB(UserlandProcess up, OS.PriorityType priority) {
        this.userlandProcess = up;
        this.allowance = 0;
        this.wakeUp = 0;
        this.pid = nextPid++;
        this.priority = priority;
        this.devices = new int[10];
        Arrays.fill(devices, -1);
        name = this.getName();
        messages = new LinkedList<>();
        pageMap = new int[100];
        Arrays.fill(pageMap, -1);
    }

    public String getName() {
        return userlandProcess.getClass().getSimpleName();
    }

    OS.PriorityType getPriority() {
        return priority;
    }

    public void requestStop() throws InterruptedException {
        userlandProcess.requestStop();
    }

    public void stop() throws InterruptedException { /* calls userlandprocess’ stop. Loops with Thread.sleep() until ulp.isStopped() is true.  */
        userlandProcess.stop();
        while (!userlandProcess.isStopped()) {
            Thread.sleep(10);
        }
    }

    public boolean isDone() { /* calls userlandprocess’ isDone() */
        return userlandProcess.isDone();
    }

    void start() { /* calls userlandprocess’ start() */
        userlandProcess.start();
    }

    public void checkAllowance() throws InterruptedException { //Checks how many times the program has run a timeout and demotes the program if necessary

        if (++allowance >= 5) {
            if (this.priority.equals(OS.PriorityType.realtime))
                this.setPriority(OS.PriorityType.interactive);
            else if (this.priority.equals(OS.PriorityType.interactive))
                this.setPriority(OS.PriorityType.background);
            allowance = 0;
        }

    }

    public void setPriority(OS.PriorityType newPriority) {
        priority = newPriority;
    }

    public void Sleep(int millis) throws InterruptedException {
        allowance = 0;
        Thread.sleep(millis);
    }
}
