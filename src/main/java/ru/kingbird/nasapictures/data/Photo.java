package ru.kingbird.nasapictures.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import ru.kingbird.nasapictures.data.remote.dto.Camera;

@Entity(tableName = "photo")
public class Photo {

    public Photo() {
    }

    @Ignore
    public Photo(@NonNull Integer photoId, Boolean deleted, Camera camera, String img_src) {
        this.photoId = photoId;
        this.deleted = deleted;
        this.camera = camera;
        this.imgSrc= img_src;
    }

    @SerializedName("id")
    @Expose
    @NonNull
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "photoId")
    private Integer photoId;

    @ColumnInfo(name = "deleted")
    private Boolean deleted = false;

    @SerializedName("camera")
    @Expose
    @ColumnInfo(name = "camera")
    private Camera camera;

    @SerializedName("img_src")
    @Expose
    @ColumnInfo(name = "img_src")
    private String imgSrc;

    @NonNull
    public Integer getPhotoId() {
        return photoId;
    }

    public void setPhotoId(@NonNull Integer photoId) {
        this.photoId = photoId;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }


    public Boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "Id='" + photoId + '\'' +
                ", description='" + camera.getFullName() + '\'' +
                ", path='" + deleted + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return Objects.equals(getPhotoId(), photo.getPhotoId());
    }
}
