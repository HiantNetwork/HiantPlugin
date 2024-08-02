package xyz.prorickey.hiantplugin.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.insilicon.hiantplugin.HiantPlugin;
import xyz.prorickey.hiantplugin.discord.commands.LinkSlashCommand;
import xyz.prorickey.hiantplugin.discord.commands.UnlinkSlashCommand;
import xyz.prorickey.hiantplugin.discord.events.Ready;

public class HiantDiscord extends Thread {

    private Thread thread;

    public HiantDiscord() {
        super("HiantDiscordThread");

        this.start();
    }

    private JDA jda;

    @Override
    public void run() {

        jda = JDABuilder.createLight(HiantPlugin.getConf().getString("discordBot.token"))
                .addEventListeners(new LinkSlashCommand())
                .addEventListeners(new UnlinkSlashCommand())
                .addEventListeners(new Ready())
                .build();
    }

    public JDA getJDA() { return jda; }
}
