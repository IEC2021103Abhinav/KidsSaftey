package com.example.kidssaftey

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ContactDao {

//    dao-->data access object
//    primary key hamesa unique hona chaihiye but agar conflict aa jaye to nay wale ko laete hai purane wale ko ignore
//    we have replace with new one
//    hame basically contact model ko insert karna rahta hai
//    as it is a heavy task of inserting then it will not be on main threads
//    it should be in coroutines
//    so suspend function always work in coroutines not in main threads

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contactModel: ContactModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contactModelList:List<ContactModel>)

    @Query("SELECT * FROM contactModel")
    fun getAllContacts():LiveData<List<ContactModel>>

}