package kr.ac.kumoh.s20180909.mystardewvalley.ui

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

class VillagerViewModel(application: Application) : AndroidViewModel(application) {
    data class Stardew(var id: Int, var name: String, var job: String, var birthSeason: String, var gender: String, var target: String, var fFood:String, var nFood:String, var hFood:String, var image: String)

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
    fun getImageUrl(i: Int, url: String): String = "$url/image/" + URLEncoder.encode(stardew[i].image, "utf-8")
    fun getImageUrl(i: Int): String = "$SERVER_URL/image/" + URLEncoder.encode(stardew[i].image, "utf-8")
    fun getStardew(i :Int) = stardew[i]
    fun getSize() = stardew.size

    override fun onCleared(){
        super.onCleared()
        mQueue.cancelAll(QUEUE_TAG)
    }

    fun requestStardew(url: String?){
        var URl: String
        //마을 주민 데이터 리스트를 JSON 형태로 받아오기 위한 경로
        if (url==null)
        {
            URl = "$SERVER_URL/villager"
        }
        //선택한 계절에 태어난 주민 데이터 리스트를 받아오기 위한 경로
        else
        {
            URl = url
        }
        var request = JsonArrayRequest(
            Request.Method.GET,
            URl,
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
            val id = item.getInt("vid")
            val name = item.getString("vname")
            val job = item.getString("job")
            val birthSeason = item.getString("birthSeason")
            val gender = item.getString("gender")
            val target = item.getString("isTarget")
            val fFood = item.getString("FavoriteFood")
            val nFood = item.getString("NormalFood")
            val hFood = item.getString("HateFood")
            val image = item.getString("vimage")

            stardew.add(Stardew(id, name, job, birthSeason, gender, target, fFood, nFood, hFood, image))

        }
    }
}