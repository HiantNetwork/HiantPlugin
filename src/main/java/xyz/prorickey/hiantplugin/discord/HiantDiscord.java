package xyz.prorickey.hiantplugin.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.insilicon.hiantplugin.HiantPlugin;
import space.arim.libertybans.api.*;
import space.arim.libertybans.api.punish.DraftPunishment;
import xyz.prorickey.hiantplugin.LibertyBansHook;
import xyz.prorickey.hiantplugin.discord.commands.LinkSlashCommand;
import xyz.prorickey.hiantplugin.discord.commands.UnlinkSlashCommand;
import xyz.prorickey.hiantplugin.discord.events.Ready;

import java.awt.*;
import java.sql.Time;
import java.time.Instant;
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

    @Override
    public void run() {

        jda = JDABuilder.createLight(HiantPlugin.getConf().getString("discordBot.token"))
                .addEventListeners(new LinkSlashCommand())
                .addEventListeners(new UnlinkSlashCommand())
                .addEventListeners(new Ready())
                .build();

        Bukkit.getScheduler().runTaskTimerAsynchronously(HiantPlugin.getPlugin(), () -> {

            List<MessageEmbed> embeds = new ArrayList<>();
            for (DraftPunishment draft : new ArrayList<>(LibertyBansHook.draftsToBeLogged)) {
                MessageEmbed embed = buildEmbedFromDraft(draft);
                embeds.add(embed);
                LibertyBansHook.draftsToBeLogged.remove(draft);
            }

            if(!embeds.isEmpty()) jda.getTextChannelById(HiantPlugin.getConf().getString("discordBot.modLogsId"))
                    .sendMessageEmbeds(embeds).queue();


        }, 0L, 20L);
    }

    public JDA getJDA() { return jda; }

    private static final String baseHeadAPI = "https://minotar.net/helm/<uuid>/256.png";
    private static final String defaultSkin = "c06f89064c8a49119c29ea1dbd1aab82"; // MHF_Steve
    private static final Map<PunishmentType, Color> colorMap = Map.of(
            PunishmentType.BAN, new Color(0xff1919),
            PunishmentType.KICK, new Color(0xff8219),
            PunishmentType.MUTE, new Color(0xffd919),
            PunishmentType.WARN, new Color(0xffe97d)
    );

    private static MessageEmbed buildEmbedFromDraft(DraftPunishment draft) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setColor(colorMap.get(draft.getType()));
        eb.setTimestamp(Instant.now());
        eb.setDescription(draft.getReason());

        String punishmentString = draft.getType().toString().toUpperCase();

        // Victim Data
        Victim victim = draft.getVictim();
        switch(victim.getType()) {
            case Victim.VictimType.PLAYER -> {
                PlayerVictim playerVictim = (PlayerVictim) victim;
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerVictim.getUUID()); // Safe since we are on another thread
                eb.setTitle(punishmentString + " | " + offlinePlayer.getName());
                eb.setThumbnail(baseHeadAPI.replace("<uuid>", playerVictim.getUUID().toString().replaceAll("-", "")));
                eb.addField("Victim Data",
                        "UUID: " + playerVictim.getUUID().toString(),
                        false);
            }
            case Victim.VictimType.ADDRESS -> {
                AddressVictim addressVictim = (AddressVictim) victim;
                eb.setThumbnail(baseHeadAPI.replace("<uuid>", defaultSkin));
                eb.setTitle(punishmentString + " | " + addressVictim.getAddress());
            }
            case Victim.VictimType.COMPOSITE -> {
                CompositeVictim compositeVictim = (CompositeVictim) victim;
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(compositeVictim.getUUID());
                eb.setTitle(punishmentString + " | " + offlinePlayer.getName());
                eb.setThumbnail(baseHeadAPI.replace("<uuid>", compositeVictim.getUUID().toString().replaceAll("-", "")));
                eb.addField("Victim Data",
                        "UUID: " + compositeVictim.getUUID().toString() + "\n" +
                                "IP: " + compositeVictim.getAddress(),
                        false);
            }
        }

        // Operator Data
        Operator operator = draft.getOperator();
        switch(operator.getType()) {
            case Operator.OperatorType.PLAYER -> {
                PlayerOperator playerOperator = (PlayerOperator) operator;
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerOperator.getUUID());
                eb.addField("Operator",
                        "Player Name: " + offlinePlayer.getName() +
                                "UUID: " + playerOperator.getUUID().toString(),
                        false);
            }
            case Operator.OperatorType.CONSOLE -> eb.addField("Operator", "Console", false);
        }

        if(!draft.getType().equals(PunishmentType.KICK)) {
            if(draft.getDuration().toSeconds() == 0) eb.addField("Duration", "Permanent", false);
            else eb.addField("Duration", DurationFormatUtils.formatDuration(draft.getDuration().toMillis(), "[M'm'd'd'H'hrs'm'mins's'secs']"), false);
        }

        return eb.build();
    }
}
