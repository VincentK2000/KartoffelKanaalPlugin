package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.UUID;

public class PNTechSpecEditAccess extends PNTech{
	protected UUID[] ids;
	protected byte[] accessLevels;
	
	
	protected PNTechSpecEditAccess(UUID[] ids, byte[] accessLevels, boolean invisible, int ID, PulserNotifStandard base) throws IllegalArgumentException{
		super(invisible, ID, base);
		if((ids == null?0:ids.length) != (ids == null?0:accessLevels.length))
			throw new IllegalArgumentException("UUID length en accessLevel length komen niet overeen");
		this.ids = ids;
		this.accessLevels = accessLevels;
	}
	
	protected PNTechSpecEditAccess(byte[] src, UUID[] ids, byte[] accessLevels){
		super(src);
		if((ids == null?0:ids.length) != (ids == null?0:accessLevels.length))
			throw new IllegalArgumentException("UUID length en accessLevel length komen niet overeen");
		this.ids = ids;
		this.accessLevels = accessLevels;
	}
	
	@Override
	public byte getTechType() {return 5;}

	@Override
	public String getTypeName(){
		return "TechSpecEditAccess";
	}

	protected byte getSpecialEditAccessLevel(UUID id){
		if(this.ids == null || this.accessLevels == null)return 0;
		int l = this.ids.length;
		if(this.accessLevels.length < l)l = this.accessLevels.length;
		
		for(int i = 0; i < l; i++){
			if(this.ids[i].equals(id)){
				if((this.accessLevels[i] & 0x80) == 0x00)return this.accessLevels[i];
			}
		}
		
		return 0;
	}
	
	public boolean hasSpecAccess(UUID id, byte level){
		if(id == null || level < 0)return false;
		if(this.ids == null || this.ids.length == 0 || this.accessLevels == null || this.accessLevels.length == 0)return false;
		int max = this.ids.length;
		if(this.accessLevels.length < max) max = this.accessLevels.length;
		for(int i = 0; i < max; i++){
			if(this.ids[i] != null && this.ids[i].equals(id)){
				if(this.accessLevels[i] >= level)return true;//Er kunnen per ongeluk meerdere accessLevels per persoon worden geregistreerd, ook al zou dat wel onzin zijn 
			}
		}
		return false;
	}

	@Override
	public int getEstimatedSize() {
		return (this.ids == null)?generalInfoLength():((this.ids.length * 17) + generalInfoLength());
	}

	protected static PNTechSpecEditAccess loadFromBytes(byte[] src){
		if(src == null || src.length < PNTech.generalInfoLength())return null;
		int specEditAccessLength = (src.length - PNTech.generalInfoLength()) / 17;
		if(specEditAccessLength > 300)specEditAccessLength = 300;
		
		UUID[] ids = new UUID[specEditAccessLength];
		byte[] accessLevels = new byte[specEditAccessLength];
		
		int pos = PNTech.generalInfoLength();
		
		for(int i = 0; i < specEditAccessLength; i++){
			accessLevels[i] = src[pos++];

			ids[i] = new UUID(
				//Most Significant Bits
				src[pos++] << 56 | src[pos++] << 48 | src[pos++] << 40 | src[pos++] << 32 |
				src[pos++] << 24 | src[pos++] << 16 | src[pos++] <<  8 | src[pos++],
					
				//Least Significat Bits
				src[pos++] << 56 | src[pos++] << 48 | src[pos++] << 40 | src[pos++] << 32 |
				src[pos++] << 24 | src[pos++] << 16 | src[pos++] <<  8 | src[pos++]
			);
		}
		
		return new PNTechSpecEditAccess(src, ids, accessLevels);
	}
	
	@Override
	protected byte[] saveTech() {
		int l = (this.ids == null?0:this.ids.length);
		if(this.accessLevels != null && this.accessLevels.length < l)l = this.accessLevels.length;
		byte[] ans = new byte[generalInfoLength() + (l * 17)];
		
		int pos = generalInfoLength();
		for(int i = 0; i < l; i++){
			ans[pos++] = this.accessLevels[i];
			
			long a = this.ids[i].getMostSignificantBits();
			ans[pos++] = (byte) ((a >>> 56) & 0xFF);
			ans[pos++] = (byte) ((a >>> 48) & 0xFF);
			ans[pos++] = (byte) ((a >>> 40) & 0xFF);
			ans[pos++] = (byte) ((a >>> 32) & 0xFF);
			ans[pos++] = (byte) ((a >>> 24) & 0xFF);
			ans[pos++] = (byte) ((a >>> 16) & 0xFF);
			ans[pos++] = (byte) ((a >>>  8) & 0xFF);
			ans[pos++] = (byte) ((a       ) & 0xFF);
			
			a = this.ids[i].getLeastSignificantBits();
			ans[pos++] = (byte) ((a >>> 56) & 0xFF);
			ans[pos++] = (byte) ((a >>> 48) & 0xFF);
			ans[pos++] = (byte) ((a >>> 40) & 0xFF);
			ans[pos++] = (byte) ((a >>> 32) & 0xFF);
			ans[pos++] = (byte) ((a >>> 24) & 0xFF);
			ans[pos++] = (byte) ((a >>> 16) & 0xFF);
			ans[pos++] = (byte) ((a >>>  8) & 0xFF);
			ans[pos++] = (byte) ((a       ) & 0xFF);
		}
		
		saveGeneralInfo(ans);
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
