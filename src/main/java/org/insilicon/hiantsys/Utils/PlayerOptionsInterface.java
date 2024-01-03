package org.insilicon.hiantsys.Utils;

import org.bukkit.entity.Player;

import java.util.List;

@FunctionalInterface
public interface PlayerOptionsInterface {
    void run(List<Player> players);
}
