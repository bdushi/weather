package obg.android.feature.analytics.utils

import android.os.Bundle
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

class KeyValuePairList : ArrayList<KeyValuePair>() {

    fun toJson(): JSONObject {
        val obj = JSONObject()

        for (pair in this) {
            try {
                obj.put(pair.key, pair.value)
            } catch (e: JSONException) {
                Timber.w(e)
            }

        }
        return obj
    }

    fun toBundle(): Bundle {
        val bundle = Bundle()
        for (pair in this) {
            bundle.putString(pair.key, pair.stringValue)
        }
        return bundle
    }

    companion object {
        fun createList(vararg values: KeyValuePair): KeyValuePairList {
            val list = KeyValuePairList()
            list.addAll(listOf(*values))
            return list
        }
    }
}
