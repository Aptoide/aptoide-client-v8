package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.install.Installer;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import lombok.Getter;

/**
 * Created by neuro on 02-08-2016.
 */
public class UpdatesHeaderDisplayable extends Displayable {

	@Getter private String label;
	@Getter private Installer installManager;

	public UpdatesHeaderDisplayable() {
	}

	public UpdatesHeaderDisplayable(Installer installManager, String label) {
		this.installManager = installManager;
		this.label = label;
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
