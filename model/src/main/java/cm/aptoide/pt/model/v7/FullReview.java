/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/08/2016.
 */

package cm.aptoide.pt.model.v7;

import android.support.annotation.Nullable;

import lombok.Data;

/**
 * Created by sithengineer on 02/08/16.
 */
@Data
public class FullReview extends Review {

	@Nullable private GetAppMeta.App data;
}
