package com.nitnelave.CreeperHeal.block;

import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Represents an ArmorStand
 */
public class CreeperArmorStand implements Replaceable
{
    private final ArmorStand stand;
    private final ItemStack[] contents;
    private boolean wasRemoved = false;

    public CreeperArmorStand(ArmorStand stand)
    {
        this.stand = stand;
        this.contents = new ItemStack[]
                { 
                        stand.getHelmet(), stand.getChestplate(), stand.getLeggings(), stand.getBoots(),
                        stand.getItemInHand()
                };
        CreeperLog.debug("Armor: " + Arrays.toString(contents));
        remove();
    }

    @Override
    public boolean replace(boolean shouldDrop)
    {
        ArmorStand s = getWorld()
                .spawn(getLocation().getBlock().getRelative(BlockFace.DOWN).getLocation(), ArmorStand.class);
        s.setArms(stand.hasArms());
        s.setBasePlate(stand.hasBasePlate());
        s.setBodyPose(stand.getBodyPose());
        s.setCustomName(stand.getCustomName());
        s.setCustomNameVisible(stand.isCustomNameVisible());
        s.setGlowing(stand.isGlowing());
        s.setGravity(stand.hasGravity());
        s.setHeadPose(stand.getHeadPose());
        s.setLeftArmPose(stand.getLeftArmPose());
        s.setRightArmPose(stand.getRightArmPose());
        s.setLeftLegPose(stand.getLeftLegPose());
        s.setRightLegPose(stand.getRightLegPose());
        s.setMarker(stand.isMarker());
        s.setSmall(stand.isSmall());
        s.setVisible(stand.isVisible());

        s.setHelmet(contents[0]);
        s.setChestplate(contents[1]);
        s.setLeggings(contents[2]);
        s.setBoots(contents[3]);
        s.setItemInHand(contents[4]);

        s.teleport(stand.getLocation());
        return true;
    }

    @Override
    public Block getBlock()
    {
        return stand.getLocation().getBlock();
    }

    @Override
    public World getWorld()
    {
        return stand.getWorld();
    }

    @Override
    public Material getType()
    {
        return Material.ARMOR_STAND;
    }

    @Override
    public BlockFace getAttachingFace()
    {
        return BlockFace.DOWN;
    }

    @Override
    public Location getLocation()
    {
        return stand.getLocation();
    }

    @Override
    public boolean isDependent()
    {
        return true;
    }

    @Override
    public boolean drop(boolean forced)
    {
        if (forced || CreeperConfig.shouldDrop())
        {
            getWorld().dropItemNaturally(getLocation(), new ItemStack(Material.ARMOR_STAND, 1));
            for (ItemStack itemStack : contents)
                if (itemStack != null && itemStack.getType() != Material.AIR)
                    getWorld().dropItemNaturally(getLocation(), itemStack);
            return true;
        }
        return false;
    }

    @Override
    public void remove()
    {
        if (!wasRemoved)
        {
            wasRemoved = true;
            ItemStack air = new ItemStack(Material.AIR);
            stand.setChestplate(air);
            stand.setHelmet(air);
            stand.setLeggings(air);
            stand.setBoots(air);
            stand.setItemInHand(air);

            CreeperLog.debug("Removing armor, chestplate = " + stand.getChestplate().getType());
            stand.remove();
        }
    }
}
