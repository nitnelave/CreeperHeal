package com.nitnelave.CreeperHeal;

public class BlockId {
	int id;
	byte data;
	boolean hasData;

	public BlockId(int type_id){
		id = type_id;
		data = 0;
		hasData = false;
	}
	
	public BlockId(int type_id, byte data_value) {
		id = type_id;
		data = data_value;
		hasData = (data_value != 0);
	}
	
	public BlockId(String str) throws NumberFormatException{
		str = str.trim();
		try{
			id = Integer.parseInt(str);
			data = 0;
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
		
		if(block.hasData){
			return block.data == data;
		}
		else {
			return true;
		}
	}
	
	@Override
	public int hashCode(){
		return 37*id;
	}
}
