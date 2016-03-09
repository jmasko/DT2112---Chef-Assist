package iristk.app.chefassist;

public class DirectionParser {
	private String[] words;
	private double amountCache = 1;
	
	public DirectionParser(String direction){
		words = direction.replace(".", "").replace(",", "").replace("?", "").replace("!","").replace(";","").replace(":","").split(" ");
	}
	
	public boolean hasIngredient(Ingredient ing){
		for(int i = 0; i < words.length; i++){
			if(words.equals(ing.getName())){
				cacheAmount(i, ing);
				return true;
			}
		}
		return false;
	}
	
	private void cacheAmount(int currentIndex, Ingredient ing){
		int wordCheck = currentIndex-1;
		if(wordCheck < 0)
			return;
		
		if(words[wordCheck].equalsIgnoreCase("the")){
			wordCheck--;	
			if(wordCheck < 0)
				return;
		}
		
		if(words[wordCheck].equalsIgnoreCase("of")){
			wordCheck--;
			if(wordCheck < 0)
				return;
			
			if(words[wordCheck].equalsIgnoreCase("half")){
				amountCache = 0.5d;
			} else if(words[wordCheck].equalsIgnoreCase("quarter")){
				amountCache = 0.25d;
			} else if(words[wordCheck].equalsIgnoreCase("one")){
				amountCache = 1d / ing.getAmount();
			} else if(words[wordCheck].equalsIgnoreCase("two")){
				amountCache = 2d / ing.getAmount();
			} else if(words[wordCheck].equalsIgnoreCase("three")){
				amountCache = 3d / ing.getAmount();
			}else if(words[wordCheck].equalsIgnoreCase("four")){
				amountCache = 4d / ing.getAmount();
			}else if(words[wordCheck].equalsIgnoreCase("five")){
				amountCache = 5d / ing.getAmount();
			}else if(words[wordCheck].equalsIgnoreCase("six")){
				amountCache = 6d / ing.getAmount();
			}else if(words[wordCheck].equalsIgnoreCase("seven")){
				amountCache = 7d / ing.getAmount();
			}
			//And so on... sufficient for demo
		}
		
		amountCache = 1;
	}
	
	/**
	 * Returns the percentage amount (between 0.0 and 1.0) of the last FOUND ingredient from hasIngredient. Returns 1 by default.
	 * @return	a number between 0.0 and 1.0.
	 */
	public double getPercentageAmount(){
		return amountCache;
	}
}
