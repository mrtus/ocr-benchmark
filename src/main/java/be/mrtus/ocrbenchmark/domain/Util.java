package be.mrtus.ocrbenchmark.domain;

import java.time.Duration;
import java.util.stream.IntStream;

public class Util {

	private Util() {
	}

	public static String durationToString(long time) {
		Duration duration = Duration.ofMillis(time);

		long hours = duration.toHours();

		Duration minutesDuration = duration.minusHours(hours);
		long minutes = minutesDuration.toMinutes();

		Duration secondsDuration = minutesDuration.minusMinutes(minutes);
		long seconds = secondsDuration.getSeconds();

		Duration milliesDuratoin = secondsDuration.minusSeconds(seconds);
		long millies = milliesDuratoin.toMillis();

		String durationString = hours + "h "
								+ minutes + "m "
								+ seconds + "s "
								+ millies + "ms";

		return durationString;
	}

	public static int calculateLevenshteinDistance(String target, String result) {
		int resultLength = result.length();
		int targetLenth = target.length();

		if(resultLength == 0) {
			return targetLenth;
		}

		if(targetLenth == 0) {
			return resultLength;
		}

		int[][] d = new int[resultLength + 1][targetLenth + 1];

		IntStream.range(0, resultLength)
				.forEach(i -> d[i][0] = i);

		IntStream.range(0, targetLenth)
				.forEach(j -> d[0][j] = j);

		IntStream.range(0, resultLength)
				.forEach(i -> {
					char resultChar = result.charAt(i);

					IntStream.range(0, targetLenth)
					.forEach(j -> {
						char targetChar = target.charAt(j);
						int cost;

						if(resultChar == targetChar) {
							cost = 0;
						} else {
							cost = 1;
						}

						d[i + 1][j + 1] = minimum(d[i][j + 1] + 1, d[i + 1][j] + 1, d[i][j] + cost);
					});
				});

		return d[resultLength][targetLenth];
	}

	private static int minimum(int a, int b, int c) {
		int min = a;

		if(b < min) {
			min = b;
		}

		if(c < min) {
			min = c;
		}

		return min;
	}
}
