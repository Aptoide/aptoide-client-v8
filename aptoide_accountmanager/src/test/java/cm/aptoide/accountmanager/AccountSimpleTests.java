/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.accountmanager;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class AccountSimpleTests {

  @Test public void emptyAccountIsNotLoggedIn() throws Exception {
    Account emptyAccount = new LocalAccount();
    assertFalse(emptyAccount.isLoggedIn());
  }

  @Test public void emptyAccountIsNotConfirmed() throws Exception {
    Account emptyAccount = new LocalAccount();
    assertFalse(emptyAccount.isAccessConfirmed());
  }

  @Test public void emptyAccountHasNotAdultContentEnabled() throws Exception {
    Account emptyAccount = new LocalAccount();
    assertFalse(emptyAccount.isAdultContentEnabled());
  }
}
