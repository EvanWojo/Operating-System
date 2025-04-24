import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VFS implements Device {

    private final Map<Integer, DeviceContainer> vfsMap = new HashMap<>();
    private final RandomDevice randomDevice = new RandomDevice();
    private final FakeFileSystem fakeFileSystem = new FakeFileSystem();
    private int nextId = 0;
    TextManager textManager;


    @Override
    public int Open(String s) throws FileNotFoundException {

        textManager = new TextManager(s);

        char peek =  textManager.peekCharacter();
        String deviceName = "";

        while (peek != ' ' && !textManager.isAtEnd()) {
            deviceName = deviceName.concat(String.valueOf(textManager.getCharacter()));

            if (!textManager.isAtEnd())
                peek = textManager.peekCharacter();
        }

        String parameters = "";

        if (!textManager.isAtEnd()) {

            textManager.getCharacter();

            while (!textManager.isAtEnd()) {
                parameters = parameters.concat(String.valueOf(textManager.getCharacter()));

            }
        }

        Device device = switch (deviceName) {
            case "random" -> randomDevice;
            case "file" -> fakeFileSystem;
            default -> throw new IllegalArgumentException("Unknown device " + deviceName);
        };

        int deviceID = device.Open(parameters);
        if (deviceID == -1) {
            return deviceID;
        }

        vfsMap.put(nextId, new DeviceContainer(device, deviceID));
        return nextId++;
    }

    @Override
    public void Close(int id) throws IOException {

        DeviceContainer deviceContainer = vfsMap.remove(id);
        if (deviceContainer != null) {
            deviceContainer.device.Close(deviceContainer.id);
        }
    }

    @Override
    public byte[] Read(int id, int size) throws IOException {

        DeviceContainer deviceContainer = vfsMap.get(id);

        return (deviceContainer != null) ? deviceContainer.device.Read(deviceContainer.id, size) : new byte[0];
    }

    @Override
    public void Seek(int id, int to) throws IOException {

        DeviceContainer deviceContainer = vfsMap.get(id);
        if (deviceContainer != null) {
            deviceContainer.device.Seek(deviceContainer.id, to);
        }
    }

    @Override
    public int Write(int id, byte[] data) throws IOException {

        DeviceContainer deviceContainer = vfsMap.get(id);

        return (deviceContainer != null) ? deviceContainer.device.Write(deviceContainer.id, data) : 0;
    }
}
