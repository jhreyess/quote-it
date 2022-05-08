package com.example.quoteit.ui.home

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.quoteit.R
import com.example.quoteit.databinding.FragmentQuotesListBinding
import com.example.quoteit.ui.QuoteItApp
import com.example.quoteit.ui.utils.*

class QuotesListFragment : Fragment(), PopupMenu.OnMenuItemClickListener{

    companion object{
        const val FOLDER = "folder"
    }

    private var _binding: FragmentQuotesListBinding? = null
    private val binding get() = _binding!!

    private var folderId: Long = 0L
    private var isfavFolder: Boolean = false
    private lateinit var placeholderView: TextView

    private var mToast: Toast? = null

    private val model: HomeViewModel by activityViewModels {
        val repos = (requireActivity().application) as QuoteItApp
        HomeViewModelFactory(repos.foldersRepository, repos.foldersQuoteRepository, repos.quotesRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the FOLDER ID from the Fragment arguments
        folderId = arguments?.getLong(FOLDER) ?: 0L
        isfavFolder = (folderId == 1L)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuotesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        placeholderView = if(isfavFolder) binding.emptyFavsView else binding.emptyView
        val adapter = QuoteAdapter(object: AdapterCallback{
            override fun onItemSelected(id: Long) { /* TODO: Possible share screen? */ }
            override fun onDetailsClicked(view: View, id: Long) {
                showPopUp(view, R.menu.quote_detail_menu)
            }
            override fun onFavoriteClicked(id: Long, b: Boolean) {
                val msg = if(b){ "Añadido a favoritos"
                }else{ "Eliminado de favoritos" }
                model.updateFavQuote(id, b)
                mToast?.cancel()
                mToast = Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT)
                mToast?.show()
            }
        })

        // Bindings
        binding.backButton.setOnClickListener {findNavController().popBackStack()}
        binding.quotesRecycler.adapter = adapter
        binding.quotesRecycler.setHasFixedSize(false)
        if(!isfavFolder){
            binding.folderDetailOptions.setOnClickListener { showPopUp(it, R.menu.folder_content_menu) }
            binding.folderDetailOptions.visibility = View.VISIBLE
        }

        model.getQuotes(folderId).observe(viewLifecycleOwner) {
            placeholderView.visibility = if (it.quotes.isEmpty()) View.VISIBLE else View.GONE
            binding.fragmentLabel.text = it.parentFolder
            adapter.setData(it.quotes)
        }
    }

    private fun showPopUp(view: View, menu: Int){
        PopupMenu(requireContext(), view).apply {
            setOnMenuItemClickListener(this@QuotesListFragment)
            inflate(menu)
            show()
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.add_to_folder -> {
                val action = R.id.action_quotesListFragment_to_newQuote
                val bundle = bundleOf("folder" to folderId)
                findNavController().navigate(action, bundle)
                true
            }
            R.id.modify_folder ->{
                showBottomDialog()
                true
            }
            R.id.delete_folder -> {
                showAlert()
                true
            }
            else -> false
        }
    }

    private fun showBottomDialog(){
        val bottomSheet = BottomSheet(resources.getString(R.string.dialog_folder_modify))
        bottomSheet.onActionCompleteListener(object : DialogCallback {
            override fun onConfirm(str: String?) { model.modifyFolder(folderId, str!!) }
        })
        bottomSheet.show(parentFragmentManager, "new_folder_bottom_sheet")
    }

    private fun showAlert(){
        val dialog = ConfirmDialog(resources.getString(R.string.confirm_delete))
        dialog.onActionCompleteListener(object : DialogCallback{
            override fun onConfirm(str: String?) {
                findNavController().popBackStack()
                model.deleteFolder(folderId)
            }
        })
        dialog.show(parentFragmentManager, "confirm_delete_dialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}