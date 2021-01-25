package kr.ac.kumoh.s20180909.mystardewvalley.ui.crop

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import kr.ac.kumoh.s20180909.mystardewvalley.MySingleton
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

class CropViewModel(application: Application) : AndroidViewModel(application) {
    data class Crop(var id: Int, var name: String, var season: String,
                    var bundle:String, var price:Int, var image: String)

    companion object{
        const val QUEUE_TAG = "VolleyRequest"
        val SERVER_URL = "http://192.168.0.6:8080"
    }
    val list = MutableLiveData<ArrayList<Crop>>()
    private val stardew = ArrayList<Crop>()
    private var mQueue: RequestQueue
    val imageLoader: ImageLoader
    init {
        list.value = stardew
        mQueue = MySingleton.getInstance(application).requestQueue
        imageLoader = MySingleton.getInstance(application).imageLoader
    }

    fun getImageUrl(i: Int): String = "$SERVER_URL/image/" +
            URLEncoder.encode(stardew[i].image, "utf-8")
    fun getStardew(i :Int) = stardew[i]
    fun getSize() = stardew.size

    override fun onCleared(){
        super.onCleared()
        mQueue.cancelAll(QUEUE_TAG)
    }

    fun requestStardew(){
        var request = JsonArrayRequest(
            Request.Method.GET,
            "$SERVER_URL/crop", //crop 경로로 가서 Farm 테이블 데이터를 받아옴
            null,
            {
                stardew.clear()
                parseJson(it)
                list.value = stardew
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
            val id = item.getInt("farmId") //농작물 인덱스
            val name = item.getString("farmName") //농작물 이름
            val season = item.getString("farmSeason") //농작물 계절
            val bundle = item.getString("isBundle") //번들 유무
            val price = item.getInt("farmPrice") //되팔기 가격
            val image = item.getString("farmImage") //이미지

            stardew.add(Crop(id, name, season, bundle, price, image))

        }
    }
}