package com.sym.accountbook.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.DatePicker
import android.widget.TimePicker
import java.util.Calendar
import java.util.Date

object DatePickerComponent {

    fun showDatePicker(
        context: Context,
        selectedDate: Date,
        onDateSelected: (Date) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        calendar.time = selectedDate

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val newCalendar = Calendar.getInstance()
                newCalendar.time = selectedDate
                newCalendar.set(Calendar.YEAR, selectedYear)
                newCalendar.set(Calendar.MONTH, selectedMonth)
                newCalendar.set(Calendar.DAY_OF_MONTH, selectedDay)
                onDateSelected(newCalendar.time)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    fun showTimePicker(
        context: Context,
        selectedDate: Date,
        onTimeSelected: (Date) -> Unit
    ) {
        val calendar = Calendar.getInstance()
        calendar.time = selectedDate

        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            context,
            { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                val newCalendar = Calendar.getInstance()
                newCalendar.time = selectedDate
                newCalendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                newCalendar.set(Calendar.MINUTE, selectedMinute)
                onTimeSelected(newCalendar.time)
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }
}
