import java.util.Random;

public class RandomDevice implements Device {

    private final Random[] devices = new Random[10];

    @Override
    public int Open(String s) {

        for (int i = 0; i < devices.length; i++) { //Looks for open array slot and inserts random device accordingly
            if (devices[i] == null) {
                if (s.isEmpty()) {
                    devices[i] = new Random();
                } else {
                    devices[i] = new Random(Integer.parseInt(s));
                }
                return i;
            }
        }
        return -1;
    }

    @Override
    public void Close(int id) { //Removes random device
        devices[id] = null;
    }

    @Override
    public byte[] Read(int id, int size) { //Populates random devices
        if (id >= 0 && id < devices.length && devices[id] != null && size > 0) {
            byte[] b = new byte[size];
            devices[id].nextBytes(b);
            return b;
        }
        return new byte[0];
    }

    @Override
    public void Seek(int id, int to) { //Consumes random devices
        if (id >= 0 && id < devices.length && devices[id] != null) {
            byte[] temp = new byte[to];
            devices[id].nextBytes(temp);
        }
    }

    @Override
    public int Write(int id, byte[] data) {
        return 0;
    }
}
