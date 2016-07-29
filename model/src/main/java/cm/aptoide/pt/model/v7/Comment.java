/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 29/07/2016.
 */

package cm.aptoide.pt.model.v7;

import android.support.annotation.Nullable;

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
	private User user;
	private long parentReview;
	@Nullable private GetAppMeta.App data;

	@Data
	public static class User {

		private String name;
		private String avatar;
	}
}
