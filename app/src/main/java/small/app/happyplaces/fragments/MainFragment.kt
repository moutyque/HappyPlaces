package small.app.happyplaces.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.runBlocking
import small.app.happyplaces.R
import small.app.happyplaces.adapters.HappyPlaceAdapter
import small.app.happyplaces.db.Repository
import small.app.happyplaces.db.getInstance
import small.app.happyplaces.model.HappyPlaceModel
import small.app.happyplaces.model.ShareHappyPlaceModel
import small.app.happyplaces.utils.SwipeToDeleteCallback
import small.app.happyplaces.utils.SwipeToEditCallback

class MainFragment : Fragment() {

    private lateinit var repo: Repository
    private val viewModel: ShareHappyPlaceModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        repo = Repository(getInstance(requireContext()))


        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fabAddHappyPlace.setOnClickListener { _ ->
            viewModel.happyPlace.value = HappyPlaceModel()
            view.findNavController()
                .navigate(MainFragmentDirections.actionMainFragmentToAddHappyPlaceFragment())
        }

        initView()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initView() {
        runBlocking {
            repo.getHappyPlaces()
            setupHappyPlacesRecyclerView()
        }
    }


    private fun setupHappyPlacesRecyclerView() {
        rv_happy_places_list.layoutManager = LinearLayoutManager(requireContext())
        val placesAdapter = HappyPlaceAdapter(requireContext(), repo.happyPlaces)
        rv_happy_places_list.adapter = placesAdapter
        rv_happy_places_list.setHasFixedSize(true)
        placesAdapter.setOnClickListener(object : HappyPlaceAdapter.OnClickListener {
            override fun onClick(position: Int, mode: HappyPlaceModel) {
                viewModel.happyPlace.value = mode
                view!!.findNavController()
                    .navigate(MainFragmentDirections.actionMainFragmentToHappyPlaceDetailsFragment())
            }

        })

        val editSwipeHandler = object : SwipeToEditCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rv_happy_places_list.adapter as HappyPlaceAdapter
                adapter.notifyEditItem(requireView(), viewHolder.adapterPosition, viewModel)
            }

        }

        val deleteSwipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = rv_happy_places_list.adapter as HappyPlaceAdapter
                adapter.removeAt(viewHolder.adapterPosition)

                runBlocking {
                    repo.getHappyPlaces()
                }
            }

        }

        val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(rv_happy_places_list)

        val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(rv_happy_places_list)

    }

}

