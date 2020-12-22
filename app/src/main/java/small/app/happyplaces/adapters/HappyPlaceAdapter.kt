package small.app.happyplaces.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_happy_place.view.*
import small.app.happyplaces.R
import small.app.happyplaces.db.Repository
import small.app.happyplaces.db.getInstance
import small.app.happyplaces.fragments.MainFragmentDirections
import small.app.happyplaces.model.HappyPlaceModel
import small.app.happyplaces.model.ShareHappyPlaceModel

class HappyPlaceAdapter(
    private val context: Context,
    private var list: MutableLiveData<List<HappyPlaceModel>>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return HappyPlaceViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_happy_place, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list.value!![position]

        if (holder is HappyPlaceViewHolder) {
            holder.itemView.iv_place_image.setImageURI(model.uri.value)
            holder.itemView.tvTitle.text = model.title.value
            holder.itemView.tvDescription.text = model.descriptor.value

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.value?.size ?: 0
    }


    private class HappyPlaceViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, mode: HappyPlaceModel)
    }

    fun notifyEditItem(view: View, position: Int, shareHappyPlaceModel: ShareHappyPlaceModel) {
        shareHappyPlaceModel.happyPlace.value = list.value!![position]
        view.findNavController()
            .navigate(MainFragmentDirections.actionMainFragmentToAddHappyPlaceFragment())

    }

    fun removeAt(position: Int) {

        Repository(getInstance(context)).delete(list.value!![position])
        notifyItemRemoved(position)

    }


}