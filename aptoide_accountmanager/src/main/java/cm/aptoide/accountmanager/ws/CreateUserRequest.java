package cm.aptoide.accountmanager.ws;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import cm.aptoide.accountmanager.ws.responses.OAuth;
import cm.aptoide.pt.utils.MathUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by trinkes on 4/29/16.
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CreateUserRequest extends v3accountManager<OAuth> {

	private String password;
	private String email;
	private String name;

	public static CreateUserRequest of(String email, String password) {
		return new CreateUserRequest().setEmail(email).setName("").setPassword(password);
	}



	@Override
	protected Observable<OAuth> loadDataFromNetwork(Interfaces interfaces) {

		HashMap<String, String> parameters = new HashMap<String, String>();
		try {
			String passhash = null;
			passhash = MathUtils.computeSHA1sum(password);
			parameters.put("mode", "json");
			parameters.put("email", email);
			parameters.put("passhash", passhash);

			// TODO: 4/29/16 trinkes check aptoide oem id
//        if(Aptoide.getConfiguration().getExtraId().length()>0){
//            parameters.put("oem_id", Aptoide.getConfiguration().getExtraId());
//        }

			parameters.put("hmac", MathUtils.computeHmacSha1(email + passhash + name,
					"bazaar_hmac"));
		} catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return interfaces.createUser(parameters);
	}
}
