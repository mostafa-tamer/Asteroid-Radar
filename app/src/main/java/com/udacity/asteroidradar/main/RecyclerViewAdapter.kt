package com.udacity.asteroidradar.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.databinding.ViewHolderBinding

class RecyclerViewAdapter :
    ListAdapter<Asteroid, RecyclerViewAdapter.ViewHolder>(DiffUtilCallBack()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ViewHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        lateinit var asteroid: Asteroid

        fun bind(property: Asteroid) {

            binding.asteroid = property

            asteroid = property

            itemClickLister()

            binding.executePendingBindings()
        }

        private fun itemClickLister() {

            binding.root.setOnClickListener {

                Navigation.findNavController(binding.root)
                    .navigate(MainFragmentDirections.actionMainFragmentToDetailFragment(asteroid))
            }
        }


        companion object {

            fun create(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)

                val binding = ViewHolderBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }

    }
}


class DiffUtilCallBack : DiffUtil.ItemCallback<Asteroid>() {
    override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
        return oldItem == newItem
    }
}