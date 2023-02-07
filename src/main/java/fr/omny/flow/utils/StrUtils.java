package fr.omny.flow.utils;

public class StrUtils {

	private StrUtils() {}

	/**
	 * 
	 * @param pascalCase
	 * @return
	 */
	public static String toSnakeCase(String pascalCase) {
		StringBuilder snakeCase = new StringBuilder();
		for (int i = 0; i < pascalCase.length(); i++) {
			char c = pascalCase.charAt(i);
			if (Character.isUpperCase(c)) {
				if (i > 0) {
					snakeCase.append("_");
				}
				snakeCase.append(Character.toLowerCase(c));
			} else {
				snakeCase.append(c);
			}
		}
		return snakeCase.toString();
	}

}
