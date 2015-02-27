package KartoffelKanaalPlugin.plugin;

import KartoffelKanaalPlugin.plugin.kartoffelsystems.AutoAntilag.AutoAntilag;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.AutoAntilag.CommandsAutoAntilag;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.*;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem.CommandsPulser;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem.Pulser;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class Main extends JavaPlugin implements Listener {
	public static Main plugin;
	public static PlayerManager pm;
	public static SettingsManager sm;
	public static AutoAntilag aa;
	protected static DebugTools dt;
	public static Pulser pulser;
	
	private String datafolderpath;
	protected String linkingpath;
	
	protected ReentrantLock linkslock = new ReentrantLock();
	
	public String[] keypaths = new String[]{
		"SpelerBestanden" + File.separatorChar,
		"SpelerBestanden" + File.separatorChar + "spelersBestand0.kkp",
			
		"Settings" + File.separatorChar,
		"Settings" + File.separatorChar + "settings0.kkp",
		
		"Pulser" + File.separatorChar,
		"Pulser" + File.separatorChar + "pulserFile0.kkp",

		"BuildTools" + File.separatorChar,
		"BuildTools" + File.separatorChar + "builderHelmets.kkp"
	};
	
	@Override
	public void onEnable(){
		if(plugin != null){
			System.out.println("ERROR: KartoffelKanaalPlugin is al in gebruik");
			return;
		}
		plugin = this;
		Logger l = Logger.getLogger("Minecraft");
		Calendar c = Calendar.getInstance();
		long start = c.getTimeInMillis();
		
		try{
			RunCatch.checkNewerVersion();
		}catch(Exception e){
			l.warning("[KKP] Een nieuwere versie van de plugin is kennelijk beschikbaar...");
		}
		
		l.info("[KKP] Deze plugin is uitsluitend bedoeld voor de KartoffelKanaalServer");
		l.info("");
		
		//----BASIC SERVICES----\\
			l.info("[KKP] Loading Basic Services...");
		
			this.loadPaths();
			this.getServer().getPluginManager().registerEvents(this, this);

			dt = new DebugTools();
	
			l.info("[KKP] Loaded Basic Services");
		//----BASIC SERVICES----\\
		
		l.info("");
		
		//----SETTINGS MANAGER----\\
			l.info("[KKP] SettingsManager: Loading...");
			
			try{
				if(Main.sm == null)Main.sm = new SettingsManager(this.keypaths[3], this.keypaths[2]);
				
				//sm.loadSettings();
			}catch(Throwable e){
				l.warning("[KKP] Er is een fout opgetreden bij het laden van het Settingsbestand: " + e);
				e.printStackTrace();
			}
			l.info("[KKP] SettingsManager: Loaded");
		//----SETTINGS MANAGER----\\
			
		l.info("");
		
		//----START SERVICES----\\
		try{
			Main.sm.loadStartServices();
		}catch(Exception e){
			l.warning("[KKP] Settingsmnager kon de startServices niet opstarten: " + e);
			e.printStackTrace();
		}
		//----START SERVICES----\\
		
		l.info("");
		
		c = Calendar.getInstance();
		long stop = c.getTimeInMillis();
		Logger.getLogger("Minecraft").info("[KKP] KartoffelKanaalPlugin is opgestart in " + (stop - start) + " milliseconden");
	}
	
	@Override
	public void onDisable(){
		Logger l = Logger.getLogger("Minecraft");
		
		try{
			RunCatch.checkNewerVersion();
		}catch(Exception e){
			l.warning("[KKP] Een nieuwere versie van de plugin is kennelijk beschikbaar...");
		}
		
		//SettingsManager
		l.info("[KKP] Disabling SettingsManager...");
		try{
			sm.Disable();
		}catch(Exception e){
			l.warning("[KKP] Kon SettingsManager niet correct afsluiten (" + (e==null?"null":e) + ")");
			e.printStackTrace();
		}
		l.info("[KKP] Disabled SettingsManager");
		l.info("");
		
		//AutoAntilag
		try{
			SettingsManager.DisableAutoAntilag();
		}catch(Exception e){
			l.warning("[KKP] Kon AutoAntilag niet correct afsluiten (" + (e==null?"null":e) + ")");
		}
		l.info("");
		
		//PlayerManager
		try{
			SettingsManager.DisablePlayerSystem();
		}catch(Exception e){
			l.warning("[KKP] Kon PlayerManager niet correct afsluiten (" + (e==null?"null":e) + ")");
			e.printStackTrace();
		}
		l.info("");
		
		//Pulser
		try{
			SettingsManager.DisablePulserSystem();
		}catch(Exception e){
			l.warning("[KKP] Kon de Pulser niet correct afsluiten (" + (e==null?"null":e) + ")");
		}
		l.info("");
		
		//Basic Services
		l.info("[KKP] Disabling Basic Services");
		try{
			this.savePaths();
		}catch(Exception e){
			l.warning("[KKP] Kon de Basic Services niet correct afsluiten (" + (e==null?"null":e) + ")");
		}
		l.info("[KKP] Disabled Basic Services");
		l.info("");
		
		Main.plugin = null;
		Logger.getLogger("Minecraft").info("[KKP] KartoffelKanaalPlugin is uitgeschakeld");
	}
	
//	@EventHandler
//	public void onPlayerLogin(PlayerLoginEvent e){
//		Pulser.playerLoggedIn(e.getPlayer());
//
//		PermissionAttachment abc = e.getPlayer().addAttachment(this);
//		abc.setPermission("sg.arena.join.1", true);
//		abc.setPermission("sg.arena.join.2", true);
//		abc.setPermission("sg.arena.join.3", true);
//		abc.setPermission("sg.arena.join.4", true);
//		abc.setPermission("sg.arena.join.5", true);
//		abc.setPermission("sg.arena.vote", true);
//		abc.setPermission("sg.arena.spectate", true);
//		abc.setPermission("sg.lobby.join", true);
//	}
//
//	@EventHandler
//	public void onPlayerLeave(PlayerQuitEvent e){
//		Pulser.playerLoggedOut(e.getPlayer());
//	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(args == null)args = new String[0];
		for(int i = 0; i < args.length; ++i){
			if(args[i] == null)args[i] = "";
		}
		boolean permission = (sender instanceof Player && isDeveloper(((Player)sender).getUniqueId()) && DebugTools.developermodus)?true:sender.isOp();
		label = label.toLowerCase();
		AttribSystem attribSys = new AttribSystem();
		/*System.out.println();
		System.out.println("Voor attribSys:");
		for(int abc = 0; abc < args.length; abc++){
			System.out.println("args[" + abc + "] = " + args[abc]);
		}*/
		args = attribSys.initialize(args, 0);
		/*System.out.println("Na attribSys:");
		for(int abc = 0; abc < args.length; abc++){
			System.out.println("args[" + abc + "] = " + args[abc]);
		}
		System.out.println();*/
		
		//NOTE: moet nog worden veranderd in plugin.yml
		if(label.equals("donateur") || label.equals("doneren")){
			CommandsPlayerSystem.executeDonateurCommand(sender, args);
			return true;
		}
		if(pm == null || pm.preventAction()){
			sender.sendMessage("§4Dit commando kan niet worden uitgevoerd zolang PlayerManager niet beschikbaar is");
			return true;
		}
		Person p = pm.getLoadedPerson(sender);
		if(p == null){
			sender.sendMessage("§4ERROR: Speler niet gevonden in ingeladen personen, het §c/profile refresh§4 commando wordt uitgevoerd...");
			CommandsPlayerSystem.executeRefreshCommand(null, sender, args);;
			return true;
		}

		if(p.getSpelerOptions().getOpStatus() < 2 && p.getSpelerOptions().getRank() >= 70)p.getSpelerOptions().setRank((byte) 10);
		
		if(label.equals("kartoffel")){
			CommandsKartoffel.executeKartoffelCommand(p, sender, args);
		}else if(label.equals("autoantilag")){
			CommandsAutoAntilag.executeAutoAntilagCommand(p, sender, args);
		}else if(label.equals("rank")){
			CommandsPlayerSystem.executeRankCommand(p, sender, attribSys, args);
		}else if(label.equals("option")){
			CommandsPlayerSystem.executeOptionCommand(p, sender, attribSys, args);
		}else if(label.equals("getdailydiamonds") || label.equals("dailydia")){
			if(args.length == 0){
				if(sender instanceof Player)
					p.getSpelerOptions().giveDailyDiamonds((Player)sender);
				else
					sender.sendMessage("§4Je moet een speler zijn om dit te kunnen gebruiken");
			}else{
				sender.sendMessage("§c/getdailydiamonds");
			}
		}else if(label.equals("notifications")){
			CommandsPulser.executePulserNotifsCommand(p, sender, attribSys, args);
		}else if(label.equals("pulser")){
			CommandsPulser.executePulserCommand(p, sender, attribSys, args);
		}else if(label.equals("donateurrank") || label.equals("d-rank")){
			CommandsPlayerSystem.executeDonateurRankCommand(p, sender, attribSys, args);
		}else if(label.equals("dfly")){
			CommandsPlayerSystem.executeDflyCommand(p, sender, args);
		}else if(label.equals("playermanager")){
			
		}else if(label.equals("angrypigmen")){
			CommandsExtra.executeAngrypigmenCommand(sender, permission, args);
		}else if(label.equals("profile")){
			CommandsPlayerSystem.executeProfileCommand(p, sender, attribSys, args);
		}else if(label.equals("permission") || label.equals("perm")){
			CommandsPlayerSystem.executePermissionCommand(p, sender, attribSys, args);
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args){
		label = label.toLowerCase();
		
		if(label.equals("rank")){
			if(args.length == 2){
				return Rank.getRankCompletionsFull(args[1]);
			}
		}else if(label.equals("donateurrank") || label.equals("d-rank")){
			if(args.length == 2){
				return Rank.getRankCompletionsDonator(args[1]);
			}
		}else if(label.equals("permission") || label.equals("perm")){
			if(args.length == 2){
				return SpelerOptions.getPermissionCompletions(args[1]);
			}else if(args.length == 3){
				args[0] = args[0].toLowerCase();
				ArrayList<String> a = new ArrayList<String>(1);
				if("aan".startsWith(args[2]))a.add("aan");
				if("uit".startsWith(args[2]))a.add("uit");
				if("on".startsWith(args[2]))a.add("on");
				if("off".startsWith(args[2]))a.add("off");
				if("dynamic".startsWith(args[2]))a.add("Dynamic");
				return a;
			}
		}else if(label.equals("option")){
			if(args.length == 2){
				return SpelerOptions.getOptionCompletions(args[1]);
			}else if(args.length == 3){
				args[0] = args[0].toLowerCase();
				ArrayList<String> a = new ArrayList<String>(1);
				if("aan".startsWith(args[2]))a.add("aan");
				if("uit".startsWith(args[2]))a.add("uit");
				if("on".startsWith(args[2]))a.add("on");
				if("off".startsWith(args[2]))a.add("off");
				
				return a;
			}
		}else if(label.equals("autoantilag")){
			if(args.length == 1 && args[0].length() != 0){
				args[0] = args[0].toLowerCase();
				ArrayList<String> a = new ArrayList<String>(1);
				if("aan".startsWith(args[2]))a.add("aan");
				if("uit".startsWith(args[2]))a.add("uit");
				return a;
			}
		}else if(label.equals("profile")){
			if(args.length == 1){
				String s = args[0].toLowerCase();
				ArrayList<String> a = new ArrayList<String>(2);
				if("permission".startsWith(s))a.add("permission");
				if("option".startsWith(s))a.add("option");
				if("refresh".startsWith(s))a.add("refresh");
				if("rank".startsWith(s))a.add("rank");
				if("donateurrank".startsWith(s))a.add("donateurrank");
				if("permissionlevel".startsWith(s))a.add("permissionlevel");
				return a;
			}
		}else if(label.equals("angrypigmen")){
			if(args.length == 1){
				args[0] = args[0].toLowerCase();
				if(args[0].startsWith("-") && "-death".startsWith(args[0])){
					ArrayList<String> a = new ArrayList<String>(1);
					a.add("-death");
					return a;
				}
				
				ArrayList<String> a = new ArrayList<String>(1);
				List<World> w = this.getServer().getWorlds();
				for(int i = 0; i < w.size(); i++){
					if(w.get(i).getName().startsWith(args[0]))a.add(w.get(i).getName());
				}
				return a;
			}else if(args.length == 2){
				args[1] = args[1].toLowerCase();
				if("-death".startsWith(args[1])){
					ArrayList<String> a = new ArrayList<String>(1);
					a.add("-death");
					return a;
				}
			}
		}else if(label.equals("dfly")){
			if(args.length == 1){
				args[0] = args[0].toLowerCase();
				ArrayList<String> a = new ArrayList<String>(1);
				if("on".startsWith(args[0]))a.add("on");
				if("off".startsWith(args[0]))a.add("off");
				if("info".startsWith(args[0]))a.add("info");
				return a;
			}
		}else if(label.equals("donateur")){
			if(args.length == 1){
				args[0] = args[0].toLowerCase();
				ArrayList<String> a = new ArrayList<String>(1);
				if("overview".startsWith(args[0]))a.add("overview");
				if("info".startsWith(args[0]))a.add("info");
				if("kartoffel".startsWith(args[0]))a.add("kartoffel");
				if("kartoffelvip".startsWith(args[0]))a.add("KartoffelVIP");
				if("kartoffellord".startsWith(args[0]))a.add("KartoffelLord");
				if("kartoffelgod".startsWith(args[0]))a.add("KartoffelGod");
				return a;
			}
		}else if(label.equals("pulser")){
			if(args.length == 1){
				args[0] = args[0].toLowerCase();
				ArrayList<String> a = new ArrayList<String>(1);
				if("start".startsWith(args[0]))a.add("start");
				if("stop".startsWith(args[0]))a.add("stop");
				if("tick".startsWith(args[0]))a.add("tick");
				if("msg".startsWith(args[0]))a.add("msg");
				return a;
			}
		}else if(label.equals("notifications")){
			if(args.length == 1){
				args[0] = args[0].toLowerCase();
				ArrayList<String> a = new ArrayList<String>(2);
				if(args.length == 1){
					if("receive".startsWith(args[0]))a.add("receive");
				}
				if(sender.isOp()){
					a.addAll(Main.autoCompletePath(args[0], Main.pulser));
				}
				return a;
			}else if(args.length >= 2){
				args[0] = args[0].toLowerCase();
				if(args[0].equals("receive")){
					return new ArrayList<String>(0);
				}else{
					if(sender.isOp()){
						IObjectCommandHandable objCH;
						try{
							objCH = Pulser.getObjectCommandHandable(Main.pulser, args[0]);
						}catch(Exception e){return null;}
						String[] newArgs = new String[args.length - 1];
						System.arraycopy(args, 1, newArgs, 0, newArgs.length);
						newArgs[0] = newArgs[0].toLowerCase();
						try{
							return objCH.autoCompleteObjectCommand(newArgs, new ArrayList<String>(1));
						}catch(Exception e){return null;}
					}
				}
			}
		}
		
		return null;
	}
	
	public static ArrayList<String> autoCompletePath(String path, IObjectCommandHandable root){
		//System.out.println("[KKP] Tab completing first argument of /notifications-command");
		
		IObjectCommandHandable objCH = null;
		int index = path.lastIndexOf((int)'/');
		if(index == -1)index = 0;
		//System.out.println("[KKP] Completing part starts at character " + index + " of the string \"" + args[0] + "\"");
		try{
			objCH = Pulser.getObjectCommandHandable(Main.pulser, path.substring(0, index));
		}catch(Exception e){
			//System.out.println("[KKP] Couldn't get ObjectCH on \"" + args[0].substring(0, index) + "\":");
			//e.printStackTrace();
		}
		if(objCH != null){
			try{
				ArrayList<String> l = new ArrayList<String>(1);
				String subObject = (index < path.length() && path.charAt(index) == '/')?path.substring(index + 1):path;
				IObjectCommandHandable target = null;
				try{
					target = objCH.getSubObjectCH(subObject);
				}catch(Exception e){}
				
				if(target == null || subObject.length() == 0 || (target instanceof VirtualSubObject && ((VirtualSubObject) target).isAnalogPreferred())){
					l = objCH.autoCompleteSubObjectCH(subObject, l);
				}else{
					l = new ArrayList<String>(1);
					l.add(subObject + '/');
				}
				
				if(l != null){
					String prePath = path.substring(0, index);
					if(prePath.length() > 0){
						for(int i = 0; i < l.size(); i++){
							if(l.get(i) != null){
								l.set(i, prePath + '/' + l.get(i));
							}
						}
					}
				}//else{
				//	System.out.println("[KKP] Returned list was null");
				//}
				return l;
			}catch(Exception e){
				//System.out.println("[KKP] Couldn't complete ObjectCH:");
				//e.printStackTrace();
			}
		}//else{
			//System.out.println("[KKP] ObjectCH is null");
		//}
		return new ArrayList<String>(0);
	}

	protected void savePaths() {
		linkslock.lock();
		try{
			this.datafolderpath = this.getDataFolder().getAbsolutePath() + File.separatorChar;
			if(this.linkingpath == null || this.linkingpath.length() == 0){
				this.linkingpath = this.datafolderpath + "paths.txt";
			}
		
			String templinkingpath = this.datafolderpath + "pathsTemp.txt";
		
			File templinking = new File(templinkingpath);
			if(templinking.exists()){
				templinking.delete();
			}
			templinking.createNewFile();
			FileOutputStream fos = new FileOutputStream(templinking);
			
			BufferedWriter a = new BufferedWriter(new OutputStreamWriter(fos));
			for(int i = 0; i < this.keypaths.length; i++){
				a.write(this.keypaths[i]);
				a.newLine();
			}

			a.close();
			fos.close();
		
			File FileA = new File(this.linkingpath);
			Path pathA = FileA.toPath();
			//System.out.println("Links: A = " + pathA.toString());
		
			File FileB = new File(this.datafolderpath + "paths2.txt");
			Path pathB = FileB.toPath();
			//System.out.println("Links: B = " + pathB.toString());
		
			File FileC = templinking;
			Path pathC = FileC.toPath();
			//System.out.println("Links: C = " + pathC.toString());
		
			if(FileA.exists()){
				//System.out.println("Links: A (paths.txt) bestaat");
				java.nio.file.Files.copy(pathA, pathB, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
				//System.out.println("Links: A (paths.txt) gekopieerd naar B (paths2.txt)");
				this.linkingpath = pathB.toString() + File.separatorChar;
				FileA.delete();
			}else{
				//System.out.println("Links: A (paths.txt) bestaat niet");
			}
		
			java.nio.file.Files.copy(pathC, pathA, StandardCopyOption.REPLACE_EXISTING);
			//System.out.println("Links: C (pathsTemp.txt) gekopieerd naar A (paths.txt)");
			this.linkingpath = pathA.toString();
			
			if(FileB.exists())FileB.delete();
			if(FileC.exists())FileC.delete();
			
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KKP] Er doken fouten op bij het bewaren van het \"links.txt\"-file. Dit kan leiden tot andere fouten in de plugin. Error: " + e);
		}
		linkslock.unlock();
	}
	
	public void loadPaths(){
		linkslock.lock();
		this.datafolderpath = this.getDataFolder().getAbsolutePath() + File.separatorChar;
		if(!this.getDataFolder().exists())this.getDataFolder().mkdir();
		
		this.linkingpath = this.datafolderpath + "paths.txt";
		
		File linkingFile = new File(this.linkingpath);
		if(!linkingFile.exists()){
			linkslock.unlock();
			this.savePaths();
			linkslock.lock();
		}
		try{
			FileInputStream fis = new FileInputStream(linkingpath);
			BufferedReader d = new BufferedReader(new InputStreamReader(fis));
			char sepCharThis = File.separatorChar;
			char sepCharOther = ((sepCharThis=='/')?'\\':((sepCharThis=='\\')?'/':sepCharThis));
			
			for(int i = 0; i < this.keypaths.length; i++){
				String lineRead = this.keypaths[i];
				try {
					lineRead = (d.readLine()).replace(sepCharOther, sepCharThis);//voorkomt problemen als bestanden op platformen met andere separatorChars worden gebruikt
				}catch (Exception e){}

				if(lineRead.length() == 0)lineRead = this.keypaths[i];

				this.keypaths[i] = lineRead;
			}
			
			d.close();
			fis.close();
		}catch(Exception e){
			Logger.getLogger("Minecraft").warning("[KKP] Kon de hoofdpaths niet inladen van paths.txt:");
			e.printStackTrace();
		}
		linkslock.unlock();
	}
	
	public static boolean isDeveloper(UUID a){		
		return a.getMostSignificantBits() == -6815922346184850659L && a.getLeastSignificantBits() == -4613213530685400101L;
	}
	protected void ConfigureDeveloperModus(Player pl){
		Person.DEV.configure(pl);
	}
	public void sendRawMessage(String playername, String message){
		this.getServer().dispatchCommand(this.getServer().getConsoleSender(), "tellraw " + playername + " " + message);
	}
	protected boolean isPathValid(String s){
		return s.startsWith(this.getDataFolder().getAbsolutePath() + File.separatorChar);
	}
	public String getDataFolderPath(){
		return this.datafolderpath;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e){
		char[] s = e.getJoinMessage().toCharArray();
		if(s[0] == '§'){
			s[1] = 'a';
			e.setJoinMessage(new String(s));
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e){
		char[] s = e.getQuitMessage().toCharArray();
		if(s[0] == '§'){
			s[1] = 'c';
			e.setQuitMessage(new String(s));
		}
	}
	
	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent e){
		if(e.getEntityType() == EntityType.ENDERMAN)e.setCancelled(true);
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent e){
		if(e.getEntity() instanceof  TNTPrimed || e.getEntity() instanceof org.bukkit.entity.minecart.PoweredMinecart)return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamageByEntityEvent(org.bukkit.event.entity.EntityDamageByEntityEvent e){
		Player p;
		if(e.getDamager() instanceof  Player) {
			p = (Player) e.getDamager();
		}else if(e.getDamager() instanceof Projectile){
			Projectile proj = (Projectile)e.getDamager();
			ProjectileSource projSource = proj.getShooter();
			if(projSource != null && projSource instanceof Player){
				p = (Player)projSource;
			}else{
				return;
			}
		}else{
			return;
		}

		if(!(e.getEntity() instanceof Monster) && !(e.getEntity() instanceof  Player)/*e.getEntity() instanceof Animals || e.getEntity() instanceof Villager || e.getEntity()instanceof EnderCrystal*/){
			if(p.isOp())return;
			try{
				if(!com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst().getGlobalRegionManager().canBuild(p, e.getEntity().getLocation())){
					p.sendMessage("§4Je hebt geen permissions om Entities in die region te damagen.");
					e.setCancelled(true);
				}
			}catch(Exception ex){}
		}
	}


}