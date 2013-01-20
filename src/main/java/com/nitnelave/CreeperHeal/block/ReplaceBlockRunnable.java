package com.nitnelave.CreeperHeal.block;

public class ReplaceBlockRunnable implements Runnable
{
	CreeperBlock block;
	
	public ReplaceBlockRunnable(CreeperBlock b)
	{
		block = b;
	}

	@Override
    public void run()
    {
	    block.update(true);
    }

}
