package small.app.happyplaces.model

import android.app.Activity
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import small.app.happyplaces.db.HappyPlace
import small.app.happyplaces.db.Repository
import small.app.happyplaces.db.getInstance
import java.text.DateFormat
import java.util.*

class HappyPlaceModel() : ViewModel() {

    //val activity: Activity, private val state: SavedStateHandle
    private val scope = CoroutineScope(Job() + Dispatchers.IO)


    val title: MutableLiveData<String> = MutableLiveData()
    val dateText: MutableLiveData<String> = MutableLiveData()

    var descriptor: MutableLiveData<String> = MutableLiveData()
    var date: Date = Calendar.getInstance().time
        set(value) {
            dateText.value = DateFormat.getDateInstance(DateFormat.FULL).format(value)
        }
    var location: MutableLiveData<String> = MutableLiveData()
    var latitude: MutableLiveData<Double> = MutableLiveData()
    var longitude: MutableLiveData<Double> = MutableLiveData()

    var uri: MutableLiveData<Uri> = MutableLiveData()

    var id: Long = 0

    init {

        title.value = ""
        dateText.value = ""
        descriptor.value = ""
        location.value = ""
        latitude.value = 0.0
        longitude.value = 0.0
        uri.value = Uri.EMPTY
    }

    constructor(entity: HappyPlace) : this() {
        id = entity.id
        title.value = entity.title
        date = entity.date
        location.value = entity.locale
        latitude.value = entity.latitude
        longitude.value = entity.longitude
        descriptor.value = entity.description
        uri.value = entity.image

    }

    fun save(activity: Activity) {
        val model = this
        scope.launch {
            Repository(getInstance(activity.applicationContext)).save(model)
        }
    }
}