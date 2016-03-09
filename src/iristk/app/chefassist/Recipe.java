package iristk.app.chefassist;

import java.util.ArrayList;
import java.util.Arrays;

public class Recipe {
	private int step = 0;
	private String name;
	private String[] directions;
	private Ingredient[] ingredients;
	private ArrayList<ArrayList<Ingredient>> ingredientsPerStepLists; 
	
	public Recipe(String name, String dir[], Ingredient[] ing){
		this.name = name;
		directions = Arrays.copyOf(dir, dir.length);
		ingredients = Arrays.copyOf(ing, ing.length);
		buildRecipe();
	}
	
	public String getName(){
		return name;
	}
	
	private void buildRecipe(){
		ingredientsPerStepLists = new ArrayList<ArrayList<Ingredient>>();
		for(int i = 0; i < directions.length; i++){
			ArrayList<Ingredient> ingredientList = new ArrayList<Ingredient>();
			DirectionParser d = new DirectionParser(directions[i]);
			for(int k = 0; k < ingredients.length; k++){
				if(d.hasIngredient(ingredients[k])){
					ingredientList.add(new Ingredient(ingredients[k], d.getPercentageAmount()));
				}
			}
			ingredientsPerStepLists.add(i, ingredientList);
		}
	}
}
