package com.talhaatif.tickojet.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.talhaatif.tickojet.databinding.PaymentBottomSheetBinding

class PaymentBottomSheet(
    private val onWalletPay: () -> Unit,
    private val onStripePay: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: PaymentBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = PaymentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnWallet.setOnClickListener {
            dismiss()
            onWalletPay()
        }

        binding.btnStripe.setOnClickListener {
            dismiss()
            onStripePay()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }
}
