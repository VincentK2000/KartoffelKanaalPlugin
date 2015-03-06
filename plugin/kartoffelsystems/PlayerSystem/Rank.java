package KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem;

import KartoffelKanaalPlugin.plugin.Main;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.ArrayList;
import java.util.List;

public class Rank {
	public static final byte[] ladder = new byte[]{-127, 0, 1, 5, 10, 20, 25, 30, 35, 70, 100};
	
	public static byte getRank(String rankname){
		rankname = rankname.toLowerCase();
		for(int i = 0; i < ladder.length; i++){
			if(getRankName(ladder[i]).toLowerCase().equals(rankname)){
				return ladder[i];
			}
		}
		
		try{
			return Byte.parseByte(rankname);
		}catch(Exception ex){}
		return -128;
	}
	public static String getRankName(byte r){
		if(r >= 100)return "Owner";
		if(r >=  70)return "Admin";
		
		if(r >=  35)return "KartoffelGod";
		if(r >=  30)return "KartoffelLord";
		if(r >=  25)return "KartoffelVIP";
		if(r >=  20)return "Kartoffel";
		
		if(r >=  10)return "Veteran";
		if(r >=   5)return "Builder";
		
		if(r >=   1)return "Player";
		if(r ==   0)return "Unset";
		if(r == -127)return "Geen";
		if(r == -128)return "Internal:Unknown";
		return "Onbekend:" + r;
	}
	public static String getRankPrefix(byte r){
		if(r >= 100)return "§b[Owner]";
		if(r >=  70)return "§4[Admin]";
		
		if(r >=  35)return "§9[God]";
		if(r >=  30)return "§6[Lord]";
		if(r >=  25)return "§2[VIP]";
		if(r >=  20)return "§d[Kartoffel]";
		
		if(r >=  10)return "§e[Veteran]";
		if(r >=   5)return "§7[Builder]";
		return "";
	}
	public static char getRankColor(byte r){
		if(r >= 100)return 'b';
		if(r >=  70)return '4';
		
		if(r >=  35)return '9';
		if(r >=  30)return '6';
		if(r >=  25)return '2';
		if(r >=  20)return 'd';
		
		if(r >=  10)return 'e';
		if(r >=   5)return '7';
		return 'f';
	}
	public static String getRankDisplay(byte r){
		return "\"" + getRankName(r) + "\" (" + r + ")";
	}

	public static void setBukkitPermissions(byte r, Person p){
		if(p == null || p.player == null)return;
		try{
			p.player.removeAttachment(p.pa);
		}catch(Exception e){
			//System.out.println("WARNING van KartoffelKanaalPlugin: Bij het toepassen van ranks op \"" + p.toString() + "\", konder de oude permissions niet worden verwijderd, als het blijkt dat de persoon permissions bezit die het niet zou mogen krijgen op de rank " + Rank.getDisplayRankName(p.getSpelerOptions().getRank()) + " is het het best om de persoon te forceren te reloggen");
		}
		p.pa = applyBukkitPermissionsOnPlayer(r, p, p.player);
	}
	public static PermissionAttachment applyBukkitPermissionsOnPlayer(byte r, Person p, Player pl){
		PermissionAttachment a = pl.addAttachment(Main.plugin);
		addRankPermissionsForPA(r, a);
		addOtherPermissionForPA(p, a);
		pl.recalculatePermissions();
		return a;
	}
	public static List<String> getRankCompletionsFull(String in){
		in = in.toLowerCase();
		ArrayList<String> ans = new ArrayList<String>();
		for(int i = 1/*"Geen" telt niet echt mee*/; i < ladder.length; i++){
			if(getRankName(ladder[i]).toLowerCase().startsWith(in))ans.add(getRankName(ladder[i]));
		}
		return ans;
	}
	public static List<String> getRankCompletionsDonator(String in){
		in = in.toLowerCase();
		ArrayList<String> ans = new ArrayList<String>();
		for(int i = 5; i < 9; i++){
			if(getRankName(ladder[i]).toLowerCase().startsWith(in))ans.add(getRankName(ladder[i]));
		}
		//if("geen".startsWith(in))ans.add("Geen");
		return ans;
	}
	
	private static void addOtherPermissionForPA(Person p, PermissionAttachment a){
		SpelerOptions so = p.getSpelerOptions();
		if(so.getSwitch((byte) 0x48, false)){
			a.setPermission("essentials.protect.exemptusage", true);
			a.setPermission("essentials.protect.exemptplacement", true);
		}
	}
	
	private static void addRankPermissionsForPA(byte r, PermissionAttachment a){
		if(r < 1)return; //Player
			a.setPermission("sg.arena.join.*", true);
			a.setPermission("sg.arena.vote", true);
			a.setPermission("sg.arena.spectate", true);
			a.setPermission("sg.lobby.join", true);

		if(r < 5)return; //Builder
			a.setPermission("essentials.sethome.multiple", true);
			a.setPermission("essentials.sethome.multiple.twohomes", true);

		if(r < 10)return; //Veteran
			a.setPermission("essentials.hat", true);
			a.setPermission("essentials.sethome.multiple.threehomes", true);
            a.setPermission("nocheatplus.checks.moving", true);

		if(r < 20)return; //Kartoffel
			a.setPermission("essentials.workbench", true);
			a.setPermission("essentials.teleport.timer.bypass", true);
			a.setPermission("essentials.teleport.timer.move", true);

		if(r < 25)return; //KartoffelVIP
			a.setPermission("essentials.nick", true);
			a.setPermission("essentials.nick.color",true);
			a.setPermission("essentials.heal", true);
			a.setPermission("essentials.sethome.multiple.fivehomes", true);

		if(r < 30)return; //KartoffelLord
			a.setPermission("essentials.top", true);
			a.setPermission("essentials.sethome.multiple.sevenhomes", true);

		if(r < 35)return; //KartoffelGod
			a.setPermission("essentials.weather", true);
			a.setPermission("bukkit.command.weather", true);
			a.setPermission("bukkit.command.toggledownfall",true);
			a.setPermission("essentials.time.set", true);
			a.setPermission("bukkit.command.time.add", true);
			a.setPermission("bukkit.command.time.set", true);
			a.setPermission("essentials.enderchest", true);
			a.setPermission("essentials.sethome.multiple.tenhomes", true);
            a.setPermission("essentials.speed", true);
            a.setPermission("essentials.speed.fly", true);
	}
}
