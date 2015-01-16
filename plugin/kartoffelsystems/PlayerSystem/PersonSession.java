package KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem;

import org.bukkit.command.CommandSender;

import java.util.UUID;

public class PersonSession {
	private PlayerManager pm;
	private Person p;
	
	public PersonSession(PlayerManager pm){
		this.pm = pm;
	}
	
	public boolean loadLoadedPerson(CommandSender a){
		if(this.p != null)return true;
		if(this.pm == null){
			return false;
		}else{
			this.p = this.pm.getLoadedPerson(a);
			if(this.p == null)return false;
			this.p.sessionSys.acquireAccess();
			return true;
		}
	}
	
	public boolean loadLoadedPlayer(String name){
		if(this.p != null)return true;
		if(this.pm == null){
			return false;
		}else{
			this.p = this.pm.getLoadedPlayer(name);
			if(this.p == null)return false;
			this.p.sessionSys.acquireAccess();
			return true;
		}
	}
	
	public boolean loadLoadedPlayer(UUID id){
		if(this.p != null)return true;
		if(this.pm == null){
			return false;
		}else{
			this.p = this.pm.getLoadedPlayer(id);
			if(this.p == null)return false;
			this.p.sessionSys.acquireAccess();
			return true;
		}
	}
	
	public boolean loadAnyPlayer(String name){
		if(this.p != null)return true;
		if(this.pm == null){
			return false;
		}else{
			this.p = this.pm.getPlayer(name);
			if(this.p == null)return false;
			this.p.sessionSys.acquireAccess();
			return true;
		}
	}
	
	public boolean loadAnyPlayer(UUID id){
		if(this.p != null)return true;
		if(this.pm == null){
			return false;
		}else{
			this.p = this.pm.getPlayer(id);
			if(this.p == null)return false;
			this.p.sessionSys.acquireAccess();
			return true;
		}
	}
	
	public boolean foundPerson(){
		return p != null;
	}
	
	public Person getPerson(){
		return this.p;
	}
	
	public void unlock(){
		if(this.p != null){
			this.p.sessionSys.releaseAccess();
		}
	}
	
}
