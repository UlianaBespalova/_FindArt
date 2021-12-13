package com.skvoznyak.findart

import android.app.PendingIntent.getActivity
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.skvoznyak.findart.databinding.LoadingScreenBinding
import android.view.LayoutInflater

import android.content.Context

import android.view.ViewGroup

import android.view.View


class LoadingFragment() : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val loadingBinding = LoadingScreenBinding.inflate(layoutInflater)
        return loadingBinding.root
    }
}

