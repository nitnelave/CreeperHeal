package com.nitnelave.CreeperHeal;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;

public class ReorientRails implements Runnable
{
	private BlockState blockState;

	public ReorientRails(BlockState blockState)
	{
		this.blockState = blockState;
	}

	@Override
	public void run()
	{
		BlockFace[] faces = {BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH};

		byte data = blockState.getRawData();
		List<Byte> dataList = new ArrayList<Byte>(12);
		Block block = blockState.getBlock();
		for(int i = -1; i<2; i++)
		{
			for(int j = 0; j<4; j++)
			{
				Block tmp_block = block.getRelative(faces[j]);
				if(i == 1)
					tmp_block = tmp_block.getRelative(BlockFace.UP);
				else if(i== -1)
					tmp_block = tmp_block.getRelative(BlockFace.DOWN);
				dataList.add(tmp_block.getData());          //save every orientation
			}
		}
		blockState.update(true);    //add the new rail 
		block.setData(data);
		for(int i = -1; i<2; i++)
		{
			for(int j = 0; j<4; j++)
			{
				Block tmp_block = block.getRelative(faces[j]);
				if(i == 1)
					tmp_block = tmp_block.getRelative(BlockFace.UP);
				else if(i== -1)
					tmp_block = tmp_block.getRelative(BlockFace.DOWN);
				tmp_block.setData(dataList.get(4*i + 4 + j));       //reset every orientation
			}
		}


	}


}
