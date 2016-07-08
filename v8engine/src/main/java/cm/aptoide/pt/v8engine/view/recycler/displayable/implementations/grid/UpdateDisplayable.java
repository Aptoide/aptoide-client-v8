/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 08/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by neuro on 17-05-2016.
 */
@AllArgsConstructor
public class UpdateDisplayable extends Displayable {

	@Getter private String packageName;
	@Getter private long appId;
	@Getter private String label;
	@Getter private String icon;
	@Getter private String md5;
	@Getter private String apkPath;
	@Getter private String alternativeApkPath;
	@Getter private String updateVersionName;

	// Obb
	@Getter private String mainObbPath;
	@Getter private String mainObbMd5;
	@Getter private String patchObbPath;
	@Getter private String patchObbMd5;

	public UpdateDisplayable() {
	}

	public static UpdateDisplayable create(Update update) {
		UpdateDisplayable updateDisplayable = new UpdateDisplayable();

		updateDisplayable.packageName = update.getPackageName();
		updateDisplayable.appId = update.getAppId();
		updateDisplayable.label = update.getLabel();
		updateDisplayable.icon = update.getIcon();
		updateDisplayable.md5 = update.getMd5();
		updateDisplayable.apkPath = update.getApkPath();
		updateDisplayable.alternativeApkPath = update.getAlternativeApkPath();
		updateDisplayable.updateVersionName = update.getUpdateVersionName();
		updateDisplayable.mainObbPath = update.getMainObbPath();
		updateDisplayable.mainObbMd5 = update.getMainObbMd5();
		updateDisplayable.patchObbPath = update.getPatchObbPath();
		updateDisplayable.patchObbMd5 = update.getPatchObbMd5();

		return updateDisplayable;
	}

	@Override
	public Type getType() {
		return Type.UPDATE;
	}

	@Override
	public int getViewLayout() {
		return R.layout.update_row;
	}
}
