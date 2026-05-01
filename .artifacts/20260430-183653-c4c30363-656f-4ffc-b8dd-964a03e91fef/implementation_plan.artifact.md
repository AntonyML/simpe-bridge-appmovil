# Sync SINPE Classification Data

The app identifies SINPE messages, but the classification results (classification, confidence, and details) are not currently saved in the local database or synced to Supabase. This plan will add the necessary fields to the local database and remote synchronization.

## Proposed Changes

### Local Data Layer

#### [MessageEntity.kt](file:///C:/DEV/simpe-bridge-appmovil/app/src/main/java/com/simpe/bridge/appmovil/data/local/MessageEntity.kt)

- Add `classification`, `detectionConfidence`, and `detectionDetails` fields.

```kotlin
    // New fields for classification
    val classification: String,
    val detectionConfidence: Float,
    val detectionDetails: String
```

#### [MessageMappers.kt](file:///C:/DEV/simpe-bridge-appmovil/app/src/main/java/com/simpe/bridge/appmovil/data/local/MessageMappers.kt)

- Update `toDomain()` and `toEntity()` to include the new classification fields.

#### [Converters.kt](file:///C:/DEV/simpe-bridge-appmovil/app/src/main/java/com/simpe/bridge/appmovil/data/local/Converters.kt)

- Ensure `MessageStatus` and `SmsClassification` enums can be handled by Room if needed (or store as Strings).

---

### Remote Data Layer

#### [SupabaseMessageService.kt](file:///C:/DEV/simpe-bridge-appmovil/app/src/main/java/com/simpe/bridge/appmovil/data/remote/SupabaseMessageService.kt)

- Update `MessageRecord` and `toMessageRecord()` to include the new fields for Supabase synchronization.

```kotlin
@Serializable
data class MessageRecord(
    // ... existing fields ...
    @SerialName("classification")        val classification: String,
    @SerialName("detection_confidence")  val detectionConfidence: Float,
    @SerialName("detection_details")     val detectionDetails: String
)
```

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to verify compilation and Room schema generation.
- (Optional) Add a unit test for `MessageMappers` to verify data mapping.

### Manual Verification
- Deploy to device and receive a SINPE SMS.
- Check logs to confirm data is correctly saved with classification.
- Verify Supabase receives the new fields if the remote table is updated.
