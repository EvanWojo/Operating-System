public class Hardware {

    private static final Integer[][] tlb = new Integer[2][2]; //TLB is a static array of Integer holding 2 virtual addresses and 2 physical addresses
    private static final byte[] memory = new byte[1024 * 1024];

    public static byte Read(int address) throws InterruptedException {
        return memory[getPhysicalAddress(address)];
    }

    public static void Write(int address, byte value) throws InterruptedException {
        memory[getPhysicalAddress(address)] = value;
    }

    private static int getPhysicalAddress(int address) throws InterruptedException {

        int virtualPage = address / 1024;
        int physicalPage = -1;

        //Look at TLB to see if this virtual page -> physical page mapping is in there
        for (Integer[] integers : tlb) {
            if (integers[0] != null && integers[0].equals(virtualPage)) {
                physicalPage = integers[1];
            }
        }
        int pageOffset = address % 1024;

        if (physicalPage != - 1) //Physical address was found in the TLB, return here
            return physicalPage * 1024 + pageOffset;

        OS.GetMapping(virtualPage);
        return getPhysicalAddress(address);
    }

    public static void updateTLB(int virtualPage, int physicalPage) {
        int random = (int)(Math.random() * 2);
        tlb[random][0] = virtualPage;
        tlb[random][1] = physicalPage;
    }

    public static void ClearTLB() {
        for (int i = 0; i < tlb.length; i++) {
            tlb[i][0] = -1;
            tlb[i][1] = -1;
            System.out.println("TLB cleared.");
        }
    }
}
