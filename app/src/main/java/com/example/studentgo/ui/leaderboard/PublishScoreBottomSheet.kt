import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.studentgo.databinding.BottomSheetPublishScoreBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PublishScoreBottomSheet : BottomSheetDialogFragment() {
    private var _binding: BottomSheetPublishScoreBinding? = null
    private val binding get() = _binding!!

    var onPublishClick: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPublishScoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.publishButton.setOnClickListener {
            onPublishClick?.invoke()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "PublishScoreBottomSheet"
    }
}