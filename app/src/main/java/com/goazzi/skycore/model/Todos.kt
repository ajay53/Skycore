package com.goazzi.skycore.model

import android.os.Parcel
import android.os.Parcelable
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
//import androidx.room.Entity
//import androidx.room.PrimaryKey
//import com.squareup.moshi.Json

//@Entity
class Todos() : BaseObservable(), Parcelable {

    @get:Bindable
    var userId: Int = 0
        set(value) {
            field = value
//            notifyPropertyChanged(BR.userId)
        }

    @get:Bindable
//    @PrimaryKey
    var id: Int = 0
        set(value) {
            field = value
//            notifyPropertyChanged(BR.id)
        }

    @get:Bindable
    var title: String = ""
        set(value) {
            field = value
//            notifyPropertyChanged(BR.title)
        }

    @get:Bindable
//    @field:Json(name = "completed")
    var completed: Boolean = false
        set(value) {
            field = value
//            notifyPropertyChanged(BR.completed)
        }

    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Todos> {
        override fun createFromParcel(parcel: Parcel): Todos {
            return Todos(parcel)
        }

        override fun newArray(size: Int): Array<Todos?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "userId: $userId || id: $id || title: $title || completed: $completed"
    }
}