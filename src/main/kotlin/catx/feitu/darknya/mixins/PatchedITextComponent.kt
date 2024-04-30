package catx.feitu.darknya.mixins

import net.minecraft.util.text.ITextComponent

interface PatchedITextComponent {
    fun inplaceIterator(): Iterator<ITextComponent>
}