package li.itzjakey.CrgSG.Utils;

import io.anw.Core.Bukkit.Utils.ItemStack.ItemUtils;
import io.anw.Core.Bukkit.Utils.Misc.BossBarUtils;
import io.anw.Core.Bukkit.Utils.Misc.SoundPlayer;
import io.anw.Core.Bukkit.Utils.UUID.UUIDUtility;
import li.itzjakey.CrgSG.Game.Game;
import li.itzjakey.CrgSG.Main;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.UUID;

public class GameUtils extends li.itzjakey.CrgSG.API.Utils.GameUtils {

    @Override
    public void initializePlayer(final Player player) {
        Game.getInstance().getInGame().add(UUIDUtility.getUUID(player.getName()));

        if (Main.getInstance().Data.getString("LOBBY_SPAWN.WORLD") != null) {
            player.teleport(LocationUtils.getWaitingLobbyLocation());
        } else {
            MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "&c&lERROR : &7The lobby spawn is not set! Please contact a staff member!");
        }

        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setLevel(0);
        player.setExp(0);

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(potionEffect.getType());
        }

        SoundPlayer.play(player, Sound.BLAZE_HIT, 5);
        Game.getInstance().broadcastGame(MessageUtils.MessageType.GOOD, "&b" + player.getName() + " &ahas joined Survival Games! &6(&e" + Game.getInstance().getInGame().size() + "&6/&e" + Main.getInstance().Config.getInt("Max-Players") + "&6)");

        ItemStack maps = ItemUtils.createItemStack(
                "&e&lMap Voting &7(Right Click)",
                Arrays.asList(
                        "&6&lRight Click &7to open the map voting menu!"
                ), Material.ENCHANTED_BOOK
        );

        ItemStack clock = ItemUtils.createItemStack(
                "&e&lReturn to Lobby &7(Right Click)",
                Arrays.asList(
                        "&6&lRight Click &7to return to the lobby!"
                ),
                Material.WATCH
        );

        player.getInventory().setItem(0, maps);
        player.getInventory().setItem(8, clock);

        player.setScoreboard(Game.getInstance().getScoreboard());
    }

    @Override
    public void unregisterPlayer(Player player) {
        UUID player_uuid = UUIDUtility.getUUID(player.getName());

        if (Game.getInstance().getInGame().contains(player_uuid)) {
            Game.getInstance().getInGame().remove(player_uuid);
        }
        if (Game.getInstance().getSpectating().contains(player_uuid)) {
            Game.getInstance().getSpectating().remove(player_uuid);
        }
    }

    @Override
    public void initializeSpectator(final Player player) {
        Game.getInstance().getSpectating().add(UUIDUtility.getUUID(player.getName()));
        Main.getInstance().getConditionManager().addHiddenPlayer(player);

        if (BossBarUtils.hasBar(player)) {
            BossBarUtils.destroyDragon(player);
        }

        BossBarUtils.setBar(player, "&6CrgSG &8» &eYou are now a spectator!", 200);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (BossBarUtils.hasBar(player)) {
                    BossBarUtils.destroyDragon(player);
                }
            }
        }.runTaskLater(Main.getInstance(), 20 * 3);

        player.setMaxHealth(20.0D);
        player.setHealth(20.0D);
        player.setFireTicks(0);
        player.setAllowFlight(true);
        player.setFlying(true);
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setExp(0F);
        player.setLevel(0);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.ADVENTURE);

        ItemStack spectatingCompass = ItemUtils.createItemStack(
                "&e&lSpectate Menu &7(Right Click)",
                Arrays.asList(
                        "&6&lRight Click &7to open the spectator menu!"
                ),
                Material.EYE_OF_ENDER
        );

        ItemStack clock = ItemUtils.createItemStack(
                "&e&lReturn to Lobby &7(Right Click)",
                Arrays.asList(
                        "&6&lRight Click &7to return to the lobby!"
                ),
                Material.WATCH
        );

        player.getInventory().setItem(0, spectatingCompass);
        player.getInventory().setItem(1, clock);

        player.setScoreboard(Game.getInstance().getScoreboard());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (Main.getInstance().Data.getString("LOBBY_SPAWN.WORLD") != null) {
                    player.teleport(LocationUtils.getWaitingLobbyLocation());
                    SoundPlayer.play(player, Sound.ENDERMAN_TELEPORT, 25);
                } else {
                    MessageUtils.messagePrefix(player, MessageUtils.MessageType.BAD, "&c&lERROR : &7The lobby spawn is not set! Please contact a staff member!");
                }
            }
        }.runTaskLater(Main.getInstance(), 0);
    }

}
