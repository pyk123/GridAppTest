package com.example.gridapptest

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gridapptest.databinding.ItemGirdBinding

class GridAdapter(
    private val data: MutableList<MutableList<String>>,
    private val onItemClicked: (Int, Int) -> Unit
) : RecyclerView.Adapter<GridAdapter.GridViewHolder>() {

    inner class GridViewHolder(private val binding: ItemGirdBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(row: Int, col: Int, text: String) {

            binding.gridItemText.text = text
            if (highlightedPositions.contains(Pair(row, col))) {

                binding.gridItemText.setBackgroundColor(Color.YELLOW)
            } else {

                binding.gridItemText.setBackgroundColor(Color.WHITE)
            }

        }
    }
    private var highlightedPositions: List<Pair<Int, Int>> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GridViewHolder {
        val binding = ItemGirdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GridViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GridViewHolder, position: Int) {
        val row = position / data[0].size
        val col = position % data[0].size
        holder.bind(row, col, data[row][col])
    }

    override fun getItemCount(): Int {
        return data.size * data[0].size
    }
    fun highlightWord(word: String, positions: List<Pair<Int, Int>>) {
        highlightedPositions = positions
        notifyDataSetChanged()
    }
}
