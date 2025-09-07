package com.example.karunadaan.Main

import android.content.Context
import com.example.karunadaan.entity.DonateEntiy
import java.io.Serializable

class DonationsWrapper (val donation: DonateEntiy, @Transient val context: Context? = null) : Serializable
