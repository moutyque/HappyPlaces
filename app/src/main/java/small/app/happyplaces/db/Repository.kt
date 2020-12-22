package small.app.happyplaces.db

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import small.app.happyplaces.model.HappyPlaceModel

class Repository(db_in: AppDatabase) {
    val db = db_in
    val happyPlaces: MutableLiveData<List<HappyPlaceModel>> = MutableLiveData()
    var happyPlacesEntity: List<HappyPlace> = ArrayList()
    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    suspend fun save(model: HappyPlaceModel) {
        withContext(Dispatchers.IO) {
            db.happyPlaceDAO().save(entity(model))
        }
    }


    suspend fun getHappyPlaces() {
        withContext(Dispatchers.IO) {
            happyPlacesEntity = db.happyPlaceDAO().findAll()
        }
        happyPlaces.value = happyPlacesEntity.asViewModel()
    }

    fun delete(model: HappyPlaceModel) {
        scope.launch {
            db.happyPlaceDAO().delete(entity(model))
        }
        

    }

    private fun entity(model: HappyPlaceModel) = HappyPlace(
        id = model.id,
        title = model.title.value!!,
        date = model.date,
        description = model.descriptor.value!!,
        image = model.uri.value!!,
        latitude = model.latitude.value!!,
        longitude = model.longitude.value!!,
        locale = model.location.value!!
    )

}

