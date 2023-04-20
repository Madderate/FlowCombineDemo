package com.madderate.flowcombinedemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
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

        itemListView.adapter = Adapter()

        lifecycleScope.launch {
            viewModel.mainItemModelsFlow.collectLatest {
                (itemListView.adapter as? Adapter)?.submitData(it)
            }
        }
    }


    private class Adapter : PagingDataAdapter<MainItemModel, VH>(MainItemModel.DiffCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(parent)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind(getItem(position))
        }
    }

    private class VH(
        parent: ViewGroup,
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_main, parent, false)
    ) {
        private val titleView: MaterialTextView = itemView.findViewById(R.id.main_item_title)

        fun bind(model: MainItemModel?) {
            titleView.text = model?.content
        }
    }
}