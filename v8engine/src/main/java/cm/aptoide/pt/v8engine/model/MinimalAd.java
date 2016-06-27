/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 20/06/2016.
 */

package cm.aptoide.pt.v8engine.model;

import android.os.Parcel;
import android.os.Parcelable;

import cm.aptoide.pt.model.v2.GetAdsResponse;
import lombok.Data;

/**
 * Created by neuro on 20-06-2016.
 */
@Data
public class MinimalAd implements Parcelable{

	private final String packageName;
	private final long networkId;
	private final String clickUrl;
	private final long appId;
	private final long adId;

	public MinimalAd(GetAdsResponse.Ad ad) {
		packageName = ad.getData().getPackageName();
		networkId = ad.getPartner().getInfo().getId();
		clickUrl = ad.getPartner().getData().getClickUrl();
		appId = ad.getData().getId();
		adId = ad.getInfo().getAdId();
	}

	protected MinimalAd(Parcel in) {
		packageName = in.readString();
		networkId = in.readLong();
		clickUrl = in.readString();
		appId = in.readLong();
		adId = in.readLong();
	}

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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(packageName);
		dest.writeLong(networkId);
		dest.writeString(clickUrl);
		dest.writeLong(appId);
		dest.writeLong(adId);
	}
}
