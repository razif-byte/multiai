package com.example.data.model

data class UnsplashPreset(
    val id: String,
    val name: String,
    val imageUrl: String,
    val description: String
) {
    companion object {
        val PRESETS = listOf(
            UnsplashPreset(
                id = "cyber_ai",
                name = "Cyberpunk AI",
                imageUrl = "https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?auto=format&fit=crop&w=1200&q=80",
                description = "Abstrak bercahaya neon futuristik biru & magenta."
            ),
            UnsplashPreset(
                id = "deep_cosmos",
                name = "Deep Cosmos",
                imageUrl = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?auto=format&fit=crop&w=1200&q=80",
                description = "Nebula kosmik galaksi berdefinisi tinggi."
            ),
            UnsplashPreset(
                id = "quantum_grid",
                name = "Quantum Matrix",
                imageUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?auto=format&fit=crop&w=1200&q=80",
                description = "Jejaring kod binari siber dan matriks hijau emerald."
            ),
            UnsplashPreset(
                id = "emerald_nature",
                name = "Zen Nature",
                imageUrl = "https://images.unsplash.com/photo-1518495973542-4542c06a5843?auto=format&fit=crop&w=1200&q=80",
                description = "Dedaunan segar berembun pagi dengan suasana tenang."
            ),
            UnsplashPreset(
                id = "synthwave",
                name = "Retro Synthwave",
                imageUrl = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?auto=format&fit=crop&w=1200&q=80",
                description = "Gradien senja ungu neon retro dan horizon digital."
            ),
            UnsplashPreset(
                id = "dark_minimal",
                name = "Dark Minimalist",
                imageUrl = "",
                description = "Latar belakang gelap klasik Material 3 tanpa gambar."
            )
        )
    }
}
