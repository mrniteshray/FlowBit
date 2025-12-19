package xcom.niteshray.xapps.xblockit.feature.block.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import xcom.niteshray.xapps.xblockit.model.Appitem
import xcom.niteshray.xapps.xblockit.util.BlockAppsUtil

import xcom.niteshray.xapps.xblockit.util.InstallApps

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val _apps = mutableStateListOf<Appitem>()
    var app : List<Appitem> = _apps
    val context = application

    init {
        viewModelScope.launch {
            viewModelScope.launch(Dispatchers.IO) {
                val blockapps = BlockAppsUtil.getBlockApps(application)
                val result = InstallApps.getApps(application).map {
                    it.copy(isBlock = blockapps.contains(it.packageName))
                }
                withContext(Dispatchers.Main) {
                    _apps.addAll(result)
                }
            }
        }
    }

    fun updateBlock(packageName: String, isBlocked: Boolean) {
        _apps.find { it.packageName == packageName }?.isBlock = isBlocked
        if (isBlocked){
            BlockAppsUtil.addBlockApp(context = context,packageName)
        }else{
            BlockAppsUtil.removeBlockApp(context = context,packageName)
        }
    }
}