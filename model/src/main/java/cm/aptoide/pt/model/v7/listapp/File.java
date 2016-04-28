/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/04/2016.
 */

package cm.aptoide.pt.model.v7.listapp;

import lombok.Data;

/**
 * Class used on an App item TODO: Incomplete
 */
@Data
public class File {

	private String vername;
	private Number vercode;
	private String md5sum;
	private String path;
	private String pathAlt;
	private Number filesize;
}
