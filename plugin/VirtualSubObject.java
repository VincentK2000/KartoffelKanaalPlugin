package KartoffelKanaalPlugin.plugin;

import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class VirtualSubObject implements IObjectCommandHandable{
	private IObjectCommandHandable parent;
	
	private String[] prefixArguments;
	private String prefixSubObject;
	
	private boolean analogPreferred;
	
	public VirtualSubObject(IObjectCommandHandable parent, boolean analogPreferred, String[] prefixArguments, String prefixSubObject) throws Exception{
		if(parent == null)throw new Exception("VirtualSubObject: Parent is null");
		this.parent = parent;
		this.analogPreferred = analogPreferred;
		this.prefixArguments = prefixArguments;
		this.prefixSubObject = prefixSubObject;
		if(this.prefixSubObject == null)this.prefixSubObject = new String();
	}

	@Override
	public boolean handleObjectCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args) throws Exception {
		if(this.prefixArguments != null && this.prefixArguments.length > 0){
			if(args == null)args = new String[0];
			String[] newArgs = new String[args.length + prefixArguments.length];
			System.arraycopy(this.prefixArguments, 0, newArgs, 0, this.prefixArguments.length);
			System.arraycopy(args, 0, newArgs, this.prefixArguments.length, args.length);
			args = newArgs;
		}
		return this.parent.handleObjectCommand(executor, a, attribSys, args);
	}

	@Override
	public ArrayList<String> autoCompleteObjectCommand(String[] args, ArrayList<String> a)throws Exception {
		a = this.parent.autoCompleteObjectCommand(args, a);
		if(this.prefixArguments != null && this.prefixArguments.length > 0){
			if(args == null)args = new String[0];
			String[] newArgs = new String[args.length + prefixArguments.length];
			System.arraycopy(this.prefixArguments, 0, newArgs, 0, this.prefixArguments.length);
			System.arraycopy(args, 0, newArgs, this.prefixArguments.length, args.length);
			args = newArgs;
		}
		return a;
	}

	@Override
	public IObjectCommandHandable getSubObjectCH(String path) throws Exception {
		return this.parent.getSubObjectCH(this.prefixSubObject + path);
	}

	@Override
	public ArrayList<String> autoCompleteSubObjectCH(String s, ArrayList<String> a) throws Exception {
		ArrayList<String> response = this.parent.autoCompleteSubObjectCH(this.prefixSubObject + s, a);
		if(response != null && this.prefixSubObject.length() > 0){
			for(int i = 0; i < response.size(); i++){
				String r = response.get(i);
				if(r != null && r.startsWith(this.prefixSubObject)){
					response.set(i, r.substring(this.prefixSubObject.length()));
				}
			}
		}
		return response;
	}
	
	public boolean isAnalogPreferred(){
		return this.analogPreferred;
	}	
}
