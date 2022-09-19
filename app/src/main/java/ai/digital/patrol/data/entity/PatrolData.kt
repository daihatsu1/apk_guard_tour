package ai.digital.patrol.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class PatrolData(
    @Embedded val zone: Zone,
    @Relation(
        parentColumn = "id",
        entityColumn = "zone_id"
    )
    val checkpoint: List<Checkpoint>?
){

}
