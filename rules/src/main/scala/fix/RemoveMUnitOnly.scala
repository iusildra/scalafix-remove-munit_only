package fix

import scalafix.v1._
import scala.meta._
import scala.annotation.tailrec

import scala.collection.mutable

class RemoveMUnitOnly extends SemanticRule("RemoveMUnitOnly") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    val selectToReplace = mutable.Set[Term.Select]()

    @tailrec
    def lookForOnly(exp: Term): Option[Term.Select] = {
      exp match {
        case term @ Term.Select(_, Term.Name("only")) => Some(term)
        case Term.Select(qual, _) => lookForOnly(qual)
        case Term.Apply(Term.Select(qual, _), _) => lookForOnly(qual)
        case _ => None
      }
    }

    @tailrec
    def traverse(tree: Tree): Unit = tree match {
      case Source(List(source)) => traverse(source)
      case Pkg(_, stats) => traverseSeq(stats)
      case Defn.Class(_, _, _, _, body) => traverse(body)
      case Defn.Object(_, _, body) => traverse(body)
      case Template(_, _, _, stats) => traverseSeq(stats)
      case Term.Apply(
            Term.Apply(Term.Name("test"), List(arg)),
            List(Term.Block(_))
          ) =>
        lookForOnly(arg).foreach(selectToReplace.add)
      case Term.Apply.After_4_6_0(
        Term.Apply.After_4_6_0(Term.Name("test"), Term.ArgClause(List(arg), _)),
        Term.ArgClause(_, _)
      ) =>
        lookForOnly(arg).foreach(selectToReplace.add)
      case apply: Term.Apply => println(s"\u001b[31m apply ${apply.structure}" + "\u001b[0m")
      case _ => ()
    }

    def traverseSeq(trees: Seq[Tree]): Unit = trees.foreach(traverse)

    traverse(doc.tree)

    selectToReplace.map { tree =>
      Patch.replaceTree(tree, tree.qual.toString).atomic
    }.asPatch
  }

}
