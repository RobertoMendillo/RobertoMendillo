package it.unisannio.studenti.mendillo.teamwork.model

data class User(
    var id: String? = null,
    var email: String? = null,
    var name: String? = null,
    var ownedGroups: ArrayList<Group>? = null,
    var hostingGroups: ArrayList<Group>? = null
    ) {
}