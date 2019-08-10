package com.osmp4j.osm.features

object OSMBoundary : OSMFeature<OSMBoundary>("boundary") {
    val ABORIGINAL_LANDS = value("aboriginal_lands")
    val ADMINISTRATIVE = value("administrative")
    val HISTORIC = value("historic")
    val MARITIME = value("maritime")
    val MARKER = value("marker")
    val NATIONAL_PARK = value("national_park")
    val POLITICAL = value("political")
    val POSTAL_CODE = value("postal_code")
    val PROTECTED_AREA = value("protected_area")
    val USER_DEFINED = value("user defined")

    val ADMIN_LEVEL = attr("admin_level")
    val BORDER_TYPE = attr("border_type")
    val START_DATE = attr("start_date")
    val END_DATE = attr("end_date")
}