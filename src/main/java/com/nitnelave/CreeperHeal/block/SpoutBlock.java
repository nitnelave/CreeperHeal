package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.getspout.spout.block.SpoutCraftBlock;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.material.CustomBlock;

public class SpoutBlock extends CreeperBlock {
    private final CustomBlock spoutBlock;

    protected SpoutBlock (BlockState state) {
        super (state);
        spoutBlock = ((SpoutCraftBlock) state.getBlock ()).getCustomBlock ();
    }

    @Override
    public void update () {
        SpoutManager.getMaterialManager ().overrideBlock (getBlock (), spoutBlock);
    }

    public static boolean isCustomBlock (BlockState blockState) {
        return ((SpoutCraftBlock) blockState.getBlock ()).getCustomBlock () != null;
    }

    @Override
    public boolean isDependent () {
        return getAttachingFace () != BlockFace.SELF || isDependent (getTypeId ());
    }
}
