package KartoffelKanaalPlugin.plugin;

public class DataFieldShort extends DataFieldBase {
	short[] data = new short[32];
	short defaultVal = 0;
	
	public DataFieldShort(KartoffelFile f, String description, short defaultValue) throws Exception {
		super(f, description, (byte) 2, new byte[]{(byte) ((defaultValue >>> 8) & 0xFF), (byte) (defaultValue & 0xFF)});
		this.defaultVal = defaultValue;
	}

	@Override
	protected void _swapData(int dataLoc1, int dataLoc2) {
		if(dataLoc1 == dataLoc2 || dataLoc1 < 0 || dataLoc1 >= this.data.length || dataLoc2 < 0 || dataLoc2 >= this.data.length)return;
		short mem = this.data[dataLoc2];
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
		return new byte[]{
			(byte) ((data[i] >>> 8) & 0xFF),
			(byte) ( data[i]        & 0xFF)
		};
	}

	@Override
	protected void _setValue(byte[] v, int entryIndex) {
		if(entryIndex < 0 || entryIndex >= data.length)return;
		if(v == null || v.length < 2)return;
		this.data[entryIndex] = (short) ((((short) v[0]) & 0xFF) << 8 | (((short) v[1]) & 0xFF));
	}
	
	public void setValue(short kartoffelID, short value){
		if(kartoffelID < 0 || kartoffelID == 0x7FFF)return;
		int entryIndex = this.getEntryIndex(kartoffelID);
		if(entryIndex < 0 || entryIndex >= this.data.length)return;
		if(this.data[entryIndex] != value){
			this.data[entryIndex] = value;
			this.notifyChange();
		}
	}
	
	public short getValue(short kartoffelID){
		int index = this.getEntryIndex(kartoffelID);
		if(index < 0 || index >= data.length)return this.defaultVal;
		return this.data[index];
	}
	
	public void add(short kartoffelID, short amount){
		if(amount == 0)return;
		int entryIndex = this.getEntryIndex(kartoffelID);
		if(entryIndex < 0 || entryIndex >= data.length)return;
		this.data[entryIndex] = (short) (this.data[entryIndex] + amount);
		this.notifyChange();
	}
}
