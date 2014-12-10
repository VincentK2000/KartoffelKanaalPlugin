package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

public class PNMessage{}

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
	

	protected void initialize(){
		this.initializeRawText();
		this.initialized = true;
	}
}*/
