package com.nitnelave.CreeperHeal.utils;

import java.util.*;

import com.nitnelave.CreeperHeal.block.CreeperBlock;

public class CreeperComparator implements Comparator<CreeperBlock>{		//used to sort blocks from bottom to top
	
	public int compare(CreeperBlock b1, CreeperBlock b2) {
		
		boolean c1 = CreeperBlock.isDependent(b1.getTypeId());
		boolean c2 = CreeperBlock.isDependent(b2.getTypeId());
		if(c1 && !c2)
			return 1;
		else if(c2 && !c1)
			return -1;
		
		int pos1 = b1.getLocation().getBlockY();		//altitude of block one
		int pos2 = b2.getLocation().getBlockY();		//altitude of block two
		
		if(pos1 > pos2)
			return 1;
		else if(pos1<pos2)
			return -1;
		else
			return 0;
		
		
	}

}
