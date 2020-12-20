package `in`.ruchita.wardrobe.adapters

import `in`.ruchita.wardrobe.fragments.TopFragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter


class MyPagerAdapter(
    supportFragmentManager: FragmentManager,
    var topWearImageUriList: ArrayList<String>?
) : FragmentStatePagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()
    var  LOOPS_COUNT = 1000
    private val ARG_PARAM1 = "param1"
    override fun getItem(position: Int): Fragment {
        if (topWearImageUriList != null && topWearImageUriList!!.size > 0)
        {
           var pos = position % topWearImageUriList!!.size;
             // use modulo for infinite cycling
            return newInstance(topWearImageUriList!!.get(pos).toString())!!
        }
        else
        {
            return newInstance(null)!!;
        }
    }

    override fun getCount(): Int {
        if (topWearImageUriList != null && topWearImageUriList!!.size > 0)
        {
             // simulate infinite by big number of products
            return topWearImageUriList!!.size*LOOPS_COUNT
        }
        else
        {
            return 1;
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    fun newInstance(param1: String?): Fragment? {
        val fragment:Fragment = TopFragment()
        val args = Bundle()
        args.putString(ARG_PARAM1, param1)
        fragment.arguments = args
        return fragment
    }
}