package sdo;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class CopyAndRename {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		File directory = new File("Z:/Projects_Mac_Mini/Smart Data One/Nestle/framed_images/871_Ergiebigkeit_Zubereitungsanweisungen/Zubereitung_02");
		File[] files = directory.listFiles();
		
		for(File f:files){
			if(f.isFile()){
				String newName = f.getName();
				newName = newName.replace("_02.jpg", "_01.jpg");
				File newFile = new File("Z:/Projects_Mac_Mini/Smart Data One/Nestle/framed_images/871_Ergiebigkeit_Zubereitungsanweisungen/Zubereitung_02/renamed/" + newName);
				FileUtils.copyFile(f, newFile);
				System.out.println(f);
				System.out.println(newFile);
			}
		}
	}

}