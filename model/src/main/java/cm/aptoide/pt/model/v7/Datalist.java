/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/04/2016.
 */

package cm.aptoide.pt.model.v7;

import java.util.List;

import lombok.Data;

/**
 * Created by neuro on 27-04-2016.
 */
@Data
public class Datalist<T> {

	private Number total;
	private Number count;
	private Number offset;
	private Number limit;
	private Number next;
	private Number hidden;
	private boolean loaded;
	private List<T> list;
}
