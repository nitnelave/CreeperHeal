package com.nitnelave.CreeperHeal.block;

import java.util.Date;

import net.minecraft.server.v1_4_5.EntityItemFrame;
import net.minecraft.server.v1_4_5.EntityPainting;
import net.minecraft.server.v1_4_5.EnumArt;

import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_4_5.CraftWorld;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.inventory.ItemStack;

import com.nitnelave.CreeperHeal.utils.CreeperLog;

public class CreeperPainting
{
	private Hanging hanging;
	private Date date;
	private boolean fire, postPoned = false;

	public CreeperPainting(Hanging p, Date d, boolean f)
	{
		hanging = p;
		date = d;
		fire = f;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date d)
	{
		date = d;
	}

	public boolean isBurnt()
	{
		return fire;
	}

	public Hanging getHanging()
	{
		return hanging;
	}

	public World getWorld()
	{
		return hanging.getWorld();
	}

	public Location getLocation()
	{
		return hanging.getLocation();
	}

	public void postPone(int delay)
	{
		date = new Date(date.getTime() + 1000 * delay);
	}

	public void setPostPoned(boolean b) {
		postPoned = b;
	}

	public boolean isPostPoned() {
		return postPoned;
	}


	public Location getAttachingBlock()
	{
		BlockFace face = hanging.getAttachedFace();
		Location loc = hanging.getLocation().getBlock().getRelative(face).getLocation();

		if(hanging instanceof Painting)
		{
			Art art = ((Painting) hanging).getArt();

			if(art.getBlockHeight() + art.getBlockWidth() < 5)
			{
				int i = 0, j = 0, k = art.getBlockWidth() - 1;
				switch(face){
				case EAST:
					j = -k;
					break;
				case NORTH:
					i = -k;
					break;
				default:
					break;
				}
				loc.add(i, 1-art.getBlockHeight(), j);
			}
			else 
			{ 

				if(art.getBlockHeight() != 3)
					loc.add(0, -1, 0);
				switch(face){
				case EAST:
					loc.add(0, 0, -1);
					break;
				case SOUTH:
					loc.add(0, 0, 0);
					break;
				case WEST:
					loc.add(0, 0, 0);
					break;
				case NORTH:
					loc.add(-1, 0, 0);
				default:
					break;
				}

			}
		}
		else
		{
			switch(face) {
			case EAST:
				loc.add(-1, 0, 0);
				break;
			case NORTH:
				loc.add(0, 0, -1);
				break;
			case SOUTH:
				loc.add(0, 0, 1);
				break;
			case WEST:
				loc.add(1, 0, 0);
				break;
			default:
				break;

			}
		}
		return loc;
	}

	private int getDirection() {

		BlockFace face = hanging.getAttachedFace();
		switch(face) {
		case NORTH:
		default:
			return 0;
		case WEST:
			return 3;
		case SOUTH:
			return 2;
		case EAST:
			return 1;
		}
	}

	public boolean replace() {
		Location loc = getAttachingBlock();
		CraftWorld w = (CraftWorld) loc.getWorld();

		int dir = getDirection();
		if(hanging instanceof Painting)
		{		
			Painting p = (Painting) hanging;
			EntityPainting paint = new EntityPainting(w.getHandle(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), dir);
			EnumArt[] array = EnumArt.values();
			paint.art = array[p.getArt().getId()];
			paint.setDirection(dir);
			if (!paint.survives()) {
				paint = null;
				return false;
			}
			w.getHandle().addEntity(paint);
		}
		else if(hanging instanceof ItemFrame)
		{
			CreeperLog.debug("item frame");
			ItemFrame f = (ItemFrame) hanging;
			EntityItemFrame frame = new EntityItemFrame(w.getHandle(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), dir);
			net.minecraft.server.v1_4_5.ItemStack stack = new net.minecraft.server.v1_4_5.ItemStack(f.getItem().getTypeId(), 1, 0);
			frame.a(stack);
			//set item rotation, direction
			if(!frame.survives()) {
				frame = null;
				return false;
			}
			w.getHandle().addEntity(frame);
		}
		return true;

	}

	public void drop() {
		if(hanging instanceof Painting)
			getWorld().dropItemNaturally(getLocation(), new ItemStack(321, 1));
		else if(hanging instanceof ItemFrame)
		{
			ItemFrame f = (ItemFrame) hanging;
			getWorld().dropItemNaturally(getLocation(), f.getItem());
			getWorld().dropItemNaturally(getLocation(), new ItemStack(389, 1));
		}

	}

}
