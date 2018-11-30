package ru.kingbird.nasapictures.data.local;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import ru.kingbird.nasapictures.data.Photo;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PhotoDao {
    @Query("SELECT * FROM photo")
    List<Photo> getAll();

    @Insert(onConflict = REPLACE)
    void insertAll(Photo... photos);

    @Insert(onConflict = REPLACE)
    void insert(Photo photo);

    @Delete
    void delete(Photo photo);

    @Query("DELETE FROM photo")
    void deleteAll();

    @Query("SELECT * FROM photo WHERE photoId LIKE :photoId LIMIT 1")
    Photo findById(Integer photoId);

    @Query("SELECT * FROM photo WHERE deleted = 1 ")
    List<Photo> loadDeletedPhotos();

}
