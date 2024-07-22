package xyz.prorickey.hiantplugin.discord.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.insilicon.hiantplugin.HiantPlugin;
import org.jetbrains.annotations.NotNull;
import xyz.prorickey.hiantplugin.commands.LinkCommand;

public class LinkSlashCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (!event.getName().equals("link")) return; // Not our slash command
        OptionMapping codeMapping = event.getOption("code");
        if(HiantPlugin.getDatabase().getLinkedAccount(event.getIdLong()) != null) {
            event.reply("You already have a linked account.")
                    .setEphemeral(true)
                    .queue();
            return;
        }
        if(codeMapping != null) {
            String code = codeMapping.getAsString();
            if (LinkCommand.codes.containsKey(code)) {
                LinkCommand.LinkCode linkCode = LinkCommand.codes.get(code);
                LinkCommand.minecraftLinking.remove(linkCode.getUuid());
                LinkCommand.codes.remove(code);
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
