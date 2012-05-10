package com.nitnelave.CreeperHeal;

import java.util.ArrayList;
import java.util.Arrays;


public class WorldConfig {

	public boolean replaceTNT, enderman, replaceAbove, blockLava, blockTNT, blockIgnite, blockBlackList, 
	blockSpawnEggs, blockPvP, warnLava, warnTNT, warnIgnite, warnBlackList, warnSpawnEggs, warnPvP, preventFireSpread, preventFireLava,
	creepers, tnt, fire, ghast, magical, dragons;
	public String restrictBlocks, name;
	public int repairTime, replaceLimit;
	public ArrayList<BlockId> blockList = new ArrayList<BlockId>(), placeList = new ArrayList<BlockId>();




	public WorldConfig(String name) {
		creepers = tnt = ghast = fire = true;
		replaceTNT = magical = dragons = replaceAbove = enderman = blockLava = blockTNT = blockIgnite = blockBlackList = blockSpawnEggs = blockPvP= 
				warnLava = warnTNT = warnIgnite = warnBlackList = warnSpawnEggs = warnPvP = preventFireSpread = preventFireLava = false;
		restrictBlocks = "false";
		replaceLimit = 60;
		repairTime = -1;
		blockList = new ArrayList<BlockId>();        //sample whitelist
		int[] tmp_list = { 1,2,3,9,11,12,13,14,15,16,17,18,21,24,31,32,37,38,39,40,48,49,56,73,79,81,82,86,87,88,89 };
		for(int k : tmp_list)
			blockList.add(new BlockId(k));
		this.name = name;
	}

	@SuppressWarnings("unchecked")
	public WorldConfig(String name, Object... l) {
		this.name = name;
		creepers =  (Boolean) l[0];
		tnt = (Boolean) l[1];
		ghast = (Boolean) l[2];
		dragons = (Boolean) l[3];
		magical = (Boolean) l[4];
		fire = (Boolean) l[5];
		enderman = (Boolean) l[6];
		replaceTNT = (Boolean) l[7];
		replaceAbove = (Boolean) l[8];
		replaceLimit = (Integer) l[9]; 
		restrictBlocks = (String) l[10];
		blockList = (ArrayList<BlockId>) l[11];
		repairTime = (Integer) l[12];
		blockLava = (Boolean) l[13]; 
		blockTNT = (Boolean) l[14];
		blockIgnite = (Boolean) l[15];
		blockBlackList = (Boolean) l[16];
		blockSpawnEggs = (Boolean) l[17]; 
		blockPvP = (Boolean) l[18];
		warnLava = (Boolean) l[19];
		warnTNT = (Boolean) l[20];
		warnIgnite = (Boolean) l[21];
		warnBlackList = (Boolean) l[22]; 
		warnSpawnEggs = (Boolean) l[23];
		warnPvP = (Boolean) l[24];
		preventFireSpread = (Boolean) l[25];
		preventFireLava = (Boolean) l[26];
		placeList = (ArrayList<BlockId>) l[27];
	}

	@SuppressWarnings("unchecked")
    public ArrayList<Object> getConfig() {
		String blocklist = "";
		for(BlockId block : blockList)
			blocklist += block.toString() + ", ";
		
		blocklist = blocklist.substring(0, blocklist.length() - 2);
		
		String placelist = "";
		for(BlockId block : placeList)
			placelist += block.toString() + ", ";
		
		placelist = placelist.substring(0, placelist.length() - 2);

		return new ArrayList<Object>(Arrays.asList(creepers, tnt, ghast, dragons, magical, fire, enderman, replaceTNT, replaceAbove,
				replaceLimit, restrictBlocks, blocklist, repairTime, blockLava, blockTNT, blockIgnite, blockBlackList, blockSpawnEggs, blockPvP,
				warnLava, warnTNT, warnIgnite, warnBlackList, warnSpawnEggs, warnPvP, preventFireSpread, preventFireLava, placelist));
	}



	public String getName() {
		return name;
	}

	public String formatList(ArrayList<BlockId> list)
	{
		String blocklist = "";
		for(BlockId block : list)
			blocklist += block.toString() + ", ";

		return blocklist.substring(0, blocklist.length() - 2);

	}

	public boolean isRepairTimed()
	{
		return repairTime > -1;
	}

}