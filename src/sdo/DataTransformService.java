package sdo;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DataTransformService {

	
	//indexes of the columns from the source file (which is the export from clickworker.com in *.xlsx format)
	private static final int COL_INDEX_GTIN = 0;
	private static final int COL_INDEX_TASK_ID = 2;
	private static final int COL_INDEX_KJL = 3;
	private static final int COL_INDEX_KCAL = 4;
	private static final int COL_INDEX_PROTEINS = 5;
	private static final int COL_INDEX_CARBONS = 6;
	private static final int COL_INDEX_SUGAR = 7;
	private static final int COL_INDEX_CARBONS_ALCOHOL = 8;
	private static final int COL_INDEX_CARBONS_STARCH = 9;
	private static final int COL_INDEX_FAT = 10;
	private static final int COL_INDEX_SATURED_FAT = 11;
	private static final int COL_INDEX_MONOSATURED_FAT = 12;
	private static final int COL_INDEX_POLYUNSATURED_FAT = 13;
	private static final int COL_INDEX_FIBER = 14;
	private static final int COL_INDEX_NATRIUM = 15;
	private static final int COL_INDEX_SALT = 16;
	private static final int COL_INDEX_ALOCOHL = 17;
	private static final int COL_INDEX_BASE = 20;
	private static final int COL_INDEX_PREPSTATE = 21;

	//indexes of the columns for the target file (as long as they are statically. The others are getting dynamically created during processing)
	private static final int GTIN_DER_ARTIKELEINHEIT = 0;
	private static final int PORTIONSGRÖßE = 1;
	private static final int NÄHRSTOFF_ZUBEREITUNGSGRAD = 2;
	private static final int PORTIONSGRÖßE_MAßEINHEIT = 3;
	private static final int BRENNWERT_KCAL = 4;
	private static final int BRENNWERT_KJ = 5;
	private static final int BRENNWERT_MESSGENAUIGKEIT = 6;
	private static final int BRENNWERT_KJ_PERC_RDA = 7;
	private static final int NÄHRWERTANGABEN_BESTANDTEIL_0 = 8;
	private static int PERCENTAGEOFALCOHOLBYVOLUME_PERC = 63; //gets dynamically re-set during processing
	private static int SONSTIGE_NÄHRWERTANGABEN_BESTANDTEIL_0 = 119; //gets dynamically re-set during processing
	private static int SONSTIGE_NÄHRWERTANGABEN_WERT_0 = 120; //gets dynamically re-set during processing
	private static int SONSTIGE_NÄHRWERTANGABEN_MAßEINHEIT_0 = 121; //gets dynamically re-set during processing
	private static int SONSTIGE_NÄHRWERTANGABEN_MESSGENAUIGKEIT_0 = 122; //gets dynamically re-set during processing
	private static int SONSTIGE_NÄHRWERTANGABEN_PERC_DER_REFERENZMENGE_0 = 123; //gets dynamically re-set during processing

	private static int VITAMINE_MINERALIEN_CODE_0 = 64; //gets dynamically re-set during processing
	private short maxCountOfNutritions = 0;
	private short maxCountOfVitamins = 0;

	private String columnNamesCSV = "GTIN der Artikeleinheit,Portionsgröße,Nährstoff: Zubereitungsgrad,Portionsgröße: Maßeinheit," +
	"Brennwert [kcal],Brennwert [KJ],Brennwert: Messgenauigkeit,Brennwert [kj]: % RDA";

	private String columnNamesNutritions = "Nährwertangaben: Bestandteil [count],Nährwertangaben: Wert [count],Nährwertangaben: Maßeinheit [count],Nährwertangaben: Messgenauigkeit [count],Nährwertangaben: % RDA [count]";
	private String columnNameAlcohol = "percentageOfAlcoholByVolume [%]";
	private String columnNamesVitamins = "Vitamine / Mineralien: Code [count],Vitamine / Mineralien: Menge [count],Vitamine / Mineralien: Maßeinheit [count],Vitamine / Mineralien: Messgenauigkeit [count],Vitamine / Mineralien: % der Nährstoffbezugswerte [count]";
	private String columnNamesOther = "Sonstige Nährwertangaben: Bestandteil [0],Sonstige Nährwertangaben: Wert [0],Sonstige Nährwertangaben: Maßeinheit [0],Sonstige Nährwertangaben: Messgenauigkeit [0],Sonstige Nährwertangaben: % der Referenzmenge [0]";
	
	private CellStyle textCellStyle = null;
		
	
	public ArrayList<Product> transferDataFromSourceToModel(String sourceFile) throws IOException{
		ArrayList<Product> products = this.createModelFromSource(sourceFile);

		return products;
		/*for(Product prod : products){
			System.out.println(prod);
		}*/
	}

	private ArrayList<Product> createModelFromSource(String sourceFile) throws IOException{
		Workbook sourceWB = new XSSFWorkbook(sourceFile);
		Sheet sourceSheet = sourceWB.getSheetAt(0);
		DataFormatter df = new DataFormatter();
		ArrayList<Product> products = new ArrayList<Product>();

		for(Row row : sourceSheet){
			Product p = new Product();
			if(row.getRowNum() == 0){

			}else{
				p.setRowNum(row.getRowNum());
				p.setGtin(df.formatCellValue(row.getCell(COL_INDEX_GTIN)));
				//				p.setBadImages(Boolean.valueOf(df.formatCellValue(row.getCell(COL_INDEX_BAD_IMAGES))));
				//				p.setNoNutritionsListonProduct(Boolean.valueOf(df.formatCellValue(row.getCell(COL_INDEX_NO_NUTRITIONS_LIST_ON_PROD))));
				p.setTask_id(Long.valueOf(df.formatCellValue(row.getCell(COL_INDEX_TASK_ID))));
				p.setPrepState(df.formatCellValue(row.getCell(COL_INDEX_PREPSTATE)));
				p.setAlcohol(df.formatCellValue(row.getCell(COL_INDEX_ALOCOHL)));
				p.setKcal(df.formatCellValue(row.getCell(COL_INDEX_KCAL)));
				p.setKj(df.formatCellValue(row.getCell(COL_INDEX_KJL)));
				p.setBase(df.formatCellValue(row.getCell(COL_INDEX_BASE)));
				Nutrition nut = new Nutrition();

				//alcohol
				/*nut.setBase(df.formatCellValue(row.getCell(COL_INDEX_BASE)));
				nut.setName("percentageOfAlcoholByVolume [%]");
				nut.setValue(df.formatCellValue(row.getCell(COL_INDEX_ALOCOHL)));
				nut.setMultiValueNutrition(false);
				p.addNutrition(nut);*/

				//carbons
				nut = new Nutrition();
				nut.setBase(df.formatCellValue(row.getCell(COL_INDEX_BASE)));
				//nut.setName("Kohlenhydrate");
				//FD:
				nut.setName("CHOAVL");
				nut.setValue(df.formatCellValue(row.getCell(COL_INDEX_CARBONS)));
				p.addNutrition(nut);

				//carbons_alcohol
				nut = new Nutrition();
				nut.setBase(df.formatCellValue(row.getCell(COL_INDEX_BASE)));
				//nut.setName("Kohlenhydrate, davon mehrwertige Alkohole");
				//FD:
				nut.setName("POLYL");
				nut.setValue(df.formatCellValue(row.getCell(COL_INDEX_CARBONS_ALCOHOL)));
				p.addNutrition(nut);

				//carbons_starch
				nut = new Nutrition();
				nut.setBase(df.formatCellValue(row.getCell(COL_INDEX_BASE)));
				//nut.setName("Kohlenhydrate, davon Stärke");
				//FD:
				nut.setName("STARCH");
				nut.setValue(df.formatCellValue(row.getCell(COL_INDEX_CARBONS_STARCH)));
				p.addNutrition(nut);

				//fat
				nut = new Nutrition();
				nut.setBase(df.formatCellValue(row.getCell(COL_INDEX_BASE)));
				//nut.setName("Fett");
				//FD:
				nut.setName("FAT");
				nut.setValue(df.formatCellValue(row.getCell(COL_INDEX_FAT)));
				p.addNutrition(nut);

				//fiber
				nut = new Nutrition();
				nut.setBase(df.formatCellValue(row.getCell(COL_INDEX_BASE)));
				//nut.setName("Ballaststoffe");
				//FD:
				nut.setName("FIBTG");
				nut.setValue(df.formatCellValue(row.getCell(COL_INDEX_FIBER)));
				p.addNutrition(nut);

				//monounsatured_fat
				nut = new Nutrition();
				nut.setBase(df.formatCellValue(row.getCell(COL_INDEX_BASE)));
				//nut.setName("Fett, davon einfach ungesättigte Fettsäuren");
				//FD:
				nut.setName("FAMSCIS");
				nut.setValue(df.formatCellValue(row.getCell(COL_INDEX_MONOSATURED_FAT)));
				p.addNutrition(nut);

				//natrium
				nut = new Nutrition();
				nut.setBase(df.formatCellValue(row.getCell(COL_INDEX_BASE)));
				//nut.setName("Natrium");
				//FD:
				nut.setName("NA");
				nut.setValue(df.formatCellValue(row.getCell(COL_INDEX_NATRIUM)));
				nut.setMultiValueNutrition(false);
				nut.setType(NutritionType.OTHER);
				p.addNutrition(nut);

				//polyunsatured_fat
				nut = new Nutrition();
				nut.setBase(df.formatCellValue(row.getCell(COL_INDEX_BASE)));
				//nut.setName("Fett, davon mehrfach ungesättigte Fettsäuren");
				//FD:
				nut.setName("FAPUCIS");
				nut.setValue(df.formatCellValue(row.getCell(COL_INDEX_POLYUNSATURED_FAT)));
				p.addNutrition(nut);

				//proteins
				nut = new Nutrition();
				nut.setBase(df.formatCellValue(row.getCell(COL_INDEX_BASE)));
				//nut.setName("Eiweiß");
				//FD:
				nut.setName("PRO-");

				nut.setValue(df.formatCellValue(row.getCell(COL_INDEX_PROTEINS)));
				p.addNutrition(nut);

				//salt
				nut = new Nutrition();
				nut.setBase(df.formatCellValue(row.getCell(COL_INDEX_BASE)));
				//nut.setName("Salz");
				//FD:
				nut.setName("SALTEQ");
				nut.setValue(df.formatCellValue(row.getCell(COL_INDEX_SALT)));
				p.addNutrition(nut);

				//satured_fat
				nut = new Nutrition();
				nut.setBase(df.formatCellValue(row.getCell(COL_INDEX_BASE)));
				//nut.setName("Fett, davon gesättigte Fettsäuren");
				//FD:
				nut.setName("FASAT");

				nut.setValue(df.formatCellValue(row.getCell(COL_INDEX_SATURED_FAT)));
				p.addNutrition(nut);

				//sugar
				nut = new Nutrition();
				nut.setBase(df.formatCellValue(row.getCell(COL_INDEX_BASE)));
				//nut.setName("Kohlenhydrate, davon Zucker");
				//FD:
				nut.setName("SUGAR-");
				nut.setValue(df.formatCellValue(row.getCell(COL_INDEX_SUGAR)));
				p.addNutrition(nut);

				//collect product in ArrayList
				this.addVitaminsToProduct(sourceSheet, p);
				products.add(p);
			}
		}


		return products;
	}
	
	
	
	private Cell createTextCellWithValue(Row row, int colIndex, String value){
		Cell cell = row.createCell(colIndex);
		cell.setCellValue(value);
		cell.setCellStyle(textCellStyle);
		return cell;
	}
	
	public void writeModelToSheet(ArrayList<Product> products, String targetFile) throws IOException, Exception{
		Workbook targetWB = new XSSFWorkbook();
		this.textCellStyle = targetWB.createCellStyle();
		textCellStyle.setDataFormat((short)BuiltinFormats.getBuiltinFormat("text"));
		
		Sheet targetSheet = targetWB.createSheet();
		
		int rowIndex = 1;
		this.writeHeaderToTarget(targetSheet, products);

		for(Product prod : products){
			Row row = targetSheet.createRow(rowIndex);

			this.createTextCellWithValue(row, GTIN_DER_ARTIKELEINHEIT, prod.getGtin());
			

			if(prod.getPrepState().equals("unprepared")){
				this.createTextCellWithValue(row, NÄHRSTOFF_ZUBEREITUNGSGRAD, "UNPREPARED");
			}else if(prod.getPrepState().equals("prepared")){
				this.createTextCellWithValue(row, NÄHRSTOFF_ZUBEREITUNGSGRAD, "PREPARED");
			}else{
				this.createTextCellWithValue(row, NÄHRSTOFF_ZUBEREITUNGSGRAD, "");
			}
			
			this.createTextCellWithValue(row, BRENNWERT_KCAL, prod.getKcal().trim().equals("") ? "" : prod.getKcal().trim());
			this.createTextCellWithValue(row, BRENNWERT_KJ, prod.getKj().trim().equals("") ? "" : prod.getKj().trim());
			this.createTextCellWithValue(row, BRENNWERT_MESSGENAUIGKEIT, prod.getKj().trim().equals("") ? "" : prod.getKj().trim().indexOf("<") > -1 ?
					"LESS_THAN" :
			"APPROXIMATELY");
			this.createTextCellWithValue(row, BRENNWERT_KJ_PERC_RDA, "");
			this.createTextCellWithValue(row, PERCENTAGEOFALCOHOLBYVOLUME_PERC, prod.getAlcohol().trim().equals("") ? "" : prod.getAlcohol().trim());

			int columnStartIndexOfNextNutrition = NÄHRWERTANGABEN_BESTANDTEIL_0;
			int multiValueNutritionsCount = 0;
			//System.out.println("Prod :" + prod.getGtin() + " has " + prod.getNutritions().size() + " Nutritions.");
			for(Nutrition nutrition : prod.getNutritions()){
				if(nutrition.isMultiValueNutrition() && nutrition.getType() == NutritionType.NUTRITION){


					if(multiValueNutritionsCount < 1){ //needs to be done only one time per row
						this.createTextCellWithValue(row, PORTIONSGRÖßE, nutrition.getValue().equals("") ? "N/A" : "100");
					}


					if(multiValueNutritionsCount < 1){ //needs to be done only one time per row							
						this.createTextCellWithValue(row, PORTIONSGRÖßE_MAßEINHEIT, prod.getBase());
					}

					this.createTextCellWithValue(row, columnStartIndexOfNextNutrition, nutrition.getValue().equals("") ? "N/A" : nutrition.getName());

					this.createTextCellWithValue(row, columnStartIndexOfNextNutrition+1, nutrition.getValue().equals("") ? "N/A" : nutrition.getValue());
					this.createTextCellWithValue(row, columnStartIndexOfNextNutrition+2, nutrition.getValue().equals("") ? "N/A" : "GR");
					this.createTextCellWithValue(row, columnStartIndexOfNextNutrition+3, nutrition.getValue().equals("") ? "N/A" : nutrition.getValue().indexOf("<") > -1 ?
							"LESS_THAN" :
					"APPROXIMATELY");
					this.createTextCellWithValue(row, columnStartIndexOfNextNutrition+4, "");

					multiValueNutritionsCount++;
					columnStartIndexOfNextNutrition += 5;
				}else if(nutrition.getName().trim().equals("NA")){ //special treatment of natrium
					this.createTextCellWithValue(row, SONSTIGE_NÄHRWERTANGABEN_BESTANDTEIL_0, nutrition.getValue().trim().equals("") ? "N/A" : nutrition.getName().trim());
					this.createTextCellWithValue(row, SONSTIGE_NÄHRWERTANGABEN_WERT_0, nutrition.getValue().trim().equals("") ? "N/A" : nutrition.getValue().trim());
					this.createTextCellWithValue(row, SONSTIGE_NÄHRWERTANGABEN_MAßEINHEIT_0, nutrition.getValue().trim().equals("") ? "N/A" : "GR");
					this.createTextCellWithValue(row, SONSTIGE_NÄHRWERTANGABEN_MESSGENAUIGKEIT_0, nutrition.getValue().trim().equals("")  ? "N/A" : nutrition.getValue().indexOf("<") > -1 ?
							"LESS_THAN" :
					"APPROXIMATELY");
					this.createTextCellWithValue(row, SONSTIGE_NÄHRWERTANGABEN_PERC_DER_REFERENZMENGE_0, "");
				}else if(nutrition.getType() != NutritionType.VITAMIN){
					System.out.println("Skipped Nutrition: " + nutrition.getName());
					continue;
				}
			}
			rowIndex++;
			this.writeVitaminsToSheet(prod, targetSheet);
		}
		targetWB.write(new FileOutputStream(targetFile));
	}

	private void writeHeaderToTarget(Sheet targetSheet, ArrayList<Product> products) {
		Row row = targetSheet.createRow(0);
		int columnIndex = 0;
		for(String columnName : this.columnNamesCSV.split(",")){
			this.createTextCellWithValue(row, columnIndex, columnName);
			columnIndex++;
		}

		short maxNutritions = this.getMaxCountOfNutritions(products);
		
		String columnNames = "";
		for(int i = 0; i < maxNutritions; ++i){
			columnNames = columnNamesNutritions.replaceAll("count", i + "");
			for(String columnName : columnNames.split(",")){
				this.createTextCellWithValue(row, columnIndex, columnName);
				columnIndex++;
			}
		}
		PERCENTAGEOFALCOHOLBYVOLUME_PERC = columnIndex;
		this.createTextCellWithValue(row, PERCENTAGEOFALCOHOLBYVOLUME_PERC, columnNameAlcohol);
		columnIndex++;
		VITAMINE_MINERALIEN_CODE_0 = columnIndex;
		short maxVitamins = this.getMaxCountOfVitamins(products);
		String vitColumnNames = "";
		for(int i = 0; i < maxVitamins; ++i){
			vitColumnNames = columnNamesVitamins.replaceAll("count", i + "");
			for(String columnName : vitColumnNames.split(",")){
				this.createTextCellWithValue(row, columnIndex, columnName);
				columnIndex++;
			}
		}

		SONSTIGE_NÄHRWERTANGABEN_BESTANDTEIL_0 = columnIndex;
		SONSTIGE_NÄHRWERTANGABEN_WERT_0 = columnIndex+1;
		SONSTIGE_NÄHRWERTANGABEN_MAßEINHEIT_0 = columnIndex+2;
		SONSTIGE_NÄHRWERTANGABEN_MESSGENAUIGKEIT_0 = columnIndex+3;
		SONSTIGE_NÄHRWERTANGABEN_PERC_DER_REFERENZMENGE_0 = columnIndex+4;
		if(this.hasOther(products)){
			for(String columnName : columnNamesOther.split(",")){
				this.createTextCellWithValue(row, columnIndex, columnName);
				columnIndex++;
			}
		}
	}
	
	private boolean hasOther(ArrayList<Product> products){
		boolean ret = false;
		for(Product p : products){
			if(p.hasOther()){
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	private short getMaxCountOfVitamins(ArrayList<Product> products) {
		short count = 0;

		if(this.maxCountOfVitamins == 0){
			Product maxProd = null;
			for(Product prod : products){
				
				short countFromCurrentProduct = prod.getCountOfVitamins();
				if(count < countFromCurrentProduct){
					count = countFromCurrentProduct;
					maxProd = prod;
				}
			}
			this.maxCountOfVitamins = count;
			System.out.println("Maximum count of Vitamins is: " + count + " from Product in Row: " + (count > 0 ? maxProd.getRowNum()+1 : "N/A") );
		}else{
		}
		return this.maxCountOfVitamins;
	}

	/*private short getMaxCountOfVitamins() throws Exception{
		if(this.maxCountOfVitamins > 0){
			return this.maxCountOfVitamins;
		}else{
			System.out.println("WARNING: Maximum count of Vitamins is 0");
			return -1;
		}
	}*/
	private short getMaxCountOfNutritions(ArrayList<Product> products){
		short count = 0;

		if(this.maxCountOfNutritions == 0){
			Product maxProd = null;
			for(Product prod : products){
				
				short countFromCurrentProduct = prod.getCountOfNutritions();
				if(count < countFromCurrentProduct){
					count = countFromCurrentProduct;
					maxProd = prod;
				}
			}
			this.maxCountOfNutritions = count;
			System.out.println("Maximum count of Nutritions is: " + count + " from Product in Row: " + (maxProd.getRowNum()+1) + " and GTIN: " + maxProd.getGtin() );
		}else{			
		}

		return this.maxCountOfNutritions;

	}

	/*private short getMaxCountOfNutritions() throws Exception{
		if(this.maxCountOfNutritions > 0){
			return this.maxCountOfNutritions;
		}else{
			throw new Exception();
		}
	}*/

	private void writeVitaminsToSheet(Product product, Sheet sheet) throws IOException, Exception{

		int startIndexOfNextNutrition = VITAMINE_MINERALIEN_CODE_0;
		int countOfVitamins = 0;
		Row row = sheet.getRow(product.getRowNum());
		for(Nutrition nut : product.getNutritions()){

			if(nut.getType() == NutritionType.VITAMIN && nut.isMultiValueNutrition()){

				this.createTextCellWithValue(row, startIndexOfNextNutrition, nut.getValue().equals("") ? "N/A" : nut.getName());
				this.createTextCellWithValue(row, startIndexOfNextNutrition+1, nut.getValue().equals("") ? "N/A" : nut.getValue());
				this.createTextCellWithValue(row, startIndexOfNextNutrition+2, nut.getValue().equals("") ? "N/A" : nut.getBase()); //TODO: getBase need to map value to correct name
				this.createTextCellWithValue(row, startIndexOfNextNutrition+3, nut.getValue().trim().equals("")  ? "N/A" : nut.getValue().indexOf("<") > -1 ? "LESS_THAN" : "APPROXIMATELY");
				this.createTextCellWithValue(row, startIndexOfNextNutrition+4, "");
				startIndexOfNextNutrition += 5;
				countOfVitamins++;
			}
		}

		/*for(;countOfVitamins < this.getMaxCountOfVitamins();countOfVitamins++){
			this.createTextCellWithValue(row, startIndexOfNextNutrition).setCellValue("N/A");
			this.createTextCellWithValue(row, startIndexOfNextNutrition+1).setCellValue("N/A");
			this.createTextCellWithValue(row, startIndexOfNextNutrition+2).setCellValue("N/A");
			this.createTextCellWithValue(row, startIndexOfNextNutrition+3).setCellValue("N/A");
			this.createTextCellWithValue(row, startIndexOfNextNutrition+4).setCellValue("N/A");
			startIndexOfNextNutrition += 5;
			//countOfVitamins++;
		}*/


	}

	private void addVitaminsToProduct(Sheet sheet, Product product) throws IOException {
		DataFormatter df = new DataFormatter();

		//column indexes from source sheet - which is the export from clickworker
		int INDEX_BIOTIN = 25;
		int INDEX_BIOTIN_MEASUREMENT = 26;
		
		int INDEX_CALCIUM = 27;
		int INDEX_CALCIUM_MEASUREMENT = 28;
		
		int INDEX_CHLOR = 29;
		int INDEX_CHLOR_MEASUREMENT = 30;
		
		int INDEX_KUPFER = 31;
		int INDEX_KUPFER_MEASUREMENT = 32;
		
		int INDEX_FLUOR = 33;
		int INDEX_FLUOR_MEASUREMENT = 34;
		
		int INDEX_FOLSAEURE = 35;
		int INDEX_FOLSAEURE_MEASUREMENT = 36;
		
		int INDEX_JOD = 37;
		int INDEX_JOD_MEASUREMENT = 38;
		
		int INDEX_EISEN = 39;
		int INDEX_EISEN_MEASUREMENT = 40;
		
		int INDEX_MAGNESIUM = 41;
		int INDEX_MAGNESIUM_MEASUREMENT = 42;
		
		int INDEX_MANGAN = 43;
		int INDEX_MANGAN_MEASUREMENT = 44;
		
		int INDEX_NIACIN = 45;
		int INDEX_NIACIN_MEASUREMENT = 46;
		
		int INDEX_PANTOTHENSÄURE = 47;
		int INDEX_PANTOTHENSÄURE_MEASUREMENT = 48;
		
		int INDEX_PHOSPHOR = 49;
		int INDEX_PHOSPHOR_MEASUREMENT = 50;
		
		int INDEX_KALIUM = 51;
		int INDEX_KALIUM_MEASUREMENT = 52;
		
		int INDEX_SELEN = 53;
		int INDEX_SELEN_MEASUREMENT = 54;
		
		int INDEX_A = 55;
		int INDEX_A_MEASUREMENT = 56;
		
		int INDEX_B1 = 57;
		int INDEX_B1_MEASUREMENT = 58;
		
		int INDEX_B12 = 59;
		int INDEX_B12_MEASUREMENT = 60;
		
		int INDEX_B2 = 61;
		int INDEX_B2_MEASUREMENT = 62;
		
		int INDEX_B6 = 63;
		int INDEX_B6_MEASUREMENT = 64;
		
		int INDEX_C 	= 65;
		int INDEX_C_MEASUREMENT = 66;
		
		int INDEX_D = 67;
		int INDEX_D_MEASUREMENT = 68;
		
		int INDEX_E = 69;
		int INDEX_E_MEASUREMENT = 70;
		
		int INDEX_K = 71;
		int INDEX_K_MEASUREMENT = 72;
		
		int INDEX_ZINK = 73;
		int INDEX_ZINK_MEASUREMENT = 74;

		Row row = sheet.getRow(product.getRowNum());	

		//				p.setGtin();
		//				p.setBadImages(Boolean.valueOf(df.formatCellValue(row.getCell(INDEX_BAD_IMAGES))));
		//				p.setNoNutritionsListonProduct(Boolean.valueOf(df.formatCellValue(row.getCell(NO_NUTRTIONS))));
		//				p.setTask_id(Long.valueOf(df.formatCellValue(row.getCell(INDEX_TASK_ID))));

		Nutrition nut = new Nutrition();

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_BIOTIN_MEASUREMENT)));
		nut.setName("BIOT");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_BIOTIN)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_CALCIUM_MEASUREMENT)));
		nut.setName("CA");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_CALCIUM)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_CHLOR_MEASUREMENT)));
		nut.setName("CLD"); //FD?
		nut.setValue(df.formatCellValue(row.getCell(INDEX_CHLOR)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_EISEN_MEASUREMENT)));
		nut.setName("FE");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_EISEN)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_FLUOR_MEASUREMENT)));
		nut.setName("FD"); //FD nur Fluorid gefunden
		nut.setValue(df.formatCellValue(row.getCell(INDEX_FLUOR)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_FOLSAEURE_MEASUREMENT)));
		nut.setName("FOLDFE");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_FOLSAEURE)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_JOD_MEASUREMENT)));
		nut.setName("ID");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_JOD)));
		//nut.setMultiValueNutrition(false);
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_KALIUM_MEASUREMENT)));
		nut.setName("K");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_KALIUM)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_KUPFER_MEASUREMENT)));
		nut.setName("CU"); //FD: nicht gefunden
		nut.setValue(df.formatCellValue(row.getCell(INDEX_KUPFER)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_MAGNESIUM_MEASUREMENT)));
		nut.setName("MG");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_MAGNESIUM)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_MANGAN_MEASUREMENT)));
		nut.setName("MN");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_MANGAN)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_NIACIN_MEASUREMENT)));
		nut.setName("NIA");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_NIACIN)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_PANTOTHENSÄURE_MEASUREMENT)));
		nut.setName("PANTAC");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_PANTOTHENSÄURE)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_PHOSPHOR_MEASUREMENT)));
		nut.setName("P");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_PHOSPHOR)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_SELEN_MEASUREMENT)));
		nut.setName("SE");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_SELEN)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_ZINK_MEASUREMENT)));
		nut.setName("ZN");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_ZINK)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_B1_MEASUREMENT)));
		nut.setName("THIA");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_B1)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_B2_MEASUREMENT)));
		nut.setName("RIBF");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_B2)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_A_MEASUREMENT)));
		nut.setName("VITA-");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_A)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_B12_MEASUREMENT)));
		nut.setName("VITB12");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_B12)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_B6_MEASUREMENT)));
		nut.setName("VITB6-");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_B6)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_C_MEASUREMENT)));
		nut.setName("VITC-");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_C)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_D_MEASUREMENT)));
		nut.setName("VITD-");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_D)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_E_MEASUREMENT)));
		nut.setName("VITE-");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_E)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

		nut = new Nutrition();
		nut.setBase(df.formatCellValue(row.getCell(INDEX_K_MEASUREMENT)));
		nut.setName("VITK");
		nut.setValue(df.formatCellValue(row.getCell(INDEX_K)));
		nut.setType(NutritionType.VITAMIN);
		product.addNutrition(nut);

	}
}