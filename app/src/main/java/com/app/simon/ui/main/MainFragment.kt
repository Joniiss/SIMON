package com.app.simon.ui.main

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.simon.MyAdapter
import com.app.simon.R


class MainFragment : Fragment() {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MyAdapter
    private lateinit var mItems: MutableList<String>
    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)

        mRecyclerView = rootView.findViewById(R.id.rvLista)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)

        mItems = ArrayList()
        mItems.add("Item 1 AKNJSRNKJARJNKR")
        mItems.add("Item 2 pubg")

        mAdapter = MyAdapter(mItems)
        mRecyclerView.adapter = mAdapter

        return rootView
    }

}