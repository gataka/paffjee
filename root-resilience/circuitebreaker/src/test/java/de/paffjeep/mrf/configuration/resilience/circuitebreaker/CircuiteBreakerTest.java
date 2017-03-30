package de.paffjeep.mrf.configuration.resilience.circuitebreaker;

import de.paffjeep.mrf.configuration.resilience.circuitebreaker.impl.CircuiteBreaker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import javax.interceptor.InvocationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CircuiteBreakerTest {

	@Mock
	private InvocationContext ctx;

	@InjectMocks
	private CircuiteBreaker cb;

	@Before
	public void prepare() {
		System.setProperty("com.bmw.log.maxHistory", "10");
		// ConfigurationManager.install(new MapConfiguration(map));
		// HystrixPlugins.getInstance().registerConcurrencyStrategy(new
		// ConcurrencyStrategy());
		// HystrixPlugins.getInstance().registerEventNotifier(new
		// EventNotifier());
		// HystrixPlugins.getInstance().registerMetricsPublisher(HystrixServoMetricsPublisher.getInstance());
	}

	@After
	public void destroy() {
		Mockito.reset(ctx);

	}

	@Test
	public void test_ok_command() throws Throwable {

		TestCommand_OK commandUnderTest = new TestCommand_OK();
		when(ctx.getMethod()).thenReturn(TestCommand_OK.class.getMethod("command", String.class));
		when(ctx.getTarget()).thenReturn(commandUnderTest);

		String param1 = "ok_test";
		when(ctx.proceed()).then(new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				// TODO Auto-generated method stub
				return commandUnderTest.command(param1);
			}
		});


		Object result = cb.observeWithCB(ctx);

		assertEquals("Command not executed", param1, result);

	}

	@Test
	public void test_fail_command() throws Exception {

		TestCommand_faild commandUnderTest = new TestCommand_faild();
		when(ctx.getMethod()).thenReturn(TestCommand_faild.class.getMethod("command", String.class));
		when(ctx.getTarget()).thenReturn(commandUnderTest);

		String param1 = "faild_test";
		when(ctx.proceed()).then(new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				// TODO Auto-generated method stub
				return commandUnderTest.command(param1);
			}
		});

		Object result = null;
		try {
			result = cb.observeWithCB(ctx);
		} catch (Throwable e) {
			e.printStackTrace();
			assertEquals("Command should not executed", param1, e.getMessage());
		}
		assertNull("No result should be there", result);

	}

	@Test
	public void test_fail_command_fallback_ok() throws Throwable {

		TestCommand_faild_fallback_ok commandUnderTest = new TestCommand_faild_fallback_ok();
		when(ctx.getMethod()).thenReturn(TestCommand_faild_fallback_ok.class.getMethod("command", String.class));
		when(ctx.getTarget()).thenReturn(commandUnderTest);

		String param1 = "faild_test_with_fallback_ok";
		when(ctx.getParameters()).thenReturn(new Object[] { param1 });
		when(ctx.proceed()).thenThrow(new Exception(param1));

		Object result = cb.observeWithCB(ctx);
		assertEquals("Fallback of the command not executed", "the fallback is ok", result);

	}

	@Test
	public void test_fail_command_fallback_fail() throws Throwable {

		TestCommand_faild_fallback_fail commandUnderTest = new TestCommand_faild_fallback_fail();
		when(ctx.getMethod()).thenReturn(TestCommand_faild_fallback_fail.class.getMethod("command", String.class));
		when(ctx.getTarget()).thenReturn(commandUnderTest);

		String param1 = "faild_test_with_fallback_faild";
		when(ctx.getParameters()).thenReturn(new Object[] { param1 });
		when(ctx.proceed()).thenThrow(new Exception(param1));

		Object result = null;
		try {
			result = cb.observeWithCB(ctx);
		} catch (Throwable e) {
			e.printStackTrace();
			assertEquals("Fallback of the command not executed", "the fallback is ok", result);
		}
		assertNull("No result should be there", result);

	}

	@Test
	public void test_fail_command_replay_ok() throws Throwable {

		TestCommand_faild_replay_ok commandUnderTest = new TestCommand_faild_replay_ok();
		when(ctx.getMethod()).thenReturn(commandUnderTest.getClass().getMethod("command", String.class));
		when(ctx.getTarget()).thenReturn(commandUnderTest);

		String param1 = "faild_test";
		when(ctx.proceed()).then(new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				return commandUnderTest.command(param1);
			}
		});

        Object result = cb.observeWithCB(ctx);
		assertEquals("replayed command executed", "replay:1", result);

	}

    public class TestCommand_OK {
        @CircuiteBreakerCommand(commandGroup = "test", commandName = "ok_command")
        public String command(String param1) {
            return param1;
        }
    }

    public class TestCommand_faild {
        @CircuiteBreakerCommand(commandGroup = "test", commandName = "faild_command")
        public String command(String param1) throws Exception {

            throw new Exception(param1);
        }
    }

    public class TestCommand_faild_fallback_ok {
        @CircuiteBreakerCommand(commandGroup = "test", commandName = "faild_command_fallback_ok")
        public String command(String param1) throws Exception {

            throw new Exception(param1);
        }

        @CircuiteBreakerFallback
        public String fallback(String param1, CircuiteBreaker.CBContext ctx) throws Exception {
            System.out.println(ctx.getCommandException());
            assertEquals("The param should be the same.", param1, ctx.getCommandException().getMessage());

            return "the fallback is ok";
        }
    }

    public class TestCommand_faild_fallback_fail {
        @CircuiteBreakerCommand(commandGroup = "test", commandName = "faild_command_fallback_fail")
        public String command(String param1) throws Exception {

            throw new Exception(param1);
        }

        @CircuiteBreakerFallback
        public String fallback(String param1, CircuiteBreaker.CBContext ctx) throws Exception {
            System.out.println(ctx.getCommandException());
            assertEquals("The param should be the same.", param1, ctx.getCommandException().getMessage());

            throw new Exception("the fallback is faild");
        }
    }

	public class TestCommand_faild_replay_ok {
		private int count = 0;

		@CircuiteBreakerCommand(commandGroup = "test",
								commandName = "faild_command_replay_ok",
								replayRule = @CircuiteBreakerReplayRule(value = 1))
		public String command(String param1) throws Exception {
			if (count == 0) {
				count++;
				throw new Exception(param1);
			} else {
				return "replay:" + count;
			}
		}
	}
}
