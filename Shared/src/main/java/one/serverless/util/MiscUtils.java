/*    _____                           __                  ____           
 *   / ___/___  ______   _____  _____/ /__  __________   / __ \____  ___ 
 *   \__ \/ _ \/ ___/ | / / _ \/ ___/ / _ \/ ___/ ___/  / / / / __ \/ _ \
 *  ___/ /  __/ /   | |/ /  __/ /  / /  __(__  |__  )  / /_/ / / / /  __/
 * /____/\___/_/    |___/\___/_/  /_/\___/____/____/   \____/_/ /_/\___/
 */

package one.serverless.util;

import java.util.Objects;
import java.util.stream.Stream;

/**
 *
 * @author avk
 */
public class MiscUtils {

	/**
	 * Retrieves property or environment variable value for the name supplied
	 * @return property or environment variable value or null if neither is present
	 */
	public static String getPropOrEnv(String name) {
		return Stream
			.of(
				System.getProperty(name),
				System.getenv(name))
			.filter(Objects::nonNull)
			.findFirst()
			.orElse(null);
	}
}
