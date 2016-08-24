package cm.aptoide.pt.dataprovider.ws;

import android.content.SharedPreferences;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import lombok.AllArgsConstructor;

/**
 * Created by diogoloureiro on 10/08/16.
 */
@AllArgsConstructor
public class BaseBodyDecorator {

	private final IdsRepository idsRepository;
	private final SharedPreferences preferences;


	public BaseBody decorate(BaseBody baseBody){
		baseBody.setAccessToken(AptoideAccountManager.getAccessToken());
		baseBody.setAptoideId(idsRepository.getAptoideClientUUID());
		baseBody.setAptoideVercode(AptoideUtils.Core.getVerCode());
		baseBody.setCdn("pool");
		baseBody.setLang(Api.LANG);
		baseBody.setMature(Api.isMature());
		if (ManagerPreferences.getHWSpecsFilter()) {
			baseBody.setQ(Api.Q);
		}

		return baseBody;
	}
	
}
