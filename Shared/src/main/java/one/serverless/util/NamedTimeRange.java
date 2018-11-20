/*    _____                           __                  ____           
 *   / ___/___  ______   _____  _____/ /__  __________   / __ \____  ___ 
 *   \__ \/ _ \/ ___/ | / / _ \/ ___/ / _ \/ ___/ ___/  / / / / __ \/ _ \
 *  ___/ /  __/ /   | |/ /  __/ /  / /  __(__  |__  )  / /_/ / / / /  __/
 * /____/\___/_/    |___/\___/_/  /_/\___/____/____/   \____/_/ /_/\___/
 */

package one.serverless.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author akrymskiy
 */
public enum NamedTimeRange {
	CUSTOM {
		@Override
		public Pair<LocalDateTime, LocalDateTime> getTimeRange() {
			throw new UnsupportedOperationException("Cannot get range for custom");
		}
	},
	LAST_HOUR {
		@Override
		public Pair<LocalDateTime, LocalDateTime> getTimeRange() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
	},
	LAST_3_HOURS {
		@Override
		public Pair<LocalDateTime, LocalDateTime> getTimeRange() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
	},
	YESTERDAY {
		@Override
		public Pair<LocalDateTime, LocalDateTime> getTimeRange() {
			return 
				Optional
					.of(LocalDate.now().atStartOfDay())
					.map(x -> Pair.of(x.minusDays(1), x))
					.get();
		}
	},
	DAY_BEFORE_YESTERDAY {
		@Override
		public Pair<LocalDateTime, LocalDateTime> getTimeRange() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
	},
	LAST_WEEK {
		@Override
		public Pair<LocalDateTime, LocalDateTime> getTimeRange() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
	},
	LAST_2_WEEKS {
		@Override
		public Pair<LocalDateTime, LocalDateTime> getTimeRange() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
	},
	LAST_MONTH {
		@Override
		public Pair<LocalDateTime, LocalDateTime> getTimeRange() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
	},
	NEXT_MONTH {
		@Override
		public Pair<LocalDateTime, LocalDateTime> getTimeRange() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
	},
	TODAY {
		@Override
		public Pair<LocalDateTime, LocalDateTime> getTimeRange() {
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
	};

	public abstract Pair<LocalDateTime, LocalDateTime> getTimeRange();
};