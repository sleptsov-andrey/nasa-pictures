package ru.kingbird.nasapictures.data.remote.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Camera {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("rover_id")
    @Expose
    private Integer roverId;
    @SerializedName("full_name")
    @Expose
    private String fullName;

    public String getFullName() {
        return fullName;
    }
}
