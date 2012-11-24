package com.nitnelave.CreeperHeal.block;

import org.bukkit.block.CreatureSpawner;

public class CreeperMonsterSpawner extends CreeperBlock{

	public CreeperMonsterSpawner(CreatureSpawner blockState) {
		super(blockState);
	}

	@Override
	public void update(boolean force) {
		super.update(force);
		((CreatureSpawner)getBlock().getState()).setCreatureTypeByName(((CreatureSpawner)getState()).getCreatureTypeName());
	}
}
