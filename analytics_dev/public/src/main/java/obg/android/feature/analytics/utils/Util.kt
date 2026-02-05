package obg.android.feature.analytics.utils

import android.annotation.SuppressLint
import android.os.Bundle
import java.util.Locale

@SuppressLint("DefaultLocale")
fun String.processTextForGTM(): String {
    var newString = ""
    if (!this.isAllUpperCase()) {
        val strs = this.split("(?<!^)(?=[A-Z])".toRegex(), 0)

        strs.forEach {
            newString += if (it.contains(" ")) {
                it
            } else {
                "$it "
            }
        }
    }

    newString = newString.replace("_", " ").capitalizeWords()
    return newString.trim()
}

@SuppressLint("DefaultLocale")
fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") {
        replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }

@SuppressLint("DefaultLocale")
fun String.isAllUpperCase(): Boolean {
    for (c in this.toCharArray()) {
        if (c.isLowerCase()) {
            return false
        }
    }
    return true
}

fun Bundle.toListPair(): List<Pair<String, String>> {
    return keySet().mapNotNull { key ->
        val value = getString(key)?.takeIf { it.isNotBlank() }
        if (value != null) {
            key to value
        } else {
            null
        }
    }
}

fun List<String>.getProcessedList(): List<String> {
    val newList = mutableListOf<String>()

    for (str in this) {
        newList.add(str.processTextForGTM())
    }
    return newList
}
