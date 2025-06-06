package com.example.shoppapp.common.ui.imageview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.withStyledAttributes
import com.example.shoppapp.R
import kotlin.math.min

class CircleImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var mCustomDrawable: Drawable? = null

    init {
        outlineProvider = ViewOutlineProvider.BACKGROUND
        clipToOutline = true
        setBackgroundResource(R.drawable.background_circle_image_view)
        scaleType = ScaleType.CENTER_CROP
        context.withStyledAttributes(attrs, R.styleable.CircleImageView) {
            mCustomDrawable = getDrawable(R.styleable.CircleImageView_civ_src)
        }
        mCustomDrawable?.let {
            setImageDrawable(it)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val minDimension = min(width, height)
        setMeasuredDimension(minDimension, minDimension)
    }

}