package small.app.happyplaces.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import small.app.happyplaces.databinding.FragmentHappyPlaceDetailsBinding
import small.app.happyplaces.model.HappyPlaceModel
import small.app.happyplaces.model.ShareHappyPlaceModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HappyPlaceDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HappyPlaceDetailsFragment() : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val viewModel: ShareHappyPlaceModel by activityViewModels()
    private lateinit var binding: FragmentHappyPlaceDetailsBinding
    private lateinit var model: HappyPlaceModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        Toast.makeText(
            requireContext(),
            viewModel.happyPlace.value!!.title.value,
            Toast.LENGTH_LONG
        ).show()
        binding = FragmentHappyPlaceDetailsBinding.inflate(inflater)
        model = viewModel.happyPlace.value!!
        binding.model = model
        binding.ivImage.setImageURI(model.uri.value)
        binding.button.setOnClickListener {
            it.findNavController().navigate(
                HappyPlaceDetailsFragmentDirections.actionHappyPlaceDetailsFragmentToMapsFragment(
                    model.latitude.value!!.toFloat(),
                    model.longitude.value!!.toFloat()
                )
            )
        }

        return binding.root
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        iv_image.setImageURI(model.uri.value)
//    }


}