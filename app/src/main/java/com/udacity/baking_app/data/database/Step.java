package com.udacity.baking_app.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import static android.arch.persistence.room.ForeignKey.CASCADE;


@Entity(indices = {@Index("recipe_id")},foreignKeys = @ForeignKey(entity = Recipe.class,
        parentColumns = "id",
        childColumns = "recipe_id",
        onDelete = CASCADE))
public class Step implements Parcelable {
    /*"steps": [
    {
            "id": 0,
            "shortDescription": "Recipe Introduction",
            "description": "Recipe Introduction",
            "videoURL": "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4",
            "thumbnailURL": ""
    },*/

    @PrimaryKey(autoGenerate = true)
    private long step_Id;
    private long id;
    private String shortDescription;
    private String description;
    private String videoURL;
    private String thumbnailURL;
    private long recipe_id;

    public long getStep_Id() {
        return step_Id;
    }

    public long getId() {
        return id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public long getRecipe_id() {
        return recipe_id;
    }

    // Define Primary Key for Room
    public Step(long step_Id, long id, String shortDescription, String description, String videoURL, String thumbnailURL, long recipe_id) {
        this.step_Id=step_Id;
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
        this.recipe_id=recipe_id;
    }

    @Ignore
    public Step(long id, String shortDescription, String description, String videoURL, String thumbnailURL) {
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Ignore
    public Step(Parcel in) {
        id = in.readLong();
        shortDescription = in.readString();
        description = in.readString();
        videoURL = in.readString();
        thumbnailURL = in.readString();
    }

    @Override
    public void writeToParcel(Parcel destParcel, int flags) {
        destParcel.writeLong(id);
        destParcel.writeString(shortDescription);
        destParcel.writeString(description);
        destParcel.writeString(videoURL);
        destParcel.writeString(thumbnailURL);
    }

    public static final Creator<Step> CREATOR = new Creator<Step>() {
        @Override
        public Step createFromParcel(Parcel in) {
            return new Step(in);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };
}

