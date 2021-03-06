package panda.lang.escape;

import java.io.IOException;

import panda.lang.Arrays;

/**
 * Executes a sequence of translators one after the other. Execution ends whenever 
 * the first translator consumes codepoints from the input.
 */
public class AggregateTranslator extends CharSequenceTranslator {

	private final CharSequenceTranslator[] translators;

	/**
	 * Specify the translators to be used at creation time.
	 * 
	 * @param translators CharSequenceTranslator array to aggregate
	 */
	public AggregateTranslator(final CharSequenceTranslator... translators) {
		this.translators = Arrays.clone(translators);
	}

	/**
	 * The first translator to consume codepoints from the input is the 'winner'. Execution stops
	 * with the number of consumed codepoints being returned. {@inheritDoc}
	 */
	@Override
	public int translateChar(final CharSequence input, final int index, final Appendable out) throws IOException {
		for (final CharSequenceTranslator translator : translators) {
			final int consumed = translator.translateChar(input, index, out);
			if (consumed != 0) {
				return consumed;
			}
		}
		return 0;
	}

}
