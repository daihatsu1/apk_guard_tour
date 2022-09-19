package ai.digital.patrol.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CheckpointObject(
    @Embedded val checkpoint: Checkpoint,
    @Relation(
        parentColumn = "id",
        entityColumn = "checkpoint_id",
        entity = ObjectPatrol::class
    )
    val objectPatrol: List<ObjectPatrol>?
) {
    override fun toString(): String {
        return "CheckpointObject(checkpoint=$checkpoint, objectPatrol=$objectPatrol)"
    }
}
