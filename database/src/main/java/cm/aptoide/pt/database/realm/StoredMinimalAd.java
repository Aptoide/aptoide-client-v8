package cm.aptoide.pt.database.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by neuro on 28-07-2016.
 */
public class StoredMinimalAd extends RealmObject {

	public static final String PACKAGE_NAME = "packageName";
	public static final String REFERRER = "referrer";
	public static final String CPI_URL = "cpiUrl";

	@PrimaryKey @Required private String packageName;
	private String referrer;
	private String cpiUrl;
	private long timestamp;

	private long adId;

	public StoredMinimalAd() {
	}

	// apagar
	public StoredMinimalAd(String packageName, String referrer, String cpiUrl, long adId) {
		this.packageName = packageName;
		this.referrer = referrer;
		this.cpiUrl = cpiUrl;
		this.adId = adId;
		this.timestamp = System.currentTimeMillis();
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getReferrer() {
		return referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	public String getCpiUrl() {
		return cpiUrl;
	}

	public void setCpiUrl(String cpiUrl) {
		this.cpiUrl = cpiUrl;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getAdId() {
		return adId;
	}

	public void setAdId(long adId) {
		this.adId = adId;
	}
}
