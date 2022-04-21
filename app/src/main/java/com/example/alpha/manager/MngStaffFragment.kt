package com.example.alpha.manager

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alpha.databinding.FragmentMngStaffBinding
import com.example.alpha.user.*
import com.example.alpha.util.showToast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.alpha.R

class MngStaffFragment : Fragment() {
    private lateinit var binding: FragmentMngStaffBinding
    private val nav by lazy { findNavController() }
    private val model: UserViewModel by activityViewModels()

    //    private val vm: StaffViewModel by activityViewModels()
    private val db by lazy { Firebase.firestore.collection("Users").whereEqualTo("role", 1) }
    private val userDB = Firebase.firestore.collection("Users")
    private var tracker: SelectionTracker<Long>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentMngStaffBinding.inflate(inflater, container, false)

        binding.insertStaffBtn.setOnClickListener {
            nav.navigate(R.id.action_mngStaffFragment_to_mngAddStaffFragment)
        }

        val adapter = UserAdapter() { holder, user ->
            holder.root.setOnClickListener {
                nav.navigate(R.id.action_mngStaffFragment_to_editStaffFragment, bundleOf("id" to user.id) )
            }
            holder.activeUserBtn.setOnClickListener {
                userDB.document(user.id).update("status", "1")
                    .addOnSuccessListener {

                    }.addOnFailureListener {

                    }
            }
            holder.blockUserBtn.setOnClickListener {
                userDB.document(user.id).update("status", "2")
                    .addOnSuccessListener {

                    }.addOnFailureListener {

                    }
            }
            holder.deleteUserBtn.setOnClickListener {
                deleteSingleDialog(user)
            }
        }

        val rv = binding.staffRV

        rv.itemAnimator = null;

        val layoutManager = LinearLayoutManager(activity)
        rv.adapter = adapter
        rv.layoutManager = layoutManager
        rv.addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
        tracker = SelectionTracker.Builder<Long>(
            "mySelection",
            rv,
            MyItemKeyProvider(rv),
            MyItemDetailsLookup(rv),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        adapter.tracker = tracker


        //model.search("test")
        model.result.observe(viewLifecycleOwner) {
            Log.d("testuser", it.toString())
            if (it.isEmpty()) {
                tracker?.clearSelection()
            }
            adapter.submitList(it)
        }

        tracker?.addObserver(
            object : SelectionTracker.SelectionObserver<Long>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    //adapter.currentList
                    if (tracker?.selection?.size() != 0) {
                        binding.multiSelect.visibility = View.VISIBLE
                        binding.selectedTxt.text = "(${tracker?.selection?.size()})"
                    } else {
                        binding.multiSelect.visibility = View.GONE
                    }

                    binding.cancelSelect.setOnClickListener {
                        tracker?.clearSelection()
                    }

                    var list: List<LUser>? = null


                    if (adapter.currentList.size != 0) {
                        list = tracker?.selection?.map {
                            adapter.currentList[it.toInt()]
                        }?.toList()
                    }

                    binding.blockBtn.setOnClickListener {
                        var size = 0
                        Firebase.firestore.runBatch { batch ->
                            if (list != null) {
                                for (user in list!!) {
                                    if (user.status != 2) {
                                        size++
                                        batch.update(userDB.document(user.id), "status", 2)
                                    }
                                }
                            }
                        }.addOnSuccessListener {
                            activity?.let {
                                "Block ($size) selected User Successfully".showToast(it)
                            }
                            nav.navigateUp()
                        }.addOnFailureListener {
                            activity?.let {
                                "Failed to Block Users".showToast(it)
                            }
                        }
                    }

                    binding.activeBtn.setOnClickListener {
                        var size = 0
                        Firebase.firestore.runBatch { batch ->
                            if (list != null) {
                                for (user in list!!) {
                                    if (user.status == 2) {
                                        size++
                                        batch.update(userDB.document(user.id), "status", 1)
                                    }
                                }
                            }
                        }.addOnSuccessListener {
                            activity?.let {
                                "Active ($size) selected User Successfully".showToast(it)
                            }
                            nav.navigateUp()
                        }.addOnFailureListener {
                            activity?.let {
                                "Failed to Active Users".showToast(it)
                            }
                        }
                    }

                    binding.deleteBtn.setOnClickListener {
                        list?.let { deleteMultipleDialog(it) }
                    }
                }
            })

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?) = true
            override fun onQueryTextChange(p0: String): Boolean {
                model.search(p0)
                return true
            }
        })


        return binding.root
    }

    private fun deleteSingleDialog(user: LUser) {
        var dialog = AlertDialog.Builder(requireActivity())
            .setTitle("Delete User")
            .setMessage("Are you sure want to delete ${user.username}?")
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                userDB.document(user.id).delete()
                    .addOnSuccessListener {
                        activity?.let { it ->
                            "Delete User(${user.username}) Successfully".showToast(
                                it
                            )
                        }
                    }
                    .addOnFailureListener {
                        activity?.let { it -> "Delete User(${user.username}) Failed".showToast(it) }
                    }
                dialogInterface.dismiss()
            })
            .setNegativeButton("NO", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            })
            .create()
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun deleteMultipleDialog(list: List<LUser>) {
        var size = 0
        var items: Array<String> = list.map(LUser::username).toTypedArray()
        var userIds: Array<String> = list.map(LUser::id).toTypedArray()
        var checkedItems = BooleanArray(items.size) { true }

        var dialog = AlertDialog.Builder(requireActivity())
            .setTitle("Delete Users")
            .setMultiChoiceItems(
                items,
                checkedItems,
                DialogInterface.OnMultiChoiceClickListener { _, i, isChecked ->
                    checkedItems[i] = isChecked
                })
            .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                var deleteList = mutableListOf<String>()
                checkedItems.forEachIndexed { index, element ->
                    if (element as Boolean) {
                        deleteList.add(userIds[index])
                    }
                }
                Firebase.firestore.runBatch { batch ->
                    for (id in deleteList) {
                        size++
                        batch.delete(userDB.document(id))
                    }
                }.addOnSuccessListener {
                    activity?.let { it -> "Delete (${size}) Users Successfully".showToast(it) }
                }.addOnFailureListener {
                    activity?.let { it -> "Delete Users Failed".showToast(it) }
                }

                dialogInterface.dismiss()
            })
            .setNegativeButton("NO", DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.dismiss()
            })
            .create()
        dialog.setCancelable(false)
        dialog.show()
    }
}