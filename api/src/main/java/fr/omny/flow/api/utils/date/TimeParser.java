package fr.omny.flow.api.utils.date;

public class TimeParser {

	public static long toMillis(String string) {
		return Time.TO_MILLIS(Time.stringToTime(string));
	}

	public enum Time {

		YEAR("y", 365 * 3600 * 24), MONTH("m", 30 * 3600 * 24), WEEK("w", 7 * 3600 * 24), DAY("d", 3600 * 24),
		HOUR("h", 3600), MINUTES("M", 60), SECONDS("s", 1);

		private long timeInSec;
		private String charToGet;

		Time(String tag, long valueAsSecond) {
			this.charToGet = tag;
			this.timeInSec = valueAsSecond;
		}

		public static long stringToTime(String s) {
			long Return = -1;
			int previousCharIndex = 0;
			for (Time t : Time.values()) {
				if (s.contains(t.getCharToGet())) {
					String ss = s.substring(previousCharIndex, s.indexOf(t.getCharToGet()));
					previousCharIndex = s.indexOf(t.getCharToGet()) + 1;
					Return = Return + (Integer.parseInt(ss) * t.getTimeInSec());
				}
			}
			return Return;
		}

		public static long TO_MILLIS(long l) {
			return l * 1000;
		}

		public static long TO_SECOND(long l) {
			return l / 1000;
		}

		public long getTimeInSec() {
			return timeInSec;
		}

		public void setTimeInSec(long timeInSec) {
			this.timeInSec = timeInSec;
		}

		public String getCharToGet() {
			return charToGet;
		}

		public void setCharToGet(String charToGet) {
			this.charToGet = charToGet;
		}

	}

}
