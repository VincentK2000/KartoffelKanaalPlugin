package KartoffelKanaalPlugin.plugin;

import java.util.List;

import org.bukkit.command.CommandSender;

import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;

public interface IObjectCommandHandable {
	boolean handleObjectCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args) throws Exception;
	List<String> autoCompleteObjectCommand(String s) throws Exception;
			
	IObjectCommandHandable getSubObjectCH(String path) throws Exception;
	List<String> autoCompleteSubObjectCH(String s) throws Exception;
}
