package com.skylock.ai_cartoon.cropvideoview


import android.graphics.Matrix


class ScaleManager(private val mViewSize: Size, private val mVideoSize: Size) {

    fun getScaleMatrix(scaleType: ScaleType): Matrix? {
        println("getScaleMatrix $scaleType")
        return when (scaleType) {
            ScaleType.NONE -> getNoScale()
            ScaleType.FIT_XY -> fitXY()
            ScaleType.FIT_CENTER -> fitCenter()
            ScaleType.FIT_START -> fitStart()
            ScaleType.FIT_END -> fitEnd()
            ScaleType.LEFT_TOP -> getOriginalScale(PivotPoint.LEFT_TOP)
            ScaleType.LEFT_CENTER -> getOriginalScale(PivotPoint.LEFT_CENTER)
            ScaleType.LEFT_BOTTOM -> getOriginalScale(PivotPoint.LEFT_BOTTOM)
            ScaleType.CENTER_TOP -> getOriginalScale(PivotPoint.CENTER_TOP)
            ScaleType.CENTER -> getOriginalScale(PivotPoint.CENTER)
            ScaleType.CENTER_BOTTOM -> getOriginalScale(PivotPoint.CENTER_BOTTOM)
            ScaleType.RIGHT_TOP -> getOriginalScale(PivotPoint.RIGHT_TOP)
            ScaleType.RIGHT_CENTER -> getOriginalScale(PivotPoint.RIGHT_CENTER)
            ScaleType.RIGHT_BOTTOM -> getOriginalScale(PivotPoint.RIGHT_BOTTOM)
            ScaleType.LEFT_TOP_CROP -> getCropScale(PivotPoint.LEFT_TOP)
            ScaleType.LEFT_CENTER_CROP -> getCropScale(PivotPoint.LEFT_CENTER)
            ScaleType.LEFT_BOTTOM_CROP -> getCropScale(PivotPoint.LEFT_BOTTOM)
            ScaleType.CENTER_TOP_CROP -> getCropScale(PivotPoint.CENTER_TOP)
            ScaleType.CENTER_CROP -> getCropScale(PivotPoint.CENTER)
            ScaleType.CENTER_BOTTOM_CROP -> getCropScale(PivotPoint.CENTER_BOTTOM)
            ScaleType.RIGHT_TOP_CROP -> getCropScale(PivotPoint.RIGHT_TOP)
            ScaleType.RIGHT_CENTER_CROP -> getCropScale(PivotPoint.RIGHT_CENTER)
            ScaleType.RIGHT_BOTTOM_CROP -> getCropScale(PivotPoint.RIGHT_BOTTOM)
            ScaleType.START_INSIDE -> startInside()
            ScaleType.CENTER_INSIDE -> centerInside()
            ScaleType.END_INSIDE -> endInside()
        }
    }

    private fun getMatrix(f: Float, f2: Float, f3: Float, f4: Float): Matrix {
        val matrix = Matrix()
        matrix.setScale(f, f2, f3, f4)
        return matrix
    }

    private fun getMatrix(f: Float, f2: Float, pivotPoint: PivotPoint): Matrix {
        return when (pivotPoint) {
            PivotPoint.LEFT_TOP -> getMatrix(f, f2, 0.0f, 0.0f)
            PivotPoint.LEFT_CENTER -> getMatrix(f, f2, 0.0f, mViewSize.height / 2.0f)
            PivotPoint.LEFT_BOTTOM -> getMatrix(f, f2, 0.0f, mViewSize.height.toFloat())
            PivotPoint.CENTER_TOP -> getMatrix(f, f2, mViewSize.width / 2.0f, 0.0f)
            PivotPoint.CENTER -> getMatrix(f, f2, mViewSize.width / 2.0f, mViewSize.height / 2.0f)
            PivotPoint.CENTER_BOTTOM -> getMatrix(f, f2, mViewSize.width / 2.0f, mViewSize.height.toFloat())
            PivotPoint.RIGHT_TOP -> getMatrix(f, f2, mViewSize.width.toFloat(), 0.0f)
            PivotPoint.RIGHT_CENTER -> getMatrix(f, f2, mViewSize.width.toFloat(), mViewSize.height / 2.0f)
            PivotPoint.RIGHT_BOTTOM -> getMatrix(f, f2, mViewSize.width.toFloat(), mViewSize.height.toFloat())
        }
    }

    private fun getNoScale(): Matrix = getMatrix(
        mVideoSize.width.toFloat() / mViewSize.width,
        mVideoSize.height.toFloat() / mViewSize.height,
        PivotPoint.LEFT_TOP
    )

    private fun getFitScale(pivotPoint: PivotPoint): Matrix {
        val widthScale = mViewSize.width.toFloat() / mVideoSize.width
        val heightScale = mViewSize.height.toFloat() / mVideoSize.height
        val minScale = Math.min(widthScale, heightScale)
        return getMatrix(minScale / widthScale, minScale / heightScale, pivotPoint)
    }

    private fun fitXY(): Matrix = getMatrix(1.0f, 1.0f, PivotPoint.LEFT_TOP)
    private fun fitStart(): Matrix = getFitScale(PivotPoint.LEFT_TOP)
    private fun fitCenter(): Matrix = getFitScale(PivotPoint.CENTER)
    private fun fitEnd(): Matrix = getFitScale(PivotPoint.RIGHT_BOTTOM)

    private fun getOriginalScale(pivotPoint: PivotPoint): Matrix = getMatrix(
        mVideoSize.width.toFloat() / mViewSize.width,
        mVideoSize.height.toFloat() / mViewSize.height,
        pivotPoint
    )

    private fun getCropScale(pivotPoint: PivotPoint): Matrix {
        val widthScale = mViewSize.width.toFloat() / mVideoSize.width
        val heightScale = mViewSize.height.toFloat() / mVideoSize.height
        val maxScale = Math.max(widthScale, heightScale)
        return getMatrix(maxScale / widthScale, maxScale / heightScale, pivotPoint)
    }

    private fun startInside(): Matrix {
        return if (mVideoSize.height <= mViewSize.width && mVideoSize.height <= mViewSize.height) {
            getOriginalScale(PivotPoint.LEFT_TOP)
        } else fitStart()
    }

    private fun centerInside(): Matrix {
        return if (mVideoSize.height <= mViewSize.width && mVideoSize.height <= mViewSize.height) {
            getOriginalScale(PivotPoint.CENTER)
        } else fitCenter()
    }

    private fun endInside(): Matrix {
        return if (mVideoSize.height <= mViewSize.width && mVideoSize.height <= mViewSize.height) {
            getOriginalScale(PivotPoint.RIGHT_BOTTOM)
        } else fitEnd()
    }
}