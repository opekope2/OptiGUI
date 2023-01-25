package opekope2.optigui

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.text.Text

fun setupDevMessage() {
    if ("alpha" !in modVersion) return

    var shown = false

    ClientPlayConnectionEvents.JOIN.register(ClientPlayConnectionEvents.Join { _, _, client ->
        if (shown) return@Join

        shown = true

        client.inGameHud.chatHud.addMessage(
            Text.Serializer.fromJson(
                """
                {
                    "extra": [
                        {
                            "underlined": true,
                            "clickEvent": {
                                "action": "open_url",
                                "value": "https://github.com/opekope2/OptiGUI-Next/issues"
                            },
                            "text": "GitHub"
                        },
                        {
                            "text": "."
                        }
                    ],
                    "text": "Thank you for trying out OptiGUI $modVersion development build! If you find a bug, please open an issue on "
                }
                """.trimIndent()
            )
        )
    })
}
