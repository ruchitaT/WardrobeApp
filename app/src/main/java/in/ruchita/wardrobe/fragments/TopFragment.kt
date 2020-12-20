package `in`.ruchita.wardrobe.fragments

import `in`.ruchita.wardrobe.R
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


class TopFragment : Fragment() {
    private val ARG_PARAM1 = "param1"
    var context = null

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    //private OnFragmentInteractionListener mListener;

    //private OnFragmentInteractionListener mListener;
//    fun TopFragment(): Fragment {
//        // Required empty public constructor
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (getArguments() != null) {
            mParam1 = getArguments()!!.getString(ARG_PARAM1);
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_top, container, false)
        val view: View = inflater.inflate(R.layout.fragment_top, container, false)
        val imageView: ImageView = view!!.findViewById(R.id.iv_item)

        val requestOptions = RequestOptions()
        requestOptions.diskCacheStrategy(DiskCacheStrategy.DATA)
        requestOptions.centerCrop()
        requestOptions.placeholder(R.mipmap.ic_launcher)
        requestOptions.override(450, 450)
        if (!TextUtils.isEmpty(mParam1)) {
            Glide.with(requireActivity())
                .load(Uri.parse(mParam1))
                .apply(requestOptions).into(imageView)
        } else {
            imageView.setImageResource(R.mipmap.ic_launcher)
        }
        return view
    }

}