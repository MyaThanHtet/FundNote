package com.mth.bettingnote

import androidx.room.*

@Dao
interface NoteDao {
    @Query("SELECT * FROM noteTbl ORDER BY id DESC")
    fun allNote(): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(note: Note)

    /* @Query("DELETE FROM noteTbl WHERE id =:id")
     fun delete(id:Int)*/
    @Delete
    fun delete(note: Note)

    @Update
    fun update(note:Note)
    @Query("SELECT SUM(amount) FROM noteTbl WHERE ispaid=:isPaid")
    fun totalAmount(isPaid:Boolean=true):Int
}