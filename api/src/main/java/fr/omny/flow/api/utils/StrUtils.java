package fr.omny.flow.api.utils;


import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class StrUtils {

	private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static SecureRandom rnd = null;

	private StrUtils() {}

	/**
	 * Transform from pascale case to snake case
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

	static {
		try {
			rnd = SecureRandom.getInstance("SHA1PRNG");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Replace the last occurence of something
	 * @param text
	 * @param regex
	 * @param replacement
	 * @return
	 */
	public static String replaceLast(String text, String regex, String replacement) {
		return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
	}

	/**
	 * From alphabet index to String
	 * @param code
	 * @return
	 */
	public static String alphabet(int code) {
		String text = "";
		int currentIndex = 0;
		int textLength = code / 26 + 1;
		do {
			int maxRemove = Math.min(26, code);
			text += "" + (char) ((maxRemove % 26) + 'a');
			code -= maxRemove;
			currentIndex++;
			if (currentIndex == textLength)
				break;
		} while (true);

		return text.toUpperCase();
	}

	/**
	 * Random string generator
	 * @param len
	 * @return
	 */
	public static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}

	/**
	 * Capitalize string
	 * @param string
	 * @return
	 */
	public static String capitalize(String string) {
		if (string.length() < 0)
			return string;
		return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
	}

	/**
	 * Clamp
	 * @param string
	 * @param maxSize
	 * @return
	 */
	public static String clamp(String string, int maxSize) {
		int max = Math.min(string.length(), maxSize);
		return string.substring(0, max);
	}

}
