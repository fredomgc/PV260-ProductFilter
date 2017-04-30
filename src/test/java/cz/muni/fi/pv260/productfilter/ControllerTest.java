package cz.muni.fi.pv260.productfilter;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ControllerTest {

	private Product greenProduct;
	private Input productsInput;

	@Before
	public void setUp() throws ObtainFailedException {
		productsInput = mock(Input.class);
		when(productsInput.obtainProducts()).thenReturn(Arrays.asList(
				new Product(123, "red", Color.RED, BigDecimal.ONE),
				greenProduct = new Product(1536, "green", Color.GREEN, BigDecimal.ONE),
				new Product(456, "blue", Color.BLUE, BigDecimal.ONE)
		));
	}

	@Test
	public void testOnlyFilteredProductsToTheOutput() throws ObtainFailedException {
		Output out = mock(Output.class);
		Logger log = mock(Logger.class);
		Controller controller = new Controller(productsInput, out, log);

		controller.select(new ColorFilter(Color.GREEN));

		ArgumentCaptor<Collection> offerCaptor = ArgumentCaptor.forClass(Collection.class);
		verify(out).postSelectedProducts(offerCaptor.capture());
		assertThat(offerCaptor.getValue()).containsExactly(greenProduct);
	}

	@Test
	public void testLogMessageOnSuccess() throws ObtainFailedException {
		Output out = mock(Output.class);
		Logger log = mock(Logger.class);
		Controller controller = new Controller(productsInput, out, log);

		controller.select(new ColorFilter(Color.GREEN));

		verify(log).log("Controller", "Successfully selected 1 out of 3 available products.");
	}

	@Test
	public void testControllerLogsProductsObtainingException() throws ObtainFailedException {
		Input in = mock(Input.class);
		when(in.obtainProducts()).thenThrow(new ObtainFailedException());
		Output out = mock(Output.class);
		Logger log = mock(Logger.class);
		Controller controller = new Controller(in, out, log);

		controller.select(new ColorFilter(Color.GREEN));

		verify(log).log(Matchers.eq("Controller"), Matchers.startsWith("Filter procedure failed with exception:"));
	}

	@Test
	public void testNoOutputWhenObtainingException() throws ObtainFailedException {
		Input in = mock(Input.class);
		when(in.obtainProducts()).thenThrow(new ObtainFailedException());
		Output out = mock(Output.class);
		Logger log = mock(Logger.class);
		Controller controller = new Controller(in, out, log);

		controller.select(new ColorFilter(Color.GREEN));

		verify(out, never()).postSelectedProducts(Matchers.any());
	}

}
