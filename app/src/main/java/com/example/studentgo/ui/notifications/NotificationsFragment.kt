package com.example.studentgo.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.studentgo.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val logTag = "NOTIFICATIONS"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(logTag, "onCreate() triggered")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(logTag, "onCreateView() triggered")

        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(logTag, "onViewCreated() triggered")
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        Log.d(logTag, "onViewStateRestored() triggered")
    }

    override fun onStart() {
        super.onStart()

        Log.d(logTag, "onStart() triggered")
    }

    override fun onResume() {
        super.onResume()

        Log.d(logTag, "onResume() triggered")
    }

    override fun onPause() {
        super.onPause()

        Log.d(logTag, "onPause() triggered")
    }

    override fun onStop() {
        super.onStop()

        Log.d(logTag, "onStop() triggered")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        Log.d(logTag, "onSaveInstanceState() triggered")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        Log.d(logTag, "onDestroyView() triggered")
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(logTag, "onDestroy() triggered")
    }
}