package KartoffelKanaalPlugin.plugin;

import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public interface IObjectCommandHandable {
	boolean handleObjectCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args) throws Exception;
	ArrayList<String> autoCompleteObjectCommand(String[] args, ArrayList<String> a) throws Exception;
			
	IObjectCommandHandable getSubObjectCH(String path) throws Exception;
	ArrayList<String> autoCompleteSubObjectCH(String s, ArrayList<String> a) throws Exception;
}
