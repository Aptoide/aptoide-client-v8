/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/07/2016.
 */

package android.content.pm;

/**
 * Created by marcelobenites on 7/19/16.
 */
public interface IPackageInstallObserver extends android.os.IInterface {

  public abstract static class Stub extends android.os.Binder
      implements android.content.pm.IPackageInstallObserver {

    public Stub() {
      throw new RuntimeException("Stub!");
    }

    public static android.content.pm.IPackageInstallObserver asInterface(android.os.IBinder obj) {
      throw new RuntimeException("Stub!");
    }

    public android.os.IBinder asBinder() {
      throw new RuntimeException("Stub!");
    }

    public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags)
        throws android.os.RemoteException {
      throw new RuntimeException("Stub!");
    }

    public abstract void packageInstalled(java.lang.String packageName, int returnCode)
        throws android.os.RemoteException;
  }
}