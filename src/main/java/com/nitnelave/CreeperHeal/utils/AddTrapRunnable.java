package com.nitnelave.CreeperHeal.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

import com.nitnelave.CreeperHeal.block.CreeperExplosion;

public class AddTrapRunnable implements Runnable {

    private final CreeperExplosion cEx;
    private final Block block;
    private final Material type;

    public AddTrapRunnable (CreeperExplosion cEx, Block b, Material t) {
        this.cEx = cEx;
        block = b;
        type = t;
    }

    @Override
    public void run () {
        BlockState tmp_state = block.getState ();

        block.setType (type); //set the block to tnt

        cEx.record (block);

        tmp_state.update (true); //set it back to what it was

    }

}
