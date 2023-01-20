package opekope2.optigui

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.minecraft.text.Text

fun registerDevMessage() {
    if ("alpha" !in modVersion && "beta" !in modVersion) return

    ClientPlayConnectionEvents.JOIN.register(ClientPlayConnectionEvents.Join { _, _, client ->
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
