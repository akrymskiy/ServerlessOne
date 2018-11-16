/*    _____                           __                  ____           
 *   / ___/___  ______   _____  _____/ /__  __________   / __ \____  ___ 
 *   \__ \/ _ \/ ___/ | / / _ \/ ___/ / _ \/ ___/ ___/  / / / / __ \/ _ \
 *  ___/ /  __/ /   | |/ /  __/ /  / /  __(__  |__  )  / /_/ / / / /  __/
 * /____/\___/_/    |___/\___/_/  /_/\___/____/____/   \____/_/ /_/\___/
 */

package one.serverless.util.aws;

import java.time.format.DateTimeFormatter;

/**
 *
 * @author akrymskiy
 */
public class Athena {
	//DATE, in the UNIX format, such as YYYY-MM-DD. 
//TIMESTAMP. Instant in time and date in the UNiX format, such as yyyy-mm-dd hh:mm:ss[.f...]. For example, TIMESTAMP '2008-09-15 03:04:05.324'. This 
	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
	public static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
}
