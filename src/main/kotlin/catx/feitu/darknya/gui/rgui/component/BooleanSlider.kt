package catx.feitu.darknya.gui.rgui.component

import dev.luna5ama.trollhack.gui.IGuiScreen

open class BooleanSlider(
    screen: IGuiScreen,
    name: CharSequence,
    description: CharSequence,
    visibility: (() -> Boolean)? = null
) : Slider(screen, name, description, visibility)