package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import org.bukkit.entity.Player;

public abstract class PNTechTextProv extends PNTech{
	protected abstract String getMessage();
	
	@Override
	public byte getTechType(){return 1;}
	
	public abstract byte getTextProvType();

	protected PNTechTextProv(boolean invisible, int ID, PulserNotifStandard notificationBase){
		super(invisible, ID, notificationBase);
	}
	
	protected PNTechTextProv(byte[] src){
		super(src);
	}
	
	protected static PNTechTextProv loadFromBytes(byte[] src){
		//System.out.println("            PNTechTextProv.loadFromBytes: PNTechTextProv laden...");
		if(src == null || src.length < PNTech.generalInfoLength() + 1)return null;
		//System.out.println("            PNTechTextProv.loadFromBytes: Voldoende informatie om mee te beginnen");
		PNTechTextProv a = null;
		byte t = (byte) (src[PNTech.generalInfoLength()] & 0x7F);
		//System.out.println("            PNTechTextProv.loadFromBytes: src.length = " + src.length);
		//System.out.println("            PNTechTextProv.loadFromBytes: TextProvType is " + t);
		if(t == 1){
			a = PNTechTextProvRaw.loadFromBytes(src);
		}else if(t == 2){
			a = PNTechTextProvFormatted.loadFromBytes(src);
		}
		//System.out.println("            PNTechTextProv.loadFromBytes: PNTechTextProv geladen, a = " + (a==null?"null":a));
		return a;
	}
	
	public static String createJSONSafeString(String src){
		if(src == null || src.length() == 0)return "";
		StringBuilder s = new StringBuilder(src.length());
		char[] in = src.toCharArray();
		for(int i = 0; i < in.length; i++){
			if(in[i] == '\"' || in[i] == '\\' || in[i] == '/'){
				s.append('\\');
				s.append(in[i]);
				
			}else if(in[i] == '\b' || in[i] == '\f' || in[i] == '\n' || in[i] == '\r' || in[i] == '\t'){
				s.append(" ");
				
			}else{
				s.append(in[i]);
			}
		}
		
		//System.out.println("JSONSafeString van \"" + src + "\" = \"" + s.toString() + "\"");
		return s.toString();
	}

	protected static int generalInfoLength(){return PNTech.generalInfoLength() + 1;}
	
	protected boolean saveGeneralInfo(byte[] ans){
		if(ans == null || ans.length < PNTech.generalInfoLength() + generalInfoLength())return false;
		super.saveGeneralInfo(ans);
		ans[PNTech.generalInfoLength()] = this.getTextProvType();
		return true;
	}
	
	public abstract boolean crashTestRequired();
	public abstract void doCrashTest(Player pl) throws Exception;
}
