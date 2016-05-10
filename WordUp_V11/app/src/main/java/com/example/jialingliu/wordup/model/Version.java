package com.example.jialingliu.wordup.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

/**
 * Created by jialingliu on 4/17/16.
 */
public class Version implements Parcelable {
    String version;

    public Version() {}

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String toGson() {
        return new Gson().toJson(this);
    }

    @Override
    public String toString() {
        return toGson();
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.version);
    }

    private Version(Parcel in) {
        this.version = in.readString();
    }

    public static final Parcelable.Creator<Version> CREATOR = new Parcelable.Creator<Version>() {
        public Version createFromParcel(Parcel source) {return new Version(source);}

        public Version[] newArray(int size) {return new Version[size];}
    };

    public static void main(String[] a) {
        Version version1 = new Version();
        version1.version = "2.0";
        System.out.println(new Gson().toJson(version1, Version.class));
    }
}
