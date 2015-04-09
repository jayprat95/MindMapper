package edu.engagement.application;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder> {

	public static class PersonViewHolder extends RecyclerView.ViewHolder {

		CardView cv;
		
		//ImageView locationPhoto;
		
		TextView average;
		TextView variance;
		TextView location;
		TextView label;
		
		
		

		PersonViewHolder(View itemView) {
			super(itemView);
			cv = (CardView) itemView.findViewById(R.id.cv);

			// cv.setCardBackgroundColor(Color.BLACK);

			average = (TextView) itemView.findViewById(R.id.card_average);
			location = (TextView) itemView.findViewById(R.id.card_location);
			variance = (TextView) itemView.findViewById(R.id.card_variance);
			label = (TextView) itemView.findViewById(R.id.card_label);
			//locationPhoto = (ImageView) itemView.findViewById(R.id.person_photo);
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
		personViewHolder.average.setText("" + cardDataList.get(i).getAverage());
		personViewHolder.location.setText(cardDataList.get(i).getLocation());
		personViewHolder.variance.setText("" + cardDataList.get(i).getVariance());
		personViewHolder.label.setText(cardDataList.get(i).getLabel());
		//personViewHolder.locationPhoto.setImageResource(persons.get(i).photoId);
	}

	@Override
	public int getItemCount() {
		return cardDataList.size();
	}
}
