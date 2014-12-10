package KartoffelKanaalPlugin.plugin;

public class StoreTechnics{

	public static byte[][] loadArray(byte[] data, int maxParams, int maxParamSize, int startpos){
		if(startpos < 0)startpos = 0;
		if(data == null || data.length < startpos + 4)return new byte[0][];
		int paramCount = 0;
		int pos = startpos;
		
		while(pos + 3 <= data.length && paramCount < maxParams){
			int s = (((int)data[pos++] & 0xFF) << 24) | (((int)data[pos++] & 0xFF) << 16) | (((int)data[pos++] & 0xFF) <<  8) | ((int)data[pos++] & 0xFF);
			pos += s;
			if(pos > data.length)break;
			paramCount++;
		}
		
		pos = startpos;
		byte[][] ans = new byte[paramCount][];
		for(int i = 0; i < paramCount; i++){
			int s = (((int)data[pos++] & 0xFF) << 24) | (((int)data[pos++] & 0xFF) << 16) | (((int)data[pos++] & 0xFF) <<  8) | ((int)data[pos++] & 0xFF);
			if(s < 0 || (s > maxParamSize && maxParamSize >= 0)){
				ans[i] = new byte[0];
			}else{
				ans[i] = new byte[s];
				System.arraycopy(data, pos, ans[i], 0, s);
			}
			pos += s;
		}
		return ans;
	}
	
	
	public static byte[][] loadArrayShort(byte[] data, int maxParams, short maxParamSize, int startpos){
		if(startpos < 0)startpos = 0;
		if(data == null || data.length < startpos + 2)return new byte[0][];
		int paramCount = 0;
		int pos = startpos;
	
		while(pos + 1 <= data.length && paramCount < maxParams){
			int s = (((int)data[pos++] & 0xFF) <<  8) | ((int)data[pos++] & 0xFF);
			pos += s;
			if(pos > data.length)break;
			paramCount++;
		}
	
		pos = startpos;
		byte[][] ans = new byte[paramCount][];
		for(int i = 0; i < paramCount; i++){
			int s = (((int)data[pos++] & 0xFF) << 8) | ((int)data[pos++] & 0xFF);
			if(s < 0 || (s > maxParamSize && maxParamSize >= 0)){
				ans[i] = new byte[0];
			}else{
				ans[i] = new byte[s];
				System.arraycopy(data, pos, ans[i], 0, s);
			}
			pos += s;
		}
		return ans;
	}
	
	public static byte[] saveArray(byte[][] data, int maxParams){
		int params = data.length;
		if(params > maxParams)params = maxParams;
		long len = data.length * 4;
		for(int i = 0; i < params; i++){
			if(data[i] == null)continue;
			len += data[i].length;
		}
		if(len > Integer.MAX_VALUE)return new byte[]{0,0,0,0};
		
		byte[] ans = new byte[(int) len];
		
		int pos = 0;
		for(int i = 0; i < params; i++){
			int l = ((data[i] == null)?0:data[i].length);
			ans[pos++] = (byte) ((l >>> 24) & 0xFF);
			ans[pos++] = (byte) ((l >>> 16) & 0xFF);
			ans[pos++] = (byte) ((l >>>  8) & 0xFF);
			ans[pos++] = (byte) ( l         & 0xFF);
			
			if(l > 0){
				System.arraycopy(data[i], 0, ans, pos, l);
				pos += l;
			}
			
		}
		
		return ans;
	}
	
	public static byte[] saveArrayShort(byte[][] data, int maxParams){
		int params = data.length;
		if(params > maxParams)params = maxParams;
		long len = data.length * 2;
		for(int i = 0; i < params; i++){
			if(data[i] == null)continue;
			len += data[i].length;
		}
		if(len > Integer.MAX_VALUE)return new byte[]{0,0};
		
		byte[] ans = new byte[(int) len];
		
		int pos = 0;
		for(int i = 0; i < params; i++){
			int l = ((data[i] == null)?0:data[i].length);
			ans[pos++] = (byte) ((l >>> 8) & 0xFF);
			ans[pos++] = (byte) ( l        & 0xFF);
		
			if(l > 0){
				System.arraycopy(data[i], 0, ans, pos, l);
				pos += l;
			}
		
		}
		
		return ans;
	}

}
