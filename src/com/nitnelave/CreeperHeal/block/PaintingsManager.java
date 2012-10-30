package com.nitnelave.CreeperHeal.block;

import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.server.EntityPainting;
import net.minecraft.server.EnumArt;
import net.minecraft.server.WorldServer;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Painting;
import org.bukkit.inventory.ItemStack;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.utils.CreeperUtils;

public class PaintingsManager {

	private static List<CreeperPainting> paintings = Collections.synchronizedList(new LinkedList<CreeperPainting>());					//paintings to be replaced
	private static CreeperHeal plugin;
	
	public PaintingsManager(CreeperHeal plugin) {
		PaintingsManager.plugin = plugin;
	}


	public static void replacePaintings(Date time)
	{
		Iterator<CreeperPainting> iter = paintings.iterator();
		while(iter.hasNext())
		{

			CreeperPainting cp = iter.next();
			if(cp.isBurnt())
			{
				if(cp.getDate().getTime() - time.getTime() < 0 || plugin.getFireList().size() == 0)
				{
					if(!replacePainting(cp.getPainting()))
					{
						if(plugin.getFireList().size() > 0)
						{
							cp.getWorld().dropItemNaturally(cp.getLocation(), new ItemStack(321, 1));
							iter.remove();
						}
						else
							cp.postPone(CreeperConfig.waitBeforeHealBurnt);

					}
					else 
						iter.remove();
				}
			}
			else
			{
				if(Math.abs(cp.getDate().getTime() - time.getTime()) < 500 || ExplodedBlockManager.getExplosionList().size() == 0);

				{
					if(!replacePainting(cp.getPainting()))
						cp.getWorld().dropItemNaturally(cp.getLocation(), new ItemStack(321, 1));
					iter.remove();
				}
			}
		}
	}


	private static boolean replacePainting(Painting painting) {
		BlockFace face = painting.getAttachedFace().getOppositeFace();
		Location loc = painting.getLocation().getBlock().getRelative(face.getOppositeFace()).getLocation();
		CraftWorld w = (CraftWorld) loc.getWorld();

		loc = CreeperUtils.getAttachingBlock(loc, painting.getArt(), face);

		int dir;
		switch(face) {
		case EAST:
		default:
			dir = 0;
			break;
		case NORTH:
			dir = 1;
			break;
		case WEST:
			dir = 2;
			break;
		case SOUTH:
			dir = 3;;
			break;
		}

		EntityPainting paint = new EntityPainting(w.getHandle(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), dir);
		EnumArt[] array = EnumArt.values();
		paint.art = array[painting.getArt().getId()];
		paint.setDirection(paint.direction);
		if (!(paint).survives()) {
			paint = null;
			return false;
		}
		w.getHandle().addEntity(paint);
		return true;

	}



	public static void checkForPaintings(Painting p, boolean postpone, boolean burnt)
	{
		Date time = new Date();
		if(postpone)
			time = new Date(time.getTime() + 1200000);

		if(burnt)
			paintings.add(new CreeperPainting(p, new Date(time.getTime() + 1000 * CreeperConfig.waitBeforeHealBurnt + 10000), true));
		else
			paintings.add(new CreeperPainting(p, time, false));
		WorldServer w = ((CraftWorld)p.getWorld()).getHandle();
		w.getEntity(p.getEntityId()).dead = true;
	}


	public static void replace_paintings()
	{
		for(CreeperPainting p : paintings)
		{
			if(!replacePainting(p.getPainting()))
				p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(321, 1));
		}
		paintings.clear();

	}


}
