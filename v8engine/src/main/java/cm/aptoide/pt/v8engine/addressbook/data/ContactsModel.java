package cm.aptoide.pt.v8engine.addressbook.data;

import cm.aptoide.pt.v8engine.addressbook.utils.StringEncryption;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jdandrade on 23/02/2017.
 */
public class ContactsModel {

  private List<String> mobileNumbers;
  private List<String> emails;

  public ContactsModel() {
    mobileNumbers = new ArrayList<>();
    emails = new ArrayList<>();
  }

  public List<String> getMobileNumbers() {
    return mobileNumbers;
  }

  public List<String> getEmails() {
    return emails;
  }

  public void addMobileNumber(String mobileNumber) {
    if (mobileNumber == null) return;
    try {
      mobileNumber = StringEncryption.SHA256(mobileNumber);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    if (!mobileNumbers.contains(mobileNumber)) {
      mobileNumbers.add(mobileNumber);
    }
  }

  public void addEmail(String email) {
    email = email.toLowerCase();
    try {
      email = StringEncryption.SHA256(email);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    if (!emails.contains(email)) {
      emails.add(email);
    }
  }
}
