package com.yfy.basearchitecture.core.database.impl.migration

sealed class MigrationStrategy {
    /**
     * Manuel migration - custom migration script'leri ile
     */
    data class Manual(val migrations: List<DatabaseMigration>) : MigrationStrategy()

    /**
     * Destructive migration - tüm veriyi silip yeniden oluşturur
     */
    object Destructive : MigrationStrategy()

    /**
     * Fallback to destructive - manuel migration başarısız olursa destructive'e geçer
     */
    data class FallbackToDestructive(val migrations: List<DatabaseMigration>) : MigrationStrategy()
}