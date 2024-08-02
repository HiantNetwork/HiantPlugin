package xyz.prorickey.hiantplugin.discord.commands;

import com.sk89q.worldedit.command.util.annotation.Link;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.insilicon.hiantplugin.HiantPlugin;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.hiantplugin.commands.LinkCommand;
import xyz.prorickey.hiantplugin.database.Database;

public class LinkSlashCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("link")) return; // Not our slash command
        OptionMapping codeMapping = event.getOption("code");
        Database.LinkedAccount linkedAccount = HiantPlugin.getDatabase().getLinkedAccount(event.getUser().getIdLong());
        if(linkedAccount != null) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(linkedAccount.getMinecraftUUID());
            event.reply("Your account is already linked to `" + player.getName() + "`. If you want to unlink, use `/unlink`.")
                    .setEphemeral(true)
                    .queue();
        } else {
            if(codeMapping != null) {
                String code = codeMapping.getAsString();
                if (LinkCommand.codes.containsKey(code)) {
                    LinkCommand.LinkCode linkCode = LinkCommand.codes.get(code);
                    LinkCommand.minecraftLinking.remove(linkCode.getUuid());
                    LinkCommand.codes.remove(code);
                    if(!HiantPlugin.getDatabase().linkedBefore(linkCode.getUuid())) LinkCommand.giveKey(linkCode.getUuid());
                    HiantPlugin.getDatabase().linkAccount(event.getUser().getIdLong(), linkCode.getUuid().toString());
                    LinkCommand.recentlyLinked.put(linkCode.getUuid(), event.getUser().getName());
                    event.reply("Your account has been linked successfully!")
                            .setEphemeral(true)
                            .queue();
                } else {
                    event.reply("Invalid code.")
                            .setEphemeral(true)
                            .queue();
                }
            } else {
                String code = LinkCommand.generateCode();
                LinkCommand.codes.put(code, new LinkCommand.LinkCode(code, null, event.getUser().getIdLong()));
                LinkCommand.discordLinking.put(event.getUser().getIdLong(), System.currentTimeMillis());
                event.reply("Your link code is `" + code + "`. Do not give this to anyone." +
                                "\n\n" +
                                "In the Minecraft server, execute `/link " + code + "` to link your account.")
                        .setEphemeral(true)
                        .queue();
            }
        }
    }
}
