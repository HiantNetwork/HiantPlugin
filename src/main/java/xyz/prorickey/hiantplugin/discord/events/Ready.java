package xyz.prorickey.hiantplugin.discord.events;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.bukkit.Bukkit;
import org.insilicon.hiantplugin.HiantPlugin;
import org.jetbrains.annotations.NotNull;

public class Ready extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {

        event.getJDA().getGuildById(HiantPlugin.getConf().getString("discordBot.guildId"))
                        .updateCommands()
                        .addCommands(
                                Commands.slash("link", "Link your Minecraft account to your Discord account")
                                        .addOption(OptionType.STRING, "code", "The code you received in-game", false),
                                Commands.slash("unlink", "Unlink your Minecraft account from your Discord account")
                        ).queue();

        Bukkit.getScheduler().callSyncMethod(HiantPlugin.getPlugin(), () -> {
            HiantPlugin.getPlugin().getLogger().info("Discord bot is ready!");
            return null;
        });
    }
}
