package xyz.prorickey.hiantplugin.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.insilicon.hiantplugin.HiantPlugin;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.api.users.UserManager;

import javax.annotation.Nullable;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LinkCommand {

    public static Map<String, LinkCode> codes = new HashMap<>();
    public static Map<UUID, Long> minecraftLinking = new HashMap<>();
    public static Map<Long, Long> discordLinking = new HashMap<>();

    public static Map<UUID, String> recentlyLinked = new HashMap<>();

    @SuppressWarnings("UnstableApiUsage")
    public static void register(LifecycleEventManager<Plugin> manager) {
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            commands.register(
                    Commands.literal("link")
                            .then(Commands.argument("code", StringArgumentType.string())
                                    .executes(context -> {
                                        handle(context.getSource().getSender(), StringArgumentType.getString(context, "code"));
                                        return Command.SINGLE_SUCCESS;
                                    })
                            )
                            .executes(context -> {
                                handle(context.getSource().getSender(), null);
                                return Command.SINGLE_SUCCESS;
                            })
                            .build(),
                    "Link your Minecraft account to your Discord account"
            );
        });

        Bukkit.getScheduler().runTaskTimer(HiantPlugin.getInstance(), () -> {
            long currentTime = System.currentTimeMillis();
            minecraftLinking.entrySet().removeIf(entry -> currentTime - entry.getValue() > 60000);
            discordLinking.entrySet().removeIf(entry -> currentTime - entry.getValue() > 60000);

            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                if(recentlyLinked.containsKey(player.getUniqueId())) {
                    String username = recentlyLinked.get(player.getUniqueId());
                    recentlyLinked.remove(player.getUniqueId());
                    player.sendMessage(MiniMessage.miniMessage().deserialize("\n<green>Your account has been linked to <yellow>" + username + "\n" +
                            "<green>If this was not you, please execute the /unlink command.\n"));
                    player.playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1.0f, 1.0f));
                }
            }
        }, 0, 20);
    }

    private static void handle(CommandSender sender, @Nullable String code) {
        if(!(sender instanceof Player player)) return;
        if(HiantPlugin.getDatabase().getLinkedAccount(player.getUniqueId()) != null) {
            player.sendMessage(MiniMessage.miniMessage().deserialize("\n<red>You already have a linked account.\n"));
            player.playSound(Sound.sound(Key.key("block.note_block.bass"), Sound.Source.MASTER, 1.0f, 1.0f));
            return;
        }
        if (code != null) {
            if (codes.containsKey(code)) {
                LinkCode linkCode = codes.get(code);
                discordLinking.remove(linkCode.discordId);
                codes.remove(code);
                linkCode.setUuid(player.getUniqueId());
                if(!HiantPlugin.getDatabase().linkedBefore(player.getUniqueId())) LinkCommand.giveKey(player.getUniqueId());
                HiantPlugin.getDatabase().linkAccount(linkCode.discordId, player.getUniqueId().toString());
                player.sendMessage(MiniMessage.miniMessage().deserialize("\n<green>Your account has been linked successfully!\n"));
                player.playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1.0f, 1.0f));
                Bukkit.getScheduler().runTaskAsynchronously(HiantPlugin.getInstance(), () -> HiantPlugin.getDiscord().getJDA().retrieveUserById(linkCode.discordId)
                        .queue(user -> user
                                .openPrivateChannel()
                                .flatMap(channel -> channel
                                        .sendMessage("Your account has been linked to `" +
                                                player.getName() +
                                                "`. If this was not you, please execute the /unlink command in the discord server."
                                        ))
                                .queue()));
            } else {
                player.sendMessage(MiniMessage.miniMessage().deserialize("\n<red>Invalid code.\n"));
                player.playSound(Sound.sound(Key.key("block.note_block.bass"), Sound.Source.MASTER, 1.0f, 1.0f));
            }
        } else {
            if(minecraftLinking.containsKey(player.getUniqueId())) {
                LinkCode linkCode = codes.values().stream().filter(lc -> lc.getUuid().equals(player.getUniqueId())).findFirst().orElse(null);
                if(linkCode != null) codes.remove(linkCode.getCode());
                minecraftLinking.remove(player.getUniqueId());
            }
            String generatedCode = generateCode();
            codes.put(generatedCode, new LinkCode(generatedCode, player.getUniqueId(), null));
            minecraftLinking.put(player.getUniqueId(), System.currentTimeMillis());
            player.sendMessage(
                    MiniMessage.miniMessage().deserialize("\n<yellow>Your link code is <green>" + generatedCode + "<yellow>. Click to copy it to your clipboard.\n")
                            .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, generatedCode))
                            .append(MiniMessage.miniMessage().deserialize("<yellow>Use the /link command in Discord to link your account.\n"))
            );
            player.playSound(Sound.sound(Key.key("block.note_block.pling"), Sound.Source.MASTER, 1.0f, 1.0f));
        }
    }

    public static void giveKey(UUID uuid) {
        HiantPlugin.getPlugin().getServer().getScheduler().runTask(HiantPlugin.getPlugin(), () -> {
            UserManager userManager = CratesProvider.get().getUserManager();
            userManager.addKeys(uuid, "EmeraldCrate", KeyType.virtual_key, 1);
        });
    }

    public static String generateCode() {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        return code.toString();
    }

    public static class LinkCode {
        public final String code;
        public UUID uuid;
        public Long discordId;

        public LinkCode(String code, UUID uuid, Long discordId) {
            this.code = code;
            this.uuid = uuid;
            this.discordId = discordId;
        }

        public String getCode() { return code; }
        public UUID getUuid() { return uuid; }
        public Long getDiscordId() { return discordId; }

        public void setUuid(UUID uuid) { this.uuid = uuid; }
        public void setDiscordId(Long discordId) { this.discordId = discordId; }
    }

}
