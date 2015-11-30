package edu.engagement.application;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import java.util.List;

import edu.engagement.application.utils.AttentionColor;
import edu.engagement.application.utils.ColorUtils;
import edu.engagement.application.utils.Session;
import edu.engagement.application.utils.TimeUtils;

public class SummaryCardAdapter extends RecyclerView.Adapter<SummaryCardAdapter.SummaryHolder> {

	public class SummaryHolder extends RecyclerView.ViewHolder {

		private CardView cv;

		private TextView description, time;
		private ImageView image;
        private RoundCornerProgressBar averageFocus, overallFelt;


		public SummaryHolder(View itemView) {
			super(itemView);
			cv = (CardView) itemView.findViewById(R.id.summary_card);

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            description = (TextView) itemView.findViewById(R.id.card_description);
            time = (TextView) itemView.findViewById(R.id.card_time);
			image = (ImageView) itemView.findViewById(R.id.card_image);
			averageFocus = (RoundCornerProgressBar) itemView.findViewById(R.id.card_average_focus);
            overallFelt = (RoundCornerProgressBar) itemView.findViewById(R.id.card_overall_felt);
		}

		/**
		 * Takes an EventSummary, and populates the appropriate views with data.
		 * @param session the session to be shown on the card
		 */
		public void bindSummary(Session session) {
			if(session.getActivityName().length() == 0){
				description.setText(session.getLocation().getName());
			}else{
				description.setText(session.getActivityName() + " at " + session.getLocation().getName());
			}

            time.setText(TimeUtils.getSessionTimeFormatted(session));

            float att = session.getEEGAverage();
			averageFocus.setProgress(att);
            averageFocus.setProgressColor(ColorUtils.getAttentionColor(att));

            float felt = (float)session.getSelfReportAverage();
            overallFelt.setProgress(felt);
            overallFelt.setProgressColor(ColorUtils.getAttentionColor(felt));
		}
	}

	private List<Session> sessions;

	public SummaryCardAdapter(List<Session> sessions) {
		this.sessions = sessions;
	}

	/**
	 * Called by RecyclerView when it starts observing this Adapter.
	 *
	 * Keep in mind that same adapter may be observed by multiple RecyclerViews
	 *
	 * @param recyclerView The RecyclerView instance which started observing this adapter.
	 */
	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
	}

	/**
	 * Called when the RecyclerView needs a new SummaryHolder to represent an item.
	 *
	 * This isn't called when a SummaryHolder is being recycled.
	 *
	 * @param parent The ViewGroup into which the new View will be added after it is bound
	 *               to an adapter position.
	 * @param viewType the view type of the new View.
	 * @return the newly created SummaryHolder
	 */
	@Override
	public SummaryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.summary_card, parent, false);

		return new SummaryHolder(v);
	}

	/**
	 * Called by RecyclerView to display the data at the specified position.
	 * @param summaryHolder the SummaryHolder that is being updated
	 * @param position the position of the item within the adapter's data set
	 */
	@Override
	public void onBindViewHolder(SummaryHolder summaryHolder, int position) {
		summaryHolder.bindSummary(sessions.get(position));
        final Session s = sessions.get(position);

        summaryHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GraphActivity.class);
                intent.putExtra(GraphActivity.SESSION_ID_TAG, s.getId());
                v.getContext().startActivity(intent);
            }
        });
	}

	/**
	 * Returns the total number of items in the data set held by the adapter.
	 * @return The total number of items in this adapter.
	 */
	@Override
	public int getItemCount() {
		return sessions.size();
	}
}
