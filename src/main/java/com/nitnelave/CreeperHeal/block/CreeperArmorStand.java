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

import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents an ArmorStand
 */
public class CreeperArmorStand implements Replaceable
{
    private final ArmorStand stand;
    private final ArrayList<ItemStack> contents = new ArrayList<>();
    private boolean wasRemoved = false;

    public CreeperArmorStand(ArmorStand stand)
    {
        this.stand = stand;
        Collections.addAll(contents,
                           stand.getBoots(), stand.getChestplate(), stand.getHelmet(), stand.getItemInHand(),
                           stand.getLeggings());
        CreeperLog.debug("Armor: " + contents);
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
        s.setBoots(stand.getBoots());
        s.setChestplate(stand.getChestplate());
        s.setHeadPose(stand.getHeadPose());
        s.setHelmet(stand.getHelmet());
        s.setItemInHand(stand.getItemInHand());
        s.setLeftArmPose(stand.getLeftArmPose());
        s.setRightArmPose(stand.getRightArmPose());
        s.setLeftLegPose(stand.getLeftLegPose());
        s.setRightLegPose(stand.getRightLegPose());
        s.setLeggings(stand.getLeggings());
        s.setMarker(stand.isMarker());
        s.setSmall(stand.isSmall());
        s.setVisible(stand.isVisible());

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
            for (ItemStack s : contents)
                if (s.getType() != Material.AIR)
                    getWorld().dropItemNaturally(getLocation(), s);
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
