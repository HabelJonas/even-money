package com.evenmoney.repositories.interfaces

interface IGroupRepository {
    fun createGroup(groupName: String, callback: (Boolean, String?) -> Unit)
    fun joinGroup(invitationCode: String, callback: (Boolean, String?) -> Unit)
    fun readGroupMembers(groupId: String, callback: (Boolean, List<String>) -> Unit)
    fun readGroupsMap(callback: (Boolean, Map<String, String>) -> Unit)
    fun readGroupInvitationCode(groupId: String, callback: (Boolean, String) -> Unit)
}