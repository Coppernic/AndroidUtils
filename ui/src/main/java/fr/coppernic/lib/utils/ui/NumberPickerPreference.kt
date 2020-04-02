package fr.coppernic.lib.utils.ui

import android.content.Context
import android.content.res.TypedArray
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import android.widget.NumberPicker
import androidx.preference.DialogPreference
import androidx.preference.PreferenceDialogFragmentCompat
import fr.coppernic.lib.utils.R
import timber.log.Timber

const val MAX_EXTERNAL_VALUE = "maxValue"
const val MIN_EXTERNAL_VALUE = "minValue"
const val DEFAULT_VALUE = 0

class NumberPickerPreference(context: Context, attrs: AttributeSet) : DialogPreference(context, attrs) {

    var min = 0
    var max = 0
    var value = 0
        set(number) {
            field = number
            persistInt(field)
        }

    init {
        setPositiveButtonText(R.string.ok)
        setNegativeButtonText(R.string.cancel)
        dialogIcon = null

        getStyledAttr(attrs)
    }

    private fun getStyledAttr(attrs: AttributeSet) {
        Timber.d("Nb attr ${attrs.attributeCount}")
        for (i in 0 until attrs.attributeCount) {
            Timber.d("Attribute name : ${attrs.getAttributeName(i)}")
            when (attrs.getAttributeName(i)) {
                MAX_EXTERNAL_VALUE -> max = attrs.getAttributeIntValue(i, DEFAULT_VALUE)
                MIN_EXTERNAL_VALUE -> min = attrs.getAttributeIntValue(i, DEFAULT_VALUE)
            }
        }
    }

    override fun onSetInitialValue(restorePersistedValue: Boolean,
                                   defaultValue: Any?) {
        if (restorePersistedValue) { // Restore existing state
            Timber.d("Restore persisted value")
            value = getPersistedInt(value)
        } else if (defaultValue != null) { // Set default state from the XML attribute
            Timber.d("Default value from xml")
            value = defaultValue as Int
        }
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any? {
        return a.getInt(index, DEFAULT_VALUE)
    }

    override fun getDialogLayoutResource(): Int {
        return R.layout.number_picker_dialog
    }

}

class NumberPickerPreferenceDialogFragment : PreferenceDialogFragmentCompat() {

    private lateinit var numberPicker: NumberPicker

    companion object {
        fun newInstance(key: String?): NumberPickerPreferenceDialogFragment {
            val fragment = NumberPickerPreferenceDialogFragment()
            val b = Bundle(1)
            b.putString(ARG_KEY, key)
            fragment.arguments = b
            return fragment
        }
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        Timber.d("onBindDialog")
        numberPicker = view.findViewById<View>(R.id.number_picker) as NumberPicker
        numberPicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS

        if (preference is NumberPickerPreference) {
            // Initialize state
            numberPicker.minValue = (preference as NumberPickerPreference).min
            numberPicker.maxValue = (preference as NumberPickerPreference).max
            numberPicker.value = (preference as NumberPickerPreference).value
        }

        numberPicker.wrapSelectorWheel = false
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult && preference is NumberPickerPreference) {
            // This allows the client to ignore the user value.
            (preference as NumberPickerPreference).callChangeListener(numberPicker.value)
            (preference as NumberPickerPreference).value = numberPicker.value
        }
    }
}
