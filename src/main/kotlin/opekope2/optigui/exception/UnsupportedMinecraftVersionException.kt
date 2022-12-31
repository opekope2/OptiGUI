package opekope2.optigui.exception

class UnsupportedMinecraftVersionException(modVersion: String, gameVersion: String) :
    Exception("Minecraft $gameVersion is not supported by OptiGUI $modVersion.")
