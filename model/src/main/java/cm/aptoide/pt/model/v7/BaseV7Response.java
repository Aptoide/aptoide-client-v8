/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/05/2016.
 */

package cm.aptoide.pt.model.v7;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by neuro on 20-04-2016.
 */
@Data
public class BaseV7Response {

	private Info info;
	private List<Error> errors;

	public Error getError() {
		if (errors.size() > 0) {
			return errors.get(0);
		} else {
			return null;
		}
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Info {

		private Status status;
		private Time time;

		public enum Status {
			OK, QUEUED, FAIL
		}

		@Data
		private static class Time {

			private double seconds;
			private String human;
		}
	}

	@Data
	public static class Error {

		private String code;
		private String description;
	}
}
