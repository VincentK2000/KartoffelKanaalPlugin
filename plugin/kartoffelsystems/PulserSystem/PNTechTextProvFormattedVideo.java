package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import org.bukkit.entity.Player;

import KartoffelKanaalPlugin.plugin.Main;

public class PNTechTextProvFormattedVideo extends PNTechTextProvFormatted{
	//0: Video titel
	//1: Video URL suffix
	//2: Youtube-kanaal weergavenaam
	//3: Youtube-kanaal URL suffix
	
	//https://www.youtube.com/watch?v=
	//htpps://www.youtube.com/channel/
	
	@Override
	public byte getFormattedType(){return 1;}

	public static final int correctAmountParameters = 4;
	
	protected String[] possibilities = new String[]{"video.titel","video.URL","kanaal.titel","kanaal.URL"};
	
	protected PNTechTextProvFormattedVideo(String[] parameters, boolean invisible, int ID, PulserNotifStandard base) {
		super(parameters, invisible, ID, base);
	}
	
	protected PNTechTextProvFormattedVideo(byte[] src){
		super(src);
	}

	protected int getNamedParameterIndex(String key) {
		key = key.toLowerCase();
		//if(key.equals("video.titel") || key.equals("videotitel") || key.equals("vidtitel") || key.equals("titel")){
		//	return 0;
		//}else if(key.equals("video.url") || key.equals("videourl") || key.equals("vidurl") || key.equals("url")){
		//	return 1;
		//}
		switch(key){
			case "video.titel":
			case "vid.titel":
			case "titel":
				return 0;
				
			case "video.url":
			case "video.link":
			case "vid.url":
			case "vid.link":
			case "url":
				return 1;
			
			case "kanaal.titel":
			case "kanaal.weergave":
			case "kanaal.naam":
				return 2;
				
			case "kanaal.url":
			case "kanaal.link":
				return 3;
		}
		return -1;
	}
	
	@Override
	protected String getMessage(){
		if(this.parameters == null || this.parameters.length < 4)return null;
		/*String vidtitel;
		String vidurl;
		
		String kanaalnaam = "???";
		String kanaalurl = "???";
		
		if(this.parameters[0] == null || this.parameters[0].length() == 0 || this.parameters[1] == null || this.parameters[1].length() == 0){
			this.NotificationBase.rawmessage = "";
			return;
		}
		
		vidtitel = this.parameters[0];
		vidurl = this.parameters[1];
		if(vidurl.length() == 11){
			vidurl = "https://www.youtube.com/watch?v=" + vidurl;
		}else if(!(vidurl.startsWith("http://www.youtube.com/watch?v=") || vidurl.startsWith("https://www.youtube.com/watch?v=")){
			vidurl = java.net.URLEncoder.encode(vidtitel,"UTF-8");
			vidurl = vidurl.replace(' ', '+');
			vidurl = "https://www.youtube.com/results?search_query=" + vidurl;
		}
		if(this.parameters.length >= 3 && this.parameters[2] != null && this.parameters.length > 0){
			kanaalnaam = this.parameters[2];
			if(this.parameters.length >= 4 && this.parameters[3] != null && this.parameters.length > 0){
				kanaalurl = this.parameters[3];
				if(!(this.parameters[3].startsWith("http://www.youtube.com/user/") || this.parameters[3].startsWith("https://www.youtube.com/user/"))){
					kanaalurl = "https://www.youtube.com/user/" + kanaalurl;
				}
			}
		}
		*/
		String video = PNTechTextProvFormattedVideo.createVideoJSON(this.parameters[0], this.parameters[1], this.parameters[2], "blue");
		String channel = PNTechTextProvFormattedVideo.createChannelJSON(this.parameters[2], this.parameters[3], "dark_green");
		
		if(video == null || video.length() == 0)return null;
		
		StringBuilder s = new StringBuilder(1300);	
		
		s.append("{text:\"\",color:\"green\",extra:[");
		if(channel == null || channel.length() == 0){
			s.append("\"Nieuwe video\"");
		}else{
			s.append("\"Nieuwe video van \",");
			s.append(channel);
		}
		s.append(",\": \",");
		s.append(video);
		s.append(",\" \",");
		s.append("{text:\"\\[Bekijk\\]\",color:\"dark_green\",");
		s.append("hoverEvent:{action:\"show_text\",value:\"Klik om naar de video te gaan\"},");
		s.append("clickEvent:{action:\"open_url\",value:\"https://www.youtube.com/watch?v=" + this.parameters[1] + "\"}");
		s.append("}]}");
		
		return s.toString();
	}
	
	@Override
	protected byte getCorrectAmountParameters(){return 4;}

	@Override
	protected String[] getPossibleKeys() {
		return this.possibilities;
	}
	
	protected static String[] getChannelDescription(String channelurl){
		if(channelurl.equals("KartoffelKanaal")){//KartoffelKanaal
			return new String[]{
					"KartoffelKanaal is het",
					"Youtube-kanaal van de owner",
					"Hierop komen o.a. video's zoals:",
					"-Server Tour",
					"-Server Survival",
					"-Nog veel meer"
			};
		}else if(channelurl.equals("wolfert66")){//Laurens Wolfert
			return new String[]{
					"Laurens maakt:",
					"Engelstalige video's"
			};
		}else if(channelurl.equals("/UCJ2zukd7RcVYfDLzFNiJMnw")){//Jelle Van Den Aakster
			return new String[]{
					"Jelle maakt:",
					"-Overwegend video's van mini-games",
					"-Een SurvivalSerie op de KartoffelKanaalServer",
					"-Andere videos"
			};
		}else if(channelurl.equals("/UCRaVPcUcH0VkWDCnMP0bXSQ")){//Merlijn
			return new String[]{
					"Merlijn maakt:",
					"Interessante Redstone videos"
			};
		}
		return new String[0];
	}
	
	protected static String createChannelJSON(String weergave, String loc, String color){
		if(weergave == null || weergave.length() == 0 || loc == null || loc.length() == 0)return "";
		weergave = PNTechTextProv.createJSONSafeString(weergave);
		StringBuilder s = new StringBuilder(450);
		s.append("{text:\"");
		s.append(weergave);
		s.append("\"");
		if(color != null && color.length() > 0){
			s.append(",color:\"");
			s.append(color);
			s.append("\"");
		}
		s.append(",hoverEvent:{action:\"show_item\",value:\"{id:322,tag:{display:{Name:\\\"");
		s.append(PNTechTextProv.createJSONSafeString(weergave));//Het moet nog een keer bewerkt worden omdat het dit keer in een String moet namelijk in de "show_item"-value
		s.append("\\\",Lore:[");
		{
			String[] description = PNTechTextProvFormattedVideo.getChannelDescription(loc);
			for(int i = 0; i < description.length; i++){
				if(i > 0)s.append(',');
				s.append(PNTechTextProv.createJSONSafeString(description[i]));
			}
		}
		s.append("]}}}\"},clickEvent:{action:\"open_url\",value:\"");
		loc = PNTechTextProv.createJSONSafeString(loc);
		if(loc == null || loc.length() == 0)return "";
		s.append((loc.charAt(0) == '\\' && loc.charAt(1) == '/')?"https://www.youtube.com/channel":"https://www.youtube.com/user/");
		s.append(loc);
		s.append("\"}}");
		return s.toString();
	}
	protected static String createVideoJSON(String titel, String loc, String maker, String color){
		if(titel == null || titel.length() == 0 || loc == null || loc.length() != 11)return "";
		titel = PNTechTextProv.createJSONSafeString(titel);
		loc = PNTechTextProv.createJSONSafeString(loc);
		maker = PNTechTextProv.createJSONSafeString(maker);
		StringBuilder s = new StringBuilder(480);
		s.append("{text:\"");
		s.append(titel);
		s.append("\"");
		if(color != null && color.length() > 0){
			s.append(",color:\"");
			s.append(color);
			s.append("\"");
		}
		s.append(",bold:true");
		s.append(",hoverEvent:{action:\"show_item\",value:\"{id:322,tag:{display:{Name:\\\"");
		s.append(PNTechTextProv.createJSONSafeString(titel));
		s.append("\\\",Lore:[\\\"Door ");
		s.append(PNTechTextProv.createJSONSafeString(maker));
		s.append("\\\",\\\"Klik om naar de video te gaan\\\"]}}}\"}");
		s.append(",clickEvent:{action:\"open_url\",value:\"https://www.youtube.com/watch?v=");
		s.append(loc);
		s.append("\"}}");
		return s.toString();
	}

	@Override
	protected void onParametersChanged() {
		try{
			this.setInvisible(true);
		}catch(Exception e){}
	}

	@Override
	public boolean crashTestRequired() {
		return true;
	}

	@Override
	public void doCrashTest(Player pl) throws Exception {
		if(pl == null)throw new Exception("Player is null!");
		Main.plugin.getServer().dispatchCommand(Main.plugin.getServer().getConsoleSender(), "tellraw " + pl.getName() + ' ' + this.getMessage());
	}

	@Override
	protected byte getParameterViewAccessLevel() {
		return 20;
	}

	@Override
	protected byte getParameterChangeAccessLevel(int paramID) {
		if(paramID == 0 || paramID == 1){
			return 21;
		}else if(paramID == 2){
			return 22;
		}else if(paramID == 3){
			return 23;
		}else{
			return 127;
		}
	}

	@Override
	protected boolean isSectionSignFormatAccepted(int index) {
		return true;
	}
	
	public static PNTechTextProvFormattedVideo createFromParams(String[] params, int ID, PulserNotifStandard notificationBase) throws Exception {
		if(params == null)throw new Exception("De parameters zijn null");
		if(params.length != 1 || !params[0].toLowerCase().equals("ok")){
			throw new Exception("De Formatted.Video moet achteraf worden setup'ed met het setup-commando op het element en geactiveerd met het activate-commando. Voeg OK toe om door te gaan.");
		}
		return new PNTechTextProvFormattedVideo(new String[]{"","","",""}, true, ID, notificationBase);
	}

	@Override
	public PNTechTextProvFormattedVideo copyTech(int ID, PulserNotifStandard notificationBase) {
		return new PNTechTextProvFormattedVideo(this.copyParameters(), true, ID, notificationBase);
	}
	
	@Override
	public String getTypeName(){
		return super.getTypeName() + "Video";
	}
}
