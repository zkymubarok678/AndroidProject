package com.example.our_book

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.our_book.databinding.ActivityEditBookBinding
import java.io.ByteArrayOutputStream
import java.util.Calendar

class EditBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBookBinding
    private lateinit var db: DatabaseHelper
    private var bookId: Int = -1
    private lateinit var newPhoto: Bitmap
    private val GALLERY_REQUEST_CODE = 200
    private val CAMERA_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        bookId = intent.getIntExtra("book_id", -1)
        if (bookId == -1) {
            finish()
            return
        }

        binding.editTglLahir.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    binding.editTglLahir.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                },
                year, month, day
            )
            datePickerDialog.show()
        }

        val book = db.getBookByID(bookId)

        binding.editNama.setText(book.nama)
        binding.editNP.setText(book.namapang)
        binding.editEmail.setText(book.email)
        binding.editAlamat.setText(book.alamat)
        binding.editTglLahir.setText(book.tglLahir)
        binding.editHP.setText(book.telp)

        // Set photo or default image
        val bitmap = BitmapFactory.decodeByteArray(book.photo, 0, book.photo.size)
        binding.editPhoto.setImageBitmap(bitmap)

        binding.editPhoto.setOnClickListener {
            selectPhoto()
        }

        binding.editButton.setOnClickListener {
            val newnama = binding.editNama.text.toString()
            val newnamapang = binding.editNP.text.toString()
            val newemail = binding.editEmail.text.toString()
            val newalamat = binding.editAlamat.text.toString()
            val newtglLahir = binding.editTglLahir.text.toString()
            val newtelp = binding.editHP.text.toString()

            // Convert Bitmap to ByteArray
            val photoByteArray = bitmapToByteArray(newPhoto)

            val updateBook = OurBook(bookId, newnama, newnamapang, newemail, newalamat, newtglLahir, newtelp, photoByteArray)

            db.updateBook(updateBook)
            finish()
            Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    private fun selectPhoto() {
        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Photo")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> openCamera() // Camera option
                1 -> openGallery() // Gallery option
            }
        }
        builder.show()
    }

    private fun openCamera() {
        val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePicture, CAMERA_REQUEST_CODE)
    }

    private fun openGallery() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val selectedPhoto = data?.extras?.get("data") as Bitmap
                    binding.editPhoto.setImageBitmap(selectedPhoto)
                    newPhoto = selectedPhoto // Save the selected photo
                }
                GALLERY_REQUEST_CODE -> {
                    val selectedImageUri: Uri? = data?.data
                    selectedImageUri?.let {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                        binding.editPhoto.setImageBitmap(bitmap)
                        newPhoto = bitmap // Save the selected photo
                    }
                }
            }
        }
    }
}