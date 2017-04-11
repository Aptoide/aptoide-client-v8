package cm.aptoide.pt.v8engine.addressbook.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import cm.aptoide.pt.v8engine.addressbook.data.ContactsModel;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import java.util.Locale;

/**
 * Created by jdandrade on 13/02/2017.
 */

public class ContactUtils {

  public ContactUtils() {

  }

  public ContactsModel getContacts(Context context) {
    ContactsModel contacts = new ContactsModel();
    ContentResolver contentResolver = context.getContentResolver();
    Cursor cursor =
        contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
    if (cursor != null && cursor.getCount() > 0) {
      while (cursor.moveToNext()) {
        final String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        addContactPhoneNumbers(contentResolver, cursor, contacts, id, context);
        addContactEmails(contentResolver, contacts, id);
      }
    }

    if (cursor != null) {
      cursor.close();
    }

    return contacts;
  }

  private void addContactPhoneNumbers(ContentResolver contentResolver, Cursor cursor,
      ContactsModel contact, String id, Context context) {

    String country = getUserCountry(context);

    if (country == null) {
      return;
    } else {
      country = country.toUpperCase();
    }

    if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
      Cursor cursorNumbers =
          contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
              ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id },
              null);
      if (cursorNumbers != null) {
        while (cursorNumbers.moveToNext()) {
          final String mobileNumber = cursorNumbers.getString(
              cursorNumbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
          String normalizedMobileNumber = null;

          try {
            final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(mobileNumber, country);

            if (phoneNumberUtil.isValidNumberForRegion(phoneNumber, country)) {
              normalizedMobileNumber =
                  phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
            }
          } catch (NumberParseException ignored) {

          }

          contact.addMobileNumber(normalizedMobileNumber);
        }
        cursorNumbers.close();
      }
    }
  }

  private void addContactEmails(ContentResolver contentResolver, ContactsModel contact, String id) {
    Cursor cursorEmails =
        contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[] { id }, null);

    if (cursorEmails != null) {
      while (cursorEmails.moveToNext()) {
        contact.addEmail(cursorEmails.getString(
            cursorEmails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
      }
      cursorEmails.close();
    }
  }

  /**
   * Get ISO 3166-1 alpha-2 country code for this device (or null if not available)
   *
   * @param context Context reference to get the TelephonyManager instance from
   *
   * @return country code or null
   */
  public String getUserCountry(Context context) {
    try {
      final TelephonyManager tm =
          (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
      final String simCountry = tm.getSimCountryIso();
      if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
        return simCountry.toLowerCase(Locale.US);
      } else if (tm.getPhoneType()
          != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
        String networkCountry = tm.getNetworkCountryIso();
        if (networkCountry != null
            && networkCountry.length() == 2) { // network country code is available
          return networkCountry.toLowerCase(Locale.US);
        }
      }
    } catch (Exception ignored) {
    }
    return null;
  }

  public String normalizePhoneNumber(String phoneNumber) {
    if (phoneNumber.startsWith("+")) {
      return phoneNumber;
    }
    return "+" + phoneNumber;
  }

  public String getCountryCodeForRegion(Context context) {
    String country = getUserCountry(context);

    if (country == null) {
      return "";
    } else {
      country = country.toUpperCase();
    }

    final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    return String.valueOf(phoneNumberUtil.getCountryCodeForRegion(country));
  }

  public boolean isValidNumberInE164Format(String number) {
    final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    Phonenumber.PhoneNumber phoneNumber = null;

    int country;
    try {
      phoneNumber = phoneNumberUtil.parse(number, "");
      country = phoneNumber.getCountryCode();
    } catch (NumberParseException e) {
      e.printStackTrace();
      return false;
    }

    return phoneNumberUtil.isValidNumberForRegion(phoneNumber,
        phoneNumberUtil.getRegionCodeForCountryCode(country));
  }
}
