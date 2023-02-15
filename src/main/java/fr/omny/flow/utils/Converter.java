package fr.omny.flow.utils;

public final class Converter {
	
	private Converter(){}

	public static long tickToMillis(long tick){
		return tick * 50;
	}

}
