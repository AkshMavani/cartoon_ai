package com.skylock.ai_cartoon.remove_obj

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Pair
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.signature.ObjectKey
import com.skylock.ai_cartoon.R
import com.skylock.ai_cartoon.callback.ProcessingFinishListener
import com.skylock.ai_cartoon.databinding.ActivityRemoveObjBinding
import com.skylock.ai_cartoon.model.ImageResponse
import com.skylock.ai_cartoon.model.SizeImage
import com.skylock.ai_cartoon.remove_obj.doodlecanvaslibrary.OnTouchEventListener
import com.skylock.ai_cartoon.util.Constants.showToast
import com.skylock.ai_cartoon.util.Feature
import com.skylock.ai_cartoon.util.SharePreferenceRepositoryImpl
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.sqrt

class RemoveObjActivity : AppCompatActivity() {
    companion object {
        const val stable = 8

        fun safedk_RemoveObjActivity_startActivity_1cc3e32c89605540215f33ee203c7548(
            p0: RemoveObjActivity,
            p1: Intent?
        ) {
            if (p1 == null) {
                return
            }
            p0.startActivity(p1)
        }
    }

    private var bottomSheetChooseSave: BottomSheetSave? = null
    private var dialogDiscardResult: DialogDiscardResult? = null
    private var imageAfter: String? = null
    private var imageHeight: Int = 0
    private var imageHeightView: Int = 0
    private var imageResultUrl: String? = null
    private var imageUri: String? = null
    private var imageWidth: Int = 0
    private var imageWidthView: Int = 0
    private var isAuto: Boolean = false
    private var loadingRemoveObject: LoadingRemoveObject? = null
    private var newScreenHeight: Int = 0
    private var newScreenWidth: Int = 0
    private var resultMaskItemAdapter: ResultMaskItemAdapter? = null

    private var showAdRunnable: Runnable? = null
    private var sizeImage: SizeImage? = null
    private var timerRunnable: Runnable? = null
//    private var viewAIToolSave: ViewAIToolSave? = null

    private var maskUri: String = ""
    private val strokeWidth: Int = 40
    private val timerHandler: Handler = Handler(Looper.getMainLooper())
    private val showAdHandler: Handler = Handler(Looper.getMainLooper())

    private lateinit var viewModel: RemoveObjViewModel
    private lateinit var binding: ActivityRemoveObjBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRemoveObjBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[RemoveObjViewModel::class.java]

        initializeComponents()
        setupImageProcessing()
        setupObservers()
        setupClickListeners()
        setupCanvasAndSeekBar()
        initializeFeatures()
        loadRewardAd()
    }

    private fun initializeComponents() {
        //viewAIToolSave = ViewAIToolSave(this)
        loadingRemoveObject = LoadingRemoveObject(this)

        imageUri = intent.getStringExtra("image_uri")

        if (imageUri != null && imageUri!!.contains("http", ignoreCase = false)) {
            imageResultUrl = imageUri
        }

        if (imageUri == null) {
            showToast(this.getString(R.string.process_image_failed))
        } else {
            imageWidth = intent.getIntExtra("image_width", 0)
            imageHeight = intent.getIntExtra("image_height", 0)
        }
    }

    private fun setupImageProcessing() {
        loadingRemoveObject?.show()

        Glide.with(this as FragmentActivity)
            .asBitmap()
            .load(imageUri)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    loadingRemoveObject?.dismiss()
                    binding.imgOriginal.setImageBitmap(resource)
                    sizeImage = SizeImage(resource.width, resource.height)
                    imageWidth = resource.width
                    imageHeight = resource.height
                    calculateImageDimensions(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    loadingRemoveObject?.dismiss()
                }
            })
    }

    private fun calculateImageDimensions(bitmap: Bitmap) {
        val displayMetrics = Resources.getSystem().displayMetrics

        if (bitmap.width >= bitmap.height) {
            imageWidthView = displayMetrics.widthPixels
            imageHeightView = (displayMetrics.widthPixels * bitmap.height) / bitmap.width
            updateRootImage()
        } else {
            binding.imgPreview.post {
                val measuredHeight = binding.imgOriginal.measuredHeight
                imageHeightView = measuredHeight
                val width = (measuredHeight * bitmap.width) / bitmap.height
                imageWidthView = width

                if (width > displayMetrics.widthPixels) {
                    val screenWidth = displayMetrics.widthPixels
                    imageWidthView = screenWidth
                    imageHeightView = (screenWidth * bitmap.height) / bitmap.width
                }
                updateRootImage()
            }
        }
    }

    @SuppressLint("NewApi")
    private fun setupObservers() {
        viewModel.imageResponse.observe(this) { imageResponse: ImageResponse? ->
            if (imageResponse != null) {
                Feature.addCountFeature(Feature.REMOVEOBJ.value)
                handleImageResponse(imageResponse)
            }
        }

        viewModel.maskRemoveResponse.observe(this) { maskRemoveResponse: MaskRemoveResponse? ->
            maskRemoveResponse?.let {
                loadingRemoveObject?.dismiss()
                binding.tvAllRemoveSizeMask.text = getString(R.string.ai_remove_object)

                if (it.code == 99) {
                    val string = getString(R.string.error)
                    showToast(string)
                }

                // Fix: Convert nullable list to non-nullable
                val masks: List<MaskRemove> = it.masks!!
                resultMaskItemAdapter?.setResultItems(masks)
                updateMaskBorderUI()
            }
        }
    }

    private fun handleImageResponse(imageResponse: ImageResponse) {
        imageResultUrl = imageResponse.url

        runOnUiThread {
            loadingRemoveObject?.onDismissLoading(ProcessingFinishListener {
                // Empty callback
            })

            setListMaskObjectCache()
            resetAutoMask()
            removeMaskObjectAndBorder()
            removeAllViewsExceptCanvas()
            binding.btnSave.visibility = View.VISIBLE
            binding.ivDone.visibility = View.VISIBLE

            imageAfter = imageResponse.url
            //viewAIToolSave?.setImageUri(imageResponse.url)
            binding.canvasDraw.clearCanvas()

            val file = File(maskUri.replace("file://", "", ignoreCase = false))
            if (file.exists()) {
                file.delete()
            }
        }

        Glide.with(this as FragmentActivity)
            .asBitmap()
            .load(imageResponse.url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    binding.imgPreview.setImageBitmap(resource)
                    visibleImagePreview()
                    binding.imgFlipback.visibility = View.VISIBLE
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Do nothing
                }
            })
    }

    private fun setupClickListeners() {
        binding.imgUndo.setOnClickListener {
            binding.canvasDraw.undoMove()
        }

        binding.imgRedo.setOnClickListener {
            binding.canvasDraw.redoMove()
        }

        binding.imgRefresh.setOnClickListener {
            if (isAuto) {
                resetAutoMask()
            } else {
                binding.canvasDraw.clearCanvas()
            }
        }

        binding.ivDone.setOnClickListener {
            onDone()
        }

        binding.imgBack.setOnClickListener {
            showDialogDiscard()
        }

        binding.itemClose.setOnClickListener {
            showDialogDiscard()
        }

        binding.btnSave.setOnClickListener {
            onSave()
        }

        binding.imgFlipback.setOnTouchListener { view, motionEvent ->
            val action = motionEvent?.action
            when (action) {
                MotionEvent.ACTION_UP -> visibleImagePreview()
                MotionEvent.ACTION_DOWN -> goneImagePreview()
            }
            true
        }

        binding.itemDelete.setOnClickListener {
            onRemoveObj()
        }

        binding.itemRemoveAuto.setOnClickListener {
            isAuto = true
            updateActiveStyleRemove()

            if (resultMaskItemAdapter?.itemCount != 0) {
                return@setOnClickListener
            }

            loadingRemoveObject?.updateLoadingRemoveAuto(getString(R.string.lable_content_loading_auto))
            loadingRemoveObject?.show()

            imageUri?.let { uri ->
                viewModel.getMaskRemove(uri)
            }
        }

        binding.itemRemoveManual.setOnClickListener {
            isAuto = false
            updateActiveStyleRemove()
        }

        binding.itemMaskTotal.setOnClickListener {
            if (resultMaskItemAdapter?.isCheckTickAll() == true) {
                binding.imgRefresh.performClick()
            } else {
                resultMaskItemAdapter?.setAllSelectedMask()
                updateMaskUI()
            }
        }
    }

    private fun setupCanvasAndSeekBar() {
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Fix: Use setStrokeWidth method instead of property
                binding.canvasDraw.setStrokeWidth(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.seekBar.progress = strokeWidth
        binding.canvasDraw.setStrokeWidth(strokeWidth.toFloat())
        binding.canvasDraw.setStrokeColor(-1879113728)

        /*  binding.canvasDraw.setOnListener(object :
              doodlecanvaslibrary.OnTouchEventListener {
              override fun onTouchEvent(event: MotionEvent) {
                  binding.itemDelete.visibility = View.GONE
              }

              override fun onUpdateChange() {
                  updateIconDone()
              }
          })*/

        binding.canvasDraw.setOnListener(object : OnTouchEventListener {
            override fun onTouchEvent(event: MotionEvent) {
                binding.itemDelete.visibility = View.GONE
            }

            override fun onUpdateChange() {
                updateIconDone()
            }
        })


    }

    private fun initializeFeatures() {
        initViewFeatureExpand()
        updateActiveStyleRemove()
        binding.itemStyleRemove.visibility = View.VISIBLE
        binding.tvRemoveObject.visibility = View.GONE
        reloadListMaskResult()
        initTutorialManual()
    }

    private fun visibleImagePreview() {
        binding.imgPreview.visibility = View.VISIBLE
    }

    private fun goneImagePreview() {
        binding.imgPreview.visibility = View.GONE
    }

    private fun initTutorialAuto() {
        if (SharePreferenceRepositoryImpl.getSharedPreferences()
                .isBooleanKey("show_tutorial_auto")
        ) {
            binding.tutorial1.main.visibility = View.GONE
        } else {
            binding.tutorial1.main.visibility = View.VISIBLE
            binding.tutorial1.btnContinue.setOnClickListener {
                binding.tutorial1.main.visibility = View.GONE
                SharePreferenceRepositoryImpl.getSharedPreferences()
                    .setBooleanKey("show_tutorial_auto", true)
            }
        }
    }

    private fun initTutorialManual() {
        if (SharePreferenceRepositoryImpl.getSharedPreferences()
                .isBooleanKey("show_tutorial_manual")
        ) {
            binding.tutorial2.main.visibility = View.GONE
        } else {
            binding.tutorial2.main.visibility = View.VISIBLE
            binding.tutorial2.btnContinue.setOnClickListener {
                binding.tutorial2.main.visibility = View.GONE
                SharePreferenceRepositoryImpl.getSharedPreferences()
                    .setBooleanKey("show_tutorial_manual", true)
                initTutorialAuto()
            }
        }
    }

    private fun removeAllViewsExceptCanvas() {
        val rootMaskObject = binding.rootMaskObject
        var childCount = rootMaskObject.childCount - 1

        while (childCount >= 0) {
            if (rootMaskObject.getChildAt(childCount).id != R.id.canvasDraw) {
                rootMaskObject.removeViewAt(childCount)
            }
            childCount--
        }
    }

    private fun resetAutoMask() {
        removeAllViewsExceptCanvas()
        resultMaskItemAdapter?.unCheckTickAll()

        val isCheckTickAll = resultMaskItemAdapter?.isCheckTickAll() ?: false
        binding.itemTick.setImageResource(
            if (isCheckTickAll) R.drawable.ic_check_auto_remove
            else R.drawable.ic_check_auto_remove_un_select
        )
    }

    private fun updateRootImage() {
        val layoutParams = FrameLayout.LayoutParams(imageWidthView, imageHeightView)
        layoutParams.gravity = Gravity.CENTER
        binding.canvasDraw.layoutParams = layoutParams

        val originalParams = binding.imgOriginal.layoutParams
        originalParams.width = imageWidthView
        originalParams.height = imageHeightView

        val rootParams = binding.rootImage.layoutParams
        rootParams.width = imageWidthView
        rootParams.height = imageHeightView

        binding.rootImage.invalidate()
        binding.rootImage.requestLayout()

        newScreenWidth = imageWidthView
        newScreenHeight = imageHeightView
    }

    private fun convertDrawPathForNewScreen(points: List<Float>): Pair<List<Float>, Float> {
        val arrayList = ArrayList<Float>()
        val scaleX = newScreenWidth.toFloat() / imageWidth
        val scaleY = newScreenHeight.toFloat() / imageHeight

        var i = 0
        while (i < points.size) {
            arrayList.add(points[i] * scaleX)
            if (i + 1 < points.size) {
                arrayList.add(points[i + 1] * scaleY)
            }
            i += 2
        }

        return Pair(arrayList, sqrt(scaleX * scaleY))
    }

    private fun updateIconDone() {
        // Fix: Use isChange() method
        binding.itemDelete.visibility =
            if (binding.canvasDraw.isChange) View.VISIBLE else View.GONE
    }

    private fun showDialogDiscard() {
        if (dialogDiscardResult?.isShowing == true) {
            return
        }

        if (viewModel.imageResponse.value != null) {
            if (dialogDiscardResult == null) {
                dialogDiscardResult = DialogDiscardResult(this, false)
            }
            dialogDiscardResult?.show()
        } else {
            finish()
        }
    }

    private fun updateActiveStyleRemove() {
        binding.canvasDraw.clearCanvas()
        binding.canvasDraw.visibility = if (isAuto) View.GONE else View.VISIBLE
        binding.itemDelete.visibility = View.GONE

        val isEnabled = !isAuto
        binding.imgUndo.isEnabled = isEnabled
        binding.imgRedo.isEnabled = isEnabled

        val alpha = if (isAuto) 0.7f else 1.0f
        binding.imgUndo.alpha = alpha
        binding.imgRedo.alpha = alpha

        binding.itemToolManual.visibility = if (isAuto) View.GONE else View.VISIBLE
        binding.itemToolAuto.visibility = if (isAuto) View.VISIBLE else View.GONE
        binding.itemMaskBorder.visibility = if (isAuto) View.VISIBLE else View.GONE

        binding.itemRemoveAuto.setBackgroundResource(
            if (isAuto) R.drawable.bg_button_remove_obj_style_selected
            else R.drawable.bg_button_remove_obj_style
        )

        binding.itemRemoveManual.setBackgroundResource(
            if (!isAuto) R.drawable.bg_button_remove_obj_style_selected
            else R.drawable.bg_button_remove_obj_style
        )

        val colorAuto = if (isAuto) "#202020" else "#FFFFFF"
        val colorManual = if (isAuto) "#FFFFFF" else "#202020"

        binding.tvRemoveAuto.setTextColor(Color.parseColor(colorAuto))
        binding.tvRemoveManual.setTextColor(Color.parseColor(colorManual))
        binding.ivRemoveManual.setColorFilter(Color.parseColor(colorManual))

        if (!isAuto) {
            resetAutoMask()
        }
    }

    private fun initViewFeatureExpand() {
        val layoutBinding = binding.moreTool

        layoutBinding.cvEnhance.setOnClickListener {
            onFeatureExpandSelect("enhance")
        }

        layoutBinding.cvBrighten.setOnClickListener {
            onFeatureExpandSelect("brighten")
        }

        layoutBinding.cvCartoon.setOnClickListener {
            onFeatureExpandSelect("cartoon")
        }

        layoutBinding.cvColorize.setOnClickListener {
            onFeatureExpandSelect("colorize")
        }

        layoutBinding.cvDehaze.setOnClickListener {
            onFeatureExpandSelect("dehaze")
        }

        layoutBinding.cvDescratch.setOnClickListener {
            onFeatureExpandSelect("descratch")
        }

        layoutBinding.cvRemoveObj.setOnClickListener {
            onFeatureExpandSelect("removeobj")
        }

        layoutBinding.tvRemoveObj.setTextColor(getColor(R.color.selected_icon_expand))
        layoutBinding.imgRemoveObj.setColorFilter(getColor(R.color.selected_icon_expand))
    }

    private fun onFeatureExpandSelect(feature: String) {
        if (feature == "removeobj") {
            return
        }

        // Note: You'll need to implement Constants.startActivityFeature
        // Constants.startActivityFeature(this, feature, imageAfter, imageWidth, imageHeight, false)
    }

    private fun onSave() {
        /*if (premium != null) {
            Thread {
                viewAIToolSave?.save(null, "")
                checkSaveAndStartActivity()
            }.start()
        } else {
            onShowChooseSaveModal()
        }*/
    }

    override fun onResume() {
        super.onResume()

        /* premium = SharePreferenceRepositoryImpl.getSharedPreferences().getPremium()

         if (premium != null && bottomSheetChooseSave?.isShowing == true) {
             bottomSheetChooseSave?.dismiss()
         }*/
    }

    private fun onDone() {
        /*    val intent = Intent(this, ResultToolActivity::class.java)
            intent.putExtra("FEATURE", Feature.REMOVEOBJ.value)
            intent.putExtra("image_after", imageAfter)
            intent.putExtra("image_before", imageUri)
            intent.putExtra("image_width", imageWidth)
            intent.putExtra("image_height", imageHeight)

            safedk_RemoveObjActivity_startActivity_1cc3e32c89605540215f33ee203c7548(this, intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_nothing)
            finish()*/
    }

    fun bitmapConvertToFile(context: Context, bitmap: Bitmap, format: String): File {
        val timeStamp = System.currentTimeMillis()
        val fileName = "mask_${timeStamp}.$format"
        val outputDir = context.cacheDir // or use external storage if needed
        val outputFile = File(outputDir, fileName)

        try {
            val outputStream = FileOutputStream(outputFile)
            bitmap.compress(
                if (format.equals("png", ignoreCase = true)) Bitmap.CompressFormat.PNG
                else Bitmap.CompressFormat.JPEG,
                100,
                outputStream
            )
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return outputFile
    }

    private fun onRemoveObj() {
        // Note: You'll need to implement Feature.INSTANCE.isLimitWithFeature
        // if (Feature.INSTANCE.isLimitWithFeature(Feature.REMOVEOBJ.value)) {
        //     ExtensionKt.goToPremium(this, "limit_action")
        //     return
        // }

        if (imageWidth <= 0 || imageHeight <= 0) {
            return
        }

        val bitmap = Bitmap.createBitmap(imageWidthView, imageHeightView, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val frameLayout = binding.rootMaskObject
        frameLayout.layout(frameLayout.left, frameLayout.top, frameLayout.right, frameLayout.bottom)
        frameLayout.draw(canvas)

        // ✅ UNCOMMENT AND FIX THIS LINE
        maskUri = "file://" + bitmapConvertToFile(this, bitmap, "png").absolutePath

        viewModel.setAuto(isAuto)
        Log.e("RemoveObj", "isAuto: $isAuto")
        Log.e("RemoveObj", "maskUri: $maskUri")

        imageUri?.let { uri ->
            Log.e("RemoveObj", "uri: $uri")
            Log.e("RemoveObj", "imgresulturi: $imageResultUrl")
            Log.e("RemoveObj", "getListMasks: ${resultMaskItemAdapter?.getListMasks()}")

            viewModel.onRemoveObj(
                uri,
                imageResultUrl,
                maskUri, // ✅ Now this will have the actual file URI
                resultMaskItemAdapter?.getListMasks() ?: ""
            )
        }

        if (loadingRemoveObject?.isShowing != true) {
            loadingRemoveObject?.showProcessing()
        }
    }

    private fun setListMaskObjectCache() {
        resultMaskItemAdapter?.setListDisableMaskObject()
    }

    private fun onShowChooseSaveModal() {
        if (bottomSheetChooseSave?.isShowing == true) {
            return
        }

        /*   val bottomSheet = BottomSheetSave(this) {
               // showRewardAd() - You'll need to implement this
           }
           bottomSheetChooseSave = bottomSheet
           bottomSheet.show()*/
    }

    private fun checkSaveAndStartActivity() {
        timerRunnable = Runnable {
            // Note: You'll need to implement ResultActivity.saveUri
            // if (ResultActivity.saveUri != null) {
            //     loadingRemoveObject?.dismiss()
            //     timerHandler.removeCallbacks(timerRunnable!!)
            //
            //     val intent = Intent(this, SavedActivity::class.java)
            //     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            //     intent.putExtra("image_uri", ResultActivity.saveUri)
            //
            //     safedk_RemoveObjActivity_startActivity_1cc3e32c89605540215f33ee203c7548(this, intent)
            //     overridePendingTransition(R.anim.slide_in_right, R.anim.slide_nothing)
            //     finish()
            // } else {
            loadingRemoveObject?.show()
            timerHandler.postDelayed(timerRunnable!!, 500L)
            // }
        }

        timerHandler.postDelayed(timerRunnable!!, 0L)
    }

    private fun loadRewardAd() {
        // Implement your ad loading logic here
    }

    override fun onDestroy() {
        showAdRunnable?.let { showAdHandler.removeCallbacks(it) }
        timerRunnable?.let { timerHandler.removeCallbacks(it) }
        super.onDestroy()
    }

    private fun reloadListMaskResult() {
        resultMaskItemAdapter = ResultMaskItemAdapter(this)
        binding.rvResult.adapter = resultMaskItemAdapter

        resultMaskItemAdapter?.setOnItemClickListener { position ->
            if (position < 0) return@setOnItemClickListener
            val maskRemove = resultMaskItemAdapter?.resultItems?.getOrNull(position)
            if (maskRemove == null || maskRemove.isDisable) return@setOnItemClickListener

            resultMaskItemAdapter?.setItemIndexSelected(position)
            updateMaskUI()
        }
    }

    private fun updateMaskUI() {
        val resultItems = resultMaskItemAdapter?.resultItems ?: return

        for (maskRemove in resultItems) {
            if (maskRemove.isDisable) continue

            val imageView = binding.rootMaskObject.findViewById<ImageView>(maskRemove.id)

            if (imageView != null) {
                imageView.isSelected = maskRemove.selected
            }

            if (imageView == null && maskRemove.selected) {
                addMaskImageView(maskRemove)
            } else if (!maskRemove.selected && imageView != null) {
                binding.rootMaskObject.removeView(imageView)
            }
        }

        binding.itemDelete.visibility = if (resultItems.isNotEmpty()) View.VISIBLE else View.GONE

        val isCheckTickAll = resultMaskItemAdapter?.isCheckTickAll() ?: false
        binding.itemTick.setImageResource(
            if (isCheckTickAll) R.drawable.ic_check_auto_remove
            else R.drawable.ic_check_auto_remove_un_select
        )
    }

    private fun addMaskImageView(data: MaskRemove) {
        val imageView = ImageView(this)
        imageView.id = data.id

        val boxes = data.boxes
        val convertedPoints = convertDrawPathForNewScreen(boxes!!).first

        val y = convertedPoints[1]
        val x = convertedPoints[0]
        val x2 = convertedPoints[2]
        val y2 = convertedPoints[3]

        val layoutParams = FrameLayout.LayoutParams(
            (x2 - x).toInt(),
            (y2 - y).toInt()
        )

        imageView.x = x
        imageView.y = y
        binding.rootMaskObject.addView(imageView, layoutParams)

        val requestOptions = RequestOptions()
            .signature(ObjectKey(System.currentTimeMillis()))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)

        Glide.with(this as FragmentActivity)
            .asBitmap()
            .load(data.maskUrl)
            .apply(requestOptions)
            .into(imageView)
    }

    private fun updateMaskBorderUI() {
        val resultItems = resultMaskItemAdapter?.resultItems ?: return

        for (maskRemove in resultItems) {
            maskRemove.id = View.generateViewId()

            val imageView = ImageView(this)
            imageView.id = maskRemove.id
            imageView.setImageResource(R.drawable.bg_mask_object)

            imageView.setOnClickListener {
                resultMaskItemAdapter?.setResultItemSelected(imageView.id)
                updateMaskUI()
            }

            val boxes = maskRemove.boxes
            val convertedPoints = convertDrawPathForNewScreen(boxes!!).first

            if (convertedPoints.size >= 4) {
                val y = convertedPoints[1]
                val x = convertedPoints[0]
                val x2 = convertedPoints[2]
                val y2 = convertedPoints[3]

                val layoutParams = FrameLayout.LayoutParams(
                    (x2 - x).toInt(),
                    (y2 - y).toInt()
                )

                imageView.x = x
                imageView.y = y
                binding.itemMaskBorder.addView(imageView, layoutParams)
            }
        }
    }

    private fun removeMaskObjectAndBorder() {
        val resultItems = resultMaskItemAdapter?.resultItems ?: return

        for (maskRemove in resultItems) {
            val imageView = binding.itemMaskBorder.findViewById<ImageView>(maskRemove.id)
            if (imageView != null && maskRemove.isDisable) {
                binding.itemMaskBorder.removeView(imageView)
            }
        }
    }
}