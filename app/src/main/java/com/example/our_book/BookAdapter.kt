package com.example.our_book

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random

class BookAdapter(private var books: List<OurBook>, private val context: Context) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    private val db: DatabaseHelper = DatabaseHelper(context)

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namatext: TextView = itemView.findViewById(R.id.txtNama)
        val namapangtext: TextView = itemView.findViewById(R.id.txtNamaPanggilan)
        val emailtext: TextView = itemView.findViewById(R.id.txtEmail)
        val alamattext: TextView = itemView.findViewById(R.id.txtAlamat)
        val tglLahirtext: TextView = itemView.findViewById(R.id.txtTglLahir)
        val telptext: TextView = itemView.findViewById(R.id.txtHP)
        val photo: ImageView = itemView.findViewById(R.id.Photo)
        val editButton: ImageView = itemView.findViewById(R.id.btnEdit)
        val deleteButton: ImageView = itemView.findViewById(R.id.btnDelete)
        val cardView: CardView = itemView.findViewById(R.id.cardView) // CardView from activity_book_item.xml
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_our_book_item, parent, false)
        return BookViewHolder(view)
    }

    override fun getItemCount(): Int = books.size

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]

        holder.namatext.text = book.nama
        holder.namapangtext.text = book.namapang
        holder.emailtext.text = book.email
        holder.alamattext.text = book.alamat
        holder.tglLahirtext.text = book.tglLahir
        holder.telptext.text = book.telp

        // Set the photo to imageView3
        book.photo?.let { photoByteArray ->
            val bitmap = BitmapFactory.decodeByteArray(photoByteArray, 0, photoByteArray.size)
            holder.photo.setImageBitmap(bitmap)
        } ?: run {
            // Set a placeholder image if no photo exists
            holder.photo.setImageResource(R.drawable.baseline_person_24)
        }

        // List of predefined colors
        val colors = listOf(
            ContextCompat.getColor(context, R.color.green),
            ContextCompat.getColor(context, R.color.white),
            ContextCompat.getColor(context, R.color.black),
            ContextCompat.getColor(context, R.color.yellow)
        )

        // Set random color for CardView background
        val randomColor = colors[Random.nextInt(colors.size)]
        holder.cardView.setCardBackgroundColor(randomColor)

        // Edit button click listener
        holder.editButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, EditBookActivity::class.java).apply {
                putExtra("book_id", book.id)
            }
            Log.d("BookAdapter", "Starting updateBook Activity with book_id: ${book.id}")
            holder.itemView.context.startActivity(intent)
        }

        // Delete button click listener
        holder.deleteButton.setOnClickListener { view ->
            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        db.deleteBook(book.id)
                        refreshData(db.getAllNotes())
                        Toast.makeText(
                            holder.itemView.context,
                            "Book Deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    DialogInterface.BUTTON_NEGATIVE -> dialog.dismiss() // Do nothing if "No" is pressed
                }
            }
            val builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
            builder.setMessage("Apakah anda yakin akan menghapusnya?")
                .setPositiveButton("Ya", dialogClickListener)
                .setNegativeButton("Tidak", dialogClickListener)
                .show()
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, detailActivity::class.java).apply {
                putExtra("book_id", book.id)
            }
            holder.itemView.context.startActivity(intent)
        }

    }

    // This method is used to refresh the data in the adapter
    fun refreshData(newBooks: List<OurBook>) {
        books = newBooks
        notifyDataSetChanged()
    }
}