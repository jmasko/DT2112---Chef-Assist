package iristk.app.chefassist;

import iristk.system.SimpleDialogSystem;

public class ChefController {
	private SimpleDialogSystem system;
	private Recipe[] recipes = buildTemporaryHardcodedRecipes();
	private Recipe currentRecipe;
	
	public ChefController(SimpleDialogSystem system){
		this.system = system;
		currentRecipe = recipes[0];
	}
	
	public Recipe getCurrentRecipe(){
		return currentRecipe;
	}
	
	private Recipe[] buildTemporaryHardcodedRecipes(){
		Recipe[] r = new Recipe[1];
		
		//Test Recipe
		Ingredient[] ingredients = new Ingredient[]{
			new Ingredient("cream cheese", "softened", 250, "g"),
			new Ingredient("milk", 250, "ml"),
			new Ingredient("butter", "softened", 60, "g"),
			new Ingredient("egg yolks", 6),
			new Ingredient("cake flour", 55, "g"),
			new Ingredient("corn flour", 20, "g"),
			new Ingredient("lemon zest", 1),
			new Ingredient("egg whites", 6),
			new Ingredient("cream of tartar", 0.25, "tsp"),
			new Ingredient("sugar", 130, "g"),
		};
		String[] directions = new String[]{
				"Preheat oven to 150 degrees celcius",
				"Use a large bowl, pour in milk. Place the bowl over simmering water. Don’t let the bottom of the bowl touch the water. ",
				"Add cream cheese, stir occasionally until completely dissolved and the mixture turns smooth. Stir in butter, till dissolved. ",
				"Remove the mixture from heat and let it cool down a bit, then add the egg yolks and mix well.",
				"Mix the cake flour and corn flour. Sift in the flours into the cream cheese mixture, a small amount at a time. Mix well between every addition, and make sure there aren’t any flour lumps. Stir in freshly grated zest. Set aside.",
				"Place egg whites in a large clean bowl. Use an electric mixer to beat the egg whites for 3 minutes, then add cream of tartar and blend again. Pour sugar in the egg whites and blend until very the mixture becomes half solid. ",
				"Add the egg whites into the cream cheese mixture gently with a rubber spatula just until all ingredients are mixed well. Do not stir or beat. ",
				"Pour the mixture into the two baking pans. Place the pans into another larger baking tray. Add hot water in the tray up to halfway and bake for about 50 to 60 minutes. Test with a needle or skewer that comes out clean.",
				"Turn off the oven. Leave the oven door slightly open for 10 minutes. Remove from the oven and remove from the pans. Let cool completely on a wire rack. Chill in a fridge for about 3 hours then the cake will be ready to be served."
		};
		r[1] = new Recipe("Manly Meetballs", directions, ingredients);
		
		return r;
	}
	
}
