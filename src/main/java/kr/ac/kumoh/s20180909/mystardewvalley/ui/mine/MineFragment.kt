package kr.ac.kumoh.s20180909.mystardewvalley.ui.mine

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
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import kr.ac.kumoh.s20180909.mystardewvalley.R

class MineFragment : Fragment() {
    private lateinit var model: MineViewModel
    private val myAdapter = StardewAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        model = ViewModelProvider(activity as AppCompatActivity)
            .get(MineViewModel::class.java)
        model.list.observe(viewLifecycleOwner,
            Observer<ArrayList<MineViewModel.Stardew>> {
            myAdapter.notifyDataSetChanged()
        })

        val root = inflater.inflate(R.layout.fragment_mine, container, false)
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
            val txName = itemView.findViewById<TextView>(R.id.text1)//광물 및 몬스터 이름
            val txModel = itemView.findViewById<TextView>(R.id.text2)//광산, 종류
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
            val view = layoutInflater.inflate(R.layout.item_mine, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: StardewAdapter.ViewHolder, position: Int) {
            holder.txName.text = model.getStardew(position).name
            holder.txModel.text = "${model.getStardew(position).area}" +
                    " / ${model.getStardew(position).monster}"
            holder.niImage.setImageUrl(model.getImageUrl(position), model.imageLoader)

        }
    }
}