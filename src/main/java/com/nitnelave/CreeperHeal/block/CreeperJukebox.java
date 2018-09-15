package com.nitnelave.CreeperHeal.block;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WCfgVal;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Jukebox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;

/**
 * Jukebox implementation of CreeperBlock.
 *
 * @author Jikoo
 */
public class CreeperJukebox extends CreeperBlock
{

    /*
     * Constructor.
     */
    CreeperJukebox(Jukebox blockState)
    {
        super(blockState);
        
    }

    /*
     * @see com.nitnelave.CreeperHeal.block.Replaceable#remove()
     */
    @Override
    public void remove()
    {
        Jukebox jukebox = ((Jukebox) blockState);

        Listener recordDropListener = null;
        
        if (!CreeperConfig.getWorld(getWorld()).getBool(WCfgVal.DROP_CHEST_CONTENTS))
        {
            recordDropListener = new RecordDropListener(jukebox.getPlaying());
            Bukkit.getPluginManager().registerEvents(recordDropListener, CreeperHeal.getInstance());
        }

        super.remove();

        if (recordDropListener != null) {
            HandlerList.unregisterAll(recordDropListener);
        }
    }

    /*
     * @see com.nitnelave.CreeperHeal.block.Replaceable#update()
     */
    @Override
    public void update()
    {
        if (CreeperConfig.getWorld(getWorld()).getBool(WCfgVal.DROP_CHEST_CONTENTS))
            blockState.getBlock().setType(Material.JUKEBOX);
        else
            super.update();
    }

    private class RecordDropListener implements Listener {

        private final Material record;

        private RecordDropListener(Material record) {
            this.record = record;
        }

        @EventHandler(ignoreCancelled = true)
        public void onItemSpawn(ItemSpawnEvent event) {
            if (event.getEntity().getItemStack().getType() == record) {
                event.setCancelled(true);
            }
        }
    }

}
