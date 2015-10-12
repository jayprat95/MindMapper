package edu.engagement.application;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RatingBar;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import java.util.List;

import edu.engagement.application.utils.AttentionColor;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.SummaryHolder> {

	public class SummaryHolder extends RecyclerView.ViewHolder {

		private CardView cv;

		private TextView location, time;
        private RoundCornerProgressBar eegAverage;
        private RatingBar selfReportAverage;


		public SummaryHolder(View itemView) {
			super(itemView);
			cv = (CardView) itemView.findViewById(R.id.cardView);

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            location = (TextView) itemView.findViewById(R.id.card_location);
            time = (TextView) itemView.findViewById(R.id.card_time);
			eegAverage = (RoundCornerProgressBar) itemView.findViewById(R.id.card_eeg_average);
            selfReportAverage = (RatingBar) itemView.findViewById(R.id.card_self_average);
		}

		/**
		 * Takes an EventSummary, and populates the appropriate views with data.
		 * @param eventSummary the event summary to be shown on the card
		 */
		public void bindSummary(EventSummary eventSummary) {
            location.setText(eventSummary.getLocation());
            time.setText(eventSummary.getTimeRangeFormatted("hh:mm a", " -> "));

            double att = eventSummary.getEegAttention();
			eegAverage.setProgress((float)att);
            eegAverage.setProgressColor(AttentionColor.getAttentionColor(att));

			selfReportAverage.setRating(eventSummary.getSelfReportedAttention() + 1);
		}
	}

	private List<EventSummary> eventSummaryList;

	public RVAdapter(List<EventSummary> eventSummaryList) {
		this.eventSummaryList = eventSummaryList;
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
		summaryHolder.bindSummary(eventSummaryList.get(position));
        final EventSummary event = eventSummaryList.get(position);

        summaryHolder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GraphActivity.class);
                intent.putExtra("sessionId", event.getSessionId());
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
		return eventSummaryList.size();
	}
}
