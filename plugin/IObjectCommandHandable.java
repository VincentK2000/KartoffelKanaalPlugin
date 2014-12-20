package KartoffelKanaalPlugin.plugin;

import java.util.*;

import org.bukkit.command.CommandSender;

import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;

public interface IObjectCommandHandable {
	boolean handleObjectCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args) throws Exception;
	ArrayList<String> autoCompleteObjectCommand(String[] args) throws Exception;
			
	IObjectCommandHandable getSubObjectCH(String path) throws Exception;
	ArrayList<String> autoCompleteSubObjectCH(String s) throws Exception;
}
