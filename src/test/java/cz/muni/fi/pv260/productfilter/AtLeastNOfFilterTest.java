package cz.muni.fi.pv260.productfilter;

import static com.googlecode.catchexception.CatchException.caughtException;
import static com.googlecode.catchexception.CatchException.verifyException;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class AtLeastNOfFilterTest {

	@Test
	public void testConstructorThrowsIllegalArgumentException() throws Exception {
		verifyException(() -> new AtLeastNOfFilter<>(-1));
		assertThat((Exception) caughtException()).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testConstructorThrowsFilterNeverSucceedsException() throws Exception {
		verifyException(() -> new AtLeastNOfFilter<>(1));
		assertThat((Exception) caughtException()).isInstanceOf(FilterNeverSucceeds.class);
	}

	@Test
	public void testFilterPasses() throws Exception {
		AtLeastNOfFilter<Object> filter = new AtLeastNOfFilter<>(2, new PassingFilter(), new PassingFilter(), new FailingFilter());

		assertThat(filter.passes(new Object())).isTrue();
	}

	@Test
	public void testFilterFails() throws Exception {
		AtLeastNOfFilter<Object> filter = new AtLeastNOfFilter<>(3, new PassingFilter(), new PassingFilter(), new FailingFilter());

		assertThat(filter.passes(new Object())).isFalse();
	}

	private class FailingFilter implements Filter<Object> {

		@Override
		public boolean passes(Object item) {
			return false;
		}

	}

	private class PassingFilter implements Filter<Object> {

		@Override
		public boolean passes(Object item) {
			return true;
		}

	}

}
