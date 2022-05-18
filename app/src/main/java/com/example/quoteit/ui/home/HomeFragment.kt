package com.example.quoteit.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.quoteit.R
import com.example.quoteit.databinding.FragmentHomeBinding
import com.example.quoteit.ui.QuoteItApp
import com.example.quoteit.ui.utils.AdapterCallback
import com.example.quoteit.ui.utils.BottomSheet
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val model: HomeViewModel by activityViewModels {
        val repos = (requireActivity().application) as QuoteItApp
        HomeViewModelFactory(repos.foldersRepository, repos.foldersQuoteRepository, repos.quotesRepository)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val toolbar = binding.homeToolbar
        toolbar.title = resources.getString(R.string.home_label)
        toolbar.inflateMenu(R.menu.home_menu)
        toolbar.setOnMenuItemClickListener { onItemSelected(it) }
        val adapter = FolderAdapter(context, callback = object : AdapterCallback{
            override fun onItemSelected(id: Long) {
                val bundle = bundleOf("folder" to id)
                val action = R.id.action_homeFragment_to_quotesListFragment
                findNavController().navigate(action, bundle)
            }
        })

        // Bindings
        binding.createFolderButton.setOnClickListener { showDialog() }
        binding.folderRecycler.adapter = adapter
        binding.folderRecycler.setHasFixedSize(false)

        viewLifecycleOwner.lifecycleScope.launch {
            model.getFolders().collect {
                adapter.setData(it)
            }
        }
    }

    private fun onItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.home_settings -> {
            Log.d("Navigation", "Settings fragment...")
            val action = R.id.action_homeFragment_to_settingsFragment
            findNavController().navigate(action)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDialog(){
        val bottomSheet = BottomSheet(resources.getString(R.string.dialog_folder_new))
        bottomSheet.setOnInputConfirmListener { model.insertFolder(it) }
        bottomSheet.show(parentFragmentManager, "new_folder_bottom_sheet")
    }

}

