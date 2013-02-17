package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Represents a possibly dependent neighbor block. Example of use :
 * 
 * <pre>
 * {@code
 * Block solidBlock;
 * BlockFace f = BlockFace.WEST;
 * CreeperBlock cb = CreeperBlock.newBlock(solidBlock.getRelative (face));
 * NeighborBlock neighbor = new NeighborBlock (cb, face);
 * if (neighbor.isNeighbor())
 *   // neighbor depends on solidBlock
 * }
 * 
 * @author nitnelave
 * 
 */
public class NeighborBlock {
    private final CreeperBlock block;
    private final BlockFace face;

    /**
     * Constructor.
     * 
     * @param block
     *            The block represented, the one that is possibly dependent.
     * @param face
     *            The BlockFace with which it was obtained.
     */
    public NeighborBlock (Block block, BlockFace face) {
        this.block = CreeperBlock.newBlock (block.getState ());
        this.face = face;
    }

    /**
     * Check if the represented block is a dependent block and that it's
     * dependent face matches the one defined in the constructor.
     * 
     * @return Whether this block depends on the block provided in the
     *         constructor.
     */
    public boolean isNeighbor () {
        return block != null && block.getAttachingFace () == face.getOppositeFace ();
    }

    /**
     * Get the represented block.
     * 
     * @return The represented block.
     */
    public Block getBlock () {
        return block.getBlock ();
    }

}
