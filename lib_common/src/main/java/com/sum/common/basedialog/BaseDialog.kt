package com.sum.common.basedialog

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.Size
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDialog
import com.sum.common.R
import com.sum.framework.utils.DisplayHelper

/**
 *
 * @author why
 * @date 2023/11/17 17:36
 */
class BaseDialog private constructor(
    context: Context,
    val layoutId: Int = R.layout.dialog_confirm,
    themeResId: Int = R.style.Dialog
) :
    AppCompatDialog(context, themeResId) {
    protected var views = SparseArray<View?>()
    protected var cancelIds: MutableList<Int> = ArrayList()
    protected var controlView: DialogRootView? = null
    protected var width = 0
    protected var height = 0
    protected var bgBean = BgBean() //背景属性对象



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controlView = DialogRootView(context)
        val view = LayoutInflater.from(context).inflate(layoutId, controlView, false)
        controlView?.addView(view)
        setContentView(controlView!!)
        //判断xml根布局, 宽高
        val xmlLp = view.layoutParams
        //window窗口
        val windowLP = window!!.attributes
        windowLP.width = xmlLp.width
        windowLP.height = xmlLp.height
        window!!.attributes = windowLP
        //controlView宽高
        val controlLP: ViewGroup.LayoutParams = controlView!!.getLayoutParams()
        controlLP.width = xmlLp.width
        controlLP.height = xmlLp.height
        controlView?.setLayoutParams(controlLP)
        init()
    }

    protected fun init() {
        setCanceledOnTouchOutside(true)
        window!!.setBackgroundDrawable(getRoundRectDrawable(dp2px(context, 0f), Color.TRANSPARENT))
        window!!.setWindowAnimations(R.style.li_dialog_default)
    }

    protected fun with(): BaseDialog {
        show()
        dismiss()
        return this
    }

    fun setOnTouchOutside(cancel: Boolean): BaseDialog {
        super.setCancelable(cancel)
        return this
    }
    //    >>>>>>>>>>>>>>>>>>>>>>>>>>>>设置动画>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /**
     * 自定义动画
     * @param style
     * @return
     */
    fun setAnimationsStyle(style: Int): BaseDialog {
        window!!.setWindowAnimations(style)
        return this
    }

    /**
     * 内置动画
     * @param styleType 类型
     * @return
     */
    fun setAnimations(@LAnimationsType.LAnimationsType styleType: String?): BaseDialog {
        var style = -1
        when (styleType) {
            LAnimationsType.DEFAULT -> style = R.style.li_dialog_default
            LAnimationsType.SCALE -> style = R.style.li_dialog_scale
            LAnimationsType.LEFT -> style = R.style.li_dialog_translate_left
            LAnimationsType.TOP -> style = R.style.li_dialog_translate_top
            LAnimationsType.RIGHT -> style = R.style.li_dialog_translate_right
            LAnimationsType.BOTTOM -> style = R.style.li_dialog_translate_bottom
        }
        if (style != -1) {
            setAnimationsStyle(style)
        }
        return this
    }
    //    >>>>>>>>>>>>>>>>>>>>>>>>>>>>设置位置>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /**
     * 设置位置
     * @param gravity
     * @param offX
     * @param offY
     */
    fun setGravity(gravity: Int, offX: Int, offY: Int): BaseDialog {
        setGravity(gravity)
        val layoutParams = window!!.attributes
        layoutParams.x = offX
        layoutParams.y = offY
        window!!.attributes = layoutParams
        return this
    }

    fun setGravity(gravity: Int): BaseDialog {
        window!!.setGravity(gravity)
        return this
    }

    /**
     * 遮罩透明度
     * @param value 0-1f
     * @return
     */
    fun setMaskValue(value: Float): BaseDialog {
        window!!.setDimAmount(value)
        return this
    }
    //    >>>>>>>>>>>>>>>>>>>>>>>>>>>>设置背景>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /**
     * 刷新背景
     * @return
     */
    protected fun refreshBg(): BaseDialog {
//        getWindow().setBackgroundDrawable(getRoundRectDrawable(bgRadius, bgColor));
        controlView?.setBackground(bgBean.getRoundRectDrawable())
        controlView?.setBgRadius(
            bgBean.left_top_radius,
            bgBean.right_top_radius,
            bgBean.right_bottom_radius,
            bgBean.left_bottom_radius
        )
        return this
    }

    /**
     * 设置背景颜色
     * @return
     */
    fun setBgColor(@ColorInt color: Int): BaseDialog {
        bgBean.color = color
        return refreshBg()
    }

    fun setBgColor(@Size(min = 1) colorString: String?): BaseDialog {
        bgBean.color = Color.parseColor(colorString)
        return refreshBg()
    }

    /**
     * 渐变背景
     * @param orientation
     * @param colors
     * @return
     */
    fun setBgColor(
        orientation: GradientDrawable.Orientation?,
        @ColorInt vararg colors: Int
    ): BaseDialog {
        bgBean.gradientsOrientation = orientation!!
        bgBean.gradientsColors = colors
        return refreshBg()
    }

    fun setBgColor(
        orientation: GradientDrawable.Orientation?,
        @Size(min = 1) vararg colorStrings: String?
    ): BaseDialog {
        bgBean.gradientsOrientation = orientation!!
        if (colorStrings == null) {
            return this
        }
        bgBean.gradientsColors = IntArray(colorStrings.size)
        for (i in 0 until (bgBean.gradientsColors?.size ?: 0)) {
            bgBean.gradientsColors!![i] = Color.parseColor(colorStrings[i])
        }
        return refreshBg()
    }

    fun setBgColorRes(@ColorRes colorRes: Int): BaseDialog {
        bgBean.color = context.resources.getColor(colorRes)
        return refreshBg()
    }

    fun setBgColorRes(
        orientation: GradientDrawable.Orientation?,
        @Size(min = 1) vararg colorRes: String?
    ): BaseDialog {
        bgBean.gradientsOrientation = orientation!!
        bgBean.gradientsColors = IntArray(colorRes.size)
        for (i in colorRes.indices) {
            bgBean.gradientsColors!![i] = Color.parseColor(colorRes[i])
        }
        return refreshBg()
    }

    fun setBgColorRes(
        orientation: GradientDrawable.Orientation?,
        @ColorRes vararg colorRes: Int
    ): BaseDialog {
        bgBean.gradientsOrientation = orientation!!
        bgBean.gradientsColors = IntArray(colorRes.size)
        for (i in colorRes.indices) {
            bgBean.gradientsColors!![i] = getColor(colorRes[i])
        }
        return refreshBg()
    }

    /**
     * 设置背景圆角
     * @param bgRadius
     */
    fun setBgRadius(bgRadius: Float): BaseDialog {
        setBgRadius(bgRadius, bgRadius, bgRadius, bgRadius)
        return refreshBg()
    }

    fun setBgRadius(
        left_top_radius: Float,
        right_top_radius: Float,
        right_bottom_radius: Float,
        left_bottom_radius: Float
    ): BaseDialog {
        bgBean.left_top_radius = dp2px(context, left_top_radius).toFloat()
        bgBean.right_top_radius = dp2px(context, right_top_radius).toFloat()
        bgBean.right_bottom_radius = dp2px(context, right_bottom_radius).toFloat()
        bgBean.left_bottom_radius = dp2px(context, left_bottom_radius).toFloat()
        return refreshBg()
    }

    /**
     * 设置背景圆角
     * @param bgRadius
     */
    fun setBgRadiusPX(bgRadius: Int): BaseDialog {
        setBgRadiusPX(
            bgRadius.toFloat(),
            bgRadius.toFloat(),
            bgRadius.toFloat(),
            bgRadius.toFloat()
        )
        return refreshBg()
    }

    fun setBgRadiusPX(
        left_top_radius: Float,
        right_top_radius: Float,
        right_bottom_radius: Float,
        left_bottom_radius: Float
    ): BaseDialog {
        bgBean.left_top_radius = left_top_radius
        bgBean.right_top_radius = right_top_radius
        bgBean.right_bottom_radius = right_bottom_radius
        bgBean.left_bottom_radius = left_bottom_radius
        return refreshBg()
    }
    //    >>>>>>>>>>>>>>>>>>>>>>>>>>>>设置宽高>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /**
     * 设置宽高(精确)
     */
    private fun setWidthHeight(): BaseDialog {
//        Window dialogWindow = getWindow();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        lp.width = width;
//        lp.height = height;
//        dialogWindow.setAttributes(lp);
        val layoutParams: ViewGroup.LayoutParams = controlView!!.getLayoutParams()
        layoutParams.width = width
        layoutParams.height = height
        controlView?.setLayoutParams(layoutParams)
        return this
    }

    /**
     * 精确宽
     * @param width
     * @return
     */
    fun setWidth(width: Int): BaseDialog {
        this.width = dp2px(context, width.toFloat())
        return setWidthHeight()
    }

    fun setWidthPX(width: Int): BaseDialog {
        this.width = width
        return setWidthHeight()
    }

    /**
     * 最大宽
     * @param width
     * @return
     */
    fun setMaxWidth(width: Int): BaseDialog {
        setMaxWidthPX(dp2px(context, width.toFloat()))
        return this
    }

    fun setMaxWidthPX(width: Int): BaseDialog {
        controlView?.setMaxWidth(width)
        return this
    }

    /**
     * 最小宽
     * @param width
     * @return
     */
    fun setMinWidth(width: Int): BaseDialog {
        setMinWidthPX(dp2px(context, width.toFloat()))
        return this
    }

    fun setMinWidthPX(width: Int): BaseDialog {
        controlView?.setMinimumWidth(width)
        return this
    }

    /**
     * 精确高度
     * @param height
     * @return
     */
    fun setHeight(height: Int): BaseDialog {
        this.height = dp2px(context, height.toFloat())
        return setWidthHeight()
    }

    fun setHeightPX(height: Int): BaseDialog {
        this.height = height
        return setWidthHeight()
    }

    /**
     * 最大高度
     * @param height
     * @return
     */
    fun setMaxHeight(height: Int): BaseDialog {
        setMaxHeightPX(dp2px(context, height.toFloat()))
        return this
    }

    fun setMaxHeightPX(height: Int): BaseDialog {
        controlView?.setMaxHeight(height)
        return this
    }

    /**
     * 最小高度
     * @param height
     * @return
     */
    fun setMinHeight(height: Int): BaseDialog {
        setMinHeightPX(dp2px(context, height.toFloat()))
        return this
    }

    fun setMinHeightPX(height: Int): BaseDialog {
        controlView?.setMinimumHeight(height)
        return this
    }

    /**
     * 设置宽占屏幕的比例
     * @param widthRatio
     */
    fun setWidthRatio(widthRatio: Double): BaseDialog {
        width = ((DisplayHelper.getScreenWidth() * widthRatio).toInt())
        setWidthHeight()
        return this
    }

    fun setMaxWidthRatio(widthRatio: Double): BaseDialog {
        return setMaxWidthPX((DisplayHelper.getScreenWidth() * widthRatio).toInt())
    }

    fun setMinWidthRatio(widthRatio: Double): BaseDialog {
        return setMinWidthPX((DisplayHelper.getScreenWidth() * widthRatio).toInt())
    }

    /**
     * 设置高占屏幕的比例
     * @param heightRatio
     */
    fun setHeightRatio(heightRatio: Double): BaseDialog {
        height = ((DisplayHelper.getFullScreenHeight(context) * heightRatio).toInt())
        setWidthHeight()
        return this
    }

    fun setMaxHeightRatio(heightRatio: Double): BaseDialog {
        return setMaxHeightPX((DisplayHelper.getScreenWidth() * heightRatio).toInt())
    }

    fun setMinHeightRatio(heightRatio: Double): BaseDialog {
        return setMinHeightPX((DisplayHelper.getScreenWidth() * heightRatio).toInt())
    }
    //    >>>>>>>>>>>>>>>>>>>>>>>>>>>>设置监听>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    /**
     * 设置监听
     * @param onClickListener
     * @param viewIds
     */
    fun setOnClickListener(onClickListener: DialogOnClickListener, vararg viewIds: Int): BaseDialog {
        val Dialog = this
        for (i in viewIds.indices) {
            if (cancelIds.contains(viewIds[i])) {
                getView<View>(viewIds[i])!!.setOnClickListener { v ->
                    onClickListener.onClick(v, Dialog)
                    Dialog.dismiss()
                }
            } else {
                getView<View>(viewIds[i])!!.setOnClickListener { v ->
                    onClickListener.onClick(
                        v,
                        Dialog
                    )
                }
            }
        }
        return this
    }

    /**
     * 设置 关闭dialog的按钮
     * @param viewIds
     * @return
     */
    fun setCancelBtn(vararg viewIds: Int): BaseDialog {
        for (i in viewIds.indices) {
            if (cancelIds.contains(viewIds[i])) {
                continue
            }
            cancelIds.add(viewIds[i])
            getView<View>(viewIds[i])!!.setOnClickListener { dismiss() }
        }
        return this
    }

    //    >>>>>>>>>>>>>>>>>>>>>>>>>>>>设置常见属性>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun <T : View?> getView(@IdRes viewId: Int): T? {
        var view = views[viewId]
        if (view == null) {
            view = findViewById(viewId)
            views.put(viewId, view)
        }
        return view as T?
    }

    /**
     * Will set the text of a TextView.
     *
     * @param viewId The view id.
     * @param value  The text to put in the text view.
     * @return The BaseViewHolder for chaining.
     */
    fun setText(@IdRes viewId: Int, value: CharSequence?): BaseDialog {
        val view = getView<TextView>(viewId)!!
        view.text = value
        return this
    }

    fun setText(@IdRes viewId: Int, @StringRes strId: Int): BaseDialog {
        val view = getView<TextView>(viewId)!!
        view.setText(strId)
        return this
    }

    /**
     * 设置文字大小
     * @param viewId
     * @param size (单位：SP)
     * @return
     */
    fun setTextSize(@IdRes viewId: Int, size: Float): BaseDialog {
        setTextSizePX(viewId, sp2px(context, size).toFloat())
        return this
    }

    fun setTextSizePX(@IdRes viewId: Int, size: Float): BaseDialog {
        val view = getView<TextView>(viewId)!!
        view.textSize = sp2px(context, size).toFloat()
        return this
    }

    /**
     * Will set the image of an ImageView from a resource id.
     *
     * @param viewId     The view id.
     * @param imageResId The image resource id.
     * @return The BaseViewHolder for chaining.
     */
    fun setImageResource(@IdRes viewId: Int, @DrawableRes imageResId: Int): BaseDialog {
        val view = getView<ImageView>(viewId)!!
        view.setImageResource(imageResId)
        return this
    }

    /**
     * Will set background color of a view.
     *
     * @param viewId The view id.
     * @param color  A color, not a resource id.
     * @return The BaseViewHolder for chaining.
     */
    fun setBackgroundColor(@IdRes viewId: Int, @ColorInt color: Int): BaseDialog {
        val view = getView<View>(viewId)!!
        view.setBackgroundColor(color)
        return this
    }

    /**
     * Will set background of a view.
     *
     * @param viewId        The view id.
     * @param backgroundRes A resource to use as a background.
     * @return The BaseViewHolder for chaining.
     */
    fun setBackgroundRes(@IdRes viewId: Int, @DrawableRes backgroundRes: Int): BaseDialog {
        val view = getView<View>(viewId)!!
        view.setBackgroundResource(backgroundRes)
        return this
    }

    /**
     * Will set text color of a TextView.
     *
     * @return The BaseViewHolder for chaining.
     */
    fun setTextColor(@IdRes viewId: Int, @ColorInt textColor: Int): BaseDialog {
        val view = getView<TextView>(viewId)!!
        view.setTextColor(textColor)
        return this
    }

    /**
     * Will set the image of an ImageView from a drawable.
     *
     * @param viewId   The view id.
     * @param drawable The image drawable.
     * @return The BaseViewHolder for chaining.
     */
    fun setImageDrawable(@IdRes viewId: Int, drawable: Drawable?): BaseDialog {
        val view = getView<ImageView>(viewId)!!
        view.setImageDrawable(drawable)
        return this
    }

    /**
     * Add an action to set the image of an image view. Can be called multiple times.
     */
    fun setImageBitmap(@IdRes viewId: Int, bitmap: Bitmap?): BaseDialog {
        val view = getView<ImageView>(viewId)!!
        view.setImageBitmap(bitmap)
        return this
    }

    /**
     * Add an action to set the alpha of a view. Can be called multiple times.
     * Alpha between 0-1.
     */
    fun setAlpha(@IdRes viewId: Int, value: Float): BaseDialog {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getView<View>(viewId)!!.alpha = value
        } else {
            // Pre-honeycomb hack to set Alpha value
            val alpha = AlphaAnimation(value, value)
            alpha.duration = 0
            alpha.fillAfter = true
            getView<View>(viewId)!!.startAnimation(alpha)
        }
        return this
    }

    /**
     * Set a view visibility to VISIBLE (true) or GONE (false).
     *
     * @param viewId  The view id.
     * @param visible True for VISIBLE, false for GONE.
     * @return The BaseViewHolder for chaining.
     */
    fun setGone(@IdRes viewId: Int, visible: Boolean): BaseDialog {
        val view = getView<View>(viewId)!!
        view.visibility = if (visible) View.VISIBLE else View.GONE
        return this
    }

    /**
     * Set a view visibility to VISIBLE (true) or INVISIBLE (false).
     *
     * @param viewId  The view id.
     * @param visible True for VISIBLE, false for INVISIBLE.
     * @return The BaseViewHolder for chaining.
     */
    fun setVisible(@IdRes viewId: Int, visible: Boolean): BaseDialog {
        val view = getView<View>(viewId)!!
        view.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        return this
    }

    //    public static GradientDrawable getRoundRectDrawable(int radius, int color){
    //        //左上、右上、右下、左下的圆角半径
    //        float[] radiuss = {radius, radius, radius, radius, radius, radius, radius, radius};
    //        GradientDrawable drawable = new GradientDrawable();
    //        drawable.setCornerRadii(radiuss);
    //        drawable.setColor(color!=0 ? color : Color.TRANSPARENT);
    //        return drawable;
    //    }
    //    public static GradientDrawable getRoundRectDrawable(int radius, int color, boolean isFill, int strokeWidth){
    //        //左上、右上、右下、左下的圆角半径
    //        float[] radiuss = {radius, radius, radius, radius, radius, radius, radius, radius};
    //        GradientDrawable drawable = new GradientDrawable();
    //        drawable.setCornerRadii(radiuss);
    //        drawable.setColor(isFill ? color : Color.TRANSPARENT);
    //        drawable.setStroke(isFill ? 0 : strokeWidth, color);
    //        return drawable;
    //    }
    fun getColor(colorResId: Int): Int {
        return context.resources.getColor(colorResId)
    }

    interface DialogOnClickListener {
        fun onClick(v: View?, Dialog: BaseDialog?)
    }

    companion  object {
        /**
         * 获取对象
         * @param context
         * @return
         */
        fun newInstance(context: Context): BaseDialog {
            return BaseDialog(context).with()
        }

        fun newInstance(context: Context, layoutId: Int): BaseDialog {
            return BaseDialog(context, layoutId).with()
        }

        fun newInstance(context: Context, layoutId: Int, themeResId: Int): BaseDialog {
            return BaseDialog(context, layoutId, themeResId).with()
        }

        fun getRoundRectDrawable(radius: Int, color: Int): GradientDrawable {
            //左上、右上、右下、左下的圆角半径
            val radiuss = floatArrayOf(
                radius.toFloat(),
                radius.toFloat(),
                radius.toFloat(),
                radius.toFloat(),
                radius.toFloat(),
                radius.toFloat(),
                radius.toFloat(),
                radius.toFloat()
            )
            val drawable = GradientDrawable()
            drawable.cornerRadii = radiuss
            drawable.setColor(if (color != 0) color else Color.TRANSPARENT)
            return drawable
        }

        /** dp转px  */
        fun dp2px(context: Context, dpVal: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dpVal,
                context.resources.displayMetrics
            ).toInt()
        }

        /** sp转px  */
        fun sp2px(context: Context, spVal: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                spVal,
                context.resources.displayMetrics
            ).toInt()
        }
    }
}
