package kr.ac.kumoh.s20180909.mystardewvalley.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import kr.ac.kumoh.s20180909.mystardewvalley.R
import kr.ac.kumoh.s20180909.mystardewvalley.StardewActivity

class HomeFragment : Fragment() {
    private lateinit var model: HomeViewModel
    private val myAdapter = StardewAdapter()
    companion object{
        const val NAME = "name" // 클릭한 계절 이름
        const val IMAGE = "imageURL" //Season 테이블의 캘린더 이미지 경로
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        model = ViewModelProvider(activity as AppCompatActivity).get(HomeViewModel::class.java)
        model.list.observe(viewLifecycleOwner, Observer<ArrayList<HomeViewModel.Stardew>> {
            myAdapter.notifyDataSetChanged()
        })

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val lsResult = root.findViewById<RecyclerView>(R.id.lsResult)
        lsResult.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = myAdapter
        }
        model.requestStardew()
        return root
    }
    inner class StardewAdapter: RecyclerView.Adapter<StardewAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val txName = itemView.findViewById<TextView>(R.id.text1)
            val txModel = itemView.findViewById<TextView>(R.id.text2)
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
        ): StardewAdapter.ViewHolder {
            val view = layoutInflater.inflate(R.layout.item_home, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: StardewAdapter.ViewHolder, position: Int) {
            var season = ""
            //영어로 저장된 계절 이름을 한글로 바꿔 출력하기 위한 조건문
            when(model.getStardew(position).name){
                "Spring" -> season="봄"
                "Summer" -> season="여름"
                "Autumn" -> season="가을"
                "Winter" -> season="겨울"
            }
            holder.txName.text = "$season (${model.getStardew(position).name})" //계절 이름
            holder.txModel.text = model.getStardew(position).festival //축제
            holder.niImage.setImageUrl(model.getImageUrl(position), model.imageLoader) //대표 축제사진

            //아이템 뷰 클릭시 실행 -> 상세 페이지 호출
            holder.itemView.setOnClickListener {
                val myIntent = Intent(it.context, StardewActivity::class.java)
                myIntent.putExtra(NAME, model.getStardew(position).name) //계절 이름 전달
                myIntent.putExtra(IMAGE, model.getStardew(position).calendar) //캘린더 경로 전달
                startActivity(myIntent)
            }
        }
    }
}