package iristk.app.chefassist;

import java.util.HashMap;

public class UnitTranslator {
	
	private static final HashMap<String, String> map = buildMap();
	
	private static final HashMap<String, String> buildMap(){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("g", "grams");
		map.put("kg", "kilograms");
		map.put("dl", "decilitres");
		map.put("ml", "millilitres");
		map.put("tsp", "teaspoon");
		map.put("tbsp", "tablespoon");
		return map;
	}
	
	public static String translate(String s){
		if(!map.containsKey(s)){ return s;}
		return map.get(s);
	}
}
