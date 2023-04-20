package com.madderate.flowcombinedemo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textview.MaterialTextView
import com.madderate.flowcombinedemo.data.main.MainItemModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel>()

    private val itemListView: RecyclerView by lazy(LazyThreadSafetyMode.PUBLICATION) {
        findViewById(R.id.items)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        itemListView.adapter = Adapter(viewModel::itemClicked)

        lifecycleScope.launch {
            viewModel.mainItemModelsFlow.collectLatest {
                (itemListView.adapter as? Adapter)?.submitData(it)
            }
        }
    }


    private class Adapter constructor(
        private val itemClick: (model: MainItemModel?) -> Unit,
    ) : PagingDataAdapter<MainItemModel, VH>(MainItemModel.DiffCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(parent, this::onItemClick)
        }

        private fun onItemClick(position: Int) {
            itemClick(getItem(position))
        }

        override fun onBindViewHolder(holder: VH, position: Int, payloads: MutableList<Any>) {
            Log.d(
                "MainActivity",
                "onBindViewHolder: position=$position, payloadCount=${payloads.size}"
            )
            if (payloads.isEmpty()) {
                super.onBindViewHolder(holder, position, payloads)
                return
            }

            for (payload in payloads) {
                if (payload !is Map<*, *>) {
                    continue
                }
                for ((k, v) in payload) {
                    if (k == MainItemModel.DiffCallback.EXTRA_IS_SELECTED && v is Boolean) {
                        holder.setSelected(v)
                    }
                }
            }
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind(getItem(position))
        }
    }

    private class VH(
        parent: ViewGroup,
        private val onItemClick: (position: Int) -> Unit,
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_main, parent, false)
    ) {
        private val titleView: MaterialTextView = itemView.findViewById(R.id.main_item_title)
        private val box: MaterialCheckBox = itemView.findViewById(R.id.main_item_selected_box)

        init {
            itemView.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }
            box.setOnClickListener {
                onItemClick(bindingAdapterPosition)
            }
        }

        fun bind(model: MainItemModel?) {
            titleView.text = model?.content
            box.isChecked = model?.isSelected ?: false
        }

        fun setSelected(isSelected: Boolean) {
            box.isChecked = isSelected
        }
    }
}