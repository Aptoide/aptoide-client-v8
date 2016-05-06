package cm.aptoide.pt.utils;

/**
 * Created by sithengineer on 02/05/16.
 */
public class StringUtils {

	public static String withSuffix(long count) {
		if (count < 1000) return String.valueOf(count);
		int exp = (int) (Math.log(count) / Math.log(1000));
		return String.format("%d %c", (int) (count / Math.pow(1000, exp)), "kMGTPE".charAt(exp -
				1));
	}
}
