/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 04/07/2016.
 */

package cm.aptoide.pt.model.v7;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

import lombok.Data;

/**
 * Created by neuro on 04-07-2016.
 */
@Data
public class Comment {

	private long id;
	private String body;
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "UTC")
	private Date added;
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "UTC")
	private Date modified;
	private User user;
	private long parent;
	private Stats stats;

	@Data
	public static class User {

		private String name;
		private String avatar;
	}

	@Data
	public static class Stats {

		private long comments;
		private long points;
	}
}
