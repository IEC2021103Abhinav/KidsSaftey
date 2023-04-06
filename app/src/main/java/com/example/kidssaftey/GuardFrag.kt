package com.example.kidssaftey

import android.content.Context
import android.os.Binder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kidssaftey.databinding.FragmentGuardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GuardFrag : Fragment(),InviteMailAdapter.OnActionClick {
    private lateinit var mContext:Context
    lateinit var binding: FragmentGuardBinding
    lateinit var auth:FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth=FirebaseAuth.getInstance()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding=FragmentGuardBinding.inflate(inflater, container, false)
        binding.sendInvite.setOnClickListener{
            sendInviteToOthers()
        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getInvitesFromFireStore()

    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun getInvitesFromFireStore() {
        val db=Firebase.firestore
        val senderMail=auth.currentUser?.email.toString()

        db.collection("users")
            .document(senderMail)
            .collection("invites").get().addOnCompleteListener{
                if(it.isSuccessful)
                {
                    val list : ArrayList<String> = ArrayList()
                    for(item in it.result)
                    {
                        if(item.get("invite_status")==0L)
                        {
                            list.add(item.id)

                        }
                    }
                    val adapter=InviteMailAdapter(list,this)
                    binding.inviteRecycler.layoutManager=LinearLayoutManager(mContext)
//                    as we already done it in the xml
                    binding.inviteRecycler.adapter=adapter
                }
            }
    }

    private fun sendInviteToOthers() {
        val gmail=binding.inviteMail.text.toString()
        val db=Firebase.firestore
        val data= hashMapOf(
            "invite_status" to 0
        )
        val senderMail=auth.currentUser?.email.toString()
        db.collection("users")
            .document(gmail)
            .collection("invites")
            .document(senderMail).set(data)
            .addOnSuccessListener {

            }.addOnFailureListener {

            }
    }




    companion object {
        @JvmStatic
        fun newInstance() =GuardFrag()
    }

    override fun onAcceptClick(mail: String) {
        val db=Firebase.firestore
        val data= hashMapOf(
            "invite_status" to 1
        )
        val senderMail=auth.currentUser?.email.toString()
        db.collection("users")
            .document(senderMail)
            .collection("invites")
            .document(mail).set(data)
            .addOnSuccessListener {

            }.addOnFailureListener {

            }
    }

    override fun onDenyClick(mail: String) {
        val db=Firebase.firestore
        val data= hashMapOf(
            "invite_status" to -1
        )
        val senderMail=auth.currentUser?.email.toString()
        db.collection("users")
            .document(senderMail)
            .collection("invites")
            .document(mail).set(data)
            .addOnSuccessListener {

            }.addOnFailureListener {

            }
    }
}