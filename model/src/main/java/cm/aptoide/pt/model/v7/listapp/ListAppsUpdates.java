/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.model.v7.listapp;

/**
 * Created by neuro on 22-04-2016.
 */

import java.util.List;

import cm.aptoide.pt.model.v7.BaseV7Response;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ListAppsUpdates extends BaseV7Response {

	private List<ListAppData> list;
}
