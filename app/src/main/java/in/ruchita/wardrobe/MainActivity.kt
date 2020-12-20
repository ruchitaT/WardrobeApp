package `in`.ruchita.wardrobe

import `in`.ruchita.wardrobe.adapters.MyPagerAdapter
import `in`.ruchita.wardrobe.utils.Utils
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

import android.Manifest.permission.CAMERA

import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.net.Uri
import android.widget.Toast


class MainActivity : AppCompatActivity() {
    private var viewPagerTop: ViewPager? = null
    private var viewPagerBottom: ViewPager? = null
    private var mAdapter: MyPagerAdapter? = null
    private var mAdapter2: MyPagerAdapter? = null
    private var topWearImageUriList: ArrayList<String>? = null
    private var bottomWearImageUriList: ArrayList<String>? = null
    private val PICK_IMAGE = 100
    private val OPEN_CAMERA = 101
    private var flag = 0
    private var localDatabase: SharedPreferences? = null

    private var fabTop: FloatingActionButton? = null
    private  var fabBottom:FloatingActionButton? = null
    private  var fabShuffle:FloatingActionButton? = null
    private  var fab_fav_btn:FloatingActionButton? = null
    val PREFERENCE = "wardrobeAssignment"
    val TOP_WEAR_LIST = "topWearList"
    val BOTTOM_WEAR_LIST = "bottomWearList"
    val FAV_TOP_CLOTH = "favTopCloth"
    val FAV_BOTTOM_CLOTH = "favBottomCloth"
    private val PERMISSION_REQUEST_CODE = 200
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        localDatabase = getSharedPreferences(PREFERENCE, MODE_PRIVATE)
        viewPagerTop = findViewById<ViewPager?>(R.id.view_pager_top)
        viewPagerBottom = findViewById<ViewPager?>(R.id.view_pager_bottom)

        fabBottom = findViewById<FloatingActionButton>(R.id.fab_add_to_bottom)
        fabTop = findViewById<FloatingActionButton>(R.id.fab_add_to_top)
        fabShuffle = findViewById<FloatingActionButton>(R.id.fab_shuffle)
        fab_fav_btn = findViewById<FloatingActionButton>(R.id.fab_fav_btn)


        fabTop!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                showChooser(0)
            }
        })
        fabBottom!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                showChooser(1)
            }
        })

        fabShuffle!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                topWearImageUriList!!.shuffle()
                bottomWearImageUriList!!.shuffle()
                mAdapter!!.notifyDataSetChanged()
                mAdapter2!!.notifyDataSetChanged()
            }
        })

        fab_fav_btn!!.setOnClickListener {
            Toast.makeText(this@MainActivity,"Marked as favourite",Toast.LENGTH_LONG).show()
            markFav()
        }


        val tops: String = if(localDatabase!!.getString(TOP_WEAR_LIST, null)!=null){
            localDatabase!!.getString(TOP_WEAR_LIST, null)!!
        }else{
            ""
        }
        if (tops != null && tops != "") {
            val type: Type = object : TypeToken<ArrayList<String?>?>() {}.getType()
            topWearImageUriList = Gson().fromJson(tops, type)
        } else {
            topWearImageUriList = ArrayList()
        }
        val bottoms: String = if(localDatabase!!.getString(BOTTOM_WEAR_LIST, null)!=null){
            localDatabase!!.getString(BOTTOM_WEAR_LIST, null)!!
        }else{
            ""
        }

        if (bottoms != null && bottoms!="") {
            val type: Type = object : TypeToken<ArrayList<String?>?>() {}.getType()
            bottomWearImageUriList = Gson().fromJson(bottoms, type)
        } else {
            bottomWearImageUriList = ArrayList()
        }

        mAdapter = MyPagerAdapter(supportFragmentManager, topWearImageUriList)
        mAdapter2 = MyPagerAdapter(supportFragmentManager, bottomWearImageUriList)
        viewPagerTop!!.setAdapter(mAdapter)
        viewPagerBottom!!.setAdapter(mAdapter2)
        viewPagerTop!!.setCurrentItem(viewPagerTop!!.getChildCount() * 1000 / 2, false) // set curren

        viewPagerBottom!!.setCurrentItem(viewPagerBottom!!.getChildCount() * 1000 / 2, false) // set curren

    }

    fun showChooser(viewPagerPosition: Int){
        val view: View = LayoutInflater.from(this@MainActivity).inflate(R.layout.chooser_layout, null)
        val tvCamera: TextView
        val tvGallery: TextView
        tvCamera = view.findViewById(R.id.tv_camera)
        tvGallery = view.findViewById(R.id.tv_gallery)
        flag = viewPagerPosition

        val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
        alertDialog.setTitle("Select Image:")
        alertDialog.setView(view)
        val alertDialog1: AlertDialog = alertDialog.create()
        alertDialog1.show()

        tvCamera.setOnClickListener {
            openCamera()
            alertDialog1.dismiss()
        }
        tvGallery.setOnClickListener {
            openGallery()
            alertDialog1.dismiss()
        }
    }


    private fun openCamera() {
        if (checkPermission()) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, OPEN_CAMERA)
        } else {
            requestPermission()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra("return-data", true)
        intent.action = Intent.ACTION_GET_CONTENT
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                val uri: Uri? = data.data
                if (uri != null) {
                    updateDataAndNotify(uri)
                }
            }
        } else if (resultCode == RESULT_OK) {
            try {
                val photo = data!!.extras!!["data"] as Bitmap?

                val uri: Uri = Utils.bitmapToUriConverter(photo, this@MainActivity)!!
                updateDataAndNotify(uri)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateDataAndNotify(uri: Uri) {
        if (flag == 0) {
            topWearImageUriList!!.add(uri.toString())
            val editor = localDatabase!!.edit()
            val gson = Gson()
            editor.putString(TOP_WEAR_LIST, gson.toJson(topWearImageUriList))
            editor.apply()
            mAdapter!!.notifyDataSetChanged()
            viewPagerTop!!.currentItem = topWearImageUriList!!.size - 1
        } else {
            bottomWearImageUriList!!.add(uri.toString())
            val editor = localDatabase!!.edit()
            val gson = Gson()
            editor.putString(BOTTOM_WEAR_LIST, gson.toJson(bottomWearImageUriList))
            editor.apply()
            mAdapter2!!.notifyDataSetChanged()
            viewPagerBottom!!.currentItem = bottomWearImageUriList!!.size - 1
        }
    }

    fun markFav(){
        val editor = localDatabase!!.edit()
        val gson = Gson()
        editor.putString(FAV_TOP_CLOTH, gson.toJson(viewPagerTop!!.currentItem))
        editor.putString(FAV_BOTTOM_CLOTH, gson.toJson(viewPagerBottom!!.currentItem))
        editor.apply()
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, WRITE_EXTERNAL_STORAGE)
        val result1 = ContextCompat.checkSelfPermission(applicationContext, CAMERA)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE, CAMERA), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> if (grantResults.size > 0) {
                val externalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (externalStorage && cameraAccepted) {
                    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, OPEN_CAMERA)
                } else {
                    val alertDialog = AlertDialog.Builder(this@MainActivity)
                    alertDialog.setMessage("Permission Not Granted")
                    alertDialog.setNeutralButton("Ok") { dialogInterface, i -> }
                    val alertDialog1 = alertDialog.create()
                    alertDialog1.show()
                }
            }
        }
    }
}