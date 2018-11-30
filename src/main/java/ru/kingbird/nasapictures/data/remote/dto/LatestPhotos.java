package ru.kingbird.nasapictures.data.remote.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import ru.kingbird.nasapictures.data.Photo;

public class LatestPhotos {

        @SerializedName("latest_photos")
        @Expose
        private List<Photo> latestPhotos = null;

        public List<Photo> getLatestPhotos() {
            return latestPhotos;
        }

        public void setLatestPhotos(List<Photo> latestPhotos) {
            this.latestPhotos = latestPhotos;
        }

    }

