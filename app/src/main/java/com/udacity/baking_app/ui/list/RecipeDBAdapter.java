package com.udacity.baking_app.ui.list;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.baking_app.R;
import com.udacity.baking_app.data.database.Recipe;

import java.util.List;


/**
 * {@link RecipeDBAdapter} exposes a list of recipe details to a
 * {@link android.support.v7.widget.RecyclerView}
 */
public class RecipeDBAdapter extends RecyclerView.Adapter<RecipeDBAdapter.RecipeDBAdapterViewHolder> {
    private static final int VIEW_TYPE_REGULAR = 0;

    private List<Recipe> mRecipeData;


    private final RecipeDBAdapterOnClickHandler mClickHandler;

    public interface RecipeDBAdapterOnClickHandler {
        void itemClickListener(Recipe recipe);
    }

    public RecipeDBAdapter(RecipeDBAdapterOnClickHandler mClickHandler) {
        this.mClickHandler = mClickHandler;
    }

    /**
     * Cache of the children views for a recipe list/grid item.
     */
    public class RecipeDBAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView dishImageIV;
        public final TextView dishNameTV;
        public final TextView servingsTV;


        public RecipeDBAdapterViewHolder(View view) {
            super(view);
            dishImageIV = (ImageView) view.findViewById(R.id.iv_dish_image);
            dishNameTV = (TextView) view.findViewById(R.id.tv_dish_name);
            servingsTV = (TextView) view.findViewById(R.id.tv_servings);

            // Call setOnClickListener on the view passed into the constructor (use 'this' as the OnClickListener)
            view.setOnClickListener(this);
        }

        // Override onClick, passing the clicked Recipe's data to mClickHandler via its onClick method
        @Override
        public void onClick(View view) {
            mClickHandler.itemClickListener(mRecipeData.get(getAdapterPosition()));
        }
    }

    @NonNull
    @Override
    public RecipeDBAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = getLayoutIdByType(viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        view.setFocusable(true);
        return new RecipeDBAdapterViewHolder(view);
    }

    /**
     * Returns the the layout id depending on whether the movie item is a normal item or the favourite movie item.
     *
     * @param viewType
     * @return
     */
    private int getLayoutIdByType(int viewType) {
        switch (viewType) {

            case VIEW_TYPE_REGULAR: {
                return R.layout.recipe_list_item;
            }

            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeDBAdapterViewHolder holder, int position) {
        Recipe recipe = mRecipeData.get(position);
        String posteUrl = recipe.getImage();
        if (!TextUtils.isEmpty(posteUrl))
            Picasso.get()
                    .load(posteUrl)
                    .placeholder(R.drawable.ic_cake_black_150dp)
                    .into(holder.dishImageIV);
        holder.dishNameTV.setText(recipe.getName());
        holder.servingsTV.setText(Integer.toString(recipe.getServings()));
    }

    @Override
    public int getItemCount() {
        if (null == mRecipeData) return 0;
        return mRecipeData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_REGULAR;
    }

    /*
     * This method is used to set the movie data on a RecipeDBAdapter
     * @param recipeData The new recipe data to be displayed.
     */
    public void setRecipeData(List<Recipe> recipeData) {
        mRecipeData = recipeData;
        notifyDataSetChanged();
    }
}
