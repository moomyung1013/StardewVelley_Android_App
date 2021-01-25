package kr.ac.kumoh.s20180909.mystardewvalley.ui.home

import androidx.lifecycle.MutableLiveData
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import kr.ac.kumoh.s20180909.mystardewvalley.MySingleton
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    data class Stardew(var id: Int, var name: String, var festival: String, var image: String, var calendar: String)

    companion object{
        const val QUEUE_TAG = "VolleyRequest"
        val SERVER_URL = "http://192.168.0.6:8080"
    }
    val list = MutableLiveData<ArrayList<Stardew>>()
    private val stardew = ArrayList<Stardew>()
    private var mQueue: RequestQueue
    val imageLoader: ImageLoader
    init {
        list.value = stardew
        mQueue = MySingleton.getInstance(application).requestQueue
        imageLoader = MySingleton.getInstance(application).imageLoader
    }

    fun getImageUrl(i: Int): String = "$SERVER_URL/image/" + URLEncoder.encode(stardew[i].image, "utf-8")
    fun getStardew(i :Int) = stardew[i]
    fun getSize() = stardew.size

    override fun onCleared(){
        super.onCleared()
        mQueue.cancelAll(QUEUE_TAG)
    }

    fun requestStardew(){
        var request = JsonArrayRequest(
            Request.Method.GET,
            SERVER_URL,
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
            val id = item.getInt("seasonId") //계절 인덱스
            val name = item.getString("seasonName") //계절 이름
            val festival = item.getString("Festival") //계절 축제
            val image = item.getString("seasonImage") //계절 대표 축제 이미지 경로
            val calendar = item.getString("seasonCalendar") //계절 캘린더 이미지 경로

            stardew.add(Stardew(id, name, festival, image, calendar))

        }
    }
}