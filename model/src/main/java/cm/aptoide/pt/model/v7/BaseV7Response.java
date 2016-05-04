/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 04/05/2016.
 */

package cm.aptoide.pt.model.v7;

import java.util.List;

import lombok.Data;

/**
 * Created by neuro on 20-04-2016.
 */
@Data
public class BaseV7Response {

	private Info info;
	private List<Error> errors;

	@Data
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
