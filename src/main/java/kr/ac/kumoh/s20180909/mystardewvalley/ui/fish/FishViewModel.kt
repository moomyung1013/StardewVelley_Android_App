package kr.ac.kumoh.s20180909.mystardewvalley.ui.fish

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import kr.ac.kumoh.s20180909.mystardewvalley.MySingleton
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

class FishViewModel(application: Application) : AndroidViewModel(application) {
    data class Fish(var id: Int, var name: String, var bundle: String,
                    var season: String, var area: String, var price:Int, var image: String)

    companion object{
        const val QUEUE_TAG = "VolleyRequest"
        //val SERVER_URL = "http://202.31.136.148:8080"
        val SERVER_URL = "http://192.168.0.6:8080"
    }
    val list = MutableLiveData<ArrayList<Fish>>()
    private val fish = ArrayList<Fish>()
    private var mQueue: RequestQueue
    val imageLoader: ImageLoader
    init {
        list.value = fish
        mQueue = MySingleton.getInstance(application).requestQueue
        imageLoader = MySingleton.getInstance(application).imageLoader
    }

    fun getImageUrl(i: Int): String = "$SERVER_URL/image/" +
            URLEncoder.encode(fish[i].image, "utf-8")
    fun getStardew(i :Int) = fish[i]
    fun getSize() = fish.size

    override fun onCleared(){
        super.onCleared()
        mQueue.cancelAll(QUEUE_TAG)
    }

    fun requestStardew(){
        var request = JsonArrayRequest(
            Request.Method.GET,
            "$SERVER_URL/fish",
            null,
            {
//                Toast.makeText(getApplication(),
//                    it.toString(),
//                    Toast.LENGTH_LONG).show()
                fish.clear()
                parseJson(it)
                list.value = fish
            },
            {
                Toast.makeText(getApplication(),
                    it.toString(),
                    Toast.LENGTH_LONG).show()
            }
        )

        request.tag = QUEUE_TAG
        MySingleton.getInstance(getApplication()).addToRequestQueue(request)
    }

    private fun parseJson(items: JSONArray) {
        for (i in 0 until items.length()) {
            val item: JSONObject = items[i] as JSONObject
            val id = item.getInt("fishId")
            val name = item.getString("fishName")
            val bundle = item.getString("isBundle")
            val season = item.getString("fishSeason")
            val area = item.getString("fishArea")
            val price = item.getInt("fishPrice")
            val image = item.getString("fishImage")

            fish.add(Fish(id, name, bundle, season, area, price, image))

        }
    }
}