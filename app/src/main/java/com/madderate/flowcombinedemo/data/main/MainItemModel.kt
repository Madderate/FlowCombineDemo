package com.madderate.flowcombinedemo.data.main

import androidx.recyclerview.widget.DiffUtil
import java.util.UUID

data class MainItemModel @JvmOverloads constructor(
    val content: String,
    val isSelected: Boolean = false,
    val id: String = UUID.randomUUID()?.toString() ?: "${System.currentTimeMillis()}",
) {
    class DiffCallback : DiffUtil.ItemCallback<MainItemModel>() {
        override fun areItemsTheSame(oldItem: MainItemModel, newItem: MainItemModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MainItemModel, newItem: MainItemModel): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: MainItemModel, newItem: MainItemModel): Any? {
            if (oldItem == newItem) {
                return null
            }

            val map = mutableMapOf<String, Any>()
            if (oldItem.content != newItem.content) {
                map[EXTRA_CONTENT] = newItem.content
            }
            if (oldItem.isSelected != newItem.isSelected) {
                map[EXTRA_IS_SELECTED] = newItem.isSelected
            }
            return map
        }


        companion object {
            const val EXTRA_CONTENT = "content"
            const val EXTRA_IS_SELECTED = "is_selected"
        }
    }
}