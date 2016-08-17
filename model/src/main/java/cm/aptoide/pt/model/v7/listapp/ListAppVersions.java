/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 17/08/2016.
 */

package cm.aptoide.pt.model.v7.listapp;

import java.util.List;

import cm.aptoide.pt.model.v7.BaseV7EndlessResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by neuro on 22-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ListAppVersions extends BaseV7EndlessResponse {

	/**
	 * The other versions list always returns one item (itself), as per the web team.
	 */
	private List<App> list;

	// needs a fix to support endless scroll
	// implements abstract method to get list size in parent and concrete implementation in child classes
}
