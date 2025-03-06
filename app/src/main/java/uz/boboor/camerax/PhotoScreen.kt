package uz.boboor.camerax

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2

class PhotoScreen(private val list: List<Uri>) : Fragment(R.layout.screen_photo) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val pager = view.findViewById<ViewPager2>(R.id.page)
        pager.adapter = PhotoAdapter(list.reversed())
        pager.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        pager.offscreenPageLimit = 2

    }
}