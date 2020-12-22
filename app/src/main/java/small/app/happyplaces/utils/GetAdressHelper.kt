package small.app.happyplaces.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import java.util.*

class GetAdressUtil() {

    suspend fun getAdress(
        context: Context,
        lat: Double,
        lng: Double,
        container: MutableLiveData<String>
    ) {

        val geocoder = Geocoder(context, Locale.getDefault())
        val adressList: List<Address>? = geocoder.getFromLocation(lat, lng, 1)
        if (adressList != null && !adressList.isEmpty()) {
            val adress: Address = adressList.get(0)
            val sb = StringBuilder()

            for (i in 0..adress.maxAddressLineIndex) {
                sb.append(adress.getAddressLine(i)).append(" ")
            }
            container.value = sb.toString().trim()

        }
        

    }
}