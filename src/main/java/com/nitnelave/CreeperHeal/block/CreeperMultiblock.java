package com.nitnelave.CreeperHeal.block;

import com.nitnelave.CreeperHeal.config.CfgVal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.utils.ShortLocation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.BlockState;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract CreeperBlock representing an item that may span multiple blocks.
 *
 * @author Jikoo
 */
public abstract class CreeperMultiblock extends CreeperBlock
{

    protected class BlockStateAndData {
      public final BlockState state;
      public final BlockData data;
      public BlockStateAndData(BlockState state, BlockData data) {
        this.state = state;
        this.data = data.clone();
      }
    }

    private final Set<BlockStateAndData> dependents;


    CreeperMultiblock(BlockState blockState)
    {
        super(blockState);
        this.dependents = new HashSet<BlockStateAndData>();
    }

    public void addDependent(BlockState state) {
      dependents.add(new BlockStateAndData(state, state.getBlockData()));
    }

    protected Set<BlockStateAndData> getDependents() {
      return dependents;
    }

    public void addDependent(BlockState state, BlockData data) {
      dependents.add(new BlockStateAndData(state, data));
    }

    @Override
    public void update()
    {
        super.update();
        for (BlockStateAndData dependent : dependents)
            dependent.state.getBlock().setBlockData(dependent.data, false);
        for (BlockStateAndData dependent : dependents)
            dependent.state.getBlock().setBlockData(dependent.data, true);
    }

    @Override
    protected boolean checkForDrop()
    {
        if (checkForDependentDrop(getBlock()))
            return true;

        for (BlockStateAndData dependent : dependents)
            if (checkForDependentDrop(dependent.state.getBlock()))
                return true;

        return false;

    }

    private boolean checkForDependentDrop(Block block)
    {
        Material type = block.getType();

        if (!CreeperConfig.getBool(CfgVal.OVERWRITE_BLOCKS) && !isEmpty(type))
        {
            if (CreeperConfig.getBool(CfgVal.DROP_DESTROYED_BLOCKS))
                drop(true);
            return true;
        } else if (CreeperConfig.getBool(CfgVal.OVERWRITE_BLOCKS) && !isEmpty(type)
                && CreeperConfig.getBool(CfgVal.DROP_DESTROYED_BLOCKS))
        {
            CreeperBlock b = CreeperBlock.newBlock(block.getState());
            if (b == null)
                throw new IllegalArgumentException("Null block for: " + block.getState().getType().toString());
            b.drop(true);
            b.remove();
        }
        return false;
    }

    @Override
    public void remove()
    {
        this.blockState.getBlock().setType(Material.AIR, false);
        for (BlockStateAndData dependent : dependents)
            dependent.state.getBlock().setType(Material.AIR, false);
    }

    @Override
    void record(Collection<ShortLocation> checked)
    {
        super.record(checked);
        for (BlockStateAndData dependent : dependents)
            checked.add(new ShortLocation(dependent.state.getLocation()));
    }
}
