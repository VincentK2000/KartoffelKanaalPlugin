package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.entity.Player;

import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;
import KartoffelKanaalPlugin.plugin.Main;

public class PNTechTextProvRaw extends PNTechTextProv{
	protected boolean requiresCrashTest = true;
	protected String rawtext;
	
	protected PNTechTextProvRaw(String rawtext, boolean invisible, int ID, PulserNotifStandard notificationBase){
		super(invisible, ID, notificationBase);
		this.rawtext = rawtext;
	}
	
	protected PNTechTextProvRaw(String rawtext, byte[] src){
		super(src);
		this.rawtext = rawtext;
	}
	
	public byte getTechType(){return 1;}
	public byte getTextProvType(){return 1;}
	
	public int getEstimatedSize(){
		return PNTech.generalInfoLength() + rawtext.length();
	}
	
	protected void handleCommand(String[] args, int startindex){
		
	}
	
	protected String getMessage(){
		return this.rawtext;
	}
	
	protected byte[] saveTech(){
		byte[] text;
		if(this.rawtext == null){
			text = new byte[0];
		}else{
			try {
				text = this.rawtext.getBytes("UTF8");
			} catch (UnsupportedEncodingException e) {
				Logger.getLogger("Minecraft").warning("[KKP] De Encoding \"UTF8\" wordt niet herkent bij een PNTechTextProvRaw");
				byte[] ans = new byte[PNTech.generalInfoLength() + 1];
				this.saveGeneralInfo(ans);
				ans[PNTech.generalInfoLength()] = this.getTextProvType();
				return ans;
			}
		}
		
		byte[] ans = new byte[text.length + PNTech.generalInfoLength() + 1];
		
		this.saveGeneralInfo(ans);
		
		ans[PNTech.generalInfoLength()] = 1; //TextProvType
		
		System.arraycopy(text, 0, ans, PNTech.generalInfoLength() + 1, text.length);
		return ans;
	}
	protected static PNTechTextProvRaw loadFromBytes(byte[] src){
		//System.out.println("                PNTechTextProvRaw.loadFromBytes: PNTechTextProvRaw laden...");
		if(src == null || src.length < generalInfoLength())return null;
		//System.out.println("                PNTechTextProvRaw.loadFromBytes: Voldoende informatie om mee te beginnen");
		String rawtext;
		//System.out.println("                PNTechTextProvRaw.loadFromBytes: Eerste bytes van de rawtext:");
		int pos = generalInfoLength();
		/*for(int i = pos; i < 10 && i < (src.length - pos); i++){
			System.out.println("                    PNTechTextProvRaw.loadFromBytes: Byte " + i + ": " + src[pos + i] + " (" + ((char)src[pos + i]) + ")");
		}*/
		
		{
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			b.write(src, pos, src.length - pos);
			try {
				rawtext = b.toString("UTF8");
			} catch (UnsupportedEncodingException e) {
				Logger.getLogger("Minecraft").warning("[KKP] De Encoding \"UTF8\" werd niet herkend bij de static method loadFromBytes bij een PNTechTextProvRaw");
				return null;
			}
		}
		//System.out.println("                PNTechTextProvRaw.loadFromBytes: Begin van de RawText: " + rawtext.substring(0, (rawtext.length() <= 10?rawtext.length():10)));
		//System.out.println("                PNTechTextProvRaw.loadFromBytes: PNTechTextProvRaw geladen, rawtext.length = " + rawtext.length());
		return new PNTechTextProvRaw(rawtext, src);
	}

	@Override
	public boolean crashTestRequired() {
		return this.requiresCrashTest;
	}

	@Override
	public void doCrashTest(Player pl) throws Exception {
		if(pl == null)throw new Exception("Player is null!");
		Main.plugin.getServer().dispatchCommand(Main.plugin.getServer().getConsoleSender(), "tellraw " + pl.getName() + ' ' + this.getMessage());
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

	@Override
	public String[] getLocalTopLevelArgsPossibilities() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static PNTechTextProvRaw createFromParams(String[] params, int ID, PulserNotifStandard notificationBase) throws Exception {
		StringBuilder sb = new StringBuilder();
		if(params.length >= 1){
			for(int i = 0; i < params.length - 1; i++){
				sb.append(params[i]);
				sb.append(' ');
			}
			sb.append(params[params.length - 1]);
		}
		return new PNTechTextProvRaw(sb.toString(), false, ID, notificationBase);
	}
	
	@Override
	public PNTechTextProvRaw copyTech(int ID, PulserNotifStandard notificationBase) throws Exception{
		return new PNTechTextProvRaw(new String(this.rawtext), true, ID, notificationBase);
	}
}
