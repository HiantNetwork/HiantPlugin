package xyz.prorickey.hiantplugin.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.bukkit.Bukkit;
import org.insilicon.hiantplugin.HiantPlugin;
import space.arim.libertybans.api.PunishmentType;
import space.arim.libertybans.api.punish.DraftPunishment;
import xyz.prorickey.hiantplugin.LibertyBansHook;
import xyz.prorickey.hiantplugin.discord.commands.LinkSlashCommand;
import xyz.prorickey.hiantplugin.discord.commands.UnlinkSlashCommand;
import xyz.prorickey.hiantplugin.discord.events.Ready;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HiantDiscord extends Thread {

    private Thread thread;

    public HiantDiscord() {
        super("HiantDiscordThread");

        this.start();
    }

    private JDA jda;

    private Map<PunishmentType, Color> colorMap = Map.of(
            PunishmentType.BAN, new Color(0xff1919),
            PunishmentType.KICK, new Color(0xff8219),
            PunishmentType.MUTE, new Color(0xffd919),
            PunishmentType.WARN, new Color(0xffe97d)
    );

    @Override
    public void run() {

        jda = JDABuilder.createLight(HiantPlugin.getConf().getString("discordBot.token"))
                .addEventListeners(new LinkSlashCommand())
                .addEventListeners(new UnlinkSlashCommand())
                .addEventListeners(new Ready())
                .build();

        Bukkit.getScheduler().runTaskTimerAsynchronously(HiantPlugin.getPlugin(), () -> {

            List<MessageEmbed> embeds = new ArrayList<>();
            for (DraftPunishment draft : LibertyBansHook.draftsToBeLogged) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle(draft.getType().toString() + " | " + draft.getVictim().toString());
                eb.setColor(colorMap.get(draft.getType()));
                eb.setDescription(draft.getReason());

                eb.addField("Punisher", draft.getOperator().toString(), false);
                eb.addField("Duration", draft.getDuration().toString()
                        .substring(2)
                        .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                        .toLowerCase(), false);

                embeds.add(eb.build());
                LibertyBansHook.draftsToBeLogged.remove(draft);
            }

            if(!embeds.isEmpty()) jda.getTextChannelById(HiantPlugin.getConf().getString("discordBot.modLogsId"))
                    .sendMessageEmbeds(embeds).queue();


        }, 0L, 20L);
    }

    public JDA getJDA() { return jda; }
}
