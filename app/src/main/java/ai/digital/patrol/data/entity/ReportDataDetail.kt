package ai.digital.patrol.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ReportDataDetail(
    @Embedded val report: Report,
    @Relation(
        parentColumn = "sync_token",
        entityColumn = "report"
    )
    val reportDetail: List<ReportDetail>?
){
    override fun toString(): String {
        return "ReportDataDetail(report=$report, reportDetail=$reportDetail)"
    }
}
