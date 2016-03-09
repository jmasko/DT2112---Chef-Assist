package iristk.app.chefassist;

public class Ingredient {
	private String name, unit;
	private double amount;
	
	public Ingredient(Ingredient i){
		this(i.getName(), i.getUnit(), i.getAmount());
	}
	
	public Ingredient(Ingredient i, double percentage){
		this(i.getName(), i.getUnit(), i.getAmount() * percentage);
	}
	
	public Ingredient(String name, double amount){
		this(name, "", amount);
	}
	
	public Ingredient(String name, String unit, double amount){
		this.name = name;
		this.unit = unit;
		this.amount = amount;
	}

	public String getName() {
		return name;
	}

	public String getUnit() {
		return unit;
	}
	
	public String getIngredientAmount(){
		return unit.length() > 0 ? amount + " " + unit : amount + "";
	}
	
	public double getAmount(){
		return amount;
	}
}
