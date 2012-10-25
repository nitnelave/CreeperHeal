package com.nitnelave.CreeperHeal;

import java.util.Comparator;

public class BlockIdComparator implements Comparator<BlockId> {

	@Override
	public int compare(BlockId b1, BlockId b2) {
		if(b1.id != b2.id || (b1.hasData ^ b2.hasData)) 		//if only one of them has data, it is the block taken from the world, and not the one defined in the config, and the data should be ignored
		{
			return b1.id > b2.id ? 1 : -1;
		}
		else if(!b1.hasData || b1.data == b2.data)
			return 0;
		else
			return b1.data > b2.data ? 1 : -1;
	}

}
