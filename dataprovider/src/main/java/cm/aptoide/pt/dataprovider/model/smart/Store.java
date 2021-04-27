package cm.aptoide.pt.dataprovider.model.smart;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Store {

	@JsonProperty("store_name")
	private String storeName;

	@JsonProperty("platforms")
	private List<Platform> platforms;

	public void setStoreName(String storeName){
		this.storeName = storeName;
	}

	public String getStoreName(){
		return storeName;
	}

	public void setPlatforms(List<Platform> platforms){
		this.platforms = platforms;
	}

	public List<Platform> getPlatforms(){
		return platforms;
	}

	@Override
 	public String toString(){
		return 
			"StoresItem{" + 
			"store_name = '" + storeName + '\'' + 
			",platforms = '" + platforms + '\'' + 
			"}";
		}
}