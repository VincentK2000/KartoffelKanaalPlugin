package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

public class PNExtended{}

/*package me.vincentk.kartoffelkanaalplugin.PulserSystem;

public abstract class PNMessage {
	//protected final static Class<?extends PulserNotificationMessage>[] RegisteredPulserNotificationTypes = new Class[255];
	//protected Class<?extends PulserNotificationMessage>[] a = new PulserNotificationMessage.class[];

	protected PulserNotification NotificationBase;
	protected boolean initialized = false;
	
	protected PulserNotificationMessage(PulserNotification parent){
		this.NotificationBase = parent;
	}
	
	
	protected abstract void updateRawText();
	protected abstract void initializeRawText();
	
	protected abstract byte[] getSaveArray();
	
	protected static PulserNotificationMessage loadFromBytes(byte[] src){
		if(src == null || src.length == 0)return null;
		byte type = src[0];
		if(type == 1){
			return PulserNotificationMessageFormatted.loadFromBytes(src);
		}else{
			return null;
		}
	}
	
	protected void notifyChange(){
		if(this.NotificationBase != null)this.NotificationBase.changed = true;
	}
	
	protected abstract String[] getPossibleProperties();
	
	public static String createJSONSafeString(String src){
		if(src == null || src.length() == 0)return "";
		StringBuilder s = new StringBuilder(src.length());
		char[] in = src.toCharArray();
		for(int i = 0; i < in.length; i++){
			if(in[i] == '\"' || in[i] == '\\' || in[i] == '/'){
				s.append('\\');
				s.append(in[i]);
				
			}else if(in[i] == '\b' || in[i] == '\f' || in[i] == '\n' || in[i] == '\r' || in[i] == '\t'){
				s.append(" ");
				
			}else{
				s.append(in[i]);
			}
		}
		return s.toString();
	}
	
	protected boolean preventSavingRawText(){
		return false;
	}
	
	protected void initialize(){
		this.initializeRawText();
		this.initialized = true;
	}
}
*/