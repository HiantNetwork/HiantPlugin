package xyz.prorickey.hiantplugin.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.insilicon.hiantplugin.HiantPlugin;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.hiantplugin.database.Database;

public class UnlinkSlashCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("unlink")) return; // Not our slash command
        Database.LinkedAccount linkedAccount = HiantPlugin.getDatabase().getLinkedAccount(event.getUser().getIdLong());
        if (linkedAccount != null) {
            HiantPlugin.getDatabase().unlinkAccount(event.getUser().getIdLong());
            event.reply("Your account has been unlinked successfully!")
                    .setEphemeral(true)
                    .queue();
        } else {
            event.reply("You do not have a linked account.")
                    .setEphemeral(true)
                    .queue();
        }
    }
}
