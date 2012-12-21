package com.nitnelave.CreeperHeal.block;


import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.material.Skull;

import com.nitnelave.CreeperHeal.utils.CreeperLog;

@SuppressWarnings("unused")
public class CreeperHead extends CreeperBlock {

	protected CreeperHead(BlockState blockState) {
		super(blockState);
		CreeperLog.debug("skull");
		World world = blockState.getWorld();
		Location loc = getLocation();
		
		/*TileEntitySkull tileentityskull = (TileEntitySkull) world.getTileEntity(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        if (tileentityskull.getSkullType() == 3 && tileentityskull.getExtraType() != null && tileentityskull.getExtraType().length() > 0) {
            itemstack.setTag(new NBTTagCompound());
            itemstack.getTag().setString("SkullOwner", tileentityskull.getExtraType());
        }

        this.b(world, i, j, k, itemstack);*/
	}
	
	@Override
	public void update(boolean force) {
		super.update(force);
		Skull skull = (Skull) blockState;
		((Skull) blockState.getBlock().getState()).setFacingDirection(skull.getFacing());
	}

}
