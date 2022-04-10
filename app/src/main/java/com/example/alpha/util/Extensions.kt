package com.example.alpha.util

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.alpha.R
import com.example.alpha.data.CategoryViewModel
import com.example.alpha.data.VoucherViewModel
import com.google.firebase.firestore.Blob
import java.io.ByteArrayOutputStream

// TODO: Convert to extension method
fun Fragment.errorDialog(text: String) {
    AlertDialog.Builder(context)
        .setIcon(R.drawable.ic_error)
        .setTitle("Error")
        .setMessage(text)
        .setPositiveButton("Dismiss", null)
        .show()
}

fun Fragment.successDialog(text: String) {
    AlertDialog.Builder(context)
        .setIcon(R.drawable.ic_done_24)
        .setTitle("Successful")
        .setMessage(text)
        .setPositiveButton("Close", null)
        .show()
}

fun Fragment.deleteAllCategoryDialog(deleteId: String){
    val vm: CategoryViewModel by activityViewModels()
    var builder = AlertDialog.Builder(context)
    builder.setTitle("Delete confirmation")
    builder.setMessage("Are you sure to delete?")
    builder.setIcon(R.drawable.ic_delete)
    builder.setPositiveButton("Yes", DialogInterface.OnClickListener{ dialog, id->
        vm.delete(deleteId)
        dialog.cancel()
    })
    builder.setNegativeButton("No", DialogInterface.OnClickListener{ dialog, id->
        dialog.cancel()
    })

    var alert = builder.create()
    alert.show()
}

fun Fragment.deleteAllVoucherDialog(deleteId: String){
    val vm: VoucherViewModel by activityViewModels()
    var builder = AlertDialog.Builder(context)
    builder.setTitle("Delete confirmation")
    builder.setMessage("Are you sure to delete?")
    builder.setIcon(R.drawable.ic_delete)
    builder.setPositiveButton("Yes", DialogInterface.OnClickListener{ dialog, id->
        vm.delete(deleteId)
        dialog.cancel()
    })
    builder.setNegativeButton("No", DialogInterface.OnClickListener{ dialog, id->
        dialog.cancel()
    })

    var alert = builder.create()
    alert.show()
}

// Usage: Crop and resize bitmap (upscale)
fun Bitmap.crop(width: Int, height: Int): Bitmap {
    // Source width, height and ratio
    val sw = this.width
    val sh = this.height
    val sratio = 1.0 * sw / sh

    // Target offset (x, y), width, height and ratio
    val x: Int
    val y: Int
    val w: Int
    val h: Int
    val ratio = 1.0 * width / height

    if (ratio >= sratio) {
        // Retain width, calculate height
        w = sw
        h = (sw / ratio).toInt()
        x = 0
        y = (sh - h) / 2
    }
    else {
        // Retain height, calculate width
        w = (sh * ratio).toInt()
        h = sh
        x = (sw - w) / 2
        y = 0
    }

    return Bitmap
        .createBitmap(this, x, y, w, h) // Crop
        .scale(width, height) // Resize
}


// Usage: Convert from Bitmap to Blob
@Suppress("DEPRECATION")
fun Bitmap.toBlob(): Blob {
    ByteArrayOutputStream().use {
        this.compress(Bitmap.CompressFormat.WEBP, 80, it)
        return Blob.fromBytes(it.toByteArray())
    }
}

// Usage: Convert from Blob to Bitmap
fun Blob.toBitmap(): Bitmap? {
    val bytes = this.toBytes()
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

// Usage: Crop to Blob
fun ImageView.cropToBlob(width: Int, height: Int): Blob {
    if (this.drawable == null)
        return Blob.fromBytes(ByteArray(0))
    else
        return this.drawable.toBitmap().crop(width, height).toBlob()
}