package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.install.InstallManager;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import io.realm.Realm;
import lombok.Getter;

/**
 * Created by neuro on 02-08-2016.
 */
public class UpdatesHeaderDisplayable extends Displayable {

	@Getter private String label;
	@Getter private InstallManager installManager;
	@Getter private Realm realm;

	public UpdatesHeaderDisplayable() {
	}

	public UpdatesHeaderDisplayable(InstallManager installManager, String label, Realm realm) {
		this.installManager = installManager;
		this.label = label;
		this.realm = realm;
	}

	@Override
	public Type getType() {
		return Type.UPDATES_HEADER;
	}

	@Override
	public int getViewLayout() {
		return R.layout.updates_header_row;
	}
}
