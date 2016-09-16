package li.itzjakey.CrgSG.Rollback;

import org.bukkit.Material;

public class EditedBlock {

    private String world;
    private Material prevBlock;
    private Material newBlock;
    private byte prevBlockData, newBlockData;
    private int x, y, z;

    public EditedBlock(String world, Material prevBlock, byte prevBlockData, Material newBlock, byte newBlockData, int x, int y, int z) {
        this.world = world;
        this.prevBlock = prevBlock;
        this.prevBlockData = prevBlockData;
        this.newBlock = newBlock;
        this.newBlockData = newBlockData;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getWorld() {
        return world;
    }

    public byte getPrevBlockData() {
        return prevBlockData;
    }

    public byte getNewBlockData() {
        return newBlockData;
    }

    public Material getPrevBlock() {
        return prevBlock;
    }

    public Material getNewBlock() {
        return newBlock;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

}
