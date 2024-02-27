package org.insilicon.hiantplugin.skript.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.cybercake.cyberapi.spigot.chat.Log;
import org.bukkit.block.Block;
import org.bukkit.block.DoubleChest;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

public class ExprConvertDoubleChestToBlock extends SimpleExpression<Block>  {

    static {
        try {
            Skript.registerExpression(ExprConvertDoubleChestToBlock.class, Block.class, ExpressionType.COMBINED, "converted doublechest of %object%");
        } catch(Exception ignored) {
            Log.error("Failed to load expression ConvertDoubleChestToBlock");
        }
    }

    private Expression<Object> blockState;

    @Override
    @Nullable
    protected Block[] get(Event event) {
        Object blockState1 = blockState.getSingle(event);
        if(blockState1 == null) return null;
        return new Block[] {((DoubleChest) blockState1).getLocation().getBlock()};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Block> getReturnType() {
        return Block.class;
    }

    @Override
    @Nullable
    public String toString(Event event, boolean b) {
        return "dumbass, go ask prorickey what you did wrong";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        blockState = (Expression<Object>) expressions[0];
        return true;
    }
}
