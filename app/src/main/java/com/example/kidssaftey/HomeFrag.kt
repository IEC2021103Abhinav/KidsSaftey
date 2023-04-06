package com.example.kidssaftey

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeFrag : Fragment() {

    lateinit var inviteAdapter:InviteAdapter
    lateinit var mContext: Context
    private val contactList:ArrayList<ContactModel> = ArrayList()
    private var callBtn:ImageView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }

    override fun onCreateView(
//        view ko create kar rahe hai
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }



//    lifecycle events
//        view create ho chuka hai
//        aab aap uske saath khel rahe honge
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    val memberslist= listOf<MemberModel>(
        MemberModel(
            "Anusha",
            "9th building, 2nd floor, maldiv road, manali 9th building, 2nd floor",
            "90%",
            "223",
            "On"
        ),
        MemberModel(
            "Yash",
            "10th building, 3rd floor, maldiv road, manali 10th building, 3rd floor",
            "80%",
            "210",
            "Off"
        ),
        MemberModel(
            "Ramesh",
            "12th buildind, 5th floor, maldiv road, manali 12th building, 5th floor",
            "60%",
            "190",
            "On"
        ),
    )
    val memberAdapter=MemberAdapter(memberslist)
    val recyclerView=requireView().findViewById<RecyclerView>(R.id.recycler_member)
//    fargment ke pass context nahi hota hai to isko view dena padta hai
    recyclerView.layoutManager=LinearLayoutManager(mContext)
    recyclerView.adapter=memberAdapter




//    here we work for Contacts
    inviteAdapter=InviteAdapter(contactList)
    fetchDatabaseContacts()
    CoroutineScope(Dispatchers.IO).launch {


//        yahan pe ham user ke phone se fetch kar rahe hai to slow respond karta hai
//        isi liye ham ab directly database se fetch kar lete hai to slow nahi work karega
        insertDatabaseContacts(fetchContactsFromUser())

//        threads switching from ui threads to main
//        ku ki ab hamein main mein switch karna hai
//        ku ki inviteAdapter wala kaam main thread mein chal rha hai
//        but isse ek problem ho rha hai ki jo data hai contact ka wo thora der baad aa raha hai
//        to uske liye ham apna database banayenge jissse wo turant fetch kar lega phir baaad mein background se
//        ye to work karega hi to wo update kar dega
    }


    val inviteRecycler=requireView().findViewById<RecyclerView>(R.id.recycler_invite)
//    for horizontal recycler
    inviteRecycler.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
    inviteRecycler.adapter=inviteAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchDatabaseContacts() {
        val database=ContactDatabase.getDatabase(requireContext())
        database.contactDao().getAllContacts().observe(viewLifecycleOwner){
            contactList.clear()
            contactList.addAll(it)
//            it hai apna list of contact model
            inviteAdapter.notifyDataSetChanged()

        }

    }


    private suspend fun insertDatabaseContacts(contactList: ArrayList<ContactModel>)
    {
        val database=ContactDatabase.getDatabase(mContext)
        database.contactDao().insertAll(contactList)
    }


    @SuppressLint("Range", "Recycle")
    private fun fetchContactsFromUser(): ArrayList<ContactModel> {
//        there is a content provider which provide the data like contacts of your mobile,calender etc
//        content resolver basically fetch content from content provider and give to us
//        cursor basically column by column jata hai database mein
//        cursor.movetonext ye batata hai ki cursor mein next element hai ya nahi


//        for storing number we have to create a list

        val contactList:ArrayList<ContactModel> = ArrayList()


        val contentresolver=mContext.contentResolver
        val cursor=contentresolver.query(ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            null)
        if(cursor!=null&&cursor.count>0)
        {
            while(cursor.moveToNext())
            {
                val id=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                val isPhoneNumber=cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
//                basically ye return 1 ya 0 karega agar hai to 1 karega
                if(isPhoneNumber>0)
                {
//                    ab basically phone ke database se hame number lana padega
                    val phoneCursor=contentresolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" = ?",
                    arrayOf(id),
                        ""
                    )
                    if(phoneCursor!=null&&phoneCursor.count>0)
                    {
                        while(phoneCursor.moveToNext())
                        {
                            val phoneNum=phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            contactList.add(ContactModel(name,phoneNum))
                        }
//                        hame hamesa cursor ko close karna hai
//                        use open nahi chorna hai
                        phoneCursor.close()
                    }
                }
            }
            cursor.close()
        }
        return contactList
    }


    companion object {
        @JvmStatic
        fun newInstance() =HomeFrag()

    }
}