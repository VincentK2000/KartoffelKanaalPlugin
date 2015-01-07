package KartoffelKanaalPlugin.plugin.kartoffelsystems.PulserSystem;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;

import KartoffelKanaalPlugin.plugin.AttribSystem;
import KartoffelKanaalPlugin.plugin.IObjectCommandHandable;
import KartoffelKanaalPlugin.plugin.Main;
import KartoffelKanaalPlugin.plugin.kartoffelsystems.PlayerSystem.Person;

public class ConditionArrayFunctions {
	
	public static PNCondition[] handleSubCommand(Person executor, CommandSender a, AttribSystem attribSys, String[] args, PNCondition affectedCondition, PNCondition[] currentArray) throws Exception{
		if(args.length > 1){
			String subCmdLabel = args[1].toLowerCase();
			if(subCmdLabel.equals("list")){
				if(currentArray.length == 0){
					a.sendMessage("§4Er zijn geen conditions beschikbaar");
					return currentArray;
				}
				int page;
				if(args.length == 2){
					page = 0;
				}else if(args.length == 3){
					try{
						page = Integer.parseInt(args[2]);
					}catch(NumberFormatException e){
						a.sendMessage("§4Oncorrect nummer; \"" + args[2] + "\"");
						return currentArray;
					}
				}else{
					a.sendMessage("§clist [page]");
					return currentArray;
				}
				if(page < 1){
					a.sendMessage("§4Paginanummer moet minimum 1 zijn");
				}else{
					int numberOfItemsPerPage = attribSys.getIntValue("itemsPerPage", 7);
					if(numberOfItemsPerPage < 1){
						a.sendMessage("§4Er moet minimum 1 item per pagina zijn");
						return currentArray;
					}
					int maxPages = (int)Math.ceil((double)currentArray.length / numberOfItemsPerPage);
					if(page > maxPages){
						a.sendMessage("§4Paginanummer moet maximum " + maxPages + " zijn");
						return currentArray;
					}
					int startIndex = attribSys.getIntValue("start", (page - 1) * numberOfItemsPerPage);
					if(startIndex >= currentArray.length){
						a.sendMessage("§4De startIndex is hoger dan de hoogste index");
						return currentArray;
					}
					int endIndex = attribSys.getIntValue("stop", startIndex + numberOfItemsPerPage - 1);
					if(endIndex < startIndex){
						a.sendMessage("§4De endIndex kan niet kleiner zijn dan de startIndex");
						return currentArray;
					}
					if(endIndex >= currentArray.length)endIndex = currentArray.length - 1;
					a.sendMessage("§e---- Conditions (pagina " + page + ": §9#" + startIndex + "§e tot §9#" + endIndex + "§e) ----");
					for(int i = startIndex; i <= endIndex; i++){
						if(currentArray[i] == null){
							a.sendMessage("§7[#" + i + "] leeg");
						}else{
							boolean isInvisible = currentArray[i].isInvisible();
							a.sendMessage("§9[#" + i + "]" + (isInvisible?"§7":"§a") + " " + currentArray[i].toString());
						}
					}
					a.sendMessage("§e---- " + ((page == maxPages)?"Laatste pagina":("Bekijk volgende pagina met §clist " + (page + 1) + ((numberOfItemsPerPage==7)?"":" ^itemsPerPage:" + numberOfItemsPerPage))) + " ----");
				}
			}else if(subCmdLabel.equals("add")){
				affectedCondition.checkDenyChanges();
				if(args.length == 2){
					a.sendMessage("§cadd <mode> ...");
					a.sendMessage("§eMogelijke modes: create, copy");
				}else if(args.length >= 3){
					String modeSelection = args[2].toLowerCase();
					if(modeSelection.equals("create")){
						String[] creationParams = new String[args.length - 3];
						System.arraycopy(args, 3, creationParams, 0, creationParams.length);
						PNCondition newObject;
						try{
							newObject = PNCondition.createFromParams(creationParams, (byte)0x00, 600, affectedCondition.root);
						}catch(Exception e){
							a.sendMessage("§4Fout bij het maken van de PNCondition: " + e.getMessage());
							return currentArray;
						}
						if(newObject == null){
							a.sendMessage("§4De nieuwe PNCondition is leeg. Misschien is er iets fout gegaan in de creatie procedure?");
							return currentArray;
						}
						
						if(currentArray == null || currentArray.length == 0){
							currentArray = new PNCondition[1];
							currentArray[0] = newObject;
							a.sendMessage("§eDe nieuwe PNCondition is de enige PNCondition in deze PulserNotifStandard. Zijn index is dus §9#0§e.");
							affectedCondition.notifyChange();
						}else{
							int index;
							for(index = 0; index < currentArray.length; index++){
								if(currentArray[index] == null){
									a.sendMessage("§eEen vrije plaats voor de nieuwe PNCondition is gevonden op index §9#" + index + "§e.");
									break;
								}
							}
							if(index < currentArray.length){
								currentArray[index] = newObject;
								a.sendMessage("§eDe nieuwe PNCondition is geplaatst op de index " + index + ".");
								affectedCondition.notifyChange();
							}else{
								a.sendMessage("§eDe lengte van de PNConditions lijst wordt uitgebreid van " + currentArray.length + " naar " + (currentArray.length + 1) + " om plaats te voorzien voor de nieuwe PNCondition.");
								PNCondition[] newArray = new PNCondition[currentArray.length + 1];
								System.arraycopy(currentArray, 0, newArray, 0, currentArray.length);
								newArray[newArray.length - 1] = newObject;
								currentArray = newArray;
								a.sendMessage("§eDe nieuwe PNCondition is geplaatst op de index " + index + ".");
								affectedCondition.notifyChange();
							}
						}
					}else if(modeSelection.equals("copy")){
						if(args.length != 4){
							a.sendMessage("§eNotif-deel: §cadd copy <copy van path>");
						}else{
							String path = args[3];
							IObjectCommandHandable objCH;
							try{
								objCH = Pulser.getObjectCommandHandable(Main.pulser, path);
							}catch(Exception e){
								a.sendMessage("§4Kon de van-PNCondition niet vinden: " + e);
								return currentArray;
							}
							if(objCH == null){
								a.sendMessage("§4Kon de van-PNCondition niet vinden");
								return currentArray;
							}
							if(!(objCH instanceof PNCondition)){
								a.sendMessage("§4Je kan geen PNCondition kopi§ren van een niet-PNCondition");
								return currentArray;
							}
							PNCondition newObject;
							try{
								newObject = ((PNCondition)objCH).createCopy(600, affectedCondition.root);
							}catch(Exception e){
								a.sendMessage("§4Kon geen kopie maken van het object: " + e);
								return currentArray;
							}
							if(newObject == null){
								a.sendMessage("§4Het gegeven kopie is leeg... Misschien is er iets fout met de originele PNCondition?");
								return currentArray;
							}
							if(currentArray == null || currentArray.length == 0){
								currentArray = new PNCondition[1];
								currentArray[0] = newObject;
								a.sendMessage("§eDe gekopi§erde PNCondition is de enige PNCondition in deze PulserNotifStandard. Zijn index is dus §9#0§e.");
								affectedCondition.notifyChange();
							}else{
								int index;
								for(index = 0; index < currentArray.length; index++){
									if(currentArray[index] == null){
										a.sendMessage("§eEen vrije plaats voor de gekopi§erde PNCondition is gevonden op index §9#" + index + "§e.");
										break;
									}
								}
								if(index < currentArray.length){
									currentArray[index] = newObject;
									a.sendMessage("§eEen kopie van de PNCondition op path \"" + path + "\" is geplaatst op de index " + index + ".");
									affectedCondition.notifyChange();
								}else{
									a.sendMessage("§eDe lengte van de PNConditions lijst wordt uitgebreid van " + currentArray.length + " naar " + (currentArray.length + 1) + " om plaats te voorzien voor de gekopi§erde PNCondition.");
									PNCondition[] newArray = new PNCondition[currentArray.length + 1];
									System.arraycopy(currentArray, 0, newArray, 0, currentArray.length);
									newArray[newArray.length - 1] = newObject;
									currentArray = newArray;
									a.sendMessage("§eEen kopie van de PNCondition op path \"" + path + "\" is geplaatst op de index " + (newArray.length - 1) + ".");
									affectedCondition.notifyChange();
								}
							}
						}	
					}
				}					
			}else if(subCmdLabel.equals("remove")){
				affectedCondition.checkDenyChanges();
				if(args.length == 3){
					String selector = args[2];
					if(args[2].startsWith("#")){
						int index;
						try{
							index = Integer.parseInt(selector.substring(1));
						}catch(Exception e){
							a.sendMessage("§4Oncorrecte index: \"" + selector + "\"");
							return currentArray;
						}
						if(index < 0 || index >= currentArray.length){
							a.sendMessage("§4De index moet minimum 0 en maximum " + (currentArray.length - 1) + " zijn.");
							return currentArray;
						}
						currentArray[index] = null;
						a.sendMessage("§eDe PNCondition op index §9#" + index + "§e is nu verwijderd.");
						affectedCondition.notifyChange();
					}else{
						a.sendMessage("§4Momenteel is enkel removen met indexes beschikbaar.");
					}
				}else{
					a.sendMessage("§cremove #<index>");
				}
			}
		}else{
			a.sendMessage("§e'ConditionArray Command'-deel: §c<list|add|remove>");
		}
		return currentArray;
	}
	
	public static ArrayList<String> autoCompleteSubCommand(String[] args, ArrayList<String> a) throws Exception{
		String operationName = args[1].toLowerCase();
		if(args.length == 2){
			if("list".startsWith(operationName))a.add("list");
			if("add".startsWith(operationName))a.add("add");
			if("remove".startsWith(operationName))a.add("remove");
		}else{
			if(operationName.equals("list")){
				int currentIndex = 0;
				try{
					currentIndex = Integer.parseInt(args[2]);
				}catch(Exception e){}//Gewoon weer naar 0 gaan als geen nummer
				a.add(String.valueOf(currentIndex + 1));
			}else if(operationName.equals("add")){
				args[2] = args[2].toLowerCase();
				if(args.length == 3){
					if("copy".startsWith(args[2]))a.add("copy");
					if("create".startsWith(args[2]))a.add("create");
				}else{
					if(args[2].equals("copy")){
						if(args.length == 4){
							a.addAll(Main.autoCompletePath(args[3], Main.pulser));
						}
					}
				}
			}
		}
		return a;
	}
	
	public static IObjectCommandHandable getSubObjectCH(String path, String item, PNCondition[] array) throws Exception{
		item = item.toLowerCase();
		if(item.startsWith("#")){
			int conditionIndex;
			try{
				conditionIndex = Integer.parseInt(item.substring(1));
			}catch(NumberFormatException e){
				throw new Exception("De # moet gevolgd worden door een correcte indexnummer.");
			}
			if(conditionIndex < 0 || conditionIndex >= array.length)throw new Exception("Oncorrecte index voor een item in de array: #" + conditionIndex + ". De index moet minimum 0 en maximum " + (array.length - 1) + " zijn.");
			return array[conditionIndex];
		}else{
			throw new Exception("Onbekende ConditionArray Item selector: \"" + item + "\"");
		}
	}
}
