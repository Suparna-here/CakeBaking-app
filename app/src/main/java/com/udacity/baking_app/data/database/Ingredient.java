package com.udacity.baking_app.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Room;
import android.os.Parcel;
import android.os.Parcelable;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(indices = {@Index("recipe_id")},foreignKeys = @ForeignKey(entity = Recipe.class,
        parentColumns = "id",
        childColumns = "recipe_id",
        onDelete = CASCADE))
public class Ingredient implements Parcelable {
    /*"ingredients": [
    {
        "quantity": 2,
            "measure": "CUP",
            "ingredient": "Graham Cracker crumbs"
    },*/

     /**
     * Makes sure the id is the primary key (ensures uniqueness), is auto generated by {@link Room}.
     */
    @PrimaryKey(autoGenerate = true)
    private int ingredient_id;
    private float quantity;
    private String measure;
    private String ingredient;
    private long recipe_id;

    public int getIngredient_id() {
        return ingredient_id;
    }

    public float getQuantity() {
        return quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public long getRecipe_id() {
        return recipe_id;
    }

    // Define Primary Key for Room
    public Ingredient(int ingredient_id, float quantity, String measure, String ingredient, long recipe_id) {
        this.ingredient_id=ingredient_id;
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
        this.recipe_id=recipe_id;
    }

    @Ignore
    public Ingredient(float quantity, String measure, String ingredient, long recipe_id) {
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
        this.recipe_id=recipe_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Ignore
    public Ingredient(Parcel in) {
        quantity = in.readInt();
        measure = in.readString();
        ingredient = in.readString();
    }

    @Override
    public void writeToParcel(Parcel destParcel, int flags) {
        destParcel.writeFloat(quantity);
        destParcel.writeString(measure);
        destParcel.writeString(ingredient);
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}
