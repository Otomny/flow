package fr.omny.flow.api.utils;

public final class Converter {
	
	private Converter(){}

	public static long tickToMillis(long tick){
		return tick * 50;
	}

}
