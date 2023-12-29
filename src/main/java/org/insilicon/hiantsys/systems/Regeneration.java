package org.insilicon.hiantsys.systems;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.insilicon.hiantsys.Hiantsys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Regeneration implements Listener {

    public static void resetBox(String region) throws NullPointerException {
        World world = Hiantsys.getPlugin().getWorld();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        ProtectedRegion reg = container.get(world).getRegion(region);
        if(reg == null) {
            throw new NullPointerException("Region not found");
        }
        EditSession session = WorldEdit.getInstance().newEditSession(world);
        session.setBlocks((Region) new CuboidRegion(reg.getMinimumPoint(), reg.getMaximumPoint()), BlockTypes.AIR.getDefaultState());
    }

    public static void resetCastle() throws IOException {
        File folder = Hiantsys.getPlugin().getFolder();
        World world = Hiantsys.getPlugin().getWorld();
        EditSession session = WorldEdit.getInstance().newEditSession(world);
        FileConfiguration config = Hiantsys.getPlugin().getConfig();
        ClipboardFormat format = ClipboardFormats.findByFile(new File(folder, "nether_castle.schem"));
        assert format != null;
        ClipboardReader reader = format.getReader(new FileInputStream(new File(folder, "nether_castle.schem")));
        Clipboard clipboard = reader.read();
        ClipboardHolder holder = new ClipboardHolder(clipboard);
        int cX = config.getInt("regeneration.castle.x");
        int cY = config.getInt("regeneration.castle.y");
        int cZ = config.getInt("regeneration.castle.z");
        BlockVector3 CastlePos = BlockVector3.at(cX, cY, cZ);
        Operation operation = holder
                .createPaste(session)
                .to(CastlePos)
                .ignoreAirBlocks(false)
                .build();
        Operations.complete(operation);
        session.close();
    }

}


