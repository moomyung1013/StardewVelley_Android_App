package kr.ac.kumoh.s20180909.mystardewvalley

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import kotlinx.android.synthetic.main.activity_stardew.*
import kr.ac.kumoh.s20180909.mystardewvalley.ui.VillagerViewModel

class StardewActivity : AppCompatActivity() {
    companion object {
        val url = "http://192.168.0.6:8080"
    }

    private lateinit var model: VillagerViewModel
    private val myAdapter = VillagerAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stardew)

        val seasonName = intent.getStringExtra("name") //HomeFragment에서 전달한 계절 이름
        val calendarName = intent.getStringExtra("imageURL") //HomeFragment에서 전달한 경로
        val seasonUrl = "$url/$seasonName" //Villager 테이블을 받아오기 위한 경로 설정

        title = "$seasonName 생일 주민 데이터" //title 설정

        //NetworkImageView를 캘린더 이미지로 설정
        calendar.setImageUrl("$url/image/$calendarName", MySingleton.getInstance(this).imageLoader)

        //TextView를 클릭한 계절 이름으로 설정
        title2.text = seasonName

        IsResult.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = myAdapter
        }

        model = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory(application)).get(
            VillagerViewModel::class.java
        )
        model.list.observe(this, Observer<ArrayList<VillagerViewModel.Stardew>> {
            myAdapter.notifyDataSetChanged()
        })
        model.requestStardew(seasonUrl) //url 전달


    }
    inner class VillagerAdapter: RecyclerView.Adapter<VillagerAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val txName = itemView.findViewById<TextView>(R.id.name) //이름 출력
            val txJob = itemView.findViewById<TextView>(R.id.job) //직업, 결혼 가능 여부 출력
            val txGender = itemView.findViewById<TextView>(R.id.gender) //성별, 직업 출력
            val txfFood = itemView.findViewById<TextView>(R.id.fFood) //좋아하는 음식 출력
            val txnFood = itemView.findViewById<TextView>(R.id.nFood) //그저 그런 음식 출력
            val txhFood = itemView.findViewById<TextView>(R.id.hFood) //싫어하는 음식 출력
            val niImage: NetworkImageView = itemView.findViewById<NetworkImageView>(R.id.image)

            init {
                niImage.setDefaultImageResId(android.R.drawable.ic_menu_report_image)
            }
        }

        override fun getItemCount(): Int {
            return model.getSize()
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): VillagerAdapter.ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_villager, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: VillagerAdapter.ViewHolder, position: Int) {
            var season = ""
            var target = ""
            //태어난 계절을 영어에서 한글로 번역
            when(model.getStardew(position).birthSeason){
                "Spring" -> season="봄 생일"
                "Summer" -> season="여름 생일"
                "Autumn" -> season="가을 생일"
                "Winter" -> season="겨울 생일"
            }
            //O,X로 표시된 결혼 가능 여부를 한글로 변환
            when(model.getStardew(position).target){
                "O" -> target="가능"
                "X" -> target="불가능"
            }
            holder.txName.text = model.getStardew(position).name
            holder.txGender.text = "성별 : ${model.getStardew(position).gender}\n연애 및 결혼 : $target"
            holder.txJob.text = "생일 계절 : ${season}\n직업 : ${model.getStardew(position).job}"
            holder.txfFood.text = "좋아하는 음식: "+ model.getStardew(position).fFood
            holder.txnFood.text = "평범한 음식: "+ model.getStardew(position).nFood
            holder.txhFood.text = "싫어하는 음식: "+ model.getStardew(position).hFood
            holder.niImage.setImageUrl(model.getImageUrl(position, url), model.imageLoader)
        }
    }
}