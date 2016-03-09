package iristk.app.chefassist;

import iristk.situated.SituatedDialogSystem;

public class ChefController {
	private SituatedDialogSystem system;
	private Recipe[] recipes = buildTemporaryHardcodedRecipes();
	private Recipe currentRecipe;
	
	public ChefController(SituatedDialogSystem system){
		this.system = system;
		currentRecipe = recipes[0];
	}
	
	public Recipe getCurrentRecipe(){
		return currentRecipe;
	}
	
	private Recipe[] buildTemporaryHardcodedRecipes(){
		Recipe[] r = new Recipe[2];
		
		//Pancakes
		Ingredient[] ingredients = new Ingredient[]{
			new Ingredient("Milk", "dl", 6.0),
			new Ingredient("Salt", "tsp", 0.5),
			new Ingredient("Flour", "dl", 2.5),
			new Ingredient("Eggs", 3.0),
			new Ingredient("Butter, melted", "g", 50),
		};
		String[]  directions = new String[]{
			"Mix salt and flour in a bowel",
			"Add half of the milk. Mix it together",
			"Add the other half of the milk. Mix it together.",
			"Add the eggs one at the time.",
			"Add the butter to the mix.",
			"Pour about 0.5 declilitres of the pancake mix on a medium high heated frying pan. Fry about 1 minute on each side. Repeat until finished.",
		};
		r[0] = new Recipe("Pancakes", directions, ingredients);
		
		//Manly Meatballs
		ingredients = new Ingredient[]{
			new Ingredient("Ground Beef", "kg", 0.5),
			new Ingredient("Breadcrumbs", "dl", 0.5),
			new Ingredient("Milk", "dl", 1),
			new Ingredient("chopped onion", "tbsp", 2),
			new Ingredient("Egg", "", 1),
			new Ingredient("Salt", "tsp", 1),
			new Ingredient("Pepper", "ml", 1),
		};
		directions = new String[]{
				"In a manly bowel; mix breadcrums and milk. Let it soak for 10 minutes or until it grows a beard",
				"Put in the ground beef, salt, pepper, egg and chipped onion. Mix using your fist",
				"When it starts to fight back, you can form meatballs. A true man makes at most 1 ball",
				"Cook over open fire, holding them with your hands.",
				};
		r[1] = new Recipe("Manly Meetballs", directions, ingredients);
		
		return r;
	}
	
}
