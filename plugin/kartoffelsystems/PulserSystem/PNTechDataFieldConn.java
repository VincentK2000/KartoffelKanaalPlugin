package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

public class PNTechDataFieldConn extends PNTech{
	byte[] toIncrease;
	
	protected PNTechDataFieldConn(byte[] increase, boolean invisible, int ID, PulserNotifStandard notificationBase) {
		super(invisible, ID, notificationBase);
		this.toIncrease = increase;
	}
	
	protected PNTechDataFieldConn(byte[] increase, byte[] src){
		super(src);
	}

	@Override
	public byte getTechType() {return 3;}

	@Override
	public String getTypeName(){
		return "TechDataFieldConn";
	}

	protected void increaseValue(short... playerID){
		
	}
	
	protected void increaseValue(short playerID, short amount){
		
	}
	
	protected void setInvisibleToIncreaseConn(byte datafieldID, boolean value){
		if(this.toIncrease == null)return;
		datafieldID &= 0x7F;
		if(value){
			for(int i = 0; i < this.toIncrease.length; i++){
				if(this.toIncrease[i] == datafieldID){
					this.toIncrease[i] |= 0x80;
					return;
				}
			}
		}else{
			byte search = (byte) (datafieldID | 0x80);
			for(int i = 0; i < this.toIncrease.length; i++){
				if(this.toIncrease[i] == search){
					this.toIncrease[i] = datafieldID;
					return;
				}
			}
		}
	}
	
	@Override
	public int getEstimatedSize() {
		return PNTech.generalInfoLength() + 1 + toIncrease.length;
	}

	protected static PNTechDataFieldConn loadFromBytes(byte[] src){
		if(src == null || src.length < PNTech.generalInfoLength() || src[PNTech.generalInfoLength()] < 0 || src.length != (PNTech.generalInfoLength() + src[PNTech.generalInfoLength()]))return null;
		
		int s = PNTech.generalInfoLength();
		byte[] increase = new byte[src[s++]];
		
		for(int i = s; i < increase.length; i++){
			increase[i] = src[s++];
		}
		
		return new PNTechDataFieldConn(increase, src);
		
	}


	@Override
	protected byte[] saveTech() {
		if(toIncrease == null){
			toIncrease = new byte[0];
		}
		byte l;
		
		if(toIncrease.length > 127){
			l = 127;
		}else{
			l = (byte) toIncrease.length;
		}
		
		byte[] ans = new byte[PNTech.generalInfoLength() + 1 + l];
		
		ans[PNTech.generalInfoLength()] = l;
		System.arraycopy(toIncrease, 0, ans, PNTech.generalInfoLength() + 1, l);
		
		return ans;
	}

	public static PNTechCondition createFromParams(String[] params, int ID, PulserNotifStandard notificationBase) throws Exception {
		throw new Exception("Functie nog niet beschikbaar");
	}
	
	@Override
	public PNTech createCopy(int ID, PulserNotifStandard notificationBase) throws Exception{
		throw new Exception("Functie nog niet beschikbaar");
	}
}
