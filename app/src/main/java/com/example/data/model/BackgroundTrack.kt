package com.example.data.model

data class BackgroundTrack(
    val id: Int,
    val title: String,
    val subtitle: String,
    val driveLink: String,
    val fileId: String
) {
    val streamUrl: String
        get() = "https://docs.google.com/uc?export=download&id=$fileId"

    companion object {
        val TRACKS = listOf(
            BackgroundTrack(
                id = 1,
                title = "Ambient Space Flow",
                subtitle = "Trek Latar Utama (Track 1)",
                driveLink = "https://drive.google.com/file/d/1UPVL_pHXrZ3jVT5MAErXmn6El6rWJj7x/view?usp=drive_link",
                fileId = "1UPVL_pHXrZ3jVT5MAErXmn6El6rWJj7x"
            ),
            BackgroundTrack(
                id = 2,
                title = "Cybernetic Pulse",
                subtitle = "Irama Tenang & Fokus (Track 2)",
                driveLink = "https://drive.google.com/file/d/1ercSFr987XBQr99pUmy296XAkiwpu-DH/view?usp=drive_link",
                fileId = "1ercSFr987XBQr99pUmy296XAkiwpu-DH"
            ),
            BackgroundTrack(
                id = 3,
                title = "Antigravity Orbit",
                subtitle = "Harmoni Tenang Futuristik (Track 3)",
                driveLink = "https://drive.google.com/file/d/1v2Ji2WrLl1v6MxpoNDvRQUkIklw_syUE/view?usp=drive_link",
                fileId = "1v2Ji2WrLl1v6MxpoNDvRQUkIklw_syUE"
            )
        )
    }
}
