package com.nitnelave.CreeperHeal.block;

import com.nitnelave.CreeperHeal.config.CreeperConfig;
import org.bukkit.Bukkit;
import org.bukkit.block.Banner;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

/**
 * Banner implementation of CreeperBlock.
 *
 * @author Jikoo
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
     * @see com.nitnelave.CreeperHeal.block.Replaceable#drop(boolean)
     */
    @Override
    public boolean drop(boolean forced)
    {
        if (forced || CreeperConfig.shouldDrop())
        {
            ItemStack itemStack = new ItemStack(blockState.getType());
            BannerMeta bannerMeta = ((BannerMeta) Bukkit.getItemFactory().getItemMeta(blockState.getType()));
            bannerMeta.setPatterns(((Banner) blockState).getPatterns());
            itemStack.setItemMeta(bannerMeta);
            blockState.getWorld().dropItemNaturally(blockState.getLocation().add(0.5, 0.5, 0.5), itemStack);
            return true;
        }
        return false;
    }

}
