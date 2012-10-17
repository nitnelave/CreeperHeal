package com.nitnelave.CreeperHeal;

import java.util.Comparator;

public class BlockIdComparator implements Comparator<BlockId> {

	@Override
	public int compare(BlockId b1, BlockId b2) {
		if(b1.id != b2.id)
		{
			return b1.id > b2.id ? 1 : -1;
		}
		else
			if(b1.hasData && !b2.hasData)
				return 1;
			else if(b2.hasData && !b1.hasData)
				return -1;
			else if(b2.hasData && b1.hasData)
			{
				return b1.data > b2.data ? 1 : -1;
			}
			else
				return 0;
	}

}
