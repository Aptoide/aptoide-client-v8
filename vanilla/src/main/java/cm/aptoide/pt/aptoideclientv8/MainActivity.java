/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.aptoideclientv8;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.model.v7.GetStore;
import cm.aptoide.pt.v8engine.fragments.GridRecyclerFragment;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		getSupportFragmentManager().beginTransaction().replace(R.id.contentor, new GridRecyclerFragment()).commit();

		final GetStore[] gaga = new GetStore[1];
		GetStoreRequest getStoreRequest = GetStoreRequest.of("apps");
		getStoreRequest.execute(getStoreResponse -> System.out.println("Ss: " + getStoreResponse.getNodes().getMeta()), System.out::println);
	}
}
