package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import cm.aptoide.pt.dataprovider.ws.v7.ListCommentsRequest;
import cm.aptoide.pt.model.v7.FullReview;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CommentsReadMoreDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.BaseWidget;

/**
 * Created by trinkes on 8/5/16.
 */
public class CommentsReadMoreWidget extends BaseWidget<CommentsReadMoreDisplayable> {

	private TextView readMoreButton;

	public CommentsReadMoreWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		readMoreButton = (TextView) itemView.findViewById(R.id.read_more_button);
	}

	@Override
	public void bindView(CommentsReadMoreDisplayable displayable) {
		FullReview review = displayable.getPojo();
		RxView.clicks(readMoreButton).subscribe(aVoid -> {
			ListCommentsRequest.of(review.getId(), displayable.getNext(), 100)
					.execute(listComments -> displayable.getCommentAdder().addComment(listComments.getDatalist().getList()));
		});
	}
}
