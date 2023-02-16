package fr.omny.flow.plugins;

import java.util.Optional;

/**
 * Environment helper class
 */
public final class Env {
	
	public static final String ENVIRONMENT = "FLOW_ENV";
	public static final String SERVER_NAME = "FLOW_SERVER_NAME";
	public static final String DATABASE_NAME = "FLOW_DATABASE_NAME";

	/**
	 * 
	 * @return Server name
	 */
	public static String getServerName(){
		return Env.get(Env.SERVER_NAME, "server");
	}

	/**
	 * 
	 * @return Env type, "DEVELOPMENT" | "PRODUCTION"
	 */
	public static String getEnvType(){
		return Env.get(Env.ENVIRONMENT, "DEVELOPMENT");
	}

	/**
	 * Get environment variable
	 * @param key
	 * @return
	 */
	public static Optional<String> get(String key){
		return Optional.ofNullable(System.getenv(key));
	}

	/**
	 * Get environment variable
	 * @param key
	 * @param fallBack
	 * @return
	 */
	public static String get(String key, String fallBack){
		return Optional.ofNullable(System.getenv(key)).orElse(fallBack);
	}

	private Env(){}

}
