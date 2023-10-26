package org.d3if3038.answerme.ui.mypost

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.d3if3038.answerme.data.SettingDataStore
import org.d3if3038.answerme.data.dataStore
import org.d3if3038.answerme.databinding.FragmentMyPostBinding

class MyPostFragment : Fragment() {
    private lateinit var binding: FragmentMyPostBinding
    private lateinit var myPostAdapter: MyPostAdapter

    private val viewModel: MyPostViewModel by lazy {
        ViewModelProvider(this)[MyPostViewModel::class.java]
    }
    private val settingDataStore: SettingDataStore by lazy {
        SettingDataStore(requireContext().dataStore)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyPostBinding.inflate(inflater, container, false)
        myPostAdapter = MyPostAdapter()
        val username = settingDataStore.getString("username", "")

        viewModel.fetchMyPost(username)
        viewModel.getMyPost().observe(viewLifecycleOwner) {
            binding.emptyView.visibility = if (it.isEmpty()) View.VISIBLE else View.INVISIBLE
            binding.progressCircular.visibility = View.GONE

            myPostAdapter.submitList(it)
        }

        with(binding.myPostRecycleView) {
            adapter = myPostAdapter

            setHasFixedSize(true)
        }

        return binding.root
    }
}