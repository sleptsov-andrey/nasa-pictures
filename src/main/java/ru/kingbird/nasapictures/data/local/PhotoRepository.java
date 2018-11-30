package ru.kingbird.nasapictures.data.local;

import android.content.Context;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import ru.kingbird.nasapictures.data.Photo;

public class PhotoRepository {

    private final Context mContext;

    public PhotoRepository(Context mContext) {
        this.mContext = mContext;
    }

    public Completable saveData(final List<Photo> photoList) {
        return Completable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                PhotosDatabase db = PhotosDatabase.getAppDatabase(mContext);

                Photo[] photos = photoList.toArray(new Photo[photoList.size()]);

                db.photoDao().insertAll(photos);

                return null;
            }
        });
    }

    public Single<List<Photo>> getData() {

        return Single.fromCallable(new Callable<List<Photo>>() {
            @Override
            public List<Photo> call() throws Exception {
                PhotosDatabase db = PhotosDatabase.getAppDatabase(mContext);

                return db.photoDao().getAll();
            }
        });
    }


    public Observable<List<Photo>> getDataObservable() {
        PhotosDatabase db = PhotosDatabase.getAppDatabase(mContext);

        return db.photoAsyncDao().getAll();
    }

    public Completable setPhotoDeleted(final Photo photo) {
        return Completable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                PhotosDatabase db = PhotosDatabase.getAppDatabase(mContext);
                db.photoDao().insert(photo);
                return null;
            }
        });
    }

    public Observable<List<Photo>> loadDeletedPhotos() {
        PhotosDatabase db = PhotosDatabase.getAppDatabase(mContext);

        return db.photoAsyncDao().loadDeletedPhotos();
    }

    public Observable<List<Photo>> loadNotDeletedPhotos() {
        PhotosDatabase db = PhotosDatabase.getAppDatabase(mContext);

        return db.photoAsyncDao().loadNotDeletedPhotos();
    }

}
