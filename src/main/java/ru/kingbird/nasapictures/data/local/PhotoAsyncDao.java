package ru.kingbird.nasapictures.data.local;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import io.reactivex.Observable;
import io.reactivex.Single;
import ru.kingbird.nasapictures.data.Photo;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface PhotoAsyncDao {
    @Query("SELECT * FROM photo")
    Observable<List<Photo>> getAll();

    @Insert(onConflict = REPLACE)
    void insertAll(Photo... photos);

    @Delete
    void delete(Photo photo);

    @Query("DELETE FROM photo")
    void deleteAll();

    @Query("SELECT * FROM photo WHERE photoId LIKE :photoId LIMIT 1")
    Single<Photo> findById(Integer photoId);

    @Query("SELECT * FROM photo WHERE deleted = 1 ")
    Observable<List<Photo>> loadDeletedPhotos();

    @Query("SELECT * FROM photo WHERE deleted = 0 ")
    Observable<List<Photo>> loadNotDeletedPhotos();

}
