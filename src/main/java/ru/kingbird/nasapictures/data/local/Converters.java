package ru.kingbird.nasapictures.data.local;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import androidx.room.TypeConverter;
import ru.kingbird.nasapictures.data.remote.dto.Camera;

public class Converters {
    @TypeConverter
    public static Camera stringToCamera(String value) {
        Type listType = new TypeToken<Camera>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String cameraToString(Camera camera) {
        Gson gson = new Gson();
        String json = gson.toJson(camera);
        return json;
    }
}
