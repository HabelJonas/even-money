package com.evenmoney.repositories

import android.util.Log
import com.evenmoney.repositories.interfaces.IGroupRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class GroupRepository : IGroupRepository {
    private val db = FirebaseFirestore.getInstance()

    override fun createGroup(groupName: String, callback: (Boolean, String?) -> Unit) {
        if (groupName.isNotEmpty()) {
            val admin = FirebaseAuth.getInstance().currentUser?.uid
            val invitationCode = generateInvitationCode()

            val groupData = hashMapOf(
                "groupName" to groupName, "admin" to admin, "invitationCode" to invitationCode
            )

            db.collection("groups").add(groupData).addOnSuccessListener { documentReference ->
                val groupId = documentReference.id
                updateUserGroups(admin, groupId) { success, error ->
                    if (success) {
                        callback(true, null)
                    } else {
                        callback(false, error)
                    }
                }
            }.addOnFailureListener { e ->
                callback(false, e.message)
            }
        } else {
            callback(false, "Group name cannot be empty")
        }
    }

    private fun updateUserGroups(
        userId: String?, groupId: String, callback: (Boolean, String?) -> Unit
    ) {
        if (userId == null) {
            callback(false, "User ID is null")
            return
        }

        val userRef = db.collection("users").document(userId)
        userRef.update("groups", FieldValue.arrayUnion(groupId)).addOnSuccessListener {
            callback(true, null)
        }.addOnFailureListener { e ->
            callback(false, e.message)
        }
    }

    private fun generateInvitationCode(): String {
        return UUID.randomUUID().toString().substring(0, 6).uppercase()
    }

    private fun getGroupId(invitationCode: String, onResult: (String?) -> Unit) {
        db.collection("groups").whereEqualTo("invitationCode", invitationCode).limit(1).get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val groupId = querySnapshot.documents[0].id
                    onResult(groupId)
                } else {
                    onResult(null)
                }
            }
    }

    override fun joinGroup(invitationCode: String, callback: (Boolean, String?) -> Unit) {
        if (invitationCode.isNotEmpty()) {
            val admin = FirebaseAuth.getInstance().currentUser?.uid
            getGroupId(invitationCode) { groupId ->
                if (groupId != null) {
                    updateUserGroups(admin, groupId.toString()) { success, error ->
                        if (success) {
                            callback(true, null)
                        } else {
                            callback(false, error)
                        }
                    }
                }
            }
        }
    }

    override fun readGroupMembers(
        groupId: String,
        callback: (Boolean, List<String>) -> Unit
    ) {
        db.collection("users")
            .whereArrayContains("groups", groupId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    var members = mutableListOf<String>()
                    for (element in querySnapshot) {
                        members.add(element.data.getValue("firstname").toString())
                    }
                    callback(true, members)
                } else {
                    callback(false, listOf())
                }
            }
    }

    override fun readGroupsMap(callback: (Boolean, Map<String, String>) -> Unit) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Log.e("Firestore", "User ID is null")
            callback(false, emptyMap())
            return
        }

        Log.d("Firestore", "Attempting to fetch groups for user ID: $userId")

        db.collection("users").document(userId).get()
            .addOnSuccessListener { docSnapshot ->
                Log.d("Firestore", "User document snapshot: ${docSnapshot.exists()}")
                val groupIds = docSnapshot.get("groups") as? List<String> ?: emptyList()
                Log.d("Firestore", "Found ${groupIds.size} group IDs: $groupIds")

                if (groupIds.isEmpty()) {
                    callback(true, emptyMap())
                    return@addOnSuccessListener
                }

                val groupsMap = mutableMapOf<String, String>()
                var pendingRequests = groupIds.size

                for (groupId in groupIds) {
                    db.collection("groups").document(groupId).get()
                        .addOnSuccessListener { groupSnapshot ->
                            val groupName = groupSnapshot.getString("groupName") ?: "Unknown Group"
                            groupsMap[groupId] = groupName
                            Log.d("Firestore", "Added group: $groupId -> $groupName")

                            pendingRequests--
                            if (pendingRequests == 0) {
                                Log.d("Firestore", "All groups fetched successfully: $groupsMap")
                                callback(true, groupsMap)
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error fetching group $groupId: ${e.message}")
                            pendingRequests--
                            if (pendingRequests == 0) {
                                callback(true, groupsMap)
                            }
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error fetching user document: ${e.message}")
                callback(false, emptyMap())
            }
    }

    override fun readGroupInvitationCode(
        groupId: String,
        callback: (Boolean, String) -> Unit
    ) {
        db.collection("groups").document(groupId).get()
            .addOnSuccessListener { docSnapshot ->
                if(docSnapshot != null) {
                    val invitationCode = docSnapshot.getString("invitationCode") ?: ""
                    callback(true, invitationCode)
                }
            }
            .addOnFailureListener { e ->
                callback(false, e.message.toString())
            }
    }
}