package ru.kingbird.nasapictures.data.local;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import ru.kingbird.nasapictures.data.Photo;

@Database(entities = {Photo.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class PhotosDatabase extends RoomDatabase {

    private static PhotosDatabase singleton;

    private static final String DATABASE_NAME = "PhotoRoomDb.db";

    public abstract PhotoDao photoDao();

    public abstract PhotoAsyncDao photoAsyncDao();

    public static PhotosDatabase getAppDatabase(Context context) {
        if (singleton == null) {
            synchronized (PhotosDatabase.class) {
                if (singleton == null) {
                    singleton = Room.databaseBuilder(context.getApplicationContext(),
                            PhotosDatabase.class,
                            DATABASE_NAME)
                            .build();
                }
            }
        }
        return singleton;
    }

}
