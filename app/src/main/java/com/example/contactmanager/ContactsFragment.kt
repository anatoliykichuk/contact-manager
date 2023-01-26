package com.example.contactmanager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.contactmanager.databinding.FragmentContactsBinding

const val REQUEST_CODE = 99

class ContactsFragment : Fragment() {

    private val viewModel: ContactsViewModel by lazy {
        ViewModelProvider(this).get(ContactsViewModel::class.java)
    }

    private var _binding: FragmentContactsBinding? = null
    private val binding
        get() = _binding!!

    companion object {
        @JvmStatic
        fun newInstance() =
            ContactsFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getLiveData().observe(
            viewLifecycleOwner,
            Observer { showContacts(it) }
        )

        checkPermission()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    viewModel.getContacts()
                } else {
                    showDialog()
                }
            }
        }
    }

    private fun checkPermission() {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(it, Manifest.permission.READ_CONTACTS) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    viewModel.getContacts()
                }

                shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                    showDialog()
                }

                else -> {
                    requestPermission()
                }
            }
        }
    }

    private fun requestPermission() {
        requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CODE)
    }

    private fun showDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Доступ к контактам")
            .setMessage("Для просмотра информации")
            .setPositiveButton("Предоставить доступ") { _, _ ->
                requestPermission()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showContacts(contacts: List<String>) {
        for (contactName in contacts) {
            context?.let { addView(it, contactName) }
        }
    }

    private fun addView(context: Context, textToShow: String) {
        binding.containerForContacts.addView(AppCompatTextView(context).apply {
            text = textToShow
            textSize = resources.getDimension(R.dimen.margin_15_dp)
        })
    }
}