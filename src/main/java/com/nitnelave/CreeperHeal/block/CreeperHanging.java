package com.nitnelave.CreeperHeal.block;

import java.util.Date;

import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_4_R1.CraftWorld;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.inventory.ItemStack;

import com.nitnelave.CreeperHeal.config.CreeperConfig;

/**
 * Represents a hanging, i.e. either a painting or an ItemFrame.
 * 
 * @author nitnelave
 * 
 */
public class CreeperHanging implements Replaceable
{
    private final Hanging hanging;
    private Date date;
    private final boolean fire;
    private boolean postPoned = false;
    private final Location location;

    /**
     * Constructor. The date is the time the hanging was destroyed, and fire is
     * whether the hanging was destroyed by fire, or an explosion.
     * 
     * @param hanging
     *            The hanging destroyed.
     * @param time
     *            The time of the destruction.
     * @param fire
     *            Whether the hanging was destroyed by fire.
     */
    public CreeperHanging (Hanging hanging, Date time, boolean fire)
    {
        this.hanging = hanging;
        date = time;
        this.fire = fire;
        location = computeLocation ();
    }

    /**
     * Get the location of the painting, so that it is right in front of the
     * block it should be attached to.
     * 
     * @return The location of the painting.
     */
    private Location computeLocation () {
        BlockFace face = hanging.getAttachedFace ();
        Location loc = hanging.getLocation ().getBlock ().getRelative (face).getLocation ();

        if (hanging instanceof Painting)
        {
            Art art = ((Painting) hanging).getArt ();

            if (art.getBlockHeight () + art.getBlockWidth () < 5)
            {
                int i = 0, j = 0, k = art.getBlockWidth () - 1;
                switch (face)
                {
                    case EAST:
                        j = -k;
                        break;
                    case NORTH:
                        i = -k;
                    default:
                        break;
                }
                loc.add (i, 1 - art.getBlockHeight (), j);
            }
            else
            {

                if (art.getBlockHeight () != 3)
                    loc.add (0, -1, 0);
                switch (face)
                {
                    case EAST:
                        loc.add (0, 0, -1);
                        break;
                    case NORTH:
                        loc.add (-1, 0, 0);
                    default:
                        break;
                }

            }
        }
        return loc.getBlock ().getRelative (face.getOppositeFace ()).getLocation ();
    }
    /**
     * Get the time the hanging was destroyed, or later if the replacement is
     * postponed.
     * 
     * @return
     */
    public Date getDate()
    {
        return date;
    }

    /**
     * Get whether the hanging was destroyed by fire.
     * 
     * @return Whether the hanging was destroyed by fire.
     */
    public boolean isBurnt()
    {
        return fire;
    }

    @Override
    public World getWorld()
    {
        return hanging.getWorld();
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#getLocation()
     */
    @Override
    public Location getLocation()
    {
        return location;
    }

    private void postPone (int delay) {
        date = new Date (date.getTime () + 1000 * delay);
        postPoned = true;
    }

    private int getIntDirection() {
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


    @Override
    public boolean replace(boolean shouldDrop) {
        Block block = location.getBlock ().getRelative (hanging.getAttachedFace ());
        CraftWorld w = (CraftWorld) block.getWorld ();

        int dir = getIntDirection();
        if(hanging instanceof Painting)
        {
            Painting p = (Painting) hanging;
            net.minecraft.server.v1_4_R1.EntityPainting paint = new net.minecraft.server.v1_4_R1.EntityPainting (w.getHandle (), block.getX (), block.getY (),
                    block.getZ (), dir);
            net.minecraft.server.v1_4_R1.EnumArt[] array = net.minecraft.server.v1_4_R1.EnumArt.values ();
            paint.art = array[p.getArt().getId()];
            paint.setDirection(dir);
            if (!paint.survives()) {
                paint = null;
                return postpone();
            }
            w.getHandle().addEntity(paint);
        }
        else if(hanging instanceof ItemFrame)
        {
            ItemFrame f = (ItemFrame) hanging;
            net.minecraft.server.v1_4_R1.EntityItemFrame frame = new net.minecraft.server.v1_4_R1.EntityItemFrame (w.getHandle (), block.getX (),
                    block.getY (), block.getZ (), dir);
            net.minecraft.server.v1_4_R1.ItemStack stack = new net.minecraft.server.v1_4_R1.ItemStack(f.getItem().getTypeId(), 1, 0);
            frame.a(stack);
            //TODO: set item rotation, direction
            if(!frame.survives()) {
                frame = null;
                return postpone();
            }
            w.getHandle().addEntity(frame);
        }
        return true;

    }

    private boolean postpone () {
        if (postPoned || (!CreeperConfig.lightweightMode && BurntBlockManager.isIndexEmpty ()))
        {
            drop ();
            return true;
        }
        postPone (CreeperConfig.waitBeforeHealBurnt);
        return false;
    }

    /*

	public boolean replace(boolean shouldDrop) {
		Location loc = getAttachingBlock();
		CreeperLog.displayBlockLocation(loc.getBlock(), false);
		World w = loc.getWorld();

		if (Hanging.class.isAssignableFrom(Painting.class))
			CreeperLog.debug("stuff");

		if (hanging instanceof Painting)
		{
			try{
			Painting p = w.spawn(loc, Painting.class);
			p.setArt(((Painting) hanging).getArt(), true);
			p.setFacingDirection(hanging.getFacing(), true);
			}catch (IllegalArgumentException e) {
				CreeperLog.debug("Noo!");
			}
		}
		else if (hanging instanceof ItemFrame)
		{
			ItemFrame f = w.spawn(loc, ItemFrame.class);
			f.setItem(((ItemFrame) hanging).getItem());
			f.setRotation(((ItemFrame) hanging).getRotation());
			f.setFacingDirection(hanging.getFacing(), true);
		}
		return true;
	}*/



    /**
     * Drop the hanging on the ground.
     */
    @Override
    public void drop () {
        if(hanging instanceof Painting)
            getWorld().dropItemNaturally(getLocation(), new ItemStack(321, 1));
        else if(hanging instanceof ItemFrame)
        {
            ItemFrame f = (ItemFrame) hanging;
            getWorld().dropItemNaturally(getLocation(), f.getItem());
            getWorld().dropItemNaturally(getLocation(), new ItemStack(389, 1));
        }

    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#getBlock()
     */
    @Override
    public Block getBlock() {
        return hanging.getLocation().getBlock();
    }

    /*
     * (non-Javadoc)
     * @see com.nitnelave.CreeperHeal.block.Replaceable#getTypeId()
     */
    @Override
    public int getTypeId() {
        return 0;
    }


    @Override
    public BlockFace getAttachingFace () {
        return hanging.getAttachedFace ();
    }

    @Override
    public boolean isDependent () {
        return true;
    }

}
