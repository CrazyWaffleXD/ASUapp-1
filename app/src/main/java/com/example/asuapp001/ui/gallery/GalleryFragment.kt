@file:Suppress("DEPRECATION")

package com.example.asuapp001.ui.gallery

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.asuapp001.R
import com.example.asuapp001.databinding.FragmentGalleryBinding

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!
    private lateinit var webView: WebView

    // Фиксированная ссылка
    private val scheduleUrl =
        "https://schedule.mstimetables.ru/publications/60ca7b5d-4f71-4f13-9685-07058590c6c0#/"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root = binding.root

        webView = binding.webView

        // Настройка WebView
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.useWideViewPort = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.setSupportZoom(true)
        webView.settings.builtInZoomControls = true
        webView.settings.displayZoomControls = false
        webView.webViewClient = WebViewClient() // Открывает ссылки внутри

        // Загружаем фиксированную страницу
        webView.loadUrl(scheduleUrl)

        return root
    }

    override fun onDestroyView() {
    // Останавливаем загрузку и очищаем WebView
    webView.stopLoading()
    webView.loadUrl("about:blank") // быстрая очистка
    _binding = null
    super.onDestroyView()
}
}