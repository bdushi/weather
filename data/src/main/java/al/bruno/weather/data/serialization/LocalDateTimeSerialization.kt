package al.bruno.weather.data.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.ZoneOffset

class LocalDateTimeSerialization : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val epochSeconds = value.atZone(ZoneOffset.UTC).toEpochSecond()
        encoder.encodeLong(epochSeconds)
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val epochSeconds = decoder.decodeLong()
        return LocalDateTime.ofEpochSecond(epochSeconds, 0, ZoneOffset.UTC)
    }

//    override val descriptor: SerialDescriptor =
//        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)
//
//    override fun serialize(encoder: Encoder, value: LocalDateTime) {
//        encoder.encodeString(value.format(formatter))
//    }
//
//    override fun deserialize(decoder: Decoder): LocalDateTime {
//        return LocalDateTime.parse(decoder.decodeString(), formatter)
//    }
}