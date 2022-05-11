package com.example.quoteit.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.quoteit.R
import com.example.quoteit.databinding.FragmentQuotesListBinding
import com.example.quoteit.ui.EditImageActivity
import com.example.quoteit.ui.QuoteItApp
import com.example.quoteit.ui.utils.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class QuotesListFragment : Fragment(){

    companion object{
        const val FOLDER = "folder"
    }

    private enum class FolderType{
        FAVORITES, PERSONAL, REGULAR
    }

    private var _binding: FragmentQuotesListBinding? = null
    private val binding get() = _binding!!

    private var folderId: Long = 0L
    private lateinit var folderType: FolderType
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
        folderType = when(folderId){
            1L -> FolderType.FAVORITES
            2L -> FolderType.PERSONAL
            else -> FolderType.REGULAR
        }
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
        val toolbar = binding.quotesListToolbar

        if(folderType == FolderType.REGULAR) toolbar.inflateMenu(R.menu.folder_content_menu)
        placeholderView = if(folderType == FolderType.FAVORITES) binding.emptyFavsView else binding.emptyView

        val adapter = QuoteAdapter(object: AdapterCallback{
            override fun onItemSelected(id: Long) { /* TODO: Possible share screen? */ }
            override fun onDetailsClicked(view: View, id: Long) {
                showPopUp(view, R.menu.quote_detail_menu, id)
            }
            override fun onFavoriteClicked(id: Long, b: Boolean) {
                val msg = if(b) resources.getString(R.string.added_to_favorites)
                else resources.getString(R.string.removed_from_favorites)
                model.updateFavQuote(id, b)
                mToast?.cancel()
                mToast = Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT)
                mToast?.show()
            }
        })

        // Bindings
        toolbar.setOnMenuItemClickListener { optionItemSelected(it) }
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.quotesRecycler.adapter = adapter
        binding.quotesRecycler.setHasFixedSize(false)

        when(folderType){
            FolderType.REGULAR -> {
                model.getFolderQuotes(folderId).observe(viewLifecycleOwner) {
                    placeholderView.visibility = if (it.quotes.isEmpty()) View.VISIBLE else View.GONE
                    toolbar.title = it.parentFolder
                    adapter.setData(it.quotes)
                }
            }
            FolderType.FAVORITES -> {
                viewLifecycleOwner.lifecycleScope.launch {
                    model.getFavQuotes().collect {
                        placeholderView.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                        toolbar.title = resources.getString(R.string.fav_folder)
                        adapter.setData(it)
                    }
                }
            }
            FolderType.PERSONAL -> {
                viewLifecycleOwner.lifecycleScope.launch {
                    model.getQuotes().collect {
                        placeholderView.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
                        toolbar.title = resources.getString(R.string.user_folder)
                        adapter.setData(it)
                    }
                }
            }
        }
    }

    private fun showPopUp(view: View, menu: Int, itemId: Long){
        PopupMenu(requireContext(), view).apply {
            setOnMenuItemClickListener { popUpMenuItem(it, itemId) }
            inflate(menu)
            show()
        }
    }

    private fun popUpMenuItem(item: MenuItem, quoteId: Long): Boolean {
        return when(item.itemId){
            R.id.delete_quote -> {
                if(folderType == FolderType.PERSONAL) {
                    showAlert(resources.getString(R.string.confirm_quote_delete)) {
                        mToast?.cancel()
                        mToast = Toast.makeText(requireContext(), resources.getString(R.string.removed_from_folder), Toast.LENGTH_SHORT)
                        mToast?.show()
                        model.deleteQuoteFromFolder(quoteId, folderId)
                    }
                }else{
                    model.deleteQuoteFromFolder(quoteId, folderId)
                }
                true
            }
            R.id.add_quote_to_folder -> {
                val bottom = BottomSheetList(resources.getString(R.string.add_quote_to_folder))
                val adapter = FolderAdapter(requireContext(), 1, object:AdapterCallback{
                    override fun onItemSelected(id: Long) {
                        mToast?.cancel()
                        mToast = Toast.makeText(requireContext(), resources.getString(R.string.added_to_folder), Toast.LENGTH_SHORT)
                        mToast?.show()
                        model.addQuoteToFolder(quoteId, id)
                        bottom.dismiss()
                    }
                })
                lifecycle.coroutineScope.launch {
                    model.getFolders().collect {
                        adapter.setData(it.drop(2))
                    }
                }
                bottom.setList(adapter)
                bottom.show(parentFragmentManager, "add_quote_to_folder_dialog")
                true
            }
            R.id.share_quote -> { /* TODO: navigate to share screen */ true }
            R.id.convert_quote -> {
                pickImage(quoteId)
                true
            }
            else -> false
        }
    }

    private fun optionItemSelected(item: MenuItem): Boolean {
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
                showAlert(resources.getString(R.string.confirm_folder_delete)) {
                    findNavController().popBackStack()
                    model.deleteFolder(folderId)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showBottomDialog(){
        val bottomSheet = BottomSheet(resources.getString(R.string.dialog_folder_modify))
        bottomSheet.setOnInputConfirmListener { newName -> model.modifyFolder(folderId, newName) }
        bottomSheet.show(parentFragmentManager, "new_folder_bottom_sheet")
    }

    private fun showAlert(message: String, callback: () -> Unit){
        val dialog = ConfirmDialog(message)
        dialog.setOnConfirmListener(callback)
        dialog.show(parentFragmentManager, "confirm_delete_dialog")
    }

    private fun pickImage(id: Long){
        val intent = Intent(requireActivity(), EditImageActivity::class.java)
        intent.putExtra("quote", id)
        startActivity(intent)
        activity?.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}