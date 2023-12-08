package com.sum.common.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import kotlinx.android.parcel.Parcelize;

/**
 * @author why
 * @date 2023/11/23 14:20
 */

@Parcelize
public class UserInfo implements Parcelable {


    @JsonProperty("token")
    public String token;
    @JsonProperty("userId")
    public Integer userId;

    @JsonProperty("phone")
    public String phone;

    protected UserInfo(Parcel in) {
        token = in.readString();
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readInt();
        }
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(token);
        parcel.writeInt(userId);
        parcel.writeString(phone);
    }
}
