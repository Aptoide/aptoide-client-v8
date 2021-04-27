package cm.aptoide.pt.dataprovider.model.smart;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FilteredAppsDto {

	@JsonProperty("stores")
	private List<Store> stores;

	public void setStores(List<Store> stores){
		this.stores = stores;
	}

	public List<Store> getStores(){
		return stores;
	}

	@Override
 	public String toString(){
		return 
			"FilteredApp{" + 
			"stores = '" + stores + '\'' + 
			"}";
		}
}