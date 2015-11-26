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

import edu.engagement.application.utils.Annotation;
import edu.engagement.application.utils.AttentionColor;

public class AnnotationListAdapter extends RecyclerView.Adapter<AnnotationListAdapter.AnnotationHolder> {

    public class AnnotationHolder extends RecyclerView.ViewHolder {

        private CardView cv;

        private TextView annotationTime, annotationText;
        private RoundCornerProgressBar annotationLevelBar;

        public AnnotationHolder(View itemView) {
            super(itemView);

            annotationTime = (TextView) itemView.findViewById(R.id.annotation_time_label);
            annotationText = (TextView) itemView.findViewById(R.id.annotation_text_label);

            annotationLevelBar = (RoundCornerProgressBar)itemView.findViewById(R.id.annotation_level_bar);
        }

        /**
         * Takes an annotation, and populates the appropriate views with data.
         * @param annotation the annotation to be shown on the card
         */
        public void bindAnnotation(Annotation annotation) {
            annotationTime.setText(annotation.getTimeFormatted("hh:mm a"));
            annotationText.setText((annotation.getAnnotation()));
            annotationLevelBar.setProgress((annotation.getAttentionLevel().ordinal() + 1)*4);
            annotationLevelBar.setProgressColor(annotation.getAttentionLevel().getColor());
        }
    }

    private List<Annotation> annotationList;

    public AnnotationListAdapter(List<Annotation> annotationList) {
        this.annotationList = annotationList;
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
    public AnnotationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_view_item, parent, false);

        return new AnnotationHolder(v);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * @param annotationHolder the SummaryHolder that is being updated
     * @param position the position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(AnnotationHolder annotationHolder, int position) {
        annotationHolder.bindAnnotation(annotationList.get(position));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return annotationList.size();
    }
}