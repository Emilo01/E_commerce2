package com.farukayata.e_commerce2.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserProfile(
    val userId: String? = null,//bu olmasa da olur gibi
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val gender: String? = null,
    val address: String? = null,
    val phoneNumber: String? = null,
    val profileImageUrl: String? = null
) : Parcelable
//parcelize amacı userprofile nesnesini fragmentlar arası taşımak