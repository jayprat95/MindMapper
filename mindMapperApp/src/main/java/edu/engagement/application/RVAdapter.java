package edu.engagement.application;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {

	public static class PersonViewHolder extends RecyclerView.ViewHolder {

		CardView cv;
		
		ImageView locationPhoto;
		
		TextView average;
		TextView variance;
		TextView location;
		
		
		

		PersonViewHolder(View itemView) {
			super(itemView);
			cv = (CardView) itemView.findViewById(R.id.cv);

			// cv.setCardBackgroundColor(Color.BLACK);

			average = (TextView) itemView.findViewById(R.id.card_average_obj);
			location = (TextView) itemView.findViewById(R.id.card_location);
			locationPhoto = (ImageView) itemView.findViewById(R.id.card_photo);
		}
	}

	List<CardData> cardDataList;

	public RVAdapter(List<CardData> cardDataList) {
		this.cardDataList = cardDataList;
	}

	@Override
	public void onAttachedToRecyclerView(RecyclerView recyclerView) {
		super.onAttachedToRecyclerView(recyclerView);
	}

	@Override
	public PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View v = LayoutInflater.from(viewGroup.getContext()).inflate(
				R.layout.item, viewGroup, false);
		PersonViewHolder pvh = new PersonViewHolder(v);
		return pvh;
	}

	@Override
	public void onBindViewHolder(PersonViewHolder personViewHolder, int i) {
		personViewHolder.average.setText("Average: " + cardDataList.get(i).getEegAttention());
		personViewHolder.location.setText(cardDataList.get(i).getLocation());
		personViewHolder.locationPhoto.setImageResource(R.drawable.mcbryde);
	}

	@Override
	public int getItemCount() {
		return cardDataList.size();
	}
}
