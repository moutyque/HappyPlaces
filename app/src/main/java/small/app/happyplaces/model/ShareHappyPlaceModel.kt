package small.app.happyplaces.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShareHappyPlaceModel : ViewModel() {
    val happyPlace: MutableLiveData<HappyPlaceModel> = MutableLiveData()
}