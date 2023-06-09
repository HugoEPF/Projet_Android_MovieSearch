package com.epf.MovieSearch

data class PeopleObject (
    var results : Array<peopleJsonObject>
)

data class peopleJsonObject(
    var name : String,
    var biography : String,
    var profile_path : String,
    var popularity : String,
    var homepage : String,
    var known_for_department : String,
    var id : Int,
)

