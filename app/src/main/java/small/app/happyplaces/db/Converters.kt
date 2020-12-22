package small.app.happyplaces.db

import android.net.Uri
import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }

    @TypeConverter
    fun uriToString(uri: Uri?): String? {
        return uri.toString();
    }

    @TypeConverter
    fun stringToUri(str: String?): Uri? {
        return Uri.parse(str);
    }
}