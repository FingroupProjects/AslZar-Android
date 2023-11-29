package com.fin_group.aslzar.util

import android.app.Dialog
import android.content.res.Resources
import android.graphics.Rect
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.fin_group.aslzar.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class BaseDialogFullFragment : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.DialogFragmentTheme2

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        Dialog(requireContext(), theme)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupView()
    }

    private fun setupView() {
        setStyle(STYLE_NO_TITLE, R.style.DialogFragmentTheme2)

        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    override fun onResume() {
        super.onResume()

        dialog?.window?.decorView?.setOnTouchListener(SwipeDismissTouchListener(dialog, object : DismissCallbacks {
            override fun canDismiss(): Boolean {
                return true
            }

            override fun onDismiss(view: View) {
                dismiss()
            }
        }))
    }

    inner class SwipeDismissTouchListener(
        private val dialog: Dialog?,
        private val dismissCallbacks: DismissCallbacks
    ) : View.OnTouchListener {

        private val gestureDetector: GestureDetector

        init {
            gestureDetector = GestureDetector(dialog?.context, GestureListener())
        }

        override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
            return gestureDetector.onTouchEvent(motionEvent)
        }

        private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {

            override fun onDown(e: MotionEvent): Boolean {
                return true
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
                val deltaX = e2.x - e1.x
                val deltaY = e2.y - e1.y
                if (Math.abs(deltaY) > Math.abs(deltaX) && Math.abs(deltaY) > SWIPE_THRESHOLD_VELOCITY) {
                    dismissCallbacks.onDismiss(dialog!!.window!!.decorView)
                    return true
                }
                return false
            }
        }


    }

    interface DismissCallbacks {

        fun canDismiss(): Boolean
        fun onDismiss(view: View)

    }

    companion object {
        private const val SWIPE_THRESHOLD_VELOCITY = 100
    }
}