package game.slidepuzzle.util;

public class MillisecondsToMinSec {

	public static String parseMilliSeconds(long millis) {
		long seconds = (millis / 1000) % 60;
		long minutes = millis / 60000;
		String minutesString = "";
		String secondsString = "";
		
		if (minutes < 10) {
			minutesString = "0" + minutes;
		} else {
			minutesString = minutes + "";
		}
		if (seconds < 10) {
			secondsString = "0" + seconds;
		} else {
			secondsString = seconds + "";
		}
		
		return minutesString + ":" + secondsString;
	}
}
