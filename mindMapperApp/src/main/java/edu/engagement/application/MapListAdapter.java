package edu.engagement.application;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;

import java.util.List;

import edu.engagement.application.utils.MapListData;


public class MapListAdapter extends RecyclerView.Adapter<MapListAdapter.MapViewHolder> {

    public class MapViewHolder extends RecyclerView.ViewHolder {



        private TextView locationNameText;
        private TextView activityText;
        private RoundCornerProgressBar focusLevelBar;
        private RoundCornerProgressBar feltLevelBar;

        public MapViewHolder(View itemView) {
            super(itemView);

            locationNameText = (TextView) itemView.findViewById(R.id.locationName);
            activityText = (TextView) itemView.findViewById(R.id.activityTextLabel);

            focusLevelBar = (RoundCornerProgressBar)itemView.findViewById(R.id.focus_level_bar);
            feltLevelBar = (RoundCornerProgressBar)itemView.findViewById(R.id.felt_level_bar);
        }

        /**
         * Takes an data, and populates the appropriate views with data.
         * @param data the data to be shown on the card
         */
        public void bindView(MapListData data) {
            locationNameText.setText(data.getLocation());
            activityText.setText(data.getActivityText());
            focusLevelBar.setProgress((data.getFocusLevel().ordinal() + 1) * 4);
            focusLevelBar.setProgressColor(data.getFocusLevel().getColor());
            feltLevelBar.setProgress((data.getFeltLevel().ordinal() + 1)*4);
            feltLevelBar.setProgressColor(data.getFeltLevel().getColor());
        }
    }

    private List<MapListData> MapList;

    public MapListAdapter(List<MapListData> MapList) {
        this.MapList = MapList;
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
    public MapViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.map_list_view_item, parent, false);

        return new MapViewHolder(v);
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * @param viewHolder the SummaryHolder that is being updated
     * @param position the position of the item within the adapter's data set
     */
    @Override
    public void onBindViewHolder(MapViewHolder viewHolder, int position) {
        viewHolder.bindView(MapList.get(position));
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return MapList.size();
    }
}


