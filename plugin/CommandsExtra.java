package KartoffelKanaalPlugin.plugin;

import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;

import java.util.Collection;

public class CommandsExtra {
	protected static void executeAngrypigmenCommand(CommandSender sender, boolean permission, String[] args){
		if(permission){
			boolean death = false;
			World w = (sender instanceof Player)?((Player)sender).getWorld():((sender instanceof BlockCommandSender)?(((BlockCommandSender)sender).getBlock().getWorld()):null);
		
			if(args.length == 1){
				if(args[0].equals("-death")){
					death = true;
				}else{
					w = Main.plugin.getServer().getWorld(args[0]);
					if(w == null){
						sender.sendMessage("Onbekende wereld: §e\"§f" + args[0] + "§e\"");
						return;
					}
				}
			}else if(args.length == 2){
				w = Main.plugin.getServer().getWorld(args[0]);
				if(!args[1].equals("-death")){
					sender.sendMessage("§c/angrypigmen [world] [-death]");
					return;
				}
				death = true;
				if(w == null){
					sender.sendMessage("Onbekende wereld: §e\"§f" + args[0] + "§e\"");
					return;
				}
			}
			if(w == null)sender.sendMessage("§4Je dient een wereld in te voeren");
			int total = 0;
			Collection<PigZombie> a = w.getEntitiesByClass(PigZombie.class);
			PigZombie[] pz = new PigZombie[a.size()];
			a.toArray(pz);
			for(int i = 0; i < pz.length; i++){
				if(pz[i] != null && pz[i].isAngry()){
					++total;
					if(death){
						pz[i].damage(100.0);
					}else{
						pz[i].setAngry(false);
						pz[i].setTarget(null);
					}
				}
			}
			if(death){
				sender.sendMessage("Er zijn " + total + " boze Zombie Pigmen gedood");
			}else{
				sender.sendMessage("Er zijn " + total + " Zombie Pigmen gekalmeerd");
			}
			return;
		}else{
			sender.sendMessage("§4Je hebt geen toegang tot dit commando");
		}
	}
}
