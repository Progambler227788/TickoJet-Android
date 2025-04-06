package com.talhaatif.tickojet.custom

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.talhaatif.tickojet.R
import com.talhaatif.tickojet.databinding.LayoutStatefulRecyclerviewBinding
import kotlin.math.abs

class StatefulRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutStatefulRecyclerviewBinding =
        LayoutStatefulRecyclerviewBinding.inflate(LayoutInflater.from(context), this)

    var emptyText: String = ""
        set(value) {
            binding.emptyText.text = value
            field = value
        }

    var emptyIcon: Drawable? = null
        set(value) {
            binding.emptyIcon.setImageDrawable(value)
            field = value
        }

    var errorText: String = ""
        set(value) {
            binding.errorText.text = value
            field = value
        }

    var errorIcon: Drawable? = null
        set(value) {
            binding.errorIcon.setImageDrawable(value)
            field = value
        }

    var retryAction: (() -> Unit)? = null
        set(value) {
            binding.retryButton.setOnClickListener { value?.invoke() }
            field = value
        }

    val recyclerView: RecyclerView
        get() = binding.recyclerView

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StatefulRecyclerView)
        try {
            emptyText = typedArray.getString(R.styleable.StatefulRecyclerView_emptyText) ?: ""
            emptyIcon = typedArray.getDrawable(R.styleable.StatefulRecyclerView_emptyIcon)
            errorText = typedArray.getString(R.styleable.StatefulRecyclerView_errorText) ?: ""
            errorIcon = typedArray.getDrawable(R.styleable.StatefulRecyclerView_errorIcon)

            val loadingLayout = typedArray.getResourceId(
                R.styleable.StatefulRecyclerView_loadingLayout,
                -1
            )
            if (loadingLayout != -1) {
                binding.loadingView.removeAllViews()
                LayoutInflater.from(context).inflate(loadingLayout, binding.loadingView)
            }
        } finally {
            typedArray.recycle()
        }
    }

    // Add animation methods
    fun setupRecyclerViewAnimations() {
        val animator = DefaultItemAnimator().apply {
            addDuration = 200
            removeDuration = 200
            moveDuration = 200
            changeDuration = 200
        }
        recyclerView.itemAnimator = animator

        // Add item decoration for spacing
        recyclerView.addItemDecoration(
            HorizontalMarginItemDecoration(
                margin = resources.getDimensionPixelSize(R.dimen.item_margin)
            )
        )
    }

    //  Old Carousel effect
    fun enableCarouselEffect(scale: Float = 0.8f) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                val lastVisiblePosition = layoutManager.findLastVisibleItemPosition()

                for (i in 0 until recyclerView.childCount) {
                    val child = recyclerView.getChildAt(i)
                    val position = recyclerView.getChildAdapterPosition(child)

                    // Calculate scale based on position
                    val scaleValue = if (position == firstVisiblePosition || position == lastVisiblePosition) {
                        scale
                    } else {
                        1.0f
                    }

                    // Apply animation
                    child.animate()
                        .scaleX(scaleValue)
                        .scaleY(scaleValue)
                        .setDuration(200)
                        .start()
                }
            }
        })
    }

    // Optimized carousel effect
    fun enableSmoothCarouselEffect(scale: Float = 0.85f, elevation: Float = 8f) {
        recyclerView.apply {
            // 1. Enable hardware acceleration for smoother animations
            setLayerType(View.LAYER_TYPE_HARDWARE, null)

            // 2. Add snap helper for better scrolling behavior
            LinearSnapHelper().attachToRecyclerView(this) // For center snapping

            // 3. Add item decoration for visual spacing
            addItemDecoration(LinearHorizontalSpacingDecoration(
                spacing = resources.getDimensionPixelSize(R.dimen.carousel_spacing),
                edgeSpacing = resources.getDimensionPixelSize(R.dimen.carousel_edge_spacing)
            ))

            // 4. Optimized scroll listener with property animations
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                private val animator = RecyclerViewItemAnimator(scale, elevation)

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val centerX = recyclerView.width / 2
                    for (i in 0 until recyclerView.childCount) {
                        val child = recyclerView.getChildAt(i)
                        val childCenterX = (child.left + child.right) / 2
                        val distance = abs(centerX - childCenterX)

                        // Calculate scale and elevation based on distance from center
                        val scaleFactor = 1f - (1f - scale) * (distance.toFloat() / centerX)
                        val elevationFactor = elevation * (1 - distance.toFloat() / centerX)

                        animator.animateItem(child, scaleFactor, elevationFactor)
                    }
                }
            })
        }
    }

    // Setup optimized animations
    fun setupSmoothAnimations() {
        recyclerView.apply {
            // 1. Use custom item animator
            itemAnimator = OptimizedItemAnimator().apply {
                addDuration = 180L
                changeDuration = 180L
                moveDuration = 180L
                removeDuration = 180L
            }

            // 2. Enable predictive animations
            layoutManager?.isItemPrefetchEnabled = true
            setItemViewCacheSize(20) // Cache more views offscreen
            setHasFixedSize(true) // If all items have same size
        }
    }

    // Custom animator class
    private inner class RecyclerViewItemAnimator(
        private val minScale: Float,
        private val maxElevation: Float
    ) {
        fun animateItem(view: View, scale: Float, elevation: Float) {
            // Use ViewPropertyAnimator for smooth transitions
            view.animate()
                .scaleX(scale)
                .scaleY(scale)
                .translationZ(elevation)
                .setDuration(150L)
                .setInterpolator(FastOutSlowInInterpolator())
                .start()
        }
    }


    fun showLoading() {
        binding.recyclerView.visibility = View.GONE
        binding.emptyView.visibility = View.GONE
        binding.errorView.visibility = View.GONE
        binding.loadingView.visibility = View.VISIBLE
    }

    fun showContent() {
        binding.loadingView.visibility = View.GONE
        binding.emptyView.visibility = View.GONE
        binding.errorView.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }

    fun showEmpty() {
        binding.loadingView.visibility = View.GONE
        binding.errorView.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.emptyView.visibility = View.VISIBLE
    }

    fun showError() {
        binding.loadingView.visibility = View.GONE
        binding.emptyView.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.errorView.visibility = View.VISIBLE
    }

    fun setLayoutManager(layoutManager: RecyclerView.LayoutManager) {
        binding.recyclerView.layoutManager = layoutManager
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>) {
        binding.recyclerView.adapter = adapter
    }
}



