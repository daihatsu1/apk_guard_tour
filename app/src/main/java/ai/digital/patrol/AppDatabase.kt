package ai.digital.patrol

import ai.digital.patrol.data.dao.*
import ai.digital.patrol.data.entity.*
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [
        User::class,
        Zone::class,
        Checkpoint::class,
        ObjectPatrol::class,
        Schedule::class,
        Event::class,
        Report::class,
        ReportDetail::class,
        Temuan::class,
        PatrolActivity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(ZoneTypeConverters::class)

abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao?
    abstract fun zoneDao(): ZoneDao?
    abstract fun checkpointDao(): CheckpointDao?
    abstract fun objectPatrolDao(): ObjectPatrolDao?
    abstract fun patrolDataDao(): PatrolDataDao?
    abstract fun scheduleDao(): ScheduleDao?
    abstract fun eventDao(): EventDao?
    abstract fun temuanDao(): TemuanDao?
    abstract fun patrolActivityDao(): PatrolActivityDao?

}