/*    _____                           __                  ____           
 *   / ___/___  ______   _____  _____/ /__  __________   / __ \____  ___ 
 *   \__ \/ _ \/ ___/ | / / _ \/ ___/ / _ \/ ___/ ___/  / / / / __ \/ _ \
 *  ___/ /  __/ /   | |/ /  __/ /  / /  __(__  |__  )  / /_/ / / / /  __/
 * /____/\___/_/    |___/\___/_/  /_/\___/____/____/   \____/_/ /_/\___/
 */

package one.serverless.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author akrymskiy
 */
public class CollectionUtils {
    @SafeVarargs
    public static <T, U> Map<T, U> pairsToMap(Pair<T, U>... pairs) {
        return Arrays
			.stream(pairs)
			.collect(HashMap::new, (m, x) -> m.put(x.getKey(), x.getValue()), HashMap::putAll);
    }
}