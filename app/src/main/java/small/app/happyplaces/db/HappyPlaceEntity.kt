package small.app.happyplaces.db

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull
import small.app.happyplaces.model.HappyPlaceModel
import java.util.*

@Entity
data class HappyPlace(
    @PrimaryKey(autoGenerate = true)
    @NotNull
    val id: Long = 0,
    val title: String,
    val image: Uri,
    val description: String,
    val date: Date,
    val locale: String,
    val latitude: Double,
    val longitude: Double
) {
}

//Conversion method to go from DB model to kotlin model
fun List<HappyPlace>.asViewModel(): List<HappyPlaceModel> {
    return map {
        HappyPlaceModel(it)
    }

}