public class IdleProcess extends UserlandProcess {
    @Override
    public void main() {
        while (true) {
            try {
                System.out.println("Zzz");
                cooperate();
                Thread.sleep(1000);
                OS.Sleep(100);
            } catch (Exception e) { }
        }
    }
}
