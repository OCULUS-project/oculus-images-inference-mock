package pl.poznan.put.oculus.imagesinferencemock.model

data class SourceFactEvent (
        val head: String,
        val set: List<String>,
        val conjunction: Boolean,
        val grfIrf: GrfIrf,
        val job: String,
        val source: FactSource,
        val last: Boolean
)

data class GrfIrf(
        val grf: Double,
        val irf: Double
)

data class FactSource (
        val id: String,
        val type: String = "IMAGE"
)
