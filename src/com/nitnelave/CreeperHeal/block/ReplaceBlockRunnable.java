package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.BlockState;

public class ReplaceBlockRunnable implements Runnable
{
	BlockState block;
	
	public ReplaceBlockRunnable(BlockState b)
	{
		block = b;
	}

	@Override
    public void run()
    {
	    block.update(true);
    }

}
