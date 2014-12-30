package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import KartoffelKanaalPlugin.plugin.AttribSystem;
import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;
import KartoffelKanaalPlugin.plugin.StoreTechnics;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;

public class PNConditionNUMB extends PNCondition{
	protected PNCondition[] arr;
	protected byte min = 0;
	protected byte max = 127;
	
	protected PNConditionNUMB(PNCondition[] children, byte minimum, byte maximum, byte options, boolean invisible, int conditionID, PNTechCondition root){
		super(options, invisible, conditionID, root);
		this.arr = children;
		this.min = minimum;
		this.max = maximum;
	}
	
	protected PNConditionNUMB(PNCondition[] children, byte minimum, byte maximum, byte[] src){
		super(src);
		this.arr = children;
		this.min = minimum;
		this.max = maximum;
	}
	
	@Override
	protected byte getConditionType() {return 4;}
	
	protected boolean calculateValue(){
		if(arr == null)return ((this.options & 0x20) == 0x20);
		boolean atLeastOne = false;
		int count = 0;
		for(int i = 0; i < arr.length; i++){
			if(this.arr[i] == null || this.arr[i].isInvisible())continue;
			atLeastOne = true;
			if(this.arr[i].getConditionValue())count++;
		}
		if(atLeastOne){
			return count >= min && count <= max;
		}else{
			return ((this.options & 0x20) == 0x20);
		}
	}

	@Override
	protected int getEstimatedSize() {
		if(arr == null)return PNCondition.generalInfoLength();
		int l = PNCondition.generalInfoLength() + arr.length * 4;
		for(int i = 0; i < arr.length; i++){
			if(arr[i] != null)l += arr[i].getEstimatedSize();
		}
		return l;
	}

	public static PNConditionNUMB loadFromBytes(byte[] src) {
		if(src == null || src.length < PNCondition.generalInfoLength() || src.length > 500000)return null;
		byte[][] c = StoreTechnics.loadArray(src, 100, 100000, PNCondition.generalInfoLength() + 2);
		PNCondition[] conditions = new PNCondition[c.length];
		for(int i = 0; i < c.length; i++){
			conditions[i] = PNCondition.loadFromBytes(src);
		}
		
		return new PNConditionNUMB(conditions, src[6], src[7], src);
	}

	@Override
	protected byte[] saveCondition(){
		byte[][] a = new byte[this.arr.length][];
		for(int i = 0; i < arr.length; i++){
			if(arr[i] == null){
				a[i] = new byte[0];
			}else{
				a[i] = arr[i].saveCondition();
				if(a[i] == null)a[i] = new byte[0];
			}
		}
		byte[] solution = StoreTechnics.saveArray(a, 100);
		if(solution == null || solution.length > 500000)solution = new byte[0];
		
		byte[] ans = new byte[solution.length + PNCondition.generalInfoLength()];
		
		this.saveGeneralInfo(ans);
		
		ans[PNCondition.generalInfoLength()    ] = min;
		ans[PNCondition.generalInfoLength() + 1] = max;
		
		System.arraycopy(solution, 0, ans, PNCondition.generalInfoLength() + 2, solution.length);
		
		return ans;
	}

	public static PNConditionNUMB createFromParams(String[] params, byte options, int ID, PNTechCondition root) throws Exception{
		throw new Exception("Functie nog niet beschikbaar");
	}

	@Override
	public PNConditionNUMB createCopy(int ID, PNTechCondition root) throws Exception {
		PNCondition[] children = null;
		if(this.arr == null){
			children = new PNCondition[0];
		}else{
			children = new PNCondition[this.arr.length];
			for(int i = 0; i < this.arr.length; i++){
				if(this.arr[i] != null){
					children[i] = this.arr[i].createCopy(601, root);//TODO De Condition-ID moet dynamisch asigned worden
				}
			}
		}
		return new PNConditionNUMB(children, this.min, this.max, this.options, true, ID, root);
	}

	@Override
	public boolean handleObjectCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args) throws Exception {
		if(super.handleObjectCommand(executor, a, attribSys, args))return true;
		String commandLabel = args[0];
		if(commandLabel.equals("array")){
			this.arr = ConditionArrayFunctions.handleSubCommand(executor, a, attribSys, args, this, this.arr);
		}else if(commandLabel.equals("bounds")){
			if(args.length == 1){
				a.sendMessage("§eEr moeten minimum " + this.min + " en maximum " + this.max + " conditions van de " + (this.arr == null?0:this.arr.length) + " conditions juist zijn om de waarde van deze condition true maken.");
			}else if(args.length == 2){
				byte exact;
				try{
					exact = Byte.parseByte(args[1]);
				}catch(NumberFormatException e){
					a.sendMessage("§4De nieuwe, exacte value moet een getal van 0 tot 127 zijn.");
					return true;
				}
				if(exact < 0)exact = 0;
				min = exact;
				max = exact;
				a.sendMessage("§eHet aantal ware conditions moet nu exact " + exact + " zijn");
			}else if(args.length == 3){
				byte bound1;
				byte bound2;
				try{
					bound1 = Byte.parseByte(args[1]);
					bound2 = Byte.parseByte(args[2]);
				}catch(NumberFormatException e){
					a.sendMessage("§4De nieuwe bounds moeten allebei een getal van 0 tot 127 zijn.");
					return true;
				}
				
				boolean reverse = bound2 < bound1;
				this.min = (reverse)?bound2:bound1;
				this.max = (reverse)?bound1:bound2;
				
				a.sendMessage("§eEr moeten minimum " + this.min + " en maximum " + this.max + " conditions van de " + (this.arr == null?0:this.arr.length) + " conditions juist zijn om de waarde van deze condition true maken.");
			}else{
				a.sendMessage("§eObjectCommand: §cbounds [<<bound 1> <bound 2>>|<nieuwe exacte value>]");
			}
		}else{
			return false;
		}
		return true;
	}

	@Override
	public ArrayList<String> autoCompleteObjectCommand(String[] args, ArrayList<String> a) throws Exception {
		a = super.autoCompleteObjectCommand(args, a);
		if(args.length == 0)return a;
		
		String commandLabel = args[0].toLowerCase();
		if(args.length == 1){
			if("array".startsWith(commandLabel))a.add("array");
			if("bounds".startsWith(commandLabel))a.add("bounds");
		}else{
			if(commandLabel.equals("array")){
				a = ConditionArrayFunctions.autoCompleteSubCommand(args, a);
			}
		}
		return a;
	}

	@Override
	public IObjectCommandHandable getSubObjectCH(String path) throws Exception {
		if(path.startsWith("#")){
			int arrIndex;
			try{
				arrIndex = Integer.parseInt(path.substring(1));
			}catch(NumberFormatException e){
				return null;
			}
			if(arrIndex < 0 || arrIndex >= this.arr.length)return null;
			return this.arr[arrIndex];
		}
		return null;
	}

	@Override
	public ArrayList<String> autoCompleteSubObjectCH(String s, ArrayList<String> a) throws Exception {
		a = super.autoCompleteSubObjectCH(s, a);
		if("#".startsWith(s))a.add("#");
		return a;
	}

}
