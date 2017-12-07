package org.elm.lang.core.psi

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.StubBasedPsiElement
import com.intellij.psi.stubs.IStubElementType
import com.intellij.psi.stubs.StubElement
import org.elm.lang.core.resolve.reference.ElmReference


/**
 * Base interface for all Elm Psi elements
 */
interface ElmPsiElement : PsiElement {
    /**
     * Get the file containing this element as an [ElmFile]
     */
    val elmFile: ElmFile
}

/**
 * Base class for normal Elm Psi elements
 */
abstract class ElmPsiElementImpl(node: ASTNode) : ASTWrapperPsiElement(node), ElmPsiElement {

    companion object {
        private val EMPTY_REFERENCE_ARRAY = emptyArray<ElmReference>()
    }

    override val elmFile: ElmFile
        get() = containingFile as ElmFile

    // Make the type-system happy by using our reference interface instead of PsiReference
    override fun getReferences(): Array<ElmReference> {
        val ref = getReference() as? ElmReference ?: return EMPTY_REFERENCE_ARRAY
        return arrayOf(ref)
    }
}

/**
 * Base class for Elm Psi elements which can be stubbed
 */
abstract class ElmStubbedElement<StubT : StubElement<*>>
    : StubBasedPsiElementBase<StubT>, StubBasedPsiElement<StubT>, ElmPsiElement {

    constructor(node: ASTNode)
            : super(node)

    constructor(stub: StubT, nodeType: IStubElementType<*, *>)
            : super(stub, nodeType)

    // TODO [kl] will this inadvertently cause the stub to become AST-backed?
    override val elmFile: ElmFile
        get() = containingFile as ElmFile

    // this is needed to match how [ASTWrapperPsiElement] implements `toString()`
    override fun toString(): String =
            "${javaClass.simpleName}($elementType)"
}
