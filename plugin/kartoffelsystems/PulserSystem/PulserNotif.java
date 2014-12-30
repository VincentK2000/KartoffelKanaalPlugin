package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import KartoffelKanaalPlugin.plugin.AttribSystem;
import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;
import KartoffelKanaalPlugin.plugin.Main;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;

public abstract class PulserNotif implements IObjectCommandHandable {
	protected boolean denyChanges;//Dit is voor bv. copy, move, edit,...
	protected byte options = 0x00;
	protected boolean invisible = true;
	protected int specialOperationLockKey;
	
	private boolean changed;
	
	protected byte[] cache = null;
	
	protected PulserNotif(byte[] src){
		this.cache = src;
		if(src != null && src.length >= PulserNotif.generalInfoLength()){
			this.invisible = (src[0] & 0x80) == 0x80;
			this.options = src[1];
		}
	}
	
	protected PulserNotif(boolean invisible, byte options){
		this.invisible = invisible;
		this.options = options;
	}
	
	protected abstract byte getNotifType();
	
	protected abstract void processTick(Person[] online, boolean[] receivers, int tick);
	
	protected abstract void sendMessage(Person[] p, boolean[] receivers);
	
	protected boolean isEditMode(){
		return denyChanges;
	}
	protected boolean startEdit(){
		if(denyChanges){
			return false;
		}else{
			this.denyChanges = true;
			return true;
		}
	}
	protected void stopEdit(){
		this.denyChanges = false;
	}
	
	public boolean isActive(){
		return !this.invisible;
	}

	public void deactivate() throws Exception{
		this.checkDenyChanges();
		this.invisible = true;
		this.notifyChange();
	}

	public void activate(CommandSender a, AttribSystem attribSys) throws Exception{
		if(a == null || attribSys == null)throw new NullPointerException("CommandSender of AttribSystem is null");
		if(!a.isOp()){
			a.sendMessage("§4Je moet Op zijn om deze actie uit te mogen voeren");
			return;
		}
		this.checkDenyChanges();
		int activationStage = (this.activationRequiresCrashTest() || attribSys.hasAttrib("doCrashTest"))?attribSys.getIntValue("_activationStage", 0):100;
		if(a instanceof ConsoleCommandSender && attribSys.hasAttrib("skipCrashTest")){
			a.sendMessage("§4Crash-test skippen. Onthoud dat de crash test wel een nut heeft en je wilt natuurlijk niet dat clients crashen omwille van een foute PulserNotifications...");
			activationStage = 100;
		}
		if(activationStage == 0){
			a.sendMessage("§eWegens grote veranderingen moet er een crash-test uitgevoerd worden om te kijken of clients niet crashen door de PulserNotifications.");
			if(!(a instanceof Player)){
				a.sendMessage("§4Deze test is alleen beschikbaar voor spelers. Gebruik het attribuut \"^skipCrashTest\" als je op de Console bent om de test over te slaan");
				return;
			}
			Player pl = (Player) a;
			pl.sendMessage("§eTijdens de test zullen een of meerdere berichten naar je gestuurd worden. Als je de berichten correct ontvangt, moet je op [Activeer] duwen om het correct ontvangen te bevestigen.");
			pl.sendMessage("§eKlik op [Start] om de test te beginnen");
			CommandsPulser.activationComponent = this;
			Main.plugin.getServer().dispatchCommand(Main.plugin.getServer().getConsoleSender(), "tellraw " + pl.getName() + " [{text:\"Klik om de crash-test te beginnen: \",color:\"yellow\"},{text:\"[Start]\",color:\"dark_red\",bold:true,clickEvent:{action:\"run_command\",value:\"/pulser _activation ^doCrashTest ^_activationStage:1\"}}]");
		}else if(activationStage == 1){
			if(!(a instanceof Player)){
				a.sendMessage("§4Deze test is alleen beschikbaar voor spelers. Gebruik het attribuut \"^skipCrashTest\" als je op de Console bent om de test over te slaan");
				return;
			}
			Player pl = (Player) a;
			try{
				this.doCrashTest(pl);
			}catch(Exception e){//Misschien kan er een Exception voorkomen als de client omwille van een crash eerder de server verlaat dan het volgende bericht gestuurd wordt
				if(pl.isOnline()){
					pl.sendMessage("§4Hmm... De crash-test is om de een of andere reden mislukt...");
					return;
				}
			}
			if(pl.isOnline()){
				this.specialOperationLockKey = (new Random()).nextInt();
				Main.plugin.getServer().dispatchCommand(Main.plugin.getServer().getConsoleSender(), "tellraw " + pl.getName() + " [{text:\"Klik om te bevestigen dat de crash-test succesvol is verlopen: \",color:\"yellow\"},{text:\"[Activeer]\",color:\"dark_red\",bold:true,clickEvent:{action:\"run_command\",value:\"/pulser _activation ^_activationStage:100 ^doCrashTest ^_specialOperationLockKey:" + this.specialOperationLockKey + "\"}}]");
			}
		}else if(activationStage == 100){
			if(!(a instanceof ConsoleCommandSender) && (this.activationRequiresCrashTest() || attribSys.hasAttrib("doCrashTest")) && this.specialOperationLockKey != attribSys.getIntValue("_specialOperationLockKey", -1)){
				a.sendMessage("§4Oncorrecte specialOperationKey. Doe de test misschien opnieuw. Als dit probleem zich weer voordoet, contacteer dan de developer van de KKP");
				return;
			}
			this.invisible = false;
			this.notifyChange();
			a.sendMessage("§eDe PulserNotif is geactiveerd");
		}else{
			a.sendMessage("§4ERROR: Onbekende activationStage!");
		}
	}

	/*public void setActive(boolean status) throws Exception{Bij de activatie zou er mogelijk een crashTest moeten plaatsvinden...
		if(this.denyChanges())throw new Exception("De PulserNotif accepteert geen veranderingen. Controleer of read-only aan staat.");
		this.invisible = !status;
		this.notifyChange();
	}*/
	
	public abstract boolean activationRequiresCrashTest();

	/*public void setActive(boolean status) throws Exception{Bij de activatie zou er mogelijk een crashTest moeten plaatsvinden...
		if(this.denyChanges())throw new Exception("De PulserNotif accepteert geen veranderingen. Controleer of read-only aan staat.");
		this.invisible = !status;
		this.notifyChange();
	}*/
	
	public abstract void doCrashTest(Player pl) throws Exception;

	public void checkDenyChanges() throws Exception{
		if(this.denyChanges())throw new Exception("Veranderingen zijn niet toegestaan voor de PulserNotif. Controleer read-only");
	}

	public boolean denyChanges(){
		return this.denyChanges || (this.options & 0x20) != 0x00;
	}

	public boolean isChanged(){
		return changed;
	}
	
	protected void notifyChange(){
		this.changed = true;
		if(Main.pulser != null)Main.pulser.notifyChange();
	}

	public static PulserNotif loadFromBytes(byte[] src){
		//System.out.println("PulserNotif.loadFromBytes: Notification laden...");
		if(src == null || src.length < generalInfoLength())return null;
		//System.out.println("PulserNotif.loadFromBytes: Voldoende informatie om mee te starten");
		PulserNotif a = null;
		byte t = (byte) (src[0] & 0x7F);
		if(t == 1){
			a = PulserNotifStandard.loadFromBytes(src);
		}
		if(a == null){
			a = new PulserNotifNLOADED(src);
		}
		//System.out.println("PulserNotif.loadFromBytes: Notification geladen, a = " + (a == null?"null":a));
		return a;
	}

	protected abstract byte[] saveNotif();

	protected static int generalInfoLength(){return 2;}

	protected void saveGeneralInfo(byte[] ans){
		if(ans == null || ans.length < 2)return;
		ans[0] = this.getNotifType();
		if(this.invisible)ans[0] |= 0x80;
		ans[1] = this.options;
	}
	
	
	
	/*public void setActive(boolean status) throws Exception{Bij de activatie zou er mogelijk een crashTest moeten plaatsvinden...
		if(this.denyChanges())throw new Exception("De PulserNotif accepteert geen veranderingen. Controleer of read-only aan staat.");
		this.invisible = !status;
		this.notifyChange();
	}*/
	
	
	
	//public abstract void handleLocalCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args) throws Exception;
	/*
	public abstract String[] getLocalTopLevelArgsPossibilities();
	public final    String[] getTotalTopLevelArgsPossibilities(){
		String[] general = new String[]{"actief","read-only","forceSend"};
		String[] local = this.getLocalTopLevelArgsPossibilities();
		if(local == null)local = new String[0];
		
		String[] total = new String[general.length + local.length];
		System.arraycopy(general, 0, total, 0, general.length);
		System.arraycopy(local, 0, total, general.length, local.length);
		return total;
	}*/

	@Override
	public boolean handleObjectCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args) throws Exception{		
		String label = args[0].toLowerCase();
		
		if(label.equals("actief")){
			if(executor.getSpelerOptions().getOpStatus() < 2){
				throw new Exception("§4Je hebt geen toegang tot dit commando");
			}
			if(args.length < 2){
				a.sendMessage("§eDe staat van de notification is " + (this.isActive()?"§2aan":"§4uit"));
			}else{
				args[1] = args[1].toLowerCase();
				if(args[1].equals("on") || args[1].equals("aan") || args[1].equals("+")){
					this.activate(a, attribSys);
				}else if(args[1].equals("off") || args[1].equals("uit") || args[1].equals("-")){
					this.deactivate();
					a.sendMessage("§eDe staat van de notification is nu " + (this.isActive()?"§2aan":"§4uit"));
				}else{
					a.sendMessage("§4Mogelijke nieuwe waarden voor de staat zijn: §2on, aan, +§f of §4off, uit, -");
					return true;
				}
			}
		}else if(label.equals("read-only")){
			if(executor.getSpelerOptions().getOpStatus() < 2){
				throw new Exception("Je hebt geen toegang tot dit commando");
			}
			if(args.length < 2){
				a.sendMessage("§eRead-only staat " + (((this.options & 0x20) == 0x20)?"§4aan":"§2uit") + "§e en is " + (((this.options & 0x10) == 0x10)?"wel":"niet") + " locked door de Console of Dev");
			}else{
				if(executor.getSpelerOptions().getOpStatus() < 3 && ((this.options & 0x10) == 0x10)){
					a.sendMessage("§4De read-only is locked door de Console of de Dev");
					return true;
				}
				boolean nieuweWaarde;
				args[1] = args[1].toLowerCase();
				if(args[1].equals("on") || args[1].equals("aan") || args[1].equals("+")){
					nieuweWaarde = true;
				}else if(args[1].equals("off") || args[1].equals("uit") || args[1].equals("-")){
					nieuweWaarde = false;
				}else{
					a.sendMessage("§4Mogelijke nieuwe waarden voor de staat zijn: §2on, aan, +§f of §4off, uit, -");
					return true;
				}
				if(nieuweWaarde){
					this.options |= 0x20;
				}else{
					this.options &= 0xDF;
				}
				this.notifyChange();
				a.sendMessage("§eDe notification is nu " + (((this.options & 0x20) == 0x20)?"§4read-only":"§2niet read-only"));
			}
		}else if(label.equals("forcesend")){
			if(executor.getSpelerOptions().getOpStatus() < 2){
				throw new Exception("Je hebt geen toegang tot dit commando");
			}
			if(args.length < 2){
				a.sendMessage("§eDit bericht wordt enkel naar jou gestuurd. Gebruik Notif-Deel: \"forceSend all\" of \"forceSend <spelernamen...>\"");
				this.sendMessage(new Person[]{executor}, new boolean[]{true});
			}else{
				if(Main.pm == null || Main.pm.preventAction()){
					a.sendMessage("§4De PlayerManager is niet beschikbaar");
					return true;
				}
				Person[] online = Main.pm.getOnlinePlayers();
				boolean[] receive = new boolean[online.length];
				if(args[1].toLowerCase().equals("all")){
					for(int i = 0; i < receive.length; i++){
						receive[i] = true;
					}
				}else{
					for(int i = 0; i < online.length; i++){
						if(online[i] == null)continue;
						for(int arg = 1; arg < args.length; arg++){
							if(online[i].getName().equals(args[arg].toLowerCase()) || online[i].getUUID().toString().equalsIgnoreCase(args[arg])){
								receive[i] = true;
								break;
							}
						}
					}
				}
				this.sendMessage(online, receive);
				a.sendMessage("§eBericht(en) gestuurd");
			}
		}else if(label.equals("getType")){
			if(executor.getSpelerOptions().getOpStatus() < 2){
				throw new Exception("Je hebt geen toegang tot dit commando");
			}
			a.sendMessage("§eNotificationType = " + this.getNotifType());
		}else if(label.equals("toString")){
			if(executor.getSpelerOptions().getOpStatus() < 2){
				throw new Exception("Je hebt geen toegang tot dit commando");
			}
			a.sendMessage("§etoString() = " + this.toString());
		}else{		
			return false;
		}
		return true;
	}
	
	/*private String getTopLevelPossibilitiesString(){
		String[] total = this.getTotalTopLevelArgsPossibilities();
		if(total.length == 0)return "";
		StringBuilder sb = new StringBuilder(20);
		for(int i = 0; i < total.length - 1; i++){
			if(total[i] == null || total[i].length() == 0)continue;
			sb.append(total[i]);
			sb.append('|');
		}
		if(total[total.length - 1] != null){
			sb.append(total[total.length - 1]);
		}
		return sb.toString();
	}*/
	
	@Override
	public ArrayList<String> autoCompleteObjectCommand(String[] args, ArrayList<String> a) throws Exception{
		String label = args[0];
		if(args.length == 1){
			if("actief".startsWith(label))a.add("actief");
			if("read-only".startsWith(label))a.add("read-only");
			if("forcesend".startsWith(label))a.add("forcesend");
		}else if(args.length > 1){
			if(label.equals("actief")){
				if("aan".startsWith(args[1]))a.add(args[1]);
				if("uit".startsWith(args[1]))a.add(args[1]);
			}else if(label.equals("read-only")){
				if("aan".startsWith(args[1]))a.add(args[1]);
				if("uit".startsWith(args[1]))a.add(args[1]);
			}
		}
		return a;
	}
	
	@Override
	public String toString(){
		return "PulserNotif[invisible=" + this.invisible + ",options=" + this.options + ",notifType=" + this.getNotifType() + "]";
	}
}
