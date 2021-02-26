package com.shopify.shopifyapp.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView

class MagenativeImageView : androidx.appcompat.widget.AppCompatImageView {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var d = drawable
        if (d != null) {
            var width = MeasureSpec.getSize(widthMeasureSpec)
            var height = width * d.intrinsicHeight / d.intrinsicWidth
            setMeasuredDimension(width, height)
        } else super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}