package KartoffelKanaalPlugin.plugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class AdvancedChat implements Listener{
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerChat(AsyncPlayerChatEvent e){
		if(DebugTools.devadvancedchat && Main.isDeveloper(e.getPlayer().getUniqueId())){
			e.setMessage(AdvancedChat.verkleurUitgebreid(e.getMessage()));
			if(e.getMessage().length() == 0)e.setCancelled(true);
		}
	}
	
	public static String verkleurUitgebreid(String input){
		if(input == null)return "";
		if(input.length() == 0)return "";
		char[] src = input.toCharArray();
		StringBuilder nieuwbericht = new StringBuilder(input.length());
		boolean vlak = false;
		boolean code = false;
		for(int i = 0; i < src.length; i++){
			if(vlak || i == src.length - 1){
				if(src[i] == '§')
					nieuwbericht.append('&');
				else
					nieuwbericht.append(src[i]);
				vlak = false;
			}else{
				if(code){
					if(src[i] == '~'){
						code = false;
						nieuwbericht.append("§r");
					}else if(src[i] == '\\'){
						vlak = true;
					}else{
						nieuwbericht.append(src[i]);
					}
				}else{
					if(src[i] == '&' || src[i] == '§'){
						if(isOpmaakSoort(src[i + 1])){
							nieuwbericht.append('§');
						}else{
							nieuwbericht.append('&');
						}
					}else if(src[i] == '~'){
						code = true;
						nieuwbericht.append("§r§c");
					}else if(src[i] == '\\'){
						vlak = true;
					}else{
						nieuwbericht.append(src[i]);
					}
				}
			}
		}
		String uitkomst = nieuwbericht.toString();
		if(uitkomst.length() > 255){
			uitkomst = uitkomst.substring(0, 255);
		}
		return uitkomst;
	}
	public static boolean isOpmaakSoort(char src){
		/*return (src == '0' || src == '1' || src == '2' || src == '3' || src == '4' || src == '5' || src == '6' || src == '7' ||
				src == '8' || src == '9' || src == 'a' || src == 'b' || src == 'c' || src == 'd' || src == 'e' || src == 'f' ||
				src == 'k' || src == 'l' || src == 'm' || src == 'n' || src == 'o' || src == 'r'
			   );*/
		return isOpmaak(src) || isKleur(src);
	}
	public static boolean isOpmaak(char abc){
		return abc > 106 && abc < 112 || abc == 'r';
	}
	public static boolean isKleur(char abc){
		return abc > 47 && abc < 58 || abc > 96 && abc < 103;
	}
}
