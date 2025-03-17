package com.example.studentregister.db

import androidx.room.Database

@Database(entities = [Student::class], version = 1, exportSchema = false)
abstract class StudentDatabase {

    abstract fun studentDao():StudentDao
}