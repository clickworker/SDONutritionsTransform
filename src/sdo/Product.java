package sdo;

import java.util.ArrayList;
import java.util.Collections;

public class Product {
	private ArrayList<Nutrition> nutritions = null;
	private String gtin = null;
	private Long task_id = null;
	private boolean badImages = false;
	private boolean noNutritionsListonProduct = false;
	private String prepState = "";
	private String kj = "";
	private String kcal = "";
	private String alcohol = "";
	private int rowNum = 0;
	private String base = "";
	
	public String getGtin() {
		return gtin;
	}
	public void setGtin(String gtin) {
		this.gtin = gtin;
	}
	public Long getTask_id() {
		return task_id;
	}
	public void setTask_id(Long task_id) {
		this.task_id = task_id;
	}
	public boolean isBadImages() {
		return badImages;
	}
	public void setBadImages(boolean badImages) {
		this.badImages = badImages;
	}
	public boolean isNoNutritionsListonProduct() {
		return noNutritionsListonProduct;
	}
	public void setNoNutritionsListonProduct(boolean noNutritionsListonProduct) {
		this.noNutritionsListonProduct = noNutritionsListonProduct;
	}
	public ArrayList<Nutrition> getNutritions() {
		Collections.sort(this.nutritions);
		return this.nutritions;
	}
	
	public boolean addNutrition(Nutrition nutrition){
		if(this.nutritions == null){
			this.nutritions = new ArrayList<Nutrition>();
		}
		
		if(!nutrition.getValue().equals("")){
			return this.nutritions.add(nutrition);
		}else{
			return false;
		}
		
		
	}
	
	@Override
	public String toString() {
		return "Product [nutritions=" + this.getNutritions() + ", gtin=" + gtin
				+ ", badImages=" + badImages + ", noNutritionsListonProduct="
				+ noNutritionsListonProduct + "]";
	}
	public String getPrepState() {
		return prepState;
	}
	public void setPrepState(String prepState) {
		this.prepState = prepState;
	}
	public String getKj() {
		return kj;
	}
	public void setKj(String kj) {
		this.kj = kj;
	}
	public String getKcal() {
		return kcal;
	}
	public void setKcal(String kcal) {
		this.kcal = kcal;
	}
	public String getAlcohol() {
		return alcohol;
	}
	public void setAlcohol(String alcohol) {
		this.alcohol = alcohol;
	}
	public short getCountOfNutritions(){
		short count = 0;
		
		for(Nutrition nut : this.nutritions){
			if(nut.getType() == NutritionType.NUTRITION && nut.isMultiValueNutrition()){
				count++;
			}
		}
		
		return count;
	}
	public short getCountOfVitamins(){
		short count = 0;
		
		for(Nutrition nut : this.nutritions){
			if(nut.getType() == NutritionType.VITAMIN && nut.isMultiValueNutrition()){
				count++;
			}
		}
		
		return count;
	}
	
	public boolean hasOther(){
		boolean ret = false;
		
		for(Nutrition nut : this.nutritions){
			if(nut.getType() == NutritionType.OTHER){
				ret = true;
				break;
			}
		}
		
		return ret;
	}
	
	public int getRowNum() {
		return rowNum;
	}
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}
	public String getBase() {
		String ret = this.base;
		
		//Änderungen der Codewerte von FD
		if(ret.equals("l")){
			//ret = "Liter";
			ret = "LT";
		}else if(ret.equals("µg")){
			//ret = "Mikrogramm";
			ret = "MC";
		}else if(ret.equals("mg")){
			//ret = "Milligramm";
			ret = "ME"; 
		}else if(ret.equals("ml")){
			//ret = "Milliliter";
			ret = "ML";
		}else if(ret.equals("mm")){
			//ret = "Millimeter";
			ret = "MM";
		}else if(ret.equals("kg")){
			//ret = "Kilogramm";
			ret = "KG";
		}else if(ret.equals("g")){
			//ret = "Gramm";
			ret = "GR";
		}else{
			System.out.println("No service_size_uom Mapping found for Product " + this.getGtin());
		}
		
		return ret;
	}
	public void setBase(String base) {
		this.base = base;
	}
}
