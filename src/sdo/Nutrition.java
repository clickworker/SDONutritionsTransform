package sdo;

public class Nutrition implements Comparable<Nutrition>{
	private String base = "";
	private String name = "";
	private String value = "";
	private boolean multiValueNutrition = true;
	private NutritionType type = NutritionType.NUTRITION;
	
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
			System.out.println("No measurement Mapping found for " + this.getName() + " for Measurement " + this.base + " for Value " + this.getValue());
		}
		
		return ret;
	}
	public void setBase(String base) {
		this.base = base;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value.replaceAll("<", "");
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public boolean isLessThan(){
		return this.value.trim().indexOf("<") > -1;
	}
	
	@Override
	public String toString() {
		return "Nutrition [base=" + base + ", name=" + name + ", value="
				+ value + "]";
	}
	@Override
	public int compareTo(Nutrition o) {
		int ret = 0;
		if(o.getValue().equals("") && !this.getValue().equals("")){
			ret = -1;
		}else if(!o.getValue().equals("") && this.getValue().equals("")){
			ret = 1;
		}else if(o.getValue().equals("0") && !this.getValue().equals("0")){
			ret = -1;
		}else if(!o.getValue().equals("0") && this.getValue().equals("0")){
			ret = 1;
		}else{
			return this.getName().compareTo(o.getName());
		}
		return ret;
	}
	public boolean isMultiValueNutrition() {
		return multiValueNutrition;
	}
	public void setMultiValueNutrition(boolean multiValueNutrition) {
		this.multiValueNutrition = multiValueNutrition;
	}
	public NutritionType getType() {
		return type;
	}
	public void setType(NutritionType type) {
		this.type = type;
	}

}
