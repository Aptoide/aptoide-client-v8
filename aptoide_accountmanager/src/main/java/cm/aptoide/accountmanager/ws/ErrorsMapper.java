package cm.aptoide.accountmanager.ws;

import java.util.HashMap;

import cm.aptoide.accountmanager.R;

/**
 * Created by j-pac on 19-05-2014.
 */

public class ErrorsMapper {

	private static HashMap<String, Integer> errors = new HashMap<String, Integer>();

	static {

		errors.put("IARG-1", R.string.error_IARG_1);
		errors.put("IARG-2", R.string.error_IARG_2);
		errors.put("IARG-3", R.string.error_IARG_3);
		errors.put("IARG-4", R.string.error_IARG_4);
		errors.put("IARG-5", R.string.error_IARG_5);
		errors.put("IARG-6", R.string.error_IARG_6);
		errors.put("IARG-100", R.string.error_IARG_100);
		errors.put("IARG-101", R.string.error_IARG_101);
		errors.put("IARG-102", R.string.error_IARG_102);
		errors.put("IARG-103", R.string.error_IARG_103);
		errors.put("IARG-104", R.string.error_IARG_104);
		errors.put("IARG-105", R.string.error_IARG_105);
		errors.put("IARG-106", R.string.error_IARG_106);
		errors.put("IARG-107", R.string.error_IARG_107);
		errors.put("IARG-108", R.string.error_IARG_108);
		errors.put("IARG-109", R.string.error_IARG_109);
		errors.put("IARG-110", R.string.error_IARG_110);
		errors.put("IARG-200", R.string.error_IARG_200);
		errors.put("IARG-201", R.string.error_IARG_201);
		errors.put("IARG-300", R.string.error_IARG_300);
		errors.put("IARG-301", R.string.error_IARG_301);
		errors.put("IARG-302", R.string.error_IARG_302);

		errors.put("MARG-1", R.string.error_MARG_1);
		errors.put("MARG-2", R.string.error_MARG_2);
		errors.put("MARG-3", R.string.error_MARG_3);
		errors.put("MARG-4", R.string.error_MARG_4);
		errors.put("MARG-5", R.string.error_MARG_5);
		errors.put("MARG-6", R.string.error_MARG_6);
		errors.put("MARG-7", R.string.error_MARG_7);
		errors.put("MARG-8", R.string.error_MARG_8);
		errors.put("MARG-9", R.string.error_MARG_9);
		errors.put("MARG-10", R.string.error_MARG_10);
		errors.put("MARG-11", R.string.error_MARG_11);
		errors.put("MARG-100", R.string.error_MARG_100);
		errors.put("MARG-101", R.string.error_MARG_101);
		errors.put("MARG-102", R.string.error_MARG_102);
		errors.put("MARG-103", R.string.error_MARG_103);
		errors.put("MARG-104", R.string.error_MARG_104);
		errors.put("MARG-105", R.string.error_MARG_105);
		errors.put("MARG-106", R.string.error_MARG_106);
		errors.put("MARG-107", R.string.error_MARG_107);
		errors.put("MARG-108", R.string.error_MARG_108);
		errors.put("MARG-109", R.string.error_MARG_109);
		errors.put("MARG-110", R.string.error_MARG_110);
		errors.put("MARG-111", R.string.error_MARG_111);
		errors.put("MARG-112", R.string.error_MARG_112);
		errors.put("MARG-113", R.string.error_MARG_113);
		errors.put("MARG-200", R.string.error_MARG_200);
		errors.put("MARG-201", R.string.error_MARG_201);
		errors.put("MARG-202", R.string.error_MARG_202);

		errors.put("AUTH-1", R.string.error_AUTH_1);
		errors.put("AUTH-2", R.string.error_AUTH_2);
		errors.put("AUTH-3", R.string.error_AUTH_3);
		errors.put("AUTH-4", R.string.error_AUTH_4);
		errors.put("AUTH-100", R.string.error_AUTH_100);
		errors.put("AUTH-101", R.string.error_AUTH_101);
		errors.put("AUTH-102", R.string.error_AUTH_102);
		errors.put("AUTH-103", R.string.error_AUTH_103);

		errors.put("WOP-6", R.string.error_WOP_6);
		errors.put("WOP-7", R.string.error_WOP_7);
		errors.put("WOP-8", R.string.error_WOP_8);
		errors.put("WOP-9", R.string.error_WOP_9);
		errors.put("WOP-10", R.string.error_WOP_10);
		errors.put("WOP-11", R.string.error_WOP_11);

		errors.put("REPO-1", R.string.error_REPO_1);
		errors.put("REPO-2", R.string.error_REPO_2);
		errors.put("REPO-3", R.string.error_REPO_3);

		errors.put("APK-1", R.string.error_APK_1);
		errors.put("APK-4", R.string.error_APK_4);
		errors.put("REPO-7", R.string.error_REPO_7);
	}

	public static HashMap<String, Integer> getErrorsMap() {
		return errors;
	}
}
