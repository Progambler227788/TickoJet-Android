package com.talhaatif.tickojet.adapter.viewholder

import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.zhpan.bannerview.BaseBannerAdapter
import com.zhpan.bannerview.BaseViewHolder
import com.talhaatif.tickojet.R

class ImageBannerViewHolder : BaseBannerAdapter<Int>() {

    override fun bindData(
        holder: BaseViewHolder<Int>,
        data: Int,
        position: Int,
        pageSize: Int
    ) {
        val imageView: ImageView = holder.findViewById(R.id.banner_image)
        Glide.with(holder.itemView.context)
            .load(data)
            .into(imageView)
    }

    override fun getLayoutId(viewType: Int): Int {
        return R.layout.item_banner_image
    }
}
