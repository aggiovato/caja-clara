package com.cajaclara.app.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

/** Default categories. "Otros" is the fallback selected by the form. */
private val DEFAULT_CATEGORIES = listOf("Bebidas", "Alimentación", "Limpieza", "Papelería", "Otros")

/**
 * Seeds the default categories when the table is empty. Runs on every open and is idempotent
 * (the count check), so it also fills databases created before seeding existed.
 */
val SeedCallback = object : RoomDatabase.Callback() {
    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        val isEmpty = db.query("SELECT COUNT(*) FROM categories").use { cursor ->
            cursor.moveToFirst() && cursor.getInt(0) == 0
        }
        if (isEmpty) {
            DEFAULT_CATEGORIES.forEach { name ->
                db.execSQL(
                    "INSERT INTO categories (name, colorHex, createdAt) VALUES (?, NULL, 0)",
                    arrayOf<Any?>(name),
                )
            }
        }
    }
}
