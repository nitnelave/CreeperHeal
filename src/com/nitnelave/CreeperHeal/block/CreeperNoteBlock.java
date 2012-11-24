package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.NoteBlock;

public class CreeperNoteBlock extends CreeperBlock{

	public CreeperNoteBlock(NoteBlock blockState) {
		super(blockState);
	}
	
	@Override
	public void update(boolean force) {
		super.update(force);
		((NoteBlock)getBlock().getState()).setRawNote(((NoteBlock)getState()).getRawNote());
	}

}
