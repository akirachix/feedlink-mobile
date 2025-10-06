

package com.feedlink.feedlink.repository

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.feedlink.feedlink.model.ListingItem

object CartRepository {
    val cartItems: SnapshotStateList<ListingItem> = mutableStateListOf()
}