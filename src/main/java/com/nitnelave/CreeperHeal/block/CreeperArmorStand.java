package com.nitnelave.CreeperHeal.block;

import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.utils.CreeperLog;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * Represents an ArmorStand
 */
public class CreeperArmorStand implements Replaceable
{
    private final ArmorStand stand;
    private final ItemStack mainHand, offHand;
    private final ItemStack[] contents;
    private boolean wasRemoved = false;

    CreeperArmorStand(ArmorStand stand)
    {
        this.stand = stand;
        EntityEquipment equipment = stand.getEquipment();
        this.mainHand = equipment.getItemInMainHand();
        this.offHand = equipment.getItemInOffHand();

        this.contents = new ItemStack[equipment.getArmorContents().length];
        System.arraycopy(equipment.getArmorContents(), 0, contents, 0, contents.length);
        CreeperLog.debug("Armor: " + Arrays.toString(contents));
        remove();
    }

    @Override
    public boolean replace(boolean shouldDrop)
    {
        ArmorStand s = getWorld().spawn(getLocation(), ArmorStand.class);
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

        EntityEquipment equipment = s.getEquipment();
        equipment.setArmorContents(contents);
        equipment.setItemInMainHand(this.mainHand);
        equipment.setItemInOffHand(this.offHand);

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
            stand.getEquipment().clear();
            CreeperLog.debug("Removing armor, chestplate = " + stand.getChestplate().getType());
            stand.remove();
        }
    }
}
