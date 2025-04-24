import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FakeFileSystem implements Device {

    private final RandomAccessFile[] files = new RandomAccessFile[10];

    @Override
    public int Open(String s) throws FileNotFoundException {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("Empty File Name");
        }

        for (int i = 0; i < 10; i++) {
            if (files[i] == null) {
                files[i] = new RandomAccessFile(s,"rw");
                return i;
            }
        }
        return -1;
    }

    @Override
    public void Close(int id) throws IOException {
        if (id >= 0 && id < files.length && files[id] != null) {
            files[id].close();
            files[id] = null;
        }
    }

    @Override
    public byte[] Read(int id, int size) throws IOException {
        if (id < 0 || id >= files.length || files[id] == null && size <= 0) {
            return new byte[0];
        }

        byte[] buffer = new byte[size];
        int bytesRead = files[id].read(buffer);
        if (bytesRead == -1) {
            return buffer;
        }
        if (bytesRead < size) {
            byte[] newBuffer = new byte[bytesRead];
            System.arraycopy(buffer, 0, newBuffer, 0, bytesRead);
            buffer = newBuffer;
        }
        return buffer;
    }

    @Override
    public void Seek(int id, int to) throws IOException {
        if (id >= 0 && id < files.length && files[id] != null) {
            files[id].seek(to);
        }
    }

    @Override
    public int Write(int id, byte[] data) throws IOException {
        if (id < 0 || id >= files.length || files[id] == null) {
            return 0;
        }
        files[id].write(data);
        return data.length;
    }
}
