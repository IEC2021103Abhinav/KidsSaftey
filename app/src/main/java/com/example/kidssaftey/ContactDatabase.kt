package com.example.kidssaftey

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//basically abstract class wo hota hai jismein ham log function ko declare karenge keval
//unko define nahi karna hai

@Database(entities = [ContactModel::class], version = 1, exportSchema = false)
public abstract class ContactDatabase:RoomDatabase() {
    abstract fun contactDao():ContactDao
//    singelton classes wo hoti hai jinka bas ek hi instance hota hai
//    ye database jahan jahan pe bhi use hota hai wahan pe wo log apni instance bana lete hai
//    so that why we use singelton class wo bas ek hi baar apni instance banate hai
//    aur usi ko use karte hai hamesa repeatedly
//    companion object ke andar jo bhi ham function ya var define karte wo overall project kahin bhi use ho sakta hai
//    volatile -->means ham kisi bhi thrads se use ho sakta hai(apne code ko thread safe banata hai)


    companion object{
    @Volatile
    private var INSTANCE:ContactDatabase?=null
        fun getDatabase(context:Context):ContactDatabase{
//            if(INSTANCE!=null)
//            {
//                return INSTANCE
//                smartcast matlab instance ko jabtak ye check kar rahe hai tab tak mein agar usmein null chala
//                gya ho to null return kar dega
//                so we use let funcion in which instance ka ye copy bana lega  it ke naam se aur usi ko return karega

//            }
            INSTANCE?.let {
                return it
            }
//            agar instance null nikla to hame naya instance banana padega
//            basicallly hame ye synchronized mein banana rahta hai
            return synchronized(ContactDatabase::class.java)
            {
                val instance=Room.databaseBuilder(context.applicationContext,ContactDatabase::class.java,"Contact_db").build()
                INSTANCE=instance
                instance
            }

        }
    }

}