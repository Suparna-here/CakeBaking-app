package com.udacity.baking_app.ui.detail;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.udacity.baking_app.R;
import com.udacity.baking_app.data.database.Step;

import java.util.List;


/**
 * {@link RecipeDetailListDBAdapter} exposes a list of movie details to a
 * {@link RecyclerView}
 */
public class RecipeDetailListDBAdapter extends RecyclerView.Adapter<RecipeDetailListDBAdapter.RecipeDetailListDBAdapterViewHolder>{
    private static final int VIEW_TYPE_REGULAR = 0;
    private static final int VIEW_TYPE_FAVOURITE = 1;
    private List<Step> mStepData;

    private final RecipeDetailListDBAdapterOnClickHandler mClickHandler;

    public interface RecipeDetailListDBAdapterOnClickHandler {
        void itemClickListener(Step step);
    }

    public RecipeDetailListDBAdapter(RecipeDetailListDBAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    /**
     * Cache of the children views for a movie grid item.
     */
    public class RecipeDetailListDBAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView dishImageIV;
        public final TextView dishNameTV;
        public final TextView servingsTV;



        public RecipeDetailListDBAdapterViewHolder(View view) {
            super(view);
            dishImageIV = (ImageView) view.findViewById(R.id.iv_dish_image);
            dishNameTV =(TextView)view.findViewById(R.id.tv_dish_name);
            servingsTV=(TextView)view.findViewById(R.id.tv_servings);

            // Call setOnClickListener on the view passed into the constructor (use 'this' as the OnClickListener)
            view.setOnClickListener(this);
        }

        // Override onClick, passing the clicked Recipe's data to mClickHandler via its onClick method
        @Override
        public void onClick(View view) {
            mClickHandler.itemClickListener(mStepData.get(getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public RecipeDetailListDBAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = getLayoutIdByType(viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        view.setFocusable(true);
        return new RecipeDetailListDBAdapterViewHolder(view);
    }

    /**
     * Returns the the layout id depending on whether the movie item is a normal item or the favourite movie item.
     * @param viewType
     * @return
     */
    private int getLayoutIdByType(int viewType) {
        switch (viewType) {

            case VIEW_TYPE_REGULAR: {
                return R.layout.step_list_item;
            }

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeDetailListDBAdapterViewHolder holder, int position) {
        Step step = mStepData.get(position);
      /*  String posteUrl= ServiceGenerator.POSTER_URL+ recipe.getPoster_path();
        Picasso.get()
               .load(posteUrl)
               .placeholder(R.mipmap.ic_launcher)
               .into(holder.posterThumbnailIV);*/
        holder.dishNameTV.setText(step.getShortDescription());
//        holder.servingsTV.setText(Integer.toString(recipe.getServings()));
    }

    @Override
    public int getItemCount() {
        if (null == mStepData) return 0;
        return mStepData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_REGULAR;
        /*if(sort_by.equals(ServiceGenerator.ORDER_POPULARITY) || sort_by.equals(ServiceGenerator.ORDER_TOPRATED))
            return VIEW_TYPE_FAVOURITE;*/
    }

    /*
     * This method is used to set the movie data on a RecipeDBAdapter
     * @param recipeData The new movie data to be displayed.
     */
    public void setStepDataOfRecipe(List<Step> stepDataOfRecipe) {
        mStepData = stepDataOfRecipe;
        notifyDataSetChanged();
    }
}
