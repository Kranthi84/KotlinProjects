package com.example.chinnakotla.taskslist

import android.os.Parcel
import android.os.Parcelable

class TaskList(val taskName: String, val taskList: ArrayList<String> = ArrayList()) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.createStringArrayList())

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(taskName)
        dest?.writeStringList(taskList)
    }

    override fun describeContents(): Int = 0


    companion object CREATOR : Parcelable.Creator<TaskList> {
        override fun createFromParcel(parcel: Parcel): TaskList {
            return TaskList(parcel)
        }

        override fun newArray(size: Int): Array<TaskList?> {
            return arrayOfNulls(size)
        }
    }
}