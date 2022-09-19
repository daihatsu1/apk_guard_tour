package ai.digital.patrol.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ReportDetailObject(
    @Embedded val reportDetail: ReportDetail,
    @Relation(
        parentColumn = "admisecsgp_mstobj_objek_id",
        entityColumn = "id"
    )
    val objectPatrol: ObjectPatrol){

    override fun toString(): String {
        return "ReportDetailObject(objectPatrol=$objectPatrol, reportDetail=$reportDetail)"
    }
}
