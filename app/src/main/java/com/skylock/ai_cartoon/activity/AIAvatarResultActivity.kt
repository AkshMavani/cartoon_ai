package com.skylock.ai_cartoon.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.skylock.ai_cartoon.R
import com.skylock.ai_cartoon.adapter.AIAvatarResultAdapter
import com.skylock.ai_cartoon.databinding.ActivityAiAvatarResultBinding
import com.skylock.ai_cartoon.model.AIAvatarResultItem
import com.skylock.ai_cartoon.viewmodel.CartoonViewModel

/**
 * AIAvatarResultActivity
 *
 * Displays the AI-generated avatar result. Features:
 *  - Shows BEFORE / AFTER images side by side (with flip-back gesture on BEFORE)
 *  - Horizontal RecyclerView of all previously generated results for this style
 *  - "Generate" button → calls CartoonViewModel.retryFromScratch() to produce a new result
 *  - "Save" button → saves the currently-displayed result
 *  - Error handling with Toast messages
 *
 * Intent extras expected (all supplied by AIAvatarProcessingActivity):
 *   "image_before"   (String)  – local file path / URI of the original photo
 *   "image_after"    (String)  – URL of the first generated result (may be empty on first launch)
 *   "cartoonUrl"     (String)  – alias for image_after (kept for back-compat)
 *   "style"          (String)  – style key (e.g. "ghibli_runninghub_…")
 *   "gender"         (String)  – "male" / "female" / "other"
 *   "feature"        (String)  – feature tag, e.g. "aiavatarresult"
 *   "image_width"    (Int)
 *   "image_height"   (Int)
 *   "isfromCartton"  (Boolean) – true when arriving from cartoon processing flow
 */
class AIAvatarResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAiAvatarResultBinding
    private lateinit var viewModel: CartoonViewModel

    // ── Intent data ────────────────────────────────────────────────────────
    private var imageBefore: String = ""
    private var imageAfter: String = ""
    private var style: String = ""
    private var gender: String = "other"
    private var feature: String = "aiavatarresult"
    private var imageWidth: Int = 0
    private var imageHeight: Int = 0

    // ── Result list ────────────────────────────────────────────────────────
    private val resultItems = mutableListOf<AIAvatarResultItem>()
    private var resultAdapter: AIAvatarResultAdapter? = null
    private var resultCounter = 0   // increments each time a new result is added

    // ── State ──────────────────────────────────────────────────────────────
    private var lastClickTime = 0L
    private val clickThresholdMs = 1_000L

    private val timerHandler = Handler(Looper.getMainLooper())

    companion object {
        private const val TAG = "AIAvatarResultActivity"
    }

    // ──────────────────────────────────────────────────────────────────────
    // Lifecycle
    // ──────────────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAiAvatarResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this)[CartoonViewModel::class.java]

        readIntentExtras()
        setupViews()
        setupObservers()
        setupClickListeners()
        setupResultRecyclerView()
    }

    override fun finish() {
        viewModel.setActivityFinished()
        super.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        timerHandler.removeCallbacksAndMessages(null)
    }

    // ──────────────────────────────────────────────────────────────────────
    // Initialisation
    // ──────────────────────────────────────────────────────────────────────

    private fun readIntentExtras() {
        with(intent) {
            imageBefore = getStringExtra("image_before") ?: ""
            // Support both "image_after" and legacy "cartoonUrl" keys
            imageAfter = getStringExtra("image_after")
                ?: getStringExtra("cartoonUrl") ?: ""
            style = getStringExtra("style") ?: ""
            gender = getStringExtra("gender") ?: "other"
            feature = getStringExtra("feature") ?: "aiavatarresult"
            imageWidth = getIntExtra("image_width", 0)
            imageHeight = getIntExtra("image_height", 0)
        }
        Log.d(TAG, "readIntentExtras: before=$imageBefore after=$imageAfter style=$style")
    }

    private fun setupViews() {
        // Load the BEFORE image
        Glide.with(this)
            .load(imageBefore)
            .placeholder(R.drawable.ic_launcher_background)
            .into(binding.imgBefore)

        // Load the initial AFTER image (if provided)
        if (imageAfter.isNotEmpty()) {
            loadAfterImage(imageAfter)
            // Add it as the first result item
            addResultItem(imageAfter)
        }
    }

    private fun setupResultRecyclerView() {
        resultAdapter = AIAvatarResultAdapter(this, resultItems) { item, _ ->
            onResultItemClicked(item)
        }
        binding.rvResult.apply {
            adapter = resultAdapter
            layoutManager = LinearLayoutManager(
                this@AIAvatarResultActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // ViewModel observers
    // ──────────────────────────────────────────────────────────────────────

    private fun setupObservers() {
        // New image generated successfully
        viewModel.imageResponse.observe(this) { imageResponse ->
            if (imageResponse == null) return@observe
            val url = imageResponse.url ?: return@observe
            Log.d(TAG, "imageResponse received: $url")

            imageWidth = imageResponse.size?.width ?: imageWidth
            imageHeight = imageResponse.size?.height ?: imageHeight

            loadAfterImage(url)
            addResultItem(url)
        }

        // Error events
        viewModel.errorEvent.observe(this) { event ->
            if (event == null) return@observe
            Log.e(TAG, "errorEvent: ${event.name}")
            val message = when (event) {
                CartoonViewModel.ErrorEvent.NO_INTERNET -> getString(R.string.error_no_internet)
                CartoonViewModel.ErrorEvent.SERVER_TIMEOUT -> getString(R.string.error_timeout)
                CartoonViewModel.ErrorEvent.SERVER_ERROR -> getString(R.string.error_server)
                CartoonViewModel.ErrorEvent.MAX_POLL_EXCEEDED -> getString(R.string.error_timeout)
                CartoonViewModel.ErrorEvent.NO_RESPONSE -> getString(R.string.error_server)
            }
            showToast(message)
            showGenerateButton(true)   // re-enable so user can retry
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // Click listeners
    // ──────────────────────────────────────────────────────────────────────

    private fun setupClickListeners() {
        // ← Back
        binding.imgBack.setOnClickListener { finish() }

        // Generate new result
        binding.btnGenerate.setOnClickListener {
            if (!isClickAllowed()) return@setOnClickListener
            onGenerate()
        }

        // Save current result
        binding.btnSave.setOnClickListener {
            if (!isClickAllowed()) return@setOnClickListener
            onSave()
        }

        // Flip-back: hold to reveal BEFORE image
        binding.imgFlipback.setOnTouchListener { _, event ->
            handleFlipbackTouch(event)
        }
    }

    // ──────────────────────────────────────────────────────────────────────
    // Generate flow
    // ──────────────────────────────────────────────────────────────────────

    private fun onGenerate() {
        if (imageBefore.isEmpty()) {
            showToast("No source image available.")
            return
        }
        showGenerateButton(false)
        showLoading(true)

        // Re-use the ViewModel's retry mechanism which re-submits the last request
        viewModel.retryFromScratch()
    }

    // ──────────────────────────────────────────────────────────────────────
    // Save flow
    // ──────────────────────────────────────────────────────────────────────

    private fun onSave() {
        val currentUrl = getCurrentlySelectedUrl()
        if (currentUrl.isNullOrEmpty()) {
            showToast("Nothing to save yet.")
            return
        }
        // In your real app integrate your actual save logic here
        // (e.g. ViewAIToolSave, Glide download, MediaStore insert, etc.)
        showToast("save")
        Log.d(TAG, "onSave: saving $currentUrl")
    }

    // ──────────────────────────────────────────────────────────────────────
    // Result list helpers
    // ──────────────────────────────────────────────────────────────────────

    /**
     * Adds a new result item at the END of the list, de-selects all others,
     * and notifies the adapter.
     */
    private fun addResultItem(url: String) {
        resultCounter++
        // De-select any currently selected item
        resultItems.forEach { it.isSelected = false }

        val item = AIAvatarResultItem(
            "${getString(R.string.result)} $resultCounter",
            url,
            true   // newly added = selected
        )
        resultItems.add(item)
        resultAdapter?.notifyDataSetChanged()

        // Auto-scroll to the newest item
        if (resultItems.size > 1) {
            binding.rvResult.smoothScrollToPosition(resultItems.size - 1)
        }

        showLoading(false)
        showGenerateButton(true)
    }

    /** Called when the user taps a result thumbnail. */
    private fun onResultItemClicked(item: AIAvatarResultItem) {
        // De-select all, then select the tapped one
        resultItems.forEach { it.isSelected = false }
        item.isSelected = true
        resultAdapter?.notifyDataSetChanged()

        // Load the tapped result into the main preview
        loadAfterImage(item.getActiveUrl())
    }

    /** Returns the URL of the currently selected result, or null. */
    private fun getCurrentlySelectedUrl(): String? =
        resultItems.firstOrNull { it.isSelected }?.getActiveUrl()

    // ──────────────────────────────────────────────────────────────────────
    // Image loading
    // ──────────────────────────────────────────────────────────────────────

    private fun loadAfterImage(url: String) {
        imageAfter = url
        Glide.with(this)
            .load(url)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(binding.imgAfter)
    }

    // ──────────────────────────────────────────────────────────────────────
    // UI helpers
    // ──────────────────────────────────────────────────────────────────────

    private fun showLoading(show: Boolean) {
        // Show/hide a progress indicator if your layout has one
        // binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        Log.d(TAG, "showLoading: $show")
    }

    private fun showGenerateButton(enabled: Boolean) {
        binding.btnGenerate.isEnabled = enabled
        binding.btnGenerate.alpha = if (enabled) 1f else 0.5f
    }

    /**
     * Flip-back interaction: press & hold BEFORE → reveals the original photo.
     * Release → shows the generated AFTER image again.
     */
    private fun handleFlipbackTouch(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                binding.imgAfter.visibility = View.INVISIBLE
                binding.imgBefore.visibility = View.VISIBLE
                true
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                binding.imgAfter.visibility = View.VISIBLE
                true
            }

            else -> false
        }
    }

    /** Prevents accidental double-taps within [clickThresholdMs] ms. */
    private fun isClickAllowed(): Boolean {
        val now = System.currentTimeMillis()
        if (now - lastClickTime < clickThresholdMs) return false
        lastClickTime = now
        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}