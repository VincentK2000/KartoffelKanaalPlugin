package KartoffelKanaalPlugin.plugin;

import java.util.ArrayList;

public class AttribSystem{
	Attrib[] a = null;
	public AttribSystem(){}
	public String[] initialize(String[] args, int min){
		if(args == null)return null;
		if(args.length == 0)return args;
		int i;
		ArrayList<Attrib> attribs = new ArrayList<Attrib>(0);
		for(i = args.length - 1; i >= min; i--){
			if(args[i] == null || args[i].length() < 2)break;
			if(args[i].charAt(0) == '^'){
				if(args[i].charAt(1) == '^'){
					i--;
					break;
				}
				
				String s = args[i].substring(1);
				
				{
					String b = s.replaceAll("[0-9a-zA-Z?.,!@:-^_]", "");
					if(b.length() > 0)continue;
				}
				
				int semIndex = s.indexOf(':');//semicolon index
				if(semIndex > 0){//Als er voor de semicolon niks staat wordt het beschouwd als attrib met alleen een key
					String key = s.substring(0, semIndex);
					String value = s.substring(semIndex + 1);
					attribs.add(new Attrib(key, value));
				}else{
					attribs.add(new Attrib(args[i],""));
				}
				
			}else{
				break;
			}
		}
		if(i == args.length - 1)return args;
		a = new Attrib[attribs.size()];
		attribs.toArray(a);
		String[] nArgs = new String[i + 1];
		System.arraycopy(args, 0, nArgs, 0, i + 1);
		return nArgs;
	}

	public String getStringValue(String key, String standard){
		if(key == null || key.length() == 0)return standard;
		key = key.toLowerCase();
		if(a != null){
			for(Attrib atr:a){
				if(atr != null && atr.k.equals(key))return atr.v;
			}
		}
		return standard;
	}
	public Integer getIntValue(String key, int standard){ 
		String a = getStringValue(key, Integer.toString(standard));
		int i;
		try{
			i = Integer.parseInt(a);
		}catch(Exception ex){
			return standard;
		}
		return i;
	}
	
	public boolean hasAttrib(String key){
		if(key == null || key.length() == 0)return false;
		key = key.toLowerCase();
		if(a != null){
			for(int i = 0; i < this.a.length; i++){
				if(this.a[i].k.equals(key))return true;
			}
		}
		return false;
	}
}
