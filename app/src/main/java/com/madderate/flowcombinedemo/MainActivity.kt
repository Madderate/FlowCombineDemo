package com.madderate.flowcombinedemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val exampleItems = listOf(
        "你好",
        "谢谢",
        "小笼包",
        "再见",
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
    )


    private val viewModel by viewModels<MainViewModel>()

    private val addItemBtn: MaterialButton by lazy(LazyThreadSafetyMode.PUBLICATION) {
        findViewById(R.id.add_item)
    }
    private val syncBtn: MaterialButton by lazy(LazyThreadSafetyMode.PUBLICATION) {
        findViewById(R.id.sync)
    }
    private val itemListView: RecyclerView by lazy(LazyThreadSafetyMode.PUBLICATION) {
        findViewById(R.id.items)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        itemListView.adapter = Adapter(this::onItemRemoved)
        addItemBtn.setOnClickListener {
            viewModel.add(exampleItems.random())
        }
        syncBtn.setOnClickListener {
            viewModel.sync()
        }


        lifecycleScope.launch {
            viewModel.list.collectLatest {
                (itemListView.adapter as? Adapter)?.submitList(it)
            }
        }
    }

    private fun onItemRemoved(value: String) {
        viewModel.remove(value)
    }

    private class Adapter(private val onItemRemoveClicked: (value: String) -> Unit) :
        ListAdapter<String, VH>(StringDiffCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(parent, this::onItemDeleted)
        }

        private fun onItemDeleted(position: Int) {
            val item = getItem(position) ?: return
            onItemRemoveClicked(item)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind(getItem(position))
        }
    }

    private class VH(
        parent: ViewGroup,
        onItemRemoveClicked: (position: Int) -> Unit,
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_main, parent, false)
    ) {
        private val titleView: MaterialTextView = itemView.findViewById(R.id.main_item_title)
        private val deleteView: ImageView = itemView.findViewById(R.id.main_item_delete)

        init {
            deleteView.setOnClickListener {
                onItemRemoveClicked(bindingAdapterPosition)
            }
        }

        fun bind(value: String?) {
            titleView.text = value
        }
    }

    private class StringDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.equals(newItem, false)
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: String, newItem: String): Any? {
            return super.getChangePayload(oldItem, newItem)
        }
    }
}