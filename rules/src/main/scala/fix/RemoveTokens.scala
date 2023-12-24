package fix

import scalafix.v1._
import scala.meta._
import scala.annotation.tailrec

import scala.collection.mutable

class RemoveTokens(config: RemoveTokensConfiguration) extends SemanticRule("RemoveTokens") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    val changes = mutable.Set[Patch]()


    @tailrec
    def lookForOnly(exp: Term): Patch = {
      exp match {
        case term @ Term.Select(qual, Term.Name("only")) =>
          if (term.symbol.toString == "munit/TestOptions#only().")
            Patch.replaceTree(term, qual.toString).atomic
          else Patch.empty
        case Term.Select(qual, _) => lookForOnly(qual)
        case Term.Apply(Term.Select(qual, _), _) => lookForOnly(qual)
        case _ => Patch.empty
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
        changes.add(lookForOnly(arg))
      case Term.Apply.After_4_6_0(
            Term.Apply
              .After_4_6_0(Term.Name("test"), Term.ArgClause(List(arg), _)),
            Term.ArgClause(_, _)
          ) =>
        changes.add(lookForOnly(arg))
      case apply: Term.Apply =>
        println(s"\u001b[31m apply ${apply.structure}" + "\u001b[0m")
      case _ => ()
    }

    def traverseSeq(trees: Seq[Tree]): Unit = trees.foreach(traverse)

    traverse(doc.tree)

    changes.asPatch
  }

}
