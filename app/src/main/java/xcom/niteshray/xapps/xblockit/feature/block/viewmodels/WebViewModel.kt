package xcom.niteshray.xapps.xblockit.feature.block.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import xcom.niteshray.xapps.xblockit.util.BlockWesiteUtil
import java.net.URL

class WebViewModel(application: Application) : AndroidViewModel(application) {
    private val _web = mutableStateListOf<String>()
    val web: List<String> get() = _web

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val list = BlockWesiteUtil.GetWebsite(application)
            _web.addAll(list)
        }
    }

    fun addWebsite(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val base = getBaseUrl(url)
            if (validateWebsite(base)) {
                BlockWesiteUtil.AddWebsite(getApplication(), base)
                _web.add(base)
            }
        }
    }

    fun deleteWebsite(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val base = getBaseUrl(url)
            BlockWesiteUtil.RemoveWebsite(getApplication(), base)
            _web.remove(base)
        }
    }

    fun validateWebsite(url: String): Boolean {
        if (url.isEmpty()) return false
        val base = getBaseUrl(url)
        return !web.contains(base)
    }

    private fun getBaseUrl(url: String): String {
        return try {
            val parsed = URL(url)
            "${parsed.protocol}://${parsed.host}".let {
                if (parsed.port == -1) it else "$it:${parsed.port}"
            }
        } catch (e: Exception) {
            url
        }
    }
}