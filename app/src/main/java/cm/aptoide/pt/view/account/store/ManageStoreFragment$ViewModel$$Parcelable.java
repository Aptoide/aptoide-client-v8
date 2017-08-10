
package cm.aptoide.pt.view.account.store;

import android.os.Parcelable;
import android.os.Parcelable.Creator;
import org.parceler.Generated;
import org.parceler.IdentityCollection;
import org.parceler.ParcelWrapper;
import org.parceler.ParcelerRuntimeException;

@Generated(value = "org.parceler.ParcelAnnotationProcessor", date = "2017-08-10T10:11+0100")
@SuppressWarnings({
    "unchecked",
    "deprecation"
})
public class ManageStoreFragment$ViewModel$$Parcelable
    implements Parcelable, ParcelWrapper<cm.aptoide.pt.view.account.store.ManageStoreFragment.ViewModel>
{

    private cm.aptoide.pt.view.account.store.ManageStoreFragment.ViewModel viewModel$$0;
    @SuppressWarnings("UnusedDeclaration")
    public final static Creator<ManageStoreFragment$ViewModel$$Parcelable>CREATOR = new Creator<ManageStoreFragment$ViewModel$$Parcelable>() {


        @Override
        public ManageStoreFragment$ViewModel$$Parcelable createFromParcel(android.os.Parcel parcel$$2) {
            return new ManageStoreFragment$ViewModel$$Parcelable(read(parcel$$2, new IdentityCollection()));
        }

        @Override
        public ManageStoreFragment$ViewModel$$Parcelable[] newArray(int size) {
            return new ManageStoreFragment$ViewModel$$Parcelable[size] ;
        }

    }
    ;

    public ManageStoreFragment$ViewModel$$Parcelable(cm.aptoide.pt.view.account.store.ManageStoreFragment.ViewModel viewModel$$2) {
        viewModel$$0 = viewModel$$2;
    }

    @Override
    public void writeToParcel(android.os.Parcel parcel$$0, int flags) {
        write(viewModel$$0, parcel$$0, flags, new IdentityCollection());
    }

    public static void write(cm.aptoide.pt.view.account.store.ManageStoreFragment.ViewModel viewModel$$1, android.os.Parcel parcel$$1, int flags$$0, IdentityCollection identityMap$$0) {
        int identity$$0 = identityMap$$0 .getKey(viewModel$$1);
        if (identity$$0 != -1) {
            parcel$$1 .writeInt(identity$$0);
        } else {
            parcel$$1 .writeInt(identityMap$$0 .put(viewModel$$1));
            parcel$$1 .writeString(viewModel$$1 .storeDescription);
            parcel$$1 .writeInt((viewModel$$1 .newAvatar? 1 : 0));
            parcel$$1 .writeString(viewModel$$1 .pictureUri);
            parcel$$1 .writeString(viewModel$$1 .storeName);
            cm.aptoide.pt.store.StoreTheme storeTheme$$0 = viewModel$$1 .storeTheme;
            parcel$$1 .writeString(((storeTheme$$0 == null)?null:storeTheme$$0 .name()));
            parcel$$1 .writeLong(viewModel$$1 .storeId);
        }
    }

    @Override
    public int describeContents() {
        return  0;
    }

    @Override
    public cm.aptoide.pt.view.account.store.ManageStoreFragment.ViewModel getParcel() {
        return viewModel$$0;
    }

    public static cm.aptoide.pt.view.account.store.ManageStoreFragment.ViewModel read(android.os.Parcel parcel$$3, IdentityCollection identityMap$$1) {
        int identity$$1 = parcel$$3 .readInt();
        if (identityMap$$1 .containsKey(identity$$1)) {
            if (identityMap$$1 .isReserved(identity$$1)) {
                throw new ParcelerRuntimeException("An instance loop was detected whild building Parcelable and deseralization cannot continue.  This error is most likely due to using @ParcelConstructor or @ParcelFactory.");
            }
            return identityMap$$1 .get(identity$$1);
        } else {
            cm.aptoide.pt.view.account.store.ManageStoreFragment.ViewModel viewModel$$4;
            int reservation$$0 = identityMap$$1 .reserve();
            viewModel$$4 = new cm.aptoide.pt.view.account.store.ManageStoreFragment.ViewModel();
            identityMap$$1 .put(reservation$$0, viewModel$$4);
            viewModel$$4 .storeDescription = parcel$$3 .readString();
            viewModel$$4 .newAvatar = (parcel$$3 .readInt() == 1);
            viewModel$$4 .pictureUri = parcel$$3 .readString();
            viewModel$$4 .storeName = parcel$$3 .readString();
            String storeTheme$$1 = parcel$$3 .readString();
            viewModel$$4 .storeTheme = ((cm.aptoide.pt.store.StoreTheme)((storeTheme$$1 == null)?null:Enum.valueOf(cm.aptoide.pt.store.StoreTheme.class, storeTheme$$1)));
            viewModel$$4 .storeId = parcel$$3 .readLong();
            cm.aptoide.pt.view.account.store.ManageStoreFragment.ViewModel viewModel$$3 = viewModel$$4;
            identityMap$$1 .put(identity$$1, viewModel$$3);
            return viewModel$$3;
        }
    }

}
