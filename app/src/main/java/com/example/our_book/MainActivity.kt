package com.example.our_book

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.our_book.AddBookActivity
import com.example.our_book.MainActivity
import com.example.our_book.BookAdapter
import com.example.our_book.DatabaseHelper
import com.example.our_book.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var db: DatabaseHelper
    private lateinit var bookAdapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the database and adapter
        db = DatabaseHelper(this)
        bookAdapter = BookAdapter(db.getAllNotes(), this)

        // Setup RecyclerView
        binding.booksRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.booksRecyclerView.adapter = bookAdapter

        // Setup the add button to navigate to AddNoteActivity
        binding.addButton.setOnClickListener {
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
        }
        binding.aboutUsButton.setOnClickListener {
            val intent = Intent(this, AboutBookActivity::class.java)
            startActivity(intent)
        }

    }
    override fun onResume() {
        super.onResume()
        // Refresh the data in the adapter when the activity resumes
        bookAdapter.refreshData(db.getAllNotes())
    }
}