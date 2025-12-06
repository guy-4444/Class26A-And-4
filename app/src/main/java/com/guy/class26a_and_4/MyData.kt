package com.guy.class26a_and_4

class GamaData {
    var records: MutableList<Record> = mutableListOf()
}

class Record {
    var timestamp: Long = 0
        private set
    var score: Int = 0
        private set
    var lat: Double = 0.0
        private set
    var lon: Double = 0.0
        private set

    fun setTimestamp(timestamp: Long): Record {
        this.timestamp = timestamp
        return this
    }

    fun setScore(score: Int): Record {
        this.score = score
        return this
    }

    fun setLat(lat: Double): Record {
        this.lat = lat
        return this
    }

    fun setLon(lon: Double): Record {
        this.lon = lon
        return this
    }
}
