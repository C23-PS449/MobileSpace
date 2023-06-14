package com.example.capstone.ui.fragment.forums

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Message (
    val text:String?=null,
    val name:String?=null,
    val photoUrl:String?=null,
    val timestamp: Long?=null
){

}