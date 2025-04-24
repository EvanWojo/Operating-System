import java.io.IOException;
import java.time.Clock;
import java.util.*;

public class Scheduler {

    private final LinkedList<PCB> RTQueue, IQueue, BQueue, SleeperQueue;
    public final HashMap<Integer, PCB> processes, waitingProcesses;
    private final HashMap<String, Integer> processNames;
    public PCB currentlyRunning;
    private final Clock clock;
    private final Kernel kernel;

    public Scheduler(Kernel kernel) {

        clock = Clock.systemUTC();
        RTQueue = new LinkedList<>();
        IQueue = new LinkedList<>();
        BQueue = new LinkedList<>();
        SleeperQueue = new LinkedList<>();
        Timer timer = new Timer();
        this.kernel = kernel;
        processes = new HashMap<>();
        waitingProcesses = new HashMap<>();
        processNames = new HashMap<>();

        TimerTask timerTask = new TimerTask() { //Create the task for the timer to stop the process
            public void run() {
                if (currentlyRunning != null) {
                    try {

                        if (currentlyRunning.getPriority().equals(OS.PriorityType.realtime) || currentlyRunning.getPriority().equals(OS.PriorityType.interactive)) {
                            currentlyRunning.checkAllowance();
                        }

                        currentlyRunning.requestStop();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };

        timer.schedule(timerTask, 0, 250);

    }

    public int CreateProcess(UserlandProcess up, OS.PriorityType p) throws IOException {

        PCB userlandPCB = new PCB(up, p);
        processes.put(userlandPCB.pid, userlandPCB);
        processNames.put(userlandPCB.name, userlandPCB.pid);

        switch (p) {
            case realtime -> RTQueue.add(userlandPCB);
            case interactive -> IQueue.add(userlandPCB);
            case background -> BQueue.add(userlandPCB);
        }

        if (currentlyRunning == null) //On startup begin by switchProcess
            SwitchProcess();

        return userlandPCB.pid;
    }

    public void SwitchProcess() throws IOException {

        for (PCB pcb : waitingProcesses.values()) {
            if (!pcb.messages.isEmpty())
                switch (pcb.getPriority()) {
                    case realtime -> RTQueue.add(pcb);
                    case interactive -> IQueue.add(pcb);
                    case background -> BQueue.add(pcb);
                }
        }

        if (currentlyRunning != null && !currentlyRunning.isDone() && !waitingProcesses.containsKey(currentlyRunning.pid)) { //Only add PCB to queue if necessary

            switch (currentlyRunning.getPriority()) {
                case realtime -> RTQueue.add(currentlyRunning);
                case interactive -> IQueue.add(currentlyRunning);
                case background -> BQueue.add(currentlyRunning);
            }
        }

        if (currentlyRunning != null && currentlyRunning.isDone()) { //Remove devices from the PCB if it's finished
            for (int i = 0; i < currentlyRunning.devices.length; i++) {
                kernel.Close(currentlyRunning.devices[i]);
            }
            processes.remove(currentlyRunning.pid);
        }

        int r = randomNumber();

        if (!SleeperQueue.isEmpty()) { //In the case that there are processes sleeping

            for (int i = 0; i < SleeperQueue.size(); i++) { //Loop over the queue

                if (SleeperQueue.get(i).wakeUp <= clock.millis()) { //Check if the process is ready to be woken up
                    currentlyRunning = SleeperQueue.remove(i);
                    break;
                }

            }

        } else if (!RTQueue.isEmpty()) { //Probability selection if RealTime processes are still in queue

            if (r <= 59) {
                currentlyRunning = RTQueue.remove();
            } else if (r <= 89) {
                try {
                    currentlyRunning = IQueue.remove();
                } catch (NoSuchElementException e) {
                    currentlyRunning = RTQueue.remove();
                }
            } else {
                try {
                    currentlyRunning = BQueue.remove();
                } catch (NoSuchElementException e) {
                    currentlyRunning = RTQueue.remove();
                }
            }

        } else if (!IQueue.isEmpty()) { //Probability selection if no RealTime processes are in queue

            if (r <= 74) {
                currentlyRunning = IQueue.remove();
            } else {
                try {
                    currentlyRunning = BQueue.remove();
                } catch (NoSuchElementException e) {
                    currentlyRunning = IQueue.remove();
                }
            }

        } else { //Default
            currentlyRunning = BQueue.remove();
        }

        System.out.println("Current Process: " + currentlyRunning.getName() + ", Priority: " + currentlyRunning.getPriority());

    }

    public void unSchedule() throws IOException { //Remove the process from the scheduler
        currentlyRunning = null;
        SwitchProcess();
    }

    public void Sleep(int milliseconds) throws InterruptedException, IOException {
        currentlyRunning.allowance = 0;
        SleeperQueue.add(currentlyRunning);
        currentlyRunning.wakeUp = (int) clock.millis() + milliseconds;
        currentlyRunning = null;
        SwitchProcess();
    }

    public void Wait(Integer pid, PCB pcb) throws InterruptedException, IOException {
        waitingProcesses.put(pid, pcb);
        currentlyRunning = null;
        SwitchProcess();
    }

    private int randomNumber() { //Selects random number from 0-99
        return (int) (Math.random() * 100);
    }

    public PCB GetCurrentlyRunning() {
        return currentlyRunning;
    }

    public int getPidByName(String name) {

        if (processNames.containsKey(name)) {
            return processNames.get(name);
        }

        return -1;
    }

}
