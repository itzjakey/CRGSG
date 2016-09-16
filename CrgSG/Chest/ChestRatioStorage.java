package li.itzjakey.CrgSG.Chest;

import li.itzjakey.CrgSG.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChestRatioStorage {
    Map<Integer, ArrayList<ItemStack>> lvlstore = new HashMap<>();
    public static ChestRatioStorage instance = new ChestRatioStorage();
    private int ratio = 2;
    private int maxlevel = 0;

    public static ChestRatioStorage getInstance() {
        return instance;
    }

    public void setup() {
        FileConfiguration conf = Main.getInstance().Chest;

        for (int clevel = 1; clevel <= 5; clevel++) {
            ArrayList<ItemStack> lvl = new ArrayList<>();
            List list = conf.getStringList("chest.lvl" + clevel);

            if (list != null) {
                for (int b = 0; b < list.size(); b++) {
                    ItemStack i = ItemReader.read((String) list.get(b));
                    lvl.add(i);
                }
                this.lvlstore.put(clevel, lvl);
            } else {
                this.maxlevel = clevel;
                break;
            }
        }

        int clevel = 1337;
        ArrayList<ItemStack> lvl = new ArrayList<>();
        List list = conf.getStringList("chest.lvl" + clevel);

        if (list != null) {
            for (int b = 0; b < list.size(); b++) {
                ItemStack i = ItemReader.read((String) list.get(b));
                lvl.add(i);
            }
            this.lvlstore.put(clevel, lvl);
        } else {
            this.maxlevel = clevel;
        }

        this.ratio = conf.getInt("chest.ratio", this.ratio);
    }

    public int getLevel(int base) {
        int max = Math.min(base + 5, this.maxlevel);
        while ((Main.getInstance().rand().nextInt(this.ratio) == 0) && (base < max)) {
            base++;
        }
        return base;
    }

    public ArrayList<ItemStack> getItems(int level) {
        ArrayList<ItemStack> items = new ArrayList<>();

        for (int a = 0; a < Main.getInstance().rand().nextInt(7) + 10; a++) {
            if (Main.getInstance().rand().nextBoolean()) {
                while ((level < level + 5) && (level < this.maxlevel) && (Main.getInstance().rand().nextInt(this.ratio) == 1)) {
                    level++;
                }

                ArrayList lvl = (ArrayList) this.lvlstore.get(level);
                ItemStack item = (ItemStack) lvl.get(Main.getInstance().rand().nextInt(lvl.size()));

                items.add(item);
            }

        }

        return items;
    }

    public ArrayList<ItemStack> getItems() {
        int level = 1337;
        ArrayList<ItemStack> items = new ArrayList<>();
        for (int a = 0; a < Main.getInstance().rand().nextInt(7) + 10; a++) {
            if (Main.getInstance().rand().nextBoolean()) {
                ArrayList lvl = (ArrayList) this.lvlstore.get(level);
                ItemStack item = (ItemStack) lvl.get(Main.getInstance().rand().nextInt(lvl.size()));

                items.add(item);
            }
        }

        return items;
    }
}
