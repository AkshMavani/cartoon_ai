//package com.skylock.ai_cartoon.fragment
//
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.skylock.ai_cartoon.R
//import com.skylock.ai_cartoon.activity.MainActivity
//import com.skylock.ai_cartoon.adapter.CartoonStyleAdapter
//import com.skylock.ai_cartoon.model.CartoonStyle
//
//class StyleListFragment : Fragment() {
//
//    private var styles: ArrayList<CartoonStyle>? = null
//    private var featureName: String = "cartoon"
//
//    companion object {
//        fun newInstance(styles: List<CartoonStyle>, feature: String): StyleListFragment {
//            val fragment = StyleListFragment()
//            val args = Bundle()
//            args.putParcelableArrayList("styles_list", ArrayList(styles))
//            args.putString("feature_name", feature)
//            fragment.arguments = args
//            return fragment
//        }
//    }
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val view = inflater.inflate(R.layout.activity_style_list_fragment, container, false)
//        styles = arguments?.getParcelableArrayList("styles_list")
//        featureName = arguments?.getString("feature_name") ?: "cartoon"
//
//        val recyclerView = view.findViewById<RecyclerView>(R.id.rv_styles)
//        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
//
//        val adapter = CartoonStyleAdapter(styles ?: emptyList()) { style ->
//            (activity as? MainActivity)?.goAIAvatar(style.styleKey, featureName, style.isPremium)
//        }
//        recyclerView.adapter = adapter
//        return view
//    }
//}