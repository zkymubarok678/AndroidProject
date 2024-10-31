package com.example.our_book

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.our_book.databinding.ActivityAddBookBinding
import java.io.ByteArrayOutputStream
import java.util.Calendar

class AddBookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBookBinding
    private lateinit var db: DatabaseHelper
    private lateinit var photo: Bitmap
    private val GALLERY_REQUEST_CODE = 200
    private val CAMERA_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = DatabaseHelper(this)

        // Save button listener
        binding.doneButton.setOnClickListener {
            showConfirmationDialog()
        }

        // Button to select photo
        binding.addPhoto.setOnClickListener {
            selectPhoto()
        }

        // Date picker for birth date
        binding.addTglLahir.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    binding.addTglLahir.setText("$selectedDay/${selectedMonth + 1}/$selectedYear")
                },
                year, month, day
            )
            datePickerDialog.show()
        }
    }

    private fun showConfirmationDialog() {
        val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    saveData() // Save data if user confirms
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    dialog.dismiss() // Dismiss dialog if user cancels
                }
            }
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Apakah anda yakin ingin menyimpan buku ini?")
            .setPositiveButton("Ya", dialogClickListener)
            .setNegativeButton("Tidak", dialogClickListener)
            .show()
    }

    private fun saveData() {
        val nama = binding.addNama.text.toString()
        val namapang = binding.addNP.text.toString()
        val email = binding.addEmail.text.toString()
        val alamat = binding.addAlamat.text.toString()
        val telp = binding.addHP.text.toString()
        val tglLahir = binding.addTglLahir.text.toString()

        val photoByteArray = bitmapToByteArray(photo)

        val book = OurBook(0, nama, namapang, email, alamat, tglLahir, telp, photoByteArray)
        val isInserted = db.insertNote(book)

        if (isInserted) {
            Toast.makeText(this, "Book Saved", Toast.LENGTH_SHORT).show()
            finish() // Finish the activity after saving
        } else {
            Toast.makeText(this, "Error saving book", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to convert Bitmap to byte array
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
                    binding.addPhoto.setImageBitmap(selectedPhoto)
                    photo = selectedPhoto // Save the selected photo
                }
                GALLERY_REQUEST_CODE -> {
                    val selectedImageUri: Uri? = data?.data
                    selectedImageUri?.let {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, it)
                        binding.addPhoto.setImageBitmap(bitmap)
                        photo = bitmap // Save the selected photo
                    }
                }
            }
        }
    }
}