package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.command.CommandSender;

import KartoffelKanaalPlugin.plugin.AttribSystem;
import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;

public class PNConditionRandom extends PNCondition{
	protected Random r;
	protected int total = 1;
	protected int positiveAmount = 1;
	
	
	protected PNConditionRandom(int total, int positiveAmount, byte options, boolean invisible, int conditionID, PNTechCondition base){
		super(options, invisible, conditionID, base);
		this.total = total;
		this.positiveAmount = positiveAmount;
		this.validateRandomizers();
	}
	
	protected PNConditionRandom(int total, int positiveAmount, byte[] src){
		super(src);
		this.total = total;
		this.positiveAmount = positiveAmount;
		this.validateRandomizers();
	}
	
	public void validateRandomizers(){
		if(positiveAmount < 0)positiveAmount = 0;
		if(total <= positiveAmount)total = positiveAmount;
		if(total == 0)total = 1;
	}
	
	@Override
	protected byte getConditionType() {return 10;}

	@Override
	protected boolean calculateValue() {
		if(r == null)r = new Random();
		int a = r.nextInt(total);
		return a < this.positiveAmount;
	}

	protected static PNConditionRandom loadFromBytes(byte[] src){
		if(src == null || src.length < PNCondition.generalInfoLength() + 8)return null;
		
		int s = PNCondition.generalInfoLength();
		
		int total = src[s++] << 24 | src[s++] << 16 | src[s++] << 8 | src[s++];//de ++ moet pas na de waarde-opvraging gebeuren aangezien de lengte van de generalInfo wordt gebruikt
		int positiveAmount = src[s++] << 24 | src[s++] << 16 | src[s++] << 8 | src[s++];
		
		return new PNConditionRandom(total, positiveAmount, src);
	}
	
	@Override
	protected byte[] saveCondition() {
		byte[] ans = new byte[8 + PNCondition.generalInfoLength()];
		
		this.saveGeneralInfo(ans);
		
		int s = PNCondition.generalInfoLength();
		ans[s++] = (byte)((this.total >>> 24) & 0xFF);//de ++ moet pas na de waarde-opvraging gebeuren aangezien de lengte van de generalInfo wordt gebruikt
		ans[s++] = (byte)((this.total >>> 16) & 0xFF);
		ans[s++] = (byte)((this.total >>>  8) & 0xFF);
		ans[s++] = (byte)((this.total       ) & 0xFF);
		
		ans[s++] = (byte)((this.positiveAmount >>> 24) & 0xFF);
		ans[s++] = (byte)((this.positiveAmount >>> 16) & 0xFF);
		ans[s++] = (byte)((this.positiveAmount >>>  8) & 0xFF);
		ans[s  ] = (byte)( this.positiveAmount         & 0xFF);
		
		return ans;
	}

	@Override
	protected PNCondition createCopy(int id, PNTechCondition root) {
		return new PNConditionRandom(this.total, this.positiveAmount, this.options, this.invisible, id, root);
	}

	@Override
	protected int getEstimatedSize() {
		return 8 + PNCondition.generalInfoLength();
	}
	
	@Override
	public boolean handleObjectCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args) throws Exception {
		if(super.handleObjectCommand(executor, a, attribSys, args))return true;
		String commandLabel = args[0].toLowerCase();
		if(commandLabel.equals("chance")){
			if(args.length != 1){
				a.sendMessage("§eDe kans dat de Condition positief is, is " + this.positiveAmount + " op de " + this.total + " (" + (((double)this.positiveAmount / this.total) * 100) + "%)");
				a.sendMessage("§cVerander de kans met: §chance <nieuwePosAmount/nieuweTotalAmount>|<nieuwePercentage>%  [^geenVereenvoudigen]");
			}else if(args.length == 2){
				int deviderIndex = args[1].indexOf((int)'/');
				if(deviderIndex >= 0 && deviderIndex < args[1].length()){
					int newPosAmount;
					try{
						newPosAmount = Integer.parseInt(args[1].substring(0, deviderIndex));
						if(newPosAmount < 0)throw new Exception("Positive amount is kleiner dan 0");
					}catch(Exception e){
						a.sendMessage("§4Oncorrecte positive amount. Bedenk dat het een natuurlijk getal moet zijn.");
						return true;
					}
					
					int newTotalAmount;
					try{
						newTotalAmount = Integer.parseInt(args[1].substring(deviderIndex + 1));
						if(newTotalAmount <= 0)throw new Exception("Total amount is kleiner dan of gelijk aan 0");
					}catch(Exception e){
						a.sendMessage("§4Oncorrecte total amount. Bedenk dat het een natuurlijk getal groter dan 0 moet zijn.");
						return true;
					}
					this.positiveAmount = newPosAmount;
					this.total = newTotalAmount;
				}else if(args[1].charAt(args[1].length() - 1) == '%'){
					double percentage;
					try{
						percentage = Double.parseDouble(args[1].substring(0, args[1].length() - 1));
					}catch(Exception e){
						a.sendMessage("§4Oncorrecte percentage. Bedenk dat het een decimaal getal moet zijn (tussen (incl.) 0% en 100%).");
						return true;
					}
					if(percentage <= 0){
						this.positiveAmount = 0;
						this.total = 1;
					}else if(percentage >= 100){
						this.positiveAmount = 1;
						this.total = 1;
					}else{
						int newPos;
						int newTotal;
						if((percentage % 1) == 0){
							newPos = (int)percentage;
							newTotal = 100;
						}else{
							//double multiplier = Math.ceil((double)1 / remainder);
							//newPos = (int)(percentage * multiplier);
							//newTotal = (int)(100 * multiplier);
							
							//100
							//multip = max / 100;
							//max = 800
							//multip = 8
							newTotal = Integer.MAX_VALUE;
							newPos = (int)(percentage * (Integer.MAX_VALUE / 100));
						}
						this.positiveAmount = newPos;
						this.total = newTotal;
					}
					
				}else{
					a.sendMessage("§4De nieuwe value moet of een breuk zijn (bv. 2/5 ) of een percentage (bv. 25.5% ).");
				}
				if(!attribSys.hasAttrib("geenVereenvoudigen")){
					int numb1 = this.positiveAmount;
					int numb2 = this.total;
					int oldNumb1;
					
					while(numb2 != 0){
						oldNumb1 = numb1;
						numb1 = numb2;
						numb2 = oldNumb1 % numb2;
					}
					/*private static int findGCD(int number1, int number2) {
				        //base case
				        if(number2 == 0){
				            return number1;
				        }
				        return findGCD(number2, number1%number2);
				    }*/
					this.positiveAmount /= numb1;
					this.total /= numb1;
				}
				a.sendMessage("§eDe kans dat de Condition positief is, is nu " + this.positiveAmount + " op de " + this.total + " (" + (((double)this.positiveAmount / this.total) * 100) + "%)");
			}
		}
		
		return false;
	}

	@Override
	public ArrayList<String> autoCompleteObjectCommand(String[] args) throws Exception {
		ArrayList<String> a = super.autoCompleteObjectCommand(args);
		if(a == null)a = new ArrayList<String>();
		
		return a;
	}

	@Override
	public IObjectCommandHandable getSubObjectCH(String path) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> autoCompleteSubObjectCH(String s) throws Exception {
		return super.autoCompleteSubObjectCH(s);
	}

	@Override
	public String[] getLocalTopLevelArgsPossibilities() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public PNConditionRandom copyCondition(int ID, PNTechCondition root) throws Exception {
		return new PNConditionRandom(this.total, this.positiveAmount, this.options, true, ID, root);
	}
	
	public static PNConditionRandom createFromParams(String[] params, byte options, int ID, PNTechCondition root) throws Exception{
		throw new Exception("Functie nog niet beschikbaar");
	}
}
