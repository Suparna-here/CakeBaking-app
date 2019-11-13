package com.udacity.baking_app.data.database;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class MovieReviewResponse {


    private List<MovieReviewResponse.Review> results;

    public void setMovieReviewlist(List<MovieReviewResponse.Review> results) {
        this.results = results;
    }

    public List<MovieReviewResponse.Review> getMovieReviewlist() {
        return results;
    }

    public static class Review implements Parcelable {
        private String author;//"uridon"
        private String content;//"It's the epitome of superhero trash and the franchise of manchildren.  The ultimate smashing your toys together to battle only this time it's for the world to see."
        private String id;//"5b97fa459251413c68000753"
        private String url;//"https://www.themoviedb.org/review/5b97fa459251413c68000753"

        public static final Creator<MovieReviewResponse.Review> CREATOR = new Creator<MovieReviewResponse.Review>() {
            @Override
            public MovieReviewResponse.Review createFromParcel(Parcel in) {
                return new MovieReviewResponse.Review(in);
            }

            @Override
            public MovieReviewResponse.Review[] newArray(int size) {
                return new MovieReviewResponse.Review[size];
            }
        };

        public String getAuthor() {
            return author;
        }

        public String getContent() {
            return content;
        }

        public String getId() {
            return id;
        }

        public String getUrl() {
            return url;
        }

        Review(Parcel in) {
            author = in.readString();
            content = in.readString();
            id = in.readString();
            url = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel destParcel, int flags) {
            destParcel.writeString(author);
            destParcel.writeString(content);
            destParcel.writeString(id);
            destParcel.writeString(url);
        }
    }
}
