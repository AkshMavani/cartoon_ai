package com.skylock.ai_cartoon.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skylock.ai_cartoon.R
import com.skylock.ai_cartoon.activity.MainActivity
import com.skylock.ai_cartoon.adapter.CartoonStyleAdapter
import com.skylock.ai_cartoon.model.CartoonStyle

class CartoonStyleFragment : Fragment() {

    companion object {
        private const val ARG_STYLES = "styles"

        fun newInstance(styles: List<CartoonStyle>): CartoonStyleFragment {
            return CartoonStyleFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_STYLES, ArrayList(styles))
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_cartoon_style, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val styles = arguments?.getParcelableArrayList<CartoonStyle>(ARG_STYLES) ?: emptyList()

        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_cartoon_styles)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = CartoonStyleAdapter(styles) { style ->
            (activity as? MainActivity)?.goAIAvatar(style.styleKey, "cartoon", style.isGender)
        }
    }
}
