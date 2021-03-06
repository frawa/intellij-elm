package org.elm.lang.core.psi.elements

import com.intellij.lang.ASTNode
import org.elm.lang.core.psi.*


class ElmUnitExpr(node: ASTNode) : ElmPsiElementImpl(node), ElmAtomTag, ElmFunctionParamTag,
        ElmPatternChildTag, ElmUnionPatternChildTag
