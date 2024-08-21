import android.os.Parcel
import android.os.Parcelable

data class BulletinPost(
    val title: String = "",
    val content: String = "",
    val userNickname: String = "",
    val timestamp: String = "",
    val hasAttachment: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeString(userNickname)
        parcel.writeString(timestamp)
        parcel.writeByte(if (hasAttachment) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BulletinPost> {
        override fun createFromParcel(parcel: Parcel): BulletinPost {
            return BulletinPost(parcel)
        }

        override fun newArray(size: Int): Array<BulletinPost?> {
            return arrayOfNulls(size)
        }
    }
}