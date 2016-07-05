/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 07/06/2016.
 */

package cm.aptoide.pt.model.v7;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by neuro on 20-04-2016.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseV7EndlessResponse<T> extends BaseV7Response {

	private DataList<T> datalist;
}
