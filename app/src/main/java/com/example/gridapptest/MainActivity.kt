package com.example.gridapptest

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.gridapptest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var gridAdapter: GridAdapter? = null
    private var gridData: MutableList<MutableList<String>> = mutableListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.gridRecyclerView.layoutManager = GridLayoutManager(this, 5)



        binding.buttonGenerateGrid.setOnClickListener {
            try {

                val rows = binding.edtRow.text.toString().toIntOrNull()
                val cols = binding.edtcolumn.text.toString().toIntOrNull()

                if (rows != null && cols != null) {

                    gridData = MutableList(rows) { MutableList(cols) { "" } }


                    binding.gridRecyclerView.layoutManager = GridLayoutManager(this, cols)

                    gridAdapter = GridAdapter(gridData) { row, col ->
                        val text = binding.editTextGridInput.text.toString()
                        gridData[row][col] = text
                        gridAdapter?.notifyDataSetChanged()
                    }

                    binding.gridRecyclerView.adapter = gridAdapter
                } else {
                    Toast.makeText(this, "Please enter valid rows and columns", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {

                e.printStackTrace()
                Toast.makeText(this, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }


        binding.buttonSetTextInGrid.setOnClickListener {
            try {

                if (gridAdapter != null) {
                    val text = binding.editTextGridInput.text.toString()

                    if (text.isNotEmpty()) {
                        var charIndex = 0


                        for (row in gridData.indices) {
                            for (col in gridData[row].indices) {

                                if (charIndex < text.length) {

                                    gridData[row][col] = text[charIndex].toString()
                                    charIndex++
                                } else {

                                    gridData[row][col] = ""
                                }
                            }
                        }


                        gridAdapter?.notifyDataSetChanged()
                    } else {

                        Toast.makeText(this, "Please enter text to fill the grid", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Grid has not been initialized", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonSearch.setOnClickListener {
            try {
                val word = binding.editTextSearch.text.toString()
                val positions = searchWord(word)

                if (positions.isNotEmpty()) {
                    val highlightedPositions = mutableListOf<Pair<Int, Int>>()
                    for ((row, col) in positions) {

                        highlightedPositions.add(Pair(row, col))
                    }

                    gridAdapter?.highlightWord(word, highlightedPositions)
                    Toast.makeText(this, "Word found and highlighted!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Word not found", Toast.LENGTH_SHORT).show()
                }
            }catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
            }

        }



    }

    private fun searchWord(word: String): List<Pair<Int, Int>> {
        val positions = mutableListOf<Pair<Int, Int>>()

        // Check for horizontal (left to right)
        for (row in gridData.indices) {
            for (col in 0 until gridData[row].size) {
                if (canMatchHorizontal(row, col, word)) {
                    for (i in word.indices) {
                        positions.add(Pair(row, col + i))
                    }
                    return positions
                }
            }
        }

        // Check for horizontal (right to left)
        for (row in gridData.indices) {
            for (col in gridData[row].size - 1 downTo 0) {
                if (canMatchHorizontalReverse(row, col, word)) {
                    for (i in word.indices) {
                        positions.add(Pair(row, col - i))
                    }
                    return positions
                }
            }
        }

        // Check for vertical (top to bottom)
        for (col in gridData[0].indices) {
            for (row in 0 until gridData.size) {
                if (canMatchVertical(row, col, word)) {
                    for (i in word.indices) {
                        positions.add(Pair(row + i, col))
                    }
                    return positions
                }
            }
        }

        // Check for vertical (bottom to top)
        for (col in gridData[0].indices) {
            for (row in gridData.size - 1 downTo 0) {
                if (canMatchVerticalReverse(row, col, word)) {
                    for (i in word.indices) {
                        positions.add(Pair(row - i, col))
                    }
                    return positions
                }
            }
        }

        // Check for diagonal (South-East)
        for (row in gridData.indices) {
            for (col in 0 until gridData[row].size) {
                if (canMatchDiagonal(row, col, word)) {
                    for (i in word.indices) {
                        positions.add(Pair(row + i, col + i))
                    }
                    return positions
                }
            }
        }

        // Check for diagonal (North-East)
        for (row in gridData.indices) {
            for (col in gridData[row].size - 1 downTo 0) {
                if (canMatchDiagonalReverse(row, col, word)) {
                    for (i in word.indices) {
                        positions.add(Pair(row - i, col + i))
                    }
                    return positions
                }
            }
        }

        // Check for diagonal (South-West)
        for (row in gridData.indices) {
            for (col in gridData[row].size - 1 downTo 0) {
                if (canMatchDiagonalSouthWest(row, col, word)) {
                    for (i in word.indices) {
                        positions.add(Pair(row + i, col - i))
                    }
                    return positions
                }
            }
        }

        // Check for diagonal (North-West)
        for (row in gridData.indices) {
            for (col in 0 until gridData[row].size) {
                if (canMatchDiagonalNorthWest(row, col, word)) {
                    for (i in word.indices) {
                        positions.add(Pair(row - i, col - i))
                    }
                    return positions
                }
            }
        }

        return positions
    }


    private fun canMatchHorizontal(row: Int, col: Int, word: String): Boolean {
        if (col + word.length > gridData[row].size) return false
        for (i in word.indices) {
            if (gridData[row][col + i] != word[i].toString()) return false
        }
        return true
    }

    private fun canMatchVertical(row: Int, col: Int, word: String): Boolean {
        if (row + word.length > gridData.size) return false
        for (i in word.indices) {
            if (gridData[row + i][col] != word[i].toString()) return false
        }
        return true
    }

    private fun canMatchDiagonal(row: Int, col: Int, word: String): Boolean {
        if (row + word.length > gridData.size || col + word.length > gridData[row].size) return false
        for (i in word.indices) {
            if (gridData[row + i][col + i] != word[i].toString()) return false
        }
        return true
    }

    private fun canMatchHorizontalReverse(row: Int, col: Int, word: String): Boolean {
        if (col - word.length + 1 < 0) return false
        for (i in word.indices) {
            if (gridData[row][col - i] != word[i].toString()) return false
        }
        return true
    }

    private fun canMatchVerticalReverse(row: Int, col: Int, word: String): Boolean {
        if (row - word.length + 1 < 0) return false
        for (i in word.indices) {
            if (gridData[row - i][col] != word[i].toString()) return false
        }
        return true
    }

    private fun canMatchDiagonalSouthWest(row: Int, col: Int, word: String): Boolean {
        if (row + word.length > gridData.size || col - word.length + 1 < 0) return false
        for (i in word.indices) {
            if (gridData[row + i][col - i] != word[i].toString()) return false
        }
        return true
    }

    private fun canMatchDiagonalNorthWest(row: Int, col: Int, word: String): Boolean {
        if (row - word.length + 1 < 0 || col - word.length + 1 < 0) return false
        for (i in word.indices) {
            if (gridData[row - i][col - i] != word[i].toString()) return false
        }
        return true
    }

    private fun canMatchDiagonalReverse(row: Int, col: Int, word: String): Boolean {
        if (row - word.length + 1 < 0 || col + word.length > gridData[row].size) return false
        for (i in word.indices) {
            if (gridData[row - i][col + i] != word[i].toString()) return false
        }
        return true
    }
}