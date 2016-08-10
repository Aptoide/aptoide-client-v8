/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 09/08/2016.
 */

package cm.aptoide.pt.model.v7;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

import lombok.Data;

/**
 * Created by sithengineer on 20/07/16.
 */
@Data
public class Review {

	private long id;
	private String title;
	private String body;
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "UTC") private Date added;
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss", timezone = "UTC") private Date modified;
	private User user;
	private Stats stats;
	private Comments comments;
	private ListComments commentList;

	public boolean hasComments() {
		return commentList != null && commentList.getDatalist() != null && commentList.getDatalist().getList() != null && !commentList.getDatalist()
				.getList()
				.isEmpty();
	}

	@Data
	public static class User {

		private long id;
		private String name;
		private String avatar;
	}

	@Data
	public static class Stats {

		private float rating;
		private long points;
	}

	@Data
	public static class Comments {

		private long total;
		private String view;
	}
}
