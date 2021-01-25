package kr.ac.kumoh.s20180909.mystardewvalley.ui.fish

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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import kr.ac.kumoh.s20180909.mystardewvalley.R
import kr.ac.kumoh.s20180909.mystardewvalley.ui.fish.FishViewModel

class FishFragment : Fragment() {
    private lateinit var model: FishViewModel
    private val myAdapter = StardewAdapter()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        model = ViewModelProvider(activity as AppCompatActivity).get(FishViewModel::class.java)
        model.list.observe(viewLifecycleOwner,
            Observer<ArrayList<FishViewModel.Fish>> {
            myAdapter.notifyDataSetChanged()
        })

        val root = inflater.inflate(R.layout.fragment_fish, container, false)
        val lsResult = root.findViewById<RecyclerView>(R.id.lsResult)
        lsResult.apply {
            layoutManager = GridLayoutManager(activity, 2)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = myAdapter
        }
        model.requestStardew()
        return root
    }
    inner class StardewAdapter: RecyclerView.Adapter<StardewAdapter.ViewHolder>() {
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            val txName = itemView.findViewById<TextView>(R.id.text1)//이름, 가격
            val txModel = itemView.findViewById<TextView>(R.id.text2)//서식지역 및 계절, 번들
            val niImage: NetworkImageView =
                itemView.findViewById<NetworkImageView>(R.id.image)

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
            val view = layoutInflater.inflate(R.layout.item_fish, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: StardewAdapter.ViewHolder, position: Int) {
            var season = ""
            //영어로 저장된 계절 이름을 한글로 바꿔 출력하기 위한 조건문
            when(model.getStardew(position).season){
                "Spring" -> season="봄"
                "Summer" -> season="여름"
                "Autumn" -> season="가을"
                "Winter" -> season="겨울"
            }
            holder.txName.text = model.getStardew(position).name +
                    "("+model.getStardew(position).price.toString()+"$)"
            holder.txModel.text = "$season · ${model.getStardew(position).area} 물고기" +
                    " / 번들${model.getStardew(position).bundle}"
            holder.niImage.setImageUrl(model.getImageUrl(position), model.imageLoader)

        }
    }
}