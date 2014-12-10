package KartoffelKanaalPlugin.plugin;

public class DataFieldInt extends DataFieldBase {
	int[] data = new int[32];
	int defaultVal = 0;
	
	public DataFieldInt(KartoffelFile f, String description, int defaultValue) throws Exception {
		super(f, description, (byte) 4, 
			new byte[]{
				(byte) ((defaultValue >>> 24) & 0xFF),
				(byte) ((defaultValue >>> 16) & 0xFF),
				(byte) ((defaultValue >>>  8) & 0xFF),
				(byte) ( defaultValue         & 0xFF)
			}
		);
		this.defaultVal = defaultValue;
		
	}

	@Override
	protected void _swapData(int dataLoc1, int dataLoc2) {
		if(dataLoc1 == dataLoc2 || dataLoc1 < 0 || dataLoc1 >= this.data.length || dataLoc2 < 0 || dataLoc2 >= this.data.length)return;
		int mem = this.data[dataLoc2];
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
			(byte) ((data[i] >>> 24) & 0xFF),
			(byte) ((data[i] >>> 16) & 0xFF),
			(byte) ((data[i] >>>  8) & 0xFF),
			(byte) ( data[i]         & 0xFF)
		};
	}

	@Override
	protected void _setValue(byte[] v, int entryIndex) {
		if(entryIndex < 0 || entryIndex >= data.length)return;
		if(v == null || v.length < 4)return;
		this.data[entryIndex] = (int)(
			(((int) v[0]) & 0xFF) << 24 | 
			(((int) v[1]) & 0xFF) << 16 | 
			(((int) v[2]) & 0xFF) <<  8 | 
			(((int) v[3]) & 0xFF)
		);
	}
	
	public void setValue(short kartoffelID, int value){
		if(kartoffelID < 0 || kartoffelID == 0x7FFF)return;
		int entryIndex = this.getEntryIndex(kartoffelID);
		if(entryIndex < 0 || entryIndex >= this.data.length)return;
		if(this.data[entryIndex] != value){
			this.data[entryIndex] = value;
			this.notifyChange();
		}
	}
	
	public int getValue(short kartoffelID){
		int index = this.getEntryIndex(kartoffelID);
		if(index < 0 || index >= data.length)return this.defaultVal;
		return this.data[index];
	}
	
	public void add(short kartoffelID, int amount){
		if(amount == 0)return;
		int entryIndex = this.getEntryIndex(kartoffelID);
		if(entryIndex < 0 || entryIndex >= data.length)return;
		this.data[entryIndex] = this.data[entryIndex] + amount;
		this.notifyChange();
	}
	
	public void _printDebug(){
		System.out.println("");
		System.out.println("DebugPrint DataFieldInt:");
		for(int i = 0; i < this.kartoffelIDs.length; i++){
			System.out.println("Slot " + ((i < 10)?("0" + i):i) + " = " + this.kartoffelIDs[i] + ": "+ this.data[i]);
		}
	}
	
}
