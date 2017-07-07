/*
 * Copyright (c) 2016.
 * Modified on 09/08/2016.
 */

package cm.aptoide.pt.v8engine.view.comments;

import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.model.v7.ListComments;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.ListCommentsRequest;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by trinkes on 8/5/16.
 */
public class CommentsReadMoreWidget extends Widget<CommentsReadMoreDisplayable> {

  private TextView readMoreButton;

  public CommentsReadMoreWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    readMoreButton = (TextView) itemView.findViewById(R.id.read_more_button);
  }

  @Override public void bindView(CommentsReadMoreDisplayable displayable) {
    final BodyInterceptor<BaseBody> baseBodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    final OkHttpClient httpClient =
        ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    final Converter.Factory converterFactory = WebService.getDefaultConverter();
    Observable<ListComments> listCommentsObservable =
        ListCommentsRequest.of(displayable.getResourceId(), displayable.getNext(), 100,
            displayable.isReview(), baseBodyInterceptor, httpClient, converterFactory,
            ((cm.aptoide.pt.v8engine.V8Engine) getContext().getApplicationContext()).getTokenInvalidator(),
            ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences())
            .observe();

    compositeSubscription.add(RxView.clicks(readMoreButton)
        .flatMap(__ -> listCommentsObservable)
        .subscribe(listComments -> displayable.getCommentAdder()
            .addComment(listComments.getDataList()
                .getList())));
  }
}
