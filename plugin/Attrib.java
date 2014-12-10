package KartoffelKanaalPlugin.plugin;

public class Attrib{
	public final String k;
	public final String v;
		
	public Attrib(String tag, String value){
		if(tag == null){
			k = "";
		}else{
			k = tag.toLowerCase();
		}
		if(value == null){
			v = "";
		}else{
			v = value;
		}
	}
	public boolean equals(String key){
		return this.k.equals(key.toLowerCase());
	}
	public boolean equalsLC(String key){//LC = Lower Case
		return this.k.equals(key);
	}
	public boolean equals(String key, String value){
		return this.k.equals(key.toLowerCase()) && this.v.equals(value.toLowerCase());
	}
	public boolean equals(Attrib a){
		return a != null && this.k.equals(a.k) && this.v.equals(a.v);
	}
	public String toString(){
		if(k.length() == 0)return "";
		if(v.length() > 0){
			return ("-" + k + ":" + v);
		}else{
			return ("-" + k);
		}
	}
}
