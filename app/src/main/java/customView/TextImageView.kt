package customView

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.saeedmpt.chatapp.R

class TextImageView : AppCompatTextView {

    private var mStartWidth: Int = 0
    private var mStartHeight: Int = 0
    private var mTopWidth: Int = 0
    private var mTopHeight: Int = 0
    private var mEndWidth: Int = 0
    private var mEndHeight: Int = 0
    private var mBottomWidth: Int = 0
    private var mBottomHeight: Int = 0

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }


    private fun init(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextImageView)

        mStartWidth = typedArray.getDimensionPixelOffset(R.styleable.TextImageView_drawableStartWidth, 0)
        mStartHeight = typedArray.getDimensionPixelOffset(R.styleable.TextImageView_drawableStartHeight, 0)
        mTopWidth = typedArray.getDimensionPixelOffset(R.styleable.TextImageView_drawableTopWidth, 0)
        mTopHeight = typedArray.getDimensionPixelOffset(R.styleable.TextImageView_drawableTopHeight, 0)
        mEndWidth = typedArray.getDimensionPixelOffset(R.styleable.TextImageView_drawableEndWidth, 0)
        mEndHeight = typedArray.getDimensionPixelOffset(R.styleable.TextImageView_drawableEndHeight, 0)
        mBottomWidth = typedArray.getDimensionPixelOffset(R.styleable.TextImageView_drawableBottomWidth, 0)
        mBottomHeight = typedArray.getDimensionPixelOffset(R.styleable.TextImageView_drawableBottomHeight, 0)
        typedArray.recycle()
        setDrawablesSize()
    }

    private fun setDrawablesSize() {
        val compoundDrawables = compoundDrawablesRelative
        for (i in compoundDrawables.indices) {
            when (i) {
                0 -> setDrawableBounds(compoundDrawables[0], mStartWidth, mStartHeight)
                1 -> setDrawableBounds(compoundDrawables[1], mTopWidth, mTopHeight)
                2 -> setDrawableBounds(compoundDrawables[2], mEndWidth, mEndHeight)
                3 -> setDrawableBounds(compoundDrawables[3], mBottomWidth, mBottomHeight)
                else -> {
                }
            }

        }
        setCompoundDrawablesRelative(
            compoundDrawables[0],
            compoundDrawables[1],
            compoundDrawables[2],
            compoundDrawables[3]
        )
    }

     fun setDrawableBounds(drawable: Drawable?, width: Int, height: Int) {
        if (drawable != null) {
            drawable.setBounds(0, 0, width, height)
            if (width == 0 || height == 0) {
                val scale = drawable.intrinsicHeight.toDouble() / drawable.intrinsicWidth.toDouble()
                val bounds = drawable.bounds
                // When only one value is given for height and width, self-adaptive
                if (width == 0 && height != 0) {
                    bounds.right = (bounds.bottom / scale).toInt()
                    drawable.bounds = bounds
                }
                if (width != 0 && height == 0) {
                    bounds.bottom = (bounds.right * scale).toInt()
                    drawable.bounds = bounds
                }
            }
        }
    }
}