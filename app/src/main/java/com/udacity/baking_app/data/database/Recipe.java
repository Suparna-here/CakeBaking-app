package com.udacity.baking_app.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

@Entity(indices = {@Index(value = {"name"}, unique = true)})
public class Recipe implements Parcelable {
    @PrimaryKey
    private long id;      // "id": 1
    private String name;  // "name": "Nutella Pie"

    @Ignore
    private ArrayList<Ingredient> ingredients;
    @Ignore
    private ArrayList<Step> steps;

    private int servings; //  "servings": 8
    private String image; //  "image": ""

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    public int getServings() {
        return servings;
    }

    public String getImage() {
        return image;
    }

    public Recipe(long id, String name, int servings, String image) { // ArrayList<Ingredient> ingredients, ArrayList<Step> steps
        this.id = id;
        this.name = name;
        this.servings = servings;
        this.image = image;
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Ignore
    public Recipe(Parcel in) {
        id = in.readLong();
        name = in.readString();

        ingredients = in.readArrayList(Ingredient.class.getClassLoader());
        steps = in.readArrayList(Step.class.getClassLoader());

        servings = in.readInt();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel destParcel, int flags) {
        destParcel.writeLong(id);
        destParcel.writeString(name);

        destParcel.writeList(ingredients);
        destParcel.writeList(steps);

        destParcel.writeInt(servings);
        destParcel.writeString(image);
    }

}