public class Init extends UserlandProcess {


    public void main() throws InterruptedException {

        OS.CreateProcess(new HelloWorld(), OS.PriorityType.realtime); //Normal realtime that calls sleep
        OS.CreateProcess(new GoodbyeWorld(), OS.PriorityType.interactive);  //Interactive test
//        OS.CreateProcess(new GreedyWorld(), OS.PriorityType.realtime); //Greedy realtime that never calls sleep

//        OS.CreateProcess(new DeviceTest1(), OS.PriorityType.realtime); //Tests RandomDevice
//        OS.CreateProcess(new DeviceTest2(), OS.PriorityType.interactive); //Tests FakeFileSystem

//        OS.CreateProcess(new Ping(), OS.PriorityType.realtime);
//        OS.CreateProcess(new Pong(), OS.PriorityType.realtime);

        OS.CreateProcess(new PagingTest(), OS.PriorityType.realtime);
        OS.CreateProcess(new PagingTest2(), OS.PriorityType.realtime);

        OS.Exit();
    }
}
