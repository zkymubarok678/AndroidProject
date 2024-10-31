package com.example.our_book

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.our_book.databinding.ActivityDetailBinding

class detailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        val bookId = intent.getIntExtra("book_id", -1)

        if (bookId != -1) {
            val book = db.getBookByID(bookId)

            binding.namaDetail.text = book.nama
            binding.npDetail.text = book.namapang
            binding.emailDetail.text = book.email
            binding.alamatDetail.text = book.alamat
            binding.tglDetail.text = book.tglLahir
            binding.hpDetail.text = book.telp

            if (book.photo != null) {
                val bmp = BitmapFactory.decodeByteArray(book.photo, 0, book.photo.size)
                binding.detailPhoto.setImageBitmap(bmp)
            } else {
                binding.detailPhoto.setImageResource(R.drawable.baseline_person_24)
            }
        }

        binding.backDetail.setOnClickListener{
            finish()
        }
    }
}