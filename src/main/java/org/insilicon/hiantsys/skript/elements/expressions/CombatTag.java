package org.insilicon.hiantsys.skript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class CombatTag extends SimpleExpression<Boolean> {

    private Expression<Player> player;

    static {
        Skript.registerExpression(CombatTag.class, Boolean.class, ExpressionType.COMBINED, "[the] combat tag status of %player%");
    }

    @Override
    protected Boolean[] get(Event event) {
        Player p = player.getSingle(event);
        // Simply return if the player's combat tag is true or not
        // We need to figure out if we need expressions for who they are in combat with
        return new Boolean[0];
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends Boolean> getReturnType() {
        return Boolean.class;
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "Your dumb asf";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        player = (Expression<Player>) exprs[0];
        return true;
    }
}
