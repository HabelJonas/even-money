package com.evenmoney.repositories.mocks

import com.evenmoney.repositories.interfaces.IGroupRepository

class FakeGroupRepository : IGroupRepository {
    override fun createGroup(groupName: String, callback: (Boolean, String?) -> Unit) {
        if(groupName.isNotEmpty()){
           callback(true, null)
        }
        else{
            callback(false, "Group name cannot be empty")
        }
    }

    override fun joinGroup(invitationCode: String, callback: (Boolean, String?) -> Unit) {
        callback(true, null)
    }

    override fun readGroupMembers(groupId: String, callback: (Boolean, List<String>) -> Unit) {
        callback(true, emptyList<String>())
    }

    override fun readGroupsMap(callback: (Boolean, Map<String, String>) -> Unit) {
        callback(true, emptyMap<String,String>())
    }

    override fun readGroupInvitationCode(
        groupId: String,
        callback: (Boolean, String) -> Unit
    ) {
        callback(true,"")
    }
}