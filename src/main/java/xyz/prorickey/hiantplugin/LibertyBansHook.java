package xyz.prorickey.hiantplugin;

import space.arim.libertybans.api.LibertyBans;
import space.arim.libertybans.api.event.PunishEvent;
import space.arim.libertybans.api.punish.DraftPunishment;
import space.arim.omnibus.Omnibus;
import space.arim.omnibus.OmnibusProvider;
import space.arim.omnibus.events.EventConsumer;
import space.arim.omnibus.events.ListenerPriorities;
import xyz.prorickey.hiantplugin.discord.HiantDiscord;

import java.util.ArrayList;
import java.util.List;

public class LibertyBansHook {

    private final Omnibus omnibus;
    private final LibertyBans libertyBans;

    public LibertyBansHook(Omnibus omnibus, LibertyBans libertyBans) {
        this.omnibus = omnibus;
        this.libertyBans = libertyBans;

        listenToPunishEvent();
    }

    public static LibertyBansHook create() {
        Omnibus omnibus = OmnibusProvider.getOmnibus();
        LibertyBans libertyBans = omnibus.getRegistry().getProvider(LibertyBans.class).orElseThrow();
        return new LibertyBansHook(omnibus, libertyBans);
    }

    public static List<DraftPunishment> draftsToBeLogged = new ArrayList<>();

    public void listenToPunishEvent() {
        EventConsumer<PunishEvent> listener = punishEvent -> draftsToBeLogged.add(punishEvent.getDraftSanction());

        this.omnibus.getEventBus().registerListener(PunishEvent.class, ListenerPriorities.LOWEST, listener);
    }

}
