package com.intermediate.storyapp1.ui.stories

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.intermediate.storyapp1.R
import com.intermediate.storyapp1.data.adapter.ListStoriesAdapter
import com.intermediate.storyapp1.data.constant.Constant
import com.intermediate.storyapp1.data.model.Story
import com.intermediate.storyapp1.databinding.FragmentStoriesBinding
import com.intermediate.storyapp1.ui.customView.CustomAlertDialog
import com.intermediate.storyapp1.ui.detail.createStory.CreateStoryActivity
import com.intermediate.storyapp1.ui.detail.detailStory.DetailStoryActivity

class StoriesFragment : Fragment() {

    private var _binding: FragmentStoriesBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: StoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoriesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        homeViewModel.listStories.observe(viewLifecycleOwner) {
            val isEmptyUser = it.isEmpty()
            if (isEmptyUser) {
                handlingEmptyUser(isEmptyUser)
            } else {
                showListStories(root.context, it)
            }
        }

        homeViewModel.isLoading.observe(viewLifecycleOwner) {
            shodLoading(it)
        }

        homeViewModel.isError.observe(viewLifecycleOwner) {
            errorHandler(root.context, it)
        }

        createStoryButtonHandler()

        return root
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.getStories()
    }

    private fun shodLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.storiesRv.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.storiesRv.visibility = View.VISIBLE
        }
    }

    private fun errorHandler(context: Context, isError: Boolean) {
        if (isError) {
            CustomAlertDialog(context, R.string.error_message, R.drawable.tryagain).show()
        }
    }

    private fun showListStories(context: Context, stories: List<Story>) {
        val storiesRv = binding.storiesRv

        if (context.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            storiesRv.layoutManager = GridLayoutManager(context, 2)
        } else {
            storiesRv.layoutManager = LinearLayoutManager(context)
        }

        val listStoriesAdapter = ListStoriesAdapter(stories)
        storiesRv.adapter = listStoriesAdapter

        listStoriesAdapter.setOnItemClickCallback(object : ListStoriesAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Story) {
                navigateDetailStory(data)
            }
        })
    }

    private fun navigateDetailStory(story: Story) {
        val intent = Intent(binding.root.context, DetailStoryActivity::class.java)
        intent.putExtra(Constant.DETAIL_STORY, story)
        startActivity(
            intent,
            ActivityOptionsCompat.makeSceneTransitionAnimation(binding.root.context as Activity).toBundle()
        )
    }

    private fun handlingEmptyUser(isEmptyUser: Boolean) {
        if (isEmptyUser) {
            binding.emptyStories.emptyStoriesConstraintLayout.visibility = View.VISIBLE
        } else {
            binding.emptyStories.emptyStoriesConstraintLayout.visibility - View.GONE
        }
    }

    private fun createStoryButtonHandler() {
        binding.createStoryButton.setOnClickListener {
            val intent = Intent(binding.root.context, CreateStoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}