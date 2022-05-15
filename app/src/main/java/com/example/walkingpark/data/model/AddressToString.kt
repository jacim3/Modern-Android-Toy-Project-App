package com.example.walkingpark.data.model

import android.location.Address

const val ADDRESS_INDEX = 0

object AddressToString {

    operator fun invoke(addresses: List<Address>) =
        addresses.map {
            it.getAddressLine(ADDRESS_INDEX).toString().split(" ")
        }.flatten().distinct()
}