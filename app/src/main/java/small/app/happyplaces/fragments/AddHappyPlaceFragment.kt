package small.app.happyplaces.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.gms.location.*
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.fragment_add_happy_place.*
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import small.app.happyplaces.databinding.FragmentAddHappyPlaceBinding
import small.app.happyplaces.model.HappyPlaceModel
import small.app.happyplaces.model.ShareHappyPlaceModel
import small.app.happyplaces.utils.GetAdressUtil
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList


lateinit var binding: FragmentAddHappyPlaceBinding

class AddHappyPlaceFragment : Fragment() {
    private val viewModel: ShareHappyPlaceModel by activityViewModels()
    lateinit var model: HappyPlaceModel

    private lateinit var mFuesedLoationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        model = viewModel.happyPlace.value!!


        binding = FragmentAddHappyPlaceBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.model = model
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        // Create a new DatePickerFragment
        val newFragment: DialogFragment = DatePickerFragment(model)

        mFuesedLoationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        // Display DatePickerFragment
        binding.etDate.setOnFocusChangeListener { _, b ->
            if (b) newFragment.show(childFragmentManager, "DatePicker")
        }
        binding.btnAddImage.setOnClickListener {
            val pictureDialog = AlertDialog.Builder(requireContext())
            pictureDialog.setTitle("Select Action")
            val pictureDialogItems =
                arrayOf("Select Photo from Gallery", "Capture photo from camera")
            pictureDialog.setItems(pictureDialogItems) { _, which ->
                when (which) {
                    0 -> chosePhotoFromGallery()
                    1 -> takePhoto()
                }
            }.show()

        }

        binding.tvCurrentLocation.setOnClickListener {
            if (!isLocationEnabled()) {

                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            } else {
                val list = listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )


                Dexter.withContext(requireContext()).withPermissions(list)
                    .withListener(object : MultiplePermissionsListener {
                        override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                            if (report!!.areAllPermissionsGranted()) {
                                requestNewLocationData()
                            } else {
                                showRationalDialogForPermissions()
                            }
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            p0: MutableList<PermissionRequest>?,
                            p1: PermissionToken?
                        ) {
                            showRationalDialogForPermissions()
                        }

                    }).check()


            }


        }



        binding.etLocation.setOnClickListener {
            val fields = listOf(
                Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS
            )
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(requireContext())
            startActivityForResult(intent, PLACE_AUTO_COMPLETE_CODE)
        }


        binding.etDescription.doOnTextChanged { _, _, _, _ ->
            model.descriptor.value = binding.etDescription.text.toString()
        }

        binding.etTitle.doOnTextChanged { _, _, _, _ ->
            model.title.value = binding.etTitle.text.toString()
        }

        binding.etLocation.doOnTextChanged { _, _, _, _ ->
            model.location.value = binding.etLocation.text.toString()
        }

        binding.buttonSave.setOnClickListener {
            model.save(requireActivity())
            view.findNavController()
                .navigate(AddHappyPlaceFragmentDirections.actionAddHappyPlaceFragmentToMainFragment())
        }

        model.uri.observe(viewLifecycleOwner, { item ->
            binding.imageView.setImageURI(item)
        })


        super.onViewCreated(view, savedInstanceState)
    }


    private fun setImage(uri: Uri?) {
        try {
            binding.model!!.uri.value = uri!!

        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.numUpdates = 1

        mFuesedLoationClient.requestLocationUpdates(
            mLocationRequest,
            mLocationCallBack,
            Looper.myLooper()
        )

    }

    private val mLocationCallBack = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {

            val mLastLocation: Location = locationResult!!.lastLocation
            model.longitude.value = mLastLocation.longitude
            model.latitude.value = mLastLocation.latitude
            model.location.value = "Current Location"
            MainScope().launch {
                GetAdressUtil().getAdress(
                    requireContext(),
                    mLastLocation.latitude,
                    mLastLocation.longitude,
                    model.location
                )

            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY -> {
                    if (data != null) {
                        val contentURI = data.data

                        // Here this is used to get an bitmap from URI
                        val selectedImageBitmap =
                            MediaStore.Images.Media.getBitmap(
                                requireContext().contentResolver,
                                contentURI
                            )

                        setImage(saveImageToInternalStorage(selectedImageBitmap))


                    }
                }
                CAMERA -> {
                    val thumbNail = data!!.extras!!.get("data") as Bitmap
                    setImage(saveImageToInternalStorage(thumbNail))
                }

                PLACE_AUTO_COMPLETE_CODE -> {
                    val place: Place = Autocomplete.getPlaceFromIntent(data!!)
                    et_location.setText(place.address)
                    model.latitude.value = place.latLng!!.latitude
                    model.longitude.value = place.latLng!!.longitude
                }


            }
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            data?.let {
                val status = Autocomplete.getStatusFromIntent(data)
                Log.e("Autocomplete", status.statusMessage.toString())
            }
        }
    }


    private fun runIntentWithPermission(
        permissions: List<String>,
        intent: Intent,
        expectedResult: Int
    ) {
        Dexter.withContext(requireContext())
            .withPermissions(
                permissions
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        startActivityForResult(intent, expectedResult)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    showRationalDialogForPermissions()
                }

            }).check()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            (requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager)
        return locationManager.isProviderEnabled(
            LocationManager.GPS_PROVIDER
        ) || locationManager.isProviderEnabled(
            (LocationManager.NETWORK_PROVIDER)

        )
    }

    private fun chosePhotoFromGallery() {

        val permissions: MutableList<String> = ArrayList()
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)

        runIntentWithPermission(
            permissions,
            Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ), GALLERY
        )
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(requireContext())
            .setMessage("Its looks like you have not granted the required permission. It can be changed under app settings.")
            .setPositiveButton("GO TO SETTINGS") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", requireActivity().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun takePhoto() {
        val permissions: MutableList<String> = ArrayList()
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        permissions.add(Manifest.permission.CAMERA)

        runIntentWithPermission(
            permissions,
            Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA
        )
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(requireContext())
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return file.toUri()
    }


    companion object {


        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
        private const val PLACE_AUTO_COMPLETE_CODE = 3
    }


}

class DatePickerFragment(private val model: HappyPlaceModel) : DialogFragment(),
    OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Set the current date in the DatePickerFragment
        val c = Calendar.getInstance()
        val year = c[Calendar.YEAR]
        val month = c[Calendar.MONTH]
        val day = c[Calendar.DAY_OF_MONTH]

        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    // Callback to DatePickerActivity.onDateSet() to update the UI
    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val c = Calendar.getInstance()
        c[Calendar.YEAR] = year
        c[Calendar.MONTH] = month
        c[Calendar.DAY_OF_MONTH] = dayOfMonth
        model.date = c.time
    }

}