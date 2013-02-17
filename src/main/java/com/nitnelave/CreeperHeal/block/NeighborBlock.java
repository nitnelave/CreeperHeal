package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class NeighborBlock {
    private final CreeperBlock block;
    private final BlockFace face;

    public NeighborBlock (Block block, BlockFace face) {
        this.block = CreeperBlock.newBlock (block.getState ());
        this.face = face;
    }

    public boolean isNeighbor () {
        return block != null && block.getAttachingFace () == face.getOppositeFace ();
    }

    public Block getBlock () {
        return block.getBlock ();
    }

}
