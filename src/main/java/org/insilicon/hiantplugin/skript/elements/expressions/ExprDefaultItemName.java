package org.insilicon.hiantplugin.skript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.cybercake.cyberapi.spigot.chat.Log;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ExprDefaultItemName extends SimpleExpression<String> {

    static {
        try {
            Skript.registerExpression(ExprDefaultItemName.class, String.class, ExpressionType.COMBINED, "[the] default item name of %itemstacks%");
        } catch(Exception ignored) {
            Log.error("Failed to load expression DefaultItemName");
        }
    }

    private Expression<ItemStack> item;

    @Override
    @Nullable
    protected String[] get(@NotNull Event event) {
        ItemStack istack = item.getSingle(event);
        if(istack == null) return null;
        return new String[] {PlainTextComponentSerializer.plainText().serialize(istack.displayName())};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public @NotNull String toString(Event event, boolean debug) {
        return "dumbass, go ask prorickey what you did wrong";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        item = (Expression<ItemStack>) expressions[0];
        return true;
    }
}
