package org.insilicon.hiantplugin.skript.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.cybercake.cyberapi.spigot.chat.Log;
import org.bukkit.event.Event;
import org.insilicon.hiantplugin.systems.Regeneration;
import org.jetbrains.annotations.NotNull;

public class EffResetCastle extends Effect {

    static {
        try {
            Skript.registerEffect(EffResetCastle.class, "reset hiant's nether castle");
        } catch (Exception e) {
            Log.error("Failed to load effect nether castle");
        }
    }

    @Override
    protected void execute(Event event) {
        try {
            Regeneration.resetCastle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "Ask prorickey why this isn't working";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }
}
