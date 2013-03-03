package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.BlockState;
import org.shininet.bukkit.playerheads.Skull;

import com.nitnelave.CreeperHeal.utils.CreeperLog;

/**
 * Skull (from the plugin PlayerHead) implementation of CreeperBlock.
 * 
 * @author meiskam
 * @author nitnelave
 */

class CreeperSkull extends CreeperBlock {

    /*
     * The PlayerHead skull.
     */
    private Skull skull = null;

    /*
     * Constructor.
     */
    protected CreeperSkull(BlockState blockState) {
        super(blockState);
        try {
            skull = new Skull(getLocation());
        } catch (NoSuchMethodError e) {
            CreeperLog.warning("Update PlayerHeads for compatibility with CreeperHeal.");
        }
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#update()
     */
    @Override
    public void update () {
        blockState.update (true);
        try {
            skull.place(getLocation());
        } catch (NoSuchMethodError e) {
            CreeperLog.warning("Update PlayerHeads for compatibility with CreeperHeal.");
        }
        catch (NullPointerException e) {
            CreeperLog.warning("Update PlayerHeads for compatibility with CreeperHeal.");
        }
    }
}