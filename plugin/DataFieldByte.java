package KartoffelKanaalPlugin.plugin;

public class DataFieldByte extends DataFieldBase {
	byte[] data = new byte[32];
	byte defaultVal = 0;
	
	public DataFieldByte(KartoffelFile f, String description, byte defaultValue) throws Exception {
		super(f, description, (byte) 1, new byte[]{defaultValue});
		this.defaultVal = defaultValue;
		
	}

	@Override
	protected void _swapData(int dataLoc1, int dataLoc2) {
		if(dataLoc1 == dataLoc2 || dataLoc1 < 0 || dataLoc1 >= this.data.length || dataLoc2 < 0 || dataLoc2 >= this.data.length)return;
		byte mem = this.data[dataLoc2];
		this.data[dataLoc2] = this.data[dataLoc1];
		this.data[dataLoc1] = mem;
	}

	@Override
	protected boolean isNumberStructure() {
		return true;
	}

	@Override
	public byte[] getEntryData(int i) {
		if(i < 0 || i >= data.length)return this.defaultEntry.clone();
		return new byte[]{data[i]};
	}

	@Override
	protected void _setValue(byte[] v, int entryIndex) {
		if(entryIndex < 0 || entryIndex >= data.length)return;
		if(v == null || v.length < 1)return;
		this.data[entryIndex] = v[0];
	}
	
	public void setValue(short kartoffelID, byte value){
		if(kartoffelID < 0 || kartoffelID == 0x7FFF)return;
		int entryIndex = this.getEntryIndex(kartoffelID);
		if(entryIndex < 0 || entryIndex >= this.data.length)return;
		if(this.data[entryIndex] != value){
			this.data[entryIndex] = value;
			this.notifyChange();
		}
	}
	
	public byte getValue(short kartoffelID){
		int index = this.getEntryIndex(kartoffelID);
		if(index < 0 || index >= data.length)return this.defaultVal;
		return this.data[index];
	}
	
	public void add(short kartoffelID, byte amount){
		if(amount == 0)return;
		int entryIndex = this.getEntryIndex(kartoffelID);
		if(entryIndex < 0 || entryIndex >= data.length)return;
		this.data[entryIndex] = (byte) (this.data[entryIndex] + amount);
		this.notifyChange();
		
	}
	
}
