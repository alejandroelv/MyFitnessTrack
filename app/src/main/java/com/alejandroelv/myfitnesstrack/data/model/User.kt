package com.alejandroelv.myfitnesstrack.data.model

import android.os.Parcel
import android.os.Parcelable

data class User(
    var email: String = "",
    var password: String = "",
    var gender: Int = 0,
    var age: Int = 0,
    var weight: Int = 0,
    var height: Int = 0,
    var activityLevel: Int = 0,
    var goal: Int = 0,
    var goalByWeek: Double = 0.0
) : Parcelable {
    // add parcelable implementation here
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeInt(gender)
        parcel.writeInt(age)
        parcel.writeInt(weight)
        parcel.writeInt(height)
        parcel.writeInt(activityLevel)
        parcel.writeInt(goal)
        parcel.writeDouble(goalByWeek)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}