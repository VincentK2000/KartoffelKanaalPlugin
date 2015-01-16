package KartoffelKanaalPlugin.plugin;

import java.io.File;

public class RenewableFile extends KartoffelFile {
	protected final String fileName;
	
	protected String write;
	
	public RenewableFile(String folderPath, int dedicatedResKeyPath, String fileName, String currentLocalResourcePath, byte VersionA, byte VersionB) throws Exception{
		super(VersionA, VersionB, folderPath, dedicatedResKeyPath);
		
		this.fileName = fileName;
		this.res = new SecureBestand(new File(Main.plugin.getDataFolderPath() + currentLocalResourcePath), false);
		this.lastEdition = this.res.getEdition();
	}
	
	public int getNewFileVersion(){
		return ++this.lastEdition;
	}
	
	public File acquireWriteFile(int edition, String reason){
		String editionPart = String.valueOf(edition);
		while(editionPart.length() < 6){
			editionPart = "0" + editionPart;
		}
		this.write = this.folderPath + this.fileName + editionPart + reason + ".kkp";
		return new File(Main.plugin.getDataFolderPath() + this.write);
	}

	public void writeFileFinished(int newEdition){
		if(this.res != null && this.res.getEdition() >= newEdition && this.res.isFileAccessable(this.VersionA, this.VersionB)){
			return;
		}
		if(this.write == null)return;
		File newFile = new File(Main.plugin.getDataFolderPath() + this.write);
		if(!newFile.exists())return;
		
		
		try{
			this.setResourceFile(new SecureBestand(newFile, false), true);
		}catch(Exception e){}
		
	}

}
