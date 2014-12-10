package KartoffelKanaalPlugin.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Random;

public class OverwritingFile extends KartoffelFile {
	protected String temppath;
	private final File mainFile;
	
	public OverwritingFile(String folderPath, int dedicatedResKeyPath, String mainLocalPath, byte VersionA, byte VersionB) throws Exception{
		super(VersionA, VersionB, folderPath, dedicatedResKeyPath);
		this.mainFile = new File(Main.plugin.getDataFolderPath() + mainLocalPath);
		this.res = new SecureBestand(new File(Main.plugin.getDataFolderPath() + mainLocalPath), false);
		this.lastEdition = res.getEdition();
	}
	
	public int getNewFileVersion(){
		return ++this.lastEdition;
	}
	
	public File acquireWriteFile(int version, String reason){
		String versionPart = String.valueOf(version);
		while(versionPart.length() < 6){
			versionPart = "0" + versionPart;
		}
		String fileOriginalName = this.mainFile.getName();
		if(fileOriginalName == null || fileOriginalName.length() == 0)fileOriginalName = "geenNaam";
		int fileActualNameEnd = fileOriginalName.indexOf('.');
		if(fileActualNameEnd == -1)fileActualNameEnd = fileOriginalName.length() - 1;
		
		fileOriginalName = fileOriginalName.substring(0, fileActualNameEnd);
		this.temppath = this.folderPath + fileOriginalName + "v" + versionPart + reason + ".tmp";
		return new File(Main.plugin.getDataFolderPath() + this.temppath);
	}

	public void writeFileFinished(int newEdition) throws Exception{
		if(this.res != null && this.res.getEdition() >= newEdition && this.res.isFileAccessable(this.VersionA, this.VersionB)){
			return;
		}
		if(this.temppath == null)return;
		File mainFile = this.mainFile;
		Path mainPath = mainFile.toPath();
		
		File backupFile = new File(mainFile.getAbsolutePath() + ".backup");
		Path backupPath = backupFile.toPath();
		SecureBestand backup = null;
		
		File writeFile = new File(Main.plugin.getDataFolderPath() + this.temppath);
		Path writePath = writeFile.toPath();
		
		if(!writeFile.exists())return;
		
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(writeFile);
			byte[] header = new byte[16];
			fis.read(header);
			if(header[0] != this.VersionA || header[1] != this.VersionB){
				try{
					fis.close();
				}catch(Exception e){}
				throw new Exception("FileVersion niet ondersteund, REQUIRED=" + this.VersionA + "::" + this.VersionB + ", FILE=" + header[0] + "::" + header[1]);
			}
		}catch (Exception e) {
			try{
				fis.close();
			}catch(Exception ex){}
			throw new Exception("Fout bij validatie een kartoffel-bestand, PATH = " + writeFile.getAbsolutePath(), e);
		}
		try{
			fis.close();
		}catch(Exception e){}
		
		try{
			if(mainFile != null && mainFile.exists()){
				backupFile.getParentFile().mkdirs();
				backupFile.createNewFile();
				java.nio.file.Files.copy(mainPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
				backup = new SecureBestand(backupFile, false);
				this.setResourceFile(backup, true);
			}
			if(mainFile.exists()/*Zelfs nadat eventueel de resource veranderd is naar de backup file en de mainFile daardoor verwijderd zou moeten worden als die niet meer gebruikt wordt*/){
				/*Local Var!*/mainFile = new File(mainFile.getAbsolutePath() + "." + (new Random()).nextInt(10000));
				mainPath = mainFile.toPath();
			}
			java.nio.file.Files.copy(writePath, mainPath, StandardCopyOption.REPLACE_EXISTING);
			this.setResourceFile(new SecureBestand(/*Local Var!*/mainFile, false), true);
			
			try{
				writeFile.delete();
				if(writeFile.exists())writeFile.deleteOnExit();
			}catch(Exception e){
				System.out.println("De WriteFile kon niet verwijderd worden (" + writeFile==null?"null":writeFile.getAbsolutePath() + ") :");
				e.printStackTrace();
			}
			
			try{
				backupFile.delete();
			}catch(Exception e){}
		}catch(Exception e){
			throw new Exception("Kon de veranderingsprocedure van de resourceFile bij een OverwritingFile niet uitvoeren", e);
		}
		
		try{
			fis.close();
		}catch(Exception e){}
		
	}
	
}
