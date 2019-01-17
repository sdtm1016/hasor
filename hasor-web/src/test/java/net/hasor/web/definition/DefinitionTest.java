package net.hasor.web.definition;
import net.hasor.core.AppContext;
import net.hasor.core.BindInfo;
import net.hasor.core.provider.InstanceProvider;
import net.hasor.web.Invoker;
import net.hasor.web.InvokerChain;
import net.hasor.web.InvokerFilter;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
//
public class DefinitionTest {
    @Test
    public void baseicDefineTest() throws Throwable {
        //
        BindInfo<? extends Filter> bindInfo = PowerMockito.mock(BindInfo.class);
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getInstance(bindInfo)).thenReturn(new TestCallerFilter());
        //
        //
        Map<String, String> initParams = new HashMap<String, String>();
        initParams.put("arg1s", "1");
        initParams.put("args2", "2");
        UriPatternMatcher uriPatternMatcher = UriPatternType.get(UriPatternType.SERVLET, "/servlet");
        FilterDefinition filterDefine = new FilterDefinition(123, "abc", uriPatternMatcher, bindInfo, initParams);
        //
        filterDefine.toString();
        assert "abc".equals(filterDefine.getPattern());
        assert 123 == filterDefine.getIndex();
        assert filterDefine.getUriPatternType() == uriPatternMatcher.getPatternType();
        //
        TestCallerFilter.resetCalls();
        filterDefine.init(new InvokerMapConfig(initParams, appContext));
        //
        Invoker mock = PowerMockito.mock(Invoker.class);
        PowerMockito.when(mock.getRequestPath()).thenReturn("/servlet");
        assert filterDefine.matchesInvoker(mock);
        PowerMockito.when(mock.getRequestPath()).thenReturn("/servlet22");
        assert !filterDefine.matchesInvoker(mock);
        //
        new FilterDefinition(123, "abc", uriPatternMatcher, bindInfo, null);
    }
    //
    @Test
    public void filterDefineTest1() throws Throwable {
        //
        BindInfo<? extends Filter> bindInfo = PowerMockito.mock(BindInfo.class);
        FilterDefinition filterDefine = new FilterDefinition(123, "abc",//
                UriPatternType.get(UriPatternType.SERVLET, "/servlet"),//
                bindInfo, new HashMap<String, String>());
        //
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getInstance(bindInfo)).thenReturn(new TestCallerFilter());
        //
        ServletContext servletContext = PowerMockito.mock(ServletContext.class);
        PowerMockito.when(appContext.getInstance(ServletContext.class)).thenReturn(servletContext);
        //
        //
        TestCallerFilter.resetCalls();
        filterDefine.destroy();
        assert !TestCallerFilter.isDestroyCall(); // 没有init 不会调用 destroy
        filterDefine.init(new InvokerMapConfig(null, appContext));
        filterDefine.init(new InvokerMapConfig(null, appContext));
        //
        assert TestCallerFilter.isInitCall();
    }
    //
    @Test
    public void filterDefineTest2() throws Throwable {
        //
        BindInfo<? extends Filter> bindInfo = PowerMockito.mock(BindInfo.class);
        FilterDefinition filterDefine = new FilterDefinition(123, "abc",//
                UriPatternType.get(UriPatternType.SERVLET, "/servlet"),//
                bindInfo, new HashMap<String, String>());
        //
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getInstance(bindInfo)).thenReturn(new TestDoNextCallerFilter());
        //
        ServletContext servletContext = PowerMockito.mock(ServletContext.class);
        PowerMockito.when(appContext.getInstance(ServletContext.class)).thenReturn(servletContext);
        Invoker mockInvoker = PowerMockito.mock(Invoker.class);
        //
        //
        TestCallerFilter.resetCalls();
        filterDefine.init(new InvokerMapConfig(null, InstanceProvider.wrap(appContext)));
        //
        assert TestCallerFilter.isInitCall();
        filterDefine.doInvoke(mockInvoker, new InvokerChain() {
            @Override
            public void doNext(Invoker invoker) throws Throwable {
                //
            }
        });
        assert TestCallerFilter.isDoCall();
        //
        filterDefine.destroy();
        assert TestCallerFilter.isDestroyCall();
    }
    //
    @Test
    public void filterDefineTest3() throws Throwable {
        //
        BindInfo<? extends Filter> bindInfo = PowerMockito.mock(BindInfo.class);
        FilterDefinition filterDefine = new FilterDefinition(123, "abc",//
                UriPatternType.get(UriPatternType.SERVLET, "/servlet"),//
                bindInfo, new HashMap<String, String>());
        //
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getInstance(bindInfo)).thenReturn(new TestDoNextCallerFilter());
        //
        ServletContext servletContext = PowerMockito.mock(ServletContext.class);
        PowerMockito.when(appContext.getInstance(ServletContext.class)).thenReturn(servletContext);
        Invoker mockInvoker = PowerMockito.mock(Invoker.class);
        //
        filterDefine.init(new InvokerMapConfig(null, InstanceProvider.wrap(appContext)));
        //
        try {
            filterDefine.doInvoke(mockInvoker, new InvokerChain() {
                @Override
                public void doNext(Invoker invoker) throws Throwable {
                    throw new IOException("TEST_ERROR");
                }
            });
            assert false;
        } catch (IOException e) {
            assert "TEST_ERROR".equals(e.getMessage());
        } catch (Throwable throwable) {
            assert false;
        }
        //
        try {
            filterDefine.doInvoke(mockInvoker, new InvokerChain() {
                @Override
                public void doNext(Invoker invoker) throws Throwable {
                    throw new ServletException("TEST_ERROR");
                }
            });
            assert false;
        } catch (ServletException e) {
            assert "TEST_ERROR".equals(e.getMessage());
        } catch (Throwable throwable) {
            assert false;
        }
        //
        try {
            filterDefine.doInvoke(mockInvoker, new InvokerChain() {
                @Override
                public void doNext(Invoker invoker) throws Throwable {
                    throw new Exception("TEST_ERROR");
                }
            });
            assert false;
        } catch (RuntimeException t) {
            assert t.getCause().getMessage().equals("TEST_ERROR");
            assert t.getCause() instanceof Exception;
        } catch (Throwable throwable) {
            assert false;
        }
    }
    //
    //
    @Test
    public void invokerDefineTest1() throws Throwable {
        //
        BindInfo<? extends InvokerFilter> bindInfo = PowerMockito.mock(BindInfo.class);
        InvokeFilterDefinition filterDefine = new InvokeFilterDefinition(123, "abc",//
                UriPatternType.get(UriPatternType.SERVLET, "/servlet"),//
                bindInfo, new HashMap<String, String>());
        //
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getInstance(bindInfo)).thenReturn(new TestCallerFilter());
        //
        ServletContext servletContext = PowerMockito.mock(ServletContext.class);
        PowerMockito.when(appContext.getInstance(ServletContext.class)).thenReturn(servletContext);
        //
        //
        TestCallerFilter.resetCalls();
        filterDefine.destroy();
        assert !TestCallerFilter.isDestroyCall(); // 没有init 不会调用 destroy
        filterDefine.init(new InvokerMapConfig(null, appContext));
        filterDefine.init(new InvokerMapConfig(null, appContext));
        //
        assert TestCallerFilter.isInitCall();
    }
    //
    @Test
    public void invokerDefineTest2() throws Throwable {
        //
        BindInfo<? extends InvokerFilter> bindInfo = PowerMockito.mock(BindInfo.class);
        InvokeFilterDefinition filterDefine = new InvokeFilterDefinition(123, "abc",//
                UriPatternType.get(UriPatternType.SERVLET, "/servlet"),//
                bindInfo, new HashMap<String, String>());
        //
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getInstance(bindInfo)).thenReturn(new TestDoNextCallerFilter());
        //
        ServletContext servletContext = PowerMockito.mock(ServletContext.class);
        PowerMockito.when(appContext.getInstance(ServletContext.class)).thenReturn(servletContext);
        Invoker mockInvoker = PowerMockito.mock(Invoker.class);
        //
        //
        TestCallerFilter.resetCalls();
        filterDefine.init(new InvokerMapConfig(null, InstanceProvider.wrap(appContext)));
        //
        assert TestCallerFilter.isInitCall();
        filterDefine.doInvoke(mockInvoker, new InvokerChain() {
            @Override
            public void doNext(Invoker invoker) throws Throwable {
                //
            }
        });
        assert TestCallerFilter.isDoCall();
        //
        filterDefine.destroy();
        assert TestCallerFilter.isDestroyCall();
    }
    //
    @Test
    public void invokerDefineTest3() throws Throwable {
        //
        BindInfo<? extends InvokerFilter> bindInfo = PowerMockito.mock(BindInfo.class);
        InvokeFilterDefinition filterDefine = new InvokeFilterDefinition(123, "abc",//
                UriPatternType.get(UriPatternType.SERVLET, "/servlet"),//
                bindInfo, new HashMap<String, String>());
        //
        AppContext appContext = PowerMockito.mock(AppContext.class);
        PowerMockito.when(appContext.getInstance(bindInfo)).thenReturn(new TestDoNextCallerFilter());
        //
        ServletContext servletContext = PowerMockito.mock(ServletContext.class);
        PowerMockito.when(appContext.getInstance(ServletContext.class)).thenReturn(servletContext);
        Invoker mockInvoker = PowerMockito.mock(Invoker.class);
        //
        filterDefine.init(new InvokerMapConfig(null, InstanceProvider.wrap(appContext)));
        //
        try {
            filterDefine.doInvoke(mockInvoker, new InvokerChain() {
                @Override
                public void doNext(Invoker invoker) throws Throwable {
                    throw new IOException("TEST_ERROR");
                }
            });
            assert false;
        } catch (IOException e) {
            assert "TEST_ERROR".equals(e.getMessage());
        } catch (Throwable throwable) {
            assert false;
        }
        //
        try {
            filterDefine.doInvoke(mockInvoker, new InvokerChain() {
                @Override
                public void doNext(Invoker invoker) throws Throwable {
                    throw new ServletException("TEST_ERROR");
                }
            });
            assert false;
        } catch (ServletException e) {
            assert "TEST_ERROR".equals(e.getMessage());
        } catch (Throwable throwable) {
            assert false;
        }
        //
        try {
            filterDefine.doInvoke(mockInvoker, new InvokerChain() {
                @Override
                public void doNext(Invoker invoker) throws Throwable {
                    throw new Exception("TEST_ERROR");
                }
            });
            assert false;
        } catch (Throwable e) {
            assert e.getMessage().equals("TEST_ERROR");
            assert e instanceof Exception;
        }
    }
}