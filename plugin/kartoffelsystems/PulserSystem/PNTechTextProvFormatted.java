package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.command.CommandSender;

import KartoffelKanaalPlugin.plugin.AdvancedChat;
import KartoffelKanaalPlugin.plugin.AttribSystem;
import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;
import KartoffelKanaalPlugin.plugin.StoreTechnics;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;

public abstract class PNTechTextProvFormatted extends PNTechTextProv {
	@Override
	public IObjectCommandHandable getSubObjectCH(String path) throws Exception {
		return null;
	}

	@Override
	public ArrayList<String> autoCompleteSubObjectCH(String s) throws Exception {
		return super.autoCompleteSubObjectCH(s);
	}

	@Override
	public boolean handleObjectCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args) throws Exception {
		if(super.handleObjectCommand(executor, a, attribSys, args))return true;
		if(args.length < 1){
			a.sendMessage("§ePNTechTextProvFormatted-deel van het commando: §c<parameter> <...>");
			return true;
		}
		if(executor.getSpelerOptions().getOpStatus() < 2){
			a.sendMessage("§4Je hebt geen toegang tot dit commando");
			return true;
		}
		args[0] = args[0].toLowerCase();
		if(args[0].equals("parameter")){
			if(this.notificationBase == null){
				throw new Exception("ERROR: De notificationBase is null");
			}
			if(args.length == 2){
				this.notificationBase.checkPermission(this, a, executor, this.getParameterViewAccessLevel());
				int index;
				if(args[1].startsWith("#")){
					try{
						index = Integer.parseInt(args[1].substring(1));
					}catch(NumberFormatException e){
						a.sendMessage("§4Oncorrecte parameterIndex");
						return true;
					}
				}else{
					index = this.getNamedParameterIndex(args[1]);
				}
				if(index < 0 || index >= this.parameters.length){
					a.sendMessage("§4Onbekende parameterNaam");
					return true;
				}
				a.sendMessage("§eDe parameter (#" + index + ") \"" + args[1] + "\" is " + ((this.parameters[index] == null || this.parameters[index].length() == 0)?("leeg"):("\"" + this.parameters[index] + "\"")));
			}else if(args.length >= 3){
				int index;
				if(args[1].startsWith("#")){
					try{
						index = Integer.parseInt(args[1].substring(1));
					}catch(NumberFormatException e){
						a.sendMessage("§4Oncorrecte parameterIndex");
						return true;
					}
				}else{
					index = this.getNamedParameterIndex(args[1]);
				}
				if(index < 0 || index >= this.parameters.length){
					a.sendMessage("§4Onbekende parameterNaam");
					return true;
				}
				this.notificationBase.checkPermission(this, a, executor, this.getParameterChangeAccessLevel(index));
				if(args.length == 3 && args[2].equals("leeg")){
					if(this.setParameter(index, "")){
						a.sendMessage("§eDe parameter \"" + args[1] + "\" is veranderd naar een lege status");
					}else{
						a.sendMessage("§4De parameter \"" + args[1] + "\" kon niet veranderd worden naar een lege status");
					}
				}else{
					StringBuilder sb = new StringBuilder();
					for(int i = 2; i < args.length - 1; i++){
						sb.append(args[i]);
						sb.append(' ');
					}
					sb.append(args[args.length - 1]);
					String v = sb.toString();
					if(attribSys.hasAttrib("verkleur")){
						if(this.isSectionSignFormatAccepted(index)){
							v = AdvancedChat.verkleurUitgebreid(v);
							a.sendMessage("§eSectionSign-format is uitgevoerd");
						}else{
							a.sendMessage("§4SectionSign-format is niet geaccepteerd voor de parameter op index " + index);
						}
					}
					if(this.setParameter(index, v)){
						a.sendMessage("§eDe parameter \"" + args[1] + "\" is veranderd naar \"§r§f" + v + "§r§e\"");
					}else{
						a.sendMessage("§4De parameter \"" + args[1] + "\" kon niet veranderd worden naar \"§r§f" + v + "§r§4\"");
					}
				}
			}else{
				a.sendMessage("§ePNTechTextProvFormatted-deel van het commando: §cparameter <parameterNaam> [nieuwe waarde]");
			}
		}else{
			return false;
		}
		return true;
	}

	@Override
	public String[] getLocalTopLevelArgsPossibilities() {
		return new String[]{"parameter"};
	}

	protected String[] parameters;
	
	public byte getTextProvType(){return 2;}
	
	public abstract byte getFormattedType();
	protected abstract byte getCorrectAmountParameters();
	
	
	protected static PNTechTextProvFormatted loadFromBytes(byte[] src){
		if(src == null || src.length < PNTechTextProvFormatted.generalInfoLength())return null;
		
		byte t = src[PNTechTextProv.generalInfoLength()];//De hoogste bit hoeft er niet afgehaald te worden aangezien die geen functie heeft en dus niet gebruikt zo mogen worden. Als de hoogste bit dus voor verwarring zorgt, betekent dat dat er iets fout is.
		
		if(t == 1){
			return new PNTechTextProvFormattedVideo(src);
		}
		//System.out.println("            PNTechTextProvFormatted.loadFromBytes: PNTechTextProv geladen, onbekend TextProvType: " + t);
		return null;
	}
	
	protected PNTechTextProvFormatted(byte[] src) {
		super(src);
		this.initialize(src);
	}
	
	protected void initialize(byte[] src){
		if(src == null || src.length < PNTechTextProvFormatted.generalInfoLength())return;
		byte[][] a = StoreTechnics.loadArrayShort(src, 100, (short) 5000, PNTechTextProvFormatted.generalInfoLength());
		this.parameters = new String[a.length];
		
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		for(int i = 0; i < a.length; i++){
			if(a[i].length == 0)return;
			try {
				b.write(a[i]);
			} catch (IOException e) {
				//Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Een byte-array kon niet naar een ByteArrayOutputStream geschreven worden om het te converteren naar een String bij een PNTechTextProvFormatted: " + e.getMessage());
			}
			try {
				this.parameters[i] = b.toString("UTF8");
			} catch (UnsupportedEncodingException e) {
				//Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] De Encoding \"UTF8\" wordt niet herkend (PNTechTextProvFormatted): " + e.getMessage());
			}
			b.reset();
		}
		try {
			b.close();
		} catch (IOException e) {}
		
	}
	
	protected PNTechTextProvFormatted(String[] parameters, boolean invisible, int ID, PulserNotifStandard base){
		super(invisible, ID, base);
		if(parameters == null)parameters = new String[0];
		if(parameters.length > 100){
			String[] newparams = new String[100];
			System.arraycopy(parameters, 0, newparams, 0, 100);
		}
		this.parameters = parameters;
	}
	
	protected boolean setParameter(int index, String value){
		if(index < 0 || index >= this.parameters.length)return false;
		parameters[index] = value;
		this.notifyChange();
		this.onParametersChanged();
		return true;
	}
	
	protected boolean setNamedParameter(String key, String value){
		int index = this.getNamedParameterIndex(key);
		return this.setParameter(index, value);
	}
	
	protected abstract byte getParameterViewAccessLevel();
	protected abstract byte getParameterChangeAccessLevel(int paramID);
	
	protected abstract void onParametersChanged();
	protected abstract boolean isSectionSignFormatAccepted(int index);
	
	@Override
	protected byte[] saveTech(){//Dit is zonder de eerste byte van type	
		/*byte[] paramsarray;
		{
			int paramssize = 2 * parameters.length;
			byte[][] params = new byte[this.parameters.length][];
			int alength = 0;
			for(int i = 0; i < this.parameters.length; i++){
				if(this.parameters[i] == null)this.parameters[i] = "";
				if(this.parameters[i].length() > 2500){
					Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Bij het bewaren van Parameters van een PulserNotificationMessageFormatted, is een parameters geskipt omdat die te lang was");
					params[i] = new byte[0];
					continue;
				}
				alength += this.parameters[i].length();
				if(alength > 4500){
					Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Bij het bewaren van Parameters van een PulserNotificationMessageFormatted, is er gestopt met bewaren vanwege een te lange totale lengte van parmaeters");
					for(; i < this.parameters.length; i++){
						params[i] = new byte[0];
					}
					break;
				}
				paramssize += this.parameters[i].length();
				try {
					params[i] = this.parameters[i].getBytes("UTF8");
				} catch (UnsupportedEncodingException e) {
					Logger.getLogger("Minecraft").warning("[KartoffelKanaalPlugin] Bij het bewaren van Parameters van een PulserNotificationMessageFormatted, is er een UnsuportedException opgedoken: " + e.getMessage());
				}
			}
			
			paramsarray = new byte[paramssize];
			int pos = 0;
			for(int i = 0; i < params.length; i++){
				paramsarray[pos++] = (byte) ((params[i].length >>> 8) & 0xFF);
				paramsarray[pos++] = (byte) (params[i].length & 0xFF);
				System.arraycopy(params[i], 0, paramsarray, pos, params[i].length);
				pos += params[i].length;
			}
		}
		byte[] array = new byte[6 + paramsarray.length];
		array[0] = 1;
		array[1] = format;
		
		array[2] = (byte)((paramsarray.length >>> 24) & 0xFF);
		array[3] = (byte)((paramsarray.length >>> 16) & 0xFF);
		array[4] = (byte)((paramsarray.length >>>  8) & 0xFF);
		array[5] = (byte)( paramsarray.length         & 0xFF);
		*/
		byte[] params;
		if(this.parameters == null){
			params = new byte[0];
		}else{
			int l = this.parameters.length;
			if(l > 100)l = 100;
			byte[][] paramdata = new byte[l][];
			
			for(int i = 0; i < l; i++){
				try {
					paramdata[i] = (this.parameters[i] == null)?new byte[0]:this.parameters[i].getBytes("UTF8");
				} catch (UnsupportedEncodingException e) {
					Logger.getLogger("Minecraft").warning("[KKP] De Encoding \"UTF8\" is niet herkend bij een PNTechTextProvFormatted");;
					paramdata[i] = new byte[0];
				}
			}
			params = StoreTechnics.saveArrayShort(paramdata, 100);
		}
		byte[] ans = new byte[PNTechTextProvFormatted.generalInfoLength() + params.length];
		System.arraycopy(params, 0, ans, PNTechTextProvFormatted.generalInfoLength(), params.length);
		this.saveGeneralInfo(ans);
		return ans;
	}
	
	protected abstract int getNamedParameterIndex(String key);
	
	@Override
	public int getEstimatedSize(){
		if(this.parameters == null)return PNTechTextProvFormatted.generalInfoLength();
		int l = this.parameters.length * 2;
		for(int i = 0; i < this.parameters.length; i++){
			if(this.parameters[i] == null)continue;
			l += this.parameters[i].length();
		}
		return PNTechTextProvFormatted.generalInfoLength() + l;
	}
	
	//protected abstract void changeParameter(byte index, String value);//Hier hoort een validation bij betrokken te zijn of bepaalde parameters niet te lang zijn bv.

	protected static int generalInfoLength(){return PNTechTextProv.generalInfoLength() + 1;}
	
	protected boolean saveGeneralInfo(byte[] ans){
		if(ans == null || ans.length < PNTechTextProv.generalInfoLength() + 1)return false;
		super.saveGeneralInfo(ans);
		ans[PNTechTextProv.generalInfoLength()] = this.getFormattedType();
		return true;
	}

	protected abstract String[] getPossibleKeys();
}
