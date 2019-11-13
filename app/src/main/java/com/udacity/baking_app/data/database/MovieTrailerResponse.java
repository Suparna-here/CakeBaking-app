package com.udacity.baking_app.data.database;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class MovieTrailerResponse {
    private List<MovieTrailerResponse.Trailer> results;

    public void setMovieTrailerlist(List<MovieTrailerResponse.Trailer> results) {
        this.results = results;
    }

    public List<MovieTrailerResponse.Trailer> getMovieTrailerlist() {
        return results;
    }

    public static class Trailer implements Parcelable {
        private String id;//"5a7c6a35c3a3680f7f01053a"
        private String iso_639_1;//"en"
        private String iso_3166_1;//"US"
        private String key;//"dzxFdtWmjto"
        private String name;//"VENOM - Official Teaser Trailer (HD)",
        private String site;//"YouTube"
        private long size;//1080
        private String type;//"Teaser"

        public static final Creator<MovieTrailerResponse.Trailer> CREATOR = new Creator<MovieTrailerResponse.Trailer>() {
            @Override
            public MovieTrailerResponse.Trailer createFromParcel(Parcel in) {
                return new MovieTrailerResponse.Trailer(in);
            }

            @Override
            public MovieTrailerResponse.Trailer[] newArray(int size) {
                return new MovieTrailerResponse.Trailer[size];
            }
        };

        public String getId() {
            return id;
        }

        public String getLanguage() {
            return iso_639_1;
        }

        public String getCountry() {
            return iso_3166_1;
        }

        public String getKey() {
            return key;
        }

        public String getName() {
            return name;
        }

        public String getSite() {
            return site;
        }

        public long getSize() {
            return size;
        }

        public String getType() {
            return type;
        }

        Trailer(Parcel in){
            id=in.readString();
            iso_639_1=in.readString();
            iso_3166_1=in.readString();
            key=in.readString();
            name=in.readString();
            site=in.readString();
            size=in.readLong();
            type=in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel destParcel, int flags) {
            destParcel.writeString(id);
            destParcel.writeString(iso_639_1);
            destParcel.writeString(iso_3166_1);
            destParcel.writeString(key);
            destParcel.writeString(name);
            destParcel.writeString(site);
            destParcel.writeLong(size);
            destParcel.writeString(type);
        }
    }
}
