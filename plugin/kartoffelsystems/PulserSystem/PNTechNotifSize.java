package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.ArrayList;

import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;

public class PNTechNotifSize extends PNTech{
	@Override
	public byte getTechType() {return 4;}
	
	byte limitOptions;
	//1 bit: Don't limite Operators
    //1 bit: Expand when Operators expand
	//6 bits: leeg
	
	
	//hooste bit = niet actief
	int totalMaxSize = 50000;
	int textProvMaxSize = 50000;
	int conditionsMaxSize = 50000;
	
	protected PNTechNotifSize(byte limitOptions, int totalMaxSize, int textProvMaxSize, int conditionsMaxSize, boolean invisible, int ID, PulserNotifStandard notificationBase){
		super(invisible, ID, notificationBase);
		this.limitOptions = limitOptions;
		this.totalMaxSize = totalMaxSize;
		this.textProvMaxSize = textProvMaxSize;
		this.conditionsMaxSize = conditionsMaxSize;
	}
	
	protected PNTechNotifSize(byte[] src, byte limitOptions, int totalMaxSize, int textProvMaxSize, int conditionsMaxSize){
		super(src);
		this.limitOptions = limitOptions;
		this.totalMaxSize = totalMaxSize;
		this.textProvMaxSize = textProvMaxSize;
		this.conditionsMaxSize = conditionsMaxSize;
	}
	
	protected static PNTechNotifSize loadFromBytes(byte[] src){
		if(src == null || src.length != PNTech.generalInfoLength() + 13)return null;
		
		int s = PNTech.generalInfoLength();
		byte limitOptions = src[s++];
		
		int totalMaxSize = src[s++] << 24 | src[s++] << 16 | src[s++] << 8 | src[s++];
		
		int textProvMaxSize = src[s++] << 24 | src[s++] << 16 | src[s++] << 8 | src[s++];
		
		int conditionsMaxSize = src[s++] << 24 | src[s++] << 16 | src[s++] << 8 | src[s++];
		
		return new PNTechNotifSize(src, limitOptions, totalMaxSize, textProvMaxSize, conditionsMaxSize);
	}

	@Override
	protected byte[] saveTech() {
		byte[] ans = new byte[PNTech.generalInfoLength() + 13];
		int s = PNTech.generalInfoLength();
		ans[s++] = this.limitOptions;
		
		ans[s++] = (byte) ((this.totalMaxSize >>> 24) & 0xFF);
		ans[s++] = (byte) ((this.totalMaxSize >>> 16) & 0xFF);
		ans[s++] = (byte) ((this.totalMaxSize >>>  8) & 0xFF);
		ans[s++] = (byte) ( this.totalMaxSize         & 0xFF);
		
		ans[s++] = (byte) ((this.textProvMaxSize >>> 24) & 0xFF);
		ans[s++] = (byte) ((this.textProvMaxSize >>> 16) & 0xFF);
		ans[s++] = (byte) ((this.textProvMaxSize >>>  8) & 0xFF);
		ans[s++] = (byte) ( this.textProvMaxSize         & 0xFF);
		
		ans[s++] = (byte) ((this.conditionsMaxSize >>> 24) & 0xFF);
		ans[s++] = (byte) ((this.conditionsMaxSize >>> 16) & 0xFF);
		ans[s++] = (byte) ((this.conditionsMaxSize >>>  8) & 0xFF);
		ans[s  ] = (byte) ( this.conditionsMaxSize         & 0xFF);
		
		this.saveGeneralInfo(ans);
		return ans;
	}
	
	//SizeOrigin:
	//0: Generic
	//1: TechTextProv
	//2: TechCondition
	protected boolean allowNewSize(byte sizeOrigin, int oldSize, int newSize, boolean operator){
		if(operator){
			if((this.limitOptions & 0x40) == 0x40){
				int expantion = newSize - oldSize;
				
				this.totalMaxSize += expantion;
				
				if(sizeOrigin == 1){
					this.textProvMaxSize += expantion;
				}else if(sizeOrigin == 2){
					this.conditionsMaxSize += expantion;
				}
				
				return true;
			}else if((this.limitOptions & 0x80) == 0x80){
				return true;
			}
		}
		
		if((this.totalMaxSize & 0x80000000) == 0){
			if(newSize > this.totalMaxSize)return false;
		}
		if(sizeOrigin == 1){
			return ((this.textProvMaxSize & 0x80000000) == 0)?newSize <= this.textProvMaxSize:true;
		}else if(sizeOrigin == 2){
			return ((this.conditionsMaxSize & 0x80000000) == 0)?newSize <= this.conditionsMaxSize:true;
		}
		return true;
	}

	@Override
	public int getEstimatedSize() {
		return PNTech.generalInfoLength() + 13;
	}

	@Override
	public IObjectCommandHandable getSubObjectCH(String path) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> autoCompleteSubObjectCH(String s) throws Exception {
		return super.autoCompleteSubObjectCH(s);
	}
	
	public static PNTechCondition createFromParams(String[] params, int ID, PulserNotifStandard notificationBase) throws Exception {
		throw new Exception("Functie nog niet beschikbaar");
	}
	
	@Override
	public PNTech copyTech(int ID, PulserNotifStandard notificationBase) throws Exception{
		throw new Exception("Functie nog niet beschikbaar");
	}
	
	@Override
	public String getTypeName(){
		return "TechNotifSize";
	}
}
