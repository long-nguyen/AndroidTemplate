package ref.src.io;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.annotation.TargetApi;
import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;

/**
 * Provides utility to perform synchronous/asynchronous HTTP requests.
 *
 * Currently, only GET, POST and PUT requests are supported.
 *
 * Usage examples :
 * <ul>
 * <li>
 * Synchronous request (runs on the caller thread,
 * {@link android.os.NetworkOnMainThreadException} will be raised if ran from
 * the UI thread!). Note that only GET is available for this type of operation,
 * as POST/PUT requests most likely require additional request parameters to be
 * set, while this method does not has any.
 * <ul>
 * <li>
 * Basic Usage : execute HTTP request on given URI :
 * <p>
 *
 * <pre>
 * HttpRequester.requestGET(&quot;http://remote.url/path/&quot;);
 * </pre>
 *
 * </p>
 * </li>
 * <li>
 * Execute HTTP request using specified HttpParamGet instance :
 * <p>
 *
 * <pre>
 * HttpRequester.requestGET(new HttpRequester.HttpParamGet(
 * 		&quot;http://remote.url/path/&quot;));
 * </pre>
 *
 * </p>
 * </li>
 * <li>
 * It is possible to set HTTP query parameters before executing the request; <br/>
 * {@link HttpParamGet} ({@link HttpParamPost}, {@link HttpParamPut} for
 * POST/PUT requests) is used to setup parameters to be appended to the URI
 * before the request :
 * <p>
 *
 * <pre>
 * HttpParamGet hp = new HttpParamGet(&quot;http://remote.url/path/&quot;);
 * hp.addParam(&quot;param1&quot;, &quot;foo&quot;);
 * hp.addParam(&quot;param2&quot;, 42);
 * // URI requested will be http://remote.url/path/?param1=foo&amp;param2=42
 * HttpRequester.requestGET(hp);
 * </pre>
 *
 * <br>
 * For POST/PUT requests, should the content be sent in the request body,
 * {@link HttpParamPost#setEntity(org.apache.http.HttpEntity)} should be used
 * (or HttpParamPut for put). In addition, header should be set with the correct
 * {@code Content-Type}, optionally {@code Accept}. <br>
 * Refer to the official documentation of HttpPost/HttpPut for more details.
 *
 * </p>
 * </li>
 * </ul>
 * </li>
 * <li>
 * Asynchronous requests (a new thread will be spawned, processing the request
 * in the background).
 * <ul>
 * <li>
 * <p>
 * Basic usage is no different than {@code HttpRequester#requestGET}.<br/>
 * Without any other parameter, asynchronous request response will not be
 * processable.<br/>
 * Use {@code HttpRequester.requestGETAsync} instead : (
 * {@code HttpRequester#requestPOSTAsync}/{@code HttpRequester#requestPUTAsync}
 * can be used for resp. POST and PUT requests).
 * </p>
 *
 * <p>
 *
 * <pre>
 * HttpRequester.requestGETAsync(&quot;http://remote.url/path/&quot;);
 * </pre>
 *
 * <br>
 * Note: this version is only available for GET, as it doesn't allow parameters
 * to be set.
 * </p>
 * </li>
 *
 * <li>
 * <p>
 * Additionally, {@code HttpRequester.requestGETAsync} can take a
 * {@link HttpRequestListener} parameter, allowing to perform some operations
 * before the request is executed, and to process the server response :
 * </p>
 *
 * <p>
 *
 * <pre>
 *  HttpRequester.requestGETAsync("http://remote.url/path/", new HttpRequestListener() { ... });
 * </pre>
 *
 * HttpRequestListener can override two methods that runs before/after the
 * request :<br/>
 * - {@code onRequestPrepare(ExecutableRequest http, Uri uri)}<br/>
 * - {@code onRequestComplete(HttpRequestHolder requestHolder}<br/>
 * And two other methods to customize the HTTP request about to be executed :<br/>
 * - {@code onSetHttpParams(HttpParams httpParams)}<br/>
 * - {@code onSetHttpClient(HttpClient httpClient)}
 * </p>
 *
 * <p>
 * Also, a third parameter, the calling {@link Activity} can be passed : if not
 * null, and if available then the indeterminate progress bar visibility will be
 * switched based on the request execution status.
 * </p>
 *
 * <p>
 * {@link HttpRequestListener#onRequestPrepare(ExecutableRequest, Uri)} can be
 * used in POST/PUT requests to set the content body, additional headers...
 * </p>
 *
 * <p>
 * <em>Example:</em>
 *
 * <pre>
 * <code>
 * void onRequestPrepare(ExecutableRequest er, Uri uri) {
 *   //object for storing Json
 *   JSONObject data = new JSONObject();
 *   data.put("foo", "bar");
 *   data.put("bar", "baz");
 *   JSONArray ja = new JSONArray();
 *   ja.put(1);
 *   ja.put(42);
 *   ja.put(66);
 *   data.put("array", ja);
 *   StringEntity se = new StringEntity(data.toString());
 *   er.setEntity(se);
 *   er.setHeader("Accept", "application/json");
 *   er.setHeader("Content-Type", "application/json");
 * }
 * </code>
 * </pre>
 *
 * </p>
 *
 * <p>
 * Note that all parameters added using addParam* method will be added via
 * setEntity automatically when calling
 * {@link ExecutableRequest#execute(HttpRequestListener)}, so be careful not to
 * overwrite these if onRequestPrepare also perform setEntity.<br>
 * Content-type will also be set by default to x-www-form-urlencoded.<br>
 * In addition, they will also be passed as query parameters of the request.
 * </p>
 *
 * <p>
 * Also, setEntity is NOT called when using HttpRequester#requestPOST|PUT*
 * requiring a {@link ExecutableRequest} parameter, even if addParam* are
 * called.<br>
 * However, parameters are still passed a query parameters in the request URL.
 * </p>
 *
 * <p>
 * If needed, POST/PUT parameters set using setEntity may be cleared/modified by
 * using
 *
 * <pre>
 *   HttpEntity entity = ((HttpParamPost) req).getEntity();
 *   ... customize entity ...
 *   ((HttpParamPost) req).setEntity(entity); // set to null to clear
 * </pre>
 *
 * </p>
 *
 * </li>
 * </ul>
 * </li>
 * <li>
 * {@link HttpRequester#newRequest} can also be used to create the correct
 * {@link ExecutableRequest} based on specified request type. In that case,
 * {@code .execute(HttpRequestListener)} may be used instead to execute the
 * network operation asynchronously.
 * </ul>
 *
 * List of things to add :
 * <ul>
 * <li>
 * TODO : Add cancel feature (add HttpRequestListener.onRequestCancelled)</li>
 * <li>
 * TODO : Maybe use ExecutorService instead of AsyncTask ?</li>
 * </ul>
 *
 * @author mikami-yoann
 */
public final class HttpRequester {

	public static String android_user_agent = "Android";

	public static final int REQUEST_GET = 0x1;
	public static final int REQUEST_POST = 0x2;
	public static final int REQUEST_PUT = 0x3;
	public static final int REQUEST_DELETE = 0x4;
	public static final int REQUEST_HEAD = 0x5;

	/**
	 * Base class for a request listener when running asynchronous requests
	 * through {@link HttpRequester.requestGETAsync}.
	 * <p>
	 * If passed as a parameter to the latter,
	 * {@link HttpRequestListener.onRequestPrepare} will be called just before
	 * the request is executed, while
	 * {@link HttpRequestListener.onRequestComplete} will be called with the
	 * server response as its parameter.<br/>
	 * Both methods are called within the caller thread context (e.g. the main
	 * UI thread)
	 * </p>
	 * <p>
	 * Additionally, the following methods can be overridden :<br/>
	 * {@link HttpRequestListener.onSetHttpParams} will be called before
	 * executing the request, passing the HttpParams to set to the HttpClient.
	 * Caller can add HTTP parameters as necessary.
	 * {@link HttpRequestListener.onSetHttpClient} will be called before
	 * executing the request, passing the HttpClient that will be used to
	 * execute the request. Caller can modify its behavior as necessary.
	 * </p>
	 * <p>
	 * Server response is encapsulated within an {@link HttpResponseHolder}
	 * object, which is a simple wrapper of {@link ord.apache.http.HttpResponse}
	 * .
	 * </p>
	 *
	 * @author mikami-yoann
	 */
	public static abstract class HttpRequestListener {

		/**
		 * Called on created HttpClient instance to allow custom config by
		 * caller
		 *
		 * @param client
		 *            the HttpClient to be used for this request
		 */
		private final void setHttpClient(HttpClient client) {
			this.onSetHttpClient(client);
		}

		/**
		 * Called on a default set of HTTP parameters to allow customization by
		 * caller
		 *
		 * @param params
		 *            The HttpParams object to customize
		 */
		private final void setRequestParams(HttpParams params) {
			this.onSetRequestParams(params);
		}

		/**
		 * Called before the request is executed. Wrapper for abstract method
		 * onRequestPrepare. <br/>
		 * Can be used to perform pre/post initializations around
		 * onRequestPrepare.
		 *
		 * @param http
		 *            the request about to be executed
		 * @param uri
		 *            the uri being requested
		 */
		private final void requestPrepare(ExecutableRequest http, Uri uri) {
			this.onRequestPrepare(http, uri);
		}

		/**
		 * Called after the request is executed. Wrapper for abstract method
		 * onRequestComplete. <br/>
		 * Can be used to perform pre/post initializations around
		 * onRequestComplete.
		 *
		 * @param httpGet
		 *            the request that was executed
		 * @param uri
		 *            the uri that was requested
		 */
		private final void requestComplete(HttpResponseHolder responseHolder) {
			this.onRequestComplete(responseHolder);
		}

		/**
		 * Gives an opportunity for the caller to modify client to be used by
		 * request before its execution. Contract: {@code execute} should not be
		 * called in this method!!
		 *
		 * @param params
		 *            the HttpClient object to be used for execution
		 */
		protected void onSetHttpClient(HttpClient client) {
		}

		/**
		 * Gives an opportunity for the caller to set specific HTTP parameters
		 * before executing the request
		 *
		 * methods defined in {@link HttpConnectionParams},
		 * {@link ConnManagerParams}, {@link HttpProtocolParams} can be used on
		 * the parameter to customize the HTTP request.
		 *
		 * @param params
		 *            the HttpParams object to be used for execution
		 */
		protected void onSetRequestParams(HttpParams params) {
		}

		/**
		 * Subclasses may override this. Callback called before the request is
		 * made. <br/>
		 * Does nothing by default.
		 * <p>
		 * Subclasses may want to override this to add parameters, headers to
		 * the ExecutableRequest object, or update the UI.<br>
		 * </p>
		 * <p>
		 * POST and PUT requests may set the content body in this.
		 * </p>
		 * <p>
		 * <strong>Warning :</strong> This is called in the caller thread, so it
		 * should *NOT* perform heavy processing.
		 * </p>
		 *
		 * @param http
		 *            the request about to be executed
		 * @param uri
		 *            the uri being requested
		 */
		protected void onRequestPrepare(ExecutableRequest http, Uri uri) {
		}

		/**
		 * Subclasses may override this. Callback called after the request is
		 * made. <br/>
		 * Does nothing by default.
		 * <p>
		 * Subclasses may want to override this to e.g. check response code,
		 * potential errors, or to retrieve the response body and react
		 * accordingly.<br/>
		 * <strong>Warning :</strong> This is called in the caller thread, so it
		 * should *NOT* perform heavy processing.
		 * </p>
		 * <p>
		 * The {@link HttpResponseHolder} parameter contains the HTTP status
		 * code in {@code returnCode}, the uri that was requested in {@code uri}
		 * , the response headers in {@code headers} and the response body in
		 * {@code responseBody}.
		 * </p>
		 *
		 * @param responseHolder
		 *            responseHolder the request response
		 * @param uri
		 *            the uri that was requested
		 */
		protected void onRequestComplete(HttpResponseHolder responseHolder) {
		}
	};

	/**
	 * Interface defining an executable URI request
	 *
	 * GET, POST and PUT requests are all wrapped around this common interface.
	 * It subclasses {@link HttpUriRequest} and as such extends the built-in
	 * java interfaces.
	 * <p>
	 * {@link HttpParamGet}, {@link HttpParamPost}, {@link HttpParamPut} all
	 * implements this interface.
	 * </p>
	 */
	public static interface ExecutableRequest extends HttpUriRequest {

		/**
		 * Adds the specified int request parameter with specified value.
		 *
		 * @param name
		 *            the request parameter name to add
		 * @param value
		 *            the request parameter value to add
		 */
		void addParam(String name, int value);

		/**
		 * Adds the specified long request parameter with specified value.
		 *
		 * @param name
		 *            the request parameter name to add
		 * @param value
		 *            the request parameter value to add
		 */
		void addParam(String name, long value);

		/**
		 * Adds the specified boolean request parameter with specified value.
		 *
		 * @param name
		 *            the request parameter name to add
		 * @param value
		 *            the request parameter value to add
		 */
		void addParam(String name, boolean value);

		/**
		 * Adds the specified double request parameter with specified value.
		 *
		 * @param name
		 *            the request parameter name to add
		 * @param value
		 *            the request parameter value to add
		 */
		void addParam(String name, double value);

		/**
		 * Adds the specified float request parameter with specified value.
		 *
		 * @param name
		 *            the request parameter name to add
		 * @param value
		 *            the request parameter value to add
		 */
		void addParam(String name, float value);

		/**
		 * Adds the specified String request parameter with specified value.
		 *
		 * @param name
		 *            the request parameter name to add
		 * @param value
		 *            the request parameter value to add
		 */
		void addParam(String name, String value);

		/**
		 * Returns the URI this request uses, such as
		 * {@code http://example.org/path/to/file}.
		 * <p>
		 * If parameters were previously set using any of the {@code addParam}
		 * methods, the returned URI will be appended with a query string built
		 * using the list of parameters that were provided at this point.
		 * </p>
		 *
		 * @return the URI to associated with current HTTP request, appended
		 *         with the query string if any
		 */
		@Override
		URI getURI();

		/**
		 * Adds a basic auth header encoded with user/pass
		 *
		 * @param user
		 *            the basic authorization username
		 * @param pass
		 *            the basic authorization password
		 */
		void setBasicAuthHeader(String user, String pass);

		/**
		 * Asynchronously performs the request using specified listener
		 *
		 * @param whenLoaded
		 *            the {@link HttpRequestListener} listener to use during the
		 *            asynchronous operation
		 */
		void execute(HttpRequestListener whenLoaded);
	}

	/**
	 * Returns a new {@code ExecutableRequest} instance which can be used to
	 * make an asynchronous HTTP request
	 *
	 * Usage sample for a GET request :
	 * <p>
	 *
	 * <pre>
	 * ExecutableRequest req = HttpRequester.newRequest(HttpRequester.REQUEST_GET,
	 * 		&quot;http://my/site/to/get&quot;);
	 * // GET request parameters
	 * req.addParam(&quot;foo&quot;, &quot;value&quot;); // String
	 * req.addParam(&quot;bar&quot;, 2); // int
	 * req.execute(new HttpRequestListener() {
	 * 	protected void onRequestComplete(HttpResponseHolder responseHolder) {
	 * 		// do sth with the response
	 * 	}
	 *
	 * 	// Add another parameter dynamically
	 * 	protected void onSetRequestParams(HttpParams params) {
	 * 		params.setDoubleParameter(&quot;anotherParam&quot;, 2.0);
	 * 	}
	 * });
	 * </pre>
	 *
	 * </p>
	 * The same can be achieved for a POST by replacing
	 * {@code HttpRequester.REQUEST_GET} with {@code HttpRequester.REQUEST_POST}
	 * .
	 *
	 * HttpRequest
	 *
	 * @param type
	 *            One of {@link HttpRequester#REQUEST_GET},
	 *            {@link HttpRequester#REQUEST_POST},
	 *            {@link HttpRequester#REQUEST_PUT},
	 *            {@link HttpRequester#REQUEST_DELETE},
	 *            {@link HttpRequester#REQUEST_HEAD}
	 * @param uri
	 *            the target URI
	 * @return ExecutableRequest an object
	 * @throws Exception
	 */
	public static ExecutableRequest newRequest(final int type, final String uri)
			throws Exception {
		switch (type) {
		case REQUEST_GET:
			return new HttpParamGet(uri);
		case REQUEST_POST:
			return new HttpParamPost(uri);
		case REQUEST_PUT:
			return new HttpParamPut(uri);
		case REQUEST_DELETE:
			throw new UnsupportedOperationException(
					"DELETE HTTP method not supported yet!");
		case REQUEST_HEAD:
			throw new UnsupportedOperationException(
					"HEAD HTTP method not supported yet!");
		default:
			throw new UnsupportedOperationException("Unknown HTTP method!");
		}
	}

	/**
	 * Extends HttpPost with an easy way to add POST query parameters to the
	 * request.
	 * <p>
	 * The added parameters will make it into the requested url as
	 * {@code uri?param1=xxx&param2=xxx...&paramn=xxx}.<br/>
	 * The parameters from the resulting URL are URL-encoded.
	 * </p>
	 *
	 * @author mikami-yoann
	 */
	public final static class HttpParamPost extends HttpPost implements
			ExecutableRequest {

		private List<NameValuePair> mParams = new LinkedList<NameValuePair>();

		public HttpParamPost() {
			super();
		}

		public HttpParamPost(String uri) {
			super(uri);
		}

		public HttpParamPost(URI uri) {
			super(uri);
		}

		/**
		 * Adds the specified int parameter with specified value.
		 *
		 * @param name
		 *            the parameter name to add
		 * @param value
		 *            the parameter value to add
		 */
		public void addParam(String name, int value) {
			mParams.add(new BasicNameValuePair(name, String.valueOf(value)));
		}

		/**
		 * Adds the specified long parameter with specified value.
		 *
		 * @param name
		 *            the parameter name to add
		 * @param value
		 *            the parameter value to add
		 */
		public void addParam(String name, long value) {
			mParams.add(new BasicNameValuePair(name, String.valueOf(value)));
		}

		/**
		 * Adds the specified boolean parameter with specified value.
		 *
		 * @param name
		 *            the parameter name to add
		 * @param value
		 *            the parameter value to add
		 */
		public void addParam(String name, boolean value) {
			mParams.add(new BasicNameValuePair(name, String.valueOf(value)));
		}

		/**
		 * Adds the specified double parameter with specified value.
		 *
		 * @param name
		 *            the parameter name to add
		 * @param value
		 *            the parameter value to add
		 */
		public void addParam(String name, double value) {
			mParams.add(new BasicNameValuePair(name, String.valueOf(value)));
		}

		/**
		 * Adds the specified float parameter with specified value.
		 *
		 * @param name
		 *            the parameter name to add
		 * @param value
		 *            the parameter value to add
		 */
		public void addParam(String name, float value) {
			mParams.add(new BasicNameValuePair(name, String.valueOf(value)));
		}

		/**
		 * Adds the specified String parameter with specified value.
		 *
		 * @param name
		 *            the parameter name to add
		 * @param value
		 *            the parameter value to add
		 */
		public void addParam(String name, String value) {
			mParams.add(new BasicNameValuePair(name, value));
		}

		/**
		 * Returns the URI this request uses, such as
		 * {@code http://example.org/path/to/file}.
		 *
		 * @return the URI to associated with current HTTP request, appended
		 *         with the query string if any
		 */
		@Override
		public URI getURI() {
			return URI.create(super.getURI().toString());
		}

		/**
		 * Adds a basic auth header encoded with user/pass
		 *
		 * @param user
		 *            the basic authorization username
		 * @param pass
		 *            the basic authorization password
		 */
		public void setBasicAuthHeader(String user, String pass) {
			Header hdr = HttpRequester.getBasicAuthHeader(user, pass);
			setHeader(hdr);
		}

		public void execute(HttpRequestListener whenLoaded) {
			try {
				UrlEncodedFormEntity ufe = new UrlEncodedFormEntity(mParams,
						"UTF-8");
				ufe.setContentType(URLEncodedUtils.CONTENT_TYPE);
				setEntity(ufe);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			HttpRequestHolder loadReq = HttpRequestHolder.newInstance(this,
					whenLoaded);
			(new HttpRequesterTask(whenLoaded, null)).execute(loadReq);
		}
	}

	/**
	 * Extends HttpPut with an easy way to add PUT query parameters to the
	 * request.
	 * <p>
	 * The added parameters will make it into the requested url as
	 * {@code uri?param1=xxx&param2=xxx...&paramn=xxx}.<br/>
	 * The parameters from the resulting URL are URL-encoded.
	 * </p>
	 *
	 * @author mikami-yoann
	 */
	public final static class HttpParamPut extends HttpPut implements
			ExecutableRequest {

		private List<NameValuePair> mParams = new LinkedList<NameValuePair>();

		public HttpParamPut() {
			super();
		}

		public HttpParamPut(String uri) {
			super(uri);
		}

		public HttpParamPut(URI uri) {
			super(uri);
		}

		/**
		 * Adds the specified int parameter with specified value.
		 *
		 * @param name
		 *            the parameter name to add
		 * @param value
		 *            the parameter value to add
		 */
		public void addParam(String name, int value) {
			mParams.add(new BasicNameValuePair(name, String.valueOf(value)));
		}

		/**
		 * Adds the specified long parameter with specified value.
		 *
		 * @param name
		 *            the parameter name to add
		 * @param value
		 *            the parameter value to add
		 */
		public void addParam(String name, long value) {
			mParams.add(new BasicNameValuePair(name, String.valueOf(value)));
		}

		/**
		 * Adds the specified boolean parameter with specified value.
		 *
		 * @param name
		 *            the parameter name to add
		 * @param value
		 *            the parameter value to add
		 */
		public void addParam(String name, boolean value) {
			mParams.add(new BasicNameValuePair(name, String.valueOf(value)));
		}

		/**
		 * Adds the specified double parameter with specified value.
		 *
		 * @param name
		 *            the parameter name to add
		 * @param value
		 *            the parameter value to add
		 */
		public void addParam(String name, double value) {
			mParams.add(new BasicNameValuePair(name, String.valueOf(value)));
		}

		/**
		 * Adds the specified float parameter with specified value.
		 *
		 * @param name
		 *            the parameter name to add
		 * @param value
		 *            the parameter value to add
		 */
		public void addParam(String name, float value) {
			mParams.add(new BasicNameValuePair(name, String.valueOf(value)));
		}

		/**
		 * Adds the specified String parameter with specified value.
		 *
		 * @param name
		 *            the parameter name to add
		 * @param value
		 *            the parameter value to add
		 */
		public void addParam(String name, String value) {
			mParams.add(new BasicNameValuePair(name, value));
		}

		/**
		 * Returns the URI this request uses, such as
		 * {@code http://example.org/path/to/file}.
		 *
		 * @return the URI to associated with current HTTP request, appended
		 *         with the query string if any
		 */
		@Override
		public URI getURI() {
			String paramString = URLEncodedUtils.format(mParams, "utf-8");
			return URI.create(super.getURI().toString() + "?" + paramString);
		}

		/**
		 * Adds a basic auth header encoded with user/pass
		 *
		 * @param user
		 *            the basic authorization username
		 * @param pass
		 *            the basic authorization password
		 */
		public void setBasicAuthHeader(String user, String pass) {
			Header hdr = HttpRequester.getBasicAuthHeader(user, pass);
			this.setHeader(hdr);
		}

		public void execute(HttpRequestListener whenLoaded) {
			try {
				UrlEncodedFormEntity ufe = new UrlEncodedFormEntity(mParams,
						"UTF-8");
				ufe.setContentType(URLEncodedUtils.CONTENT_TYPE);
				setEntity(ufe);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			HttpRequestHolder loadReq = HttpRequestHolder.newInstance(this,
					whenLoaded);
			(new HttpRequesterTask(whenLoaded, null)).execute(loadReq);
		}
	}

	/**
	 * Extends HttpGet with an easy way to add GET query parameters to the
	 * request.
	 * <p>
	 * The added parameters will make it into the requested url as
	 * {@code uri?param1=xxx&param2=xxx...&paramn=xxx}.<br/>
	 * The parameters from the resulting URL are URL-encoded.
	 * </p>
	 *
	 * @author mikami-yoann
	 */
	public final static class HttpParamGet extends HttpGet implements
			ExecutableRequest {

		private List<NameValuePair> mParams = new LinkedList<NameValuePair>();

		public HttpParamGet() {
			super();
		}

		public HttpParamGet(String uri) {
			super(uri);
		}

		public HttpParamGet(URI uri) {
			super(uri);
		}

		/**
		 * Adds the specified int parameter with specified value.
		 *
		 * @param name
		 *            the parameter name to add
		 * @param value
		 *            the parameter value to add
		 */
		public void addParam(String name, int value) {
			mParams.add(new BasicNameValuePair(name, String.valueOf(value)));
		}

		/**
		 * Adds the specified long parameter with specified value.
		 *
		 * @param name
		 *            the parameter name to add
		 * @param value
		 *            the parameter value to add
		 */
		public void addParam(String name, long value) {
			mParams.add(new BasicNameValuePair(name, String.valueOf(value)));
		}

		/**
		 * Adds the specified boolean parameter with specified value.
		 *
		 * @param name
		 *            the parameter name to add
		 * @param value
		 *            the parameter value to add
		 */
		public void addParam(String name, boolean value) {
			mParams.add(new BasicNameValuePair(name, String.valueOf(value)));
		}

		/**
		 * Adds the specified double parameter with specified value.
		 *
		 * @param name
		 *            the parameter name to add
		 * @param value
		 *            the parameter value to add
		 */
		public void addParam(String name, double value) {
			mParams.add(new BasicNameValuePair(name, String.valueOf(value)));
		}

		/**
		 * Adds the specified float parameter with specified value.
		 *
		 * @param name
		 *            the parameter name to add
		 * @param value
		 *            the parameter value to add
		 */
		public void addParam(String name, float value) {
			mParams.add(new BasicNameValuePair(name, String.valueOf(value)));
		}

		/**
		 * Adds the specified String parameter with specified value.
		 *
		 * @param name
		 *            the parameter name to add
		 * @param value
		 *            the parameter value to add
		 */
		public void addParam(String name, String value) {
			mParams.add(new BasicNameValuePair(name, value));
		}

		/**
		 * Returns the URI this request uses, such as
		 * {@code http://example.org/path/to/file}.
		 * <p>
		 * If parameters were previously set using any of the {@code addParam}
		 * methods, the returned URI will be appended with a query string built
		 * using the list of parameters that were provided at this point.
		 * </p>
		 *
		 * @return the URI to associated with current HTTP request, appended
		 *         with the query string if any
		 */
		@Override
		public URI getURI() {
			String paramString = URLEncodedUtils.format(mParams, "utf-8");
			return URI.create(super.getURI().toString() + "?" + paramString);
		}

		/**
		 * Adds a basic auth header encoded with user/pass
		 *
		 * @param user
		 *            the basic authorization username
		 * @param pass
		 *            the basic authorization password
		 */
		public void setBasicAuthHeader(String user, String pass) {
			Header hdr = HttpRequester.getBasicAuthHeader(user, pass);
			this.setHeader(hdr);
		}

		public void execute(HttpRequestListener whenLoaded) {
			HttpRequestHolder loadReq = HttpRequestHolder.newInstance(this,
					whenLoaded);
			(new HttpRequesterTask(whenLoaded, null)).execute(loadReq);
		}
	}

	/**
	 * Returns a basic auth header on specified {@link ExecutableRequest}
	 * encoded with user/pass
	 *
	 * @param user
	 *            the basic authorization username
	 * @param pass
	 *            the basic authorization password
	 * @return the basic authentication header
	 */
	public static Header getBasicAuthHeader(String user, String pass) {
		String basicAuth = user + ":" + pass;
		String auth = "Basic "
				+ Base64.encodeToString(basicAuth.getBytes(),
				  Base64.NO_WRAP);
		return new BasicHeader("Authorization", auth);
	}

	/**
	 * Simple wrapper for an HTTP request response
	 * <p>
	 * Public fields :<br>
	 * - {@code int returnCode} : HTTP status code returned by server<br>
	 * - {@code Uri uri} : the URI that was queried on the server<br>
	 * - {@code String responseBody} : the response body returned by the server,
	 * as a single String<br>
	 * - {@code byte[] responseBytes} : the response body returned by the server,
	 * in bytes for binary responses<br>
	 * - {@code Map<String, String> headers} : the headers found in the response
	 *
	 * @author mikami-yoann
	 */
	public final static class HttpResponseHolder {

		/**
		 * The HTTP status code
		 */
		public int returnCode;
		/**
		 * The URI queried on the server
		 */
		public Uri uri;
		/**
		 * The response body for text responses
		 */
		public String responseBody;
		/**
		 * 	The response body for binary responses
		 */
		public byte[] responseBytes;
		/**
		 * The Response HTTP headers
		 */
		public Map<String, String> headers = new HashMap<String, String>();

		@Override
		public String toString() {
			return "(" + returnCode + ") Body: " + responseBody;
		}
	};

	/**
	 * Executes an HTTP GET request to specified URI on the current thread
	 *
	 * @param uri
	 *            the address to request
	 * @return the request response
	 */
	public static HttpResponseHolder requestGET(String uri) {
		return requestGET(new HttpParamGet(uri));
	}

	/**
	 * Executes an HTTP GET request to specified URI on the current thread
	 *
	 * @param req
	 *            the HttpGet object to request
	 * @return the request response
	 */
	public static HttpResponseHolder requestGET(HttpParamGet req) {
		return request(HttpRequestHolder.newInstance(req, null));
	}

	/**
	 * Executes a new asynchronous request to specified URI
	 *
	 * <p>
	 * This method spawns a new thread to perform the request
	 * </p>
	 * Using this method, the server response will not be given back to the
	 * caller.
	 * <p>
	 * Pass in a {@link HttpRequester.HttpRequestListener} to this method to
	 * allow the caller to process the response returned by the server
	 * </p>
	 *
	 * @param uri
	 *            the uri to request
	 */
	public static void requestGETAsync(final String uri) {
		requestGETAsync(new HttpParamGet(uri));
	}

	/**
	 * Executes a new asynchronous GET request to specified URI
	 *
	 * <p>
	 * This method spawns a new thread to perform the request
	 * </p>
	 * Using this method, the server response will not be given back to the
	 * caller.<br/>
	 * Also, HTTP params and the HTTP client cannot be customized using this
	 * variant of the method.
	 * <p>
	 * Pass in a {@link HttpRequester.HttpRequestListener} to this method to
	 * allow the caller to process the response returned by the server
	 * </p>
	 *
	 * @param req
	 *            the HttpGet object to request
	 */
	public static void requestGETAsync(final HttpParamGet req) {
		requestAsync(req, null, null);
	}

	/**
	 * Executes a new asynchronous GET request to specified URI
	 *
	 * <p>
	 * This method spawns a new thread to perform the request
	 * </p>
	 * <p>
	 * Given {@link HttpRequestListener} will be used as a callback before the
	 * request is executed and will be given back the server response once the
	 * execution completes.<br/>
	 * {@code HttpRequestListener.onSetHttpClient} and
	 * {@code HttpRequestListener.onSetHttpParams} will also be called so that
	 * the caller can customize the request before it is executed.
	 * </p>
	 * <p>
	 * In case of a client-side exception during the execution,
	 * {@code returnCode} will have a value of 500 and {@code responseBody} will
	 * contain the exception message.
	 * </p>
	 *
	 * @param uri
	 *            the uri to request
	 * @param whenLoaded
	 *            the callback to use before/after the request
	 */
	public static void requestGETAsync(final String uri,
			final HttpRequestListener whenLoaded) {
		requestGETAsync(new HttpParamGet(uri), whenLoaded);
	}
	

	/**
	 * Executes a new asynchronous request to specified URI
	 *
	 * <p>
	 * This method spawns a new thread to perform the request
	 * </p>
	 * <p>
	 * Given {@link HttpRequestListener} will be used as a callback before the
	 * request is executed and will be given back the server response once the
	 * execution completes.<br/>
	 * {@code HttpRequestListener.onSetHttpClient} and
	 * {@code HttpRequestListener.onSetHttpParams} will also be called so that
	 * the caller can customize the request before it is executed.
	 * </p>
	 * <p>
	 * In case of a client-side exception during the execution,
	 * {@code returnCode} will have a value of 500 and {@code responseBody} will
	 * contain the exception message.
	 * </p>
	 *
	 * @param req
	 *            the HttpGet object to request
	 * @param whenLoaded
	 *            the callback to use before/after the request
	 */
	public static void requestGETAsync(final HttpParamGet req,
			final HttpRequestListener whenLoaded) {
		requestAsync(req, whenLoaded, null);
	}

	/**
	 * Executes a new asynchronous request to specified URI
	 *
	 * <p>
	 * This method spawns a new thread to perform the request
	 * </p>
	 * <p>
	 * Given {@link HttpRequestListener} will be used as a callback before the
	 * request is executed and will be given back the server response once the
	 * execution completes.<br/>
	 * {@code HttpRequestListener.onSetHttpClient} and
	 * {@code HttpRequestListener.onSetHttpParams} will also be called so that
	 * the caller can customize the request before it is executed.
	 * </p>
	 * <p>
	 * In case of a client-side exception during the execution,
	 * {@code returnCode} will have a value of 500 and {@code responseBody} will
	 * contain the exception message.
	 * </p>
	 * <p>
	 * The third parameter is an Activity that the caller wish to notify as
	 * being busy during the request execution.<br/>
	 * {@code Activity.setProgressBarIndeterminateVisibility} will be called
	 * before and after the request.
	 * </p>
	 *
	 * @param uri
	 *            the uri to request
	 * @param whenLoaded
	 *            the callback to use before/after the request
	 * @param activity
	 *            the activity to notify when request is busy executing
	 */
	public static void requestGETAsync(final String uri,
			final HttpRequestListener whenLoaded, Activity activity) {
		requestAsync(new HttpParamGet(uri), whenLoaded, activity);
	}

	/**
	 * Executes a new asynchronous request to specified URI
	 *
	 * <p>
	 * This method spawns a new thread to perform the request
	 * </p>
	 * <p>
	 * Given {@link HttpRequestListener} will be used as a callback before the
	 * request is executed and will be given back the server response once the
	 * execution completes.<br/>
	 * {@code HttpRequestListener.onSetHttpClient} and
	 * {@code HttpRequestListener.onSetHttpParams} will also be called so that
	 * the caller can customize the request before it is executed.
	 * </p>
	 * <p>
	 * In case of a client-side exception during the execution,
	 * {@code returnCode} will have a value of 500 and {@code responseBody} will
	 * contain the exception message.
	 * </p>
	 * <p>
	 * The third parameter is an Activity that the caller wish to notify as
	 * being busy during the request execution.<br/>
	 * {@code Activity.setProgressBarIndeterminateVisibility} will be called
	 * before and after the request.
	 * </p>
	 * 
	 * @param req
	 *            the HttpGet object to request
	 * @param whenLoaded
	 *            the callback to use before/after the request
	 * @param activity
	 *            the activity to notify when request is busy executing
	 */
	public static void requestGETAsync(final HttpParamGet req,
			final HttpRequestListener whenLoaded, Activity activity) {
		requestAsync(req, whenLoaded, activity);
	}


	/**
	 * Executes a new asynchronous POST request to specified URI
	 *
	 * <p>
	 * This method spawns a new thread to perform the request
	 * </p>
	 * Using this method, the server response will not be given back to the
	 * caller.<br/>
	 * Also, HTTP params and the HTTP client cannot be customized using this
	 * variant of the method.
	 * <p>
	 * Pass in a {@link HttpRequester.HttpRequestListener} to this method to
	 * allow the caller to process the response returned by the server
	 * </p>
	 *
	 * @param req
	 *            the HttpGet object to request
	 */
	public static void requestPOSTAsync(final HttpParamPost req) {
		requestAsync(req, null, null);
	}

	/**
	 * Executes a new asynchronous POST request to specified URI
	 *
	 * <p>
	 * This method spawns a new thread to perform the request
	 * </p>
	 * <p>
	 * Given {@link HttpRequestListener} will be used as a callback before the
	 * request is executed and will be given back the server response once the
	 * execution completes.<br/>
	 * {@code HttpRequestListener.onSetHttpClient} and
	 * {@code HttpRequestListener.onSetHttpParams} will also be called so that
	 * the caller can customize the request before it is executed.
	 * </p>
	 * <p>
	 * In case of a client-side exception during the execution,
	 * {@code returnCode} will have a value of 500 and {@code responseBody} will
	 * contain the exception message.
	 * </p>
	 *
	 * @param uri
	 *            the uri to request
	 * @param whenLoaded
	 *            the callback to use before/after the request
	 */
	public static void requestPOSTAsync(final String uri,
			final HttpRequestListener whenLoaded) {
		requestPOSTAsync(new HttpParamPost(uri), whenLoaded);
	}

	/**
	 * Executes a new asynchronous POST request to specified URI
	 *
	 * <p>
	 * This method spawns a new thread to perform the request
	 * </p>
	 * <p>
	 * Given {@link HttpRequestListener} will be used as a callback before the
	 * request is executed and will be given back the server response once the
	 * execution completes.<br/>
	 * {@code HttpRequestListener.onSetHttpClient} and
	 * {@code HttpRequestListener.onSetHttpParams} will also be called so that
	 * the caller can customize the request before it is executed.
	 * </p>
	 * <p>
	 * In case of a client-side exception during the execution,
	 * {@code returnCode} will have a value of 500 and {@code responseBody} will
	 * contain the exception message.
	 * </p>
	 *
	 * @param req
	 *            the HttpGet object to request
	 * @param whenLoaded
	 *            the callback to use before/after the request
	 */
	public static void requestPOSTAsync(final HttpParamPost req,
			final HttpRequestListener whenLoaded) {
		requestAsync(req, whenLoaded, null);
	}

	/**
	 * Executes a new asynchronous POST request to specified URI
	 *
	 * <p>
	 * This method spawns a new thread to perform the request
	 * </p>
	 * <p>
	 * Given {@link HttpRequestListener} will be used as a callback before the
	 * request is executed and will be given back the server response once the
	 * execution completes.<br/>
	 * {@code HttpRequestListener.onSetHttpClient} and
	 * {@code HttpRequestListener.onSetHttpParams} will also be called so that
	 * the caller can customize the request before it is executed.
	 * </p>
	 * <p>
	 * In case of a client-side exception during the execution,
	 * {@code returnCode} will have a value of 500 and {@code responseBody} will
	 * contain the exception message.
	 * </p>
	 * <p>
	 * The third parameter is an Activity that the caller wish to notify as
	 * being busy during the request execution.<br/>
	 * {@code Activity.setProgressBarIndeterminateVisibility} will be called
	 * before and after the request.
	 * </p>
	 *
	 * @param uri
	 *            the uri to request
	 * @param whenLoaded
	 *            the callback to use before/after the request
	 * @param activity
	 *            the activity to notify when request is busy executing
	 */
	public static void requestPOSTAsync(final String uri,
			final HttpRequestListener whenLoaded, Activity activity) {
		requestAsync(new HttpParamPost(uri), whenLoaded, activity);
	}

	/**
	 * Executes a new asynchronous POST request to specified URI
	 *
	 * <p>
	 * This method spawns a new thread to perform the request
	 * </p>
	 * <p>
	 * Given {@link HttpRequestListener} will be used as a callback before the
	 * request is executed and will be given back the server response once the
	 * execution completes.<br/>
	 * {@code HttpRequestListener.onSetHttpClient} and
	 * {@code HttpRequestListener.onSetHttpParams} will also be called so that
	 * the caller can customize the request before it is executed.
	 * </p>
	 * <p>
	 * In case of a client-side exception during the execution,
	 * {@code returnCode} will have a value of 500 and {@code responseBody} will
	 * contain the exception message.
	 * </p>
	 * <p>
	 * The third parameter is an Activity that the caller wish to notify as
	 * being busy during the request execution.<br/>
	 * {@code Activity.setProgressBarIndeterminateVisibility} will be called
	 * before and after the request.
	 * </p>
	 *
	 * @param req
	 *            the HttpGet object to request
	 * @param whenLoaded
	 *            the callback to use before/after the request
	 * @param activity
	 *            the activity to notify when request is busy executing
	 */
	public static void requestPOSTAsync(final HttpParamPost req,
			final HttpRequestListener whenLoaded, Activity activity) {
		requestAsync(req, whenLoaded, activity);
	}


	/**
	 * Executes a new asynchronous PUT request to specified URI
	 *
	 * <p>
	 * This method spawns a new thread to perform the request
	 * </p>
	 * Using this method, the server response will not be given back to the
	 * caller.<br/>
	 * Also, HTTP params and the HTTP client cannot be customized using this
	 * variant of the method.
	 * <p>
	 * Pass in a {@link HttpRequester.HttpRequestListener} to this method to
	 * allow the caller to process the response returned by the server
	 * </p>
	 *
	 * @param req
	 *            the HttpGet object to request
	 */
	public static void requestPUTAsync(final HttpParamPut req) {
		requestAsync(req, null, null);
	}

	/**
	 * Executes a new asynchronous PUT request to specified URI
	 *
	 * <p>
	 * This method spawns a new thread to perform the request
	 * </p>
	 * <p>
	 * Given {@link HttpRequestListener} will be used as a callback before the
	 * request is executed and will be given back the server response once the
	 * execution completes.<br/>
	 * {@code HttpRequestListener.onSetHttpClient} and
	 * {@code HttpRequestListener.onSetHttpParams} will also be called so that
	 * the caller can customize the request before it is executed.
	 * </p>
	 * <p>
	 * In case of a client-side exception during the execution,
	 * {@code returnCode} will have a value of 500 and {@code responseBody} will
	 * contain the exception message.
	 * </p>
	 *
	 * @param uri
	 *            the uri to request
	 * @param whenLoaded
	 *            the callback to use before/after the request
	 */
	public static void requestPUTAsync(final String uri,
			final HttpRequestListener whenLoaded) {
		requestPUTAsync(new HttpParamPut(uri), whenLoaded);
	}

	/**
	 * Executes a new asynchronous PUT request to specified URI
	 *
	 * <p>
	 * This method spawns a new thread to perform the request
	 * </p>
	 * <p>
	 * Given {@link HttpRequestListener} will be used as a callback before the
	 * request is executed and will be given back the server response once the
	 * execution completes.<br/>
	 * {@code HttpRequestListener.onSetHttpClient} and
	 * {@code HttpRequestListener.onSetHttpParams} will also be called so that
	 * the caller can customize the request before it is executed.
	 * </p>
	 * <p>
	 * In case of a client-side exception during the execution,
	 * {@code returnCode} will have a value of 500 and {@code responseBody} will
	 * contain the exception message.
	 * </p>
	 *
	 * @param req
	 *            the HttpGet object to request
	 * @param whenLoaded
	 *            the callback to use before/after the request
	 */
	public static void requestPUTAsync(final HttpParamPut req,
			final HttpRequestListener whenLoaded) {
		requestAsync(req, whenLoaded, null);
	}

	/**
	 * Executes a new asynchronous PUT request to specified URI
	 *
	 * <p>
	 * This method spawns a new thread to perform the request
	 * </p>
	 * <p>
	 * Given {@link HttpRequestListener} will be used as a callback before the
	 * request is executed and will be given back the server response once the
	 * execution completes.<br/>
	 * {@code HttpRequestListener.onSetHttpClient} and
	 * {@code HttpRequestListener.onSetHttpParams} will also be called so that
	 * the caller can customize the request before it is executed.
	 * </p>
	 * <p>
	 * In case of a client-side exception during the execution,
	 * {@code returnCode} will have a value of 500 and {@code responseBody} will
	 * contain the exception message.
	 * </p>
	 * <p>
	 * The third parameter is an Activity that the caller wish to notify as
	 * being busy during the request execution.<br/>
	 * {@code Activity.setProgressBarIndeterminateVisibility} will be called
	 * before and after the request.
	 * </p>
	 *
	 * @param uri
	 *            the uri to request
	 * @param whenLoaded
	 *            the callback to use before/after the request
	 * @param activity
	 *            the activity to notify when request is busy executing
	 */
	public static void requestPUTAsync(final String uri,
			final HttpRequestListener whenLoaded, Activity activity) {
		requestAsync(new HttpParamPut(uri), whenLoaded, activity);
	}

	/**
	 * Executes a new asynchronous PUT request to specified URI
	 *
	 * <p>
	 * This method spawns a new thread to perform the request
	 * </p>
	 * <p>
	 * Given {@link HttpRequestListener} will be used as a callback before the
	 * request is executed and will be given back the server response once the
	 * execution completes.<br/>
	 * {@code HttpRequestListener.onSetHttpClient} and
	 * {@code HttpRequestListener.onSetHttpParams} will also be called so that
	 * the caller can customize the request before it is executed.
	 * </p>
	 * <p>
	 * In case of a client-side exception during the execution,
	 * {@code returnCode} will have a value of 500 and {@code responseBody} will
	 * contain the exception message.
	 * </p>
	 * <p>
	 * The third parameter is an Activity that the caller wish to notify as
	 * being busy during the request execution.<br/>
	 * {@code Activity.setProgressBarIndeterminateVisibility} will be called
	 * before and after the request.
	 * </p>
	 *
	 * @param req
	 *            the HttpGet object to request
	 * @param whenLoaded
	 *            the callback to use before/after the request
	 * @param activity
	 *            the activity to notify when request is busy executing
	 */
	public static void requestPUTAsync(final HttpParamPut req,
			final HttpRequestListener whenLoaded, Activity activity) {
		requestAsync(req, whenLoaded, activity);
	}


	/**
	 * Executes a new asynchronous request to specified URI
	 *
	 * <p>
	 * This method spawns a new thread to perform the request.
	 * </p>
	 * <p>
	 * Given {@link HttpRequestListener} will be used as a callback before the
	 * request is executed and will be given back the server response once the
	 * execution completes.<br/>
	 * {@code HttpRequestListener.onSetHttpClient} and
	 * {@code HttpRequestListener.onSetHttpParams} will also be called so that
	 * the caller can customize the request before it is executed.
	 * </p>
	 * <p>
	 * In case of a client-side exception during the execution,
	 * {@code returnCode} will have a value of 500 and {@code responseBody} will
	 * contain the exception message.
	 * </p>
	 * <p>
	 * The third parameter is an Activity that the caller wish to notify as
	 * being busy during the request execution.<br/>
	 * {@code Activity.setProgressBarIndeterminateVisibility} will be called
	 * before and after the request.
	 * </p>
	 *
	 * @param uri
	 *            the uri to request
	 * @param whenLoaded
	 *            the callback to use before/after the request
	 * @param activity
	 *            the activity to notify when request is busy executing
	 */
	public static void requestAsync(final ExecutableRequest req,
			final HttpRequestListener whenLoaded, Activity activity) {
		HttpParams params = new BasicHttpParams();
		if (null != whenLoaded) {
			whenLoaded.setRequestParams(params);
		}
		req.setParams(params);
		HttpRequestHolder loadReq = HttpRequestHolder.newInstance(req,
				whenLoaded);
		(new HttpRequesterTask(whenLoaded, activity)).execute(loadReq);
	}

	@TargetApi(8)
	private static HttpResponseHolder request(HttpRequestHolder loadReq) {
		HttpClient client = null;

		ExecutableRequest req = loadReq.http;
		HttpRequestListener listener = loadReq.listener;
		HttpParams params = req.getParams();

		if (null == params) {
			params = new BasicHttpParams();
		}
		if (null != listener) {
			listener.setRequestParams(params);
		}

		// Use Android specific client if available
		if (android.os.Build.VERSION.SDK_INT >= 8) {
			client = android.net.http.AndroidHttpClient
					.newInstance(android_user_agent);
		} else {
			// Sets up the http part of the service.
			final SchemeRegistry supportedSchemes = new SchemeRegistry();
			// Register the "http" protocol scheme, it is required
			// by the default operator to look up socket factories.
			final SocketFactory sf = PlainSocketFactory.getSocketFactory();
			supportedSchemes.register(new Scheme("http", sf, 80));
			supportedSchemes.register(new Scheme("https", SSLSocketFactory
					.getSocketFactory(), 443));
			final ThreadSafeClientConnManager ccm = new ThreadSafeClientConnManager(
					params, supportedSchemes);
			client = new DefaultHttpClient(ccm, params);
		}
		req.setParams(params);

		if (null != listener) {
			listener.setHttpClient(client);
		}

		HttpResponse resp = null;
		HttpResponseHolder holder = new HttpResponseHolder();
		holder.uri = Uri.parse(req.getURI().toString());

		try {
			resp = client.execute(req);
			holder.returnCode = resp.getStatusLine().getStatusCode();
			Header[] hdrs = resp.getAllHeaders();
			if (hdrs.length > 0) {
				for (Header h : hdrs) {
					holder.headers.put(h.getName(), h.getValue());
				}
			}

			if ("application/octet-stream".equals(resp.getFirstHeader("Content-Type").getValue())) {
				int len = 0;
				byte[] buffer = new byte[1024];
				InputStream is = null;
				ByteArrayOutputStream baos = new ByteArrayOutputStream();

				try {
					is = resp.getEntity().getContent();

					while (0 < (len = is.read(buffer))) {
						baos.write(buffer, 0, len);
					}
					holder.responseBytes = baos.toByteArray();
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				} finally {
					if (null != baos) {
						try {
							baos.close();
						} catch (IOException e) {
						}
					}
					if (null != is) {
						try {
							is.close();
						} catch (IOException e) {
						}
					}
				}
			}
			else {
				Reader r = null;
				StringBuilder sb = new StringBuilder();
				try {
					r = new BufferedReader(new InputStreamReader(resp.getEntity()
							.getContent()));
	
					String s = null;
					while ((s = ((BufferedReader) r).readLine()) != null) {
						sb.append(s);
					}
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				} finally {
					if (null != r) {
						try {
							r.close();
						} catch (IOException e) {
						}
					}
				}
				holder.responseBody = sb.toString();
			}
			return holder;
		} catch (Exception e1) {
			e1.printStackTrace();
			holder.responseBody = e1.getMessage();
			//holder.returnCode = 500;
			return holder;
		} finally {
			// Use Android specific client if available
			if (android.os.Build.VERSION.SDK_INT >= 8) {
				((android.net.http.AndroidHttpClient) client).close();
			}
		}
	}

	/**
	 * Holder for HttpGet and Listener objects to pass to AsyncTask
	 *
	 * @author mikami-yoann
	 */
	private final static class HttpRequestHolder {
		public ExecutableRequest http;
		public HttpRequestListener listener;

		public final static HttpRequestHolder newInstance(
				ExecutableRequest http, HttpRequestListener listener) {
			HttpRequestHolder lR = new HttpRequestHolder();
			lR.http = http;
			lR.listener = listener;
			return lR;
		}

		private HttpRequestHolder() {
		};
	}

	private final static class HttpRequesterTask extends
			AsyncTask<HttpRequestHolder, Void, HttpResponseHolder> {

		private HttpRequestListener mRequestListener;
		private WeakReference<Activity> mActivity;

		public HttpRequesterTask(HttpRequestListener handler, Activity activity) {
			mRequestListener = handler;
			if (null != activity) {
				mActivity = new WeakReference<Activity>(activity);
			}
		}

		@Override
		protected void onPreExecute() {
			if (null != mActivity) {
				mActivity.get().setProgressBarIndeterminateVisibility(true);
			}
		}

		@Override
		protected void onPostExecute(HttpResponseHolder reqHolder) {
			if (null != mActivity) {
				mActivity.get().setProgressBarIndeterminateVisibility(false);
			}
			if (null != mRequestListener) {
				mRequestListener.requestComplete(reqHolder);
			}
		}

		@Override
		protected HttpResponseHolder doInBackground(HttpRequestHolder... reqs) {
			final HttpRequestHolder req = reqs[0];

			if (null != mRequestListener) {
				mRequestListener.requestPrepare(req.http,
						Uri.parse(req.http.getURI().toString()));
			}

			HttpResponseHolder holder = request(req);
			return holder;
		}
	};
}
