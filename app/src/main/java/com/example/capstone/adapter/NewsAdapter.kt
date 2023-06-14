package com.example.capstone.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.capstone.R
import com.example.capstone.data.remote.response.ArticlesItem
import com.example.capstone.databinding.ItemListNewsBinding
import com.example.capstone.utils.DateFormatter

class NewsAdapter(private val newslist: List<ArticlesItem>) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
            private lateinit var onItemClickCallback:OnItemClickCallback
            fun setOnItemClickCallback(onItemClickCallback:OnItemClickCallback){
                this.onItemClickCallback=onItemClickCallback
            }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater=LayoutInflater.from(viewGroup.context)

        val binding=ItemListNewsBinding.inflate(layoutInflater,viewGroup,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val (publishedAt,urlToImage,title)=newslist[position]
        viewHolder.title.text=title
        viewHolder.published.text=DateFormatter.formatDate(publishedAt)

        if (urlToImage.isNullOrBlank()){
            Glide.with(viewHolder.itemView.context)
                .load(R.drawable.ic_broken_image_black)
                .into(viewHolder.image)
        }else {
            Glide.with(viewHolder.itemView.context)
                .load(urlToImage)
                .into(viewHolder.image)
        }
        viewHolder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(newslist[position]) }
    }
    override fun getItemCount()=newslist.size

    interface OnItemClickCallback{
        fun onItemClicked(data:ArticlesItem)
    }

    class ViewHolder(binding:ItemListNewsBinding):RecyclerView.ViewHolder(binding.root){
        val title=binding.tvItemTitle
        val image=binding.imgPoster
        val published=binding.tvItemPublishedDate
    }
}