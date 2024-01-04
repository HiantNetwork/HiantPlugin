package org.insilicon.hiantsys.skript.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.cybercake.cyberapi.spigot.chat.Log;
import org.bukkit.event.Event;
import org.insilicon.hiantsys.systems.Regeneration;
import org.jetbrains.annotations.NotNull;

public class EffClearBox extends Effect {

    static {
        try {
            Skript.registerEffect(EffClearBox.class, "clear hiant box in %string%");
        }catch (Exception e) {
            Log.error("Failed to load effect clear box");
        }
    }

    private Expression<String> box;

    @Override
    protected void execute(@NotNull Event event) {
        String boxName = box.getSingle(event);
        if(boxName == null) return;
        try {
            Regeneration.resetBox(boxName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "Ask prorickey why this isn't working";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        box = (Expression<String>) expressions[0];
        return true;
    }
}
