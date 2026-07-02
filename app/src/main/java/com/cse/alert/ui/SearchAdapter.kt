package com.cse.alert.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cse.alert.databinding.ItemSearchResultBinding
import com.cse.alert.model.SymbolSearchResult

class SearchAdapter(
    private val onItemClick: (SymbolSearchResult) -> Unit
) : ListAdapter<SymbolSearchResult, SearchAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(private val b: ItemSearchResultBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(item: SymbolSearchResult) {
            b.tvSymbol.text = item.symbol.substringBefore(".")
            b.tvName.text   = item.name
            b.root.setOnClickListener { onItemClick(item) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemSearchResultBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<SymbolSearchResult>() {
            override fun areItemsTheSame(a: SymbolSearchResult, b: SymbolSearchResult) =
                a.symbol == b.symbol
            override fun areContentsTheSame(a: SymbolSearchResult, b: SymbolSearchResult) =
                a == b
        }
    }
}
