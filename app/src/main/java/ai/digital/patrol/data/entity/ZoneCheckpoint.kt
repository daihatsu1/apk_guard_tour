package ai.digital.patrol.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ZoneCheckpoint(
    @Embedded val zone: Zone,
    @Relation(
        parentColumn = "id",
        entityColumn = "zone_id",
        entity = Checkpoint::class
    )
    val checkpoints: List<Checkpoint>?
) {
    override fun toString(): String {
        return "ZoneCheckpoint(zone=$zone, checkpoint=$checkpoints)"
    }
}
