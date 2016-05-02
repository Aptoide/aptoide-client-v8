package cm.aptoide.accountmanager.ws;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import cm.aptoide.accountmanager.ws.responses.OAuth;
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

	// TODO: 4/29/16 trinkes remove from here!!!
	public static String computeHmacSha1(String value, String keyString) throws
			InvalidKeyException, IllegalStateException, UnsupportedEncodingException,
			NoSuchAlgorithmException {
		System.out.println(value);
		System.out.println(keyString);
		SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(key);

		byte[] bytes = mac.doFinal(value.getBytes("UTF-8"));

		return convToHex(bytes);
	}

	private static String convToHex(byte[] data) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9)) buf.append((char) ('0' + halfbyte));
				else buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	public static String computeSHA1sum(String text) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		byte[] sha1hash = md.digest();
		return convToHex(sha1hash);
	}

	@Override
	protected Observable<OAuth> loadDataFromNetwork(Interfaces interfaces) {

		HashMap<String, String> parameters = new HashMap<String, String>();
		try {
			String passhash = null;
			passhash = computeSHA1sum(password);
			parameters.put("mode", "json");
			parameters.put("email", email);
			parameters.put("passhash", passhash);

			// TODO: 4/29/16 trinkes check aptoide oem id
//        if(Aptoide.getConfiguration().getExtraId().length()>0){
//            parameters.put("oem_id", Aptoide.getConfiguration().getExtraId());
//        }

			parameters.put("hmac", computeHmacSha1(email + passhash + name, "bazaar_hmac"));
		} catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return interfaces.createUser(parameters);
	}
}
