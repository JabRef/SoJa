package net.sf.jabref.bst;

import java.util.Stack;

import net.sf.jabref.bst.VM.BstEntry;
import net.sf.jabref.bst.VM.BstFunction;



/**
The |built_in| function {\.{text.prefix\$}} pops the top two literals
(the integer literal |pop_lit1| and a string literal, in that order).
It pushes the substring of the (at most) |pop_lit1| consecutive text
characters starting from the beginning of the string.  This function
is similar to {\.{substring\$}}, but this one considers an accented
character (or more precisely, a ``special character''$\!$, even if
it's missing its matching |right_brace|) to be a single text character
(rather than however many |ASCII_code| characters it actually
comprises), and this function doesn't consider braces to be text
characters; furthermore, this function appends any needed matching
|right_brace|s.  If any of the types is incorrect, it complains and
pushes the null string.
 * 
 * @author $Author: coezbek $
 * @version $Revision: 2209 $ ($Date: 2007-08-01 20:23:38 +0200 (Wed, 01 Aug 2007) $)
 * 
 */
public class TextPrefixFunction implements BstFunction {

	VM vm;

	public TextPrefixFunction(VM vm) {
		this.vm = vm;
	}

	public void execute(BstEntry context) {
		Stack<Object> stack = vm.getStack();

		if (stack.size() < 2) {
			throw new VMException("Not enough operands on stack for operation text.prefix$");
		}
		Object o1 = stack.pop();
		Object o2 = stack.pop();

		if (!(o1 instanceof Integer)) {
			vm.warn("An integer is needed as first parameter to text.prefix$");
			stack.push("");
			return;
		}
		if (!(o1 instanceof String)) {
			vm.warn("A string is needed as second parameter to text.prefix$");
			stack.push("");
			return;
		}
		
		stack.push(BibtexTextPrefix.textPrefix(((Integer) o1).intValue(), (String) o2, vm));
	}
}
