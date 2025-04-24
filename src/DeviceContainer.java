public class DeviceContainer {

   public Device device;
   public int id;

   public DeviceContainer() {
       device = null;
       id = 0;
   }

   public DeviceContainer(Device device, int id) {
       this.device = device;
       this.id = id;
   }
}
