package KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import KartoffelKanaalPlugin.plugin.DataFieldShort;
import KartoffelKanaalPlugin.plugin.Main;

public class SpelerOptions {
private static final byte[] defaultOptions = new byte[]{                            
};
public static final SpelerOptions CONSOLE = new StaticSpelerOptions(new byte[]{
	"autoantilag.regelen",
			
	"arena.hoofdpermission", "arena.host.aanmaken", "arena.host.aanmaken.adv",
	"arena.host.aanmaken.adv.arenawapens", "arena.gevecht.deelnemen",
	"arena.gevecht.pause.timeout", "arena.gevecht.pause.infinite",
	
	"notifreceivement.change.mask1", "notifreceivement.change.mask2", "notifreceivement.change.all",
	"perms.essentials.protect.exempt"
};
public static final byte[] permissionAdresses = new byte[]{
	0x72,
			
	0x76, 0x66, 0x56, 0x46, 0x36, 0x26, 0x16,
		
	0x78, 0x68, 0x58,
	0x48
};
	protected long latestSaved = 0;
	//#0
	//1 byte: rank
	
	//#1: Arena-timeout
	//1 byte: max pauzeertijd
	
	//#2 + 3: ~leeg~ --Laatst gekregen dag van DailyDiamond--
	
	//#4:
	//4 bits: Donatierank (0 = niet gedonneerd)
	//4 bits: Bedrockblocks tegoed
	
	//#5 + 6: LoadCount

	//#7:
	//#16: Options
	//1 bit [0x70]: leeg
	//1 bit [0x60]: leeg
	//1 bit [0x50]: Vulnerable voor pulser
	//1 bit [0x40]: Personal Prefix
	
	//#18 (+ #19): Operator Permissions
	//1 bit [0x72]: Kan autoantilag regelen
	
	//#20 (+ #21): Donateur Permissions
	//1 bit [0x74]: leeg
	//1 bit [0x64]: Kan bedrock block opeisen
	//1 bit [0x54]: Mag donateur-fly gebruiken
	//1 bit [0x44]: leeg
	
	//#22 ( + #23): Arena
	//1 bit [0x76]: Hoofdpermission (kan anderen ook permissions geven)
	//1 bit [0x66]: Een host aanmaken
	//1 bit [0x56]: Een geavanceerde host aanmaken
	//1 bit [0x46]: Een geavanceerde host aanmaken met arenawapens
	//1 bit [0x36]: Mag deelnemen
	//1 bit [0x26]: Mag gevecht pauseren/hervatten zolang als de Arena-timeout
	//1 bit [0x16]: Mag gevecht pauseren/hervatten ongelimiteerd
	//1 bit [0x06]: leeg
	
	//------------
	//#24 (+ #25): Non-operator permissions
	//1 bit [0x78]: Mag PulserNotifReceivement veranderen volgens de eerste mask
	//1 bit [0x68]: Mag PulserNotifReceivement veranderen volgens de tweede mask
	//1 bit [0x58]: Mag alle PulserNotifReceivements veranderen
	//1 bit [0x48]: Mag Essentials use- en place-banlist bypassen. Mag dus lava, TnT,.. plaatsen...
		
	//1 bit [0x38]: leeg
	//1 bit [0x28]: leeg
	//1 bit [0x18]: leeg
	//1 bit [0x08]: leeg
	
	public SpelerOptions(byte[] a){
		/*if(sinceDate == 0){
			Calendar c = Calendar.getInstance();
			c.set(2014, 5, 1, 0, 0, 0);
			sinceDate = c.getTimeInMillis();
		}*/
	}
	
	public static byte getOptionAdress(String s){
		//0x7F = onbekend
		if(s == null || s.length() < 2)return 0x7F;
		//adress: 3 bits: bit index, 4 bits: byte index
		for(int i = 0; i < optionNames.length; i++){
			if(s.equals(optionNames[i]))return optionAdresses[i];
		}
		return 0x7F;
	}
			
	public void setDonatorRank(byte r, SpelerOptions executor, CommandSender a, boolean notifyAffected){
		if(executor.getRank() < 100 || executor.getOpStatus() < 2/*Moet absoluut zeker zijn dat de persoon Operator is*/){
			a.sendMessage("�4Enkel Owners hebben toegang tot dit commando");
			return;
		}

		if(r >= 20 && r < 70){
			data[4] &= 0x0F;
			if(r >= 35){
				data[4] |= 0x40;
			}else if(r >= 30){
				data[4] |= 0x30;
			}else if(r >= 25){
				data[4] |= 0x20;
			}else{
				data[4] |= 0x10;
			}
		}else if(r == -127 || r == 0) /*als er "geen" is opgegeven*/{
			data[4] &= 0x0F;
			if(this.getRank() < 70)this.setRank((byte) 10);
		}else{
			a.sendMessage("�4Onbekende donateurrank");
		}
		this.latestChange = System.currentTimeMillis();
		if(this.getDonatorRank() > this.getRank() || (this.getDonatorRank() < this.getRank() && this.getRank() >= 20 && this.getRank() < 70)){
			this.setRank(this.getDonatorRank());
		}
		
		if(executor == this){
			a.sendMessage("�eJe donateurrank is veranderd naar " + Rank.getRankDisplay(this.getDonatorRank()));
		}else{
			if(this.parent != null && this.parent.name != null){
				a.sendMessage("�eDe donateurrank van " + (this.parent.name == null?"iemand":this.parent.name) + " is veranderd naar " + Rank.getRankName(this.getDonatorRank()));
				if(notifyAffected)this.parent.sendMessage("�eJe donateurrank is veranderd naar " + Rank.getRankName(this.getDonatorRank()));
			}else{
				a.sendMessage("�eDe donateurrank is veranderd naar " + Rank.getRankName(this.getDonatorRank()));
			}
		}
	}
				int s = p.getInventory().firstEmpty();
				if(s == -1){
					p.sendMessage("�4Er is geen vrije ruimte in je inventory...");
					return;
				}
				
			Main.plugin.sendRawMessage(p.getName(), "[{text:\"Vind info over doneren op \",color:dark_red},{text:\"[/donateur]\",color:red,hoverEvent:{action:show_text,value:Klik},clickEvent:{action:run_command,value:\"/donateur\"}}]");
			if(loc == 5){//Vulnerable voor Pulser
				if(executor.getPermissionLevel() < 2){
					a.sendMessage("�4Je hebt geen machtiging om deze actie uit te voeren");
					return;
				}
				a.sendMessage("�4Deze option bestaat niet");
				return;
			}
			a.sendMessage("�4Niet spelen met de options van andere ops!");
			return;
		}
				if(notifyAffected)this.parent.sendMessage("�eJe optie " + getAdressName(adress, false) + " is veranderd naar " + (((data[index] & (0x01 << loc)) == 0x00)?"uit":"aan"));
		}
			return;
			/*
					a.sendMessage("�4Deze permission bestaat nog niet");
					return;
				}
					if(this.getPermissionLevel() < 13){a.sendMessage("�4Je hebt minimum Permission Level 13 nodig om dit te doen");return;}
				}else if(loc == 6){
					if(this.getPermissionLevel() < 7){a.sendMessage("�4Je hebt minimum Permission Level 7 nodig om dit te doen");return;}
				}else if(loc == 5){
					if(soft){
						if(this.getPermissionLevel() < 7){a.sendMessage("�4Je hebt minimum Permission Level 7 nodig om dit te doen");return;}
					}else{
						if(this.getPermissionLevel() < 11){a.sendMessage("�4Je hebt minimum Permission Level 11 nodig om dit te doen");return;}
					}
				}else if(loc == 4){
					if(soft){
						if(this.getPermissionLevel() < 7){a.sendMessage("�4Je hebt minimum Permission Level 7 nodig om dit te doen");return;}
					}else{
						if(this.getPermissionLevel() < 11){a.sendMessage("�4Je hebt minimum Permission Level 11 nodig om dit te doen");return;}
					}
				}else if(loc > 0){
					if(this.getPermissionLevel() < 7){a.sendMessage("�4Je hebt minimum Permission Level 7 nodig om dit te doen");return;}
				}else{//0x06 bestaat nog niet
					a.sendMessage("�4Deze permission bestaat nog niet");
					return;
				}
				return;
			byte permissionLevelRequired = SpelerOptions.getPermissionLevelRequired(index, loc, soft?1:2);
			if(permissionLevelRequired == 127){
				a.sendMessage("�4Onbekende permission");
				return;
			}else{
				if(executor.getPermissionLevel() < permissionLevelRequired){
					a.sendMessage("�4Voor deze operation heb je PermissionLevel " + permissionLevelRequired + " nodig, jij hebt maar PermissionLevel " + executor.getPermissionLevel());
					return;
				}
			}
		this.latestChange = System.currentTimeMillis();
		if(this.parent != null)this.parent.refreshBukkitPermissions();
		if(executor == this){
			a.sendMessage("�eJe permission \"" + SpelerOptions.getAdressName(adress, false) + "\" is nu " + (((data[index + 1] & posmask) == posmask)?"Static ":"Dynamic ") + (((data[index] & posmask) == posmask)?"�aAan":"�4Uit"));
			this.latestChange = System.currentTimeMillis();
		}else{
				if(notifyAffected){
					if((data[index] & posmask) == posmask){
						this.parent.sendMessage("�eJe hebt nu de permission \"�2" + SpelerOptions.getAdressName(adress, false) + "�e\" (" + (((data[index + 1] & posmask) == posmask)?"Static":"Dynamic") + ')');
					}else{
						this.parent.sendMessage("�eJe hebt de permission \"�4" + SpelerOptions.getAdressName(adress, false) + "�4\" niet meer (" + (((data[index + 1] & posmask) == posmask)?"Static":"Dynamic") + ')');
					}
				}
				a.sendMessage("�eDe permission \"" + SpelerOptions.getAdressName(adress, false) + "\" is nu " + (((data[index + 1] & posmask) == posmask)?"Static ":"Dynamic ") + (((data[index] & posmask) == posmask)?"�aAan":"�4Uit"));
			}			
		}
	public void setRank(byte i){
		if(i == -128){
			return;
		}
		if(data[0] < 70){
			if(i >= 70){
				this.setPermissionLevel((byte) 13);
			}
		}else{
			if(i < 70){
				this.setPermissionLevel((byte)1);
			}
		}
			
		if(i < getDonatorRank()){
			i = getDonatorRank();
		}
		data[0] = i;
		this.latestChange = System.currentTimeMillis();
		
		/*pl.removeAttachment();
		Rank.setPermissions(r, pl, p);*/
	}
		if(this.parent == null || this.parent.pm == null)return;
		DataFieldShort df = this.parent.pm.dailyDiaDays;
		if(df == null)return;
		
		df.setValue(this.parent.kartoffelID, day);
		
		this.latestChange = System.currentTimeMillis();*/
/*---Functions Part 3: Info-functions---*/
		if(Main.sm == null)return false;
		if(this.parent == null || this.parent.pm == null)return 0x7FFF;
		DataFieldShort df = this.parent.pm.dailyDiaDays;
		if(df == null)return 0x7FFF;
		try{
			return df.getValue(this.parent.kartoffelID);
		}catch(Exception e){
			return 0x7FFF;
		}
	public static SpelerOptions getDefaultOptions(){
		byte[] array = defaultOptions.clone();
		return new SpelerOptions(array);
	}
	/*public short getToday(){
		//long l = Calendar.getInstance().getTimeInMillis();
		//return (short) ((l - sinceDate) / 86400000);
		if(Main.sm != null){
			return Main.sm.getDailyDiaDay();
		}else{
			long l = Calendar.getInstance().getTimeInMillis();
			return (short) ((l - sinceDate) / 86400000);
		}
	}*/	
		
	protected void setSwitchWithoutUpdate(byte adress, boolean on, boolean staticpart){
		if(adress == 0x7F)return;
		byte index = (byte) (adress & 0x0F);
		if(staticpart && (index + 1 < 32))++index;
		byte loc = (byte) (adress >>> 4);
		setSwitches(index + 16, (byte)(0x01 << loc), on);
	}
	public boolean getSwitches(int index, byte switches){
		if(switches == 0x00)return false;
		return (index >= 0 && index < 32)?((data[index] & switches) == switches):false;
	}
	protected void setSwitches(int index, byte switches, boolean on){
		if(index < 0 || index >= data.length)return;
		if(on){
			data[index] |= switches;
		}else{
			data[index] &= ~switches;
		}
		this.latestChange = System.currentTimeMillis();
	}
	
	public boolean canChangeNotifReceivement(int pulserNotifID){//Ops zouden altijd mogen, maar dat is niet inbegrepen in deze functie
		if(pulserNotifID < 0 || pulserNotifID > 15 || Main.sm == null)return false;
		if((this.data[24] & 0x20) == 0x20)return true;
		
		short pulserNotif = (short) (0x8000 >>> pulserNotifID);
		if(((this.data[24] & 0x80) == 0x80) && (Main.sm.firstNotifChangeMask & pulserNotif) == pulserNotif){
			return true;
		}
		
		if(((this.data[24] & 0x40) == 0x40) && (Main.sm.secondNotifChangeMask & pulserNotif) == pulserNotif){
			return true;
		}
		
		return false;
	}
	
	public void refreshRank(){
		this.refreshRankRequirments();
		parent.refreshBukkitPermissions();
		this.refreshPermProperties();
	}
		applyRankPermissions(this.getRank());		
		data[24] = (byte) (data[24] & data[25]);
		
			data[22] |= (0x30 & ~data[23]);
			data[24] |= (0xE0 & ~data[25]);
			data[20] |= (0x20 & ~data[21]);
			data[18] |= (0x20 & ~data[19]);
			data[24] |= (0x20 & ~data[25]);
		in = in.toLowerCase();
		return ans;
		ArrayList<String> ans = new ArrayList<String>();
		return ans;
		this.latestChange = System.currentTimeMillis();
	
	protected static byte getPermissionLevelRequired(byte adress, int operation){
		return SpelerOptions.getPermissionLevelRequired((byte)(adress & 0x0F), (byte)(adress >>> 4), operation);
	}
	
	/* //Van vorige functie die getPermissionAccessLevel heette:
	 * //-1: onbestaande
	 * // 0: geen permission
	 * // 1: get
	 * // 2: get & soft set
	 * // 3: get & * set
	 */
	
	//Operation:
	//0: get
	//1: soft set
	//2:  *   set
	
	//Veranderd naar een functie om te kijken wel PermissionLevel nodig is
	protected static byte getPermissionLevelRequired(byte index, byte loc, int operation){
		//Als de permission niet gevonden is of de operation niet klopt, wordt uiteindelijk 127 returnt omdat het hoogst mogelijke value 15 is (vanwege 4 bits) en zo niemand permission heeft om deze incorrecte dingen te doen
		if(index == 2){//Algemene Permissions
			if(loc == 7){
				if(operation == 2)return 11;//full set
				if(operation == 1)return  9;//soft set
				if(operation == 0)return  9;//     get
			}
		}else if(index == 4){
			if(loc > 4){
				if(operation == 2)return 11;//full set
				if(operation == 1)return  9;//soft set
				if(operation == 0)return  9;//     get
			}
		}else if(index == 6){
			if(loc == 7){
				if(operation == 2)return 13;//full set
				if(operation == 1)return 13;//soft set
				if(operation == 0)return 13;//     get
			}else if(loc == 6){
				if(operation == 2)return  7;//full set
				if(operation == 1)return  7;//soft set
				if(operation == 0)return  7;//     get
			}else if(loc == 5){
				if(operation == 2)return 11;//full set
				if(operation == 1)return  7;//soft set
				if(operation == 0)return  7;//     get
			}else if(loc == 4){
				if(operation == 2)return 11;//full set
				if(operation == 1)return  7;//soft set
				if(operation == 0)return  7;//     get
			}else if(loc > 0){
				if(operation == 2)return  7;//full set
				if(operation == 1)return  7;//soft set
				if(operation == 0)return  7;//     get
			}
		}else if(index == 8){
			if(loc == 7 || loc == 6 || loc == 5){
				if(operation == 2)return  5;//full set
				if(operation == 1)return  4;//soft set
				if(operation == 0)return  4;//     get
			}else if(loc == 4){
				if(operation == 2)return 13;//full set
				if(operation == 1)return 13;//soft set
				if(operation == 0)return 13;//     get
			}
		}
		return 127;
	}
	
	public boolean isChanged(){
		return this.latestChange > this.latestSaved;
	}
	
	public long getTimeSinceLatestSave(){
		return System.currentTimeMillis() - this.latestSaved;
	}
}