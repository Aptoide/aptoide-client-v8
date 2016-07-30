/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.dataprovider.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import cm.aptoide.pt.model.v2.GetAdsResponse;
import io.realm.RealmObject;
import lombok.Getter;

/**
 * Created by neuro on 20-06-2016.
 */
public class MinimalAd extends RealmObject implements Parcelable {

	public static final Creator<MinimalAd> CREATOR = new Creator<MinimalAd>() {
		@Override
		public MinimalAd createFromParcel(Parcel in) {
			return new MinimalAd(in);
		}

		@Override
		public MinimalAd[] newArray(int size) {
			return new MinimalAd[size];
		}
	};

	@Getter private String packageName;
	@Getter private long networkId;
	@Getter private String clickUrl;
	@Getter private String cpcUrl;
	@Getter private String cpdUrl;
	@Getter private long appId;
	@Getter private long adId;
	@Getter private String cpiUrl;

	public MinimalAd() {
	}

	public MinimalAd(String packageName, long networkId, String clickUrl, String cpcUrl, String cpdUrl, long appId, long adId, String cpiUrl) {
		this.packageName = packageName;
		this.networkId = networkId;
		this.clickUrl = clickUrl;
		this.cpcUrl = cpcUrl;
		this.cpdUrl = cpdUrl;
		this.appId = appId;
		this.adId = adId;
		this.cpiUrl = cpiUrl;
	}

	protected MinimalAd(Parcel in) {
		packageName = in.readString();
		networkId = in.readLong();
		clickUrl = in.readString();
		cpcUrl = in.readString();
		cpdUrl = in.readString();
		appId = in.readLong();
		adId = in.readLong();
	}

	public static MinimalAd from(@NonNull GetAdsResponse.Ad ad) {
		return new MinimalAd(ad.getData().getPackageName(), ad.getPartner().getInfo().getId(), ad.getPartner().getData().getClickUrl(), ad.getInfo()
				.getCpcUrl(), ad.getInfo().getCpdUrl(), ad.getData().getId(), ad.getInfo().getAdId(), ad.getInfo().getCpiUrl());
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(packageName);
		dest.writeLong(networkId);
		dest.writeString(clickUrl);
		dest.writeString(cpcUrl);
		dest.writeString(cpdUrl);
		dest.writeLong(appId);
		dest.writeLong(adId);
	}
}
