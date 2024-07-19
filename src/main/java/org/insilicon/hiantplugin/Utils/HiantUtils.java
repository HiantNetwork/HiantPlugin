package org.insilicon.hiantplugin.Utils;

import net.cybercake.cyberapi.spigot.CyberAPI;
import net.cybercake.cyberapi.spigot.chat.Log;
import net.cybercake.cyberapi.spigot.chat.UChat;
import net.cybercake.cyberapi.spigot.player.CyberPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.SuffixNode;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.insilicon.hiantplugin.HiantPlugin;
import org.insilicon.hiantplugin.guis.Suffixes;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings({"ConstantConditions", "deprecation"})
public class HiantUtils {

    public static String getDisplayNameIfExists(CommandSender sender) {
        return getDisplayNameIfExists(sender, true);
    }

    public static String getDisplayNameIfExists(CommandSender sender, boolean useDisplay) {
        if (!(sender instanceof Player player)) return sender.getName();
        if (!useDisplay) return player.getName();
        try {
            return CyberPlayer.from(player).getLuckPermsData().getDisplayName();
        } catch (Error err) {
            return player.getName();
        }
    }

    public static String getPrefixIfExists(CommandSender sender) {
        if (!(sender instanceof Player player)) return sender.getName();
        try {
            String prefix = CyberPlayer.from(player).getLuckPermsData().getPrefix();
            return (prefix == null ? "" : prefix);
        } catch (Error err) {
            return "";
        }
    }

    public static void setPlayerSuffix(Player player, String suffix, int weight) {
        SuffixNode node = SuffixNode.builder(suffix, weight).build();

        HiantPlugin.getLuckpermsAPI().getUserManager().modifyUser(player.getUniqueId(), user -> {
            // Remove all other player suffixes
            user.data().clear(NodeType.SUFFIX.predicate(sn -> sn.getPriority() == 1));
            user.data().add(node);
        });
    }

    public static void removePlayerSuffix(Player player, int level) {
        HiantPlugin.getLuckpermsAPI().getUserManager().modifyUser(player.getUniqueId(), user -> {
            user.data().clear(NodeType.SUFFIX.predicate(sn -> sn.getPriority() == level));
        });
    }

    public static String getSuffixIfExists(CommandSender sender) {
        if (!(sender instanceof Player player)) return sender.getName();
        try {
            String suffix = CyberPlayer.from(player).getLuckPermsData().getSuffix();
            return (suffix == null ? "" : suffix);
        } catch (Error err) {
            return "";
        }
    }

    public static String stripColor(String string) {
        return stripColor(UChat.component(string));
    }

    public static String stripColor(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    public static String stripMiniMessageColor(String string) {
        return MiniMessage.miniMessage().stripTags(convertLegacyColors(string));
    }

    public static String convertLegacyColors(String legacy) {
        String noStupidSymbol = legacy.replaceAll("ยง", "&");
        return noStupidSymbol.replaceAll("&0", "<reset><black>")
                .replaceAll("&1", "<reset><dark_blue>")
                .replaceAll("&2", "<reset><dark_green>")
                .replaceAll("&3", "<reset><dark_aqua>")
                .replaceAll("&4", "<reset><dark_red>")
                .replaceAll("&5", "<reset><dark_purple>")
                .replaceAll("&6", "<reset><gold>")
                .replaceAll("&7", "<reset><gray>")
                .replaceAll("&8", "<reset><dark_gray>")
                .replaceAll("&9", "<reset><blue>")
                .replaceAll("&a", "<reset><green>")
                .replaceAll("&b", "<reset><aqua>")
                .replaceAll("&c", "<reset><red>")
                .replaceAll("&d", "<reset><light_purple>")
                .replaceAll("&e", "<reset><yellow>")
                .replaceAll("&f", "<reset><white>")
                .replaceAll("&n", "<underlined>")
                .replaceAll("&m", "<strikethrough>")
                .replaceAll("&k", "<obfuscated>")
                .replaceAll("&o", "<italic>")
                .replaceAll("&l", "<bold>")
                .replaceAll("&r", "<reset>");
    }

    public static Component coloredMiniMessage(String string) {
        return UChat.miniMessage(convertLegacyColors(string));
    }

    public static Component coloredMiniMessage(Component message) {
        return coloredMiniMessage(PlainTextComponentSerializer.plainText().serialize(message));
    }

    public static int getDoubleChest() {
        return InventoryType.CHEST.getDefaultSize() * 2;
    }

    public static List<String> addPlayerOptions() {
        List<String> playerNameList = Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());

        playerNameList.add(0, "*");
        playerNameList.add(1, "others");

        return playerNameList;
    }

    public static PlayerOption addPlayerOptions(String argToCheck) {
        switch (argToCheck.toLowerCase()) {
            case "*", "all", "@a" -> {
                return PlayerOption.ALL;
            }
            case "others", "other" -> {
                return PlayerOption.OTHERS;
            }
            default -> {
                return null;
            }
        }
    }

    public static void addPlayerOptions(CommandSender sender, PlayerOption type, PlayerOptionsInterface callback) {
        ArrayList<Player> playerArrayList = new ArrayList<>();

        switch (type) {
            case ALL -> playerArrayList.addAll(Bukkit.getOnlinePlayers());
            case OTHERS -> {
                playerArrayList = Bukkit.getOnlinePlayers().stream()
                        .filter(player -> !(sender instanceof Player && sender.equals(player)))
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        }

        callback.run(playerArrayList);
    }

    public static void broadcastMiniMessage(String message) {
        broadcastMiniMessage(message, null, (Predicate<? super CommandSender>) null);
    }

    public static void broadcastMiniMessage(String message, @Nullable MiniMessage colorFilter) {
        broadcastMiniMessage(message, colorFilter, (Predicate<? super CommandSender>) null);
    }

    public static void broadcastMiniMessage(String message, @Nullable MiniMessage colorFilter, @Nullable String permission) {
        broadcastMiniMessage(message, colorFilter, player ->
                permission == null
                        || permission.strip().equalsIgnoreCase("")
                        || player.hasPermission(permission)
        );
    }

    public static void broadcastMiniMessage(String message, @Nullable MiniMessage colorFilter, @Nullable Predicate<? super CommandSender> filter) {
        for (Player player : CyberAPI.getInstance().getOnlinePlayers()) {
            if (filter != null && !filter.test(player)) continue;
            String msg = HiantUtils.convertLegacyColors(message);
            if (colorFilter != null) {
                player.sendMessage(UChat.miniMessage(colorFilter, msg));
            } else {
                player.sendMessage(UChat.miniMessage(msg));
            }
        }

        if (filter == null || filter.test(CyberAPI.getInstance().getServer().getConsoleSender()))
            Log.info(message);
    }

    public static String[] getUChatLines(String... lines) {
        return UChat.listChat(lines).toArray(String[]::new);
    }
}