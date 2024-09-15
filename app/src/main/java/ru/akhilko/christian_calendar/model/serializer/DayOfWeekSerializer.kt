package ru.akhilko.christian_calendar.model.serializer

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = DayOfWeek::class)
object DayOfWeekSerializer : KSerializer<DayOfWeek> {
    private val DAY_OF_WEEK_LOCALE = Locale("en-US")
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(
            DayOfWeek::class.simpleName.toString(),
            PrimitiveKind.STRING
        )

    override fun deserialize(decoder: Decoder): DayOfWeek {
        return DayOfWeek.valueOf(decoder.decodeString().uppercase(DAY_OF_WEEK_LOCALE))
    }

    override fun serialize(encoder: Encoder, value: DayOfWeek) {
        encoder.encodeString(value.getDisplayName(TextStyle.FULL, DAY_OF_WEEK_LOCALE))
    }
}