package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.ArrayList;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import KartoffelKanaalPlugin.plugin.AttribSystem;
import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;
import KartoffelKanaalPlugin.plugin.Main;
import KartoffelKanaalPlugin.plugin.StoreTechnics;
import KartoffelKanaalPlugin.plugin.VirtualSubObject;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;

public class PulserNotifStandard extends PulserNotif{
	@Override
	protected byte getNotifType(){return 1;}

	protected boolean changed = false;

	protected byte category;
	
	protected int interval;
	protected int offset;
	
	protected PNTech[] technics;
	
	protected PNTechTextProv textProv;
	protected PNTechCondition condition;
	protected PNTechDataFieldConn datafield;
	protected PNTechNotifSize notifSize;
	protected PNTechSpecEditAccess specAccess;
	
	protected PulserNotifStandard(byte[] src, PNTech[] techs, byte category, int interval, int offset){
		super(src);
		if(src == null || src.length == 0)this.changed = true;
		this.technics = techs;
		if(this.technics != null){
			for(int i = 0; i < this.technics.length; i++){
				this.technics[i].notificationBase = this;
			}
		}
		this.changed = false;
		this.category = category;
		this.interval = interval;
		this.offset = offset;
		this.recheckPrimaryTechnics();
	}
	
	protected PulserNotifStandard(PNTech[] techs, boolean invisible, byte options, byte category, int interval, int offset){
		super(invisible, options);
		this.technics = techs;
		if(this.technics != null){
			for(int i = 0; i < this.technics.length; i++){
				this.technics[i].notificationBase = this;
			}
		}
		this.category = category;
		this.interval = interval;
		this.offset = offset;
		this.recheckPrimaryTechnics();
	}
	
	@Override
	protected void processTick(Person[] online, boolean[] receivers, int tick){
		if(this.invisible)return;
		//System.out.println("PulserNotifStandard processTick");
		if(this.interval > 0 && (tick % this.interval) != this.offset){
			//System.out.println("PulserNotifStandard geannuleerd omdat de tick niet past bij de huidige. Interval = " + this.interval + ". Tick = " + tick + ". Offset = " + this.offset);
			return;
		}
		/*if(!this.goodAdditionalConditions())return;
		
		if(this.nm != null){
			this.nm.calculateNotificationMessage();
		}
		if(this.rawmessage == null || this.rawmessage.length() == 0)return;*/
		if(this.condition != null && !this.condition.isInvisible() && this.condition.preventMessage())return;
		//System.out.println("PulserNotifStandard conditions passed");
		this.sendMessage(online, receivers);
	}
	
	@Override
	protected void sendMessage(Person[] p, boolean[] receivers){
		this.sendMessage(p, receivers, true);
	}
	
	protected void sendMessage(Person[] p, boolean[] receivers, boolean increaseStats) {
		if(p == null || p.length == 0)return;
		//System.out.println("PulserNotifStandard: Preparing message...");
		//System.out.println("PulserNotifStandard: De textProv is " + (this.textProv == null?"null":"niet null"));
		if(this.textProv == null || this.textProv.isInvisible())return;
		String msg = this.textProv.getMessage();
		//System.out.println("PulserNotifStandard: Sending message...");
		
		String start = "tellraw ";
		String end = new String(' ' + msg);
		Server s = Main.plugin.getServer();
		ConsoleCommandSender sender = s.getConsoleSender();
		
		for(int i = 0; i < p.length; i++){
			if(p != null && (receivers == null || i >= receivers.length || receivers[i]))s.dispatchCommand(sender, start + p[i].getName() + end);
		}
		
		//System.out.println("PulserNotifStandard: Messages sended");
		
		if(increaseStats && this.datafield != null && !this.datafield.isInvisible()){
			short[] a = new short[p.length];
			for(int i = 0; i < p.length; i++){
				a[i] = p[i].getKartoffelID();
			}
			this.datafield.increaseValue(a);
		}
	}
	
	/*@Override
	protected void onPlayerJoin(Person p) {
		if(p == null)return;
		this.receiversLock.lock();
		
		if(this.receivers == null || this.receivers.length == 0){
			this.receivers = new Person[1];
		}
		
		boolean notPlaced = true;
		for(int i = 0; i < this.receivers.length; i++){
			if(this.receivers[i] == null){
				this.receivers[i] = p;
				notPlaced = false;
				break;
			}
		}
		if(notPlaced){
			Person[] newReceivers = new Person[this.receivers.length + 1];
			System.arraycopy(this.receivers, 0, newReceivers, 0, this.receivers.length);
			this.receivers = newReceivers;
		}
		
		this.receiversLock.unlock();
	}
	@Override
	protected void onPlayerLeave(Person p){
		if(p == null)return;
		
		this.receiversLock.lock();
		
		for(int i = 0; i < this.receivers.length; i++){
			if(this.receivers[i] == p)this.receivers[i] = null;
		}
		
		int conserveLength = this.receivers.length;
		while(conserveLength > PulserNotifStandard.standardReceiversLength){
			if(this.receivers[conserveLength - 1] == null)
				conserveLength--;
			else
				break;
		}
		if(conserveLength < this.receivers.length){
			Person[] newReceivers = new Person[conserveLength];
			System.arraycopy(this.receivers, 0, newReceivers, 0, conserveLength);
			this.receivers = newReceivers;
		}
		
		this.receiversLock.unlock();
	}*/
	
	protected boolean goodAdditionalConditions(){
		//if(interval == 0)return true;
		//return (tick % this.interval) == offset;
		return true;
	}
	
	protected byte[] getSaveArrayExtraData(){
		return new byte[0];
	}
	
	protected final byte[] saveNotif(){	
		/*
		byte[] message;
		if(this.rawmessage == null || this.rawmessage.length() == 0 || (this.nm != null && this.nm.preventSavingRawText())){
			message = new byte[0];
		}else{
			try{
				message = this.rawmessage.getBytes("UTF8");
			}catch(Exception e){
				message = new byte[0];
			}
		}
		
		byte[] extradata = getSaveArrayExtraData();
		if(extradata == null)extradata = new byte[0];
		
		byte[] nmarray = ((this.nm == null)?new byte[0]:this.nm.getSaveArray());
		if(nmarray == null)nmarray = new byte[0];
		
		byte[] result = new byte[22 + message.length + extradata.length + nmarray.length];
		result[0] = category;
		result[1] = options;
		
		result[2] = (byte)((this.interval >>> 24) & 0xFF);
		result[3] = (byte)((this.interval >>> 16) & 0xFF);
		result[4] = (byte)((this.interval >>>  8) & 0xFF);
		result[5] = (byte)( this.interval         & 0xFF);
		
		result[6] = (byte)((this.offset >>> 24) & 0xFF);
		result[7] = (byte)((this.offset >>> 16) & 0xFF);
		result[8] = (byte)((this.offset >>>  8) & 0xFF);
		result[9] = (byte)( this.offset         & 0xFF);
		
		result[10] = (byte)((message.length >>> 24) & 0xFF);
		result[11] = (byte)((message.length >>> 16) & 0xFF);
		result[12] = (byte)((message.length >>>  8) & 0xFF);
		result[13] = (byte)( message.length         & 0xFF);
		
		for(int i = 0; i < message.length; i++){
			result[i + 14] = message[i];
		}
		int index = 14 + message.length;
		
		
		result[index++] = (byte)((extradata.length >>> 24) & 0xFF);//*************************************
		result[index++] = (byte)((extradata.length >>> 16) & 0xFF);//* Incremention dient pas na het     *
		result[index++] = (byte)((extradata.length >>>  8) & 0xFF);//* opvragen van de value te gebeuren *
		result[index++] = (byte)( extradata.length         & 0xFF);//*************************************
		
		for(int i = 0; i < extradata.length; i++){
			result[i + index] = extradata[i];
		}
		index = index + extradata.length;
		
		
		result[index++] = (byte)((nmarray.length >>> 24) & 0xFF);//*************************************
		result[index++] = (byte)((nmarray.length >>> 16) & 0xFF);//* Incremention dient pas na het     *
		result[index++] = (byte)((nmarray.length >>>  8) & 0xFF);//* opvragen van de value te gebeuren *
		result[index++] = (byte)( nmarray.length         & 0xFF);//*************************************
		
		for(int i = 0; i < nmarray.length; i++){
			result[i + index] = nmarray[i];
		}
		
		return result;
		//index = index + extradata.length;*/
		byte[] techs;
		{
			byte[][] data = new byte[this.technics.length][];
			for(int i = 0; i < data.length; i++){
				if(this.technics[i] != null)data[i] = this.technics[i].saveTech();
			}
			techs = StoreTechnics.saveArray(data, 1000);
		}
		
		byte[] ans = new byte[generalInfoLength() + 13 + techs.length];
		
		int pos = generalInfoLength();
		ans[pos++] = this.category;
		
		ans[pos++] = (byte) ((this.interval >>> 24) & 0xFF);
		ans[pos++] = (byte) ((this.interval >>> 16) & 0xFF);
		ans[pos++] = (byte) ((this.interval >>>  8) & 0xFF);
		ans[pos++] = (byte) ( this.interval         & 0xFF);
		
		ans[pos++] = (byte) ((this.offset >>> 24) & 0xFF);
		ans[pos++] = (byte) ((this.offset >>> 16) & 0xFF);
		ans[pos++] = (byte) ((this.offset >>>  8) & 0xFF);
		ans[pos++] = (byte) ( this.offset         & 0xFF);
		
		ans[pos++] = (byte) ((techs.length >>> 24) & 0xFF);
		ans[pos++] = (byte) ((techs.length >>> 16) & 0xFF);
		ans[pos++] = (byte) ((techs.length >>>  8) & 0xFF);
		ans[pos++] = (byte) ( techs.length         & 0xFF);
		
		int l = techs.length;
		if(l > 20000)l = 20000;
		System.arraycopy(techs, 0, ans, pos, l);
		
		saveGeneralInfo(ans);
		return ans;
	}
	
	protected void increaseSize(int totalincrease, int userincrease, CommandSender user){
		
	}
	
	protected void recheckPrimaryTechnics(){
		//System.out.println("recheckingPrimaryTechnics");
		this.textProv = null;
		this.condition = null;
		this.datafield = null;
		this.notifSize = null;
		this.specAccess = null;
		if(this.technics == null)return;
		//System.out.println("technics.length = " + this.technics.length);
		for(int i = 0; i < this.technics.length; i++){
			if(this.technics[i] == null || this.technics[i].isInvisible()){
				//System.out.println("Skipping technic " + i + ":");
				//if(this.technics[i] == null){
				//	System.out.println("Technic " + i + " is null");
				//}else if(this.technics[i].isInvisible()){
				//	System.out.println("Technic " + i + " is invisible");
				//}else{
				//	System.out.println("???");
				//}
				continue;
			}
			//System.out.println("Processing technic " + i + "...");
			
			if(this.technics[i] instanceof PNTechTextProv){
				//System.out.println("Technic " + i + " is PNTechTextProv");
				if(this.textProv == null)this.textProv = (PNTechTextProv) this.technics[i];
				
			}else if(this.technics[i] instanceof PNTechCondition){
				//System.out.println("Technic " + i + " is PNTechCondition");
				if(this.condition == null)this.condition = (PNTechCondition) this.technics[i];
				
			}else if(this.technics[i] instanceof PNTechDataFieldConn){
				//System.out.println("Technic " + i + " is PNTechDataFieldConn");
				if(this.datafield == null)this.datafield = (PNTechDataFieldConn) this.technics[i];
				
			}else if(this.technics[i] instanceof PNTechNotifSize){
				//System.out.println("Technic " + i + " is PNTechNotifSize");
				if(this.notifSize == null)this.notifSize = (PNTechNotifSize) this.technics[i];
				
			}else if(this.technics[i] instanceof PNTechSpecEditAccess){
				//System.out.println("Technic " + i + " is PNTechSpecEditAccess");
				if(this.specAccess == null)this.specAccess = (PNTechSpecEditAccess) this.technics[i];
			}else{
				//System.out.println("Technic " + i + " is van een onbekend type");
			}
		}
	}
	
	public static PulserNotifStandard loadFromBytes(byte[] src) {
		//System.out.println("    PulserNotifStandard.loadFromBytes: Standaard Notification laden...");
		if(src == null || src.length < PulserNotif.generalInfoLength() + 13)return null;
		//System.out.println("    PulserNotifStandard.loadFromBytes: Voldoende informatie om mee te beginnen");
		
		int pos = PulserNotif.generalInfoLength();
		
		byte category = src[pos++];
		int interval = ((int)src[pos++] & 0xFF) << 24 | ((int)src[pos++] & 0xFF) << 16 | ((int)src[pos++] & 0xFF) << 8 | ((int)src[pos++] & 0xFF);
		int offset = ((int)src[pos++] & 0xFF) << 24 | ((int)src[pos++] & 0xFF) << 16 | ((int)src[pos++] & 0xFF) << 8 | ((int)src[pos++] & 0xFF);
		
		int techLength = ((int)src[pos++] & 0xFF) << 24 | ((int)src[pos++] & 0xFF) << 16 | ((int)src[pos++] & 0xFF) << 8 | ((int)src[pos++] & 0xFF);
		
		if((pos + techLength) != src.length){
			//System.out.println("    PulserNotifStandard.loadFromBytes: ERR Onjuiste lengte");
			return null;
		}
		
		//System.out.println("    PulserNotifStandard.loadFromBytes: Loading techs...");
		PNTech[] techs;
		{
			byte[][] a = StoreTechnics.loadArray(src, 1000, 20000, pos);
			techs = new PNTech[a.length];
			
			for(int i = 0; i < a.length; i++){
				techs[i] = PNTech.loadFromBytes(a[i]);
			}
			
		}
		//System.out.println("    PulserNotifStandard.loadFromBytes: Loaded techs");
		
		//System.out.println("    PulserNotifStandard.loadFromBytes: Constructing PulserNotifStandard...");
		PulserNotifStandard b = new PulserNotifStandard(src, techs, category, interval, offset);
		for(int i = 0; i < techs.length; i++){
			techs[i].notificationBase = b;
		}
		//System.out.println("    PulserNotifStandard.loadFromBytes: Standaard Notification geladen");
		return b;
	}

	@Override
	public boolean handleObjectCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args) throws Exception{
		if(super.handleObjectCommand(executor, a, attribSys, args))return true;
		
		if(executor.getSpelerOptions().getOpStatus() < 2){
			throw new Exception("§4Je hebt geen toegang tot dit commando");
		}
		if(args[0].equals("interval")){
			if(args.length == 1){
				a.sendMessage("§eDe interval is " + this.interval + " en de offset is " + this.offset);
			}else if(args.length == 2){
				this.checkDenyChanges();
				int newValue;
				try{
					newValue = Integer.parseInt(args[1]);
				}catch(Exception e){
					throw new Exception("Oncorrecte nieuwe waarde, de nieuwe waarde voor interval moet een nummer zijn");
				}
				if(newValue < 0){
					throw new Exception("De nieuwe interval moet minimum 0 zijn");
				}
				this.interval = newValue;
				if(this.offset >= this.interval)this.offset = 0;
				this.notifyChange();
				a.sendMessage("§eDe interval is nu " + this.interval + " en de offset is " + this.offset);
			}else if(args.length == 3){
				this.checkDenyChanges();
				int newInterval;
				try{
					newInterval = Integer.parseInt(args[1]);
				}catch(Exception e){
					throw new Exception("Oncorrecte waarde voor de nieuwe interval, de nieuwe waarde voor interval moet een nummer zijn");
				}
				if(newInterval < 0){
					throw new Exception("De nieuwe interval moet minimum 0 zijn");
				}
				
				int newOffset;
				try{
					newOffset = Integer.parseInt(args[2]);
				}catch(Exception e){
					throw new Exception("Oncorrecte waarde voor de nieuwe interval, de nieuwe waarde voor interval moet een nummer zijn");
				}
				if(newOffset < 0 || newOffset >= newInterval){
					throw new Exception("De nieuwe offset moet minimum 0 zijn en moet kleiner zijn dan de interval");
				}
				this.interval = newInterval;
				this.offset = newOffset;
				this.notifyChange();
				a.sendMessage("§eDe interval is nu " + this.interval + " en de offset is " + this.offset);
			}else{
				a.sendMessage("§eNotif-deel: §cinterval [nieuwe interval] [nieuwe offset]");
			}
		}else if(args[0].equals("technics")){
			if(args.length > 1){
				String subCmdLabel = args[1].toLowerCase();
				if(subCmdLabel.equals("list")){
					if(this.technics.length == 0){
						a.sendMessage("§4Er zijn geen technics beschikbaar");
						return true;
					}
					int page;
					if(args.length == 2){
						page = 0;
					}else if(args.length == 3){
						try{
							page = Integer.parseInt(args[2]);
						}catch(NumberFormatException e){
							a.sendMessage("§4Oncorrect nummer; \"" + args[2] + "\"");
							return true;
						}
					}else{
						a.sendMessage("§eNotif-deel: §ctechnics list [page]");
						return true;
					}
					if(page < 0){
						a.sendMessage("§4Paginanummer moet minimum 0 zijn");
					}else if(page == 0){
						a.sendMessage("§e---- Primary Technics ----");
						boolean found = false;
						for(int i = 0; i < this.technics.length; i++){
							PNTech t = this.technics[i];
							if(t != null && ((t == this.textProv) || (t == this.condition) || (t == this.datafield) || (t == this.notifSize) || (t == this.specAccess))){
								a.sendMessage("§9[#" + i + "]§2 " + this.technics[i].toString());
								found = true;
							}
						}
						if(!found){
							a.sendMessage("§eGeen Primary Technics aanwezig");
						}
						a.sendMessage("§e---- Alle technics: Gebruik Notif-deel: §ctechnics list 1§e ----");
					}else{
						int numberOfItemsPerPage = attribSys.getIntValue("itemsPerPage", 7);
						if(numberOfItemsPerPage < 1){
							a.sendMessage("§4Er moet minimum 1 item per pagina zijn");
							return true;
						}
						int maxPages = (int)Math.ceil((double)this.technics.length / numberOfItemsPerPage);
						if(page > maxPages){
							a.sendMessage("§4Paginanummer moet maximum " + maxPages + " zijn");
							return true;
						}
						int startIndex = attribSys.getIntValue("start", (page - 1) * numberOfItemsPerPage);
						if(startIndex >= this.technics.length){
							a.sendMessage("§4De startIndex is hoger dan de hoogste index");
							return true;
						}
						int endIndex = attribSys.getIntValue("stop", startIndex + numberOfItemsPerPage - 1);
						if(endIndex < startIndex){
							a.sendMessage("§4De endIndex kan niet kleiner zijn dan de startIndex");
							return true;
						}
						if(endIndex >= this.technics.length)endIndex = this.technics.length - 1;
						a.sendMessage("§e---- Technics (pagina " + page + ": §9#" + startIndex + "§e tot §9#" + endIndex + "§e) ----");
						for(int i = startIndex; i <= endIndex; i++){
							if(this.technics[i] == null){
								a.sendMessage("§7[#" + i + "] leeg");
							}else{
								boolean isInvisible = this.technics[i].isInvisible();
								boolean isPrimaryTechnic = false;
								if(!isInvisible){
									PNTech t = this.technics[i];
									isPrimaryTechnic = (t == this.textProv) || (t == this.condition) || (t == this.datafield) || (t == this.notifSize) || (t == this.specAccess);
								}
								a.sendMessage("§9[#" + i + "]" + (isInvisible?("§7"):(isPrimaryTechnic?"§2":"§a")) + " " + this.technics[i].toString());
							}
						}
						a.sendMessage("§e---- " + ((page == maxPages)?"Laatste pagina":("Bekijk volgende pagina met §eNotif-deel: §ctechnics list " + (page + 1) + ((numberOfItemsPerPage==7)?"":" ^itemsPerPage:" + numberOfItemsPerPage))) + " ----");
					}
				}else if(subCmdLabel.equals("add")){
					this.checkDenyChanges();
					if(args.length == 2){
						a.sendMessage("§eNotif-deel: §ctechnics add <mode> ...");
						a.sendMessage("§eMogelijke modes: create, copy");
					}else if(args.length >= 3){
						String modeSelection = args[2].toLowerCase();
						if(modeSelection.equals("create")){
							String[] creationParams = new String[args.length - 3];
							System.arraycopy(args, 3, creationParams, 0, creationParams.length);
							PNTech newObject;
							try{
								newObject = PNTech.createFromParams(creationParams, 600, this);
							}catch(Exception e){
								a.sendMessage("§4Fout bij het maken van de PNTech: " + e.getMessage());
								return true;
							}
							if(newObject == null){
								a.sendMessage("§4De nieuwe PNTech is leeg. Misschien is er iets fout gegaan in de creatie procedure?");
								return true;
							}
							
							if(this.technics == null || this.technics.length == 0){
								this.technics = new PNTech[1];
								this.technics[0] = newObject;
								a.sendMessage("§eDe nieuwe PNTech is de enige PNTech in deze PulserNotifStandard. Zijn index is dus §9#0§e.");
								this.notifyChange();
							}else{
								int index;
								for(index = 0; index < this.technics.length; index++){
									if(this.technics[index] == null){
										a.sendMessage("§eEen vrije plaats voor de nieuwe PNTech is gevonden op index §9#" + index + "§e.");
										break;
									}
								}
								if(index < this.technics.length){
									this.technics[index] = newObject;
									a.sendMessage("§eDe nieuwe PNTech is geplaatst op de index " + index + ".");
									this.notifyChange();
								}else{
									a.sendMessage("§eDe lengte van de PNTechs lijst wordt uitgebreid van " + this.technics.length + " naar " + (this.technics.length + 1) + " om plaats te voorzien voor de nieuwe PNTech.");
									PNTech[] newArray = new PNTech[this.technics.length + 1];
									System.arraycopy(this.technics, 0, newArray, 0, this.technics.length);
									newArray[newArray.length - 1] = newObject;
									this.technics = newArray;
									a.sendMessage("§eDe nieuwe PNTech is geplaatst op de index " + index + ".");
									this.notifyChange();
								}
							}
							this.recheckPrimaryTechnics();
						}else if(modeSelection.equals("copy")){
							if(args.length != 4){
								a.sendMessage("§eNotif-deel: §ctechnics add copy <copy van path>");
							}else{
								String path = args[3];
								IObjectCommandHandable objCH;
								try{
									objCH = Pulser.getObjectCommandHandable(Main.pulser, path);
								}catch(Exception e){
									a.sendMessage("§4Kon de van-PNTech niet vinden: " + e);
									return true;
								}
								if(objCH == null){
									a.sendMessage("§4Kon de van-PNTech niet vinden");
									return true;
								}
								if(!(objCH instanceof PNTech)){
									a.sendMessage("§4Je kan geen PNTech kopiëren van een niet-PNTech");
									return true;
								}
								PNTech newObject;
								try{
									newObject = ((PNTech)objCH).copyTech(600, this);
								}catch(Exception e){
									a.sendMessage("§4Kon geen kopie maken van het object: " + e);
									return true;
								}
								if(newObject == null){
									a.sendMessage("§4Het gegeven kopie is leeg... Misschien is er iets fout met de originele PNTech?");
									return true;
								}
								if(this.technics == null || this.technics.length == 0){
									this.technics = new PNTech[1];
									this.technics[0] = newObject;
									a.sendMessage("§eDe gekopiëerde PNTech is de enige PNTech in deze PulserNotifStandard. Zijn index is dus §9#0§e.");
									this.notifyChange();
								}else{
									int index;
									for(index = 0; index < this.technics.length; index++){
										if(this.technics[index] == null){
											a.sendMessage("§eEen vrije plaats voor de gekopiëerde PNTech is gevonden op index §9#" + index + "§e.");
											break;
										}
									}
									if(index < this.technics.length){
										this.technics[index] = newObject;
										a.sendMessage("§eEen kopie van de PNTech op path \"" + path + "\" is geplaatst op de index " + index + ".");
										this.notifyChange();
									}else{
										a.sendMessage("§eDe lengte van de PNTechs lijst wordt uitgebreid van " + this.technics.length + " naar " + (this.technics.length + 1) + " om plaats te voorzien voor de gekopiëerde PNTech.");
										PNTech[] newArray = new PNTech[this.technics.length + 1];
										System.arraycopy(this.technics, 0, newArray, 0, this.technics.length);
										newArray[newArray.length - 1] = newObject;
										this.technics = newArray;
										a.sendMessage("§eEen kopie van de PNTech op path \"" + path + "\" is geplaatst op de index " + (newArray.length - 1) + ".");
										this.notifyChange();
									}
								}
							}	
						}
						this.recheckPrimaryTechnics();
					}					
				}else if(subCmdLabel.equals("remove")){
					this.checkDenyChanges();
					if(args.length == 3){
						String selector = args[2];
						if(args[2].startsWith("#")){
							int index;
							try{
								index = Integer.parseInt(selector.substring(1));
							}catch(Exception e){
								a.sendMessage("§4Oncorrecte index: \"" + selector + "\"");
								return true;
							}
							if(index < 0 || index >= this.technics.length){
								a.sendMessage("§4De index moet minimum 0 en maximum " + (this.technics.length - 1) + " zijn.");
								return true;
							}
							this.technics[index] = null;
							a.sendMessage("§eDe PNTech op index §9#" + index + "§e is nu verwijderd.");
							this.notifyChange();
							this.recheckPrimaryTechnics();
						}else{
							a.sendMessage("§4Momenteel is enkel removen met indexes beschikbaar.");
						}
					}else{
						a.sendMessage("§eNotif-deel: §ctechnics remove #<index>");
					}
				}
			}else{
				a.sendMessage("§eNotif-deel: §ctechnics <list|add|remove>");
			}
		}else {
			return false;
		}
		return true;
	}
	
	@Override
	public ArrayList<String> autoCompleteObjectCommand(String[] args, ArrayList<String> a) throws Exception{
		a = super.autoCompleteObjectCommand(args, a);
		
		if(args == null || args.length == 0)return a;
		
		String cmdLabel = args[0].toLowerCase();	
		if(args.length == 1){
			if("interval".startsWith(cmdLabel))a.add("interval");
			if("technics".startsWith(cmdLabel))a.add("technics");	
		}else if(args.length >= 2){
			if(cmdLabel.equals("interval")){
				if(args.length == 2){
					if(args[1].length() == 0)a.add(String.valueOf(this.interval));
				}else if(args.length == 3){
					if(args[1].length() == 0)a.add(String.valueOf(this.offset));
				}
			}else if(cmdLabel.equals("technics")){
				String operationName = args[1].toLowerCase();
				if(args.length == 2){
					if("list".startsWith(operationName))a.add("list");
					if("add".startsWith(operationName))a.add("add");
					if("remove".startsWith(operationName))a.add("remove");
				}else{
					if(operationName.equals("list")){
						int currentIndex = 0;
						try{
							currentIndex = Integer.parseInt(args[2]);
						}catch(Exception e){}//Gewoon weer naar 0 gaan als geen nummer
						a.add(String.valueOf(currentIndex + 1));
					}else if(operationName.equals("add")){
						args[2] = args[2].toLowerCase();
						if(args.length == 3){
							if("copy".startsWith(args[2]))a.add("copy");
							if("create".startsWith(args[2]))a.add("create");
						}else{
							if(args[2].equals("copy")){
								if(args.length == 4){
									a.addAll(Main.autoCompletePath(args[3], Main.pulser));
								}
							}
						}
					}
				}
			}
		}
		return a;
	}

	@Override
	public String[] getLocalTopLevelArgsPossibilities() {
		return new String[]{"interval","technics"};
	}

	@Override
	public boolean activationRequiresCrashTest(){
		return (this.textProv == null)?false:this.textProv.crashTestRequired();
	}

	@Override
	public void doCrashTest(Player pl) throws Exception{
		if(this.textProv == null){
			pl.sendMessage("§4ERROR: De textProvider is null");
		}else{
			this.textProv.doCrashTest(pl);
		}
	}
	
	public final void checkPermission(IObjectCommandHandable sender, CommandSender a, Person p, byte accessLevel) throws Exception{
		//AccessLevel   127 = Geen toegang
		//AccessLevel   126 = Dev only
		//AccessLevel   125 = Console (& >125) only
		//AccessLevel   124 = Owner (& >124) only
		//AccessLevel   123 = Op (& >123) only
		//AccessLevel <=100 = Instelbaar met de PNTechSpecEditAccess
		
		if(a == null || p == null || accessLevel < 0){
			throw new Exception("Kon toegangsvalidatie uitvoeren omdat bepaalde parameters niet correct meegegeven waren");
		}
		if(accessLevel == 127){
			throw new Exception("Je hebt geen toegang tot die operatie");
		}
		if(accessLevel == 126){
			if(!(a instanceof Player) || !Main.isDeveloper(((Player)a).getUniqueId())){
				throw new Exception("Je hebt geen toegang tot die operatie (die operatie is enkel voor de Developer)");
			}
		}else if(accessLevel == 125 && p.getSpelerOptions().getOpStatus() < 3){
			throw new Exception("Je hebt geen toegang tot die operatie (die operatie is enkel voor de Console)");
		}else if(accessLevel == 124){
			if(p.getSpelerOptions().getRank() < 100){
				throw new Exception("Je hebt geen toegang tot die operatie (die operatie is enkel voor de Owner en de Console)");
			}
		}
		if(a.isOp() || p.getSpelerOptions().getOpStatus() >= 2)return;
		if(accessLevel <= 100 && (this.specAccess != null && !this.specAccess.isInvisible()) && a instanceof Player){
			if(this.specAccess.hasSpecAccess(((Player)a).getUniqueId(), accessLevel));
		}
		if(accessLevel <= 100){
			throw new Exception("Je hebt geen toegang tot die operatie (je moet minimum accessLevel " + accessLevel + " hebben bij de PulserNotification)");
		}else{
			throw new Exception("Je hebt geen toegang tot die operatie");
		}
	}
	/*public void executeGetCommand(CommandSender a, String[] args){
		if(args.length != 2){
			a.sendMessage("§c/pulser notification get <notification> <property>");
		}
		String s = this.getProperty(args[1]);
		if(s == null){
			a.sendMessage("Notification \"" + args[0] + "\" has no property \"" + args[1] + "\"");
		}else{
			a.sendMessage("Notification Property \"" + args[1] + "\" of \"" + args[0] + "\" is \"" + s + "\"");
		}
	}
	
	public void executeSetCommand(CommandSender a, String[] args){
		if(args.length != 2){
			a.sendMessage("§c/pulser notification set <notification> <property> <value>");
		}
		boolean b;
		try {
			b = this.setProperty(args[1], args[2]);
		} catch (Exception e) {
			a.sendMessage("§4Error by changing the property \"" + args[1] + "\" of \"" + args[0] + "\" to " + " \"" + args[2] + "\": " + e.getMessage());
			return;
		}
		if(b){
			a.sendMessage("Operation Executed");
		}else{
			a.sendMessage("§4Unknown Property");
		}
		String s = this.getProperty(args[1]);
		if(s == null){
			a.sendMessage("");
		}
		a.sendMessage("Notification Property \"" + args[1] + "\" of \"" + args[0] + "\" is now \"" + s + "\"");
	}
	
	protected String getProperty(String key){
		key = key.toLowerCase();
		if(key.equals("category")){
			String categoryname = "???";
			if(this.category == 0){
				categoryname = "Advertisement";
			}else if(this.category == 1){
				categoryname = "News";
			}
			return categoryname + " (" + this.category + ')';
		}else if(key.equals("executes")){
			return this.executes?"Executes":"Doesn't Execute";
		}else if(key.equals("frequency")){
			return this.interval + " with an offset of " + this.offset;
		}else if(key.equals("interval")){
			return Integer.toString(this.interval);
		}else if(key.equals("offset")){
			return Integer.toString(this.offset);
		}
		return null;
	}
	
	protected boolean setProperty(String key, String value) throws Exception {
		key = key.toLowerCase();
		value = value.toLowerCase();
		if(key.equals("category")){
			value = value.toLowerCase();
			if(value.equals("adv") || value.equals("advert") || value.equals("advertisement") || value.equals("0")){
				this.category = 0;
			}else if(value.equals("news") || value.equals("1")){
				this.category = 1;
			}else{
				throw new Exception("Mogelijke waarden kunnen zijn: adv|advert|advertisement|0 of news|1");
			}
			return true;
		}else if(key.equals("executes")){
			if(value.equals("true") || value.equals("ja") || value.equals("1")){
				this.executes = true;
			}else if(value.equals("false") || value.equals("uit") || value.equals("0")){
				this.executes = false;
			}else{
				throw new Exception("Mogelijke waarden kunnen zijn: true|ja|1 of false|uit|0");
			}
			return true;
		}else if(key.equals("frequency")){
			throw new Exception("Gebruik interval en offset om dit te veranderen");
		}else if(key.equals("interval")){
			int v;
			try{
				v = Integer.parseInt(value);
			}catch(NumberFormatException e){
				throw new Exception("De ingegeven nieuwe waarde is geen nummer");
			}
			if(v < 0){
				throw new Exception("De ingegeven nieuwe waarde moet groter of gelijk zijn aan 0");
			}
			this.interval = v;
			return true;
		}else if(key.equals("offset")){
			int v;
			try{
				v = Integer.parseInt(value);
			}catch(NumberFormatException e){
				throw new Exception("De ingegeven nieuwe waarde is geen nummer");
			}
			if(v < 0){
				throw new Exception("De ingegeven nieuwe waarde moet groter of gelijk zijn aan 0");
			}
			if(v >= interval){
				throw new Exception("De offset moet kleiner zijn dan de interval");
			}
			this.offset = v;
			return true;
		}
		return false;
	}
	
	protected final String[] getPossibleProperties(){
		String[] extra = this.getExtraPossibleProperties();
		//String[] message = this.nm.getPossibleProperties();
		
		String[] list = new String[5 + extra.length /*+ message.length*//*];
		list[0] = "category";
		list[1] = "active";
		list[2] = "frequency";
		list[3] = "interval";
		list[4] = "offset";
		System.arraycopy(extra, 0, list, 5, extra.length);
		//System.arraycopy(message, 0, list, 5 + extra.length, message.length);
		return list;
	}
	
	protected String[] getExtraPossibleProperties(){
		return new String[0];
	}
	
	public void executeInfoCommand(CommandSender a, String[] args){
		
	}
	
	public void executeActivationCommand(CommandSender a, String[] args){
		
	}*/

	@Override
	public String toString(){
		return "PulserNotifStandard[super=\"" + super.toString() + "\",interval=" + this.interval + ",offset=" + this.offset + "]";
	}

	@Override
	public IObjectCommandHandable getSubObjectCH(String path) throws Exception{
		path = path.toLowerCase();
		int pointIndex = path.indexOf((int)'.');
		if(pointIndex == -1)pointIndex = path.length();
		String domain = path.substring(0, pointIndex);
		boolean itemPresent = pointIndex < path.length() - 1;
		String item = itemPresent?(path.substring(pointIndex + 1, path.length())):"";
		if(domain.equals("techs") || domain.equals("technics")){
			if(!itemPresent){
				return new VirtualSubObject(this, true, new String[]{"technics"}, "technics.");
			} else {
				item = item.toLowerCase();
				if(item.startsWith("#")){
					int techIndex;
					try{
						techIndex = Integer.parseInt(item.substring(1));
					}catch(NumberFormatException e){
						throw new Exception("De # moet gevolgd worden door een correcte indexnummer.");
					}
					if(techIndex < 0 || techIndex >= this.technics.length)throw new Exception("Oncorrecte index voor een item in de array: #" + techIndex + ". De index moet minimum 0 en maximum " + (this.technics.length - 1) + " zijn.");
					return this.technics[techIndex];
				}else if(item.equals("textprov")){
					return this.textProv;
				}else if(item.equals("condition")){
					return this.condition;
				}else if(item.equals("datafield")){
					return this.datafield;
				}else if(item.equals("notifsize")){
					return this.notifSize;
				}else if(item.equals("speceditaccess")){
					return this.specAccess;
				}else{
					throw new Exception("Onbekend SubObject van de \"technics\"/\"techs\": \"" + item + "\"");
				}
			}
		}
		return null;
	}

	@Override
	public ArrayList<String> autoCompleteSubObjectCH(String s, ArrayList<String> a) throws Exception {
		s = s.toLowerCase();
		if("technics".startsWith(s))a.add("technics");
		if(s.startsWith("technics")){
			if("technics.#".startsWith(s))a.add("technics.#");
			if("technics.textprov".startsWith(s))a.add("technics.textProv");
			if("technics.condition".startsWith(s))a.add("technics.condition");
			if("technics.datafield".startsWith(s))a.add("technics.datafield");
			if("technics.notifSize".startsWith(s))a.add("technics.notifSize");
			if("technics.speceditaccess".startsWith(s))a.add("technics.specEditAccess");
		}
		return a;
	}
	
}
