package com.bytedance.feedapp.adapter

import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bytedance.feedapp.R
import com.bytedance.feedapp.model.FeedItem
import com.bytedance.feedapp.model.ImageFeedItem
import com.bytedance.feedapp.model.LoadingFeedItem
import com.bytedance.feedapp.model.ProductFeedItem
import com.bytedance.feedapp.model.TextFeedItem
import com.bytedance.feedapp.model.VideoFeedItem
import java.util.Locale

class FeedAdapter(
    private val items: MutableList<FeedItem>,
    private val onItemLongClick: (Int) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isLoading = false

    companion object {
        const val VIEW_TYPE_TEXT = 0
        const val VIEW_TYPE_IMAGE = 1
        const val VIEW_TYPE_VIDEO = 2
        const val VIEW_TYPE_LOADING = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TextFeedItem -> VIEW_TYPE_TEXT
            is ImageFeedItem -> VIEW_TYPE_IMAGE
            is VideoFeedItem -> VIEW_TYPE_VIDEO
            is LoadingFeedItem -> VIEW_TYPE_LOADING
            is ProductFeedItem -> TODO()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_TEXT -> {
                val view = inflater.inflate(R.layout.feed_item_text, parent, false)
                TextViewHolder(view)
            }
            VIEW_TYPE_IMAGE -> {
                val view = inflater.inflate(R.layout.feed_item_image, parent, false)
                ImageViewHolder(view)
            }
            VIEW_TYPE_VIDEO -> {
                val view = inflater.inflate(R.layout.feed_item_video, parent, false)
                VideoViewHolder(view)
            }
            VIEW_TYPE_LOADING -> {
                val view = inflater.inflate(R.layout.feed_item_loading, parent, false)
                LoadingViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is TextViewHolder -> holder.bind(item as TextFeedItem)
            is ImageViewHolder -> holder.bind(item as ImageFeedItem)
            is VideoViewHolder -> holder.bind(item as VideoFeedItem)
            is LoadingViewHolder -> { /* No data to bind */ }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is VideoViewHolder) {
            holder.stopPlayback()
        }
    }

    override fun getItemCount(): Int = items.size

    fun addLoadingFooter() {
        if (!isLoading) {
            isLoading = true
            items.add(LoadingFeedItem())
            notifyItemInserted(items.size - 1)
        }
    }

    fun removeLoadingFooter() {
        if (isLoading) {
            isLoading = false
            val position = items.size - 1
            if (position > -1) {
                items.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    fun addMoreItems(newItems: List<FeedItem>) {
        val startPosition = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(startPosition, newItems.size)
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    abstract inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemLongClick(position)
                }
                true
            }
        }
    }

    inner class TextViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val textContent: TextView = itemView.findViewById(R.id.text_content)

        fun bind(item: TextFeedItem) {
            textContent.text = item.text
        }
    }

    inner class ImageViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val imageContent: ImageView = itemView.findViewById(R.id.image_content)
        private val textContent: TextView = itemView.findViewById(R.id.text_content)

        fun bind(item: ImageFeedItem) {
            textContent.text = item.text
            imageContent.load(item.imageUrl)
        }
    }

    inner class VideoViewHolder(itemView: View) : BaseViewHolder(itemView) {
        private val videoThumbnail: ImageView = itemView.findViewById(R.id.video_thumbnail)
        private val videoTimer: TextView = itemView.findViewById(R.id.video_timer)
        private val textContent: TextView = itemView.findViewById(R.id.text_content)
        private var countDownTimer: CountDownTimer? = null

        fun bind(item: VideoFeedItem) {
            textContent.text = item.text
            // You can load a thumbnail from the video here if you have one
            // videoThumbnail.load(item.videoThumbnailUrl)
        }

        fun startPlayback() {
            stopPlayback() // Stop any previous timer
            countDownTimer = object : CountDownTimer(10000, 1000) { // 10 seconds
                override fun onTick(millisUntilFinished: Long) {
                    val seconds = millisUntilFinished / 1000
                    videoTimer.text = String.format(Locale.getDefault(), "00:%02d", seconds)
                }

                override fun onFinish() {
                    videoTimer.text = "00:00"
                }
            }.start()
        }

        fun stopPlayback() {
            countDownTimer?.cancel()
            countDownTimer = null
            videoTimer.text = "00:10" // Reset timer text
        }
    }

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
