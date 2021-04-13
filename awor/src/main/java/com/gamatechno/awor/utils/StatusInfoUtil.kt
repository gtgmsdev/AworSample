package com.gamatechno.awor.utils

import com.gamatechno.awor.R

object StatusInfoUtil {
    fun getIconStatusList(status: Int?): Int? {
        return when (status) {
            0 -> R.drawable.ic_save
            1 -> R.drawable.ic_submitted
            2 -> R.drawable.ic_rejected
            3 -> R.drawable.ic_approved
            4 -> R.drawable.ic_canceled
            else -> R.drawable.ic_default_null
        }
    }

    fun getIconStatusLabel(status: Int?): Int? {
        return when (status) {
            1 -> R.drawable.ic_approved_white
            2 -> R.drawable.ic_rejected_white
            3 -> R.drawable.ic_approved_white
            4 -> R.drawable.ic_canceled_white
            else -> R.drawable.ic_default_null
        }
    }

    fun getBackgroundStatusLabel(status: Int?): Int? {
        return when (status) {
            1 -> R.drawable.bg_rounded_left_blue
            2 -> R.drawable.bg_rounded_left_red
            3 -> R.drawable.bg_rounded_left_green
            4 -> R.drawable.bg_rounded_left_red
            else -> R.drawable.ic_default_null
        }
    }

    fun getStatusLabel(status: Int?): Int? {
        return when (status) {
            0 -> R.string.saved
            1 -> R.string.submitted
            2 -> R.string.rejected
            3 -> R.string.approved
            4 -> R.string.canceled
            else -> R.string.strip
        }
    }

    fun getIconStatusApprov(status: Int?): Int? {
        return when (status) {
            0 -> R.drawable.ic_submitted
            1 -> R.drawable.ic_on_progres
            2 -> R.drawable.ic_rejected
            3 -> R.drawable.ic_approved
            else -> R.drawable.ic_default_null
        }
    }

    fun getStatusApprov(status: Int?): Int? {
        return when (status) {
            0 -> R.string.submitted
            1 -> R.string.waiting
            2 -> R.string.rejected
            3 -> R.string.approved
            else -> R.string.strip
        }
    }

    fun getStatusDate(status: Int?): Int? {
        return when (status) {
            0 -> R.string.strip
            1 -> R.string.rejected
            2 -> R.string.approved
            else -> R.string.strip
        }
    }

    fun getIconStatusDate(status: Int?): Int? {
        return when (status) {
            1 -> R.drawable.ic_rejected
            2 -> R.drawable.ic_approved
            else -> R.drawable.ic_default_null
        }
    }
}