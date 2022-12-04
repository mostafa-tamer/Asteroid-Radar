package com.udacity.asteroidradar.main

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.AsteroidPropertyDatabase
import com.udacity.asteroidradar.database.toDomain
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel

    private lateinit var binding: FragmentMainBinding

    private val adapter = RecyclerViewAdapter()

    private var currentList = listOf<Asteroid>()

    private var firstOpen: Boolean = true

    private var recyclerViewTitle = "Stored Asteroid"

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMainBinding.inflate(inflater)

        binding.title.text = recyclerViewTitle

        binding.lifecycleOwner = this

        binding.asteroidRecycler.adapter = adapter

        viewModelInitialization()

        retrieveFromCache()

        binding.viewModel = viewModel

        setHasOptionsMenu(true)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun retrieveFromCache() {
        if (firstOpen) {

            val newList = mutableListOf<AsteroidPropertyDatabase>()

            val current = LocalDate.now()


            viewModel.getDatabaseAsteroids().observe(viewLifecycleOwner) {

                if (currentList.isEmpty()) {
                    for (i in it) {

                        val localDate = LocalDate.parse(i.closeApproachDate)

                        if (localDate >= current) {
                            newList.add(i)
                        }
                    }

                    currentList = newList.toDomain()
                    adapter.submitList(newList.toDomain())
                }
            }


            firstOpen = false
        }
    }

    private fun viewModelInitialization() {

        val dataSource = AsteroidDatabase
            .getInstance(requireNotNull(activity).applicationContext)
            .dataSource

        val repository = Repository(dataSource)

        val factory = MainViewModel.ViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun loadWeekAsteroids() {

        adapter.submitList(listOf<Asteroid>())

        viewModel.data.observe(viewLifecycleOwner) {

            currentList = it
            adapter.submitList(currentList)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadTodayAsteroids() {

        adapter.submitList(listOf<Asteroid>())

        val current = LocalDate.now().toString()
        val newList: MutableList<Asteroid> = mutableListOf()

        viewModel.data.observe(viewLifecycleOwner) {

            GlobalScope.launch(Dispatchers.Default) {
                for (i in it) {

                    if (i.closeApproachDate == current) {
                        newList.add(i)
                    }
                }

                currentList = newList
                adapter.submitList(currentList)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadFromDatabase() {

        val newList = mutableListOf<AsteroidPropertyDatabase>()

        val current = LocalDate.now()

        viewModel.getDatabaseAsteroids().observe(viewLifecycleOwner) {

            for (i in it) {

                val localDate = LocalDate.parse(i.closeApproachDate)

                if (localDate >= current) {
                    newList.add(i)
                }
            }

            currentList = newList.toDomain()
            adapter.submitList(newList.toDomain())
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.show_all_menu -> {
                loadWeekAsteroids()
                recyclerViewTitle = "Week Asteroids"
                binding.title.text = recyclerViewTitle
            }
            R.id.show_rent_menu -> {
                loadTodayAsteroids()
                recyclerViewTitle = "Today Asteroids"
                binding.title.text = recyclerViewTitle
            }
            else -> {
                loadFromDatabase()
                recyclerViewTitle = "Stored Asteroids"
                binding.title.text = recyclerViewTitle
            }
        }

        return true
    }
}
