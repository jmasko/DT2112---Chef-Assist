package iristk.app.chefassist;

public class Ingredient {
	private String name, unit, modifier;
	private double amount;
	
	public Ingredient(Ingredient i){
		this(i.getName(),  i.getModifier(), i.getAmount(), i.getUnit());
	}
	
	public Ingredient(Ingredient i, double percentage){
		this(i.getName(), i.getModifier(), i.getAmount() * percentage, i.getUnit());
	}
	
	public Ingredient(String name, double amount){
		this(name, "", amount, "");
	}
	
	public Ingredient(String name, double amount, String unit){
		this(name, "", amount, unit);
	}
	
	public Ingredient(String name, String modifier, double amount, String unit){
		this.name = name;
		this.unit = unit;
		this.amount = amount;
		this.modifier = modifier;
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

	public String getModifier() {
		return modifier;
	}
	
	
}
