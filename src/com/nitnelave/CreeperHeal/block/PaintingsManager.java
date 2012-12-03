package com.nitnelave.CreeperHeal.block;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.server.WorldServer;

import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Hanging;

import com.nitnelave.CreeperHeal.config.CreeperConfig;

public class PaintingsManager {

	private static List<CreeperPainting> paintings = Collections.synchronizedList(new LinkedList<CreeperPainting>());					//paintings to be replaced


	public static void replacePaintings(Date time)
	{
		Iterator<CreeperPainting> iter = paintings.iterator();
		while(iter.hasNext())
		{

			CreeperPainting cp = iter.next();
			if(cp.isBurnt())
			{
				if(cp.getDate().getTime() - time.getTime() < 0)
				{
					if(!cp.replace())
					{
						if(cp.isPostPoned() || (!CreeperConfig.lightweightMode && BurntBlockManager.isIndexEmpty()))
						{
							cp.drop();
							iter.remove();
						}
						else
						{
							cp.postPone(CreeperConfig.waitBeforeHealBurnt);
							cp.setPostPoned(true);
						}

					}
					else 
						iter.remove();
				}
			}
			else
			{
				if(Math.abs(cp.getDate().getTime() - time.getTime()) < 500 || ExplodedBlockManager.getExplosionList().isEmpty())
				{
					if(!cp.replace())
						cp.drop();
					iter.remove();
				}
			}
		}
	}



	public static void checkPainting(Hanging h, boolean postpone, boolean burnt)
	{
		Date time = new Date();
		if(postpone)
			time = new Date(time.getTime() + 1200000);

		if(burnt)
			paintings.add(new CreeperPainting(h, new Date(time.getTime() + 1000 * CreeperConfig.waitBeforeHealBurnt + 10000), true));
		else
			paintings.add(new CreeperPainting(h, new Date(time.getTime() + 1000 * CreeperConfig.waitBeforeHeal + 150000 * CreeperConfig.blockPerBlockInterval), false));
		WorldServer w = ((CraftWorld)h.getWorld()).getHandle();
		w.getEntity(h.getEntityId()).dead = true;
	}
	
	
	public static void checkForPaintings(Location loc, double radius, boolean postpone, boolean fire) {
		Collection<Hanging> hangings = loc.getWorld().getEntitiesByClass(Hanging.class);
		Iterator<Hanging> iter = hangings.iterator();
		while(iter.hasNext()) {
			Hanging h = iter.next();
			if(h.getLocation().distance(loc) < radius) {
				checkPainting(h, postpone, fire);
			}
		}
	}


	public static void replace_paintings()
	{
		for(CreeperPainting p : paintings)
		{
			if(!p.replace())
				p.drop();
		}
		paintings.clear();

	}


}
