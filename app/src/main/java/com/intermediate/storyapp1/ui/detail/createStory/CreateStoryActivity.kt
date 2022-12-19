package com.intermediate.storyapp1.ui.detail.createStory

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.intermediate.storyapp1.R
import com.intermediate.storyapp1.data.constant.Constant
import com.intermediate.storyapp1.data.utils.createCustomTempFile
import com.intermediate.storyapp1.data.utils.uriToFile
import com.intermediate.storyapp1.databinding.ActivityCreateStoryBinding
import com.intermediate.storyapp1.ui.customView.CustomAlertDialog
import java.io.File

class CreateStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateStoryBinding
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private val createStoryViewModel: CreateStoryViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        checkPermission()
        buttonGalleryHandler()
        buttonCameraHandler()
        buttonSubmitStoryHandler()

        createStoryViewModel.isLoading.observe(this@CreateStoryActivity) {
            showLoading(it)
        }

        createStoryViewModel.isError.observe(this@CreateStoryActivity) {
            errorHandler(it)
        }
    }

    private fun setupToolbar() {
        title = resources.getString(R.string.create_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        finish()
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constant.REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGranted()) {
                Toast.makeText(this@CreateStoryActivity, R.string.permission_denied, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionGranted() = Constant.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermission() {
        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this@CreateStoryActivity,
                Constant.REQUIRED_PERMISSIONS,
                Constant.REQUEST_CODE_PERMISSIONS
            )
        }
    }

    private fun buttonGalleryHandler() {
        binding.createStoryLayout.galleryButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            val chooser = Intent.createChooser(intent, "Pilih Foto")
            launcherIntentGallery.launch(chooser)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(myFile.path)

            binding.createStoryLayout.imagePickerView.setImageBitmap(result)
        }
    }

    private fun buttonCameraHandler() {
        binding.createStoryLayout.cameraButton.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.resolveActivity(packageManager)

            createCustomTempFile(applicationContext).also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this@CreateStoryActivity,
                    "com.intermediate.storyapp1.mycamera",
                    it
                )
                currentPhotoPath = it.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                launcherIntentCamera.launch(intent)
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@CreateStoryActivity)
            getFile = myFile
            binding.createStoryLayout.imagePickerView.setImageURI(selectedImg)
        }
    }

    private fun buttonSubmitStoryHandler() {
        binding.createStoryLayout.submitStoryButton.setOnClickListener {
            val description = binding.createStoryLayout.descriptionEditText.text.toString()
            if (!TextUtils.isEmpty(description) && getFile != null) {
                createStoryViewModel.postCreateStory(getFile!!, description)
            } else {
                CustomAlertDialog(this, R.string.error_validation, R.drawable.error_form).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingLayout.root.visibility = View.VISIBLE
            binding.createStoryLayout.root.visibility = View.GONE
        } else {
            binding.loadingLayout.root.visibility = View.GONE
            binding.createStoryLayout.root.visibility = View.VISIBLE
        }
    }

    private fun errorHandler(isError: Boolean) {
        if (isError) {
            CustomAlertDialog(this, R.string.error_message, R.drawable.tryagain).show()
        } else {
            CustomAlertDialog(this, R.string.success_create_story, Toast.LENGTH_SHORT).show()
            binding.createStoryLayout.imagePickerView.setImageResource(R.drawable.choose_photo)
            binding.createStoryLayout.descriptionEditText.text?.clear()
            finish()
        }
    }
}