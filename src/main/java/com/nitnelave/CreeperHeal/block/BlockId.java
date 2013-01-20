package com.nitnelave.CreeperHeal.block;

public class BlockId {
	int id;
	byte data;
	boolean hasData;

	public BlockId(int id){
		this.id = id;
		data = -1;
		hasData = false;
	}

	public BlockId(int id, byte data) {
		this.id = id;
		this.data = data;
		hasData = (data != -1);
	}

	public BlockId(int id, byte data, boolean hasData) {
		this.id = id;
		this.data = data;
		this.hasData = hasData;
	}

	public BlockId(String str) throws NumberFormatException{
		str = str.trim();
		try{
			id = Integer.parseInt(str);
			data = -1;
			hasData = false;
		}
		catch(NumberFormatException e){
			String[] split = str.split(":");
			if(split.length == 2){
				id = Integer.parseInt(split[0]);
				data = Byte.parseByte(split[1]);
				hasData = true;
			}
			else
				throw new NumberFormatException();
		}
	}

	public int getId() {
		return id;
	}

	public byte getData() {
		return data;
	}

	public boolean hasData() {
		return hasData;
	}

	@Override
	public String toString(){
		String str = String.valueOf(id);
		if(hasData)
			str += ":" + String.valueOf(data);
		return str;
	}

	@Override
	public boolean equals(Object obj){
		if(obj == this)
			return true;

		if(!(obj instanceof BlockId))
			return false;

		BlockId block = (BlockId) obj;
		if(block.id != id)
			return false;
		//same id
		if(!(block.hasData && hasData))
			return true;
		//both have data
		return block.hasData == hasData;
	}

	@Override
	public int hashCode(){
		return id;
	}
}
