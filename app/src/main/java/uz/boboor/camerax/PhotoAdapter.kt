package uz.boboor.camerax

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.boboor.camerax.databinding.ItemPhotosBinding

class PhotoAdapter(private val list: List<Uri>) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {
    inner class PhotoViewHolder(private val binding: ItemPhotosBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.image.setImageURI(list[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(ItemPhotosBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(position)
    }
}