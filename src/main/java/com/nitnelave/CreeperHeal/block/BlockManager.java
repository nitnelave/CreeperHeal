package com.nitnelave.CreeperHeal.block;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.nitnelave.CreeperHeal.CreeperHeal;
import com.nitnelave.CreeperHeal.config.CreeperConfig;
import com.nitnelave.CreeperHeal.config.WorldConfig;
import com.nitnelave.CreeperHeal.utils.CreeperUtils;

public class BlockManager {
	/**
	 * Constants
	 */


	private static Map<Location, Replaceable> toReplace = Collections.synchronizedMap(new HashMap<Location,Replaceable>());		//blocks to be replaced immediately after an explosion


	private static CreeperHeal plugin;

	public BlockManager(CreeperHeal plugin) {
		setBlockManagerPlugin(plugin);
		new PaintingsManager();
		BurntBlockManager.setBurntBlockManagerPlugin(plugin);
	}



	private void setBlockManagerPlugin(CreeperHeal plugin) {
		BlockManager.plugin = plugin;
	}





	protected static void replaceProtected() {         //replace the blocks that should be immediately replaced after an explosion
		Iterator<Replaceable> iter = toReplace.values().iterator();
		while(iter.hasNext())
			iter.next().replace(false);


		toReplace.clear();

	}




	protected static void replace_blocks(Replaceable block) {        //if there's just one block, no need to go over all this
		block.replace(false);
	}




	protected static void replace_one_block(List<Replaceable> list) {        //replace one block (block per block)
		replace_blocks(list.get(0));        //blocks are sorted, so get the first
		check_player_one_block(list.get(0).getBlock().getLocation());
		list.remove(0);
	}



	protected static void check_player_one_block(Location loc) {      //get the living entities around to save thoses who are suffocating
		if(CreeperConfig.teleportOnSuffocate) {
			Entity[] play_list = loc.getBlock().getChunk().getEntities();
			if(play_list.length!=0) {
				for(Entity en : play_list) {
					if(en instanceof LivingEntity) {
						if(loc.distance(en.getLocation()) < 2)
							CreeperUtils.check_player_suffocate((LivingEntity)en);
					}
				}
			}
		}
	}



	protected static void replace_blocks(List<Replaceable> list) {    //replace all the blocks in the given list
		if(list == null)
			return;
		while(!list.isEmpty()){            //replace all non-physics non-dependent blocks
			Iterator<Replaceable> iter = list.iterator();
			while (iter.hasNext()){
				Replaceable block = iter.next();
				if(!CreeperBlock.hasPhysics(block.getTypeId())){
					block.replace(false);
					iter.remove();
				}
			}
			iter = list.iterator();
			while (iter.hasNext()){        //then all physics
				Replaceable block = iter.next();
				if(CreeperBlock.hasPhysics(block.getTypeId())){
					block.replace(false);
					iter.remove();
				}
			}

		}
		if(CreeperConfig.teleportOnSuffocate) {            //checks for players suffocating anywhere
			Player[] player_list = plugin.getServer().getOnlinePlayers();
			for(Player player : player_list) {
				CreeperUtils.check_player_suffocate(player);
			}
		}

	}






	public void checkReplaceTime()
	{
		for(WorldConfig w : CreeperConfig.world_config.values()) {
			long time = plugin.getServer().getWorld(w.name).getTime();
			if(w.repairTime != -1 && ((Math.abs(w.repairTime - time) < 600) || (Math.abs(Math.abs(w.repairTime - time) - 24000)) < 600)){
				ExplodedBlockManager.forceReplace(0, w);        
				BurntBlockManager.forceReplaceBurnt(0, w);
				PaintingsManager.replace_paintings();
			}
		}
	}



	public static Map<Location, Replaceable> getToReplace() {
		return toReplace;
	}

}
