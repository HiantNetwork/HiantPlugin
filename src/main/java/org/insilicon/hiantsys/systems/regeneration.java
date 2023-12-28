package org.insilicon.hiantsys.systems;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
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
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.insilicon.hiantsys.Hiantsys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class regeneration implements Listener {

    private int interval;

    //Configs




    public regeneration() {
        //Setup system to run reset() every regeneration.interval

        this.interval = Hiantsys.getPlugin().getConfig().getInt("regeneration.interval", 60); // Default to 60 if not set



        // Setup system to run reset() every regeneration.interval
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    reset();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }.runTaskTimer(Hiantsys.getPlugin(), 0L, interval * 20L); // Convert interval to ticks (20 ticks = 1 second)


    }


    public void reset() throws Exception {

        //Tell everyone that the server is regenerating and lag may occur
        //Make it colored

        //For every player
        for (int i = 0; i < Hiantsys.getPlugin().getServer().getOnlinePlayers().size(); i++) {
            //Get player
            org.bukkit.entity.Player player = Hiantsys.getPlugin().getServer().getOnlinePlayers().iterator().next();
            //Send message
            player.sendMessage(ChatColor.GREEN + "Server is regenerating,"+ChatColor.RED+" lag may occur");
        }

        //Get folder
        File folder = Hiantsys.getPlugin().getFolder();

        //Get world
        World world = Hiantsys.getPlugin().getWorld();

        //Create session
        EditSession session = WorldEdit.getInstance().newEditSession(world);



        FileConfiguration config = Hiantsys.getPlugin().getConfig();





        //Clean Grass
        int gXa = config.getInt("regeneration.grass.xA");
        int gYa = config.getInt("regeneration.grass.yA");
        int gZa = config.getInt("regeneration.grass.zA");

        int gXb = config.getInt("regeneration.grass.xB");
        int gYb = config.getInt("regeneration.grass.yB");
        int gZb = config.getInt("regeneration.grass.zB");

        BlockVector3 grassA = BlockVector3.at(gXa, gYa, gZa);
        BlockVector3 grassB = BlockVector3.at(gXb, gYb, gZb);

        //Set to AIR
        session.setBlocks((Region) new CuboidRegion(grassA, grassB), BlockTypes.AIR.getDefaultState());


        //Clean Caves

        int cXa = config.getInt("regeneration.caves.xA");
        int cYa = config.getInt("regeneration.caves.yA");
        int cZa = config.getInt("regeneration.caves.zA");

        int cXb = config.getInt("regeneration.caves.xB");
        int cYb = config.getInt("regeneration.caves.yB");
        int cZb = config.getInt("regeneration.caves.zB");

        BlockVector3 cavesA = BlockVector3.at(cXa, cYa, cZa);
        BlockVector3 cavesB = BlockVector3.at(cXb, cYb, cZb);

        //Set to AIR
        session.setBlocks((Region) new CuboidRegion(cavesA, cavesB), BlockTypes.AIR.getDefaultState());

        //Clean Deep Caves

        int dXa = config.getInt("regeneration.deepcaves.xA");
        int dYa = config.getInt("regeneration.deepcaves.yA");
        int dZa = config.getInt("regeneration.deepcaves.zA");

        int dXb = config.getInt("regeneration.deepcaves.xB");
        int dYb = config.getInt("regeneration.deepcaves.yB");
        int dZb = config.getInt("regeneration.deepcaves.zB");

        BlockVector3 deepcavesA = BlockVector3.at(dXa, dYa, dZa);
        BlockVector3 deepcavesB = BlockVector3.at(dXb, dYb, dZb);

        //Set to AIR
        session.setBlocks((Region) new CuboidRegion(deepcavesA, deepcavesB), BlockTypes.AIR.getDefaultState());

        //Clean Nether

        int nXa = config.getInt("regeneration.nether.xA");
        int nYa = config.getInt("regeneration.nether.yA");
        int nZa = config.getInt("regeneration.nether.zA");

        int nXb = config.getInt("regeneration.nether.xB");
        int nYb = config.getInt("regeneration.nether.yB");
        int nZb = config.getInt("regeneration.nether.zB");

        BlockVector3 netherA = BlockVector3.at(nXa, nYa, nZa);
        BlockVector3 netherB = BlockVector3.at(nXb, nYb, nZb);

        //Set to AIR

        session.setBlocks((Region) new CuboidRegion(netherA, netherB), BlockTypes.AIR.getDefaultState());

        //Get the schem file of nether_castle.schem
        ClipboardFormat format = ClipboardFormats.findByFile(new File(folder, "nether_castle.schem"));
        assert format != null;
        ClipboardReader reader = format.getReader(new FileInputStream(new File(folder, "nether_castle.schem")));
        Clipboard clipboard = reader.read();

        ClipboardHolder holder = new ClipboardHolder(clipboard);

        int cX = config.getInt("regeneration.castle.x");
        int cY = config.getInt("regeneration.castle.y");
        int cZ = config.getInt("regeneration.castle.z");

        BlockVector3 CastlePos = BlockVector3.at(cX, cY, cZ);

        //Paste Castle
        Operation operation = holder
                .createPaste(session)
                .to(CastlePos)
                .ignoreAirBlocks(false)
                .build();

        Operations.complete(operation);

        //close session
        session.close();



    }

}


