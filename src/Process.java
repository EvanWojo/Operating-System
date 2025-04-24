import java.io.IOException;
import java.util.concurrent.Semaphore;

public abstract class Process implements Runnable{

    public Thread thread;
    public Semaphore semaphore;
    public boolean quantumExpired;

    public Process() {
        semaphore = new Semaphore(0);
        thread = new Thread(this);
        thread.start();
        quantumExpired = false;
    }

    public void requestStop() {
        quantumExpired = true;
    }

    public abstract void main() throws InterruptedException, IOException;

    public boolean isStopped() {
        return semaphore.availablePermits() == 0;
    }

    public boolean isDone() { //Returns true if thread is NOT alive
        return !thread.isAlive();
    }

    public void start() {
        semaphore.release();
    }

    public void stop() throws InterruptedException {
        semaphore.acquire();
    }

    public void run() { // This is called by the Thread - NEVER CALL THIS!!!

        try {
            semaphore.acquire();
            main();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void cooperate() throws InterruptedException {
        if(quantumExpired) {
            quantumExpired = false;
            OS.switchProcess();
        }
    }
}
