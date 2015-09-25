package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.Banner;

/**
 * Banner implementation of CreeperBlock.
 * 
 * @author drexplosionpd
 * 
 */
public class CreeperBanner extends CreeperBlock
{
	
    /*
     * Constructor.
     */
    protected CreeperBanner(Banner banner)
    {
        super(banner);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.nitnelave.CreeperHeal.block.CreeperBlock#update()
     */
    @Override
    public void update()
    {
        super.update();
        Banner state = (Banner) getBlock().getState();
        Banner banner = (Banner) blockState;
        state.setBaseColor(banner.getBaseColor());
        state.setPatterns(banner.getPatterns());
        
        state.getData().setData(banner.getRawData());
        state.update(true);
    }
}
