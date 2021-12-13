package com.skvoznyak.findart.model

import android.annotation.SuppressLint
import android.util.Log
import com.pacoworks.rxpaper2.RxPaperBook
import io.reactivex.CompletableObserver
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

object StorageManager {

    val book = RxPaperBook.with("saved_pictures", Schedulers.newThread())

    fun write(key: String, value: Picture) {
        val write = book.write(key, value)
        write.subscribe(
            object : CompletableObserver {
                override fun onComplete() {
                    Log.d("ivan", "write: Ok")
                }
                override fun onError(e: Throwable) {
                    Log.d("ivan", "error while writing picture into storage")
                }
                override fun onSubscribe(d: Disposable) {}
            }
        )
    }

    @SuppressLint("CheckResult")
    fun read(keys: List<String>): Single<List<Picture>> {
        return Observable.fromIterable(keys)
            .flatMapSingle { book.read<Picture>(it) }
            .toList()
    }

    fun delete(key: String) {
        val delete = book.delete(key)
        delete.subscribe(
            object : CompletableObserver {
                override fun onComplete() {
                    Log.d("ivan", "delete: Ok")
                }
                override fun onError(e: Throwable) {
                    Log.d("ivan", "error while deleting picture from storage")
                }
                override fun onSubscribe(d: Disposable) {}
            }
        )
    }

    @SuppressLint("CheckResult")
    fun contains(key: String, callback: ((Boolean) -> Unit)) {
        val contains = book.contains(key)

        contains.subscribe(
            { res -> callback(res) },
            { e -> Log.d("ivan", "error: $e") }
        )
    }

    @SuppressLint("CheckResult")
    fun getAll(callback: ((Single<List<Picture>>) -> Unit)) {
        try {
            Log.d("ivan", "trying get all")
            val keys = book.keys()

            keys.subscribe(
                { keyList ->
                    Log.d("ivan", "keys: $keyList")
                    callback(read(keyList))
                },
                { e -> Log.d("ivan", "error: $e") }
            )
        } catch (e: Exception) {
            Log.d("ivan", "Well, error in getAll")
            e.printStackTrace()
        }
    }
}
