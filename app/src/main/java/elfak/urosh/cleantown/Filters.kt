package elfak.urosh.cleantown

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.activityViewModels

class Filters : Fragment() {

    companion object {
        fun newInstance() = Filters()
    }

    private val viewModel: FiltersViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filters, container, false)
        val useRadius = view.findViewById<RadioButton>(R.id.radioButton)
        val radiusSize = view.findViewById<TextView>(R.id.radiusInput)
        val spinner = view.findViewById<Spinner>(R.id.spinner2)
        val applyFilters = view.findViewById<Button>(R.id.btnApplyFilters)
        val clearFilters = view.findViewById<Button>(R.id.btnClearFilters)
        radiusSize.text = viewModel.radiusSize.toString()
        clearFilters.setOnClickListener {
            viewModel.useFilters = false
            viewModel.useRadius = false
            useRadius.isChecked = false
        }
        applyFilters.setOnClickListener {
            when (spinner.selectedItem.toString()) {
                "Event" -> viewModel.setType(MarkerType.Event)
                else -> viewModel.setType(MarkerType.Other)
            }
            viewModel.useFilters = true
            if (useRadius.isChecked) {
                viewModel.useRadius = true
                viewModel.setRadius(Integer.parseInt(radiusSize.text.toString()))
            }
        }
        return view
    }

}