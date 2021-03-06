package com.summerbrochtrup.myeats.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.summerbrochtrup.myeats.Constants;
import com.summerbrochtrup.myeats.R;
import com.summerbrochtrup.myeats.models.Restaurant;
import com.summerbrochtrup.myeats.ui.FindRestaurantDetailFragment;
import com.summerbrochtrup.myeats.ui.RestaurantDetailActivity;
import com.summerbrochtrup.myeats.util.OnRestaurantSelectedListener;
import com.squareup.picasso.Picasso;
import com.summerbrochtrup.myeats.util.RestaurantPropertyHelper;

import org.parceler.Parcels;

import java.util.ArrayList;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder> {
    private static final int MAX_WIDTH = 200;
    private static final int MAX_HEIGHT = 200;
    private ArrayList<Restaurant> mRestaurants = new ArrayList<>();
    private OnRestaurantSelectedListener mOnRestaurantSelectedListener;

    public RestaurantListAdapter(ArrayList<Restaurant> restaurants, OnRestaurantSelectedListener restaurantSelectedListener) {
        mRestaurants = restaurants;
        mOnRestaurantSelectedListener = restaurantSelectedListener;
    }

    @Override
    public RestaurantListAdapter.RestaurantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_list_item, parent, false);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view, mRestaurants, mOnRestaurantSelectedListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RestaurantListAdapter.RestaurantViewHolder holder, int position) {
        holder.bindRestaurant(mRestaurants.get(position));
    }

    @Override
    public int getItemCount() {
        return mRestaurants.size();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mRestaurantImageView;
        private TextView mNameTextView;
        private TextView mCategoryTextView;
        private TextView mRatingTextView;
        private Context mContext;
        private int mOrientation;
        private ArrayList<Restaurant> mRestaurants = new ArrayList<>();
        private OnRestaurantSelectedListener mRestaurantSelectedListener;

        public RestaurantViewHolder(View itemView, ArrayList<Restaurant> restaurants, OnRestaurantSelectedListener restaurantSelectedListener) {
            super(itemView);
            bindViews(itemView);
            mContext = itemView.getContext();
            mOrientation = itemView.getResources().getConfiguration().orientation;
            mRestaurants = restaurants;
            mRestaurantSelectedListener = restaurantSelectedListener;
            if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                createDetailFragment(0);
            }
            itemView.setOnClickListener(this);
        }

        private void bindViews(View view) {
            mRestaurantImageView = (ImageView) view.findViewById(R.id.restaurantImageView);
            mNameTextView = (TextView) view.findViewById(R.id.restaurantNameTextView);
            mCategoryTextView = (TextView) view.findViewById(R.id.categoryTextView);
            mRatingTextView = (TextView) view.findViewById(R.id.ratingTextView);
        }

        public void bindRestaurant(Restaurant restaurant) {
            Picasso.with(mContext)
                    .load(RestaurantPropertyHelper.getLargeImageUrl(restaurant.getImageUrl()))
                    .resize(MAX_WIDTH, MAX_HEIGHT)
                    .centerCrop()
                    .into(mRestaurantImageView);

            mNameTextView.setText(restaurant.getName());
            mCategoryTextView.setText(restaurant.getCategoryList().get(0));
            mRatingTextView.setText(String.format(mContext.getResources().getString(R.string.rating_format), restaurant.getRating()));
        }

        private void createDetailFragment(int position) {
            FindRestaurantDetailFragment detailFragment = FindRestaurantDetailFragment.newInstance(mRestaurants.get(position));
            FragmentTransaction ft = ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.restaurantDetailContainer, detailFragment);
            ft.commit();
        }

        @Override
        public void onClick(View v) {
            int itemPosition = getLayoutPosition();
            mRestaurantSelectedListener.onRestaurantSelected(mRestaurants.get(itemPosition), Constants.SOURCE_FIND);
            if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                createDetailFragment(itemPosition);
            } else {
                Intent intent = new Intent(mContext, RestaurantDetailActivity.class);
                intent.putExtra(Constants.EXTRA_KEY_RESTAURANT, Parcels.wrap(mRestaurants.get(itemPosition)));
                intent.putExtra(Constants.EXTRA_KEY_SOURCE, Constants.SOURCE_FIND);
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) mContext, mRestaurantImageView,
                                mContext.getResources().getString(R.string.transition_name_rest_img));
                mContext.startActivity(intent, options.toBundle());
            }
        }
    }
}