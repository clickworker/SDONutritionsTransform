package sdo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Runner {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		if(args.length != 2){
			System.out.println("Wrong number of arguments: [1] = Source *.xlsx  [2] = Target *.xlsx (will be created)!");
			return;
		}
		
		File f = new File(args[0]);
		
		if(!f.exists()){
			System.out.println("File " + args[0] + " cant't be opened!");
			return;
		}
		
		DataTransformService dts = new DataTransformService();
		ArrayList<Product> products =  dts.transferDataFromSourceToModel(args[0]);
		try{
			dts.writeModelToSheet(products, args[1]);
		}catch(Exception e){
			System.out.println("Something went wrong, when trying to write output sheet!");
			e.printStackTrace();
		}
	}
}
